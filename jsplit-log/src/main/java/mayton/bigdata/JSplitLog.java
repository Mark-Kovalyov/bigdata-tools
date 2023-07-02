package mayton.bigdata;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSplitLog {

    public static String replaceDangerous(String s) {
        return s.replaceAll(Pattern.quote(":"), "-");
    }

    public static String formatFolders(String year, String month, String day) {
        return year + "/" + month + "/" + day;
    }

    public static String formatLongFiles(String year, String month, String day, String hour, String min, String extension) {
        return String.format("%s-%s-%s-%s-%s-00%s", year, month, day, hour, min, extension);
    }

    public static String formatFiles(String hour, String min, String extension) {
        return hour + "-" + min + "-00" + extension;
    }

    public static void main(String[] args) throws Exception {
        String dest = args[0];
        new File(dest).mkdirs();
        String extension = ".csv";
        InputStream is = System.in;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String buf = "";
        Writer writer = null;
        Pattern pattern = Pattern.compile(
                "(?<year>\\d{4}).(?<month>\\d{2}).(?<day>\\d{2}).(?<hour>\\d{2}).(?<min>\\d{2}).(?<sec>\\d{2}).*"
        );
        String year2  = null;
        String month2 = null;
        String day2   = null;
        String hour2  = null;
        String min2   = null;
        while((buf = br.readLine()) != null) {
            Matcher matcher = pattern.matcher(buf);
            if (matcher.matches()) {
                String year  = matcher.group("year");
                String month = matcher.group("month");
                String day   = matcher.group("day");
                String hour  = matcher.group("hour");
                String min   = matcher.group("min");
                if (writer == null) {
                    //File dir = new File(dest + "/" + formatFolders(year, month, day));
                    //dir.mkdirs();
                    //writer = new FileWriter(new File(dir, formatFiles(hour, min, extension)));
                    writer = new FileWriter(dest + "/" + formatLongFiles(year, month, day, hour, min, extension));
                } else {
                    // TODO: Generalize
                    if (!year.equals(year2) || !month.equals(month2) || !day.equals(day2) || !hour.equals(hour2) || !min.equals(min2)) {
                        writer.close();
                        //File dir = new File(dest + "/" + formatFolders(year, month, day));
                        //dir.mkdirs();
                        //writer = new FileWriter(new File(dir, formatFiles(hour, min, extension)));
                        writer = new FileWriter(dest + "/" + formatLongFiles(year, month, day, hour, min, extension));
                    }
                }
                writer.write(buf);
                writer.write("\n");
                year2  = year;
                month2 = month;
                day2   = day;
                hour2  = hour;
                min2   = min;
            } else {
                System.err.println("Warning! Unrecognized date from " + buf);
            }
        }
        if (writer != null)
            writer.close();
        br.close();
    }
}