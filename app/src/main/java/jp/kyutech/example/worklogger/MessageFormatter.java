// MessageFormatter for constructing a well-structured message
//
// Copyright (C) 2018-2020  Masanobu UMEDA (umerin@ci.kyutech.ac.jp)
//
// $Id$

package jp.kyutech.example.worklogger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import android.content.Context;

public class MessageFormatter
{
  public static String createErrorMessage(Context context,
					  int format_id,
					  Exception ex,
					  String... params)
  {
    String format = context.getResources().getString(format_id);
    String reason = getErrorReason(ex);
    return String.format(format, reason, params);
  }

  public static String getErrorReason(Exception ex)
  {
    String reason = ex.getMessage();
    if(reason == null){
      ByteArrayOutputStream baout = new ByteArrayOutputStream();
      PrintStream out = new PrintStream(baout);
      try {
	ex.printStackTrace(out);
      } finally {
	out.close();
      }
      reason = ex.getClass().getSimpleName() + ": " + baout.toString();
    }
    return reason;
  }
}
