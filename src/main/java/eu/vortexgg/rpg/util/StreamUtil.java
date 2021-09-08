package eu.vortexgg.rpg.util;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class StreamUtil {

    public static <T, R> List<R> transform(List<T> elements, Function<T, R> func) {
        ArrayList<R> list = Lists.newArrayList();
        for (T element : elements) {
            list.add(func.apply(element));
        }
        return list;
    }

}
