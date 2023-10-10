// Notifier for posting information and error messages
// 
// Copyright (C) 2018-2020  Masanobu UMEDA (umerin@ci.kyutech.ac.jp)
//
// $Id$

package jp.kyutech.example.worklogger;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Notifier
{
  private static final DateFormat date_format = DateFormat.getInstance();
    //new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
  private Notification		notice = null;
  private Context		context = null;
  private static final int	NOTIFY_INFO = 1;
  private static final int	NOTIFY_ERROR = 2;

  public Notifier(Context context)
  {
    this.context = context;
  }

  public void postInfoNotice(String title, String message)
  {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(System.currentTimeMillis());
    String now = date_format.format(new Timestamp(cal.getTimeInMillis()));

    Notification notif = new Notification.Builder(context)
      .setAutoCancel(true)
      .setContentTitle(title + " [" + now + "]")
      .setContentText(message)
      .setSmallIcon(R.drawable.worklogger_icon)
      .setWhen(System.currentTimeMillis())
      .build();
    NotificationManager manager =
      (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    manager.notify(NOTIFY_INFO, notif);
  }

  public void postErrorNotice(String title, String message)
  {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(System.currentTimeMillis());
    String now = date_format.format(new Timestamp(cal.getTimeInMillis()));

    Notification notif = new Notification.Builder(context)
      .setAutoCancel(false)
      .setPriority(Notification.PRIORITY_HIGH)
      .setContentTitle(title + " [" + now + "]")
      .setContentText(message)
      .setSmallIcon(R.drawable.worklogger_icon)
      .setWhen(System.currentTimeMillis())
      .build();
    NotificationManager manager =
      (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    manager.notify(NOTIFY_ERROR, notif);
  }

  public void destroy()
  {
    NotificationManager manager =
      (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    manager.cancel(NOTIFY_INFO);
    manager.cancel(NOTIFY_ERROR);
  }
}
