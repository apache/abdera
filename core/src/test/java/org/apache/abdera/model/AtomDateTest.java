package org.apache.abdera.model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class AtomDateTest {

    @Test
    public void testHashCode() {
        long time = System.currentTimeMillis();

        AtomDate ad1 = new AtomDate(time);
        assertTrue(ad1.hashCode() == ad1.hashCode());

        AtomDate ad2 = new AtomDate(time + 10);
        assertFalse(ad1.hashCode() == ad2.hashCode());

    }

    @Test
    public void testAtomDateDate() {
        Date now = new Date();
        AtomDate adNow = new AtomDate(now);
        assertEquals(now, adNow.getDate());

        // mutate 'now', to assert AtomDate cloned value
        now.setTime(now.getTime() + 10);
        assertFalse(now.getTime() == adNow.getTime());
    }

    @Test
    public void testGetDate() {
        Date now = new Date();
        AtomDate adNow = new AtomDate(now);
        assertEquals(now, adNow.getDate());

        // getDate, then mutate to assert it was cloned
        Date now2 = adNow.getDate();
        now2.setTime(now2.getTime() + 10);
        assertFalse(now2.equals(adNow.getDate()));
    }

    @Test
    public void testSetValueDate() {
        Date now = new Date();
        AtomDate adNow = new AtomDate(now);
        assertEquals(now, adNow.getDate());

        // set time, then mutate 'now' to assert cloning
        now.setTime(now.getTime() + 10);
        adNow.setValue(now);
        now.setTime(now.getTime() + 10);
        assertFalse(now.equals(adNow.getDate()));
    }

    @Test
    public void testClone() {
        AtomDate ad = new AtomDate();
        AtomDate adClone = (AtomDate)ad.clone();
        assertEquals(ad, adClone);
        assertNotSame(ad, adClone);
    }

    @Test
    public void testAtomDate() {
        Date now = new Date();
        AtomDate atomNow = AtomDate.valueOf(now);
        String rfc3339 = atomNow.getValue();
        atomNow = AtomDate.valueOf(rfc3339);
        Date parsed = atomNow.getDate();
        assertEquals(now, parsed);
    }

    @Test
    public void testAtomDate2() {
        String date = "2007-12-13T14:15:16.123Z";
        AtomDate atomDate = new AtomDate(date);
        Calendar calendar = atomDate.getCalendar();
        atomDate = new AtomDate(calendar);
        assertEquals(date, atomDate.toString());
    }

    @Test
    public void testAtomDate3() {
        long date = System.currentTimeMillis();
        AtomDate atomDate = new AtomDate(date);
        Calendar calendar = atomDate.getCalendar();
        atomDate = new AtomDate(calendar);
        assertEquals(date, atomDate.getTime());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalDateFormat() {
        String date = "";
        AtomDate atomDate = new AtomDate(date);
    }

}
