package mayton.bigdata.formatters;

import mayton.bigdata.JdbcExportException;
import mayton.bigdata.TableMetadata;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.Map;

public class AvroFormatter implements ExportFormatter{

    static Logger logger = LoggerFactory.getLogger("avro-formatter");

    @SuppressWarnings("java:S2629")
    @Override
    public void export(ResultSet rs, String query, TableMetadata tableMetadata, OutputStream os, Map<String,String> props) throws Exception {
        int columnCount = tableMetadata.columnCount();
        String[] columnNames = tableMetadata.columnNames();
        String[] columnTypes = tableMetadata.columnTypes();
        SchemaBuilder.FieldAssembler<Schema> fieldAssembler = SchemaBuilder
                .record("AvroFormatter") // TODO: Is it neccasary to add namespace?
                .fields();

        for(int i = 1 ; i <= columnCount ; i++) {
            switch (columnTypes[i]) {
                case "TEXT", "CHARACTER VARYING" -> fieldAssembler.optionalString(columnNames[i]);
                case "INT", "INTEGER", "NUMERIC" -> fieldAssembler.optionalInt(columnNames[i]); // TODO: int or long?
                case "BIGINT" -> fieldAssembler.optionalLong(columnNames[i]);
                case "REAL", "DOUBLE PRECISION" -> fieldAssembler.optionalDouble(columnNames[i]);
                case "BLOB" -> fieldAssembler.optionalString(columnNames[i]); // TODO: Hex encoded?
                default -> throw new JdbcExportException("Unable to handle type " + columnTypes[i]);
            }
        }

        Schema schema = fieldAssembler.endRecord();

        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        try(DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            if (props.containsKey("compression")) {
                String comp = props.get("compression");
                logger.info("compression = {}", comp);
                CodecFactory codec = CodecFactory.fromString(comp);
                logger.info("codec factory = {}", codec);
                dataFileWriter.setCodec(codec);
            }

            dataFileWriter.create(schema, os);
            while (rs.next()) {
                GenericRecord tableRecord = new GenericData.Record(schema);
                for (int i = 1; i <= columnCount; i++) {
                    if (rs.getObject(i) != null) {
                        tableRecord.put(columnNames[i], rs.getObject(i));
                    }
                }
                dataFileWriter.append(tableRecord);
            }
        }



    }
}
