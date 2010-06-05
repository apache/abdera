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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.parser.stax.util.ResolveFunction;
import org.apache.abdera.util.AbstractXPath;
import org.apache.abdera.xpath.XPathException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.xpath.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.Function;
import org.jaxen.FunctionContext;
import org.jaxen.JaxenException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.SimpleVariableContext;
import org.jaxen.VariableContext;
import org.jaxen.XPath;

@SuppressWarnings("unchecked")
public class FOMXPath extends AbstractXPath {

    private final Map<QName, Function> functions;
    private final Map<QName, Object> variables;

    public FOMXPath(Abdera abdera) {
        this(null, null, null);
    }

    protected FOMXPath(Map<String, String> defaultNamespaces) {
        this(defaultNamespaces, null, null);
    }

    protected FOMXPath(Map<String, String> defaultNamespaces,
                       Map<QName, Function> defaultFunctions,
                       Map<QName, Object> defaultVariables) {
        super(defaultNamespaces);
        functions = (defaultFunctions != null) ? defaultFunctions : initDefaultFunctions();
        variables = (defaultVariables != null) ? defaultVariables : initDefaultVariables();
    }

    protected Map<String, String> initDefaultNamespaces() {
        Map<String, String> namespaces = super.initDefaultNamespaces();
        namespaces.put("abdera", "http://abdera.apache.org");
        return namespaces;
    }

    private Map<QName, Function> initDefaultFunctions() {
        Map<QName, Function> functions = new HashMap<QName, Function>();
        functions.put(ResolveFunction.QNAME, new ResolveFunction());
        return functions;
    }

    private Map<QName, Object> initDefaultVariables() {
        Map<QName, Object> variables = new HashMap<QName, Object>();
        return variables;
    }

    public static XPath getXPath(String path) throws JaxenException {
        return getXPath(path, null);
    }

    private static FunctionContext getFunctionContext(Map<QName, Function> functions, SimpleFunctionContext context) {
        if (context == null)
            context = new SimpleFunctionContext();
        for (QName qname : functions.keySet()) {
            Function function = functions.get(qname);
            context.registerFunction(qname.getNamespaceURI(), qname.getLocalPart(), function);
        }
        return context;
    }

    private static VariableContext getVariableContext(Map<QName, Object> variables, SimpleVariableContext context) {
        if (context == null)
            context = new SimpleVariableContext();
        for (QName qname : variables.keySet()) {
            Object value = variables.get(qname);
            context.setVariableValue(qname.getNamespaceURI(), qname.getLocalPart(), value);
        }
        return context;
    }

    public static XPath getXPath(String path,
                                 Map<String, String> namespaces,
                                 Map<QName, Function> functions,
                                 Map<QName, Object> variables) throws JaxenException {
        DocumentNavigator nav = new DocumentNavigator();
        XPath contextpath = new BaseXPath(path, nav);
        if (namespaces != null) {
            for (Map.Entry<String, String> entry : namespaces.entrySet()) {
                contextpath.addNamespace(entry.getKey(), entry.getValue());
            }
        }
        if (functions != null) {
            contextpath.setFunctionContext(getFunctionContext(functions, (SimpleFunctionContext)contextpath
                .getFunctionContext()));
        }
        if (variables != null)
            contextpath.setVariableContext(getVariableContext(variables, (SimpleVariableContext)contextpath
                .getVariableContext()));
        return contextpath;
    }

    public static XPath getXPath(String path, Map<String, String> namespaces) throws JaxenException {
        return getXPath(path, namespaces, null, null);
    }

    public List selectNodes(String path,
                            Base base,
                            Map<String, String> namespaces,
                            Map<QName, Function> functions,
                            Map<QName, Object> variables) throws XPathException {
        try {
            base = getElementWrapped(base);
            List nodes = new ArrayList();
            XPath xpath = getXPath(path, namespaces, functions, variables);
            List results = xpath.selectNodes(base);
            for (Object obj : results) {
                if (obj instanceof OMAttribute) {
                    nodes.add(new FOMAttribute((OMAttribute)obj));
                } else {
                    nodes.add(obj);
                }
            }
            return nodes;
        } catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    public List selectNodes(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return selectNodes(path, base, namespaces, functions, variables);
    }

    public Object selectSingleNode(String path,
                                   Base base,
                                   Map<String, String> namespaces,
                                   Map<QName, Function> functions,
                                   Map<QName, Object> variables) throws XPathException {
        try {
            base = getElementWrapped(base);
            XPath xpath = getXPath(path, namespaces, functions, variables);
            Object obj = xpath.selectSingleNode(base);
            if (obj instanceof OMAttribute)
                obj = new FOMAttribute((OMAttribute)obj);
            return obj;
        } catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    public Object selectSingleNode(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return selectSingleNode(path, base, namespaces, functions, variables);
    }

    public Object evaluate(String path,
                           Base base,
                           Map<String, String> namespaces,
                           Map<QName, Function> functions,
                           Map<QName, Object> variables) throws XPathException {
        try {
            base = getElementWrapped(base);
            XPath xpath = getXPath(path, namespaces, functions, variables);
            return xpath.evaluate(base);
        } catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    public Object evaluate(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return evaluate(path, base, namespaces, functions, variables);
    }

    public String valueOf(String path,
                          Base base,
                          Map<String, String> namespaces,
                          Map<QName, Function> functions,
                          Map<QName, Object> variables) throws XPathException {
        try {
            base = getElementWrapped(base);
            XPath xpath = getXPath(path, namespaces, functions, variables);
            return xpath.stringValueOf(base);
        } catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    public String valueOf(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return valueOf(path, base, namespaces, functions, variables);
    }

    public boolean booleanValueOf(String path,
                                  Base base,
                                  Map<String, String> namespaces,
                                  Map<QName, Function> functions,
                                  Map<QName, Object> variables) throws XPathException {
        try {
            base = getElementWrapped(base);
            XPath xpath = getXPath(path, namespaces, functions, variables);
            return xpath.booleanValueOf(base);
        } catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    public boolean booleanValueOf(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return booleanValueOf(path, base, namespaces, functions, variables);
    }

    public Number numericValueOf(String path,
                                 Base base,
                                 Map<String, String> namespaces,
                                 Map<QName, Function> functions,
                                 Map<QName, Object> variables) throws XPathException {
        try {
            base = getElementWrapped(base);
            XPath xpath = getXPath(path, namespaces, functions, variables);
            return xpath.numberValueOf(base);
        } catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    public Number numericValueOf(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return numericValueOf(path, base, namespaces, functions, variables);
    }

    public Map<QName, Function> getDefaultFunctions() {
        return new HashMap<QName, Function>(functions);
    }

    public synchronized void setDefaultFunctions(Map<QName, Function> functions) {
        this.functions.clear();
        this.functions.putAll(functions);
    }

    public Map<QName, Object> getDefaultVariables() {
        return new HashMap<QName, Object>(variables);
    }

    public synchronized void setDefaultVariables(Map<QName, Object> variables) {
        this.variables.clear();
        this.variables.putAll(variables);
    }

    private Base getElementWrapped(Base base) {
        if (base instanceof ElementWrapper) {
            base = ((ElementWrapper)base).getInternal();
        }
        return base;
    }
}
