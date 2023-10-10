// Picking a time using a dialog
// 
// Copyright (C) 2018-2020  Masanobu UMEDA (umerin@ci.kyutech.ac.jp)
//
// $Id$

package jp.kyutech.example.worklogger;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;
import androidx.fragment.app.DialogFragment;

public class TimePickerFragment extends DialogFragment
{
  private TimePickerDialog.OnTimeSetListener listener = null;
  private Time currentTime = null;

  void setTimeSetListener(TimePickerDialog.OnTimeSetListener listener)
  {
    this.listener = listener;
  }

  void setCurrentTime(Time time)
  {
    this.currentTime = time;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    Calendar cal = new GregorianCalendar();
    cal.setTime(currentTime);
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int minute = cal.get(Calendar.MINUTE);

    TimePickerDialog dialog =
      new TimePickerDialog(getActivity(), listener, hour, minute, false);

    return dialog;
  }

  public void onTimeSet(TimePicker view, int hour, int minute)
  {
  }
}
