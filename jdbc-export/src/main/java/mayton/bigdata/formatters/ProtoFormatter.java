package mayton.bigdata.formatters;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import mayton.bigdata.JdbcExportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ProtoFormatter implements ExportFormatter{

    static Logger logger = LoggerFactory.getLogger("proto-formatter");

    private void exportSchema(int columnCount, String[] columnNames, String[] columnTypes, Map<String, String> props) {

    }

    private void exportData(ResultSet rs, String query, int columnCount, String[] columnNames, String[] columnTypes,
                            OutputStream os, Map<String, String> props) throws SQLException, JdbcExportException, Descriptors.DescriptorValidationException {

        String table = props.get("table");
        DescriptorProtos.DescriptorProto.Builder protoBuilder = DescriptorProtos.DescriptorProto.newBuilder();
        protoBuilder.setName(table);

        for(int i = 1 ; i <= columnCount ; i++) {
            DescriptorProtos.FieldDescriptorProto.Type fieldType = null;
            switch (columnTypes[i]) {
                case "TEXT", "CHARACTER VARYING" -> fieldType = DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING;
                case "INT", "INTEGER", "NUMERIC" -> fieldType = DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32;
                case "BIGINT" -> fieldType = DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64;
                case "REAL" -> fieldType = DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT;
                case "DOUBLE PRECISION" -> fieldType = DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE;
                case "BLOB" -> fieldType = DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES;
                default -> throw new JdbcExportException("Unable to handle type " + columnTypes[i]);

            }
            protoBuilder.addField(DescriptorProtos.FieldDescriptorProto.newBuilder()
                    .setName(columnNames[i])
                    .setNumber(i)
                    .setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL)
                    .setType(fieldType));
        }

        DescriptorProtos.FileDescriptorProto fileDescriptorProto =
                DescriptorProtos.FileDescriptorProto.newBuilder()
                        .setName("person.proto")
                        .setSyntax("proto3")
                        .addMessageType(protoBuilder.build())
                        .build();

        Descriptors.FileDescriptor descFile = Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, new Descriptors.FileDescriptor[]{});

        Descriptors.Descriptor descProto = descFile.findMessageTypeByName(table);

        while(rs.next()) {
            for(int i = 1 ; i <= columnCount ; i++) {

            }
            //DynamicMessage message = messageBuilder.build();
            //message.writeTo(os);
            //messageBuilder.clear();
        }




    }

    @Override
    public void export(ResultSet rs, String query, int columnCount, String[] columnNames, String[] columnTypes,
                       OutputStream os, Map<String, String> props) throws Exception {

        exportSchema(columnCount, columnNames, columnTypes, props);
        exportData(rs, query, columnCount, columnNames, columnTypes, os, props);

    }


}
