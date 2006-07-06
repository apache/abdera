package com.rc.retroweaver.runtime;

import java.util.Iterator;

/**
 * A version of the 1.5 java.lang.Iterable class for the 1.4 VM.
 */
public interface Iterable_<E> {
	Iterator<E> iterator();
}
