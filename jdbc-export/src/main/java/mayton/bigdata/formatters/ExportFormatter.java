package mayton.bigdata.formatters;

import java.io.OutputStream;
import java.sql.ResultSet;

public interface ExportFormatter {

    void export(ResultSet rs, String query, int columnCount, String[] columnNames, String[] columnTypes, OutputStream os) throws Exception;

}
