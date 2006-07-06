package com.rc.retroweaver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.rc.retroweaver.event.WeaveListener;
import com.rc.retroweaver.optimizer.ClassConstantsCollector;
import com.rc.retroweaver.optimizer.Constant;
import com.rc.retroweaver.optimizer.ConstantComparator;
import com.rc.retroweaver.optimizer.ConstantPool;

/**
 * A bytecode enhancer that translates Java 1.5 class files into Java 1.4 class
 * files. The enhancer performs primarily two tasks: 1) Reverses changes made to
 * the class file format in 1.5 to the former 1.4 format. 2) Replaces compiler
 * generated calls into the new 1.5 runtime with calls into RetroWeaver's
 * replacement runtime.
 */
public class RetroWeaver {

	private int target;

	private boolean lazy;

	/**
	 * Indicates whether the generic signatures should be stripped. Default to <code>false</code>.
	 */
	private boolean stripSignatures;

	private int weavedClassCount;

	private WeaveListener listener;

	private RefVerifier verifier;

	private static final String newLine = System.getProperty("line.separator");

	public RetroWeaver(int target) {
		this.target = target;
	}

	protected static final FileFilter classFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().endsWith(".class");
		}
	};

	protected static final FileFilter subdirFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory();
		}
	};

	protected static void buildFileSets(ArrayList<File[]> fileSets, File path) {
		File[] files = path.listFiles(classFilter);
		if (files != null) {
			fileSets.add(files);
		}

		File[] subdirs = path.listFiles(subdirFilter);
		if (subdirs != null) {
			for (File subdir : subdirs)
				buildFileSets(fileSets, subdir);
		}
	}

	private void displayStartMessage(int n) {
		listener.weavingStarted("Processing " + n + " classe(s)");	
	}

	private void displayEndMessage() {
		listener.weavingCompleted(Integer.toString(weavedClassCount) + " classe(s) weaved.");
	}

	public void weave(File path) throws IOException {
		ArrayList<File[]> fileSets = new ArrayList<File[]>();

		buildFileSets(fileSets, path);

		int n = 0;
		for (File[] set : fileSets)
			n += set.length;
		displayStartMessage(n);

		for (int i = 0; i < fileSets.size(); i++) {
			for (File file : fileSets.get(i)) {
				String sourcePath = file.getCanonicalPath();
				weave(sourcePath, null);
			}
		}
		displayEndMessage();

		if (verifier != null) {
			verifier.verifyFiles();
			verifier.displaySummary();
		}
	}

	public void weave(File[] baseDirs, String[][] fileSets, File outputDir)
			throws IOException {
		int n = 0;
		for (String[] set : fileSets)
			n += set.length;
		displayStartMessage(n);

		Set<String> weaved = new HashSet<String>();
		for (int i = 0; i < fileSets.length; i++) {
			for (String fileName : fileSets[i]) {
				File file = new File(baseDirs[i], fileName);
				String sourcePath = file.getCanonicalPath();
				String outputPath = null;
				if (outputDir != null)
					outputPath = new File(outputDir, fileName)
							.getCanonicalPath();

				// Weave it unless already weaved.
				if (!weaved.contains(sourcePath)) {
					weave(sourcePath, outputPath);
					weaved.add(sourcePath);
				}
			}
		}
		displayEndMessage();

		if (verifier != null) {
			verifier.verifyFiles();
			verifier.displaySummary();
		}
	}

	public void weaveJarFile(String sourceJarFileName, String destJarFileName)
			throws IOException {
		JarFile jarFile = new JarFile(sourceJarFileName);
		ArrayList<JarEntry> entries = Collections.list(jarFile.entries());

		OutputStream os = new FileOutputStream(destJarFileName);
		JarOutputStream out = new JarOutputStream(os);

		int n = 0;
		for (JarEntry entry : entries) {
			if (entry.getName().endsWith(".class"))
				n++;
		}
		displayStartMessage(n);

		for (JarEntry entry : entries) {
			String name = entry.getName();
			InputStream dataStream = null;
			if (name.endsWith(".class")) {
				// weave class
				InputStream is = jarFile.getInputStream(entry);
				ByteArrayOutputStream classStream = new ByteArrayOutputStream();
				if (weave(is, name, classStream)) {
					// class file was modified
					weavedClassCount++;

					dataStream = new ByteArrayInputStream(classStream
							.toByteArray());

					// create new entry
					entry = new JarEntry(name);
					recordFileForVerifier(name);
				}
			}

			if (dataStream == null) {
				// not a class file or class wasn't no
				dataStream = jarFile.getInputStream(entry);
			}
			// writing entry
			out.putNextEntry(entry);

			// writing data
			int len;
			final byte[] buf = new byte[1024];
			while ((len = dataStream.read(buf)) >= 0) {
				out.write(buf, 0, len);
			}
		}
		out.close();

		displayEndMessage();

		if (verifier != null) {
			verifier.verifyJarFile(destJarFileName);
			verifier.displaySummary();
		}
	}

	public void weave(String sourcePath, String outputPath) throws IOException {
		InputStream is = new FileInputStream(sourcePath);
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			if (weave(is, sourcePath, bos)) {
				// new class was generated
				weavedClassCount++;

				if (outputPath == null) {
					outputPath = sourcePath;
				} else {
					// create parent dir if necessary
					File parentDir = new File(outputPath).getParentFile();
					if (parentDir != null) {
						parentDir.mkdirs();
					}
				}
				FileOutputStream fos = new FileOutputStream(outputPath);
				fos.write(bos.toByteArray());
				fos.close();
				
				recordFileForVerifier(outputPath);
			} else {
				// We're lazy and the class already has the target version.

				if (outputPath == null) {
					// weaving in place
					return;
				}

				File dir = new File(outputPath).getParentFile();
				if (dir != null)
					dir.mkdirs();

				File sf = new File(sourcePath);
				File of = new File(outputPath);

				if (!of.isFile()
						|| !of.getCanonicalPath().equals(sf.getCanonicalPath())) {
					// Target doesn't exist or is different from source so copy
					// the file and transfer utime.
					FileInputStream fis = new FileInputStream(sf);
					byte[] bytes = new byte[(int) sf.length()];
					fis.read(bytes);
					fis.close();
					FileOutputStream fos = new FileOutputStream(of);
					fos.write(bytes);
					fos.close();
					of.setLastModified(sf.lastModified());
				}
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}

	private void recordFileForVerifier(String fileName) {
		if (verifier != null) {
			verifier.addClass(fileName);
		}
	}

	private static final boolean COMPACT_CONSTANTS = true;

	protected boolean weave(InputStream sourceStream, String fileName, ByteArrayOutputStream bos)
			throws IOException {

        ClassReader cr = new ClassReader(sourceStream);
        ClassWriter cw = new ClassWriter(true);

        try {
        	NameTranslatorClassVisitor translator;
        	ConstantPool cp;
            if (COMPACT_CONSTANTS) {
                cp = new ConstantPool();
                ClassConstantsCollector ccc = new ClassConstantsCollector(cw, cp);
                translator = new NameTranslatorClassVisitor(ccc);
            } else {
            	translator = new NameTranslatorClassVisitor(cw);
            }
            ClassWeaver classWeaver = new ClassWeaver(translator, lazy, target, listener);
            ClassVisitor v;
            if (stripSignatures) {
                v = new SignatureStripper(classWeaver);
            } else {
            	v = classWeaver;
            }
            cr.accept(v, false);      	

            if (COMPACT_CONSTANTS) {
            	Set<Constant> constants = new TreeSet<Constant>(new ConstantComparator());
            	constants.addAll(cp.values());

            	cr = new ClassReader(cw.toByteArray());
                cw = new ClassWriter(false);
                for(Constant c: constants)
                	c.write(cw);
                cr.accept(cw, false);
            }

        	bos.write(cw.toByteArray());
        	return true;
        } catch (LazyException e) {
        	return false;
        }
 	}

    static void optimize(final InputStream is, final ByteArrayOutputStream os)
            throws IOException
    {
    }

	public void setListener(WeaveListener listener) {
		this.listener = listener;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public void setVerifier(RefVerifier verifier) {
		this.verifier = verifier;
	}

	public static String getUsage() {
		return "Usage: RetroWeaver " + newLine + " <source path>" + newLine
				+ " [<output path>]";
	}

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println(getUsage());
			return;
		}

		String sourcePath = args[0];
		String outputPath = null;

		if (args.length > 1) {
			outputPath = args[1];
		}

		try {
			RetroWeaver weaver = new RetroWeaver(Weaver.VERSION_1_4);
			weaver.weave(sourcePath, outputPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param stripSignatures The stripSignatures to set.
	 */
	public void setStripSignatures(boolean stripSignatures) {
		this.stripSignatures = stripSignatures;
	}
}

class LazyException extends RuntimeException {
}

class ClassWeaver extends ClassAdapter implements Opcodes {

    private boolean lazy;
    private int target;
    private WeaveListener listener;

    private String className;

    private boolean isEnum;
    private boolean isInterface;

    private Set<String> classLiteralCalls = new HashSet<String>();

    public ClassWeaver(final ClassVisitor cv, boolean lazy, int target, WeaveListener listener) {
        super(cv);
        this.lazy = lazy;
        this.target = target;
        this.listener = listener;
    }

    public void visit(
        final int version,
        final int access,
        final String name,
        final String signature,
        final String superName,
        final String[] interfaces)
    {
    	if (lazy && (version <= target)) {
        	// abort all visitors
    		throw new LazyException();
    	}

		if (listener != null) {
			listener.weavingPath(name);
		}

		className = name;
        isEnum = superName != null && superName.equals("java/lang/Enum");
        isInterface = (access & ACC_INTERFACE) == ACC_INTERFACE;

        cv.visit(target, // Changes the format of the class file from 1.5 to the target value.
                access,
                name,
                signature,
                superName,
                interfaces);
    }

    public void visitInnerClass(
        final String name,
        final String outerName,
        final String innerName,
        final int access)
    {
        cv.visitInnerClass(name, outerName, innerName, access);
    }

    public FieldVisitor visitField(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final Object value)
    {
        return cv.visitField(access, name, desc, signature, value);
    }

    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final String[] exceptions)
    {
        int newAccess;
        if ((access&ACC_SYNTHETIC&ACC_BRIDGE) == (ACC_SYNTHETIC&ACC_BRIDGE)) {
            /*
            bridge methods for generic create problems with RMIC code in 1.4.
            It's a known bug with 1.4, see SUN's bug database at:
                http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4811083
                http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5035300

            Problem found when implementing Comparable<E>, with bridge method
                compareTo(Ljava/lang/Object;)I;
            */
            newAccess = access & ~ACC_SYNTHETIC & ~ACC_BRIDGE;
        } else
            newAccess = access;

        MethodVisitor mv = new MethodWeaver(super.visitMethod(newAccess,
                    name,
                    desc,
                    signature,
                    exceptions));
    	
    	if (!isEnum || !name.equals("<clinit>"))
    		return mv;

    	return new EnumMethodWeaver(mv);
    }

    public void visitEnd() {
        if (isEnum) {
        	cv.visitField(ACC_PRIVATE + ACC_STATIC + ACC_FINAL + ACC_SYNTHETIC,
        			SERIAL_ID_FIELD,
        			SERIAL_ID_SIGNATURE,
        			null, new Long(0L));
        }
        if (classLiteralCalls.size() != 0) {
    		// generate synthetic fields and class$ method
    		for(String fieldName: classLiteralCalls) {
    			FieldVisitor fv = visitField(ACC_STATIC + ACC_SYNTHETIC + ACC_FINAL
    					+ (isInterface?ACC_PUBLIC:ACC_PRIVATE),
	    					fieldName,
	    					CLASS_FIELD_DESC,
	    					null, null);
    			fv.visitEnd();
    		}

    		if (!isInterface) {
	    		// "class$" method
	    		String exceptionTable[] = { JAVA_LANG_NOCLASSDEFFOUNDERROR };
	    		MethodVisitor mv = cv.visitMethod(ACC_STATIC+ACC_SYNTHETIC,
								    				CLASS_METHOD,
								    				CLASS_SIGNATURE,
								    				null, exceptionTable);
	
	    		mv.visitCode();

	            mv.visitVarInsn(ALOAD, 0);
	            generateClassCall(mv);
	            mv.visitInsn(ARETURN);
	    
	            mv.visitMaxs(0, 0);
	            mv.visitEnd();
    		}
    	}
        cv.visitEnd();
    }

    /**
     * Generate the byte code equivalent to ".class"
     * 
     * Note: assumes the class name is already on the stack
     * 
     * @param mv method visitor to use
     */
    private void generateClassCall(MethodVisitor mv) {
    	/* 
    	 * generate the code equivalent to ".class"
    	 * 

    	 	try {
    	 		c = Class.forName(name);
			} catch (ClassNotFoundException e) {
				NoClassDefFoundError t = new NoClassDefFoundError(e.getMessage());
				try {
					t.initCause(e); // only works with 1.4+
				} catch (NoSuchMethodError nsm) {
				}
				throw t;
			}
    	 */
    	Label start1 = new Label();
    	Label end1 = new Label();
    	Label handler1 = new Label();
    	mv.visitTryCatchBlock(start1, end1, handler1, JAVA_LANG_CLASSNOTFOUNDEXCEPTION);
    	Label start2 = new Label();
    	Label end2 = new Label();
    	Label handler2 = new Label();
    	mv.visitTryCatchBlock(start2, end2, handler2, JAVA_LANG_NOSUCHMETHODERROR);

    	mv.visitLabel(start1);
    	mv.visitMethodInsn(INVOKESTATIC, JAVA_LANG_CLASS, FOR_NAME_METHOD, FOR_NAME_SIGNATURE);
    	mv.visitVarInsn(ASTORE, 1);
    	mv.visitLabel(end1);
    	Label gotoLabel1 = new Label();
    	mv.visitJumpInsn(GOTO, gotoLabel1);

    	mv.visitLabel(handler1);
    	mv.visitVarInsn(ASTORE, 2);
    	mv.visitTypeInsn(NEW, JAVA_LANG_NOCLASSDEFFOUNDERROR);
    	mv.visitInsn(DUP);
    	mv.visitVarInsn(ALOAD, 2);
    	mv.visitMethodInsn(INVOKEVIRTUAL, JAVA_LANG_CLASSNOTFOUNDEXCEPTION, GET_MESSAGE_METHOD, GET_MESSAGE_SIGNATURE);
    	mv.visitMethodInsn(INVOKESPECIAL, JAVA_LANG_NOCLASSDEFFOUNDERROR, INIT_METHOD, JAVA_LANG_NOCLASSDEFFOUNDERROR_INIT_SIGNATURE);
    	mv.visitVarInsn(ASTORE, 3);

    	mv.visitLabel(start2);
    	mv.visitVarInsn(ALOAD, 3);
    	mv.visitVarInsn(ALOAD, 2);
    	mv.visitMethodInsn(INVOKEVIRTUAL, JAVA_LANG_NOCLASSDEFFOUNDERROR, INIT_CAUSE_METHOD, INIT_CAUSE_SIGNATURE);
    	mv.visitInsn(POP);
    	mv.visitLabel(end2);
    	Label gotoLabel2 = new Label();
    	mv.visitJumpInsn(GOTO, gotoLabel2);

    	mv.visitLabel(handler2);
    	mv.visitVarInsn(ASTORE, 4);

    	mv.visitLabel(gotoLabel2);
    	mv.visitVarInsn(ALOAD, 3);
    	mv.visitInsn(ATHROW);
    	mv.visitLabel(gotoLabel1);
    	mv.visitVarInsn(ALOAD, 1);
    }

    private class EnumMethodWeaver extends MethodAdapter implements Opcodes {
    	public EnumMethodWeaver(final MethodVisitor mv) {
    		super(mv);
    	}

        public void visitInsn(final int opcode) {
        	if (opcode == RETURN) {
            	// add call to setEnumValues(Object[] values, Class c)

            	String owner = className.replace('.', '/');
            	String fullName = 'L' + owner + ';';
            	Type t = Type.getType(fullName);

            	mv.visitMethodInsn(INVOKESTATIC, owner, "values", "()[" + fullName);
            	mv.visitLdcInsn(t);
            	mv.visitMethodInsn(INVOKESTATIC, "com/rc/retroweaver/runtime/Enum_",
            			"setEnumValues", "([Ljava/lang/Object;Ljava/lang/Class;)V");     		
        	}
            mv.visitInsn(opcode);
        }

    }

    private static final String JAVA_LANG_CLASS = "java/lang/Class";
    private static final String FOR_NAME_METHOD = "forName";
    private static final String FOR_NAME_SIGNATURE = "(Ljava/lang/String;)Ljava/lang/Class;";

    private static final String SERIAL_ID_FIELD = "serialVersionUID";
    private static final String SERIAL_ID_SIGNATURE = "J";

    private static final String JAVA_LANG_NOCLASSDEFFOUNDERROR = "java/lang/NoClassDefFoundError";
    private static final String INIT_METHOD = "<init>";
    private static final String JAVA_LANG_NOCLASSDEFFOUNDERROR_INIT_SIGNATURE = "(Ljava/lang/String;)V";
    private static final String INIT_CAUSE_METHOD = "initCause";
    private static final String INIT_CAUSE_SIGNATURE = "(Ljava/lang/Throwable;)Ljava/lang/Throwable;";
    
    private static final String JAVA_LANG_CLASSNOTFOUNDEXCEPTION = "java/lang/ClassNotFoundException";
    private static final String GET_MESSAGE_METHOD = "getMessage"; 	 
    private static final String GET_MESSAGE_SIGNATURE = "()Ljava/lang/String;";

    private static final String JAVA_LANG_NOSUCHMETHODERROR = "java/lang/NoSuchMethodError";

	private static final String CLASS_FIELD_DESC = "Ljava/lang/Class;";
	private static final String CLASS_METHOD = "class$";
	private static final String CLASS_SIGNATURE = "(Ljava/lang/String;)Ljava/lang/Class;";

	private static final String ITERABLE_CLASS = "java/lang/Iterable";
	private static final String ITERATOR_METHOD = "iterator";
	private static final String ITERATOR_SIGNATURE = "()Ljava/util/Iterator;";
	private static final String ITERABLE_METHODS_CLASS = "com/rc/retroweaver/runtime/IterableMethods";
	private static final String ITERABLE_METHODS_ITERATOR_SIGNATURE = "(Ljava/lang/Object;)Ljava/util/Iterator;";

	private static final String JAVA_LANG_SYSTEM = "java/lang/System";
	private static final String NANO_TIME_METHOD = "nanoTime";
	private static final String NANO_TIME_SIGNATURE = "()J";
	private static final String CURRENT_TIME_MILLIS_METHOD = "currentTimeMillis";
	private static final String CURRENT_TIME_MILLIS_TIME_SIGNATURE = "()J";
	
	private static final String CLASS_METHODS_CLASS = "com/rc/retroweaver/runtime/ClassMethods";
	private static final String CAST_METHOD = "cast";
	private static final String CAST_SIGNATURE = "(Ljava/lang/Object;)Ljava/lang/Object;";
	private static final String CAST_NEW_SIGNATURE = "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;";
	private static final String AS_SUB_CLASS_METHOD = "asSubclass";
	private static final String AS_SUB_CLASS_SIGNATURE = "(Ljava/lang/Class;)Ljava/lang/Class;";
	private static final String AS_SUB_CLASS_NEW_SIGNATURE = "(Ljava/lang/Class;Ljava/lang/Class;)Ljava/lang/Class;";
	private static final String IS_ENUM_METHOD = "isEnum";
	private static final String IS_ENUM_SIGNATURE = "()Z";
	private static final String IS_ENUM_NEW_SIGNATURE = "(Ljava/lang/Class;)Z";
	private static final String GET_ENUM_CONSTANTS_METHOD = "getEnumConstants";
	private static final String GET_ENUM_CONSTANTS_SIGNATURE = "()[Ljava/lang/Object;";
	private static final String GET_ENUM_CONSTANTS_NEW_SIGNATURE = "(Ljava/lang/Class;)[Ljava/lang/Object;";
	
	private static final String JAVA_LANG_STRINGBUFFER = "java/lang/StringBuffer";
	private static final String JAVA_LANG_STRINGBUILDER = "java/lang/StringBuilder";
    private static final String JAVA_LANG_STRINGBUFFER_INIT_SIGNATURE = "(Ljava/lang/CharSequence;)V";
    private static final String JAVA_LANG_STRINGBUFFER_INIT_STRING_SIGNATURE = "(Ljava/lang/String;)V";
	private static final String APPEND_METHOD = "append";
	private static final String APPEND_SIGNATURE1 = "(Ljava/lang/CharSequence;)Ljava/lang/StringBuffer;";
	private static final String APPEND_SIGNATURE2 = "(Ljava/lang/CharSequence;II)Ljava/lang/StringBuffer;";
	private static final String APPEND_SIGNATURE3 = "(Ljava/lang/String;)Ljava/lang/StringBuffer;";
	private static final String INSERT_METHOD = "insert";
	private static final String INSERT_SIGNATURE1 = "(ILjava/lang/CharSequence;)Ljava/lang/StringBuffer;";
	private static final String INSERT_SIGNATURE2 = "(ILjava/lang/CharSequence;II)Ljava/lang/StringBuffer;";
	private static final String INSERT_SIGNATURE3 = "(ILjava/lang/String;)Ljava/lang/StringBuffer;";
	private static final String TRIM_TO_SIZE = "trimToSize";
	private static final String TRIM_TO_SIZE_SIGNATURE = "()V";

	private static final String CHAR_SEQUENCE_CLASS = "java/lang/CharSequence";
	private static final String TO_STRING_METHOD = "toString";
	private static final String TO_STRING_SIGNATURE = "()Ljava/lang/String;";

	private static final String SUB_SEQUENCE_METHOD = "subSequence";
	private static final String SUB_SEQUENCE_SIGNATURE = "(II)Ljava/lang/CharSequence;";
	
	private static final String AUTOBOX_CLASS = "com/rc/retroweaver/runtime/Autobox";
	private static final String VALUE_OF_METHOD = "valueOf";

	private static final String JAVA_MATH_BIGDECIMAL_CLASS = "java/math/BigDecimal";
	private static final String INT_CONSTRUCTOR_SIGNATURE = "(I)V";
	private static final String LONG_CONSTRUCTOR_SIGNATURE = "(J)V";
	private static final String DOUBLE_CONSTRUCTOR_SIGNATURE = "(D)V";
	private static final String STRING_CONSTRUCTOR_SIGNATURE = "(Ljava/lang/String;)V";
	private static final String TO_STRING_LONG_SIGNATURE = "(J)Ljava/lang/String;";
	private static final String JAVA_LANG_LONG = "java/lang/Long";

	private static final String JAVA_UTIL_COLLECTIONS = "java/util/Collections";
	private static final String EMPTY_LIST_METHOD = "emptyList";
	private static final String EMPTY_LIST_SIGNATURE = "()Ljava/util/List;";
	private static final String EMPTY_LIST_FIELD = "EMPTY_LIST";
	private static final String EMPTY_LIST_FIELD_DESC = "Ljava/util/List;";
	private static final String EMPTY_MAP_METHOD = "emptyMap";
	private static final String EMPTY_MAP_SIGNATURE = "()Ljava/util/Map;";
	private static final String EMPTY_MAP_FIELD = "EMPTY_MAP";
	private static final String EMPTY_MAP_FIELD_DESC = "Ljava/util/Map;";
	private static final String EMPTY_SET_METHOD = "emptySet";
	private static final String EMPTY_SET_SIGNATURE = "()Ljava/util/Set;";
	private static final String EMPTY_SET_FIELD = "EMPTY_SET";
	private static final String EMPTY_SET_FIELD_DESC = "Ljava/util/Set;";

	private static Map<String, String> boxSignatures = new HashMap<String, String>();
	static {
		boxSignatures.put("java/lang/Boolean", "(Z)Ljava/lang/Boolean;");
		boxSignatures.put("java/lang/Byte", "(B)Ljava/lang/Byte;");
		boxSignatures.put("java/lang/Character", "(C)Ljava/lang/Character;");
		boxSignatures.put("java/lang/Short", "(S)Ljava/lang/Short;");
		boxSignatures.put("java/lang/Integer", "(I)Ljava/lang/Integer;");
		boxSignatures.put("java/lang/Long", "(J)Ljava/lang/Long;");
		boxSignatures.put("java/lang/Float", "(F)Ljava/lang/Float;");
		boxSignatures.put("java/lang/Double", "(D)Ljava/lang/Double;");
	}


	class MethodWeaver extends MethodAdapter implements Opcodes {
		
	public MethodWeaver(final MethodVisitor mv) {
		super(mv);
	}

    public void visitMaxs(final int maxStack, final int maxLocals) {
        mv.visitMaxs(maxStack, maxLocals);
    }

    public void visitMethodInsn(
        final int opcode,
        final String owner,
        final String name,
        final String desc)
    {
    	// ClassMethods substitutions
    	if ((opcode == INVOKEVIRTUAL || opcode == INVOKESPECIAL) && owner.equals(JAVA_LANG_CLASS)) {
			// Change calls to "cast" or "asSubclass" to an INVOKESTATIC
    		// targeting one of our runtime methods  				
			if (name.equals(CAST_METHOD) && desc.equals(CAST_SIGNATURE)) {
				super.visitMethodInsn(INVOKESTATIC,
						CLASS_METHODS_CLASS,
						name,
						CAST_NEW_SIGNATURE);
				return;
			} else if (name.equals(AS_SUB_CLASS_METHOD) && desc.equals(AS_SUB_CLASS_SIGNATURE)) {
				super.visitMethodInsn(INVOKESTATIC,
						CLASS_METHODS_CLASS,
						name,
						AS_SUB_CLASS_NEW_SIGNATURE);
				return;
			} else if (name.equals(IS_ENUM_METHOD) && desc.equals(IS_ENUM_SIGNATURE)) {
				super.visitMethodInsn(INVOKESTATIC,
						CLASS_METHODS_CLASS,
						name,
						IS_ENUM_NEW_SIGNATURE);
				return;
			} else if (name.equals(GET_ENUM_CONSTANTS_METHOD) && desc.equals(GET_ENUM_CONSTANTS_SIGNATURE)) {
				super.visitMethodInsn(INVOKESTATIC,
						CLASS_METHODS_CLASS,
						name,
						GET_ENUM_CONSTANTS_NEW_SIGNATURE);
				return;
			}
    	} else if (owner.equals(JAVA_MATH_BIGDECIMAL_CLASS)&&name.equals(INIT_METHOD)) {
    		if (desc.equals(INT_CONSTRUCTOR_SIGNATURE)) {
    			// replace int constructor with double one after convertion i2d, no loss of precision
    			super.visitInsn(I2D);
    			super.visitMethodInsn(INVOKESPECIAL,
    					JAVA_MATH_BIGDECIMAL_CLASS,
    					INIT_METHOD,
    					DOUBLE_CONSTRUCTOR_SIGNATURE);
    			return;
    		} else if (desc.equals(LONG_CONSTRUCTOR_SIGNATURE)) {
    			// longs cannot be converted to double, use toString() instead
    			super.visitMethodInsn(INVOKESTATIC,
    					JAVA_LANG_LONG,
    					TO_STRING_METHOD,
    					TO_STRING_LONG_SIGNATURE);
    			super.visitMethodInsn(INVOKESPECIAL,
    					JAVA_MATH_BIGDECIMAL_CLASS,
    					INIT_METHOD,
    					STRING_CONSTRUCTOR_SIGNATURE);
    			return;
    		}
    	} else if (opcode == INVOKEINTERFACE &&
    				owner.equals(ITERABLE_CLASS) &&
    				name.equals(ITERATOR_METHOD) &&
    				desc.equals(ITERATOR_SIGNATURE)) {
    		super.visitMethodInsn(INVOKESTATIC,
    				ITERABLE_METHODS_CLASS,
    				ITERATOR_METHOD,
    				ITERABLE_METHODS_ITERATOR_SIGNATURE);
    		return;
    	} else if (opcode == INVOKESTATIC &&
    				owner.equals(JAVA_LANG_SYSTEM)&&
    				name.equals(NANO_TIME_METHOD)&&
    				desc.equals(NANO_TIME_SIGNATURE)) {
    		// call to nanoTime() is replaced by currentTimeMillis()*1000000L
    		super.visitMethodInsn(INVOKESTATIC,
    				JAVA_LANG_SYSTEM,
    				CURRENT_TIME_MILLIS_METHOD,
    				CURRENT_TIME_MILLIS_TIME_SIGNATURE);
    		super.visitLdcInsn(new Long(1000000L));
    		super.visitInsn(LMUL);
    		return;
    	} else if (owner.equals(JAVA_LANG_STRINGBUFFER)||owner.equals(JAVA_LANG_STRINGBUILDER)) {
    		if ((opcode == INVOKESPECIAL) && name.equals(INIT_METHOD) && desc.equals(JAVA_LANG_STRINGBUFFER_INIT_SIGNATURE)) {
        		// StringBuffer(CharSequence)
    			// add toString() before arg and call StringBuffer(String)
    			super.visitMethodInsn(INVOKEINTERFACE,
    					CHAR_SEQUENCE_CLASS,
    					TO_STRING_METHOD,
    					TO_STRING_SIGNATURE);
    			super.visitMethodInsn(INVOKESPECIAL,
    					JAVA_LANG_STRINGBUFFER,
    					INIT_METHOD,
    					JAVA_LANG_STRINGBUFFER_INIT_STRING_SIGNATURE);
    			return;
    		} else if (opcode == INVOKEVIRTUAL) {
    			if (name.equals(TRIM_TO_SIZE) && desc.equals(TRIM_TO_SIZE_SIGNATURE)) {
    				// do nothing: call is removed as from the 1.5 javadoc,
    				// there is no garantee the buffer capacity will be reduced to
    				// fit the actual size
    				super.visitInsn(POP);
    				return;
    			} else if (name.equals(APPEND_METHOD)) {
    				String d = desc.replaceAll("StringBuilder", "StringBuffer");
    				if (d.equals(APPEND_SIGNATURE1)) {
    	    			super.visitMethodInsn(INVOKEINTERFACE,
    	    					CHAR_SEQUENCE_CLASS,
    	    					TO_STRING_METHOD,
    	    					TO_STRING_SIGNATURE);
    	    			super.visitMethodInsn(INVOKEVIRTUAL,
    	    					JAVA_LANG_STRINGBUFFER,
    	    					APPEND_METHOD,
    	    					APPEND_SIGNATURE3);
    	    			return;
    				} else if (d.equals(APPEND_SIGNATURE2)) {
    	    			super.visitMethodInsn(INVOKEINTERFACE,
    	    					CHAR_SEQUENCE_CLASS,
    	    					SUB_SEQUENCE_METHOD,
    	    					SUB_SEQUENCE_SIGNATURE);
    	    			super.visitMethodInsn(INVOKEINTERFACE,
    	    					CHAR_SEQUENCE_CLASS,
    	    					TO_STRING_METHOD,
    	    					TO_STRING_SIGNATURE);
    	    			super.visitMethodInsn(INVOKEVIRTUAL,
    	    					JAVA_LANG_STRINGBUFFER,
    	    					APPEND_METHOD,
    	    					APPEND_SIGNATURE3);
    	    			return;    					
    				}
    			} else if (name.equals(INSERT_METHOD)) {
    				String d = desc.replaceAll("StringBuilder", "StringBuffer");
    				if (d.equals(INSERT_SIGNATURE1)) {
    	    			super.visitMethodInsn(INVOKEINTERFACE,
    	    					CHAR_SEQUENCE_CLASS,
    	    					TO_STRING_METHOD,
    	    					TO_STRING_SIGNATURE);
    	    			super.visitMethodInsn(INVOKEVIRTUAL,
    	    					JAVA_LANG_STRINGBUFFER,
    	    					INSERT_METHOD,
    	    					INSERT_SIGNATURE3);
    	    			return;
    				} else if (d.equals(INSERT_SIGNATURE2)) {
    	    			super.visitMethodInsn(INVOKEINTERFACE,
    	    					CHAR_SEQUENCE_CLASS,
    	    					SUB_SEQUENCE_METHOD,
    	    					SUB_SEQUENCE_SIGNATURE);
    	    			super.visitMethodInsn(INVOKEINTERFACE,
    	    					CHAR_SEQUENCE_CLASS,
    	    					TO_STRING_METHOD,
    	    					TO_STRING_SIGNATURE);
    	    			super.visitMethodInsn(INVOKEVIRTUAL,
    	    					JAVA_LANG_STRINGBUFFER,
    	    					INSERT_METHOD,
    	    					INSERT_SIGNATURE3);
    	    			return;    					

    				}
    			}
    		}
		} else if (opcode == INVOKESTATIC && owner.equals(JAVA_UTIL_COLLECTIONS)) {
			if (name.equals(EMPTY_LIST_METHOD) && desc.equals(EMPTY_LIST_SIGNATURE)) {
				super.visitFieldInsn(GETSTATIC, owner, EMPTY_LIST_FIELD, EMPTY_LIST_FIELD_DESC);
				return;
			} else if (name.equals(EMPTY_MAP_METHOD) && desc.equals(EMPTY_MAP_SIGNATURE)) {
				super.visitFieldInsn(GETSTATIC, owner, EMPTY_MAP_FIELD, EMPTY_MAP_FIELD_DESC);
				return;
			} else if (name.equals(EMPTY_SET_METHOD) && desc.equals(EMPTY_SET_SIGNATURE)) {
				super.visitFieldInsn(GETSTATIC, owner, EMPTY_SET_FIELD, EMPTY_SET_FIELD_DESC);
				return;
			}
		}

    	// Autoboxing
		else if (opcode == INVOKESTATIC && name.equals(VALUE_OF_METHOD)) {
    		String sig = boxSignatures.get(owner);		
    		if (sig != null && sig.equals(desc)) {
    			// owner and signature match
    			mv.visitMethodInsn(INVOKESTATIC,
    					AUTOBOX_CLASS,
    					VALUE_OF_METHOD,
    					boxSignatures.get(owner));

    			return;
    		}
    	}

    	// not a special case, use default implementation
    	super.visitMethodInsn(opcode, owner, name, desc);
	}


    public void visitLdcInsn(final Object cst) {
    	if (cst instanceof Type) {
    		/**
    		 * Fix class literals. The 1.5 VM has had its ldc* instructions updated so
    		 * that it knows how to deal with CONSTANT_Class in addition to the other
    		 * types. So, we have to search for uses of ldc* that point to a
    		 * CONSTANT_Class and replace them with synthetic field access the way
    		 * it was generated in 1.4.
    		 */

    		// LDC or LDC_W with a class as argument

    		Type t = (Type) cst;
    		String fieldName = getClassLiteralFieldName(t);

    		classLiteralCalls.add(fieldName);

    		mv.visitFieldInsn(GETSTATIC, className, fieldName, CLASS_FIELD_DESC);
    		Label nonNullLabel = new Label();
    		mv.visitJumpInsn(IFNONNULL, nonNullLabel);
    		String s;
    		if (t.getSort() == Type.OBJECT)
    			s = t.getInternalName();
    		else
    			s = t.getDescriptor();
    		
    		/* convert retroweaver runtime classes:
    		 * 		Enum into com.rc.retroweaver.runtime.Enum_
    		 *		concurrent classes into their backport equivalent
    		 *		...
    		 */
    		s = NameTranslator.translate(s);

    		mv.visitLdcInsn(s.replace('/', '.'));
    		if (isInterface) {
    			/* synthethic methods cannot be generated in interfaces so the byte
    			 * code has to be inlined for each call to ".class"
    			 */
    			generateClassCall(mv);
    		} else {
    			mv.visitMethodInsn(INVOKESTATIC, className, CLASS_METHOD, CLASS_SIGNATURE);
    		}
    		mv.visitInsn(DUP);
    		mv.visitFieldInsn(PUTSTATIC, className, fieldName, CLASS_FIELD_DESC);
    		Label endLabel = new Label();
    		mv.visitJumpInsn(GOTO, endLabel);
    		mv.visitLabel(nonNullLabel);
    		mv.visitFieldInsn(GETSTATIC, className, fieldName, CLASS_FIELD_DESC);
    		mv.visitLabel(endLabel);

    	} else {
    		super.visitLdcInsn(cst);
    	}
    }

    private String getClassLiteralFieldName(Type type) {
    	String fieldName;
    	if (type.getSort() == Type.ARRAY) {
    		fieldName = "array" + type.getDescriptor().replace('[', '$');
    		if (fieldName.charAt(fieldName.length()-1) == ';')
    			fieldName = fieldName.substring(0, fieldName.length()-1);
    	} else {
    		fieldName = "class$" + type.getInternalName();
    	}
    	fieldName = fieldName.replace('/', '$');

    	return fieldName;
    }

}

}

class DefaultWeaveListener implements WeaveListener {

	private boolean verbose;

	DefaultWeaveListener(boolean verbose) {
		this.verbose = verbose;
	}

	public void weavingStarted(String msg) {
		System.out.println("[RetroWeaver] " + msg);
	}

	public void weavingCompleted(String msg) {
		System.out.println("[RetroWeaver] " + msg);
	}

	public void weavingPath(String sourcePath) {
		if (verbose)
			System.out.println("[RetroWeaver] Weaving " + sourcePath);
	}
};

