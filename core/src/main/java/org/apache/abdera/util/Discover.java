package org.apache.abdera.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public final class Discover {

    private Discover() {
    }

    public static <T> T locate(String id, String defaultImpl, Object... args) {
        return (T)locate(id, defaultImpl, getLoader(), args);
    }

    public static <T> T locate(String id, String defaultImpl, ClassLoader loader, Object... args) {
        try {
            T instance = null;
            Iterable<T> items = locate(id, loader, args);
            for (T i : items) {
                instance = i;
                break;
            }
            if (instance == null) {
                instance = (T)load(loader, defaultImpl, false, args);
            }
            return instance;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static ClassLoader getLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static <T> Iterable<T> locate(String id, ClassLoader cl, Object... args) {
        return locate(id, false, cl, args);
    }

    public static <T> Iterable<T> locate(String id, boolean classesonly, ClassLoader cl, Object... args) {
        return locate(id, new DefaultLoader<T>(id, classesonly, args, cl));
    }

    public static <T> Iterable<T> locate(String id, Object... args) {
        return locate(id, false, args);
    }

    public static <T> Iterable<T> locate(String id, boolean classesonly) {
        return locate(id, new DefaultLoader<T>(id, classesonly, null));
    }
    
    public static <T> Iterable<T> locate(String id, boolean classesonly, Object... args) {
        return locate(id, new DefaultLoader<T>(id, classesonly, args));
    }

    public static <T> Iterable<T> locate(String id, Iterable<T> loader) {
        List<T> impls = Collections.synchronizedList(new ArrayList<T>());
        try {
            for (T instance : loader) {
                if (instance != null)
                    impls.add(instance);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return impls;
    }

    public static class DefaultLoader<T> implements Iterable<T> {
        protected final ClassLoader loader;
        protected final String id;
        protected final Iterator<T> iterator;
        protected final Object[] args;

        public DefaultLoader(String id, boolean classesonly, Object[] args) {
            this(id, classesonly, args, getLoader());
        }

        public DefaultLoader(String id, boolean classesonly, Object[] args, ClassLoader loader) {
            this.loader = loader != null ? loader : getLoader();
            this.id = id;
            this.iterator = init(classesonly);
            this.args = args;
        }

        private Iterator<T> init(boolean classesonly) {
            try {
                List<Iterator<T>> list = new ArrayList<Iterator<T>>();
                Enumeration<URL> e = locateResources("META-INF/services/" + id, //$NON-NLS-1$ 
                                                     loader,
                                                     Discover.class);
                while (e.hasMoreElements()) {
                    Iterator<T> i =
                        new DefaultLoaderIterator<T>(loader, e.nextElement().openStream(), classesonly, args);
                    list.add(i);
                }
                return new MultiIterator<T>(list);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        public Iterator<T> iterator() {
            return iterator;
        }
    }

    public static class DefaultLoaderIterator<T> extends LineReaderLoaderIterator<T> {
        public DefaultLoaderIterator(ClassLoader cl, InputStream in, boolean classesonly, Object[] args) {
            super(cl, in, classesonly, args);
        }

        public T next() {
            try {
                if (!hasNext())
                    return null;
                return create(read(), args);
            } catch (Throwable t) {
                return null;
            }
        }

        protected T create(String spec, Object[] args) {
            try {
                return (T)load(cl, spec, classesonly, args);
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static <T> T load(ClassLoader loader, String spec, boolean classesonly, Object[] args) throws Exception {
        if (classesonly) {
            return (T)getClass(loader, spec);
        } else {
            Class<T> _class = getClass(loader, spec);
            Class<?>[] types = new Class<?>[args != null ? args.length : 0];
            if (args != null) {
                for (int n = 0; n < args.length; n++) {
                    types[n] = args[n].getClass();
                }
                return _class.getConstructor(types).newInstance(args);
            } else {
                return _class.newInstance();
            }
        }
    }

    private static <T> Class<T> getClass(ClassLoader loader, String spec) {
        Class<T> c = null;
        try {
            c = (Class<T>)loader.loadClass(spec);
        } catch (ClassNotFoundException e) {
            try {
                // try loading the class from the Discover class loader
                // if the loader failed.
                c = (Class<T>)Discover.class.getClassLoader().loadClass(spec);
            } catch (ClassNotFoundException e1) {
                // throw the original exception
                throw new RuntimeException(e);
            }
        }
        return c;
    }

    public static abstract class LineReaderLoaderIterator<T> extends LoaderIterator<T> {
        private BufferedReader buf = null;
        private String line = null;
        protected final Object[] args;
        protected final boolean classesonly;

        protected LineReaderLoaderIterator(ClassLoader cl, InputStream in, boolean classesonly, Object[] args) {
            super(cl);
            this.args = args;
            this.classesonly = classesonly;
            try {
                InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                buf = new BufferedReader(reader);
                line = readNext();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        public boolean hasNext() {
            return line != null;
        }

        protected String readNext() {
            try {
                String line = null;
                while ((line = buf.readLine()) != null) {
                    line = line.trim();
                    if (!line.startsWith("#"))break; //$NON-NLS-1$
                }
                return line;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        protected String read() {
            String val = line;
            line = readNext();
            return val;
        }
    }

    public static abstract class LoaderIterator<T> implements Iterator<T> {
        protected final ClassLoader cl;

        protected LoaderIterator(ClassLoader cl) {
            this.cl = cl;
        }

        public void remove() {
        }
    }

    public static URL locateResource(String id, ClassLoader loader, Class<?> callingClass) {
        URL url = loader.getResource(id);
        if (url == null && id.startsWith("/"))
            url = loader.getResource(id.substring(1));
        if (url == null)
            url = locateResource(id, Discover.class.getClassLoader(), callingClass);
        if (url == null && callingClass != null)
            url = locateResource(id, callingClass.getClassLoader(), null);
        if (url == null) {
            url = callingClass.getResource(id);
        }
        if ((url == null) && id.startsWith("/")) {
            url = callingClass.getResource(id.substring(1));
        }
        return url;
    }

    public static Enumeration<URL> locateResources(String id, ClassLoader loader, Class<?> callingClass)
        throws IOException {
        Enumeration<URL> urls = loader.getResources(id);
        if (urls == null && id.startsWith("/"))
            urls = loader.getResources(id.substring(1));
        if (urls == null)
            urls = locateResources(id, Discover.class.getClassLoader(), callingClass);
        if (urls == null)
            urls = locateResources(id, callingClass.getClassLoader(), callingClass);
        return urls;
    }

    public static InputStream locateResourceAsStream(String resourceName, ClassLoader loader, Class<?> callingClass) {
        URL url = locateResource(resourceName, loader, callingClass);
        try {
            return (url != null) ? url.openStream() : null;
        } catch (IOException e) {
            return null;
        }
    }
}
