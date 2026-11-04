package reloc.net.minecraftforge.srgutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class InternalUtils {
   private static final List<String> ORDER = Arrays.asList("PK:", "CL:", "FD:", "MD:");

   static IMappingFile load(InputStream in) throws IOException {
      INamedMappingFile named = loadNamed(in);
      return named.getMap(named.getNames().get(0), named.getNames().get(1));
   }

   static INamedMappingFile loadNamed(InputStream in) throws IOException {
      List<String> lines = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines().filter(l -> !l.isEmpty()).collect(Collectors.toList());
      if (lines.isEmpty()) {
         return IMappingBuilder.create().build();
      }

      String firstLine = lines.get(0);
      Iterator<String> itr = lines.iterator();

      while (stripComment(firstLine).isEmpty() && itr.hasNext()) {
         firstLine = itr.next();
      }

      String test = firstLine.split(" ")[0];
      if ("PK:".equals(test) || "CL:".equals(test) || "FD:".equals(test) || "MD:".equals(test)) {
         return loadSRG(filter(lines)).build();
      } else if (firstLine.contains(" -> ")) {
         return loadProguard(filter(lines)).build();
      } else if (firstLine.startsWith("v1\t")) {
         return loadTinyV1(lines).build();
      } else if (firstLine.startsWith("tiny\t")) {
         return loadTinyV2(lines).build();
      } else {
         return firstLine.startsWith("tsrg2 ") ? loadTSrg2(filter(lines)).build() : loadSlimSRG(filter(lines)).build();
      }
   }

   private static List<String> filter(List<String> lines) {
      return lines.stream().map(InternalUtils::stripComment).filter(l -> !l.isEmpty()).collect(Collectors.toList());
   }

   private static IMappingBuilder loadSRG(List<String> lines) throws IOException {
      IMappingBuilder ret = IMappingBuilder.create("left", "right");
      Map<String, IMappingBuilder.IClass> classes = new HashMap<>();

      for (String line : lines) {
         String[] pts = line.split(" ");
         switch (pts[0]) {
            case "PK:":
               ret.addPackage(pts[1], pts[2]);
               break;
            case "CL:":
               classes.put(pts[1], ret.addClass(pts[1], pts[2]));
               break;
            case "FD:":
               if (pts.length == 5) {
                  String[] leftx = rsplit(pts[1], '/', 1);
                  String[] rightx = rsplit(pts[3], '/', 1);
                  classes.computeIfAbsent(leftx[0], k -> ret.addClass(left[0], right[0])).field(leftx[1], rightx[1]).descriptor(pts[2]);
               } else {
                  String[] leftx = rsplit(pts[1], '/', 1);
                  String[] rightx = rsplit(pts[2], '/', 1);
                  classes.computeIfAbsent(leftx[0], k -> ret.addClass(left[0], right[0])).field(leftx[1], rightx[1]);
               }
               break;
            case "MD:":
               String[] left = rsplit(pts[1], '/', 1);
               String[] right = rsplit(pts[3], '/', 1);
               classes.computeIfAbsent(left[0], k -> ret.addClass(left[0], right[0])).method(pts[2], left[1], right[1]);
               break;
            default:
               throw new IOException("Invalid SRG file, Unknown type: " + line);
         }
      }

      return ret;
   }

   private static IMappingBuilder loadProguard(List<String> lines) throws IOException {
      IMappingBuilder ret = IMappingBuilder.create("left", "right");
      IMappingBuilder.IClass cls = null;

      for (String line : lines) {
         line = line.replace('.', '/');
         if (!line.startsWith("    ") && line.endsWith(":")) {
            String[] pts = line.replace('.', '/').split(" -> ");
            cls = ret.addClass(pts[0], pts[1].substring(0, pts[1].length() - 1));
         } else if (line.contains("(") && line.contains(")")) {
            if (cls == null) {
               throw new IOException("Invalid PG line, missing class: " + line);
            }

            line = line.trim();
            int start = 0;
            int end = 0;
            if (line.indexOf(58) != -1) {
               int i = line.indexOf(58);
               int j = line.indexOf(58, i + 1);
               start = Integer.parseInt(line.substring(0, i));
               end = Integer.parseInt(line.substring(i + 1, j));
               line = line.substring(j + 1);
            }

            String obf = line.split(" -> ")[1];
            String _ret = toDesc(line.split(" ")[0]);
            String name = line.substring(line.indexOf(32) + 1, line.indexOf(40));
            String[] args = line.substring(line.indexOf(40) + 1, line.indexOf(41)).split(",");
            StringBuffer desc = new StringBuffer();
            desc.append('(');
            String[] mtd = args;
            int var13 = mtd.length;
            int var14 = 0;

            while (true) {
               if (var14 < var13) {
                  String arg = mtd[var14];
                  if (!arg.isEmpty()) {
                     desc.append(toDesc(arg));
                     var14++;
                     continue;
                  }
               }

               desc.append(')').append(_ret);
               IMappingBuilder.IMethod mtdx = cls.method(desc.toString(), name, obf);
               if (start != 0) {
                  mtdx.meta("start_line", Integer.toString(start));
               }

               if (end != 0) {
                  mtdx.meta("end_line", Integer.toString(end));
               }
               break;
            }
         } else {
            if (cls == null) {
               throw new IOException("Invalid PG line, missing class: " + line);
            }

            String[] pts = line.trim().split(" ");
            cls.field(pts[1], pts[3]).descriptor(toDesc(pts[0]));
         }
      }

      return ret;
   }

   private static IMappingBuilder loadSlimSRG(List<String> lines) throws IOException {
      IMappingBuilder ret = IMappingBuilder.create("left", "right");
      Map<String, IMappingBuilder.IClass> classes = new HashMap<>();
      lines.stream().filter(l -> l.charAt(0) != '\t').map(l -> l.split(" ")).filter(ptsx -> ptsx.length == 2).forEach(ptsx -> {
         if (ptsx[0].endsWith("/")) {
            ret.addPackage(ptsx[0].substring(0, ptsx[0].length() - 1), ptsx[1].substring(0, ptsx[1].length() - 1));
         } else {
            classes.put(ptsx[0], ret.addClass(ptsx[0], ptsx[1]));
         }
      });
      IMappingBuilder.IClass cls = null;

      for (String line : lines) {
         String[] pts = line.split(" ");
         if (pts[0].charAt(0) == '\t') {
            if (cls == null) {
               throw new IOException("Invalid TSRG line, missing class: " + line);
            }

            pts[0] = pts[0].substring(1);
            if (pts.length == 2) {
               cls.field(pts[0], pts[1]);
            } else {
               if (pts.length != 3) {
                  throw new IOException("Invalid TSRG line, to many parts: " + line);
               }

               cls.method(pts[1], pts[0], pts[2]);
            }
         } else if (pts.length == 2) {
            if (!pts[0].endsWith("/")) {
               cls = classes.get(pts[0]);
            }
         } else if (pts.length == 3) {
            classes.computeIfAbsent(pts[0], k -> ret.addClass(k, k)).field(pts[1], pts[2]);
         } else {
            if (pts.length != 4) {
               throw new IOException("Invalid CSRG line, to many parts: " + line);
            }

            classes.computeIfAbsent(pts[0], k -> ret.addClass(k, k)).method(pts[2], pts[1], pts[3]);
         }
      }

      return ret;
   }

   private static IMappingBuilder loadTSrg2(List<String> lines) throws IOException {
      String[] header = lines.get(0).split(" ");
      if (header.length < 3) {
         throw new IOException("Invalid TSrg v2 Header: " + lines.get(0));
      }

      IMappingBuilder ret = IMappingBuilder.create(Arrays.copyOfRange(header, 1, header.length));
      int nameCount = header.length - 1;
      lines.remove(0);
      IMappingBuilder.IClass cls = null;
      IMappingBuilder.IMethod mtd = null;

      for (String line : lines) {
         if (line.length() < 2) {
            throw new IOException("Invalid TSRG v2 line, too short: " + line);
         }

         String[] pts = line.split(" ");
         if (line.charAt(0) != '\t') {
            if (pts.length != nameCount) {
               throw new IOException("Invalid TSRG v2 line: " + line);
            }

            if (pts[0].charAt(pts[0].length() - 1) != '/') {
               cls = ret.addClass(pts);
            } else {
               for (int x = 0; x < pts.length; x++) {
                  pts[x] = pts[x].substring(0, pts[x].length() - 1);
               }

               ret.addPackage(pts);
               cls = null;
            }

            mtd = null;
         } else if (line.charAt(1) == '\t') {
            if (mtd == null) {
               throw new IOException("Invalid TSRG v2 line, missing method: " + line);
            }

            pts[0] = pts[0].substring(2);
            if (pts.length == 1 && pts[0].equals("static")) {
               mtd.meta("is_static", "true");
            } else {
               if (pts.length != nameCount + 1) {
                  throw new IOException("Invalid TSRG v2 line, too many parts: " + line);
               }

               mtd.parameter(Integer.parseInt(pts[0]), Arrays.copyOfRange(pts, 1, pts.length));
            }
         } else {
            if (cls == null) {
               throw new IOException("Invalid TSRG v2 line, missing class: " + line);
            }

            pts[0] = pts[0].substring(1);
            if (pts.length == nameCount) {
               cls.field(pts);
            } else {
               if (pts.length != 1 + nameCount) {
                  throw new IOException("Invalid TSRG v2 line, to many parts: " + line);
               }

               swapFirst(pts);
               if (pts[0].charAt(0) == '(') {
                  mtd = cls.method(pts[0], Arrays.copyOfRange(pts, 1, pts.length));
               } else {
                  mtd = null;
                  cls.field(Arrays.copyOfRange(pts, 1, pts.length)).descriptor(pts[0]);
               }
            }
         }
      }

      return ret;
   }

   private static IMappingBuilder loadTinyV1(List<String> lines) throws IOException {
      String[] header = lines.get(0).split("\t");
      if (header.length < 3) {
         throw new IOException("Invalid Tiny v1 Header: " + lines.get(0));
      }

      IMappingBuilder ret = IMappingBuilder.create(Arrays.copyOfRange(header, 1, header.length));
      Map<String, IMappingBuilder.IClass> classes = new HashMap<>();
      int nameCount = header.length - 1;

      for (int x = 1; x < lines.size(); x++) {
         String[] line = lines.get(x).split("\t");
         if (!line[0].startsWith("#")) {
            switch (line[0]) {
               case "CLASS":
                  if (line.length != nameCount + 1) {
                     throw new IOException("Invalid Tiny v1 line: #" + x + ": " + line);
                  }

                  classes.put(line[1], ret.addClass(Arrays.copyOfRange(line, 1, line.length)));
                  break;
               case "FIELD":
                  if (line.length != nameCount + 3) {
                     throw new IOException("Invalid Tiny v1 line: #" + x + ": " + line);
                  }

                  classes.computeIfAbsent(line[1], k -> ret.addClass(duplicate(k, nameCount)))
                     .field(Arrays.copyOfRange(line, 3, line.length))
                     .descriptor(line[2]);
                  break;
               case "METHOD":
                  if (line.length != nameCount + 3) {
                     throw new IOException("Invalid Tiny v1 line: #" + x + ": " + line);
                  }

                  classes.computeIfAbsent(line[1], k -> ret.addClass(duplicate(k, nameCount))).method(line[2], Arrays.copyOfRange(line, 3, line.length));
                  break;
               default:
                  throw new IOException("Invalid Tiny v1 line: #" + x + ": " + line);
            }
         }
      }

      return ret;
   }

   private static IMappingBuilder loadTinyV2(List<String> lines) throws IOException {
      String[] header = lines.get(0).split("\t");
      if (header.length < 5) {
         throw new IOException("Invalid Tiny v2 Header: " + lines.get(0));
      }

      try {
         int major = Integer.parseInt(header[1]);
         int minor = Integer.parseInt(header[2]);
         if (major != 2 || minor != 0) {
            throw new IOException("Unsupported Tiny v2 version: " + lines.get(0));
         }
      } catch (NumberFormatException e) {
         throw new IOException("Invalid Tiny v2 Header: " + lines.get(0));
      }

      IMappingBuilder ret = IMappingBuilder.create(Arrays.copyOfRange(header, 3, header.length));
      int nameCount = header.length - 3;
      boolean escaped = false;
      Map<String, String> properties = new HashMap<>();
      int start = 1;

      for (start = 1; start < lines.size(); start++) {
         String[] line = lines.get(start).split("\t");
         if (!line[0].isEmpty()) {
            break;
         }

         properties.put(line[1], line.length < 3 ? null : (escaped ? unescapeTinyString(line[2]) : line[2]));
         if ("escaped-names".equals(line[1])) {
            escaped = true;
         }
      }

      Deque<InternalUtils.TinyV2State> stack = new ArrayDeque<>();
      IMappingBuilder.IClass cls = null;
      IMappingBuilder.IField field = null;
      IMappingBuilder.IMethod method = null;
      IMappingBuilder.IParameter param = null;

      for (int x = start; x < lines.size(); x++) {
         String line = lines.get(x);
         int newdepth = 0;

         while (line.charAt(newdepth) == '\t') {
            newdepth++;
         }

         if (newdepth != 0) {
            line = line.substring(newdepth);
         }

         if (newdepth != stack.size()) {
            while (stack.size() != newdepth) {
               switch ((InternalUtils.TinyV2State)stack.pop()) {
                  case CLASS:
                     cls = null;
                     break;
                  case FIELD:
                     field = null;
                     break;
                  case METHOD:
                     method = null;
                     break;
                  case PARAMETER:
                     param = null;
               }
            }
         }

         String[] parts = line.split("\t");
         if (escaped) {
            for (int y = 1; y < parts.length; y++) {
               parts[y] = unescapeTinyString(parts[y]);
            }
         }

         switch (parts[0]) {
            case "c":
               if (stack.size() == 0) {
                  if (parts.length != nameCount + 1) {
                     throw tiny2Exception(x, line);
                  }

                  cls = ret.addClass(Arrays.copyOfRange(parts, 1, parts.length));
                  stack.push(InternalUtils.TinyV2State.CLASS);
                  break;
               } else {
                  String comment = unescapeTinyString(parts[1]);
                  switch ((InternalUtils.TinyV2State)stack.peek()) {
                     case CLASS:
                        if (cls == null) {
                           throw tiny2Exception(x, line);
                        }

                        cls.meta("comment", comment);
                        continue;
                     case FIELD:
                        if (field == null) {
                           throw tiny2Exception(x, line);
                        }

                        field.meta("comment", comment);
                        continue;
                     case METHOD:
                        if (method == null) {
                           throw tiny2Exception(x, line);
                        }

                        method.meta("comment", comment);
                        continue;
                     case PARAMETER:
                        if (param == null) {
                           throw tiny2Exception(x, line);
                        }

                        param.meta("comment", comment);
                        continue;
                     default:
                        throw tiny2Exception(x, line);
                  }
               }
            case "f":
               if (parts.length != nameCount + 2 || stack.peek() != InternalUtils.TinyV2State.CLASS) {
                  throw tiny2Exception(x, line);
               }

               field = cls.field(Arrays.copyOfRange(parts, 2, parts.length)).descriptor(parts[1]);
               stack.push(InternalUtils.TinyV2State.FIELD);
               break;
            case "m":
               if (parts.length != nameCount + 2 || stack.peek() != InternalUtils.TinyV2State.CLASS) {
                  throw tiny2Exception(x, line);
               }

               method = cls.method(parts[1], Arrays.copyOfRange(parts, 2, parts.length));
               stack.push(InternalUtils.TinyV2State.METHOD);
               break;
            case "p":
               if (parts.length != nameCount + 2 || stack.peek() != InternalUtils.TinyV2State.METHOD) {
                  throw tiny2Exception(x, line);
               }

               param = method.parameter(Integer.parseInt(parts[1]), Arrays.copyOfRange(parts, 2, parts.length));
               stack.push(InternalUtils.TinyV2State.PARAMETER);
            case "v":
               break;
            default:
               throw tiny2Exception(x, line);
         }
      }

      return ret;
   }

   private static IOException tiny2Exception(int line, String data) {
      return new IOException("Invalid Tiny v2 line: #" + line + ": " + data);
   }

   private static String unescapeTinyString(String value) {
      return value.replace("\\\\", "\\").replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t").replace("\\0", "\u0000");
   }

   static String escapeTinyString(String value) {
      return value.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t").replace("\u0000", "\\0");
   }

   static String toDesc(String type) {
      if (type.endsWith("[]")) {
         return "[" + toDesc(type.substring(0, type.length() - 2));
      } else if (type.equals("int")) {
         return "I";
      } else if (type.equals("void")) {
         return "V";
      } else if (type.equals("boolean")) {
         return "Z";
      } else if (type.equals("byte")) {
         return "B";
      } else if (type.equals("char")) {
         return "C";
      } else if (type.equals("short")) {
         return "S";
      } else if (type.equals("double")) {
         return "D";
      } else if (type.equals("float")) {
         return "F";
      } else if (type.equals("long")) {
         return "J";
      } else if (type.contains("/")) {
         return "L" + type + ";";
      } else {
         throw new RuntimeException("Invalid toDesc input: " + type);
      }
   }

   static String toSource(String desc) {
      char first = desc.charAt(0);
      switch (first) {
         case 'B':
            return "byte";
         case 'C':
            return "char";
         case 'D':
            return "double";
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'W':
         case 'X':
         case 'Y':
         default:
            throw new IllegalArgumentException("Unknown descriptor: " + desc);
         case 'F':
            return "float";
         case 'I':
            return "int";
         case 'J':
            return "long";
         case 'L':
            return desc.substring(1, desc.length() - 1).replace('/', '.');
         case 'S':
            return "short";
         case 'V':
            return "void";
         case 'Z':
            return "boolean";
         case '[':
            return toSource(desc.substring(1)) + "[]";
      }
   }

   static String toSource(String name, String desc) {
      StringBuilder buf = new StringBuilder();
      int endParams = desc.lastIndexOf(41);
      String ret = desc.substring(endParams + 1);
      buf.append(toSource(ret)).append(' ').append(name).append('(');
      int idx = 1;

      while (idx < endParams) {
         int array = 0;
         char c = desc.charAt(idx);
         if (c == '[') {
            while (desc.charAt(idx) == '[') {
               array++;
               idx++;
            }

            c = desc.charAt(idx);
         }

         if (c == 'L') {
            int end = desc.indexOf(59, idx);
            buf.append(toSource(desc.substring(idx, end + 1)));
            idx = end;
         } else {
            buf.append(toSource(c + ""));
         }

         while (array-- > 0) {
            buf.append("[]");
         }

         if (++idx < endParams) {
            buf.append(',');
         }
      }

      buf.append(')');
      return buf.toString();
   }

   private static String[] rsplit(String str, char chr, int count) {
      List<String> pts = new ArrayList<>();

      int idx;
      while ((idx = str.lastIndexOf(chr)) != -1 && count > 0) {
         pts.add(str.substring(idx + 1));
         str = str.substring(0, idx);
         count--;
      }

      pts.add(str);
      Collections.reverse(pts);
      return pts.toArray(new String[pts.size()]);
   }

   public static int compareLines(String o1, String o2) {
      String[] pt1 = o1.split(" ");
      String[] pt2 = o2.split(" ");
      if (!pt1[0].equals(pt2[0])) {
         return ORDER.indexOf(pt1[0]) - ORDER.lastIndexOf(pt2[0]);
      }

      if ("PK:".equals(pt1[0])) {
         return o1.compareTo(o2);
      }

      if ("CL:".equals(pt1[0])) {
         return compareCls(pt1[1], pt2[1]);
      }

      if (!"FD:".equals(pt1[0]) && !"MD:".equals(pt1[0])) {
         return o1.compareTo(o2);
      }

      String[][] y = new String[][]{
         {pt1[1].substring(0, pt1[1].lastIndexOf(47)), pt1[1].substring(pt1[1].lastIndexOf(47) + 1)},
         {pt2[1].substring(0, pt2[1].lastIndexOf(47)), pt2[1].substring(pt2[1].lastIndexOf(47) + 1)}
      };
      int ret = compareCls(y[0][0], y[1][0]);
      return ret != 0 ? ret : y[0][1].compareTo(y[1][1]);
   }

   public static int compareCls(String cls1, String cls2) {
      if (cls1.indexOf(47) > 0 && cls2.indexOf(47) > 0) {
         return cls1.compareTo(cls2);
      }

      String[][] t = new String[][]{cls1.split("\\$"), cls2.split("\\$")};
      int max = Math.min(t[0].length, t[1].length);

      for (int i = 0; i < max; i++) {
         if (!t[0][i].equals(t[1][i])) {
            if (t[0][i].length() != t[1][i].length()) {
               return t[0][i].length() - t[1][i].length();
            }

            return t[0][i].compareTo(t[1][i]);
         }
      }

      return Integer.compare(t[0].length, t[1].length);
   }

   public static String stripComment(String str) {
      int idx = str.indexOf(35);
      if (idx == 0) {
         return "";
      }

      if (idx != -1) {
         str = str.substring(0, idx - 1);
      }

      int end = str.length();

      while (end > 1 && str.charAt(end - 1) == ' ') {
         end--;
      }

      return end == 0 ? "" : str.substring(0, end);
   }

   private static void swapFirst(String[] values) {
      String tmp = values[0];
      values[0] = values[1];
      values[1] = tmp;
   }

   private static String[] duplicate(String value, int count) {
      String[] ret = new String[count];
      Arrays.fill(ret, value);
      return ret;
   }

   static void writeMeta(IMappingFile.Format format, List<String> lines, InternalUtils.Element element, Map<String, String> meta) {
      int indent = 0;
      switch (element) {
         case PACKAGE:
         case CLASS:
            indent = 1;
            break;
         case FIELD:
         case METHOD:
            indent = 2;
            break;
         case PARAMETER:
            indent = 3;
      }

      switch (format) {
         case CSRG:
         case PG:
         case SRG:
         case TINY1:
         case TSRG:
         case XSRG:
         default:
            break;
         case TINY:
            String comment = meta.get("comment");
            if (comment != null) {
               char[] prefix = new char[indent];
               Arrays.fill(prefix, '\t');
               lines.add(new String(prefix) + "c\t" + escapeTinyString(comment));
            }
            break;
         case TSRG2:
            if (meta.containsKey("is_static")) {
               char[] prefix = new char[indent];
               Arrays.fill(prefix, '\t');
               lines.add(new String(prefix) + "static");
            }
      }
   }

   enum Element {
      PACKAGE,
      CLASS,
      FIELD,
      METHOD,
      PARAMETER;
   }

   enum TinyV2State {
      ROOT,
      CLASS,
      FIELD,
      METHOD,
      PARAMETER;
   }
}
