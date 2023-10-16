package jp.kyutech.example.worklogger;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Stream;

public class AddPrevWork implements View.OnClickListener{
    private MainActivity activity = null;
    private AlertDialog alertDialog = null;
    private ListView logList = null;
    private WorkRecordManager	recordManager = null;
    private ArrayList<String> last_items = null;
    String sub_hours = null;

    public AddPrevWork(MainActivity activity,
                       ListView logList,
                       WorkRecordManager recordManager){
        this.activity = activity;
        this.logList = logList;
        this.recordManager = recordManager;

    }
    @Override
    public void onClick(View view) {
        editTimeRecord();
        updateListView();
    }
    void updateListView()
    {
        List<WorkRecord> records = recordManager.getWorkRecords(31);

        // Create a list of items to be displayed.
        ArrayList<String> items = new ArrayList<>();
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
                sub_hours = String.format("%.1f",(lt.getTimeInMillis()-st.getTimeInMillis())/1000/3600.0);
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

    private void editTimeRecord()
    {
        final View editTimeView =
                activity.getLayoutInflater().inflate(R.layout.prev_time_editor, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.drawable.worklogger_icon);
        builder.setTitle(R.string.prev_time_editor_title);
        builder.setMessage("hello!");
        builder.setView(editTimeView);
        builder.setPositiveButton
                (R.string.prev_time_editor_yes,
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
                                Button dateButton =
                                        (Button)alertDialog.findViewById(R.id.dateButton);

                                Time startTime = getTimeOfButton(startButton);
                                Time endTime = getTimeOfButton(endButton);
                                Calendar cal = GregorianCalendar.getInstance();
                                String[] data = dateButton.getText().toString().split("-");
                                int[] date = Stream.of(data).mapToInt(Integer::parseInt).toArray();
                                cal.set(date[0], date[1]-1, date[2]);

                                WorkRecord record = new WorkRecord();
                                record.setDate(new Date(cal.getTimeInMillis()));
                                record.setCheckinTime(startTime);
                                record.setCheckoutTime(endTime);

                                Calendar now = GregorianCalendar.getInstance();
                                if(cal.getTimeInMillis() < now.getTimeInMillis()){
                                    recordManager.addPrevWorkRecord(record);
                                    System.out.println("レコード追加："+record);
                                    updateListView();
                                }


                            }
                        });
        builder.setNeutralButton
                (R.string.prev_time_editor_cancel,
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
    }

    void editStartTime(View view)
    {
        final Button acceptButton =
                (Button)alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        final Button startButton =
                (Button)alertDialog.findViewById(R.id.startTimeButton);
        final Button endButton =
                (Button)alertDialog.findViewById(R.id.endTimeButton);
        final Button dateButton =
                (Button)alertDialog.findViewById(R.id.dateButton);

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

                if(DateTimeUtils.isValidTimeRange(startTime, endTime) && !dateButton.getText().toString().equals("")){
                    acceptButton.setEnabled(true);
                } else {
                    acceptButton.setEnabled(false);
                }
            }
        });
        timePicker.setCurrentTime(startTime);
        timePicker.show(activity.getSupportFragmentManager(), "TimePickerDialog");
    }

    void editEndTime(View view)
    {
        final Button acceptButton =
                (Button)alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        final Button startButton =
                (Button)alertDialog.findViewById(R.id.startTimeButton);
        final Button endButton =
                (Button)alertDialog.findViewById(R.id.endTimeButton);
        final Button dateButton =
                (Button)alertDialog.findViewById(R.id.dateButton);

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

                if(DateTimeUtils.isValidTimeRange(startTime, endTime) && !dateButton.getText().toString().equals("")){
                    acceptButton.setEnabled(true);
                } else {
                    acceptButton.setEnabled(false);
                }
            }
        });
        timePicker.setCurrentTime(endTime);
        timePicker.show(activity.getSupportFragmentManager(), "TimePickerDialog");
    }

    public void showDatePickerDialog(View v) {
        final Button dateButton =
                (Button)alertDialog.findViewById(R.id.dateButton);
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

        DatePickerFragment datePicker = new DatePickerFragment();
        Time finalEndTime = endTime;
        datePicker.setDateSetListener(new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                dateButton.setText(String.format("%d-%d-%d",year, month+1, dayOfMonth));
                System.out.println("日付："+dateButton.getText().toString());
                if(DateTimeUtils.isValidTimeRange(startTime, finalEndTime) && !dateButton.getText().toString().equals("")){
                    acceptButton.setEnabled(true);
                } else {
                    acceptButton.setEnabled(false);
                }
            }
        });
        datePicker.show(activity.getSupportFragmentManager(), "datePicker");
    }


}
