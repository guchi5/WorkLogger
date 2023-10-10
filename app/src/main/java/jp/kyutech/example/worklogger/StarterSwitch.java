// Starter switch for starting and stopping the recorder by hand
//
// Copyright (C) 2020  Masanobu UMEDA (umerin@ci.kyutech.ac.jp)
//
// $Id$

package jp.kyutech.example.worklogger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * Starter switch class which implements ToggleButton.OnCheckedChangeListener.
 *
 * @author Masanobu UMEDA
 * @version $Revision$
 */

class StarterSwitch
  implements ToggleButton.OnCheckedChangeListener
{
  private static final String LOGTAG = "StarterSwitch";
  private static final int	VIBRATION_PERIOD = 1000; // 1000msec.
  private MainActivity		activity = null;
  private WorkRecordManager	recordManager = null;
  private ToggleButton button = null;
  private Drawable drawable_starter_stop = null;
  private Drawable drawable_starter_start = null;
  private Drawable drawable_starter_disabled = null;

  StarterSwitch(MainActivity activity,
                ToggleButton button,
                WorkRecordManager recordManager)
  {
    this.activity = activity;
    this.button = button;
    this.recordManager = recordManager;

    Bitmap bitmap_starter_stop =
      BitmapFactory.decodeResource(activity.getResources(),
				   R.drawable.starter_stop);
    drawable_starter_stop =
      new BitmapDrawable(activity.getResources(), bitmap_starter_stop);

    Bitmap bitmap_starter_start =
      BitmapFactory.decodeResource(activity.getResources(),
				   R.drawable.starter_start);
    drawable_starter_start =
      new BitmapDrawable(activity.getResources(), bitmap_starter_start);

    Bitmap bitmap_starter_disabled =
      BitmapFactory.decodeResource(activity.getResources(),
				   R.drawable.starter_disabled);
    drawable_starter_disabled =
      new BitmapDrawable(activity.getResources(), bitmap_starter_disabled);

    updateStarterView();
  }

  void saveInstanceState(Bundle outState)
  {
    outState.putBoolean("starterButton.isChecked", button.isChecked());
  }

  void restoreInstanceState(Bundle savedState)
  {
    button.setChecked(savedState.getBoolean("starterButton.isChecked", false));
    onCheckedChanged(button, button.isChecked());
  }

  /*
   * Called when a button was checked or unchecked.
   *
   * @param button the button which was clicked.
   * @param isChecked boolean true if the button is checked.
   */
  public void onCheckedChanged(CompoundButton button, boolean isChecked)
  {
    //Object var = null;
    //var.toString();

    Log.d(LOGTAG, "onCheckedChanged():" + isChecked);
    String error_message = null;
    try {
      recordManager.updateWorkRecordBy(isChecked);
    } catch(Exception ex){
      Log.e(LOGTAG, ex.getMessage(), ex);
      String title =
	activity.getResources().getString(R.string.dialog_alert_title);
      String message = MessageFormatter.getErrorReason(ex);
      ErrorFragment.showErrorDialog(activity, title, message);
    }
    activity.updateView();
  }

  /*
   * Check a starter button and change internal states.
   *
   * @param isChecked boolean true if the button is checked.
   */
  void setChecked(boolean isChecked)
  {
    button.setChecked(isChecked);
    onCheckedChanged(button, isChecked);
  }

  /*
   * Update the view of a starter button according to its internal
   * state.
   */
  void updateStarterView() {
    Log.d(LOGTAG, "updateStarterView(): ");

    // NOTE: The icons of the starter button are specified for each
    // button state (on and off) in res/drawable/starter_button.xml
    // while the texts of the starter button in
    // res/layout/activity_main.xml.  The following codes are examples
    // to change the icons and texts of a toggle button according to
    // its state by codes instead of resource files.
    /*
    if (button.isChecked()) {
      button.setBackground(drawable_starter_stop);
      button.setText(R.string.stop_working);
    } else {
      button.setBackground(drawable_starter_start);
      button.setText(R.string.start_working);
    }
    */
  }
}
