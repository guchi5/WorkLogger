// Date and time utilities
//
// Copyright (C) 2018-2021  Masanobu UMEDA (umerin@ci.kyutech.ac.jp)
//
// $Id$

package jp.kyutech.example.worklogger;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTimeUtils
{
  /*
   * Returns a time plus a offset time from GMT time zone.
   * For example, 9 hours are added to a check-in time in JST.  This
   * method is defined for time representation of MS Excel.
   *
   * @return a java.sql.Time
   * @see getTimeWithTimeZoneOffset
   */
  public static Time getTimeWithTimeZoneOffset(Time time)
  {
    Calendar gmt_cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    Calendar loc_cal = new GregorianCalendar();
    gmt_cal.setTimeInMillis(time.getTime());
    loc_cal.setTimeInMillis(time.getTime());
    int hours =
      loc_cal.get(Calendar.HOUR_OF_DAY) - gmt_cal.get(Calendar.HOUR_OF_DAY);
    int minutes =
      loc_cal.get(Calendar.MINUTE) - gmt_cal.get(Calendar.MINUTE);
    gmt_cal.add(Calendar.HOUR_OF_DAY, hours);
    gmt_cal.add(Calendar.MINUTE, minutes);

    return new Time(gmt_cal.getTimeInMillis());
  }

  /*
   * Return true if startTime and endTime constitute a valid time rage.
   *
   * @param startTime
   * @param endTime
   * @return a boolean
   */
  public static boolean isValidTimeRange(Time startTime, Time endTime)
  {
    if(endTime == null){
      return true;
    }
    if(startTime == null){
      return false;
    }
    Calendar startCal = GregorianCalendar.getInstance();
    startCal.setTime(startTime);
    Calendar endCal = GregorianCalendar.getInstance();
    endCal.setTime(endTime);
    // NOTE: We have to take care of hour and minute only.
    if((startCal.get(Calendar.HOUR_OF_DAY) > endCal.get(Calendar.HOUR_OF_DAY)) ||
       (startCal.get(Calendar.HOUR_OF_DAY) == endCal.get(Calendar.HOUR_OF_DAY) &&
	startCal.get(Calendar.MINUTE) > endCal.get(Calendar.MINUTE))){
      return false;
    }
    return true;
  }

  /*
   * Return true if time1 =< time2 in hours, minutes, and seconds.
   *
   * @param time1
   * @param time2
   * @return a boolean
   */
  public static boolean isTimeBeforeTime(Time time1, Time time2)
  {
    Calendar cal1 = new GregorianCalendar();
    cal1.setTime(time1);
    cal1.set(Calendar.YEAR, 2000);
    cal1.set(Calendar.MONTH, 1);
    cal1.set(Calendar.DAY_OF_MONTH, 1);
    Calendar cal2 = new GregorianCalendar();
    cal2.setTime(time2);
    cal2.set(Calendar.YEAR, 2000);
    cal2.set(Calendar.MONTH, 1);
    cal2.set(Calendar.DAY_OF_MONTH, 1);

    return cal1.getTimeInMillis() <= cal2.getTimeInMillis();
  }

  /*
   * Returns a today's Date.
   *
   * @return a java.sql.Date
   */
  public static Date getToday()
  {
    return new Date(System.currentTimeMillis());
  }

  /*
   * Returns a yesterday's Date.
   *
   * @return a java.sql.Date
   */
  public static Date getYesterday()
  {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(System.currentTimeMillis());
    cal.set(Calendar.HOUR_OF_DAY, 0); // 00:00 AM of today
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.add(Calendar.DATE, -1);
    return new Date(cal.getTimeInMillis());
  }

  /*
   * Returns the first day of the last month.
   *
   * @return a java.sql.Date
   */
  public static Date getFirstDayOfLastMonth()
  {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(System.currentTimeMillis());
    cal.add(Calendar.MONTH, -1);
    cal.set(Calendar.DATE, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0); // 00:00 AM of today
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return new Date(cal.getTimeInMillis());
  }

  /*
   * Returns the last day of the last month.
   *
   * @return a java.sql.Date
   */
  public static Date getLastDayOfLastMonth()
  {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(System.currentTimeMillis());
    cal.set(Calendar.DATE, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0); // 00:00 AM of today
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.add(Calendar.SECOND, -1);
    return new Date(cal.getTimeInMillis());
  }

  /*
   * Returns the first day of the this month.
   *
   * @return a java.sql.Date
   */
  public static Date getFirstDayOfThisMonth()
  {
    return getFirstDayOf(System.currentTimeMillis());
  }

  /*
   * Returns the last day of the this month.
   *
   * @return a java.sql.Date
   */
  public static Date getLastDayOfThisMonth()
  {
    return getLastDayOf(System.currentTimeMillis());
  }

  /*
   * Returns the first day of a given time.
   *
   * @return a java.sql.Date
   */
  public static Date getFirstDayOf(long time)
  {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(time);
    cal.set(Calendar.DATE, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0); // 00:00 AM of today
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return new Date(cal.getTimeInMillis());
  }

  /*
   * Returns the last day of a given time.
   *
   * @return a java.sql.Date
   */
  public static Date getLastDayOf(long time)
  {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(time);
    cal.add(Calendar.MONTH, 1);
    cal.set(Calendar.DATE, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0); // 00:00 AM of today
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.add(Calendar.SECOND, -1);
    return new Date(cal.getTimeInMillis());
  }
}
