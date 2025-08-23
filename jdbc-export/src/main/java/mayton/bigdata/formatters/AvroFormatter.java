package mayton.bigdata.formatters;

import java.io.OutputStream;
import java.sql.ResultSet;

public class AvroFormatter implements ExportFormatter{

    @Override
    public void export(ResultSet rs, String query, int columnCount, String[] columnNames, String[] columnTypes, OutputStream os) throws Exception {
        new RuntimeException("Not implemented yet!");
    }
}
