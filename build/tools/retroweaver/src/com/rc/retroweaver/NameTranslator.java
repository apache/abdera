package com.rc.retroweaver;

import java.util.HashMap;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

/**
 * Helper class for translating special class names into their retroweaver
 * runtime or concurrent backport equivalent.
 */

public class NameTranslator {

	private static final String BACKPORT_CONCURRENT_PREFIX = "edu/emory/mathcs/backport/";
	private static final String CONCURRENT_PREFIX = "java/util/concurrent/";

    private NameTranslator() {}

	private static HashMap<String, String> translations = new HashMap<String, String>();

    static {
		translations.put("java/lang/StringBuilder",
					"java/lang/StringBuffer");

		translations.put("java/lang/Iterable",
				"com/rc/retroweaver/runtime/Iterable_");
		translations.put("java/lang/Enum", "com/rc/retroweaver/runtime/Enum_");
	}

	/**
	 * Translate an id or a method signature into its retroweaver runtime or
	 * concurrent backport equivalent.
	 * 
	 * @param name The <code>String</code> to translate.
	 * @return the translated name.
	 */
	protected static String translate(String name) {
		if (name == null)
			return null;

		StringBuffer sb = new StringBuffer();
		translate(name, sb, 0, name.length());

		return sb.toString();
	}

	private static String translateName(String name) {
		if (name.startsWith(CONCURRENT_PREFIX))
			return BACKPORT_CONCURRENT_PREFIX + name;

		String newName = translations.get(name);

		if (newName != null)
			return newName;

		return name;
	}

	private static void translate(String in, StringBuffer out, int start, int end) {
		if (start >= end)
			return;

		char firstChar = in.charAt(start);

		switch (firstChar) {
		case 'Z': // boolean
		case 'B': // byte
		case 'C': // char
		case 'S': // short
		case 'I': // int
		case 'J': // long
		case 'F': // float
		case 'D': // double
		case '[': // type[]
		case 'V': // void
			out.append(firstChar);
			translate(in, out, start + 1, end);
			break;
		case 'L': // L fully-qualified-class;
			int endName = in.indexOf(';', start + 1);
			if (endName == -1) {
				// false positive: it's an id, translate the entire string
				String name = in.substring(start, end);
				String newName = translateName(name);
				out.append(newName);
			} else {
				String className = in.substring(start + 1, endName);
				String newClassName = translateName(className);

				out.append('L').append(newClassName).append(';');
				translate(in, out, endName + 1, end);
			}
			break;
		case '(': // ( arg-types ) ret-type
			int endArgs = in.indexOf(')', start + 1);
			if (endArgs == -1)
				throw new RetroWeaverException(
						"Class name parsing error: missing ')' in " + in);

			out.append('(');
			if (endArgs != start + 1)
				translate(in, out, start + 1, endArgs);
			out.append(')');
			translate(in, out, endArgs + 1, end);
			break;
		default:
			// translate the entire string
			String name = in.substring(start, end);
			String newName = translateName(name);
			out.append(newName);
		}
	}
}

class NameTranslatorClassVisitor extends ClassAdapter {

    public NameTranslatorClassVisitor(final ClassVisitor cv) {
        super(cv);
	}

	// Class Visitor methods

    public void visit(
        final int version,
        final int access,
        final String name,
        final String signature,
        final String superName,
        final String[] interfaces)
    { 
		// Change the super class name if necessary
		String newSuperName = NameTranslator.translate(superName);
		String newInterfaces[] = new String[interfaces.length];
		for(int i = 0; i < interfaces.length; i++)
			newInterfaces[i] = NameTranslator.translate(interfaces[i]);

        super.visit(version, access, name, signature, newSuperName, newInterfaces);
    }

    public FieldVisitor visitField(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final Object value)
    {
        return super.visitField(access,
        			name,
        			NameTranslator.translate(desc),
                    NameTranslator.translate(signature),
                    value);
    }

    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final String[] exceptions)
    {
		return new MethodTranslator(super.visitMethod(access,
				name,
				NameTranslator.translate(desc),
                NameTranslator.translate(signature),
                exceptions));
    }

	private class MethodTranslator extends MethodAdapter {
		MethodTranslator(MethodVisitor mv) {
			super(mv);
		}

	    public void visitTypeInsn(final int opcode, final String desc) {
	        super.visitTypeInsn(opcode, NameTranslator.translate(desc));
	    }

	    public void visitFieldInsn(
	        final int opcode,
	        final String owner,
	        final String name,
	        final String desc)
	    {
	    	super.visitFieldInsn(opcode, NameTranslator.translate(owner), name, NameTranslator.translate(desc));
	    }

	    public void visitMethodInsn(
	        final int opcode,
	        final String owner,
	        final String name,
	        final String desc)
	    {
	        super.visitMethodInsn(opcode, NameTranslator.translate(owner), name, NameTranslator.translate(desc));
	    }

	    public void visitTryCatchBlock(
	        final Label start,
	        final Label end,
	        final Label handler,
	        final String type)
	    {
	    	super.visitTryCatchBlock(start, end, handler, NameTranslator.translate(type));
	    }

	    public void visitLocalVariable(
	        final String name,
	        final String desc,
	        final String signature,
	        final Label start,
	        final Label end,
	        final int index)
	    {
	        super.visitLocalVariable(name, NameTranslator.translate(desc), NameTranslator.translate(signature), start, end, index);
	    }

	}

}
