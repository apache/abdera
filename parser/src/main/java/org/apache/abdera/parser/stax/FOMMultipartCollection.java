package org.apache.abdera.parser.stax;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMMultipartCollection extends FOMCollection {

    protected FOMMultipartCollection(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    protected FOMMultipartCollection(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMMultipartCollection(OMContainer parent, OMFactory factory) {
        super(COLLECTION, parent, factory);
    }

    public boolean acceptsMultipart(String mediaType) {
        Map<String, String> accept = getAcceptMultiparted();
        if (accept.size() == 0)
            accept = Collections.singletonMap("application/atom+xml;type=entry", null);
        for (Map.Entry<String, String> entry : accept.entrySet()) {
            if (MimeTypeHelper.isMatch(entry.getKey(), mediaType) && entry.getValue() != null
                && entry.getValue().equals(LN_ALTERNATE_MULTIPART_RELATED))
                return true;
        }
        return false;
    }

    public boolean acceptsMultipart(MimeType mediaType) {
        return accepts(mediaType.toString());
    }

    public Map<String, String> getAcceptMultiparted() {
        Map<String, String> accept = new HashMap<String, String>();
        Iterator<?> i = getChildrenWithName(ACCEPT);
        if (i == null || !i.hasNext())
            i = getChildrenWithName(PRE_RFC_ACCEPT);
        while (i.hasNext()) {
            Element e = (Element)i.next();
            String t = e.getText();
            if (t != null) {
                if (e.getAttributeValue(ALTERNATE) != null && e.getAttributeValue(ALTERNATE).trim().length() > 0) {
                    accept.put(t.trim(), e.getAttributeValue(ALTERNATE));
                } else {
                    accept.put(t.trim(), null);
                }
            }
        }
        return accept;
    }

    public Collection setAccept(String mediaRange, String alternate) {
        return setAccept(Collections.singletonMap(mediaRange, alternate));
    }

    public Collection setAccept(Map<String, String> mediaRanges) {
        complete();
        if (mediaRanges != null && mediaRanges.size() > 0) {
            _removeChildren(ACCEPT, true);
            _removeChildren(PRE_RFC_ACCEPT, true);
            if (mediaRanges.size() == 1 && mediaRanges.keySet().iterator().next().equals("")) {
                addExtension(ACCEPT);
            } else {
                for (Map.Entry<String, String> entry : mediaRanges.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("entry")) {
                        addSimpleExtension(ACCEPT, "application/atom+xml;type=entry");
                    } else {
                        try {
                            Element accept = addSimpleExtension(ACCEPT, new MimeType(entry.getKey()).toString());
                            if (entry.getValue() != null) {
                                accept.setAttributeValue(ALTERNATE, entry.getValue());
                            }
                        } catch (javax.activation.MimeTypeParseException e) {
                            throw new org.apache.abdera.util.MimeTypeParseException(e);
                        }
                    }
                }
            }
        } else {
            _removeChildren(ACCEPT, true);
            _removeChildren(PRE_RFC_ACCEPT, true);
        }
        return this;
    }

    public Collection addAccepts(String mediaRange, String alternate) {
        return addAccepts(Collections.singletonMap(mediaRange, alternate));
    }

    public Collection addAccepts(Map<String, String> mediaRanges) {
        complete();
        if (mediaRanges != null) {
            for (Map.Entry<String, String> entry : mediaRanges.entrySet()) {
                if (!accepts(entry.getKey())) {
                    try {
                        Element accept = addSimpleExtension(ACCEPT, new MimeType(entry.getKey()).toString());
                        if (entry.getValue() != null) {
                            accept.setAttributeValue(ALTERNATE, entry.getValue());
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        return this;
    }
}
