package mayton.bigdata;

import mayton.bigdata.formatters.*;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class JdbcExport {

    static Logger logger = LoggerFactory.getLogger("jdbc-export");

    static String LOGO =
            """
                _  ____  ____  ____        ________  _ ____  ____  ____ _____\s
               / |/  _ \\/  __\\/   _\\      /  __/\\  \\///  __\\/  _ \\/  __Y__ __\\
               | || | \\|| | //|  /  _____ |  \\   \\  / |  \\/|| / \\||  \\/| / \\ \s
            /\\_| || |_/|| |_\\\\|  \\__\\____\\|  /_  /  \\ |  __/| \\_/||    / | | \s
            \\____/\\____/\\____/\\____/      \\____\\/__/\\\\\\_/   \\____/\\_/\\_\\ \\_/ \s
            """;

    static Options createOptions() {
        return new Options()
                .addRequiredOption("u", "url", true, "JDBC url. (ex:jdbc:oracle:thin@localhost:1521/XE")
                .addOption("s", "schema", true, "Schema name")
                .addOption("t", "table", true, "Table or View name")
                .addOption("l", "columns", true, "Comma separated list of columns to export (default: all columns)")
                .addOption("w", "where", true, "WHERE expression (ex: id > 1000)")
                .addOption("c", "compression", true, "Optional parameter for Apache AVRO compression ex: snappy|deflate|bzip2")
                .addRequiredOption("f", "format", true, "Export format: csv|jsonl|xml|avro|protobuf")
                .addRequiredOption("o", "outputfile", true, "Output file name (ex: emp.csv)");
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("\n\n" + LOGO + "\n", createOptions());
        } else {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("\n\n" + LOGO + "\n", createOptions());
                return;
            }

            String url    = line.getOptionValue("url");
            String schema = line.getOptionValue("schema");
            String table  = line.getOptionValue("table");
            String where  = line.getOptionValue("where");
            String columns  = line.getOptionValue("columns");
            String outputFile = line.getOptionValue("outputfile");
            String format = line.getOptionValue("format");

            StringBuilder queryStrBuilder = new StringBuilder("SELECT ");
            if (line.hasOption("columns")) {
                queryStrBuilder.append(columns);
            } else {
                queryStrBuilder.append("*");
            }

            queryStrBuilder.append(" FROM ");
            Map<String, String> props = new HashMap<>();

            if (line.hasOption("schema")) {
                props.put("schema", schema);
                queryStrBuilder.append(schema).append(".");
            }

            if (line.hasOption("table")) {
                props.put("table", table);
                queryStrBuilder.append(table);
                queryStrBuilder.append(" ");
            }
            if (line.hasOption("where")) {
                queryStrBuilder.append(" WHERE ");
                queryStrBuilder.append(where);
            }

            logger.info("Generated query: {}", queryStrBuilder.toString());

            String queryStr = queryStrBuilder.toString();

            if (line.hasOption("compression")) {
                props.put("compression", line.getOptionValue("compression"));
            }

            try (Connection conn = DriverManager.getConnection(url);
                 OutputStream os = new FileOutputStream(outputFile)
            ) {
                logger.info("Start analyze schema");
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(format(" %s LIMIT 1", queryStr));
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                int    columnTypeCodes[] = new int[columnCount + 1];
                String columnTypeNames[] = new String[columnCount + 1];
                String columnNames[]     = new String[columnCount + 1];
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnTypeName = metaData.getColumnTypeName(i);
                    int columnTypeCode = metaData.getColumnType(i);
                    logger.info("Column {}: {} ({}, JDBC type {})", i, columnTypeNames, columnTypeName, columnTypeCode);
                    columnTypeCodes[i] = columnTypeCode;
                    columnTypeNames[i] = columnTypeName;
                    columnNames[i] = columnName;
                }

                logger.info("Start export");
                ExportFormatter formatter = null;
                switch (format) {
                    case "csv"   : formatter = new CsvFormatter(); break;
                    case "jsonl" : formatter = new JsonLineFormatter(); break;
                    case "xml"   : formatter = new XmlFormatter(); break;
                    case "avro"  : formatter = new AvroFormatter(); break;
                    case "protobuf" : formatter = new ProtoFormatter(); break;

                    default:
                        throw new ExportException("Unknown format : " + format);
                }
                ResultSet rs2 = st.executeQuery(queryStr);
                formatter.export(rs2, queryStr, columnCount, columnNames, columnTypeNames, os, props);
                logger.info("Finish export");
            } catch (ExportException ex) {
                logger.error("Export exception: {}", ex.getMessage());
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



}
