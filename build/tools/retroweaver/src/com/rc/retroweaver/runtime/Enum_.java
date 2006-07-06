package com.rc.retroweaver.runtime;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A version of the 1.5 java.lang.Enum class for the 1.4 VM.
 */
public class Enum_<E extends Enum_<E>> implements Comparable<E>, Serializable {

	private final transient int ordinal;

	private final String name;

	private static final Map<Class, Object[]> enumValues = new HashMap<Class, Object[]>();

	protected Enum_(String name, int ordinal) {
		this.name = name;
		this.ordinal = ordinal;
	}

	protected static final void setEnumValues(Object[] values, Class c) {
		synchronized(enumValues) {
			enumValues.put(c, values);
		}
	}

	protected static final <T> T[] getEnumValues(Class<T> class_) {
		synchronized(enumValues) {
			T[] values = (T[]) enumValues.get(class_);
			if (values != null)
				return values;
		}

		if (!class_.isEnum())
			return null;

		// force initialization of class_ as
		// class loader may not have called static initializers yet
		try {
			Class.forName(class_.getName(), true, class_.getClassLoader());
		} catch (ClassNotFoundException e) {
			// can not happen: class_ has already been resolved.
		}
	
		synchronized(enumValues) {
			return (T[]) enumValues.get(class_);
		}
	}

	/**
	 * Implement serialization so we can get the singleton behavior we're
	 * looking for in enums.
	 */
	protected Object readResolve() throws ObjectStreamException {
		/*
		 * The implementation is based on "Java Object Serialization Specification",
		 * revision 1.5.0:
		 * 
		 * only the name is saved, serialVersionUID is 0L for all enum types
		 * InvalidObjectException is raised if valueOf() raises IllegalArgumentException.
		 * 
		 */

		Class<E> c = getDeclaringClass();
		/*
		 * Note: getClass() would not work for enum inner classes
		 * such as CMYK.Cyan in the test suite.
		 */
		try {
			return valueOf(c, name);
		} catch (IllegalArgumentException iae) {
			InvalidObjectException ioe = new InvalidObjectException(name + " is not a valid enum for " + c.getName());
			try {
				ioe.initCause(iae);
			} catch (NoSuchMethodError nsm) {
				// cause should be set according to the spec but it's only available in 1.4
			}
				
			throw ioe;
		}
	}

	public static <T extends Enum_<T>> T valueOf(Class<T> enumType, String name) {

		if (enumType == null) {
			throw new NullPointerException("enumType is null");
		}

		if (name == null) {
			throw new NullPointerException("name is null");
		}

		T[] enums = getEnumValues(enumType);

		if (enums != null) {
			for (T enum_ : enums) {
				if (enum_.name.equals(name)) {
					return enum_;
				}
			}
		}

		throw new IllegalArgumentException("No enum const " + enumType + "."
				+ name);
	}

	public final boolean equals(Object other) {
		return other == this;
	}

	public final int hashCode() {
		return System.identityHashCode(this);
	}

	public String toString() {
		return name;
	}

	public final int compareTo(E e) {
		Class c1 = getDeclaringClass();
		Class c2 = e.getDeclaringClass();

		if (c1 == c2) {
			return ordinal - e.ordinal;
		}

		throw new ClassCastException();
	}

	protected final Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public final String name() {
		return name;
	}

	public final int ordinal() {
		return ordinal;
	}

	public final Class<E> getDeclaringClass() {
		Class clazz = getClass();
		Class superClass = clazz.getSuperclass();
		if (superClass != Enum_.class) {
			return superClass;
		} else {
			return clazz;
		}
	}

}
