package mayton.bigdata.formatters;

import com.ctc.wstx.stax.WstxOutputFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.Map;

public class XmlFormatter implements ExportFormatter{

    static Logger logger = LoggerFactory.getLogger("xml-formatter");

    private static String sanitizeForXml(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= 0x20 && c < 128) {
                sb.append(c);
            } else {
                sb.append("&#x").append(Integer.toHexString(c)).append(";");
            }
        }
        return sb.toString();
    }

    @Override
    @SuppressWarnings("java:S2629")
    public void export(ResultSet rs, String query, int columnCount, String[] columnNames, String[] columnTypes, OutputStream os, Map<String,String> props) throws Exception {
        //XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLOutputFactory factory = new WstxOutputFactory(); // TODO: What is the best XmlFactory? Woodstock? Com.sun.Xml?
        factory.setProperty("javax.xml.stream.isRepairingNamespaces", true);
        //factory.setProperty("com.ctc.wstx.outputIndentation", "  "); // 2 spaces
        logger.info("factory class created {}", factory.getClass());
        // com.sun.xml.internal.stream.XMLOutputFactoryImpl@74bf1791
        // com.ctc.wstx.stax.WstxOutputFactory@5c2375a9
        // C l   a u   d  i  o   
        // 43 6c 61 75 64 69 6f  \u1f20
        XMLStreamWriter writer = factory.createXMLStreamWriter(os, "utf-8");
        writer.writeStartDocument();
        writer.writeStartElement("table");
        while (rs.next()) {
            writer.writeStartElement("row");
            for (int i = 1; i <= columnCount; i++) {
                if (rs.getObject(i) != null) {
                    String v = sanitizeForXml(rs.getString(i));
                    writer.writeAttribute(columnNames[i], v);
                }
            }
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
    }
}
