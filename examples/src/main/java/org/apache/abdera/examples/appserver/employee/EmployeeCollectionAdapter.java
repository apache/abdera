package org.apache.abdera.examples.appserver.employee;

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
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;

public class EmployeeCollectionAdapter extends AbstractEntityCollectionAdapter<Employee> {
  private static final String ID_PREFIX = "tag:acme.com,2007:employee:entry:";
  
  private AtomicInteger nextId = new AtomicInteger(1000);
  private Map<Integer, Employee> employees = new HashMap<Integer, Employee>();
  private Factory factory = new Abdera().getFactory();
  
  /**
   * A unique ID for this feed.
   */
  public String getId(RequestContext request) {
    return "tag:acme.com,2007:employee:feed";
  }

  /**
   * The title of our collection.
   */
  public String getTitle(RequestContext request) {
    return "Acme Employee Database";
  }

  /**
   * The author of this collection.
   */
  public String getAuthor(RequestContext request) {
    return "Acme Industries";
  }
  
  @Override
  public Employee postEntry(String title, IRI id, String summary, 
                            Date updated, List<Person> authors,
                            Content content, RequestContext request) throws ResponseContextException {   
    Employee employee = contentToCustomer(content);
    employees.put(employee.getId(), employee);
    
    return employee;
  }

  private Employee contentToCustomer(Content content) {
    Employee employee = new Employee();
    
    return contentToCustomer(content, employee);
  }

  private Employee contentToCustomer(Content content, Employee employee) {
    Element firstChild = content.getFirstChild();
    employee.setName(firstChild.getAttributeValue("name"));
    employee.setId(nextId.incrementAndGet());
    return employee;
  }

  public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
    Integer id = getIdFromResourceName(resourceName);
    employees.remove(id);
  }

  @Override
  public List<Person> getAuthors(Employee entry, RequestContext request) throws ResponseContextException {
    Person author = request.getAbdera().getFactory().newAuthor();
    author.setName("Acme Industries");
    return Arrays.asList(author);
  }

  public Object getContent(Employee entry, RequestContext request) {
    Content content = factory.newContent();
    Element employeeEl = factory.newElement(new QName("employee"));
    employeeEl.setAttributeValue(new QName("name"), entry.getName());
   
    content.setValueElement(employeeEl);
    return content;
  }

  public Iterable<Employee> getEntries(RequestContext request) {
    return employees.values();
  }

  public Employee getEntry(String resourceName, RequestContext request) throws ResponseContextException {
    Integer id = getIdFromResourceName(resourceName);
    return employees.get(id);
  }

  private Integer getIdFromResourceName(String resourceName) throws ResponseContextException {
    int idx = resourceName.indexOf("-");
    if (idx == -1) {
      throw new ResponseContextException(404);
    }
    return new Integer(resourceName.substring(0, idx));
  }

  public String getId(Employee entry) {
    return ID_PREFIX + entry.getId();
  }

  public String getName(Employee entry) {
    return entry.getId() + "-" + entry.getName().replaceAll(" ", "_");
  }
  public String getTitle(Employee entry) {
    return entry.getName();
  }

  public Date getUpdated(Employee entry) {
    return entry.getUpdated();
  }

  @Override
  public void putEntry(Employee entry, String title, Date updated, 
                       List<Person> authors, String summary,
                       Content content, RequestContext request) throws ResponseContextException {
    contentToCustomer(content, entry);
  }

}
