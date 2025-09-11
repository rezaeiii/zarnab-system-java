package com.zarnab.panel.common.util;

import org.bardframework.time.LocalDateJalali;
import org.bardframework.time.LocalDateTimeJalali;
import org.bardframework.time.format.DateTimeFormatterJalali;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.zarnab.panel.common.constants.ConfigConstants.*;
import static java.time.ZoneOffset.UTC;

public class DateUtil {

    public static String toJalali(Instant dateToConvert) {
        return DateUtil.toJalali(dateToConvert, DATE_TIME_PATTERN);
    }

    public static String toJalali(Instant dateToConvert, String pattern) {
        if (dateToConvert == null)
            return null;

        LocalDateTime localDateTime = dateToConvert.atZone(ZoneId.of(ASIA_TEHRAN_ZONE)).toLocalDateTime();
        return LocalDateJalali.from(localDateTime)
                .format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String toJalali(Date dateToConvert) {
        if (dateToConvert == null)
            return null;

        return LocalDateTimeJalali.from(dateToConvert.toInstant()
                        .atZone(ZoneId.of(ASIA_TEHRAN_ZONE))
                        .toLocalDateTime())
                .format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    public static String toJalali(String dateToConvert) {
        if (dateToConvert == null)
            return null;
        try {
            return LocalDateTimeJalali.parse((dateToConvert))
                    .format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> getYearMonthBetween(LocalDateJalali d1, LocalDateJalali d2) {

        LocalDateJalali start = d1.compareTo(d2) <= 0 ? d1 : d2;
        LocalDateJalali end = d1.compareTo(d2) > 0 ? d1 : d2;

        int startIndex = start.getYear() * 12 + start.getMonthValue();
        int endIndex = end.getYear() * 12 + end.getMonthValue();

        return IntStream.rangeClosed(startIndex, endIndex)
                .mapToObj(i -> {
                    int year = i / 12;
                    int month = i % 12;
                    if (month == 0) {
                        month = 12;
                        year -= 1;
                    }
                    return String.format(Locale.ENGLISH, "%04d/%02d", year, month);
                })
                .sorted(Comparator.naturalOrder()) // ترتیب صعودی
                .collect(Collectors.toList());
    }

    public static LocalDateJalali parseJalali(String jalaliDate, String format) {
        LocalDateJalali localDateJalali;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        localDateJalali = LocalDateJalali.of(dtf.parse(String.valueOf(jalaliDate)).toString());
        return localDateJalali;
    }

    public static Date toGregorian(String jalaliDate) {
        if (jalaliDate == null)
            return null;
        try {
            return java.sql.Date.valueOf(LocalDateJalali.of(getDateStringToConvert(jalaliDate)).toLocalDate());
        } catch (Exception e) {
            return null;
        }
    }

    public static String toJalali(LocalDate dateToConvert) {
        return toJalali(dateToConvert, DATE_PATTERN);
    }

    public static String toJalali(LocalDate dateToConvert, String pattern) {
        if (dateToConvert == null)
            return null;

        return LocalDateJalali.from(dateToConvert)
                .format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String toJalali(LocalDateTime dateToConvert) {
        return toJalali(dateToConvert, DATE_TIME_PATTERN);
    }

    public static String toJalali(LocalDateTime dateToConvert, String pattern) {
        if (dateToConvert == null)
            return null;

        return LocalDateTimeJalali.from(dateToConvert.atZone(ZoneId.of(ASIA_TEHRAN_ZONE)).toLocalDateTime())
                .format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateJalali toJalaliLocalDate(Instant dateToConvert) {
        if (dateToConvert == null)
            return null;

        return LocalDateJalali.from(dateToConvert
                .atZone(ZoneId.of(ASIA_TEHRAN_ZONE))
                .toLocalDate());
    }

    public static LocalDateTimeJalali toJalaliLocalTimeDate(Instant dateToConvert) {
        if (dateToConvert == null)
            return null;

        return LocalDateTimeJalali.from(dateToConvert
                .atZone(ZoneId.of(ASIA_TEHRAN_ZONE))
                .toLocalDateTime());
    }

    public static LocalDateTimeJalali toJalaliLocalDate(LocalDateTime dateToConvert) {
        if (dateToConvert == null)
            return null;

        return LocalDateTimeJalali.from(dateToConvert);
    }

    public static LocalDateJalali toJalaliLocalDate(LocalDate dateToConvert) {
        if (dateToConvert == null)
            return null;

        return LocalDateJalali.from(dateToConvert);
    }

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static LocalDateJalali parseJalaliDate(String strJalali) {
        if (strJalali == null) {
            return null;
        } else {
            try {
                return LocalDateJalali.of(DateTimeFormatterJalali.ofPattern(DATE_PATTERN).parse(getDateStringToConvert(strJalali)).toString());
            } catch (Exception var5) {
                return null;
            }
        }
    }

    public static String removeTimePattern(String originalPattern) {
        if (originalPattern == null)
            return null;
        Pattern pattern = Pattern.compile(
                "[\\s]*[HhmsSaAXx][\\s:.-]*[HhmsSaAXx\\s:.-]*"
        );
        ;
        return pattern.matcher(originalPattern).replaceAll("").trim();
    }

    private static String getDateStringToConvert(String dateToConvert) {
        String[] date = normalizeToSlashFormat(dateToConvert).split("/");
        if (date.length > 1) {
            String y = date[0];
            String m = date[1].length() < 2 ? "0" + date[1] : date[1];
            String d = date[2].length() < 2 ? "0" + date[2] : date[2];
            dateToConvert = y + "/" + m + "/" + d;
        }
        return dateToConvert;
    }


    public static LocalDateTime convertToLocalDateTime(Date date) {
        ZonedDateTime zonedDateTime = date.toInstant().atZone(ZoneId.systemDefault());
        return zonedDateTime.toLocalDateTime();
    }

    public static String getJalaliMonthName(int month) {
        return switch (month) {
            case 1 -> "فروردين";
            case 2 -> "ارديبهشت";
            case 3 -> "خرداد";
            case 4 -> "تير";
            case 5 -> "مرداد";
            case 6 -> "شهريور";
            case 7 -> "مهر";
            case 8 -> "آبان";
            case 9 -> "آذر";
            case 10 -> "دي";
            case 11 -> "بهمن";
            case 12 -> "اسفند";
            default -> "";
        };
    }

    public static boolean isValidYearMonth(String dateStr) {
        dateStr = dateStr.replace("-", "/").trim();
        Pattern pattern = Pattern.compile("^(\\d{4})/(\\d{2})$");
        Matcher matcher = pattern.matcher(dateStr);

        if (!matcher.matches()) {
            return false;
        }

        int year = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));

        int currentYear = Year.now().getValue();
        int hijriCurrentYear = currentYear - 621;
        if (year < 1380 || year > hijriCurrentYear) {
            return false;
        }

        if (month < 1 || month > 12) {
            return false;
        }

        return true;
    }

    public static LocalDateTimeJalali nowJalali() {
        return LocalDateTimeJalali.now().plusMinutes(210);
    }

    public static String nowGregorian() {
        return Instant.now().atZone(ZoneId.of(UTC.getId())).format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    private static String normalizeToSlashFormat(String yearMonth) {
        return yearMonth.replace("-", "/");
    }
}
