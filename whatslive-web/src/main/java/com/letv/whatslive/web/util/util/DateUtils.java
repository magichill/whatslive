package com.letv.whatslive.web.util.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Title:
 * Desc:
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-8-14 下午3:00
 */
public class DateUtils {

    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyyMMdd");

    public final static String DATA_FORMAT = "yyyyMMddHHmmss";

    /**
     * 一天的毫秒数
     */
    private static final long ONE_DAY_MILLISECOND = 24 * 60 * 60 * 1000;

    /**
     * 获取当前时间的秒数
     *
     * @return
     */
    public static Integer currentTimeSeconds() {
        return Long.valueOf(System.currentTimeMillis() / 1000l).intValue();
    }

    public static String getToday() {
        return SDF_DATE.format(new Date());
    }

    public static Integer getTodayInt() {
        return Integer.parseInt(getToday());
    }

    /**
     * 日期格式化，自定义输出日期格式
     *
     * @param date
     * @return
     */
    public static String getFormatDate(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }


    /**
     * 返回日期段  如:beginDate:2014-08-31 endDate:2014-09-02 ==>list:[2014-08-31,2014-09-01,2014-09-02]
     * 时间格式为yyyy-MM-dd
     *
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    public static List<String> genrateDateInterval(String beginDate, String endDate) {

        List<String> dates = new ArrayList<String>();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date begin = df.parse(beginDate);
            Date end = df.parse(endDate);

            //开始日期小于结束日期
            if (begin.compareTo(end) > 0) {
                throw new RuntimeException("开始日期小于结束日期");
                //开始日期==结束日期
            } else if (begin.compareTo(end) == 0) {
                dates.add(beginDate);
                return dates;
            } else {

                //相差天数
                long interval = (end.getTime() - begin.getTime()) / ONE_DAY_MILLISECOND;
                long beginTime = begin.getTime();
                for (long i = 0; i <= interval; i++) {
                    String day = df.format(new Date(beginTime + ONE_DAY_MILLISECOND * i));
                    dates.add(day);
                }
                return dates;
            }
        } catch (ParseException e) {
            throw new RuntimeException("时间解析异常");
        }
    }

    /**
     * 开始日期结束日期是否在一个月内 如:beginDate:2014.08.01,endDate:2014.08.05 ==>true
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static boolean isInOneMonth(String beginDate, String endDate) {
        //todo
        return false;
    }

    /**
     * 开始日期结束日期是否在一周内 如:beginDate:2014.08.01,endDate:2014.08.08 ==>false
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static boolean isInOneWeek(String beginDate, String endDate) {
        //todo
        return false;
    }

    /**
     * 将LONG 类型转为 指定格式的时间
     *
     * @param fomate 时间格式
     * @param number 时间毫秒数
     * @return
     */
    public static String formateLong2Date(String fomate, Long number) {
        SimpleDateFormat sdf = new SimpleDateFormat(fomate);
        Date dt = new Date(number);
        return sdf.format(dt);
    }

    /**
     * 相隔秒数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long alsoSeconds(String startTime, String endTime) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        long alsoLong = -1;
        try {
            c1.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(startTime));
            c2.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(endTime));
            long startLong = c1.getTimeInMillis();
            long endLong = c2.getTimeInMillis();
            alsoLong = (endLong - startLong) / 1000;
        } catch (Exception e) {
            throw new RuntimeException("时间解析异常");
        }
        return alsoLong;
    }


    public static Date parse(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATA_FORMAT);
        Date date = null;
        try {
            date = sdf.parse(dateTime);
        } catch (Exception e) {
            throw new RuntimeException("时间解析异常");
        }
        return date;
    }

}
