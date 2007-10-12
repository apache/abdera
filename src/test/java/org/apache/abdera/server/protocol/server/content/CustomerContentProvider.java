package org.apache.abdera.server.protocol.server.content;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.protocol.server.content.AbstractCollectionProvider;
import org.apache.abdera.protocol.server.content.ResponseContextException;
import sun.security.pkcs.ContentInfo;

public class CustomerContentProvider extends AbstractCollectionProvider<Customer> {
  private static final String ID_PREFIX = "urn:acme:customer:";
  
  private final static AtomicInteger nextId = new AtomicInteger(1000);
  private Map<Integer, Customer> customers = new HashMap<Integer, Customer>();
  private Factory factory = new Abdera().getFactory();
  
  public String getId() {
    return "tag:example.org,2006:feed";
  }

  public Customer createEntry(String title, String summary, Content content) {
    Customer customer = contentToCustomer(content);
    customers.put(customer.getId(), customer);
    
    return customer;
  }

  private Customer contentToCustomer(Content content) {
    Customer customer = new Customer();
    
    return contentToCustomer(content, customer);
  }

  private Customer contentToCustomer(Content content, Customer customer) {
    Element firstChild = content.getFirstChild();
    customer.setName(firstChild.getAttributeValue("name"));
    customer.setId(nextId.incrementAndGet());
    return customer;
  }

  public void deleteEntry(String resourceName) throws ResponseContextException {
    Integer id = getIdFromResourceName(resourceName);
    customers.remove(id);
  }

  public String getAuthor() {
    return "Acme Industries";
  }

  public Object getContent(Customer entry) {
    Content content = factory.newContent();
    Element customerEl = factory.newElement(new QName("customer"));
    customerEl.setAttributeValue(new QName("name"), entry.getName());
   
    content.setValueElement(customerEl);
    return content;
  }

  public Iterable<Customer> getEntries() {
    return customers.values();
  }

  public Customer getEntry(String resourceName) throws ResponseContextException {
    Integer id = getIdFromResourceName(resourceName);
    return customers.get(id);
  }

  private Integer getIdFromResourceName(String resourceName) throws ResponseContextException {
    int idx = resourceName.indexOf("-");
    if (idx == -1) {
      throw new ResponseContextException(404);
    }
    Integer id = new Integer(resourceName.substring(0, idx));
    return id;
  }

  public Customer getEntryFromId(String id) {
    return customers.get(new Integer(id));
  }

  public String getId(Customer entry) {
    // TODO: is this valid?
    return ID_PREFIX + entry.getId();
  }

  public String getName(Customer entry) {
    return entry.getId() + "-" + entry.getName().replaceAll(" ", "_");
  }

  public String getTitle() {
    return "Acme Customer Database";
  }

  public String getTitle(Customer entry) {
    return entry.getName();
  }

  public Date getUpdated(Customer entry) {
    return new Date();
  }

  public Customer updateEntry(Customer entry, Content content) {
    contentToCustomer(content, entry);
    
    return entry;
  }

}
