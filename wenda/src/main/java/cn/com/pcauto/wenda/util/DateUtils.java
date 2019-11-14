package cn.com.pcauto.wenda.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author chensy
 */
public class DateUtils {

    public static String format(Date date) {
        return format(date, "yyyy-MM-dd");
    }

    public static String formatDetail() {
        return format(getNow(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatDetail(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }
       
    public static String formatYYMMDDHM(Date date) {
        return format(date, "yyyy-MM-dd HH:mm");
    }

    public static String formatMMDD(Date date) {
        return format(date, "MM-dd");
    }

    public static int formatData2YMD() {
        return NumberUtils.toInt(format(getNow(), "yyyyMMdd"));
    }

    public static int formatData2YMD(Date date) {
        return NumberUtils.toInt(format(date, "yyyyMMdd"));
    }

    public static String format(Date date, String fmt) {
        if (date == null) {
            return "";
        }
        DateFormat formatter = new SimpleDateFormat(fmt);
        return formatter.format(date);
    }

    public static Date parseDate(String dateStr, String fmt) {
        DateFormat formatter = new SimpleDateFormat(fmt);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException ex) {
            return null;
        }
    }
    
    /** 把date转成int
     * @param date
     * @return
     */
    public static int parseDateToInt(Date date) {
    	if(date == null) {
    		return 0;
    	}
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	int year = cal.get(Calendar.YEAR);
    	int mouth = cal.get(Calendar.MONTH) + 1;
    	int day = cal.get(Calendar.DAY_OF_MONTH);
    	return year * 10000 + mouth * 100 + day;
    }
    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date getNow() {
        return new Date(System.currentTimeMillis());
    }
    
    /**
     * 获取指定日期所在月份的开始
     * @param date
     * @return
     */
    public static Date getMonthBegin(Date date) {
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	c.set(Calendar.DATE, 1);
    	c.set(Calendar.HOUR_OF_DAY, 0);
    	c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }
    
    /**
     * 获取指定日期所在月份的结束
     * @param date
     * @return
     */
    public static Date getMonthEnd(Date date) {
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
    	c.set(Calendar.HOUR_OF_DAY, 23);
    	c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }
    
    public static int getYear(Date date){
    	return NumberUtils.toInt(format(date, "yyyy"));
    }
    
    public static int getMonth(Date date){
    	return NumberUtils.toInt(format(date, "MM"));
    }
    
    public static int getDay(Date date){
    	return NumberUtils.toInt(format(date, "dd"));
    }
    
    /**
     * 获取当前时间的整点
     * @return
     */
    public static Date getHour() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
    /**
     * 获取1小时前的时间的整点
     * @return
     */
    public static Date getBefore1Hour() {
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.HOUR_OF_DAY, -1);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
    /**
     * 获取24小时前的时间的整点
     * @return
     */
    public static Date getBefore24Hour() {
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.HOUR_OF_DAY, -24);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    /**
     * 获取今天凌晨的时间
     * @return
     */
    public static Date getBefore0Day() {
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.DATE, 0);
    	c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
    /**
     * 获取1天前的时间
     * @return
     */
    public static Date getBefore1Day() {
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.DATE, -1);
    	c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
    /**
     * 获取7天前的时间
     * @return
     */
    public static Date getBefore7Day() {
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.DATE, -7);
    	c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
    public static Date parseDate(String date) {
        try {
            if (date.length() == 10) {
                return new SimpleDateFormat("yyyy-MM-dd").parse(date);
            }
            if (date.length() == 19) {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
            }
            if (date.length() == 16) {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
            }
        } catch (ParseException ex) {
            throw new IllegalArgumentException(new StringBuilder(64).append(date).append("is not an valid format for: [yyyy-MM-dd] or [yyyy-MM-dd HH:mm:ss]  or [yyyy-MM-dd HH:mm]").toString());
        }
        throw new IllegalArgumentException(new StringBuilder(64).append(date).append("is not an valid format for: [yyyy-MM-dd] or [yyyy-MM-dd HH:mm:ss] or [yyyy-MM-dd HH:mm]").toString());
    }
    
    /**
     * 上一个自然周 开始时间
     *
     * @return
     */
    public static Date getLastWeekStart() {
        Calendar cur = Calendar.getInstance();
        cur.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cur.add(Calendar.DAY_OF_YEAR, -7);
        cur.set(Calendar.HOUR_OF_DAY, 0);
        cur.set(Calendar.MINUTE, 0);
        cur.set(Calendar.SECOND, 0);
        return new Date(cur.getTimeInMillis());
    }

    /**
     * 上一个自然周 结束时间
     *
     * @return
     */
    public static Date getLastWeekEnd() {
        Calendar cur = Calendar.getInstance();
        cur.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cur.add(Calendar.DAY_OF_YEAR, -1);
        cur.set(Calendar.HOUR_OF_DAY, 23);
        cur.set(Calendar.MINUTE, 59);
        cur.set(Calendar.SECOND, 59);
        return new Date(cur.getTimeInMillis());
    }
    
    /**
     * 得到几天前当天时间
     *
     * @param curDate
     * @param dayNum 负数前几天，正数后几天 00:00:00
     * @return
     */
    public static Date getSomeDateStart(Date curDate, int dayNum) {
    	if(curDate == null)
    		return null;
        Calendar cur = Calendar.getInstance();
        cur.setTime(curDate);
        cur.add(Calendar.DAY_OF_YEAR, dayNum);
        cur.set(Calendar.HOUR_OF_DAY, 0);
        cur.set(Calendar.MINUTE, 0);
        cur.set(Calendar.SECOND, 0);
        return new Date(cur.getTimeInMillis());
    }
    
    /**
     * 获取某天22时的时间
     * @param day
     * @return
     */
    public static Date get22hourTime(int day) {
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.DATE, day);
    	c.set(Calendar.HOUR_OF_DAY, 22);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
    public static String getFormatedTime(Date date) {
        String result = "";
        if (null != date) {
            Date now = new Date();
            long betweenTime = (now.getTime() - date.getTime()) / 1000;  //两个时间之间的差距,单位为秒
            long min60 = 60 * 60;  //60分钟之内
            long hour24 = 24 * 60 * 60; //24小时
            long sec60 = 60;//60秒

            if (betweenTime < sec60) {
            	result = "刚刚";//如果小于60秒，则显示刚刚
            } else if (betweenTime >= sec60 && betweenTime < min60) { //如果小于60分钟，则显示分钟
                result = (Math.round((float)betweenTime / 60) <= 0 ? 0 : Math.round((float)betweenTime / 60)) + "分钟前";
            } else if (betweenTime >= min60 && betweenTime < hour24) { //如果大于60分钟，小于24，则显示小时
                result = Math.round((float)betweenTime / 60 / 60) + "小时前";
            } else if (betweenTime >= hour24 && betweenTime < 2 * hour24) { //如果大于24，小于3天，则显示天
            	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            	result = "昨天 " + sdf.format(date);
            } else if (betweenTime >= 2 * hour24 && betweenTime < 3 * hour24) {
            	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            	result = "前天 " + sdf.format(date);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                result = sdf.format(date);
            }
        }
        return result;
    }
    /**
     * 发布时间显示优化
     * @param date
     * @return
     */
    public static String getNewFormatedTime(Date date) {
        String result = "";
        if (null != date) {
            Date now = new Date();
            long betweenTime = (now.getTime() - date.getTime()) / 1000;  //两个时间之间的差距,单位为秒
            long min10 = 60 * 10; //十分钟之内
            long min60 = 60 * 60;  //60分钟之内
            long hour24 = 24 * 60 * 60; //24小时

            if (betweenTime < min10) {
            	result = "刚刚";//如果十分钟之内，则显示刚刚
            } else if (betweenTime >= min10 && betweenTime < min60) { //如果小于60分钟，则显示分钟
                result = (Math.round((float)betweenTime / 60) <= 0 ? 0 : Math.round((float)betweenTime / 60)) + "分钟前";
            } else if (betweenTime >= min60 && betweenTime < hour24) { //如果大于60分钟，小于24，则显示小时
                result = Math.round((float)betweenTime / 60 / 60) + "小时前";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                result = sdf.format(date);
            }
        }
        return result;
    }
    /** 得到某天的结束时间
     * @param curDate
     * @return
     */
    public static Date getSomeDateEnd(Date curDate, int dayNum) {
    	if(curDate == null)
    		return null;
        Calendar cur = Calendar.getInstance();
        cur.setTime(curDate);
        cur.add(Calendar.DATE, dayNum);
        cur.set(Calendar.HOUR_OF_DAY, 23);  
        cur.set(Calendar.MINUTE, 59);  
        cur.set(Calendar.SECOND, 59);  
        cur.set(Calendar.MILLISECOND, 0);  
        return cur.getTime();
    }
    /**
     * 毫秒表示的时长转化为时分秒表示
     * @param time
     * @return
     */
    public static String longToTimeStr(long time){
        int hour = 0;
        int minute = 0;
        int second = (int) (time/1000);
        if (second >= 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        
        return (hour > 0 ? (getTwoLength(hour) + ":") :"")  +  getTwoLength(minute)  + ":"  + getTwoLength(second);
    }
    private static String getTwoLength(int data) {
        if(data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }
    /**
     * 获取每月开始的日期
     * @return
     */
    public static String getMonthStartDay(){
    	Calendar c = Calendar.getInstance();    
    	c.add(Calendar.MONTH, 0);
    	c.set(Calendar.DAY_OF_MONTH,1);
    	return format(c.getTime());
    }
}