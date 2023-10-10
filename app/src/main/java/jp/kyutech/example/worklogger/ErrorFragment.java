// Show an error dialog
//
// Copyright (C) 2018-2020  Masanobu UMEDA (umerin@ci.kyutech.ac.jp)
//
// $Id$

package jp.kyutech.example.worklogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ErrorFragment extends DialogFragment
{
  private String title = "Error Information";
  private String message = null;

  public static void showErrorDialog(Activity activity,
				     String title,
				     String message
				     )
  {
    ErrorFragment dialog = new ErrorFragment();
    dialog.setTitle(title);
    dialog.setMessage(message);
    dialog.show(activity.getFragmentManager(), "ErrorDialog");
  }

  // NOTE: We cannot define a constructor with arguments.

  public void setTitle(String title)
  {
    this.title = title;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    AlertDialog.Builder builder =
      new AlertDialog.Builder(getActivity());
    Dialog dialog =
      builder
      .setTitle(title)
      .setMessage(message)
      .setNegativeButton(R.string.dialog_alert_accept,
                        new DialogInterface.OnClickListener()
			{
			  @Override
			  public void onClick(DialogInterface dialogInterface,
					      int i)
			  {
			    // Nothing to do.
			  }
                        })
      .create();

    return dialog;
  }
}
