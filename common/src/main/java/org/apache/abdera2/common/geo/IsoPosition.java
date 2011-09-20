/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera2.common.geo;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ISO 6709 Position
 */
public final class IsoPosition 
  implements Serializable {

  private static final long serialVersionUID = -2330958817323976941L;
  private final Double alt, d_lat, d_lon;
  
  public IsoPosition(double lat, Double lon) {
    this(lat,lon,null);
  }
  
  public IsoPosition(
    Double lat, Double lon, Double alt) {
    this.d_lat = lat;
    this.d_lon = lon;
    this.alt = alt;
  }
 
  private static void toDMSString(StringBuilder buf, double dec, boolean lat) {
    boolean z = dec<0;
    dec = Math.abs(dec);
    int d = (int)dec;
    double mm = (dec-d)*60;
    int m = (int)mm;
    double ss = (mm-m)*60;
    buf.append(String.format("%+0"+(lat?"3":"4")+"d%02d",z?-d:d,m));
    if (ss < 10) buf.append('0');
    buf.append(String.format("%.6f",ss));
  }
  
  public String toString(boolean usedms) {
    StringBuilder buf = 
      new StringBuilder();
    if (!usedms) {
      double ln = getLatitude();
      String lat = String.format("%+f",getLatitude());
      if (ln < 10 && ln > -10)
        lat = lat.replaceFirst("([+-])","$10");    
      buf.append(lat);
      
      ln = getLongitude();
      String lon = String.format("%+f",ln);
      if (ln < 100 && ln > -100)
        lon = lon.replaceFirst("([+-])","$10");
      if (ln < 10 && ln > -10)
        lon = lon.replaceFirst("([+-])","$10");
      buf.append(lon);
    } else {
      toDMSString(buf,getLatitude(),true);
      toDMSString(buf,getLongitude(),false);
    }
    if (alt != null) 
      buf.append(String.format("%+f",getAltitude()));
    return buf.append('/').toString();  
  }
  
  public String toString() {
    return toString(false);
  }
  
  public double getLatitude() {
    return d_lat;
  }
  
  public double getLongitude() {
    return d_lon;
  }
  
  public double getAltitude() {
    return alt;
  }
  
  private static double toDegrees(
      Integer d1, 
      Integer m1, 
      Integer s1, 
      Double f1) {
    double d = Math.abs(d1);
    double m = m1 != null ? m1 : 0.0;
    double s = s1 != null ? s1 : 0.0;
    if (s1 != null && f1 != null)
      s += f1;
    else if (m1 != null && f1 != null)
      m += f1;
    else if (f1 != null)
      d += f1;
    double ret = d + (m/60) + (s/3600);
    return d1 < 0 ? -ret : ret;
  }
  
  public static IsoPosition parse(String val) {
    Pattern pattern = 
      Pattern.compile(
        "(([+-]\\d{2})(\\d{2})?(\\d{2})?(\\.\\d+)?)(([+-]\\d{3})(\\d{2})?(\\d{2})?(\\.\\d+)?)([+-]\\d+(\\.\\d+)?)?");
    Matcher matcher = pattern.matcher(val);
    Integer latd = null, latm = null, lats = null, 
            lond = null, lonm = null, lons = null;
    Double latf = null, lonf = null, alt = null;
    if (matcher.find()) {
      if (matcher.group(1) != null) {
        String sdeg = matcher.group(2);
        String smin = matcher.group(3);
        String ssec = matcher.group(4);
        String sfrac = matcher.group(5);
        latd = sdeg != null ? 
            Integer.valueOf(
                sdeg.charAt(0) == '+'?sdeg.substring(1):sdeg) : null;
        latm = smin != null ? Integer.valueOf(smin) : null;
        lats = ssec != null ? Integer.valueOf(ssec) : null;
        latf = sfrac != null ? Double.valueOf(sfrac) : null;
      }
      if (matcher.group(6) != null) {
        String sdeg = matcher.group(7);
        String smin = matcher.group(8);
        String ssec = matcher.group(9);
        String sfrac = matcher.group(10);
        lond = sdeg != null ? 
            Integer.valueOf(
              sdeg.charAt(0) == '+'?sdeg.substring(1):sdeg) : null;
        lonm = smin != null ? Integer.valueOf(smin) : null;
        lons = ssec != null ? Integer.valueOf(ssec) : null;
        lonf = sfrac != null ? Double.valueOf(sfrac) : null;
      }
      if (matcher.group(11) != null) {
        String s= matcher.group(11);
        alt = s != null ? 
            Double.valueOf(
                s.charAt(0) == '+'?s.substring(1):s) : null;
      }
      return new IsoPosition(
        toDegrees(latd,latm,lats,latf),
        toDegrees(lond,lonm,lons,lonf),
        alt);
    } else {
      throw new IllegalArgumentException();
    }
  }
  
}