package org.apache.abdera.protocol.server;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionProvider;
import org.apache.abdera.protocol.server.impl.ResponseContextException;

public class CustomerCollectionProvider extends AbstractEntityCollectionProvider<Customer> {
  private static final String ID_PREFIX = "urn:acme:customer:";
  
  private final static AtomicInteger nextId = new AtomicInteger(1000);
  private Map<Integer, Customer> customers = new HashMap<Integer, Customer>();
  private Factory factory = new Abdera().getFactory();
  
  public String getId(RequestContext request) {
    return "tag:example.org,2007:feed";
  }

  @Override
  public Customer createEntry(String title, IRI id, 
                              String summary, 
                              Date updated, 
                              List<Person> authors,
                              Content content, RequestContext request) throws ResponseContextException {
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

  public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
    Integer id = getIdFromResourceName(resourceName);
    customers.remove(id);
  }

  public String getAuthor() {
    return "Acme Industries";
  }

  @Override
  public List<Person> getAuthors(Customer entry, RequestContext request) throws ResponseContextException {
    Person author = request.getAbdera().getFactory().newAuthor();
    author.setName("Acme Industries");
    return Arrays.asList(author);
  }

  @Override
  public void updateEntry(Customer entry, String title, Date updated, List<Person> authors, String summary,
                          Content content, RequestContext request) throws ResponseContextException {
    // TODO Auto-generated method stub
    
  }

  public Object getContent(Customer entry, RequestContext request) {
    Content content = factory.newContent();
    Element customerEl = factory.newElement(new QName("customer"));
    customerEl.setAttributeValue(new QName("name"), entry.getName());
   
    content.setValueElement(customerEl);
    return content;
  }

  public Iterable<Customer> getEntries(RequestContext request) {
    return customers.values();
  }

  public Customer getEntry(String resourceName, RequestContext request) throws ResponseContextException {
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

  public Customer getEntryFromId(String id, RequestContext request) {
    return customers.get(new Integer(id));
  }

  public String getId(Customer entry) {
    // TODO: is this valid?
    return ID_PREFIX + entry.getId();
  }

  public String getName(Customer entry) {
    return entry.getId() + "-" + entry.getName().replaceAll(" ", "_");
  }

  public String getTitle(RequestContext request) {
    return "Acme Customer Database";
  }

  public String getTitle(Customer entry) {
    return entry.getName();
  }

  public Date getUpdated(Customer entry) {
    return new Date();
  }

  public Customer updateEntry(Customer entry, String title, List<Person> authors, Content content, RequestContext request) {
    contentToCustomer(content, entry);
    
    return entry;
  }

}
