package org.apache.abdera2.activities.extra;


import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.IO;

public class Extra {

  @SuppressWarnings("unchecked")
  public static void initExtras(IO io) {

    io.addObjectMapping(
      BookObject.class,
      MovieObject.class,
      OfferObject.class,
      TvEpisodeObject.class,
      TvSeasonObject.class,
      TvSeriesObject.class);
  }
  
  /**
   * Special AS Object that represents the authenticated user
   */
  public static ASObject SELF() {
    return new ASObject("@self");
  }
  
  /**
   * Special AS Object that represents the authenticated user.
   * synonymous with @self
   */
  public static ASObject ME() {
    return new ASObject("@me");
  }
  
  /**
   * Special AS Object that represents the authenticated users 
   * collection of direct contacts
   */
  public static ASObject FRIENDS() {
    return new ASObject("@friends");
  }
  
  /**
   * Special AS Object that represents a subset of the authenticated users
   * collection of direct contacts
   */
  public static ASObject FRIENDS(String id) {
    ASObject obj = FRIENDS();
    obj.setId(id);
    return obj;
  }
  
  /**
   * Special AS Object that represents the authenticated users collection
   * of extended contacts (e.g. friends of friends)
   */
  public static ASObject NETWORK() {
    return new ASObject("@network");
  }
  
  /**
   * Special AS Object that represents everyone. synonymous with @public
   */
  public static ASObject ALL() {
    return new ASObject("@all");
  }
  
  /**
   * Special AS Object that represents everyone
   */
  public static ASObject PUBLIC() {
    return new ASObject("@public");
  }
  
  public static ASObject anonymousObject(String id) {
    ASObject obj = new ASObject();
    obj.setObjectType(null);
    obj.setId(id);
    return obj;
  }
  
  public static ASObject DISCONTINUED() {
    return anonymousObject("discontinued");
  }
  
  public static ASObject INSTOCK() {
    return anonymousObject("in-stock");
  }
  
  public static ASObject INSTOREONLY() {
    return anonymousObject("in-store-only");
  }
  
  public static ASObject ONLINEONLY() {
    return anonymousObject("online-only");
  }
  
  public static ASObject OUTOFSTOCK() {
    return anonymousObject("out-of-stock");
  }
  
  public static ASObject PREORDER() {
    return anonymousObject("pre-order");
  }
  
  public static ASObject EBOOK() {
    return anonymousObject("ebook");
  }
  
  public static ASObject HARDCOVER() {
    return anonymousObject("hardcover");
  }
  
  public static ASObject PAPERBACK() {
    return anonymousObject("paperback");
  }
  
  public static ASObject DAMAGED() {
    return anonymousObject("damaged");
  }
  
  public static ASObject NEW() {
    return anonymousObject("new");
  }
  
  public static ASObject REFURBISHED() {
    return anonymousObject("refurbished");
  }
  
  public static ASObject USED() {
    return anonymousObject("used");
  }
  
}
