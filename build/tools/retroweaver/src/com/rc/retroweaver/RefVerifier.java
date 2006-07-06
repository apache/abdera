package com.rc.retroweaver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;

import com.rc.retroweaver.event.VerifierListener;


/**
 * Reads through a class file searching for references to classes, methods, or
 * fields, which don't exist on the specified classpath. This is primarily
 * useful when trying to target one JDK while using the compiler for another.
 */
public class RefVerifier extends ClassAdapter {

	private int target;

	private String currentclassName;

	private RetroWeaverClassLoader classLoader;

	private List<String> classPathArray;

	private Set<String> failedClasses;

	private VerifierListener listener;

	private int warningCount;

	private List<String> classes;

	private Map<String, SoftReference<ClassReader>> classReaderCache = new HashMap<String, SoftReference<ClassReader>>();

	private static final String nl = System.getProperty("line.separator");

	public RefVerifier(int target, ClassVisitor cv, List<String> classPathArray, VerifierListener listener) {
		super(cv);
		classLoader = new RetroWeaverClassLoader();
		this.classPathArray = classPathArray;

		this.listener = listener;
		this.target = target;

		classes = new LinkedList<String>();
	}

	public void addClass(String className) {
		classes.add(className);
	}

	public void verifyJarFile(String jarFileName) throws IOException {
		JarFile jarFile = new JarFile(jarFileName);

		listener.verifyPathStarted("Verifying " + classes.size() + " classe(s)");
		classLoader.setClassPath(classPathArray);

		for (String name : classes) {
			JarEntry entry = jarFile.getJarEntry(name);
			InputStream is = jarFile.getInputStream(entry);
			verifyClass(is, name);
		}
	}

	public void verifyFiles() throws IOException {
		listener.verifyPathStarted("Verifying " + classes.size() + " classe(s)");
		classLoader.setClassPath(classPathArray);

		for (String sourcePath : classes) {
			verifyClass(new FileInputStream(sourcePath), sourcePath);			
		}
	}

	private void verifySingleClass(String classFileName) throws IOException {
		classLoader.setClassPath(classPathArray);

		verifyClass(new FileInputStream(classFileName), classFileName);
	}

	private void verifyClass(InputStream sourceStream, String classFileName)
			throws IOException {

		failedClasses = new HashSet<String>();

        ClassReader cr = new ClassReader(sourceStream);
        cr.accept(this, false);
	}

	private void unknowClassWarning(String className, String msg) {
		String report = currentclassName + ": unknown class "
				+ className;

		if (msg != null) {
			report += ": " + msg;
		}

		warning(report);
	}

	private void unknownFieldWarning(String owner, String name, String desc, String msg) {
		String report = currentclassName + ": unknown field " + name + '/' + desc.replace('/', '.');

		if (msg != null) {
			report += ", " + msg;
		}

		warning(report);
	}

	private void unknownMethodWarning(String owner, String name, String desc, String msg) {
		String report = currentclassName + ": unknown method " + name + '/' + desc.replace('/', '.');

		if (msg != null) {
			report += ", " + msg;
		}

		warning(report);
	}

	private void invalidClassVersion(String className, int target, int version) {
		String report = className + ": invalid class version " + version + ", target is " + target;

		warning(report);
	}

	private void warning(String report) {
		warningCount++;
		listener.acceptWarning(report);
	}

	public void displaySummary() {
		if (warningCount != 0)
			listener.displaySummary(warningCount);
	}

	private ClassReader getClassReader(String className) throws ClassNotFoundException {
		ClassReader reader = null;
		SoftReference<ClassReader> ref = classReaderCache.get(className);
		if (ref != null)
			reader = ref.get();

		if (reader == null) {
			byte b[] = classLoader.getClassData(className);

			reader = new ClassReader(b);

			classReaderCache.put(className, new SoftReference<ClassReader>(reader));

			// class file version should not be higher than target
			int version = reader.readShort(6); // get major number only
			if (version > target) {
				invalidClassVersion(className.replace('/', '.'), target, version);
			}
		}
		return reader;		
	}

	public static String getUsage() {
		return "Usage: RefVerifier <options>" + nl + " Options: " + nl
				+ " -class <path to class to verify> (required) " + nl
				+ " -cp <classpath containing valid classes> (required)";
	}

	public static void main(String[] args) throws IOException {

		List<String> classpath = new ArrayList<String>();
		String classfile = null;

		for (int i = 0; i < args.length; ++i) {
			String command = args[i];
			++i;

			if (command.equals("-class")) {
				classfile = args[i];
			} else if (command.equals("-cp")) {
				String path = args[i];
				StringTokenizer st = new StringTokenizer(path,
						File.pathSeparator);
				while (st.hasMoreTokens()) {
					classpath.add(st.nextToken());
				}
			} else {
				System.out
						.println("I don't understand the command: " + command);
				System.out.println();
				System.out.println(getUsage());
				return;
			}
		}

		if (classfile == null) {
			System.out.println("Option \"-class\" is required.");
			System.out.println();
			System.out.println(getUsage());
			return;
		}

		RefVerifier vr = new RefVerifier(Weaver.VERSION_1_4, EMPTY_VISITOR, classpath,
				new DefaultRefVerifierListener(true));
		vr.verifySingleClass(classfile);
		vr.displaySummary();
	}
	
	private void checkClassName(String className) {
		Type t = Type.getType(className);

		switch (t.getSort()) {
		case Type.ARRAY:
			t = t.getElementType();
			if (t.getSort() != Type.OBJECT)
				return;

			// fall through to object processing
		case Type.OBJECT:
			className = t.getClassName();
			break;
		default:
			return;
		}
		
		checkSimpleClassName(className);
	}

	private void checkClassNameInType(String className) {
		switch (className.charAt(0)) {
			case 'L':
			case '[':
				checkClassName(className);
				break;
			default:
				checkSimpleClassName(className);
		}
	}

	private void checkSimpleClassName(String className) {
		try {
			className = className.replace('.', '/');
			getClassReader(className);
		} catch (ClassNotFoundException e) {
			failedClasses.add(className);
			unknowClassWarning(className.replace('/', '.'), null);
		}
	}

	// visitor methods

    public void visit(
        final int version,
        final int access,
        final String name,
        final String signature,
        final String superName,
        final String[] interfaces)
    {
		listener.verifyClassStarted("Verifying " + name);

		currentclassName = name.replace('/', '.');

		if (superName != null) {
			checkSimpleClassName(superName);
		}
		if (interfaces != null) {
			for (int i = 0; i < interfaces.length; ++i) {
				checkSimpleClassName(interfaces[i]);
			}
		}

		cv.visit(version, access, name, signature, superName, interfaces);
    }

    public void visitOuterClass(
        final String owner,
        final String name,
        final String desc)
    {
    	checkSimpleClassName(owner);

    	cv.visitOuterClass(owner, name, desc);
    }

    public void visitInnerClass(
        final String name,
        final String outerName,
        final String innerName,
        final int access)
    {
        if (name != null) {
        	checkSimpleClassName(name);
        }
        if (outerName != null) {
        	checkSimpleClassName(outerName);
        }
        
        cv.visitInnerClass(name, outerName, innerName, access);
    }

    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final String[] exceptions)
    {
        if (exceptions != null) {
            for (String s: exceptions)
            	checkSimpleClassName(s);
        }
    
        return new MethodVerifier(cv.visitMethod(access, name, desc, signature, exceptions));
    }

    private class MethodVerifier extends MethodAdapter {

    	MethodVerifier(MethodVisitor mv) {
    		super(mv);
    	}
        
    	public void visitTypeInsn(int opcode, String desc) {
    		checkClassNameInType(desc);
    		
    		mv.visitTypeInsn(opcode, desc);
    	}
 
    	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			// Don't report a field error, about a class for which we've
			// already shown an error
			if (!failedClasses.contains(owner)) {
				try {
					if (!findField(owner, name, desc))
						unknownFieldWarning(owner, name,desc, "Field not found in " + owner.replace('/', '.'));
				} catch (ClassNotFoundException e) {
						unknownFieldWarning(owner, name,desc, "The class, " + owner.replace('/', '.')
						+ ", could not be located: " + e.getMessage());
				}
			}
			mv.visitFieldInsn(opcode, owner, name, desc);
    	}
    	
    	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			if (!failedClasses.contains(owner) && owner.charAt(0) != '[') {
				// Don't report a method error, about a class for which we've
				// already shown an error.
				// We just ignore methods called on arrays, because we know
				// they must exist   		

				try {
					if (!findMethod(owner, name, desc)) {
						unknownMethodWarning(owner, name, desc, "Method not found in " + owner.replace('/', '.'));						
					}
				} catch (ClassNotFoundException e) {
					unknownMethodWarning(owner, name, desc, "The class, " + owner.replace('/', '.')
							+ ", could not be located: " + e.getMessage());
				}
			}

			mv.visitMethodInsn(opcode, owner, name, desc);
    	}

    	public void visitMultiANewArrayInsn(String desc, int dims) {
    		checkClassName(desc);
    		
    		mv.visitMultiANewArrayInsn(desc, dims);
    	}

    	public void visitLocalVariable(
	            String name,
	            String desc,
	            String signature,
	            Label start,
	            Label end,
	            int index) {
    		checkClassName(desc);
    		
    		mv.visitLocalVariable(name, desc, signature, start, end, index);
    	}

    }

	private boolean findField(String owner, final String name, final String c) throws ClassNotFoundException {
		String javaClassName = owner;
		while (true) {
			ClassReader reader = getClassReader(javaClassName);
			FindFieldOrMethodClassVisitor visitor = new FindFieldOrMethodClassVisitor(false, name, c);
		
			try {
				reader.accept(visitor, false);
			} catch (Success s) {
				return true;
			}
			String[] is = visitor.classInterfaces;
			for (String i : is)
				if (findField(i, name, c))
					return true;

			if (javaClassName.equals("java/lang/Object"))
				return false;
			javaClassName = visitor.superClassName;
		}
	}

	private boolean findMethod(final String owner, final String name, final String desc) throws ClassNotFoundException {
		String javaClassName = owner;
		while (true) {
			ClassReader reader = getClassReader(javaClassName);
			FindFieldOrMethodClassVisitor visitor = new FindFieldOrMethodClassVisitor(true, name, desc);
			try {
				reader.accept(visitor, false);
			} catch (Success s) {
				return true;
			}

			if (visitor.isInterface || visitor.isAbstract) {
				String[] is = visitor.classInterfaces;
				for (String i : is) {
					if (findMethod(i, name, desc))
						return true;
				}
				if (visitor.isInterface) {
					return false;
				}
			}

			if (javaClassName.equals("java/lang/Object"))
				return false;
			javaClassName = visitor.superClassName;
		}
	}

	private static final EmptyVisitor EMPTY_VISITOR = new EmptyVisitor();

	private static class Success extends RuntimeException {};

	// Visitor to search for fields or methods in supplier classes

	private static class FindFieldOrMethodClassVisitor implements ClassVisitor {
		FindFieldOrMethodClassVisitor(boolean methdodMatcher, final String name, final String desc) {
			this.searchedName = name;
			this.searchedDesc = desc;
			this.methdodMatcher = methdodMatcher;
		}
		boolean methdodMatcher;
		final String searchedName;
		final String searchedDesc;

		String classInterfaces[];
		String superClassName;
		boolean isInterface;
		boolean isAbstract;

	    public void visit(
	            final int version,
	            final int access,
	            final String name,
	            final String signature,
	            final String superName,
	            final String[] interfaces)
	        {
	    		classInterfaces = interfaces;
	    		superClassName = superName;
	    		isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
	    		isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
	        }

	    public void visitSource(final String source, final String debug) {
	    }

	    public void visitOuterClass(
	        final String owner,
	        final String name,
	        final String desc)
	    {
	    }

	    public AnnotationVisitor visitAnnotation(
	        final String desc,
	        final boolean visible)
	    {
	        return EMPTY_VISITOR;
	    }

	    public void visitAttribute(final Attribute attr) {
	    }

	    public void visitInnerClass(
	        final String name,
	        final String outerName,
	        final String innerName,
	        final int access)
	    {
	    }

	    public FieldVisitor visitField(
	            final int access,
	            final String name,
	            final String desc,
	            final String signature,
	            final Object value) {
	    	if (!methdodMatcher)
		    	if (name.equals(searchedName) && desc.equals(searchedDesc)) {
		    		throw new Success();
		    	}
	        return null;
	   }

		public MethodVisitor visitMethod(
	            int access,
	            String name,
	            String desc,
	            String signature,
	            String[] exceptions) {
			if (methdodMatcher)
				if (name.equals(searchedName) && desc.equals(searchedDesc)) {
					throw new Success();
				}
	        return null;
	   }

	    public void visitEnd() {
	    }
	}

}

class DefaultRefVerifierListener implements VerifierListener {

	private boolean verbose;

	DefaultRefVerifierListener(boolean verbose) {
		this.verbose = verbose;
	}

	public void verifyPathStarted(String msg) {
		System.out.println("[RefVerifier] " + msg);
	}

	public void verifyClassStarted(String msg) {
		if (verbose)
			System.out.println("[RefVerifier] " + msg);
	}

	public void acceptWarning(String msg) {
		System.out.println("[RefVerifier] " + msg);
	}

	public void displaySummary(int warningCount) {
		System.out.println("[RefVerifier] Verification complete, "
				+ warningCount + " warning(s).");
	}

}
