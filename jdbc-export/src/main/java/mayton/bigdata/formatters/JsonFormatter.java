package mayton.bigdata.formatters;

import com.jsoniter.output.JsonStream;
import mayton.bigdata.JdbcExportException;

import java.io.*;
import java.sql.Blob;
import java.sql.ResultSet;

public class JsonFormatter implements ExportFormatter {
    @Override
    public void export(ResultSet rs, String query, int columnCount, String[] columnNames,String[] columnTypes, OutputStream os) throws Exception {
        JsonStream stream = new JsonStream(os, 4096);
        while (rs.next()) {
            stream.writeObjectStart();
            for (int i = 1; i <= columnCount; i++) {
                if (rs.getObject(i) != null) {
                    stream.writeObjectField(columnNames[i]);
                    //INTEGER. The value is a signed integer, stored in 0, 1, 2, 3, 4, 6, or 8 bytes depending on the magnitude of the value.
                    //REAL. The value is a floating point value, stored as an 8-byte IEEE floating point number.
                    //TEXT. The value is a text string, stored using the database encoding (UTF-8, UTF-16BE or UTF-16LE).
                    //BLOB. The value is a blob of data, stored exactly as it was input.
                    switch (columnTypes[i]) {
                        case "TEXT" -> {
                            String v = rs.getString(i);
                            stream.writeVal(v);
                        }
                        case "INT", "INTEGER" -> stream.writeVal(rs.getInt(i));
                        case "REAL" -> stream.writeVal(rs.getDouble(i));
                        case "BLOB" -> {
                            Blob blob = rs.getBlob(i);
                            // TODO: encode bytes as BinHex
                            stream.writeVal("<blob is here>");
                        }
                        default -> throw new JdbcExportException("Unable to handle type " + columnTypes[i]);
                    }
                    if (i != columnCount) stream.writeMore();
                }
            }
            stream.writeObjectEnd();
            stream.writeRaw("\n");
        }
    }
}
