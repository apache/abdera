/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera.i18n.text.data;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * Tool for parsing the Unicode Character Database file format and generating the constants for the
 * UnicodeCharacterDatabase file.
 */
public class Generator {

    public static void main(String... args) {
        PrintWriter pw = new PrintWriter(System.out);
        BitSet exclusions = getExclusions(args[0]);
        writeDecomposition(pw, args[1], exclusions);
    }

    private static void writeDecomposition(PrintWriter pw, String file, BitSet excluded) {
        Scanner s = read(file);

        BitSet compat = new BitSet();

        List<Integer> cc_idx = new ArrayList<Integer>();
        List<Integer> cc_data = new ArrayList<Integer>();

        List<Integer> decomp_idx = new ArrayList<Integer>();
        List<Integer[]> decomp_data = new ArrayList<Integer[]>();

        List<Integer[]> comps = new ArrayList<Integer[]>();

        List<Integer[]> hanguls = new ArrayList<Integer[]>();

        while (s.hasNextLine() && s.hasNext()) {
            if (s.findInLine("([^;\\s]*);[^;]*;[^;]*;([^;]*);[^;]*;([^;]*);.*") != null) {
                MatchResult result = s.match();
                int codepoint = Integer.parseInt(result.group(1), 16);
                int cc = Integer.parseInt(result.group(2));
                if (cc != 0) {
                    cc_idx.add(codepoint);
                    cc_data.add(cc);
                }
                String dc = result.group(3).trim();
                if (dc.length() > 0) {
                    if (dc.charAt(0) == '<')
                        compat.set(codepoint);
                    dc = dc.substring(dc.indexOf('>') + 1).trim();
                    String[] points = dc.split("\\s");
                    List<Integer> list = new ArrayList<Integer>();
                    for (int n = 0; n < points.length; n++)
                        list.add(Integer.parseInt(points[n], 16));
                    decomp_idx.add(codepoint);
                    decomp_data.add(list.toArray(new Integer[list.size()]));

                    if (!compat.get(codepoint) && !excluded.get(codepoint)) {
                        char f = (list.size() > 1) ? (char)list.get(0).intValue() : '\u0000';
                        char l = (list.size() > 1) ? (char)list.get(1).intValue() : (char)list.get(0).intValue();
                        comps.add(new Integer[] {(f << 16) | l, codepoint});
                    }

                }
            }
        }

        // Hanguls
        for (int z = 0; z < 0x2BA4; ++z) {
            int t = z % 0x001C;
            char f = (t != 0) ? (char)(0xAC00 + z - t) : (char)(0x1100 + z / 0x024C);
            char e = (t != 0) ? (char)(0x11A7 + t) : (char)(0x1161 + (z % 0x024C) / 0x001C);
            int pair = (f << 16) | e;
            int value = z + 0xAC00;
            hanguls.add(new Integer[] {pair, value});
        }

        Comparator<Integer[]> comp = new Comparator<Integer[]>() {
            public int compare(Integer[] o1, Integer[] o2) {
                int i1 = o1[0];
                int i2 = o2[0];
                return i1 < i2 ? -1 : i1 > i2 ? 1 : 0;
            }
        };
        Collections.sort(comps, comp);
        Collections.sort(hanguls, comp);

        pw.print("  private static int[] getCompat() { return new int[] {");
        int i = compat.nextSetBit(0), n = 0;
        pw.print(i);
        for (i = compat.nextSetBit(i); i >= 0; i = compat.nextSetBit(i + 1), n++) {
            pw.print(',');
            pw.print(i);
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
        }
        pw.print("};}\n\n");
        pw.flush();

        pw.print("  private static int[] getCCIdx() { return new int[] {");
        for (i = 0, n = 0; i < cc_idx.size(); i++, n++) {
            pw.print(cc_idx.get(i));
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
            if (i < cc_idx.size() - 1)
                pw.print(',');
        }
        pw.print("};}\n\n");
        pw.flush();

        pw.print("  private static int[] getCCData() { return new int[] {");
        for (i = 0, n = 0; i < cc_data.size(); i++, n++) {
            pw.print(cc_data.get(i));
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
            if (i < cc_data.size() - 1)
                pw.print(',');
        }
        pw.print("};}\n\n");
        pw.flush();

        pw.print("  private static int[] getComposeIdx() { return new int[] {");
        for (i = 0, n = 0; i < comps.size(); i++, n++) {
            pw.print(comps.get(i)[0]);
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
            if (i < comps.size() - 1)
                pw.print(',');
        }
        pw.print("};}\n\n");
        pw.flush();

        pw.print("  private static int[] getComposeData() { return new int[] {");
        for (i = 0, n = 0; i < comps.size(); i++, n++) {
            pw.print(comps.get(i)[1]);
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
            if (i < comps.size() - 1)
                pw.print(',');
        }
        pw.print("};}\n\n");
        pw.flush();

        pw.print("  private static int[] getDecompIdx() { return new int[] {");
        for (i = 0, n = 0; i < decomp_idx.size(); i++, n++) {
            pw.print(decomp_idx.get(i));
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
            if (i < decomp_idx.size() - 1)
                pw.print(',');
        }
        pw.print("};}\n\n");

        int sets = 2;
        int size = decomp_idx.size() / sets;
        i = 0;
        for (int a = 0; a < sets; a++) {
            pw.print("  private static int[][] getDecompData" + (a + 1) + "() { return new int[][] {");
            for (i = a * i, n = 0; i < size * (a + 1); i++, n++) {
                Integer[] data = decomp_data.get(i);
                pw.print('{');
                for (int q = 0; q < data.length; q++) {
                    pw.print(data[q]);
                    if (q < data.length - 1)
                        pw.print(',');
                }
                pw.print('}');
                if (n % 20 == 0) {
                    pw.print("\n    ");
                    n = 0;
                }
                if (i < decomp_idx.size() - 1)
                    pw.print(',');
            }
            pw.print("};}\n\n");
        }

        pw.println("  private static int[][] getDecompData() {");
        for (n = 0; n < sets; n++)
            pw.println("    int[][] d" + (n + 1) + " = getDecompData" + (n + 1) + "();");

        pw.print("    int[][] d = new int[");
        for (n = 0; n < sets; n++) {
            pw.print("d" + (n + 1) + ".length");
            if (n < sets - 1)
                pw.print('+');
        }
        pw.println("][];");

        String len = "0";
        for (n = 0; n < sets; n++) {
            pw.println("    System.arraycopy(d" + (n + 1) + ",0,d," + len + ",d" + (n + 1) + ".length);");
            len = "d" + (n + 1) + ".length";
        }
        pw.println("    return d;}");

        pw.flush();

        sets = 2;
        i = 0;
        int e = 0;
        size = hanguls.size() / sets;
        for (int a = 0; a < sets; a++) {
            pw.print("  private static int[] getHangulPairs" + (a + 1) + "() { return new int[] {");
            for (i = a * i, n = 0; i < size * (a + 1); i++, n++) {
                pw.print(hanguls.get(i)[0]);
                if (n % 20 == 0) {
                    pw.print("\n    ");
                    n = 0;
                }
                if (i < hanguls.size() - 1)
                    pw.print(',');
            }
            pw.print("};}\n\n");
            pw.flush();

            pw.print("  private static int[] getHangulCodepoints" + (a + 1) + "() { return new int[] {");
            for (e = a * e, n = 0; e < size * (a + 1); e++, n++) {
                pw.print(hanguls.get(e)[1]);
                if (n % 20 == 0) {
                    pw.print("\n    ");
                    n = 0;
                }
                if (e < hanguls.size() - 1)
                    pw.print(',');
            }
            pw.print("};}\n\n");
            pw.flush();

        }

        pw.println("  private static int[] getHangulPairs() {");
        for (n = 0; n < sets; n++)
            pw.println("    int[] d" + (n + 1) + " = getHangulPairs" + (n + 1) + "();");

        pw.print("    int[] d = new int[");
        for (n = 0; n < sets; n++) {
            pw.print("d" + (n + 1) + ".length");
            if (n < sets - 1)
                pw.print('+');
        }
        pw.println("];");

        len = "0";
        for (n = 0; n < sets; n++) {
            pw.println("    System.arraycopy(d" + (n + 1) + ",0,d," + len + ",d" + (n + 1) + ".length);");
            len = "d" + (n + 1) + ".length";
        }
        pw.println("    return d;}");

        pw.flush();

        pw.println("  private static int[] getHangulCodepoints() {");
        for (n = 0; n < sets; n++)
            pw.println("    int[] d" + (n + 1) + " = getHangulCodepoints" + (n + 1) + "();");

        pw.print("    int[] d = new int[");
        for (n = 0; n < sets; n++) {
            pw.print("d" + (n + 1) + ".length");
            if (n < sets - 1)
                pw.print('+');
        }
        pw.println("];");

        len = "0";
        for (n = 0; n < sets; n++) {
            pw.println("    System.arraycopy(d" + (n + 1) + ",0,d," + len + ",d" + (n + 1) + ".length);");
            len = "d" + (n + 1) + ".length";
        }
        pw.println("    return d;}\n\n");

        pw.flush();
    }

    private static BitSet getExclusions(String file) {
        Scanner s = read(file).useDelimiter("\\s*#.*");
        BitSet set = new BitSet();
        while (s.hasNext()) {
            String exc = s.next().trim();
            if (exc.length() > 0) {
                int i = Integer.parseInt(exc, 16);
                set.set(i);
            }
        }
        return set;
    }

    private static Scanner read(String f) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream in = cl.getResourceAsStream(f);
        if (in == null) {
            try {
                in = new FileInputStream(f);
            } catch (Exception e) {
            }
        }
        if (in == null) {
            try {
                URL url = new URL(f);
                in = url.openStream();
            } catch (Exception e) {
            }
        }
        return in != null ? new Scanner(in) : null;
    }

}
