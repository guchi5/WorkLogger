package jp.kyutech.example.worklogger;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import java.sql.Time;

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
/*
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
*/

        builder.create();
        builder.show();
/*
        alertDialog = builder.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        Button startButton = (Button)editTimeView.findViewById(R.id.startTimeButton);
        Button endButton = (Button)editTimeView.findViewById(R.id.endTimeButton);
        startButton.setText(record.getCheckinTimeAsString("        "));
        endButton.setText(record.getCheckoutTimeAsString("        "));
*/

    }

}
