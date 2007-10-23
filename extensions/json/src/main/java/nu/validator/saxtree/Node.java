/*
 * Copyright (c) 2007 Henri Sivonen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */

package nu.validator.saxtree;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public abstract class Node implements Locator {

    private final String systemId;
    private final String publicId;
    private final int column;
    private final int line;
    
    private Node nextSibling = null;
    
    private ParentNode parentNode = null;

    Node(Locator locator) {
        this.systemId = locator.getSystemId();
        this.publicId = locator.getPublicId();
        this.column = locator.getColumnNumber();
        this.line = locator.getLineNumber();
    }
    
    public int getColumnNumber() {
        return column;
    }

    public int getLineNumber() {
        return line;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getSystemId() {
        return systemId;
    }

    abstract void visit(TreeParser treeParser) throws SAXException;
    
    void revisit(TreeParser treeParser) throws SAXException {
        return;
    }
    
    public Node getFirstChild() {
        return null;
    }

    /**
     * Returns the nextSibling.
     * 
     * @return the nextSibling
     */
    public final Node getNextSibling() {
        return nextSibling;
    }

    /**
     * Sets the nextSibling.
     * 
     * @param nextSibling the nextSibling to set
     */
    void setNextSibling(Node nextSibling) {
        this.nextSibling = nextSibling;
    }
    
    
    /**
     * Returns the parentNode.
     * 
     * @return the parentNode
     */
    public final ParentNode getParentNode() {
        return parentNode;
    }
    
    public abstract NodeType getNodeType();
    
    // Subclass-specific accessors that are hoisted here to 
    // avoid casting.

    /**
     * Sets the parentNode.
     * 
     * @param parentNode the parentNode to set
     */
    void setParentNode(ParentNode parentNode) {
        this.parentNode = parentNode;
    }
    
    public void detach() {
        if (parentNode != null) {
            parentNode.removeChild(this);
            parentNode = null;
        }
    }
    
    /**
     * Returns the name.
     * 
     * @return the name
     */
    public String getName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the publicIdentifier.
     * 
     * @return the publicIdentifier
     */
    public String getPublicIdentifier() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the systemIdentifier.
     * 
     * @return the systemIdentifier
     */
    public String getSystemIdentifier() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the attributes.
     * 
     * @return the attributes
     */
    public Attributes getAttributes() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the localName.
     * 
     * @return the localName
     */
    public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the prefixMappings.
     * 
     * @return the prefixMappings
     */
    public List<PrefixMapping> getPrefixMappings() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the qName.
     * 
     * @return the qName
     */
    public String getQName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the uri.
     * 
     * @return the uri
     */
    public String getUri() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the data.
     * 
     * @return the data
     */
    public String getData() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the target.
     * 
     * @return the target
     */
    public String getTarget() {
        throw new UnsupportedOperationException();
    }
}
