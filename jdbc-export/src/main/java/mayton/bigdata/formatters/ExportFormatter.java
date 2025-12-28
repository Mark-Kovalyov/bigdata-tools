package mayton.bigdata.formatters;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.Map;

public interface ExportFormatter {

    void export(ResultSet rs, String query, int columnCount, String[] columnNames, String[] columnTypes, String path, Map<String,String> props) throws Exception;

}
