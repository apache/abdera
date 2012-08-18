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
package org.apache.abdera.parser.stax;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.ParserOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

/**
 * {@link XMLStreamReader} wrapper that implements the various filters and transformations that can
 * be configured using {@link ParserFilter}.
 * <p>
 * The design of Apache Axiom is based on the assumption that no filtering or transformation is done
 * inside the builder. Among other things, this assumption ensures that
 * {@link OMContainer#getXMLStreamReaderWithoutCaching()} produces consistent results. One may argue
 * that for Abdera this is less important because
 * {@link OMContainer#getXMLStreamReaderWithoutCaching()} is not exposed by the Abdera API. However,
 * attempting to do filtering and transformation in the builder results in strong coupling between
 * Abdera and Axiom because {@link FOMBuilder} would depend on the internal implementation details
 * of the Axiom builder. To avoid this we do all filtering/transformation upfront.
 */
class FOMStAXFilter extends XMLStreamReaderWrapper {
    private final ParserOptions parserOptions;
    private boolean ignoreWhitespace = false;
    private boolean ignoreComments = false;
    private boolean ignorePI = false;
    private int depthInSkipElement;
    private int altEventType;
    private QName altQName;
    private String altText;
    
    FOMStAXFilter(XMLStreamReader parent, ParserOptions parserOptions) {
        super(parent);
        this.parserOptions = parserOptions;
        if (parserOptions != null) {
            ParseFilter parseFilter = parserOptions.getParseFilter();
            if (parseFilter != null) {
                ignoreWhitespace = parseFilter.getIgnoreWhitespace();
                ignoreComments = parseFilter.getIgnoreComments();
                ignorePI = parseFilter.getIgnoreProcessingInstructions();
            }
        }
        resetEvent();
    }

    private void resetEvent() {
        altEventType = -1;
        altQName = null;
        altText = null;
    }
    
    private boolean isAcceptableToParse(QName qname, boolean attribute) {
        if (parserOptions == null)
            return true;
        ParseFilter filter = parserOptions.getParseFilter();
        return (filter != null) ? (!attribute) ? filter.acceptable(qname) : filter.acceptable(getName(), qname)
            : true;
    }

    private void translateQName() {
        if (parserOptions.isQNameAliasMappingEnabled()) {
            Map<QName,QName> map = parserOptions.getQNameAliasMap();
            if (map != null) {
                altQName = map.get(super.getName());
            }
        }
    }
    
    @Override
    public int next() throws XMLStreamException {
        resetEvent();
        while (true) {
            int eventType = super.next();
            if (depthInSkipElement > 0) {
                switch (eventType) {
                    case START_ELEMENT:
                        depthInSkipElement++;
                        break;
                    case END_ELEMENT:
                        depthInSkipElement--;
                        break;
                }
            } else {
                switch (eventType) {
                    case DTD:
                        // Current StAX cursor model implementations inconsistently handle DTDs.
                        // Woodstox, for instance, does not provide a means of getting to the complete
                        // doctype declaration (which is actually valid according to the spec, which
                        // is broken). The StAX reference impl returns the complete doctype declaration
                        // despite the fact that doing so is apparently against the spec. We can get
                        // to the complete declaration in Woodstox if we want to use their proprietary
                        // extension APIs. It's unclear how other Stax impls handle this. So.. for now,
                        // we're just going to ignore the DTD. The DTD will still be processed as far
                        // as entities are concerned, but we will not be able to reserialize the parsed
                        // document with the DTD. Since very few folks actually use DTD's in feeds
                        // right now (and we should likely be encouraging folks not to do so), this
                        // shouldn't be that big of a problem
                        continue;
                    case START_ELEMENT:
                        if (!isAcceptableToParse(getName(), false)) {
                            depthInSkipElement = 1;
                            continue;
                        }
                        translateQName();
                        break;
                    case END_ELEMENT:
                        translateQName();
                        break;
                    case SPACE:
                        if (ignoreWhitespace) {
                            continue;
                        }
                        break;
                    case COMMENT:
                        if (ignoreComments) {
                            continue;
                        }
                        break;
                    case PROCESSING_INSTRUCTION:
                        if (ignorePI) {
                            continue;
                        }
                        break;
                    case CHARACTERS:
                    case CDATA:
                        if (ignoreWhitespace && isWhiteSpace()) {
                            continue;
                        }
                        break;
                    case ENTITY_REFERENCE:
                        String val = parserOptions.resolveEntity(getLocalName());
                        if (val == null) {
                            throw new ParseException("Unresolved undeclared entity: " + getLocalName());
                        } else {
                            altEventType = CHARACTERS;
                            altText = val;
                        }
                        break;
                }
                return altEventType != -1 ? altEventType : eventType;
            }
        }
    }

    @Override
    public int getEventType() {
        return altEventType != -1 ? altEventType : super.getEventType();
    }

    @Override
    public String getText() {
        return altText != null ? altText : super.getText();
    }

    @Override
    public String getNamespaceURI() {
        return altQName != null ? altQName.getNamespaceURI() : super.getNamespaceURI();
    }

    @Override
    public String getLocalName() {
        return altQName != null ? altQName.getLocalPart() : super.getLocalName();
    }

    @Override
    public QName getName() {
        return altQName != null ? altQName : super.getName();
    }
}
