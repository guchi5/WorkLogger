package jp.kyutech.example.worklogger;

import org.junit.Test;

import java.sql.Time;

import static org.junit.Assert.assertEquals;

/**
 * WorkRecordTest class to run unit tests on the development machine
 * (host).
 *
 * @author Masanobu UMEDA
 * @version $Revision$
 */

public class WorkRecordTest {
    @Test
    public void isToday() {
        WorkRecord record = new WorkRecord();
        assertEquals(true, record.isToday());
    }

    @Test
    public void isYesterday() {
        WorkRecord record = new WorkRecord();
        assertEquals(false, record.isYesterday());
    }

    @Test
    public void checkinNow() {
        WorkRecord record = new WorkRecord();
        assertEquals(true, record.checkinNow());
        // Checkin twice.
        assertEquals(false, record.checkinNow());
    }

    @Test
    public void checkoutNow() {
        WorkRecord record = new WorkRecord();
        assertEquals(true, record.checkinNow());
        assertEquals(true, record.checkoutNow());
        // Checkout twice.
        assertEquals(false, record.checkoutNow());

        // Checkout now without checkin now.
        WorkRecord record2 = new WorkRecord();
        assertEquals(false, record2.checkoutNow());
        assertEquals(false, record2.checkoutNow());
    }

    @Test
    public void checkinTime() {
        WorkRecord record = new WorkRecord();
        assertEquals(true, record.checkinNow());
        // The checkin time must be the same as a current time.
        assertEquals(new Time(System.currentTimeMillis()), record.getCheckinTime());
    }
}
