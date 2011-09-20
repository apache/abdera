package org.apache.abdera2.common.date;

/** 
 * Limited implementation of the ISO 8601 Interval construct. Not all valid
 * ISO 8601 interval representations can be represented -- only those that
 * use DateTime constructs compatible with the limited ISO 8601 DateTime
 * construct required for Atom
 */
public class Interval {

  private final int repeats;
  private final DateTime start;
  private final DateTime end;
  private final Duration duration;
  
  public Interval(DateTime start, DateTime end, int repeats) {
    this(start,end,null,repeats);
  }
  
  public Interval(DateTime start, Duration duration, int repeats) {
    this(start,null,duration,repeats);
  }

  public Interval(Duration duration, DateTime end, int repeats) {
    this(null,end,duration,repeats);
  }
  
  public Interval(DateTime start, DateTime end) {
    this(start,end,null,0);
  }
  
  public Interval(DateTime start, Duration duration) {
    this(start,null,duration,0);
  }

  public Interval(Duration duration, DateTime end) {
    this(null,end,duration,0);
  }
  
  public Interval(Duration duration) {
    this(null,null,duration,0);
  }
  
  public Interval(Duration duration,int repeats) {
    this(null,null,duration,repeats);
  }
  
  private Interval(
    DateTime start, 
    DateTime end, 
    Duration duration, 
    int repeats) {
    this.start = start;
    this.end = end;
    this.duration = duration;
    this.repeats = repeats;
  }
  
  public DateTime start() {
    return start;
  }
  
  public DateTime end() {
    return end;
  }
  
  public Duration duration() {
    return duration;
  }
  
  public int repeats() {
    return repeats;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((duration == null) ? 0 : duration.hashCode());
    result = prime * result + ((end == null) ? 0 : end.hashCode());
    result = prime * result + repeats;
    result = prime * result + ((start == null) ? 0 : start.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Interval other = (Interval) obj;
    if (duration == null) {
      if (other.duration != null)
        return false;
    } else if (!duration.equals(other.duration))
      return false;
    if (end == null) {
      if (other.end != null)
        return false;
    } else if (!end.equals(other.end))
      return false;
    if (repeats != other.repeats)
      return false;
    if (start == null) {
      if (other.start != null)
        return false;
    } else if (!start.equals(other.start))
      return false;
    return true;
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    if (repeats > 0)
      buf.append('R')
         .append(repeats)
         .append('/');
    if (start != null) 
      buf.append(start.toString());
    if (start != null && (end != null || duration != null)) 
      buf.append('/');
    if (duration != null)
      buf.append(duration.toString());
    if (end != null && duration != null)
      buf.append('/');
    if (end != null) 
      buf.append(end.toString());
    return buf.toString();
  }
  
  public static Interval parse(String val) {
    if (val == null || val.length() == 0) return null;
    int repeats = 0;
    DateTime start = null, end = null;
    Duration duration = null;
    String[] segments = val.split("/",3);
    int pos = 0;
    if (segments[pos].charAt(0) == 'R' ||
        segments[pos].charAt(0) == 'r') {
      repeats = Integer.parseInt(segments[0].substring(1));
      pos++;
    }
    if (segments[pos].charAt(0) == 'P' || 
        segments[pos].charAt(0) == 'p') {
      start = null;
      duration = Duration.parse(segments[pos]);
      pos++;
    } else {
      start = DateTime.valueOf(segments[pos]);
      pos++;
    }
    if (duration == null && 
        (segments[pos].charAt(0) == 'P' || 
         segments[pos].charAt(0) == 'p')) {
      duration = Duration.parse(segments[pos]);
      pos++;
    } else if (pos < segments.length){
      end = DateTime.valueOf(segments[pos]);
    }
    if (end == null && pos < segments.length) {
      end = DateTime.valueOf(segments[pos]);
    }
    return new Interval(start,end,duration,repeats);
  }
}
