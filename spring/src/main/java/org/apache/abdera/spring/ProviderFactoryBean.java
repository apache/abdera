package org.apache.abdera.spring;

import java.lang.reflect.Constructor;
import java.util.Collection;

import javax.security.auth.Subject;

import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.springframework.beans.factory.FactoryBean;

@SuppressWarnings("unchecked")
public class ProviderFactoryBean implements FactoryBean {
    private Class<? extends DefaultProvider> providerClass = DefaultProvider.class;
    private String base;
    private Collection<WorkspaceInfo> workspaces;
    private Resolver<Target> targetResolver;
    private Resolver<Subject> subjectResolver;
    private Filter[] filters;
    
    public Object getObject() throws Exception {
        DefaultProvider p = null;
        
        if (base != null) {
            Constructor<? extends DefaultProvider> constructor = 
              providerClass.getConstructor(String.class);
            p = constructor.newInstance(base);
        } else {
            p = providerClass.newInstance();
        }
        
        if (workspaces != null && workspaces.size() > 0) {
            p.addWorkspaces(workspaces);
        }
        if (targetResolver != null) {
            p.setTargetResolver(targetResolver);
        }
        
        if (subjectResolver != null) {
            p.setSubjectResolver(subjectResolver);
        }
        if (filters != null && filters.length > 0) {
            p.addFilter(filters);
        }
        
        return p;
    }

    public Class getObjectType() {
        return providerClass;
    }

    public boolean isSingleton() {
        return true;
    }

    public Class getProviderClass() {
        return providerClass;
    }

    public void setProviderClass(Class providerClass) {
        this.providerClass = providerClass;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Collection<WorkspaceInfo> getWorkspaces() {
        return workspaces;
    }

    public void setWorkspaces(Collection<WorkspaceInfo> workspaces) {
        this.workspaces = workspaces;
    }

    public Resolver<Target> getTargetResolver() {
        return targetResolver;
    }

    public void setTargetResolver(Resolver<Target> targetResolver) {
        this.targetResolver = targetResolver;
    }

    public Resolver<Subject> getSubjectResolver() {
        return subjectResolver;
    }

    public void setSubjectResolver(Resolver<Subject> subjectResolver) {
        this.subjectResolver = subjectResolver;
    }

    public Filter[] getFilters() {
        return filters;
    }

    public void setFilters(Filter[] filters) {
        this.filters = filters;
    }

}
