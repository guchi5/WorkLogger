// WorkRecord for representing work hours
//
// Copyright (C) 2018-2021  Masanobu UMEDA (umerin@ci.kyutech.ac.jp)
//
// $Id$

package jp.kyutech.example.worklogger;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * WorkRecord class representing work hours.
 *
 * @author Masanobu UMEDA
 * @version $Revision$
 */

public class WorkRecord
{
  private static final SimpleDateFormat time_format = new SimpleDateFormat("HH:mm");
  private long		id = 0;		 // Record ID
  private String user = null;	 // UNUSED
  private Date date = null;	 // Date of a record
  private Time checkin = null;	 // Checkin time
  private Time checkout = null; // Checkout time

  public WorkRecord()
  {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(System.currentTimeMillis());
    cal.set(Calendar.HOUR_OF_DAY, 0); // 00:00 AM of today
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    this.date = new Date(cal.getTimeInMillis());
    this.user = "worker";
  }

  public WorkRecord(String user)
  {
    this();
    this.user = user;
  }

  /*
   * Return true if the date of this record is today.
   */
  public boolean isToday()
  {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(System.currentTimeMillis());
    cal.set(Calendar.HOUR_OF_DAY, 0); // 00:00 AM of today
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return date.getTime() == cal.getTimeInMillis();
  }

  /*
   * Return true if the date of this record is yesterday.
   */
  public boolean isYesterday()
  {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(System.currentTimeMillis());
    cal.set(Calendar.HOUR_OF_DAY, 0); // 00:00 AM of today
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.add(Calendar.DATE, -1);
    return date.getTime() == cal.getTimeInMillis();
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public long getId()
  {
    return id;
  }

  public void setUser(String user)
  {
    this.user = user;
  }

  public String getUser()
  {
    return user;
  }

  public void setDate(Date date)
  {
    this.date = date;
  }

  public Date getDate()
  {
    return date;
  }

  public String getDateAsString()
  {
    if(date == null){
      return null;
    }
    return date.toString();
  }

  public void setCheckinTime(Time time)
  {
    this.checkin = time;
  }

  public Time getCheckinTime()
  {
    return checkin;
  }

  /*
   * Returns a check-in time plus a offset time from GMT time zone.
   * For example, 9 hours are added to a check-in time in JST.  This
   * method is defined for time representation of MS Excel.
   *
   * @return a java.sql.Time
   * @see getCheckoutTimeWithTimeZoneOffset
   */
  public Time getCheckinTimeWithTimeZoneOffset()
  {
    return DateTimeUtils.getTimeWithTimeZoneOffset(checkin);
  }

  public String getCheckinTimeAsString()
  {
    if(checkin == null){
      return null;
    }
    return checkin.toString();
  }

  public String getCheckinTimeAsString(String default_value)
  {
    if(checkin == null){
      return default_value;
    }
    return checkin.toString();
  }

  public String getCheckinTimeAsHHMMString()
  {
    if(checkin == null){
      return null;
    }
    return time_format.format(checkin);
  }

  public void setCheckoutTime(Time time)
  {
    this.checkout = time;
  }

  public Time getCheckoutTime()
  {
    return checkout;
  }

  /*
   * Returns a check-out time plus a offset time from GMT time zone.
   * For example, 9 hours are added to a check-out time in JST.  This
   * method is defined for time representation of MS Excel.
   *
   * @return a java.sql.Time
   * @see getCheckoutTimeWithTimeZoneOffset
   */
  public Time getCheckoutTimeWithTimeZoneOffset()
  {
    return DateTimeUtils.getTimeWithTimeZoneOffset(checkout);
  }

  public String getCheckoutTimeAsString()
  {
    if(checkout == null){
      return null;
    }
    return checkout.toString();
  }

  public String getCheckoutTimeAsString(String default_value)
  {
    if(checkout == null){
      return default_value;
    }
    return checkout.toString();
  }

  public String getCheckoutTimeAsHHMMString()
  {
    if(checkout == null){
      return null;
    }
    return time_format.format(checkout);
  }

  /*
   * Remember a checkin time.  Return true if a record is updated.
   *
   * @return a boolean
   */
  public boolean checkinNow()
  {
    boolean updated_p = false;

    // Record a checkin time unless we have never checked.
    if(checkin == null){
      checkin = new Time(System.currentTimeMillis());
      updated_p = true;
    }
    if(checkout != null){
      checkout = null;
      updated_p = true;
    }
    return updated_p;
  }

  /*
   * Remember a checkout time.  Return true if a record is updated.
   *
   * @return a boolean
   */
  public boolean checkoutNow()
  {
    boolean updated_p = false;

    // Record a checkout time if we have checked in and have not
    // checked out.
    if((checkin != null) &&
       (checkout == null)){
      checkout = new Time(System.currentTimeMillis());
      updated_p = true;
    }
    return updated_p;
  }

  public String toString()
  {
    return String.format("[%d] %s %s=>%s (%s)",
			 id,
			 (date==null)?"":date.toString(),
			 (checkin==null)?"":checkin.toString(),
			 (checkout==null)?"":checkout.toString(),
			 user);
  }
}
