// WorkRecordDatabase for persisting work records using a SQLite database
// Copyright (C) 2018-2020  Masanobu UMEDA (umerin@ci.kyutech.ac.jp)
//
// $Id$

package jp.kyutech.example.worklogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.util.LinkedList;
import java.util.List;

/**
 * WorkRecordDatabase class storing work hours in a SQLite database.
 *
 * @author Masanobu UMEDA
 * @version $Revision$
 */

public class WorkRecordDatabase extends SQLiteOpenHelper
{
  private static final String	LOGTAG = "WorkRecordDatabase";
  private static final String	DB_NAME = "iworkedharder.sqlite";
  private static final int	DB_VERSION = 1;
  private static final String	TABLE_WORKRECORDS = "workrecords";
  // Database fields
  private static final String	FIELD_ID = "id";
  private static final String	FIELD_USER = "user";
  private static final String	FIELD_DATE = "date";
  private static final String	FIELD_CHECKIN = "checkin";
  private static final String	FIELD_CHECKOUT = "checkout";

  public WorkRecordDatabase(Context context)
  {
    super(context, DB_NAME, null, DB_VERSION);
  }

  /*
   * Open a database.
   *
   * @param db the database to open.
   */
  @Override
  public void onOpen(SQLiteDatabase db)
  {
    super.onOpen(db);
  }

  /*
   * Create a table in a database.
   *
   * @param db the database to create a table.
   */
  @Override
  public void onCreate(SQLiteDatabase db)
  {
    db.execSQL(String.format("CREATE TABLE %s (" +
			     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
			     "user TEXT," +
			     "date TEXT," +
			     "checkin TEXT," +
			     "checkout TEXT)", TABLE_WORKRECORDS));
  }

  /*
   * Upgrade a table in a database.
   *
   * NOTE: Current implementation drops an old database and create a
   * new database if a new version is different from an old version.
   *
   * @param db the database to be upgraded.
   * @param oldVersion
   * @param newVersion
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    if(oldVersion != newVersion){
      db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_WORKRECORDS));
      onCreate(db);
    }
  }

  /*
   * Copy the contents of a WorkRecord to a new ContentValues for DB update.
   *
   * @param record the WorkRecord to be copied.
   */
  private ContentValues toContentValues(WorkRecord record)
  {
    ContentValues values = new ContentValues();
    values.put(FIELD_USER, record.getUser());
    values.put(FIELD_DATE, record.getDateAsString());
    values.put(FIELD_CHECKIN, record.getCheckinTimeAsString());
    values.put(FIELD_CHECKOUT, record.getCheckoutTimeAsString());
    return values;
  }

  /*
   * Copy a DB record specified by a curosr to a new WorkRecord.
   *
   * @param cursor the cursor pointing a record in a DB.
   */
  private WorkRecord toWorkRecord(Cursor cursor)
  {
    WorkRecord record = new WorkRecord();
    record.setId(Integer.parseInt(cursor.getString(0)));
    record.setUser(cursor.getString(1));
    if(cursor.getString(2) != null){
      record.setDate(Date.valueOf(cursor.getString(2)));
    }
    if(cursor.getString(3) != null){
      record.setCheckinTime(Time.valueOf(cursor.getString(3)));
    }
    if(cursor.getString(4) != null){
      record.setCheckoutTime(Time.valueOf(cursor.getString(4)));
    }
    return record;
  }

  /*
   * Add a new work record to a database.
   *
   * @param record the record to be added.
   *
   * @see WorkRecord
   */
  public void addWorkRecord(WorkRecord record)
  {
    Log.d(LOGTAG, "INSERT: " + record);

    // Check if a record of the same date does not exist.
    Date date = record.getDate();
    List<WorkRecord> list = getWorkRecordsBetween(date, date);
    if(list.size() != 0){
      throw new IllegalStateException
	("addWorkRecord: duplicated records for " + date);
    }

    SQLiteDatabase db = this.getWritableDatabase();
    try {
      ContentValues values = toContentValues(record);
      long id = db.insert(TABLE_WORKRECORDS,
			  null,	// nullColumnHack
			  values);
      if(id == -1){
	throw new IllegalArgumentException
	  ("addWorkRecord: cannot be inserted: " + record);
      }
      record.setId(id);
    } finally {
      db.close();
    }
  }

  /*
   * Update a work record in a database.
   *
   * @param record the record to be updated.
   *
   * @see WorkRecord
   */
  public void updateWorkRecord(WorkRecord record)
  {
    Log.d(LOGTAG, "UPDATE: " + record);

    SQLiteDatabase db = this.getWritableDatabase();
    try {
      ContentValues values = toContentValues(record);
      int nrows =
	db.update(TABLE_WORKRECORDS,
		  values,
		  FIELD_ID + " = ?",
		  new String[]{String.valueOf(record.getId())});
    } finally {
      db.close();
    }
  }

  /*
   * Delete a work record in a database (INCOMPLETE).
   *
   * @param record the record to be deleted.
   *
   * @see WorkRecord
   */
  public void deleteWorkRecord(WorkRecord record)
  {
    Log.d(LOGTAG, "DELETE: " + record);

    SQLiteDatabase db = this.getWritableDatabase();
    try {
      // BUGS: Actual deletion codes are incomplete.  Please fill in
      // this block to delete a work record in the database.  The
      // method updateWorkRecord() could be a good example for
      // understanding how to operate the records of a database.
        ContentValues values = toContentValues(record);
        int nrows =
               // db.update(TABLE_WORKRECORDS,
               //         values,
               //         FIELD_ID + " = ?",
               //         new String[]{String.valueOf(record.getId())});
            db.delete(TABLE_WORKRECORDS,FIELD_ID + " = ?",new String[]{String.valueOf(record.getId())});
    } finally {
      db.close();
    }
  }

  /*
   * Return the newest work record stored in a database.
   *
   * @return a WorkRecord
   *
   * @see WorkRecord
   */
  public WorkRecord getLastWorkRecord()
  {
    List<WorkRecord> records = getRecentWorkRecords(1);
    if(records.size() >= 1){
      return records.get(0);
    }
    return null;
  }

  /*
   * Return a work record located at the given position.
   *
   * @param position
   * @return a WorkRecord
   *
   * @see WorkRecord
   */
  public WorkRecord getWorkRecordAt(int position)
  {
    // Position: 0 ...
    List<WorkRecord> records = getRecentWorkRecords(position + 1);
    if(records.size() >= position+1){
      return records.get(position);
    }
    return null;
  }

  /*
   * Return the list of the newest work records in a database.
   *
   * @param count specifies the number of work records
   * @return a List<WorkRecord>
   *
   * @see WorkRecord
   */
  public List<WorkRecord> getRecentWorkRecords(int count)
  {
    List<WorkRecord> records = new LinkedList<WorkRecord>();

    String query =
      String.format("SELECT * FROM %s ORDER BY %s DESC",
		    TABLE_WORKRECORDS, FIELD_DATE);

    SQLiteDatabase db = this.getWritableDatabase();
    try {
      Cursor cursor = db.rawQuery(query, null);
      try {
	if(cursor.moveToFirst()){
	  do {
	    if(count-- == 0){
	      break;
	    }
	    WorkRecord record = toWorkRecord(cursor);
	    records.add(record);
	  } while (cursor.moveToNext());
	}
      } finally {
	cursor.close();
      }
    } finally {
      db.close();
    }

    return records;
  }

  /*
   * Return the list of work records between a duration.
   *
   * @param fromDate specifies the beginning of the duration.
   * @param toDate specifies the end of the duration.
   * @return a List<WorkRecord>
   *
   * @see WorkRecord
   */
  public List<WorkRecord> getWorkRecordsBetween(Date fromDate, Date toDate)
  {
    List<WorkRecord> records = new LinkedList<WorkRecord>();

    String query =
      String.format("SELECT * FROM %s WHERE %s >= '%s' and %s <= '%s' " +
		    " ORDER BY %s ASC",
		    TABLE_WORKRECORDS,
		    FIELD_DATE, fromDate.toString(),
		    FIELD_DATE, toDate.toString(),
		    FIELD_ID);

    SQLiteDatabase db = this.getWritableDatabase();
    try {
      Cursor cursor = db.rawQuery(query, null);
      try {
	if(cursor.moveToFirst()){
	  do {
	    WorkRecord record = toWorkRecord(cursor);
	    records.add(record);
	  } while(cursor.moveToNext());
	}
      } finally {
	cursor.close();
      }
    } finally {
      db.close();
    }

    return records;
  }

  /*
   * Return a work record which is the newest and not empty.
   *
   * @return a WorkRecord
   *
   * @see WorkRecord
   */
  public WorkRecord getLastAliveWorkRecord()
  {
    String query =
      String.format("SELECT * FROM %s WHERE NOT(NULL(%s)) ORDER BY %s DESC",
		    TABLE_WORKRECORDS, FIELD_CHECKIN, FIELD_ID);

    SQLiteDatabase db = this.getWritableDatabase();
    try {
      Cursor cursor = db.rawQuery(query, null);
      try {
	if(cursor.moveToFirst()){
	  WorkRecord record = toWorkRecord(cursor);
	  return record;
	}
      } finally {
	cursor.close();
      }
    } finally {
      db.close();
    }
    return null;
  }
}
