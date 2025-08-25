package mayton.bigdata.formatters;

import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.QuoteStrategies;
import mayton.bigdata.TableMetadata;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.Map;

public class CsvFormatter implements ExportFormatter {
    @Override
    public void export(ResultSet rs, String query, TableMetadata tableMetadata, OutputStream os, Map<String,String> props) throws Exception {
        int columnCount = tableMetadata.columnCount();
        String[] columnNames = tableMetadata.columnNames();
        try (CsvWriter csv = CsvWriter.builder()
                .quoteCharacter('"')
                .fieldSeparator(';')
                .quoteStrategy(QuoteStrategies.ALWAYS)
                .build(os)) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = columnNames[i];
            }
            csv.writeRecord(row);
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                csv.writeRecord(row);
            }
        }
    }
}
