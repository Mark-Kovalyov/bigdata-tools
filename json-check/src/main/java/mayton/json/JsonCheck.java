package mayton.json;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class JsonCheck {

    static int check(InputStream jsonInputStream, InputStream schemaInputStream) throws FileNotFoundException {
        try {
            JSONObject jsonSchema  = new JSONObject(new JSONTokener(schemaInputStream));
            JSONObject jsonSubject = new JSONObject(new JSONTokener(jsonInputStream));

            SchemaLoader loader = SchemaLoader.builder()
                    .schemaJson(jsonSchema)
                    .draftV7Support()
                    .build();

            Schema schema = loader.load().build();


            schema.validate(jsonSubject);
            System.out.println("All is OK!");
            return 0;
        } catch (ValidationException ex) {
            System.err.println("Error during validating!");
            System.err.println(ex.getMessage());
            ex.getCausingExceptions().stream()
                    .map(ValidationException::getMessage)
                    .forEach(System.err::println);
            return 1;
        }
    }

    static int check(String inputJsonArg, String jsonSchemaArg) throws FileNotFoundException {
        return check(new FileInputStream(jsonSchemaArg), new FileInputStream(inputJsonArg));
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            System.err.println("Json-Check utility 1.0 (with http://json-schema.org/draft-07/schema specification)");
            System.err.println(" Usage: java -jar json-check.jar [json-schema-file] [input-json-file]");
            System.exit(2);
        } else {
            String inputJsonArg = args[1];
            String jsonSchemaArg = args[0];
            int code = check(inputJsonArg, jsonSchemaArg);
            System.exit(code);
        }
    }



}
