package com.nbk.smart5c.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * 全局时间工具类
 *
 * @author jiaxb
 */
public class DateUtil {

    /**
     * 兼容不规范的iso8601时间格式
     */
    private static final String ISO8601_COMPATIBLE_PATTERN = "yyyy-M-d'T'H:m:s";

    /**
     * 用以输出标准的iso8601时间格式
     */
    private static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * yyyy-MM-dd HH:mm:ss时间格式
     */
    public static final String PATTERN_Y_M_D_H_M_S = "yyyy-MM-dd HH:mm:ss";

    /**
     * yyyy-MM-dd HH:mm时间格式
     */
    public static final String PATTERN_Y_M_D_H_M = "yyyy-MM-dd HH:mm";

    /**
     * HH:mm时间格式
     */
    public static final String PATTERN_H_M = "HH:mm";

    /**
     * HHmmss时间格式
     */
    public static final String PATTERN_HMS = "HHmmss";

    /**
     * HH:mm:ss时间格式
     */
    public static final String PATTERN_H_M_S = "HH:mm:ss";

    /**
     * yyyyMMddHHmmss
     */
    public static final String PATTERN_YMDHMS = "yyyyMMddHHmmss";


    /**
     * yyyyMMddHHmm
     */
    public static final String PATTERN_YMDHM = "yyyyMMddHHmm";

    /**
     * 日期格式 yyyy-MM-dd
     */
    public static final String PATTERN_Y_M_D = "yyyy-MM-dd";

    /**
     * 日期格式 yyyyMMdd
     */
    public static final String PATTERN_YMD = "yyyyMMdd";

    /**
     * 年月格式 yyyy-MM
     */
    public static final String PATTERN_Y_M = "yyyy-MM";

    /**
     * 年月格式 yyyyMM
     */
    public static final String PATTERN_YM = "yyyyMM";

    public static final String zoneStr = "Asia/Shanghai";

    public static final DateTimeFormatter formatterCompatibleISO8601 = DateTimeFormatter.ofPattern(ISO8601_COMPATIBLE_PATTERN, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatterISO8601 = DateTimeFormatter.ofPattern(ISO8601_PATTERN, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatter_y_m_d_h_m_s = DateTimeFormatter.ofPattern(PATTERN_Y_M_D_H_M_S, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatter_y_m_d_h_m = DateTimeFormatter.ofPattern(PATTERN_Y_M_D_H_M, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatter_ymdhm = DateTimeFormatter.ofPattern(PATTERN_YMDHM, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatter_ymdhms = DateTimeFormatter.ofPattern(PATTERN_YMDHMS, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatter_h_m = DateTimeFormatter.ofPattern(PATTERN_H_M, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatter_h_m_s = DateTimeFormatter.ofPattern(PATTERN_H_M_S, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatter_hms = DateTimeFormatter.ofPattern(PATTERN_HMS, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatter_Y_M_D = DateTimeFormatter.ofPattern(PATTERN_Y_M_D, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatter_YMD = DateTimeFormatter.ofPattern(PATTERN_YMD, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatter_Y_M = DateTimeFormatter.ofPattern(PATTERN_Y_M, Locale.getDefault()).withZone(ZoneId.of(zoneStr));
    public static final DateTimeFormatter formatter_YM = DateTimeFormatter.ofPattern(PATTERN_YM, Locale.getDefault()).withZone(ZoneId.of(zoneStr));

    /**
     * yyyy_MM_dd_HH_mm_ss 转时间戳
     *
     * @param formatTime
     * @return
     */
    public static long yyyy_MM_dd_HH_mm_ssToTimestamp(String formatTime) {
        TemporalAccessor temporalAccessor = formatter_y_m_d_h_m_s.parse(formatTime);
        Instant instant = Instant.from(temporalAccessor);
        return instant.toEpochMilli();
    }

    /**
     * 时间戳 转 yyyy_MM_dd_HH_mm_ss
     */
    public static String timestampTo_yyyy_MM_dd_HH_mm_ss(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return formatter_y_m_d_h_m_s.format(LocalDateTime.ofInstant(instant, ZoneId.of(zoneStr)));
    }

    /**
     * 时间戳 转 yyyy_MM_dd
     */
    public static String timestampTo_yyyy_MM_dd(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return formatter_Y_M_D.format(LocalDateTime.ofInstant(instant, ZoneId.of(zoneStr)));
    }

    /**
     * 获取当前时间
     *
     * @return yyyyMddHHmmss
     */
    public static String getCurrentYmdHms(LocalDateTime localDateTime) {
        return formatter_ymdhms.format(localDateTime);
    }

    /**
     * 获取当前时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentY_m_d_H_m_s(LocalDateTime localDateTime) {
        return formatter_y_m_d_h_m_s.format(localDateTime);
    }


    /**
     * 格式校验
     *
     * @param timeStr           时间字符串
     * @param dateTimeFormatter 待校验的格式
     * @return
     */
    public static boolean verification(String timeStr, DateTimeFormatter dateTimeFormatter) {
        try {
            LocalDate.parse(timeStr, dateTimeFormatter);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    public static String getNowForISO8601() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        return formatterISO8601.format(nowDateTime);
    }

    public static long getDifferenceForNow(String keepaliveTime) {
        if (StringUtils.isBlank(keepaliveTime)) {
            return 0;
        }
        Instant beforeInstant = Instant.from(formatter_ymdhms.parse(keepaliveTime));
        return ChronoUnit.MILLIS.between(beforeInstant, Instant.now());
    }

    public static long getDifference(String startTime, String endTime) {
        if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
            return 0;
        }
        Instant startInstant = Instant.from(formatter_ymdhms.parse(startTime));
        Instant endInstant = Instant.from(formatter_ymdhms.parse(endTime));
        return ChronoUnit.MILLIS.between(startInstant, endInstant);
    }

    public static String getDateStrForHm(LocalDateTime date) {
        if (null == date) {
            date = LocalDateTime.now();
        }
        return formatter_h_m.format(date);
    }

    public static String getDateStrForH_m_s(LocalDateTime date) {
        if (null == date) {
            date = LocalDateTime.now();
        }
        return formatter_h_m_s.format(date);
    }

    public static String getDateStrForYMDHm(LocalDateTime date) {
        if (null == date) {
            date = LocalDateTime.now();
        }
        return formatter_ymdhm.format(date);
    }

    public static String getDateStrForHms(LocalDateTime date) {
        if (null == date) {
            date = LocalDateTime.now();
        }
        return formatter_hms.format(date);
    }

    public static Long localDateToMillis(LocalDate localDate) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
        // 将 LocalDateTime 转换为 ZonedDateTime（需要指定时区）
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        // 将 ZonedDateTime 转换为 Instant（这是时间戳的Java 8+版本）
        Instant instant = zonedDateTime.toInstant();
        // 将 Instant 转换为时间戳（毫秒）
        return instant.toEpochMilli();
    }

    public static String localDateToYyyymmdd(LocalDate localDate) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
        // 将 LocalDateTime 转换为 ZonedDateTime（需要指定时区）
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        // 将 ZonedDateTime 转换为 Instant（这是时间戳的Java 8+版本）
        Instant instant = zonedDateTime.toInstant();
        return formatter_YMD.format(LocalDateTime.ofInstant(instant, ZoneId.of(zoneStr)));
    }

    /**
     * localDate 转 yyyy_MM_dd
     */
    public static String localdate_To_yyyy_MM_dd(LocalDate localDate) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
        // 将 LocalDateTime 转换为 ZonedDateTime（需要指定时区）
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        // 将 ZonedDateTime 转换为 Instant（这是时间戳的Java 8+版本）
        Instant instant = zonedDateTime.toInstant();
        return formatter_Y_M_D.format(LocalDateTime.ofInstant(instant, ZoneId.of(zoneStr)));
    }

    /**
     * localDate 转 yyyy_MM_dd
     */
    public static String localdatetime_To_yyyy_MM_dd(LocalDateTime localDateTime) {
        // 将 LocalDateTime 转换为 ZonedDateTime（需要指定时区）
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        // 将 ZonedDateTime 转换为 Instant（这是时间戳的Java 8+版本）
        Instant instant = zonedDateTime.toInstant();
        return formatter_Y_M_D.format(LocalDateTime.ofInstant(instant, ZoneId.of(zoneStr)));
    }


    /**
     * localDate 转 yyyy_MM_dd
     */
    public static String localdatetime_To_yyyy_MM_ddHH_mm_ss(LocalDateTime localDateTime) {
        // 将 LocalDateTime 转换为 ZonedDateTime（需要指定时区）
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        // 将 ZonedDateTime 转换为 Instant（这是时间戳的Java 8+版本）
        Instant instant = zonedDateTime.toInstant();
        return formatter_y_m_d_h_m_s.format(LocalDateTime.ofInstant(instant, ZoneId.of(zoneStr)));
    }

    /**
     * localDate 转 yyyy_MM_dd
     */
    public static String localdate_To_yyyy_MM(LocalDate localDate) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
        // 将 LocalDateTime 转换为 ZonedDateTime（需要指定时区）
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        // 将 ZonedDateTime 转换为 Instant（这是时间戳的Java 8+版本）
        Instant instant = zonedDateTime.toInstant();
        return formatter_Y_M.format(LocalDateTime.ofInstant(instant, ZoneId.of(zoneStr)));
    }

    public static Long localDateTimeToMillis(LocalDateTime localDateTime) {
        // 将 LocalDateTime 转换为 ZonedDateTime（需要指定时区）
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        // 将 ZonedDateTime 转换为 Instant（这是时间戳的Java 8+版本）
        Instant instant = zonedDateTime.toInstant();
        // 将 Instant 转换为时间戳（毫秒）
        long timestampMillis = instant.toEpochMilli();
        return timestampMillis;
    }

    public static LocalDate millisToLocalDate(Long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        // 使用ZoneId来获取系统默认时区（或者你可以指定一个时区，例如ZoneId.of("UTC")）
        ZoneId zone = ZoneId.systemDefault();
        // 将Instant转换为ZonedDateTime对象
        // 然后使用ZonedDateTime的toLocalDate()方法获取LocalDate
        LocalDate localDate = instant.atZone(zone).toLocalDate();
        return localDate;
    }

    public static LocalDateTime millisToLocalDateTime(Long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        // 使用ZoneId来获取系统默认时区（或者你可以指定一个时区，例如ZoneId.of("UTC")）
        ZoneId zone = ZoneId.systemDefault();
        // 将Instant转换为ZonedDateTime对象
        // 然后使用ZonedDateTime的toLocalDate()方法获取LocalDate
        return instant.atZone(zone).toLocalDateTime();
    }

    public static LocalDate yyyyMMddToLocalDate(String ymd) {
        return LocalDate.parse(ymd, formatter_YMD);
    }

    public static LocalDateTime yyyyMMddhhmmssToLocalDateTime(String ymdhms) {
        return LocalDateTime.parse(ymdhms, formatter_ymdhms);
    }

    public static LocalDateTime yyyy_MM_dd_hh_mm_ssToLocalDateTime(String ymdhms) {
        return LocalDateTime.parse(ymdhms, formatter_y_m_d_h_m_s);
    }

    public static LocalDateTime yyyyMMddhhmmToLocalDateTime(String ymdhm) {
        return LocalDateTime.parse(ymdhm, formatter_ymdhm);
    }

    public static LocalDateTime yyyy_MM_ddhh_mm_ssToLocalDateTime(String ymdhms) {
        return LocalDateTime.parse(ymdhms, formatter_y_m_d_h_m_s);
    }

    public static LocalDateTime yyyy_MM_ddhh_mmToLocalDateTime(String ymdhm) {
        return LocalDateTime.parse(ymdhm, formatter_y_m_d_h_m);
    }
}
