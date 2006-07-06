package com.rc.retroweaver.runtime;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

/**
 * Replacements for methods added to java.lang.Iterable in Java 1.5, used
 * for targets of the "foreach" statement.
 */
public final class IterableMethods {

	private IterableMethods() {
	}

	/**
	 * Returns an iterator for <code>iterable</code>.
	 * 
	 * @param iterable  the object to get the Iterator from
	 * @return an Iterator.
	 * @throws UnsupportedOperationException if an iterator method can not be found.
	 * @throws NullPointerException if <code>iterable</code> is null.
	 */
	public static Iterator iterator(Object iterable) {
		if (iterable == null) {
			throw new NullPointerException();
		}

		if (iterable instanceof Collection) {
			// core jdk classes implementing Iterable: they are not weaved but,
			// at least in 1.5, they all implement Collection and as its iterator
			// method exits in pre 1.5 jdks, a valid Iterator can be returned.
			return ((Collection) iterable).iterator();
		}

		if (iterable instanceof Iterable_) {
			// weaved classes inheriting from Iterable
			return ((Iterable_) iterable).iterator();
		}

		// for future jdk Iterable classes not inheriting from Collection
		// use reflection to try to get the iterator if it was present pre 1.5
		try {
			Method m = iterable.getClass().getMethod("iterator", (Class[]) null);
			if (m != null)
				return (Iterator) m.invoke(iterable, (Object[]) null);
		} catch (Exception ignored) {
		}

		throw new UnsupportedOperationException("iterator call on " + iterable.getClass());
	}

}
