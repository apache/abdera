package org.apache.abdera.parser.stax;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Total;
import org.apache.abdera.util.Constants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMTotal extends FOMExtensionElement implements Total {

  private static final long serialVersionUID = 241599118592917827L;

  public FOMTotal(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMTotal(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
  }
  
  public FOMTotal( 
    OMContainer parent, 
    OMFactory factory)
      throws OMException {
    super(Constants.THRTOTAL, parent, factory);
  }

  public FOMTotal(
    QName qname, 
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(qname, parent, factory, builder);
  }
  
  public int getValue() {
    return Integer.parseInt(getText());
  }

  public void setValue(int value) {
    setText(String.valueOf(value));
  }

}
