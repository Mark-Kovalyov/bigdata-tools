package mayton.bigdata;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
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

    public static <T> boolean isVectorDifference(String year, String month, String day, String hour, String min,
                                                 String year2, String month2, String day2, String hour2, String min2, int prefix) {
        return isVectorDifference(
                Arrays.asList(year,  month,  day,  hour,  min),
                Arrays.asList(year2, month2, day2, hour2, min2),
                prefix);
    }

    public static <T> boolean isVectorDifference(List<T> l1, List<T> l2, int prefix) {
        for(int i=0;i<prefix;i++) {
            if (!l1.get(i).equals(l2.get(i))) return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        String dest = args[0];
        int prefix = Integer.parseInt("4");
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
                    writer = new FileWriter(dest + "/" + formatLongFiles(year, month, day, hour, min, extension));
                } else {
                    if (isVectorDifference(year,month,day,hour,min, year2,month2,day2,hour2,min2, prefix) {
                        // if (!year.equals(year2) || !month.equals(month2) || !day.equals(day2) || !hour.equals(hour2)) {
                        writer.close();
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