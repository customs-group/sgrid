package util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 *
 * Created by edwardlol on 16/6/20.
 */
public class Util {

    //~ Constructors -----------------------------------------------------------

    // Suppress default constructor for noninstantiability
    private Util() {
        throw new AssertionError();
    }

    //~ Collection methods -----------------------------------------------------

    /**
     * count the object numbers of a list
     * @param objectList the input list
     * @param <T> the type of objects in the list
     * @return a map contains all object and their counts
     */
    public static <T> Map<T, Integer> countList(List<T> objectList) {
        Map<T, Integer> resultMap = new HashMap<>();
        objectList.forEach(object -> {
            int count = resultMap.containsKey(object) ? resultMap.get(object) + 1 : 1;
            resultMap.put(object, count);
        });
        return resultMap;
    }

    /**
     *
     * @param originalMap
     * @param object
     * @param <T>
     * @return
     */
    public static <T> Map<T, Integer> updateCountMap(Map<T, Integer> originalMap, T object) {
        int count = originalMap.containsKey(object) ? originalMap.get(object) + 1 : 1;
        originalMap.put(object, count);
        return originalMap;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(Map<K, V> map ) {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();

        st.sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEachOrdered( e -> result.put(e.getKey(), e.getValue()) );

        return result;
    }

    //~ NLP methods ------------------------------------------------------------

    /**
     * seperate words in sentence
     * @param sentence the sentence you want to seperate
     * @return the list of terms in the sentence
     */
    public static List<String> seperate(String sentence) {
        List<String> termList = new ArrayList<>();
        JiebaSegmenter segmenter = new JiebaSegmenter();

        // seperate tokens
        List<SegToken> tokens = segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH);
        // delete punctuation
        String regex = "[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×\\s]";
        Pattern pattern = Pattern.compile(regex);
        tokens.forEach(token -> {
            Matcher matcher = pattern.matcher(token.word);
            if (!matcher.find()) {
                termList.add(token.word);
            }
        });
        return termList;
    }


    //~ IO methods -------------------------------------------------------------

    /**
     * check the format of the input file
     * @param file file name
     * @return column number of rows with count
     */
    public static Map<Integer, Integer> checkFormat(String file) {
        Map<Integer, Integer> lengthMap = new HashMap<>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            String[] contents1 = line.split(",");
            for (int i = 0; i < contents1.length; i++) {
                System.out.println(i + ": " + contents1[i]);
            }
            while (line != null) {
                String[] contents = line.split(",");
                if (lengthMap.containsKey(contents.length)) {
                    int times = lengthMap.get(contents.length);
                    lengthMap.put(contents.length, times + 1);
                } else {
                    lengthMap.put(contents.length, 1);
                }
                line = bufferedReader.readLine();
            }
            lengthMap.forEach((length, times) -> System.out.println("length: " + length + "; times: " + times));
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lengthMap;
    }

    /**
     * convert NULL to "无"
     * @param content the original string
     * @return converted string
     */
    public static String readStringWithNull(String content) {
        return content.equals("") ? "无" : content;
    }

    //~ Date methods -----------------------------------------------------------

    /**
     * get the interval days of two Date instances
     * @param date1 the later Date
     * @param date2 the earlier Date
     * @return the days between date1 and date2
     */
    public static int getIntervalDays(Date date1, Date date2) {
        return new Long((date1.getTime() - date2.getTime()) / 1000 / 60 / 60 / 24).intValue();
    }

    /**
     * get the interval days of two Calendar instances
     * @param date1 the later Date
     * @param date2 the earlier Date
     * @return the days between date1 and date2
     */
    public static int getIntervalDays(Calendar date1, Calendar date2) {
        return new Long((date1.getTimeInMillis() - date2.getTimeInMillis()) / 1000 / 60 / 60 / 24).intValue();
    }

    public static int getIntervalHours(Date date1, Date date2) {
        return new Long((date1.getTime() - date2.getTime()) / 1000 / 60 / 60).intValue();
    }

    public static int getIntervalHours(Calendar date1, Calendar date2) {
        return new Long((date1.getTimeInMillis() - date2.getTimeInMillis()) / 1000 / 60 / 60).intValue();
    }

    /**
     * convert String to Date
     * @param string the input String
     * @return a Date instance
     */
    private static Date stringToDate(String string) {
        Date result;
        if (string == null || string.equals("")) {
            return null;
        }
        try { // Date format in yyyy/MM/dd HH:mm:ss
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            result = dateFormat.parse(string);
        } catch (ParseException e1) {
            try { // Date format in yyyy/MM/dd
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                result = dateFormat.parse(string);
            } catch (ParseException e2) {
                try { // Date format in yyyy-MM-dd HH:mm:ss
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    result = dateFormat.parse(string);
                } catch (ParseException e3) {
                    try { // Date format in yyyy-MM-dd
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        result = dateFormat.parse(string);
                    } catch (ParseException e4) { // other strange Date format or ""
                        result = new Date();
                    }
                }
            }
        }
        return result;
    }

    /**
     * convert String to Calendar
     * @param string the input String
     * @return a Calendar instance
     */
    public static Calendar stringToCalendar(String string) {
        Calendar calendar = Calendar.getInstance();
        Date date = stringToDate(string);
        if (date != null) {
            calendar.setTime(date);
            return calendar;
        } else {
            return null;
        }
    }
}

// End Util.java
