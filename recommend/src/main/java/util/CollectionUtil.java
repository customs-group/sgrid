package util;

import com.google.common.collect.Maps;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by edwardlol on 16/9/19.
 */
public class CollectionUtil {

    //~ Constructors -----------------------------------------------------------

    // Suppress default constructor for noninstantiability
    private CollectionUtil() {
        throw new AssertionError();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * count the object numbers of a list
     * @param objectList the input list
     * @param <T> the type of objects in the list
     * @return a map contains all object and their counts
     */
    public static <T> Map<T, Integer> countList(List<T> objectList) {
        Map<T, Integer> resultMap = Maps.newHashMap();
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
     * @param <T> the type of the key of the map
     * @return
     */
    public static <T> Map<T, Integer> updateCountMap(Map<T, Integer> originalMap, T object) {
        int count = originalMap.containsKey(object) ? originalMap.get(object) + 1 : 1;
        originalMap.put(object, count);
        return originalMap;
    }

    /**
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map ) {
//        Map<K, V> result = new LinkedHashMap<>();
//        Stream<Map.Entry<K, V>> st = map.entrySet().stream();
//
//        st.sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()) );
//
//        return result;

        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(Map<K, V> map ) {
//        Map<K, V> result = new LinkedHashMap<>();
//        Stream<Map.Entry<K, V>> st = map.entrySet().stream();
//
//        st.sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEachOrdered(e -> result.put(e.getKey(), e.getValue()) );
//
//        return result;

        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}

// End CollectionUtil.java
