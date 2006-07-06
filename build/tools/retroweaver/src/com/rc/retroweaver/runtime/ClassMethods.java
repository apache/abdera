package com.rc.retroweaver.runtime;

/**
 * Replacements for methods added to java.lang.Class in Java 1.5.
 */
public final class ClassMethods {

	/**
	 * Replacement for Class.asSubclass(Class).
	 * 
	 * @param c a Class
	 * @param superclass another Class which must be a superclass of <i>c</i>
	 * @return <i>c</i>
	 * @throws java.lang.ClassCastException if <i>c</i> is
	 */
	public static Class asSubclass(Class<?> c, Class<?> superclass) {
		if (!superclass.isAssignableFrom(c)) {
			throw new ClassCastException(superclass.getName());
		}
		return c;
	}

	/**
	 * Replacement for Class.cast(Object). Throws a ClassCastException if <i>obj</i>
	 * is not an instance of class <var>c</var>, or a subtype of <var>c</var>.
	 * 
	 * @param c Class we want to cast <var>obj</var> to
	 * @param object object we want to cast
	 * @return The object, or <code>null</code> if the object is
	 * <code>null</code>.
	 * @throws java.lang.ClassCastException if <var>obj</var> is not
	 * <code>null</code> or an instance of <var>c</var>
	 */
	public static Object cast(Class c, Object object) {
		if (object == null || c.isInstance(object)) {
			return object;
		} else {
			throw new ClassCastException(c.getName());
		}
	}

	/**
	 * Replacement for Class.isEnum().
	 * 
	 * @param class_ class we want to test.
	 * @return true if the class was declared as an Enum.
	 */
	public static <T> boolean isEnum(Class<T> class_) {
		Class c = class_.getSuperclass();

		if (c == null) {
			return false;
		}

		return Enum_.class.isAssignableFrom(c);
	}

	/**
	 * Replacement for Class.getEnumConstants().
	 * 
	 * @param class_ class we want to get Enum constants for.
	 * @return The elements of this enum class or null if this does not represent an enum type.
	 */
	public static <T> T[] getEnumConstants(Class<T> class_) {
		if (!isEnum(class_)) {
			return null;
		}

		return Enum_.getEnumValues(class_).clone();
	}

}
