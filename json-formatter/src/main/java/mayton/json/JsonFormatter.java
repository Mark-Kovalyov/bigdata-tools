package mayton.json;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.xbib.io.compress.bzip2.Bzip2InputStream;

import java.io.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

public class JsonFormatter {

	static Logger logger = LoggerFactory.getLogger("json-formatter");

	public static void main(String[] args) throws IOException {
		logger.info("user.dir = {}", System.getProperty("user.dir"));
		Profiler profiler = new Profiler("json-formatter");
		profiler.start("init");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		final int BUF_SIZE = 32 * 1024 * 1024;
		String inputfile = args[0];
		String outputfile = args[1];
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
		Reader r = new InputStreamReader(is);
		profiler.start("parse reader");
		// TODO: Check for memory consuming. Get rid of sub-optimal parser-serializer.
		// 2023-05-25 21:32:31,905 [INFO ] json-formatter - + Profiler [json-formatter]
		//|-- elapsed time                   [init]     0.090  seconds.
		//|-- elapsed time           [parse reader]   113.838  seconds.
		//|-- elapsed time                 [toJson]     7.628  seconds.
		//|-- elapsed time                  [close]     0.039  seconds.
		//|-- Total                [json-formatter]   121.595  seconds.
		JsonElement el = JsonParser.parseReader(r);
		PrintWriter fw;
		if (outputfile.equals("-")) {
			fw = new PrintWriter(System.out);
		} else {
			fw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(outputfile), BUF_SIZE));
		}
		profiler.start("toJson");
		gson.toJson(el, fw);
		profiler.start("close");
		fw.close();
		TimeInstrument ti = profiler.stop();
		logger.info(ti.toString());
	}

}


