package mayton.bigdata.formatters;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.Map;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

public class ProtoFormatter implements ExportFormatter{
    @Override
    public void export(ResultSet rs, String query, int columnCount, String[] columnNames, String[] columnTypes, OutputStream os, Map<String, String> props) throws Exception {
        //CodedOutputStream cos = CodedOutputStream.newInstance(os);
        Descriptors.Descriptor descriptor = null;

        DescriptorProtos.FieldDescriptorProto idField = DescriptorProtos.FieldDescriptorProto.newBuilder()
                .setName("id")
                .setNumber(1)
                .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32) // int32
                .build();

        for (int i = 1; i <= columnCount; i++) {
            /*DynamicMessage msg = DynamicMessage.newBuilder(descriptor)
                    .setField(idField.getOneofFieldDescriptor(), 0)
                    .build();*/
            //msg.writeDelimitedTo(os);
        }
        //cos.flush();
        os.flush();

    }
}
