package mayton.bigdata;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class ProtoTest {

    @Test
    void schemaTest() throws Exception {

        // --- Create Message Descriptor ---
        DescriptorProtos.DescriptorProto personMessage =
                DescriptorProtos.DescriptorProto.newBuilder()
                        .setName("Person")
                        .addField(DescriptorProtos.FieldDescriptorProto.newBuilder()
                                .setName("name")
                                .setNumber(1)
                                .setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL)
                                .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING))
                        .addField(DescriptorProtos.FieldDescriptorProto.newBuilder()
                                .setName("age")
                                .setNumber(2)
                                .setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL)
                                .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32))
                        .build();


        DescriptorProtos.FileDescriptorProto fileProto =
                DescriptorProtos.FileDescriptorProto.newBuilder()
                        .setName("person.proto")
                        .setSyntax("proto3")
                        .addMessageType(personMessage)
                        .build();

        // --- Wrap schema into FileDescriptorSet ---
        DescriptorProtos.FileDescriptorSet descriptorSet =
                DescriptorProtos.FileDescriptorSet.newBuilder()
                        .addFile(fileProto)
                        .build();

        // --- Save schema to file ---

        try (FileOutputStream schemaOut = new FileOutputStream("tmp/person_schema.pb")) {
        //ByteArrayOutputStream schemaOut = new ByteArrayOutputStream();
            descriptorSet.writeTo(schemaOut);
        //schemaOut.flush();
        }



    }

}
