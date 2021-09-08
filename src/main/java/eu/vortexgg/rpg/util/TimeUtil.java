package eu.vortexgg.rpg.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.FastDateFormat;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static final TimeZone SERVER_TIME_ZONE = TimeZone.getTimeZone("EST");
    public static final ZoneId SERVER_ZONE_ID = SERVER_TIME_ZONE.toZoneId();
    public static final FastDateFormat DAY_MTH_HR_MIN = FastDateFormat.getInstance("dd/MM HH:mm", SERVER_TIME_ZONE,
            Locale.ENGLISH);
    public static final FastDateFormat DAY_MTH_HR_MIN_SECS = FastDateFormat.getInstance("dd/MM HH:mm:ss",
            SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat DAY_MTH_YR_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM/yy hh:mma",
            SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat DAY_MTH_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM hh:mma",
            SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat HR_MIN_AMPM = FastDateFormat.getInstance("hh:mma", SERVER_TIME_ZONE,
            Locale.ENGLISH);
    public static final FastDateFormat HR_MIN_AMPM_TIMEZONE = FastDateFormat.getInstance("hh:mma z", SERVER_TIME_ZONE,
            Locale.ENGLISH);
    public static final FastDateFormat HR_MIN = FastDateFormat.getInstance("hh:mm", SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat MIN_SECS = FastDateFormat.getInstance("mm:ss", SERVER_TIME_ZONE, Locale.ENGLISH);

    public static final ThreadLocal<DecimalFormat> SECONDS = ThreadLocal.withInitial(() -> new DecimalFormat("0"));
    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING = ThreadLocal
            .withInitial(() -> new DecimalFormat("0.0"));
    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS = new ThreadLocal<DecimalFormat>() {
        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("0.#");
        }
    };

    public static String getRemaining(long millis, boolean milliseconds) {
        return getRemaining(millis, milliseconds, true);
    }

    public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
        if (milliseconds && duration < TimeUnit.MINUTES.toMillis(1)) {
            return (trail ? REMAINING_SECONDS_TRAILING : REMAINING_SECONDS).get().format(duration * 0.001).replace(',',
                    '.') + 's';
        }
        return DurationFormatUtils.formatDuration(duration,
                ((duration >= TimeUnit.HOURS.toMillis(1)) ? "HH:" : "") + "mm:ss");
    }

    public static String getCollonRemaining(long duration, boolean milliseconds) {
        return DurationFormatUtils.formatDuration(duration,
                ((duration >= TimeUnit.HOURS.toMillis(1)) ? "HH:" : "") + "mm:ss");
    }

    public static String getCleanRemaining(long duration, boolean milliseconds, boolean trail) {
        if (milliseconds && duration < TimeUnit.MINUTES.toMillis(1)) {
            return (trail ? REMAINING_SECONDS_TRAILING : REMAINING_SECONDS).get().format(duration * 0.001).replace(',',
                    '.');
        }
        return DurationFormatUtils.formatDuration(duration,
                ((duration >= TimeUnit.HOURS.toMillis(1)) ? "HH:" : "") + "mm:ss");
    }

    public static long getTimeByArg(String arg) {
        if (arg.equalsIgnoreCase("perm")) {
            return Long.MAX_VALUE;
        }
        long time = 0;
        if (StringUtils.containsIgnoreCase(arg, "s")) {
            time += JavaUtil.tryParseInt(arg.replace("s", ""));
        }
        if (StringUtils.containsIgnoreCase(arg, "m")) {
            time += JavaUtil.tryParseInt(arg.replace("m", "")) * 60L;
        }
        if (StringUtils.containsIgnoreCase(arg, "h")) {
            time += JavaUtil.tryParseInt(arg.replace("h", "")) * 60L * 60L;
        }
        if (StringUtils.containsIgnoreCase(arg, "d")) {
            time += JavaUtil.tryParseInt(arg.replace("d", "")) * 60L * 60L * 24L;
        }
        return time * 1000;
    }

    public static String formatSeconds(long seconds) {
        if (seconds <= 60)
            return JavaUtil.plurals(seconds, "секунда", "секунды", "секунд");
        else if (seconds <= 3540)
            return JavaUtil.plurals((int)Math.ceil(seconds / 60.0F), "минута", "минуты", "минут");
        else if (seconds <= 82800)
            return JavaUtil.plurals((int)Math.ceil(seconds / 3600.0F), "час", "часа", "часов");
        else if (seconds <= 2505600)
            return JavaUtil.plurals((int)Math.ceil(seconds / 86400.0F), "день", "дня", "дней");
        else if (seconds <= 28512000)
            return JavaUtil.plurals((int)Math.ceil(seconds / 2592000.0F), "месяц", "месяца", "месяцев");
        else return JavaUtil.plurals((int)Math.ceil(seconds / 3.1104E7F), "год", "года", "лет");
    }

}
