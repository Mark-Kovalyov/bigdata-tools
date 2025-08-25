package mayton.bigdata.formatters;

import mayton.bigdata.TableMetadata;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.function.Consumer;

public class SeqFormatter implements ExportFormatter {

    static Logger logger = LoggerFactory.getLogger("seq-formatter");

    private static boolean isPrime(int i) {
        if (i % 2 == 0 || i % 3 == 0) return false;
        boolean res = false;
        for(int k = 3; k < 1 + (int) Math.sqrt(i); k += 2) {
            if (i % k == 0) {
                return false;
            }
        }
        return true;
    }

    public static void goWrite(SequenceFile.Writer writer) throws IOException {

    }
    /*
    public static void goRead(SequenceFile.Reader reader) throws IOException, InstantiationException, IllegalAccessException {
        WritableComparable readKey = (WritableComparable) reader.getKeyClass().newInstance();
        Writable readValue = (Writable) reader.getValueClass().newInstance();
        while (reader.next(readKey, readValue)) {
            logger.info("Read item");
        }
        reader.close();
    }*/

    private static void process(String outputFile) throws IOException, InstantiationException, IllegalAccessException {


        //SequenceFile.Reader reader = new SequenceFile.Reader(FileSystem.get(config), new Path(pathString), config);

        ///goRead(reader, profiler);

        //MapFile.Writer = MapFile.Writer()

    }

    Writable decodeWritableWrapper(String type, Object cellValue) throws Exception {
        switch (type) {
            case "CHARACTER VARYING" -> {
                return new BytesWritable(((String)cellValue).getBytes());
            }
            case "INT", "INTEGER", "NUMERIC" -> {
                return new VIntWritable((Integer)cellValue);
            }
            case "BIGINT" -> {
                return new VLongWritable((Long)cellValue);
            }
            default -> {
                throw new Exception("Error! Unable to decode type " + type);
            }
        }
    }

    Class<?> decode(String type) throws Exception {
        switch (type) {
            case "CHARACTER VARYING" -> {
                return BytesWritable.class;
            }
            case "INT", "INTEGER", "NUMERIC" -> {
                return VIntWritable.class;
            }
            case "BIGINT" -> {
                return VLongWritable.class;
            }
            default -> {
                throw new Exception("Error! Unable to decode type " + type);
            }
        }
    }

    @Override
    public void export(ResultSet rs, String query, TableMetadata tableMetadata, OutputStream os, Map<String, String> props) throws Exception {
        if (tableMetadata.columnCount() != 2) {
            throw new Exception("Error! Seq format support only key-value table schema. So you must select an only 2 columns!");
        }
        int columnCount = tableMetadata.columnCount();
        String[] columnNames = tableMetadata.columnNames();
        String[] columnTypes = tableMetadata.columnTypes();
        Configuration config = new Configuration();
        SequenceFile.Writer.Option file  = SequenceFile.Writer.file(new Path(props.get("outputfile")));
        SequenceFile.Writer.Option compr = SequenceFile.Writer.compression(SequenceFile.CompressionType.NONE);

        SequenceFile.Writer.Option keyClass = SequenceFile.Writer.keyClass(decode(columnTypes[1]));
        SequenceFile.Writer.Option valueClass = SequenceFile.Writer.valueClass(decode(columnTypes[2]));

        SequenceFile.Writer.Option bs = SequenceFile.Writer.blockSize(4096);
        SequenceFile.Writer.Option append = SequenceFile.Writer.appendIfExists(false);
        SequenceFile.Writer.Option interv = SequenceFile.Writer.syncInterval(4096);
        SequenceFile.Writer writer = SequenceFile.createWriter(config, file, compr, keyClass, valueClass, bs, append, interv);
        //VIntWritable key = new VIntWritable();
        //VLongWritable value = new VLongWritable();
        int cnt = 1;
        while(rs.next()) {
            writer.append(
                    decodeWritableWrapper(columnTypes[1], rs.getObject(1)),
                    decodeWritableWrapper(columnTypes[2], rs.getObject(2))
            );
            cnt++;
        }
        writer.hflush();
        writer.close();
    }
}
