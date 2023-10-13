// WorkRecordManager for managing work records
//
// Copyright (C) 2018-2020  Masanobu UMEDA (umerin@ci.kyutech.ac.jp)
//
// Work records will be updated asynchronously by a user or events
// generated by Beacons or some other devices.
//
// $Id$

package jp.kyutech.example.worklogger;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * WorkRecordManager class manages work hours using a
 * WorkRecordDatabase.  Please make sure that work records will be
 * updated asynchronously by a user or events generated by Beacons or
 * some other devices.
 *
 * @author Masanobu UMEDA
 * @version $Revision$
 */

public class WorkRecordManager {
    private static final String LOGTAG = "WorkRecordManager";
    private static final String TEXT_FORMAT = "%10s  %5s  %5s\n";
    private static final String DATE_FORMAT = "yyyy/MM/dd";
    private static final String TXT_TIME_FORMAT = "HH:mm"; // For java.text
    private MainActivity context = null;
    private WorkRecordDatabase recdb = null;

    public WorkRecordManager(MainActivity context) {
        this.context = context;

        recdb = new WorkRecordDatabase(context);
    }

    private Date nextDate(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(date.getTime());
        cal.add(Calendar.DATE, 1);
        return new Date(cal.getTimeInMillis());
    }

    private Date prevDate(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(date.getTime());
        cal.add(Calendar.DATE, -1);
        return new Date(cal.getTimeInMillis());
    }

    /*
     * Prepare a work record for today if necessary.
     *
     * @return a WorkRecord
     */
    private WorkRecord prepareWorkRecord() {
        WorkRecord record = recdb.getLastWorkRecord();
        if (record == null) {
            // Initial use.
            record = new WorkRecord();
            recdb.addWorkRecord(record);
        } else if (record.isYesterday()) {
            // Starting a new day after yesterday.
            record.checkoutNow();
            recdb.updateWorkRecord(record);
            record = new WorkRecord();
            recdb.addWorkRecord(record);
        } else if (!record.isToday()) {
            // Starting a new day after a long sleep.
            record = new WorkRecord();
            recdb.addWorkRecord(record);
        }
        return record;
    }

    /*
     * Update a work record according to a boolean state.
     *
     * @param isWorking true if starting a work.  Otherwise, false.
     */
    public synchronized void updateWorkRecordBy(boolean isWorking) {
        WorkRecord record = prepareWorkRecord();

        // Update a work record according to isWorking flag.
        if (isWorking) {
            if (record.checkinNow()) {
                recdb.updateWorkRecord(record);
            }
        } else {
            if (record.checkoutNow()) {
                recdb.updateWorkRecord(record);
            }
        }
    }

    /*
     * Return a current work record.
     *
     * @return a WorkRecord
     */
    public synchronized WorkRecord getCurrentWorkRecord() {
        return recdb.getLastWorkRecord();
    }

    /*
     * Return a work record located at the given position.
     *
     * @param position
     * @return a WorkRecord
     *
     * @see WorkRecord
     */
    public synchronized WorkRecord getWorkRecordAt(int position) {
        return recdb.getWorkRecordAt(position);
    }

    /*
     * Return the list of the newest work records in a database.
     *
     * @param count specifies the number of work records
     * @return a List<WorkRecord>
     *
     * @see WorkRecord
     */
    public synchronized List<WorkRecord> getWorkRecords(int count) {
        return recdb.getRecentWorkRecords(count);
    }

    /*
     * Return a work record which is the newest and not empty.
     *
     * @return a WorkRecord
     *
     * @see WorkRecord
     */
    public synchronized WorkRecord getLastAliveWorkRecord() {
        return recdb.getLastAliveWorkRecord();
    }

    /*
     * Update a work record in a database.
     *
     * @param record the record to be updated
     * @return a boolean true if the database is updated.
     *
     * @see WorkRecord
     */
    public synchronized boolean updateWorkRecord(WorkRecord record) {
        if (record == null) {
            return false;
        }
        recdb.updateWorkRecord(record);
        return true;
    }

    public synchronized boolean deleteWorkRecord(WorkRecord record){
        if(record == null){
            return false;
        }
        recdb.deleteWorkRecord(record);
        return true;
    }
    /*
     * Write the work records between a duration in a databse to
     * a given stream as text.
     *
     * @param ostream the output stream to write
     * @param fromDate the start of a duration
     * @param toDate the end of a duration
     *
     * @see WorkRecord
     */
    public synchronized void writeToTextStream(OutputStream ostream,
                                               Date fromDate,
                                               Date toDate
    ) {
        SimpleDateFormat date_format = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat time_format = new SimpleDateFormat(TXT_TIME_FORMAT);

        List<WorkRecord> records =
                recdb.getWorkRecordsBetween(fromDate, toDate);

        PrintWriter writer = new PrintWriter(ostream);
        try {
            writer.printf(TEXT_FORMAT, "Date", "Start", "End");

            Date last_date = fromDate;
            for (WorkRecord record : records) {
                // Insert missing data before a current record.
                while (last_date.compareTo(record.getDate()) < 0) {
                    writer.printf(TEXT_FORMAT, date_format.format(last_date), "", "");
                    last_date = nextDate(last_date);
                }
                writer.printf(TEXT_FORMAT,
                        date_format.format(record.getDate()),
                        (record.getCheckinTime() != null) ?
                                time_format.format(record.getCheckinTime()) : "",
                        (record.getCheckoutTime() != null) ?
                                time_format.format(record.getCheckoutTime()) : "");
                last_date = nextDate(record.getDate());
            }
            // Insert missing data after the last record.
            while (last_date.compareTo(toDate) <= 0) {
                writer.printf(TEXT_FORMAT, time_format.format(last_date), "", "");
                last_date = nextDate(last_date);
            }
        } finally {
            writer.close();
        }
    }
}