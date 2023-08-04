package mayton.xml;

import org.xml.sax.SAXException;

import javax.sql.rowset.spi.XmlReader;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class XmlSchemaChecker {

    public static Validator initValidator(Reader xsdReader) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(xsdReader);
        Schema schema = factory.newSchema(schemaFile);
        return schema.newValidator();
    }

    public static boolean isValid(Reader xsdReader, Reader xmlPath) throws IOException, SAXException {
        Validator validator = initValidator(xsdReader);
        try {
            validator.validate(new StreamSource(xmlPath));
            return true;
        } catch (SAXException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean isValid(String xsd, String xml) throws IOException, SAXException {
        return isValid(new FileReader(xsd), new FileReader(xml));
    }

    public static void main(String[] args) throws IOException, SAXException {
        String xsdSchema = args[0];
        String xml = args[1];
        if (isValid(xsdSchema, xml)) {
            System.out.println("OK");
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}