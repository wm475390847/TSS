package com.chaohu.conner.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wangmin
 */
public class TimeUtil {

    public static final String FORMAT_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_HOUR = "yyyy-MM-dd HH";
    public static final String FORMAT_DAY = "yyyy-MM-dd";
    public static final String FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'+'SSSS";

    /**
     * Date转为字符串时间戳
     *
     * @param date Date
     * @return 字符串时间戳（yyyy-MM-dd）格式
     */
    public static String dateToTimestamp(Date date) {
        return dateToTimestamp(date, FORMAT_DAY);
    }

    /**
     * Date转为字符串时间戳
     *
     * @param date   Date
     * @param format 时间格式
     * @return 字符串时间戳（format）格式
     */
    public static String dateToTimestamp(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    /**
     * 字符串时间戳转Date
     *
     * @param timestamp 字符串时间戳
     * @param format    时间格式
     * @return Date
     */
    public static Date timestampToDate(String timestamp, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 增加天数并转成字符串时间戳
     *
     * @param date   Date
     * @param day    天数
     * @param format 时间格式
     * @return Date
     */
    public static String addDayToTimestamp(Date date, int day, String format) {
        date = addDay(date, day);
        return format == null ? dateToTimestamp(date) : dateToTimestamp(date, format);
    }

    /**
     * 增加天数
     *
     * @param date Date
     * @param day  天数
     * @return Date
     */
    public static Date addDay(Date date, int day) {
        long curr = date.getTime();
        curr += (long) day * 24 * 60 * 60 * 1000;
        return new Date(curr);
    }

    /**
     * 增加秒数
     *
     * @param date   Date
     * @param second 秒数
     * @return Date
     */
    public static Date addSecond(Date date, int second) {
        long curr = date.getTime();
        curr += (long) second * 1000;
        return new Date(curr);
    }

    /**
     * 增加秒数并转成unix时间戳
     *
     * @param date   Date
     * @param second 秒数
     * @param format 格式
     * @return 时间戳
     */
    public static Long addSecondToUnix(Date date, int second, String format) {
        Date newDate = addSecond(date, second);
        return dateToUnix(newDate, format);
    }

    /**
     * 字符串时间戳转为unix时间戳
     *
     * @param timestamp 字符串时间戳
     * @return unis时间戳 单位ms
     */
    public static String timestampToUnix(String timestamp) {
        return String.valueOf(timestampToUnix(timestamp, FORMAT_SECOND));
    }

    /**
     * Date对象转unix时间戳
     *
     * @param date   Date
     * @param format 时间格式
     * @return unix时间戳
     */
    public static Long dateToUnix(Date date, String format) {
        String s = dateToTimestamp(date, format);
        return timestampToUnix(s, format);
    }

    /**
     * 字符串时间戳转unix时间戳
     *
     * @param timestamp 字符串时间戳
     * @param format    时间格式
     * @return unix时间戳
     */
    public static Long timestampToUnix(String timestamp, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(timestamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * unix时间戳转为字符串时间戳
     *
     * @param unix   unix时间戳
     * @param format 时间格式
     * @return 字符串时间戳
     */
    public static String unixToTimestamp(String unix, String format) {
        return unixToTimestamp(new Long(unix), format);
    }

    /**
     * unix时间戳转为字符串时间戳
     *
     * @param unix   unix时间戳
     * @param format 时间格式
     * @return 字符串时间戳
     */
    public static String unixToTimestamp(long unix, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(unix);
        return simpleDateFormat.format(date);
    }

    /**
     * unix时间戳转为Date对象
     *
     * @param unix unix时间戳
     * @return Date
     */
    public static Date unixToDate(String unix) {
        long lt = new Long(unix);
        return unixToDate(lt);
    }

    /**
     * unix时间戳转为Date对象
     *
     * @param unix unix时间戳
     * @return Date
     */
    public static Date unixToDate(long unix) {
        return new Date(unix);
    }

    /**
     * 世界时间转字符串时间戳
     *
     * @param utc    世界时间
     * @param format 时间格式
     * @return 字符串时间戳
     */
    public static String utcToTimestamp(String utc, String format) {
        Long ls = timestampToUnix(utc, FORMAT_UTC);
        if (ls != null) {
            Date date = unixToDate(ls);
            date = addSecond(date, 8 * 60 * 60);
            return dateToTimestamp(date, format);
        } else {
            return null;
        }
    }

    /**
     * 世界时间转Date
     *
     * @param utc 世界时间
     * @return 字符串时间戳
     */
    public static Date utcToDate(String utc) {
        Long ls = timestampToUnix(utc, FORMAT_UTC);
        if (ls != null) {
            Date date = unixToDate(ls);
            return addSecond(date, 8 * 60 * 60);
        } else {
            return null;
        }
    }

    /**
     * 世界时间转unix时间戳
     *
     * @param utc 世界时间
     * @return unix时间戳
     */
    public static Long utcToUnix(String utc) {
        return timestampToUnix(utc, FORMAT_UTC);
    }

    /**
     * 获取当前时间格式
     *
     * @return 时间格式
     */
    public static String getNowFormat() {
        String time = TimeUtil.dateToTimestamp(new Date(), "MM-dd-HH");
        String[] split = time.split("-");
        String s = split[2].compareTo("11") > 0 ? "PM" : "AM";
        return split[0] + "月" + split[1] + "日" + " " + s;
    }

    /**
     * 获取星期
     *
     * @param date Date
     * @return 星期
     */
    public static String getWeek(Date date) {
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int weekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekIndex < 0) {
            weekIndex = 0;
        }
        return weeks[weekIndex];
    }
}
