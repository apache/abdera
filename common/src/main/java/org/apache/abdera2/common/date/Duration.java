package org.apache.abdera2.common.date;
/**
 * Basic implementation of the ISO 8601 Duration format. 
 * Currently, the only duration format supported is the 
 * P[n]Y[n]M[n]DT[n]H[n]M[n]S syntax. The alternative 
 * PYYYYMMDDThhmmss and P[YYYY]-[MM]-[DD]T[hh]:[mm]:[ss]
 * forms are NOT supported.
 */
public class Duration {

  private final double y, m, w, d, h, i, s;

  public Duration(double s) {
    this(0,0,0,0,0,0,s);
  }
  
  public Duration(double i, double s) {
    this(0,0,0,0,0,i,s);
  }
  
  public Duration(double h, double i, double s) {
    this(0,0,0,0,h,i,s);
  }
  
  public Duration(double d, double h, double i, double s) {
    this(0,0,0,d,h,i,s);
  }
  
  public Duration(double w, double d, double h, double i, double s) {
    this(0,0,w,d,h,i,s);
  }
  
  public Duration(double m, double w, double d, double h, double i, double s) {
    this(0,m,w,d,h,i,s);
  }
  
  public Duration(double y, double m, double w, double d, double h, double i, double s) {
    this.y = Math.max(0, y);
    this.m = Math.max(0, m);
    this.w = Math.max(0, w);
    this.d = Math.max(0, d);
    this.h = Math.max(0, h);
    this.i = Math.max(0, i);
    this.s = Math.max(0, s);
    if (is_frac(y)&&!is_smallest(m,w,d,h,i,s)) 
      throw new IllegalArgumentException();
    if (is_frac(m)&&!is_smallest(w,d,h,i,s)) 
      throw new IllegalArgumentException();
    if (is_frac(w)&&!is_smallest(d,h,i,s)) 
      throw new IllegalArgumentException();
    if (is_frac(d)&&!is_smallest(h,i,s)) 
      throw new IllegalArgumentException();
    if (is_frac(h)&&!is_smallest(i,s)) 
      throw new IllegalArgumentException();
    if (is_frac(i)&&!is_smallest(s)) 
      throw new IllegalArgumentException();
  }
  
  public double years() {
    return y;
  }
  
  public double months() {
    return m;
  }
  
  public double weeks() {
    return w;
  }
  
  public double days() {
    return d;
  }
  
  public double hours() {
    return h;
  }
  
  public double minutes() {
    return i;
  }
  
  public double seconds() {
    return s;
  }
    
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(d);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(h);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(i);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(m);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(s);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(w);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    Duration other = (Duration) obj;
    if (Double.doubleToLongBits(d) != Double.doubleToLongBits(other.d))
      return false;
    if (Double.doubleToLongBits(h) != Double.doubleToLongBits(other.h))
      return false;
    if (Double.doubleToLongBits(i) != Double.doubleToLongBits(other.i))
      return false;
    if (Double.doubleToLongBits(m) != Double.doubleToLongBits(other.m))
      return false;
    if (Double.doubleToLongBits(s) != Double.doubleToLongBits(other.s))
      return false;
    if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w))
      return false;
    if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
      return false;
    return true;
  }

  public String toString() {
    StringBuilder buf = new StringBuilder("P");
    
    if (y > 0) 
      buf.append(is_smallest(m,w,d,h,i,s)&&is_frac(y)?Double.toString(y):Integer.toString((int)y))
         .append('Y');
    
    if (m > 0) 
      buf.append(is_smallest(w,d,h,i,s)&&is_frac(m)?Double.toString(m):Integer.toString((int)m))
         .append('M');
    
    if (w > 0) 
      buf.append(is_smallest(d,h,i,s)&&is_frac(w)?Double.toString(w):Integer.toString((int)w))
         .append('W');
    
    if (d > 0) 
      buf.append(is_smallest(h,i,s)&&is_frac(d)?Double.toString(d):Integer.toString((int)d))
         .append('D');
    
    if (h > 0 || i > 0 || s > 0)
      buf.append('T');
    
    if (h > 0) 
      buf.append(is_smallest(i,s)&&is_frac(h)?Double.toString(h):Integer.toString((int)h))
         .append('H');
    
    if (i > 0) 
      buf.append(is_smallest(s)&&is_frac(i)?Double.toString(i):Integer.toString((int)i))
         .append('M');
    
    if (s > 0) 
      buf.append(is_frac(s)?Double.toString(s):Integer.toString((int)s))
         .append('S');

    return buf.toString();
  }
  
  private boolean is_frac(double d) {
    return d%1!=0;
  }
  
  private boolean is_smallest(double... ds) {
    for (double i : ds)
      if (i > 0) return false;
    return true;
  }
  
  public static Duration parse(String val) {
    double y = 0, m = 0, w = 0, d = 0, h = 0, i = 0, s = 0;
    if (val == null) return null;
    int p = 0, l = val.length();
    if (val.charAt(p) != 'P' && val.charAt(p) != 'p')
      throw new IllegalArgumentException();
    int r = ++p;
    boolean t = false;
    while(p < l) {
      while(digit(val.charAt(r)) && r < l) r++;
      String v = val.substring(p,r);
      double dv = Double.parseDouble(v);
      switch(val.charAt(r)) {
      case 'Y': case 'y': y = dv; break;
      case 'M': case 'm': if (!t) m = dv; else i = dv; break;
      case 'W': case 'w': w = dv; break;
      case 'H': case 'h': h = dv; break;
      case 'D': case 'd': d = dv; break;
      case 'S': case 's': s = dv; break;
      case 'T': case 't': t = true; break;
      default:  throw new IllegalArgumentException();
      }
      p = r+1;
      r = p;
    }
    return new Duration(y,m,w,d,h,i,s);
  }
  
  private static boolean digit(char c) {
    return (c >= '0' && c <= '9') || c == '.' || c==',';
  }
}
