package mayton.xml;

import mayton.xml.XmlSchemaChecker;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

class XmlSchemaCheckerTest {

    static String xml = "";

    static String schema = "";

    @Test
    void test() throws IOException, SAXException {
        XmlSchemaChecker.isValid(new StringReader(""),new StringReader(""));
    }

}
