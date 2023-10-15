// MainActivity for recording work log using a beacon
//
// Copyright (C) 2018-2021  Masanobu UMEDA (umerin@ci.kyutech.ac.jp)
//
// $Id$

package jp.kyutech.example.worklogger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity class defines the main activity of this application.  An
 * application process starts with this activity when an application
 * is invoked.
 *
 * @author Masanobu UMEDA
 * @version $Revision$
 */

public class MainActivity extends AppCompatActivity
{
  private static final String LOGTAG = "MainActivity";
  private WorkRecordManager     recordManager = null;
  private StarterSwitch         starterSwitch = null;
  private LogLister             logLister = null;
  private Notifier              notifier = null;
  private AddPrevWork           addPrevWork = null;

  // NOTE: Remember a current application state because Dialogs cannot
  // be created after stopped.
  private boolean               is_started_p = false;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Log.d(LOGTAG, "onCreate()");

    setContentView(R.layout.activity_main);

    notifier = new Notifier(this);
    recordManager = new WorkRecordManager(this);

    ListView logList = (ListView)findViewById(R.id.logList);
    logLister = new LogLister(this, logList, recordManager);
    logList.setOnItemLongClickListener(logLister);

    ToggleButton starterButton = (ToggleButton)findViewById(R.id.starterButton);
    starterSwitch =
      new StarterSwitch(this, starterButton, recordManager);
    starterButton.setOnCheckedChangeListener(starterSwitch);

    Button addWorkBtn = (Button)findViewById(R.id.addWorkBtn);
    addPrevWork = new AddPrevWork(this, addWorkBtn);
    addWorkBtn.setOnClickListener(addPrevWork);

    if(savedInstanceState != null){
      onRestoreInstanceState(savedInstanceState);
    }
  }

  @Override
  protected void onRestart()
  {
    super.onRestart();
    Log.d(LOGTAG, "onRestart()");
  }

  @Override
  protected void onStart()
  {
    super.onStart();
    Log.d(LOGTAG, "onStart()");

    is_started_p = true;
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    Log.d(LOGTAG, "onResume()");
    updateView();
  }

  @Override
  protected void onPause() {
    Log.d(LOGTAG, "onPause()");
    super.onPause();
  }

  @Override
  protected void onStop() {
    Log.d(LOGTAG, "onStop()");
    super.onStop();

    is_started_p = false;
  }

  @Override
  protected void onDestroy()
  {
    Log.d(LOGTAG, "onDestroy()");
    notifier.destroy();
    super.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    Log.d(LOGTAG, "onSaveInstanceState()");
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedState)
  {
    super.onRestoreInstanceState(savedState);
    Log.d(LOGTAG, "onRestoreInstanceState()");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.option_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    //Toast toast = Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG);
    //toast.show();
    switch(item.getItemId()){
    case R.id.option_menu_about:
      showAbout();
      break;
    default:
      break;
    }
    return true;
  }

  public boolean isApplicationStarted()
  {
    // NOTE: Current application state must be checked before creating
    // a dialog because a dialog cannot be created after stopped.
    return is_started_p;
  }

  /*
   * Update the sub-views in this main view.
   */
  public void updateView()
  {
    logLister.updateListView();
    starterSwitch.updateStarterView();
  }

  /*
   * Post an informative message.
   *
   * @param title the title of the message.
   * @param message the message to be shown.
   */
  public void postInfoNotice(String title, String message)
  {
    notifier.postInfoNotice(title, message);
  }

  /*
   * Post an error message.
   *
   * @param title the title of the message.
   * @param message the message to be shown.
   */
  public void postErrorNotice(String title, String message)
  {
    notifier.postErrorNotice(title, message);
  }

  /*
   * Forward an event to popup a dialog to edit a start time.
   *
   * @param view the view containing a button.
   */
  public void editStartTime(View view)
  {
    // Forward the event to the logLister.
    logLister.editStartTime(view);
  }

  /*
   * Forward an event to popup a dialog to edit an end time.
   *
   * @param view the view containing a button.
   */
  public void editEndTime(View view)
  {
    // Forward the event to the logLister.
    logLister.editEndTime(view);
  }

  /*
   * Show an about dialog.
   */
  private void showAbout()
  {
    View messageView =
      getLayoutInflater().inflate(R.layout.about, null, false);

    /*
    // Force to always use default color.
    TextView textView = (TextView)messageView.findViewById(R.id.aboutCredits);
    int defaultColor = textView.getTextColors().getDefaultColor();
    textView.setTextColor(defaultColor);
    */

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setIcon(R.drawable.worklogger_icon);
    builder.setTitle(R.string.app_name);
    builder.setView(messageView);
    builder.setPositiveButton(R.string.app_about_yes,
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
    builder.show();
  }
}
