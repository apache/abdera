package org.apache.abdera.model;

import static org.junit.Assert.*;

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
    AtomDate adClone = (AtomDate) ad.clone();
    assertEquals(ad, adClone);
    assertNotSame(ad, adClone);
  }

}
