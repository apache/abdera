package com.rc.retroweaver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.objectweb.asm.commons.EmptyVisitor;

/**
 * Applies the RetroWeaver against a set of classes.
 *
 */
public class Weaver {

	private static final String VERSION;

	private static final String BUILD_NUMBER;

	public static final String getVersion() {
		return VERSION + " (build " + BUILD_NUMBER + ')';
	}

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("retroweaver");
		VERSION = bundle.getString("retroweaver.version");
		BUILD_NUMBER = bundle.getString("retroweaver.buildNumber");
	}

	// Read the new class file format spec for how the version is computed.
	public static final int VERSION_1_5 = 49;

	public static final int VERSION_1_4 = 48;

	public static final int VERSION_1_3 = 47;

	public static final int VERSION_1_2 = 46;

	private static final String nl = System.getProperty("line.separator");

	public static void main(String[] args) {

		String source = null;
		String sourceJar = null;
		String destJar = null;
		int target = VERSION_1_4;
		int currentArg = 0;
		boolean lazy = false;
		boolean stripSignatures = false;
		boolean verbose = false;
		String verifyPath = null;

		while (currentArg < args.length) {
			String command = args[currentArg];
			++currentArg;

			if (command.equals("-source")) {
				source = args[currentArg++];
			} else if (command.equals("-jar")) {
				sourceJar = args[currentArg++];
				destJar = args[currentArg++];

				if (sourceJar.equals(destJar)) {
					System.out
							.println("source and destination jar files can not be identical");
					System.out.println();
					System.exit(1);
				}
			} else if (command.equals("-version")) {
				System.out.println("Retroweaver version " + getVersion());
				System.exit(0);
			} else if (command.equals("-target")) {
				String verStr = args[currentArg++];
				if (verStr.equals("1.4")) {
					target = VERSION_1_4;
				} else if (verStr.equals("1.3")) {
					target = VERSION_1_3;
				} else if (verStr.equals("1.2")) {
					target = VERSION_1_2;
				} else {
					System.out.println("Invalid target version: " + verStr);
					System.out.println();
					System.out.println(getUsage());
					System.exit(1);
				}
			} else if (command.equals("-lazy")) {
				lazy = true;
			} else if (command.equals("-stripSignatures")) {
				stripSignatures = true;
			} else if (command.equals("-verbose")) {
				verbose = true;
			} else if (command.equals("-verifyrefs")) {
				verifyPath = args[currentArg++];
			} else {
				System.out
						.println("I don't understand the command: " + command);
				System.out.println();
				System.out.println(getUsage());
				System.exit(1);
			}
		}

		if (source == null && sourceJar == null) {
			System.out.println("Option \"-source\" or \"-jar\" is required.");
			System.out.println();
			System.out.println(getUsage());
			System.exit(1);
		}

		if (source != null && sourceJar != null) {
			System.out
					.println("Only one of \"-source\" or \"-jar\" can be specified.");
			System.out.println();
			System.out.println(getUsage());
			System.exit(1);
		}

		File sourcePath = null;

		RetroWeaver weaver = new RetroWeaver(target);
		weaver.setListener(new DefaultWeaveListener(verbose));
		weaver.setLazy(lazy);
		weaver.setStripSignatures(stripSignatures);

		if (verifyPath != null) {
			List<String> paths = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(verifyPath,
					File.pathSeparator);
			while (st.hasMoreTokens()) {
				paths.add(st.nextToken());
			}
			RefVerifier rv = new RefVerifier(target, new EmptyVisitor(), paths,
					new DefaultRefVerifierListener(verbose));
			weaver.setVerifier(rv);
		}

		try {
			if (source != null) {
				sourcePath = new File(source);

				weaver.weave(sourcePath);
			} else {
				weaver.weaveJarFile(sourceJar, destJar);
			}
		} catch (Exception e) {
			throw new RetroWeaverException("Weaving failed", e);
		}
	}

	private static String getUsage() {
		return "Usage: Weaver <options>"
				+ nl
				+ " Options: "
				+ nl
				+ " -source <source dir>"
				+ nl
				+ " -jar <source jar> <target jar>"
				+ nl
				+ " -target <target VM version> (one of {1.4, 1.3, 1.2}, default is 1.4)"
				+ nl + " -verifyrefs <classpath>" + nl
				+ " -stripSignatures (strip generic signatures, off by default)" + nl
				+ " -verbose (message for each processed class)" + nl
				+ " -version (display version number and exit)" + nl + nl
				+ "One of \"-source\" or \"-jar\" is required.";
	}

}
