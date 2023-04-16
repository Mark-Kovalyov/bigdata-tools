package mayton.json;

// TODO: Replace with non-invasive json parser (like JSurfer)
/*import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JsonProvider;*/
//import net.minidev.json.JSONArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.input.CountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jsfr.json.compiler.JsonPathCompiler;
import org.jsfr.json.GsonParser;
import org.jsfr.json.JsonSurfer;
import org.jsfr.json.provider.GsonProvider;
        import org.xbib.io.compress.bzip2.Bzip2InputStream;


import java.io.*;
import java.util.Iterator;
        import java.util.zip.GZIPInputStream;

public class JsonLinesSelector {

    static Logger logger = LoggerFactory.getLogger(JsonLinesSelector.class);

    public static int streamWithQuery(InputStream cis, String query, PrintWriter fw) {
        JsonSurfer surfer = new JsonSurfer(GsonParser.INSTANCE, GsonProvider.INSTANCE);
        Iterator<Object> iterator = surfer.iterator(cis, JsonPathCompiler.compile(query));
        int rows = 0;
        while (iterator.hasNext()) {
            JsonObject googleGsonObject = (JsonObject) iterator.next();
            String jsonline = googleGsonObject.toString();
            fw.write(jsonline);
            fw.write("\n");
            rows++;
        }
        return rows;
    }

    public static void start(String inputfile, String query, String outputfile) throws IOException {
        logger.info("inputfile = '{}'", inputfile);
        logger.info("query = '{}'", query);
        logger.info("outputfile = '{}'", outputfile);
        long begin = System.currentTimeMillis();
        final int BUF_SIZE = 128 * 1024 * 128;

        InputStream is;
        if (inputfile.equals("-")) {
            is = System.in;
        } else if (inputfile.endsWith(".gz")) {
            is = new GZIPInputStream(new FileInputStream(inputfile));
        } else if (inputfile.endsWith(".bz2") || inputfile.endsWith(".bzip2")) {
            is = new Bzip2InputStream(new FileInputStream(inputfile));
        } else {
            is = new FileInputStream(inputfile);
        }

        CountingInputStream cis = new CountingInputStream(is);

        PrintWriter fw;
        if (outputfile.equals("-")) {
            fw = new PrintWriter(System.out);
        } else {
            fw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(outputfile), BUF_SIZE));
        }

        int jsonObjects = streamWithQuery(cis, query, fw);

        fw.close();
        cis.close();
        long end = System.currentTimeMillis();
        logger.info("JsonObject read        : {}", jsonObjects);
        logger.info("Source file bytes read : {}", cis.getByteCount());
        long elapsed = (end - begin) / 1000;
        logger.info("Elapsed time           : {} s", elapsed);
        if (elapsed != 0) {
            logger.info("AVG speed              : {} records/s", jsonObjects / elapsed);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            logger.error("Usage: java -jar json-lines-selector.jar [inputfile.json] [json-path-query] [outputfile.jsonl]");
            System.exit(1);
        } else {
            start(args[0], args[1], args[2]);
        }
    }
}
