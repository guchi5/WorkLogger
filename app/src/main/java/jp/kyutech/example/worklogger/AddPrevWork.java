package jp.kyutech.example.worklogger;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddPrevWork implements View.OnClickListener{
    private MainActivity activity = null;
    private AlertDialog alertDialog = null;

    public AddPrevWork(MainActivity mainActivity, Button btn){
        this.activity = mainActivity;
    }
    @Override
    public void onClick(View view) {
        System.out.println("HELLO WORLD");
        editTimeRecord();
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
        System.out.println("動作確認");
/*
        final WorkRecord record = recordManager.getWorkRecordAt(list_position);
*/
/*
        final String message =
                String.format(activity.getResources()
                                .getString(R.string.time_editor_edit_message_format),
                        record.getDateAsString());
*/

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.drawable.worklogger_icon);
        builder.setTitle(R.string.prev_time_editor_title);
        builder.setMessage("hello!");
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


/*
                                updateTimeRecord(record, startTime, endTime);
                                updateListView();
*/


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
/*
        startButton.setText(record.getCheckinTimeAsString("        "));
        endButton.setText(record.getCheckoutTimeAsString("        "));
*/

    }

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

        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setDateSetListener(new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                dateButton.setText(String.format("%d-%d-%d",year, month, dayOfMonth));
            }
        });
        datePicker.show(activity.getSupportFragmentManager(), "datePicker");
    }


}
