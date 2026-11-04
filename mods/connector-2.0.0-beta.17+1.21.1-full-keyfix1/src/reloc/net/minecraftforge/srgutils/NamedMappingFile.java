package reloc.net.minecraftforge.srgutils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import javax.annotation.Nullable;

class NamedMappingFile implements INamedMappingFile, IMappingBuilder {
   private final List<String> names;
   private Map<String, NamedMappingFile.Package> packages = new HashMap<>();
   private Map<String, NamedMappingFile.Cls> classes = new HashMap<>();
   private Map<String, String[]> classCache = new ConcurrentHashMap<>();
   private Map<String, IMappingFile> mapCache = new ConcurrentHashMap<>();

   NamedMappingFile(String... names) {
      if (names != null && names.length >= 2) {
         this.names = Collections.unmodifiableList(Arrays.asList(names));
      } else {
         throw new IllegalArgumentException("Can not create Mapping file with less then two names");
      }
   }

   private void ensureCount(String... names) {
      if (names == null) {
         throw new IllegalArgumentException("Names can not be null");
      }

      if (names.length != this.names.size()) {
         throw new IllegalArgumentException("Invalid number of names, expected " + this.names.size() + " got " + names.length);
      }
   }

   @Override
   public List<String> getNames() {
      return this.names;
   }

   @Override
   public IMappingFile getMap(String from, String to) {
      String key = from + "_to_" + to;
      return this.mapCache.computeIfAbsent(key, k -> {
         int fromI = this.names.indexOf(from);
         int toI = this.names.indexOf(to);
         if (fromI != -1 && toI != -1) {
            return new MappingFile(this, fromI, toI);
         } else {
            throw new IllegalArgumentException("Could not find mapping names: " + from + " / " + to);
         }
      });
   }

   @Override
   public void write(Path path, IMappingFile.Format format, String... order) throws IOException {
      if (order == null || order.length == 1) {
         throw new IllegalArgumentException("Invalid order, you must specify atleast 2 names");
      }

      if (!format.hasNames() && order.length > 2) {
         throw new IllegalArgumentException("Can not write " + order + " in " + format.name() + " format, it does not support headers");
      }

      int[] indexes = new int[order.length];

      for (int x = 0; x < order.length; x++) {
         indexes[x] = this.getNames().indexOf(order[x]);
         if (indexes[x] == -1) {
            throw new IllegalArgumentException("Invalid order: Missing \"" + order[x] + "\" name");
         }
      }

      List<String> lines = new ArrayList<>();
      Comparator<NamedMappingFile.Named> sort = (a, b) -> a.getName(indexes[0]).compareTo(b.getName(indexes[0]));
      this.getPackages().sorted(sort).forEachOrdered(pkg -> {
         lines.add(pkg.write(format, indexes));
         InternalUtils.writeMeta(format, lines, InternalUtils.Element.PACKAGE, pkg.meta);
      });
      this.getClasses().sorted(sort).forEachOrdered(cls -> {
         lines.add(cls.write(format, indexes));
         InternalUtils.writeMeta(format, lines, InternalUtils.Element.CLASS, cls.meta);
         cls.getFields().sorted(sort).forEachOrdered(fld -> {
            lines.add(fld.write(format, indexes));
            InternalUtils.writeMeta(format, lines, InternalUtils.Element.FIELD, fld.meta);
         });
         cls.getMethods().sorted(sort).forEachOrdered(mtd -> {
            lines.add(mtd.write(format, indexes));
            InternalUtils.writeMeta(format, lines, InternalUtils.Element.METHOD, mtd.meta);
            mtd.getParameters().sorted((a, b) -> a.getIndex() - b.getIndex()).forEachOrdered(par -> {
               lines.add(par.write(format, indexes));
               InternalUtils.writeMeta(format, lines, InternalUtils.Element.PARAMETER, par.meta);
            });
         });
      });
      lines.removeIf(e -> e == null);
      if (!format.isOrdered()) {
         Comparator<String> linesort = format != IMappingFile.Format.SRG && format != IMappingFile.Format.XSRG
            ? (o1, o2) -> o1.compareTo(o2)
            : InternalUtils::compareLines;
         Collections.sort(lines, linesort);
      }

      if (format == IMappingFile.Format.TINY1 || format == IMappingFile.Format.TINY) {
         StringBuilder buf = new StringBuilder();
         buf.append(format == IMappingFile.Format.TINY ? "tiny\t2\t0" : "v1");

         for (String name : order) {
            buf.append('\t').append(name);
         }

         lines.add(0, buf.toString());
      } else if (format == IMappingFile.Format.TSRG2) {
         StringBuilder buf = new StringBuilder();
         buf.append("tsrg2");

         for (String name : order) {
            buf.append(' ').append(name);
         }

         lines.add(0, buf.toString());
      }

      Files.createDirectories(path.getParent());

      try (BufferedWriter writer = Files.newBufferedWriter(path)) {
         for (String line : lines) {
            writer.write(line);
            writer.write(10);
         }
      }
   }

   private static <K, V> V retPut(Map<K, V> map, K key, V value) {
      map.put(key, value);
      return value;
   }

   private String remapClass(int index, String cls) {
      String[] ret = this.remapClass(cls);
      return ret[ret.length == 1 ? 0 : index];
   }

   private String[] remapClass(String cls) {
      String[] ret = this.classCache.get(cls);
      if (ret == null) {
         NamedMappingFile.Cls _cls = this.classes.get(cls);
         if (_cls == null) {
            int idx = cls.lastIndexOf(36);
            if (idx != -1) {
               String[] parent = this.remapClass(cls.substring(0, idx));
               ret = new String[parent.length];

               for (int x = 0; x < ret.length; x++) {
                  ret[x] = parent[x] + '$' + cls.substring(idx + 1);
               }
            } else {
               ret = new String[]{cls};
            }
         } else {
            ret = _cls.getNames();
         }

         this.classCache.put(cls, ret);
      }

      return ret;
   }

   private String remapDescriptor(int index, String desc) {
      Matcher matcher = MappingFile.DESC.matcher(desc);
      StringBuffer buf = new StringBuffer();

      while (matcher.find()) {
         matcher.appendReplacement(buf, Matcher.quoteReplacement("L" + this.remapClass(index, matcher.group("cls")) + ";"));
      }

      matcher.appendTail(buf);
      return buf.toString();
   }

   Stream<NamedMappingFile.Package> getPackages() {
      return this.packages.values().stream();
   }

   Stream<NamedMappingFile.Cls> getClasses() {
      return this.classes.values().stream();
   }

   public NamedMappingFile.Package addPackage(String... names) {
      this.ensureCount(names);
      return retPut(this.packages, names[0], new NamedMappingFile.Package(names));
   }

   public NamedMappingFile.Cls addClass(String... names) {
      this.ensureCount(names);
      return retPut(this.classes, names[0], new NamedMappingFile.Cls(names));
   }

   @Override
   public INamedMappingFile build() {
      return this;
   }

   @Nullable
   NamedMappingFile.Cls getClass(String name) {
      return this.classes.get(name);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public IMappingBuilder.IClass addClass(String[] var1) {
      return this.addClass(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public IMappingBuilder.IPackage addPackage(String[] var1) {
      return this.addPackage(var1);
   }

   class Cls extends NamedMappingFile.Named implements IMappingBuilder.IClass {
      private final Map<String, NamedMappingFile.Cls.Field> fields = new HashMap<>();
      private final Map<String, NamedMappingFile.Cls.Method> methods = new HashMap<>();
      final Map<String, String> meta = new LinkedHashMap<>();

      Cls(String... name) {
         super(name);
      }

      Stream<NamedMappingFile.Cls.Field> getFields() {
         return this.fields.values().stream();
      }

      Stream<NamedMappingFile.Cls.Method> getMethods() {
         return this.methods.values().stream();
      }

      public NamedMappingFile.Cls.Field field(String... names) {
         NamedMappingFile.this.ensureCount(names);
         return NamedMappingFile.retPut(this.fields, names[0], new NamedMappingFile.Cls.Field(names));
      }

      public NamedMappingFile.Cls.Method method(String desc, String... names) {
         NamedMappingFile.this.ensureCount(names);
         return NamedMappingFile.retPut(this.methods, names[0] + desc, new NamedMappingFile.Cls.Method(desc, names));
      }

      @Override
      public IMappingBuilder.IClass meta(String key, String value) {
         this.meta.put(key, value);
         return this;
      }

      @Override
      public IMappingBuilder build() {
         return NamedMappingFile.this;
      }

      @Override
      String write(IMappingFile.Format format, int... order) {
         switch (format) {
            case SRG:
            case XSRG:
               return "CL: " + this.getName(order[0]) + ' ' + this.getName(order[1]);
            case CSRG:
            case TSRG:
               return this.getName(order[0]) + ' ' + this.getName(order[1]);
            case TSRG2:
               return this.getTsrg2(order);
            case PG:
               return this.getName(order[0]).replace('/', '.') + " -> " + this.getName(order[1]).replace('/', '.') + ':';
            case TINY1:
               return "CLASS" + this.getNames(order);
            case TINY:
               return "c" + this.getNames(order);
            default:
               throw new UnsupportedOperationException("Unknown format: " + format);
         }
      }

      private String getTsrg2(int... order) {
         StringBuilder ret = new StringBuilder();

         for (int x = 0; x < order.length; x++) {
            ret.append(this.getName(x));
            if (x != order.length - 1) {
               ret.append(' ');
            }
         }

         return ret.toString();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public IMappingBuilder.IMethod method(String var1, String[] var2) {
         return this.method(var1, var2);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public IMappingBuilder.IField field(String[] var1) {
         return this.field(var1);
      }

      class Field extends NamedMappingFile.Named implements IMappingBuilder.IField {
         @Nullable
         private String desc;
         final Map<String, String> meta = new LinkedHashMap<>();

         Field(String... names) {
            super(names);
         }

         public String getDescriptor(int index) {
            return this.desc == null ? null : (index == 0 ? this.desc : NamedMappingFile.this.remapDescriptor(index, this.desc));
         }

         @Override
         public IMappingBuilder.IField descriptor(String value) {
            this.desc = value;
            return this;
         }

         @Override
         public IMappingBuilder.IField meta(String key, String value) {
            this.meta.put(key, value);
            return this;
         }

         @Override
         public IMappingBuilder.IClass build() {
            return Cls.this;
         }

         @Override
         String write(IMappingFile.Format format, int... order) {
            switch (format) {
               case SRG:
                  return "FD: "
                     + Cls.this.getName(order[0])
                     + '/'
                     + this.getName(order[0])
                     + ' '
                     + Cls.this.getName(order[1])
                     + '/'
                     + this.getName(order[1])
                     + (this.desc == null ? "" : this.getDescriptor(order[0]) + ' ' + this.getDescriptor(order[1]));
               case XSRG:
                  return "FD: "
                     + Cls.this.getName(order[0])
                     + '/'
                     + this.getName(order[0])
                     + (this.desc == null ? "" : this.getDescriptor(order[0]))
                     + ' '
                     + Cls.this.getName(order[1])
                     + '/'
                     + this.getName(order[1])
                     + (this.desc == null ? "" : this.getDescriptor(order[1]));
               case CSRG:
                  return Cls.this.getName(order[0]) + ' ' + this.getName(order[0]) + ' ' + this.getName(order[1]);
               case TSRG:
                  return '\t' + this.getName(order[0]) + ' ' + this.getName(order[1]);
               case TSRG2:
                  return this.getTsrg2(order);
               case PG:
                  return "    " + InternalUtils.toSource(this.getDescriptor(order[0])) + ' ' + this.getName(order[0]) + " -> " + this.getName(order[1]);
               case TINY1:
                  return "FIELD\t" + Cls.this.getName(order[0]) + '\t' + this.getDescriptor(order[0]) + this.getNames(order);
               case TINY:
                  return "\tf\t" + this.getDescriptor(order[0]) + this.getNames(order);
               default:
                  throw new UnsupportedOperationException("Unknown format: " + format);
            }
         }

         private String getTsrg2(int... order) {
            StringBuilder ret = new StringBuilder().append('\t');

            for (int x = 0; x < order.length; x++) {
               ret.append(this.getName(x));
               if (x == 0 && this.getDescriptor(order[x]) != null) {
                  ret.append(' ').append(this.getDescriptor(order[x]));
               }

               if (x != order.length - 1) {
                  ret.append(' ');
               }
            }

            return ret.toString();
         }
      }

      class Method extends NamedMappingFile.Named implements IMappingBuilder.IMethod {
         private final String desc;
         private final Map<Integer, NamedMappingFile.Cls.Method.Parameter> params = new HashMap<>();
         final Map<String, String> meta = new LinkedHashMap<>();

         Method(String desc, String... names) {
            super(names);
            this.desc = desc;
         }

         @Override
         public IMappingBuilder.IParameter parameter(int index, String... names) {
            NamedMappingFile.this.ensureCount(names);
            return NamedMappingFile.retPut(this.params, index, new NamedMappingFile.Cls.Method.Parameter(index, names));
         }

         @Override
         public IMappingBuilder.IMethod meta(String key, String value) {
            this.meta.put(key, value);
            return this;
         }

         @Override
         public IMappingBuilder.IClass build() {
            return Cls.this;
         }

         public String getDescriptor(int index) {
            return index == 0 ? this.desc : NamedMappingFile.this.remapDescriptor(index, this.desc);
         }

         Stream<NamedMappingFile.Cls.Method.Parameter> getParameters() {
            return this.params.values().stream();
         }

         @Override
         String write(IMappingFile.Format format, int... order) {
            String oOwner = Cls.this.getName(order[0]);
            String oName = this.getName(order[0]);
            String mName = this.getName(order[1]);
            String oDesc = this.getDescriptor(order[0]);
            switch (format) {
               case SRG:
               case XSRG:
                  return "MD: " + oOwner + '/' + oName + ' ' + oDesc + ' ' + Cls.this.getName(order[1]) + '/' + mName + ' ' + this.getDescriptor(order[1]);
               case CSRG:
                  return oOwner + ' ' + oName + ' ' + oDesc + ' ' + mName;
               case TSRG:
                  return '\t' + oName + ' ' + oDesc + ' ' + mName;
               case TSRG2:
                  return this.getTsrg2(order);
               case PG:
                  int start = Integer.parseInt(this.meta.getOrDefault("start_line", "0"));
                  int end = Integer.parseInt(this.meta.getOrDefault("end_line", "0"));
                  return "    " + (start == 0 && end == 0 ? "" : start + ":" + end + ":") + InternalUtils.toSource(oName, oDesc) + " -> " + mName;
               case TINY1:
                  return "METHOD\t" + oOwner + '\t' + oDesc + this.getNames(order);
               case TINY:
                  return "\tm\t" + oDesc + this.getNames(order);
               default:
                  throw new UnsupportedOperationException("Unknown format: " + format);
            }
         }

         private String getTsrg2(int... order) {
            StringBuilder ret = new StringBuilder().append('\t');

            for (int x = 0; x < order.length; x++) {
               ret.append(this.getName(x));
               if (x == 0 && this.getDescriptor(order[x]) != null) {
                  ret.append(' ').append(this.getDescriptor(order[x]));
               }

               if (x != order.length - 1) {
                  ret.append(' ');
               }
            }

            return ret.toString();
         }

         class Parameter extends NamedMappingFile.Named implements IMappingBuilder.IParameter {
            private final int index;
            final Map<String, String> meta = new LinkedHashMap<>();

            Parameter(int index, String... names) {
               super(names);
               this.index = index;
            }

            public int getIndex() {
               return this.index;
            }

            @Override
            String write(IMappingFile.Format format, int... order) {
               switch (format) {
                  case SRG:
                  case XSRG:
                  case CSRG:
                  case TSRG:
                  case PG:
                  case TINY1:
                     return null;
                  case TSRG2:
                     return this.getTsrg2(order);
                  case TINY:
                     return "\t\tp\t" + this.getIndex() + this.getNames(order);
                  default:
                     throw new UnsupportedOperationException("Unknown format: " + format);
               }
            }

            private String getTsrg2(int... order) {
               StringBuilder ret = new StringBuilder().append("\t\t").append(this.getIndex());

               for (int x = 0; x < order.length; x++) {
                  ret.append(' ').append(this.getName(x));
               }

               return ret.toString();
            }

            @Override
            public IMappingBuilder.IParameter meta(String key, String value) {
               this.meta.put(key, value);
               return this;
            }

            @Override
            public IMappingBuilder.IMethod build() {
               return Method.this;
            }
         }
      }
   }

   abstract class Named {
      private final String[] names;

      Named(String... names) {
         this.names = names;
      }

      public String getName(int index) {
         return this.names[index];
      }

      String[] getNames() {
         return this.names;
      }

      protected String getNames(int... order) {
         StringBuilder ret = new StringBuilder();

         for (int index : order) {
            ret.append('\t').append(this.getName(index));
         }

         return ret.toString();
      }

      abstract String write(IMappingFile.Format var1, int... var2);
   }

   class Package extends NamedMappingFile.Named implements IMappingBuilder.IPackage {
      final Map<String, String> meta = new LinkedHashMap<>();

      Package(String... names) {
         super(names);
      }

      @Override
      String write(IMappingFile.Format format, int... order) {
         switch (format) {
            case SRG:
            case XSRG:
               return "PK: " + this.getName(order[0]) + ' ' + this.getName(order[1]);
            case CSRG:
            case TSRG:
               return this.getName(order[0]) + "/ " + this.getName(order[1]) + '/';
            case TSRG2:
               return this.getTsrg2(order);
            case PG:
            case TINY1:
            case TINY:
               return null;
            default:
               throw new UnsupportedOperationException("Unknown format: " + format);
         }
      }

      private String getTsrg2(int... order) {
         StringBuilder ret = new StringBuilder();

         for (int x = 0; x < order.length; x++) {
            ret.append(this.getName(x)).append('/');
            if (x != order.length - 1) {
               ret.append(' ');
            }
         }

         return ret.toString();
      }

      @Override
      public IMappingBuilder.IPackage meta(String key, String value) {
         this.meta.put(key, value);
         return this;
      }

      @Override
      public IMappingBuilder build() {
         return NamedMappingFile.this;
      }
   }
}
