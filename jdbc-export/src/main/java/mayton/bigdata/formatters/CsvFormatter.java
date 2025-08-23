package mayton.bigdata.formatters;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class CsvFormatter implements ExportFormatter {
    @Override
    public void export(ResultSet rs, String query, int columnCount, String[] columnNames, String[] columnTypes, OutputStream os, Map<String,String> props) throws SQLException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                switch (columnNames[i]) {
                    default : pw.print(rs.getString(i));
                }
                if (i != columnCount) pw.print(';');
            }
            pw.println();
        }
        pw.flush();
    }
}
