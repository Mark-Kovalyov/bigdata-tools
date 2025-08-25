package mayton.bigdata.formatters;

import mayton.bigdata.TableMetadata;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.Map;

public interface ExportFormatter {

    void export(ResultSet rs, String query, TableMetadata tableMetadata, OutputStream os, Map<String,String> props) throws Exception;

}
