package mayton.bigdata;

import mayton.bigdata.formatters.CsvFormatter;
import mayton.bigdata.formatters.ExportFormatter;
import mayton.bigdata.formatters.JsonFormatter;
import mayton.bigdata.formatters.XmlFormatter;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class JdbcExport {

    static Logger logger = LoggerFactory.getLogger("jdbc-export");

    static String LOGO =  "\n" +
                    "   ____                  __                                      __ \n" +
                    "  / __ \\_________ ______/ /__        ___  _  ______  ____  _____/ /_\n" +
                    " / / / / ___/ __ `/ ___/ / _ \\______/ _ \\| |/_/ __ \\/ __ \\/ ___/ __/\n" +
                    "/ /_/ / /  / /_/ / /__/ /  __/_____/  __/>  </ /_/ / /_/ / /  / /_  \n" +
                    "\\____/_/   \\__,_/\\___/_/\\___/      \\___/_/|_/ .___/\\____/_/   \\__/  \n" +
                    "                                           /_/                      ";

    static Options createOptions() {
        return new Options()
                .addRequiredOption("u", "url", true, "JDBC url. (ex:jdbc:oracle:thin@localhost:1521/XE")
                .addOption("h", "help", false, "Print this help")
                .addOption("s", "schema", true, "Schema name")
                .addOption("t", "table", true, "Table or View name")
                .addOption("q", "query", true, "SELECT-expression (ex: SELECT * FROM EMP)")
                .addRequiredOption("f", "format", true, "Export format: csv|jsonl|xml")
                .addRequiredOption("o", "outputfile", true, "Output file name (ex: emp.csv)");
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(LOGO, createOptions());
        } else {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(LOGO, createOptions());
                return;
            }

            String url    = line.getOptionValue("url");
            String schema = line.getOptionValue("schema");
            String table  = line.getOptionValue("table");
            String query  = line.getOptionValue("query");
            String outputFile = line.getOptionValue("outputfile");
            String format = line.getOptionValue("format");
            String queryStr = line.hasOption("query") ?
                    query : String.format("SELECT * FROM %s.%s", schema, table);

            try (Connection conn = DriverManager.getConnection(url);
                 OutputStream os = new FileOutputStream(outputFile)
            ) {
                logger.info("Start analyze schema");
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(String.format("%s LIMIT 1", queryStr));
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
                    case "csv" : formatter = new CsvFormatter(); break;
                    case "jsonl" : formatter = new JsonFormatter(); break;
                    case "xml" : formatter = new XmlFormatter(); break;
                    default:
                        throw new JdbcExportException("Unknown format : " + format);
                }
                ResultSet rs2 = st.executeQuery(queryStr);
                formatter.export(rs2, query, columnCount, columnNames, columnTypeNames, os);
                logger.info("Finish export");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



}
