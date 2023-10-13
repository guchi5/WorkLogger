// LogListListener for listing and editing work records
//
// Copyright (C) 2018-2021  Masanobu UMEDA (umerin@ci.kyutech.ac.jp)
//
// $Id$

package jp.kyutech.example.worklogger;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
class LogLister
  implements AdapterView.OnItemLongClickListener
{
  private static final String LOGTAG = "LogLister";
  private MainActivity		activity = null;
  private ListView logList = null;
  private WorkRecordManager	recordManager = null;
  private ArrayList<String> last_items = null;
  private AlertDialog alertDialog = null;



  LogLister(MainActivity activity,
            ListView logList,
            WorkRecordManager recordManager)
  {
    this.activity = activity;
    this.logList = logList;
    this.recordManager = recordManager;
  }

  /*
   * Start editing a time record when a list item is clicked.
   *
   * @return a boolean
   */
  public boolean onItemLongClick(AdapterView<?> av,
                                 View view,
                                 int position, // 0..?
                                 long id)
  {
    editTimeRecord(position);
    return true;		// No need to call onItemClick()
  }

  /*
   * Update the list of the newest work records within one month days.
   */
  void updateListView()
  {
     List<WorkRecord> records = recordManager.getWorkRecords(31);

    // Create a list of items to be displayed.
    ArrayList<String> items = new ArrayList<>();
    String sub_hours = null;

    for(WorkRecord record : records){
      String checkin_time = record.getCheckinTimeAsString("        ");
      String checkout_time = record.getCheckoutTimeAsString("        ");
      String arrow = (record.getCheckinTime()==null)?"  ":"=>";
      //int dummy = 1/0;
      System.out.println("Checkin : " + checkin_time);
      System.out.println("Checkout: " + checkout_time);

      if(!checkin_time.equals("        ") && !checkout_time.equals("        ")){
          Calendar st = GregorianCalendar.getInstance();
          Calendar lt = GregorianCalendar.getInstance();
          int st_hour = Integer.valueOf(checkin_time.split(":")[0]);
          int st_minute = Integer.valueOf(checkin_time.split(":")[1]);
          int st_second = Integer.valueOf(checkin_time.split(":")[2]);
          int lt_hour = Integer.valueOf(checkout_time.split(":")[0]);
          int lt_minute = Integer.valueOf(checkout_time.split(":")[1]);
          int lt_second = Integer.valueOf(checkout_time.split(":")[2]);
          st.set(Calendar.HOUR, st_hour);
          st.set(Calendar.MINUTE, st_minute);
          st.set(Calendar.SECOND, st_second);
          lt.set(Calendar.HOUR, lt_hour);
          lt.set(Calendar.MINUTE, lt_minute);
          lt.set(Calendar.SECOND, lt_second);
          Date st_date = st.getTime();
          Date lt_date = lt.getTime();
          sub_hours = String.format("%.1f",(lt_date.getTime()-st_date.getTime())/1000/3600.0);
      }
        String label =
	String.format("%s    %s %s %s\n%s",
		      record.getDate(), checkin_time, arrow, checkout_time, (sub_hours==null)?"":(sub_hours+" hours"));
      items.add(label);
    }

    if(items.equals(last_items)){
      // No need to update a listView because nothing is updated.
      return;
    }
    last_items = items;

    ArrayAdapter<String> adapter =
      new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, items);
    logList.setAdapter(adapter);
  }

  /*
   * Update a time record by replacing its start and end times.
   *
   * @param record a work record to update
   * @param startTime a start time of a work record
   * @param endTime a end time of a work record
   */
  private void updateTimeRecord(WorkRecord record,
                                Time startTime, Time endTime)
  {
    if(!DateTimeUtils.isValidTimeRange(startTime, endTime)){
      Log.d(LOGTAG, "editTimeRecord: Ignore invalid time range");
      return;
    }

    if(startTime != null){
      Log.d(LOGTAG, "editTimeRecord: Set checkin time:" + startTime);
      record.setCheckinTime(startTime);
    }
    if(endTime != null){
      Log.d(LOGTAG, "editTimeRecord: Set checkout time:" + endTime);
      record.setCheckoutTime(endTime);
    }
    recordManager.updateWorkRecord(record);
  }

  private Time getTimeOfButton(Button button)
  {
    Time time = null;
    try {
      time = Time.valueOf(button.getText().toString());
    } catch(IllegalArgumentException ex){
        // Ignore IllegalArgumentException.
    }
    return time;
  }

  /*
   * Pop up a dialog and start editing a time record located at a
   * position in a list.
   *
   * @param list_position a position in a list from the top
   */
  private void editTimeRecord(int list_position)
  {
    final View editTimeView =
      activity.getLayoutInflater().inflate(R.layout.time_editor, null, false);
    final WorkRecord record = recordManager.getWorkRecordAt(list_position);
    final String message =
      String.format(activity.getResources()
		    .getString(R.string.time_editor_edit_message_format),
		    record.getDateAsString());

    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setIcon(R.drawable.worklogger_icon);
    builder.setTitle(R.string.time_editor_title);
    builder.setMessage(message);
    builder.setView(editTimeView);
    builder.setPositiveButton
      (R.string.time_editor_yes,
       new DialogInterface.OnClickListener()
       {
	 @Override
	 public void onClick(DialogInterface dialogInterface,
                         int i)
	 {
	   Button startButton =
	     (Button)editTimeView.findViewById(R.id.startTimeButton);
	   Button endButton =
	     (Button)editTimeView.findViewById(R.id.endTimeButton);
	   Time startTime = getTimeOfButton(startButton);
	   Time endTime = getTimeOfButton(endButton);

	   updateTimeRecord(record, startTime, endTime);
	   updateListView();
	 }
       });
    builder.setNeutralButton
      (R.string.time_editor_cancel,
       new DialogInterface.OnClickListener()
       {
	 @Override
	 public void onClick(DialogInterface dialogInterface,
                         int i)
	 {
	   // Nothing to do.
	 }
       });
    builder.create();
    alertDialog = builder.show();
    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

    Button startButton = (Button)editTimeView.findViewById(R.id.startTimeButton);
    Button endButton = (Button)editTimeView.findViewById(R.id.endTimeButton);
    startButton.setText(record.getCheckinTimeAsString("        "));
    endButton.setText(record.getCheckoutTimeAsString("        "));
  }

  /*
   * Pop up a dialog to start editing a start time.
   *
   * @param view a view that invokes this method
   */
  void editStartTime(View view)
  {
    final Button acceptButton =
      (Button)alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
    final Button startButton =
      (Button)alertDialog.findViewById(R.id.startTimeButton);
    final Button endButton =
      (Button)alertDialog.findViewById(R.id.endTimeButton);
    final Time endTime = getTimeOfButton(endButton);
    Time startTime = getTimeOfButton(startButton);
    if(startTime == null){
      Calendar cal = GregorianCalendar.getInstance();
      startTime = new Time(cal.getTimeInMillis());
    }

    final TimePickerFragment timePicker = new TimePickerFragment();
    timePicker.setTimeSetListener(new TimePickerDialog.OnTimeSetListener()
      {
	@Override
	public void onTimeSet(TimePicker view, int hour, int minute)
	{
	  Calendar cal = GregorianCalendar.getInstance();
	  cal.set(Calendar.HOUR_OF_DAY, hour);
	  cal.set(Calendar.MINUTE, minute);
	  cal.set(Calendar.SECOND, 0);
	  cal.set(Calendar.MILLISECOND, 0);
	  Time startTime = new Time(cal.getTimeInMillis());
	  startButton.setText(startTime.toString());

	  if(DateTimeUtils.isValidTimeRange(startTime, endTime)){
	    Log.d(LOGTAG, "editTimeRecord: Valid time range: "
		  + startTime + " => " + endTime);
	    acceptButton.setEnabled(true);
	  } else {
	    Log.d(LOGTAG, "editTimeRecord: Ignore invalid time range: "
		  + startTime + " => " + endTime);
	    acceptButton.setEnabled(false);
	  }
	}
      });
    timePicker.setCurrentTime(startTime);
    timePicker.show(activity.getSupportFragmentManager(), "TimePickerDialog");
  }

  /*
   * Pop up a dialog to start editing an end time.
   *
   * @param list_position a position in a list from the top
   */
  void editEndTime(View view)
  {
    final Button acceptButton =
      (Button)alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
    final Button startButton =
      (Button)alertDialog.findViewById(R.id.startTimeButton);
    final Button endButton =
      (Button)alertDialog.findViewById(R.id.endTimeButton);
    final Time startTime = getTimeOfButton(startButton);
    Time endTime = getTimeOfButton(endButton);
    if(endTime == null){
      Calendar cal = GregorianCalendar.getInstance();
      endTime = new Time(cal.getTimeInMillis());
    }

    TimePickerFragment timePicker = new TimePickerFragment();
    timePicker.setTimeSetListener(new TimePickerDialog.OnTimeSetListener()
      {
	@Override
	public void onTimeSet(TimePicker view, int hour, int minute)
	{
	  Calendar cal = GregorianCalendar.getInstance();
	  cal.set(Calendar.HOUR_OF_DAY, hour);
	  cal.set(Calendar.MINUTE, minute);
	  cal.set(Calendar.SECOND, 0);
	  cal.set(Calendar.MILLISECOND, 0);
	  Time endTime = new Time(cal.getTimeInMillis());
	  endButton.setText(endTime.toString());

	  if(DateTimeUtils.isValidTimeRange(startTime, endTime)){
	    Log.d(LOGTAG, "editTimeRecord: Valid time range: "
		  + startTime + " => " + endTime);
	    acceptButton.setEnabled(true);
	  } else {
	    Log.d(LOGTAG, "editTimeRecord: Ignore invalid time range: "
		  + startTime + " => " + endTime);
	    acceptButton.setEnabled(false);
	  }
	}
      });
    timePicker.setCurrentTime(endTime);
    timePicker.show(activity.getSupportFragmentManager(), "TimePickerDialog");
  }
}
