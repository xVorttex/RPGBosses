package eu.vortexgg.rpg.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;

public class JavaUtil {
    public static final CharMatcher CHAR_MATCHER_ASCII = CharMatcher.inRange('0', '9').or(CharMatcher.inRange('a', 'z'))
            .or(CharMatcher.inRange('A', 'Z')).or(CharMatcher.WHITESPACE).precomputed();
    public static final Pattern UUID_PATTERN = Pattern
            .compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");

    public static boolean isUUID(String string) {
        return UUID_PATTERN.matcher(string).find();
    }

    public static boolean isAlphanumeric(String string) {
        return CHAR_MATCHER_ASCII.matchesAllOf((CharSequence) string);
    }

    public static boolean containsIgnoreCase(Iterable<? extends String> elements, String string) {
        for (String element : elements) {
            if (!StringUtils.containsIgnoreCase(element, string)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static String plurals(int n, String form1, String form2, String form3) {
        int orig = n;
        if (n == 0)
            return orig + " " + form3;
        n = Math.abs(n) % 100;
        if (n > 10 && n < 20)
            return orig + " " + form3;
        n %= 10;
        if (n > 1 && n < 5)
            return orig + " " + form2;
        else if (n == 1)
            return orig + " " + form1;
        return orig + " " + form3;
    }

    public static String plurals(long n, String form1, String form2, String form3) {
        long orig = n;
        if (n == 0)
            return orig + " " + form3;
        n = Math.abs(n) % 100;
        if (n > 10 && n < 20)
            return orig + " " + form3;
        n %= 10;
        if (n > 1 && n < 5)
            return orig + " " + form2;
        else if (n == 1)
            return orig + " " + form1;
        return orig + " " + form3;
    }

    public static <E> List<E> createList(Object object, Class<E> type) {
        List<E> output = new ArrayList<>();

        if (object != null && object instanceof List<?>) {
            List<?> input = (List<?>) object;

            for (Object value : input) {
                if (value == null)
                    continue;
                assert type.isAssignableFrom(value.getClass());
                output.add(type.cast(value));
            }
        }

        return output;
    }

    public static <K, V> String stringify(List<Pair<K, V>> list, int iterations, String key, String value) {
        StringBuilder builder = new StringBuilder("[");
        Iterator<Pair<K, V>> iterator = list.iterator();
        for (int i = 0; i < iterations && i < list.size(); i++) {
            Pair<K, V> next = iterator.next();
            builder.append("{")
                   .append(key).append(":  ").append(next.getKey())
                   .append(", ")
                   .append(value).append(": ").append(next.getValue())
                   .append("}");
            if(iterator.hasNext())
                builder.append(',').append(' ');
        }
        builder.append(']');
        return builder.toString();
    }

    public static String format(Number number) {
        return format(number, 5);
    }

    public static String toDecimal(double number) {
        return TimeUtil.REMAINING_SECONDS_TRAILING.get().format(number).replace(',', '.');
    }

    public static Map<String, Double> calculatePercents(Map<String, Integer> attackers, int totalDamage) {
        Map<String, Double> damagePercents = new HashMap<>(attackers.size());
        final double totalDamagePercents = totalDamage / 100.0D;
        attackers.forEach((key, value) -> damagePercents.put(key, Math.round((value / totalDamagePercents) * 10) / 10D));
        return damagePercents;
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String format(Number number, int decimalPlaces) {
        return format(number, decimalPlaces, RoundingMode.HALF_DOWN);
    }

    public static String format(Number number, int decimalPlaces, RoundingMode roundingMode) {
        Preconditions.checkNotNull(number, "The number cannot be null");
        return new BigDecimal(number.toString()).setScale(decimalPlaces, roundingMode).stripTrailingZeros()
                .toPlainString();
    }

    public static String andJoin(Collection<String> collection, boolean delimiterBeforeAnd) {
        return andJoin(collection, delimiterBeforeAnd, ", ");
    }

    public static String andJoin(Collection<String> collection, boolean delimiterBeforeAnd, String delimiter) {
        if (collection == null || collection.isEmpty())
            return "";
        ArrayList<String> contents = new ArrayList<String>(collection);
        String last = contents.remove(contents.size() - 1);
        StringBuilder builder = new StringBuilder(Joiner.on(delimiter).join(contents));
        if (delimiterBeforeAnd) {
            builder.append(delimiter);
        }
        return builder.append(" and ").append(last).toString();
    }

    public static Integer tryParseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static Double tryParseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

}
