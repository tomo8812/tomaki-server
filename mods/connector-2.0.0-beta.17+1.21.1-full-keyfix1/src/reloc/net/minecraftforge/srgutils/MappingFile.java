package reloc.net.minecraftforge.srgutils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

class MappingFile implements IMappingFile {
   private Map<String, MappingFile.Package> packages = new HashMap<>();
   private Collection<MappingFile.Package> packagesView = Collections.unmodifiableCollection(this.packages.values());
   private Map<String, MappingFile.Cls> classes = new HashMap<>();
   private Collection<MappingFile.Cls> classesView = Collections.unmodifiableCollection(this.classes.values());
   private Map<String, String> cache = new ConcurrentHashMap<>();
   static final Pattern DESC = Pattern.compile("L(?<cls>[^;]+);");

   MappingFile() {
   }

   MappingFile(NamedMappingFile source, int from, int to) {
      source.getPackages().forEach(pkg -> this.addPackage(pkg.getName(from), pkg.getName(to), pkg.meta));
      source.getClasses().forEach(cls -> {
         MappingFile.Cls c = this.addClass(cls.getName(from), cls.getName(to), cls.meta);
         cls.getFields().forEach(fld -> c.addField(fld.getName(from), fld.getName(to), fld.getDescriptor(from), fld.meta));
         cls.getMethods().forEach(mtd -> {
            MappingFile.Cls.Method m = c.addMethod(mtd.getName(from), mtd.getDescriptor(from), mtd.getName(to), mtd.meta);
            mtd.getParameters().forEach(par -> m.addParameter(par.getIndex(), par.getName(from), par.getName(to), par.meta));
         });
      });
   }

   @Override
   public Collection<MappingFile.Package> getPackages() {
      return this.packagesView;
   }

   @Nullable
   public MappingFile.Package getPackage(String original) {
      return this.packages.get(original);
   }

   private MappingFile.Package addPackage(String original, String mapped, Map<String, String> metadata) {
      return this.packages.put(original, new MappingFile.Package(original, mapped, metadata));
   }

   @Override
   public Collection<MappingFile.Cls> getClasses() {
      return this.classesView;
   }

   @Nullable
   public MappingFile.Cls getClass(String original) {
      return this.classes.get(original);
   }

   private MappingFile.Cls addClass(String original, String mapped, Map<String, String> metadata) {
      return retPut(this.classes, original, new MappingFile.Cls(original, mapped, metadata));
   }

   @Override
   public String remapPackage(String pkg) {
      MappingFile.Package ipkg = this.packages.get(pkg);
      return ipkg == null ? pkg : ipkg.getMapped();
   }

   @Override
   public String remapClass(String cls) {
      String ret = this.cache.get(cls);
      if (ret == null) {
         MappingFile.Cls _cls = this.classes.get(cls);
         if (_cls == null) {
            int idx = cls.lastIndexOf(36);
            if (idx != -1) {
               ret = this.remapClass(cls.substring(0, idx)) + '$' + cls.substring(idx + 1);
            } else {
               ret = cls;
            }
         } else {
            ret = _cls.getMapped();
         }

         this.cache.put(cls, ret);
      }

      return ret;
   }

   @Override
   public String remapDescriptor(String desc) {
      Matcher matcher = DESC.matcher(desc);
      StringBuffer buf = new StringBuffer();

      while (matcher.find()) {
         matcher.appendReplacement(buf, Matcher.quoteReplacement("L" + this.remapClass(matcher.group("cls")) + ";"));
      }

      matcher.appendTail(buf);
      return buf.toString();
   }

   @Override
   public void write(Path path, IMappingFile.Format format, boolean reversed) throws IOException {
      List<String> lines = new ArrayList<>();
      Comparator<IMappingFile.INode> sort = reversed ? (a, b) -> a.getMapped().compareTo(b.getMapped()) : (a, b) -> a.getOriginal().compareTo(b.getOriginal());
      this.getPackages().stream().sorted(sort).forEachOrdered(pkg -> {
         lines.add(pkg.write(format, reversed));
         InternalUtils.writeMeta(format, lines, InternalUtils.Element.PACKAGE, pkg.getMetadata());
      });
      this.getClasses().stream().sorted(sort).forEachOrdered(cls -> {
         lines.add(cls.write(format, reversed));
         InternalUtils.writeMeta(format, lines, InternalUtils.Element.CLASS, cls.getMetadata());
         cls.getFields().stream().sorted(sort).forEachOrdered(fld -> {
            lines.add(fld.write(format, reversed));
            InternalUtils.writeMeta(format, lines, InternalUtils.Element.FIELD, fld.getMetadata());
         });
         cls.getMethods().stream().sorted(sort).forEachOrdered(mtd -> {
            lines.add(mtd.write(format, reversed));
            InternalUtils.writeMeta(format, lines, InternalUtils.Element.METHOD, mtd.getMetadata());
            mtd.getParameters().stream().sorted((a, b) -> a.getIndex() - b.getIndex()).forEachOrdered(par -> {
               lines.add(par.write(format, reversed));
               InternalUtils.writeMeta(format, lines, InternalUtils.Element.PARAMETER, par.getMetadata());
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

      if (format == IMappingFile.Format.TINY1) {
         lines.add(0, "v1\tleft\tright");
      } else if (format == IMappingFile.Format.TINY) {
         lines.add(0, "tiny\t2\t0\tleft\tright");
      } else if (format == IMappingFile.Format.TSRG2) {
         lines.add(0, "tsrg2 left right");
      }

      Files.createDirectories(path.getParent());

      try (BufferedWriter writer = Files.newBufferedWriter(path)) {
         for (String line : lines) {
            writer.write(line);
            writer.write(10);
         }
      }
   }

   public MappingFile reverse() {
      MappingFile ret = new MappingFile();
      this.getPackages().stream().forEach(pkg -> ret.addPackage(pkg.getMapped(), pkg.getOriginal(), pkg.getMetadata()));
      this.getClasses().stream().forEach(cls -> {
         MappingFile.Cls c = ret.addClass(cls.getMapped(), cls.getOriginal(), cls.getMetadata());
         cls.getFields().stream().forEach(fld -> c.addField(fld.getMapped(), fld.getOriginal(), fld.getMappedDescriptor(), fld.getMetadata()));
         cls.getMethods().stream().forEach(mtd -> {
            MappingFile.Cls.Method m = c.addMethod(mtd.getMapped(), mtd.getMappedDescriptor(), mtd.getOriginal(), mtd.getMetadata());
            mtd.getParameters().stream().forEach(par -> m.addParameter(par.getIndex(), par.getMapped(), par.getOriginal(), par.getMetadata()));
         });
      });
      return ret;
   }

   public MappingFile rename(IRenamer renamer) {
      MappingFile ret = new MappingFile();
      this.getPackages().stream().forEach(pkg -> ret.addPackage(pkg.getOriginal(), renamer.rename(pkg), pkg.getMetadata()));
      this.getClasses().stream().forEach(cls -> {
         MappingFile.Cls c = ret.addClass(cls.getOriginal(), renamer.rename(cls), cls.getMetadata());
         cls.getFields().stream().forEach(fld -> c.addField(fld.getOriginal(), renamer.rename(fld), fld.getDescriptor(), fld.getMetadata()));
         cls.getMethods().stream().forEach(mtd -> {
            MappingFile.Cls.Method m = c.addMethod(mtd.getOriginal(), mtd.getDescriptor(), renamer.rename(mtd), mtd.getMetadata());
            mtd.getParameters().stream().forEach(par -> m.addParameter(par.getIndex(), par.getOriginal(), renamer.rename(par), par.getMetadata()));
         });
      });
      return ret;
   }

   public MappingFile chain(final IMappingFile link) {
      return this.rename(new IRenamer() {
         @Override
         public String rename(IMappingFile.IPackage value) {
            return link.remapPackage(value.getMapped());
         }

         @Override
         public String rename(IMappingFile.IClass value) {
            return link.remapClass(value.getMapped());
         }

         @Override
         public String rename(IMappingFile.IField value) {
            IMappingFile.IClass cls = link.getClass(value.getParent().getMapped());
            return cls == null ? value.getMapped() : cls.remapField(value.getMapped());
         }

         @Override
         public String rename(IMappingFile.IMethod value) {
            IMappingFile.IClass cls = link.getClass(value.getParent().getMapped());
            return cls == null ? value.getMapped() : cls.remapMethod(value.getMapped(), value.getMappedDescriptor());
         }

         @Override
         public String rename(IMappingFile.IParameter value) {
            IMappingFile.IMethod mtd = value.getParent();
            IMappingFile.IClass cls = link.getClass(mtd.getParent().getMapped());
            mtd = cls == null ? null : cls.getMethod(mtd.getMapped(), mtd.getMappedDescriptor());
            return mtd == null ? value.getMapped() : mtd.remapParameter(value.getIndex(), value.getMapped());
         }
      });
   }

   public MappingFile merge(IMappingFile other) {
      MappingFile ret = new MappingFile();
      this.getPackages().stream().forEach(pkg -> ret.addPackage(pkg.getOriginal(), pkg.getMapped(), pkg.getMetadata()));
      this.getClasses().stream().forEach(cls -> copyClass(ret, cls));
      other.getPackages().stream().forEach(pkg -> {
         MappingFile.Package existingPkg = ret.getPackage(pkg.getOriginal());
         if (existingPkg == null) {
            ret.addPackage(pkg.getOriginal(), pkg.getMapped(), pkg.getMetadata());
         } else {
            ret.addPackage(pkg.getOriginal(), existingPkg.getMapped(), mergeMetadata(existingPkg.getMetadata(), pkg.getMetadata()));
         }
      });
      other.getClasses()
         .stream()
         .forEach(
            cls -> {
               MappingFile.Cls existingCls = ret.getClass(cls.getOriginal());
               if (existingCls == null) {
                  copyClass(ret, cls);
               } else {
                  MappingFile.Cls newCls = ret.addClass(cls.getOriginal(), existingCls.getMapped(), mergeMetadata(existingCls.getMetadata(), cls.getMetadata()));
                  newCls.methods.putAll(existingCls.methods);
                  newCls.fields.putAll(existingCls.fields);
                  cls.getFields()
                     .stream()
                     .forEach(
                        fld -> {
                           IMappingFile.IField existingFld = existingCls.getField(fld.getOriginal());
                           if (existingFld == null) {
                              newCls.addField(fld.getOriginal(), fld.getMapped(), fld.getDescriptor(), fld.getMetadata());
                           } else {
                              newCls.addField(
                                 fld.getOriginal(),
                                 existingFld.getMapped(),
                                 existingFld.getDescriptor(),
                                 mergeMetadata(existingFld.getMetadata(), fld.getMetadata())
                              );
                           }
                        }
                     );
                  cls.getMethods()
                     .stream()
                     .forEach(
                        mtd -> {
                           MappingFile.Cls.Method existingMtd = existingCls.getMethod(mtd.getOriginal(), mtd.getDescriptor());
                           if (existingMtd == null) {
                              copyMethod(newCls, mtd);
                           } else {
                              MappingFile.Cls.Method newMtd = newCls.addMethod(
                                 mtd.getOriginal(),
                                 existingMtd.getDescriptor(),
                                 existingMtd.getMapped(),
                                 mergeMetadata(existingMtd.getMetadata(), mtd.getMetadata())
                              );
                              newMtd.params.putAll(existingMtd.params);
                              mtd.getParameters()
                                 .stream()
                                 .forEach(
                                    par -> {
                                       IMappingFile.IParameter existingPar = existingMtd.getParameter(par.getIndex());
                                       if (existingPar == null) {
                                          newMtd.addParameter(par.getIndex(), par.getOriginal(), par.getMapped(), par.getMetadata());
                                       } else {
                                          newMtd.addParameter(
                                             par.getIndex(),
                                             par.getOriginal(),
                                             existingPar.getMapped(),
                                             mergeMetadata(existingPar.getMetadata(), par.getMetadata())
                                          );
                                       }
                                    }
                                 );
                           }
                        }
                     );
               }
            }
         );
      return ret;
   }

   private static void copyClass(MappingFile ret, IMappingFile.IClass cls) {
      MappingFile.Cls c = ret.addClass(cls.getOriginal(), cls.getMapped(), cls.getMetadata());
      cls.getFields().stream().forEach(fld -> c.addField(fld.getOriginal(), fld.getMapped(), fld.getDescriptor(), fld.getMetadata()));
      cls.getMethods().stream().forEach(mtd -> copyMethod(c, mtd));
   }

   private static void copyMethod(MappingFile.Cls c, IMappingFile.IMethod mtd) {
      MappingFile.Cls.Method m = c.addMethod(mtd.getOriginal(), mtd.getDescriptor(), mtd.getMapped(), mtd.getMetadata());
      mtd.getParameters().stream().forEach(par -> m.addParameter(par.getIndex(), par.getOriginal(), par.getMapped(), par.getMetadata()));
   }

   private static Map<String, String> mergeMetadata(Map<String, String> base, Map<String, String> extra) {
      Map<String, String> merged = new HashMap<>(base);

      for (Entry<String, String> entry : extra.entrySet()) {
         if (!merged.containsKey(entry.getKey())) {
            merged.put(entry.getKey(), entry.getValue());
         }
      }

      return merged;
   }

   private static <K, V> V retPut(Map<K, V> map, K key, V value) {
      map.put(key, value);
      return value;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public IMappingFile merge(IMappingFile var1) {
      return this.merge(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public IMappingFile chain(IMappingFile var1) {
      return this.chain(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public IMappingFile rename(IRenamer var1) {
      return this.rename(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public IMappingFile reverse() {
      return this.reverse();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public IMappingFile.IClass getClass(String var1) {
      return this.getClass(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public IMappingFile.IPackage getPackage(String var1) {
      return this.getPackage(var1);
   }

   class Cls extends MappingFile.Node implements IMappingFile.IClass {
      private Map<String, MappingFile.Cls.Field> fields = new HashMap<>();
      private Collection<MappingFile.Cls.Field> fieldsView = Collections.unmodifiableCollection(this.fields.values());
      private Map<String, MappingFile.Cls.Method> methods = new HashMap<>();
      private Collection<MappingFile.Cls.Method> methodsView = Collections.unmodifiableCollection(this.methods.values());

      protected Cls(String original, String mapped, Map<String, String> metadata) {
         super(original, mapped, metadata);
      }

      @Nullable
      @Override
      public String write(IMappingFile.Format format, boolean reversed) {
         String oName = !reversed ? this.getOriginal() : this.getMapped();
         String mName = !reversed ? this.getMapped() : this.getOriginal();
         switch (format) {
            case SRG:
            case XSRG:
               return "CL: " + oName + ' ' + mName;
            case CSRG:
            case TSRG:
            case TSRG2:
               return oName + ' ' + mName;
            case PG:
               return oName.replace('/', '.') + " -> " + mName.replace('/', '.') + ':';
            case TINY1:
               return "CLASS\t" + oName + '\t' + mName;
            case TINY:
               return "c\t" + oName + '\t' + mName;
            default:
               throw new UnsupportedOperationException("Unknown format: " + format);
         }
      }

      @Override
      public Collection<MappingFile.Cls.Field> getFields() {
         return this.fieldsView;
      }

      @Nullable
      @Override
      public IMappingFile.IField getField(String name) {
         return this.fields.get(name);
      }

      @Override
      public String remapField(String field) {
         MappingFile.Cls.Field fld = this.fields.get(field);
         return fld == null ? field : fld.getMapped();
      }

      private MappingFile.Cls.Field addField(String original, String mapped, String desc, Map<String, String> metadata) {
         return MappingFile.retPut(this.fields, original, new MappingFile.Cls.Field(original, mapped, desc, metadata));
      }

      @Override
      public Collection<MappingFile.Cls.Method> getMethods() {
         return this.methodsView;
      }

      @Nullable
      public MappingFile.Cls.Method getMethod(String name, String desc) {
         return this.methods.get(name + desc);
      }

      private MappingFile.Cls.Method addMethod(String original, String desc, String mapped, Map<String, String> metadata) {
         return MappingFile.retPut(this.methods, original + desc, new MappingFile.Cls.Method(original, desc, mapped, metadata));
      }

      @Override
      public String remapMethod(String name, String desc) {
         MappingFile.Cls.Method mtd = this.methods.get(name + desc);
         return mtd == null ? name : mtd.getMapped();
      }

      @Override
      public String toString() {
         return this.write(IMappingFile.Format.SRG, false);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Nullable
      @Override
      public IMappingFile.IMethod getMethod(String var1, String var2) {
         return this.getMethod(var1, var2);
      }

      class Field extends MappingFile.Node implements IMappingFile.IField {
         private String desc;

         private Field(String original, String mapped, String desc, Map<String, String> metadata) {
            super(original, mapped, metadata);
            this.desc = desc;
         }

         @Override
         public String getDescriptor() {
            return this.desc;
         }

         @Override
         public String getMappedDescriptor() {
            return this.desc == null ? null : MappingFile.this.remapDescriptor(this.desc);
         }

         @Nullable
         @Override
         public String write(IMappingFile.Format format, boolean reversed) {
            if (format != IMappingFile.Format.TSRG2 && format.hasFieldTypes() && this.desc == null) {
               throw new IllegalStateException("Can not write " + format.name() + " format, field is missing descriptor");
            }

            String oOwner = !reversed ? Cls.this.getOriginal() : Cls.this.getMapped();
            String mOwner = !reversed ? Cls.this.getMapped() : Cls.this.getOriginal();
            String oName = !reversed ? this.getOriginal() : this.getMapped();
            String mName = !reversed ? this.getMapped() : this.getOriginal();
            String oDesc = !reversed ? this.getDescriptor() : this.getMappedDescriptor();
            String mDesc = !reversed ? this.getMappedDescriptor() : this.getDescriptor();
            switch (format) {
               case SRG:
                  return "FD: " + oOwner + '/' + oName + ' ' + mOwner + '/' + mName + (oDesc == null ? "" : " # " + oDesc + " " + mDesc);
               case XSRG:
                  return "FD: " + oOwner + '/' + oName + (oDesc == null ? "" : ' ' + oDesc) + ' ' + mOwner + '/' + mName + (mDesc == null ? "" : ' ' + mDesc);
               case CSRG:
                  return oOwner + ' ' + oName + ' ' + mName;
               case TSRG:
                  return '\t' + oName + ' ' + mName;
               case TSRG2:
                  return '\t' + oName + (oDesc == null ? "" : ' ' + oDesc) + ' ' + mName;
               case PG:
                  return "    " + InternalUtils.toSource(oDesc) + ' ' + oName + " -> " + mName;
               case TINY1:
                  return "FIELD\t" + oOwner + '\t' + oDesc + '\t' + oName + '\t' + mName;
               case TINY:
                  return "\tf\t" + oDesc + '\t' + oName + '\t' + mName;
               default:
                  throw new UnsupportedOperationException("Unknown format: " + format);
            }
         }

         @Override
         public String toString() {
            return this.write(IMappingFile.Format.SRG, false);
         }

         public MappingFile.Cls getParent() {
            return Cls.this;
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public Object getParent() {
            return this.getParent();
         }
      }

      class Method extends MappingFile.Node implements IMappingFile.IMethod {
         private final String desc;
         private final Map<Integer, MappingFile.Cls.Method.Parameter> params = new HashMap<>();
         private final Collection<MappingFile.Cls.Method.Parameter> paramsView = Collections.unmodifiableCollection(this.params.values());

         private Method(String original, String desc, String mapped, Map<String, String> metadata) {
            super(original, mapped, metadata);
            this.desc = desc;
         }

         @Override
         public String getDescriptor() {
            return this.desc;
         }

         @Override
         public String getMappedDescriptor() {
            return MappingFile.this.remapDescriptor(this.desc);
         }

         @Override
         public Collection<MappingFile.Cls.Method.Parameter> getParameters() {
            return this.paramsView;
         }

         private MappingFile.Cls.Method.Parameter addParameter(int index, String original, String mapped, Map<String, String> metadata) {
            return MappingFile.retPut(this.params, index, new MappingFile.Cls.Method.Parameter(index, original, mapped, metadata));
         }

         @Nullable
         @Override
         public IMappingFile.IParameter getParameter(int index) {
            return this.params.get(index);
         }

         @Override
         public String remapParameter(int index, String name) {
            MappingFile.Cls.Method.Parameter param = this.params.get(index);
            return param == null ? name : param.getMapped();
         }

         @Override
         public String write(IMappingFile.Format format, boolean reversed) {
            String oName = !reversed ? this.getOriginal() : this.getMapped();
            String mName = !reversed ? this.getMapped() : this.getOriginal();
            String oOwner = !reversed ? Cls.this.getOriginal() : Cls.this.getMapped();
            String mOwner = !reversed ? Cls.this.getMapped() : Cls.this.getOriginal();
            String oDesc = !reversed ? this.getDescriptor() : this.getMappedDescriptor();
            String mDesc = !reversed ? this.getMappedDescriptor() : this.getDescriptor();
            switch (format) {
               case SRG:
               case XSRG:
                  return "MD: " + oOwner + '/' + oName + ' ' + oDesc + ' ' + mOwner + '/' + mName + ' ' + mDesc;
               case CSRG:
                  return oOwner + ' ' + oName + ' ' + oDesc + ' ' + mName;
               case TSRG:
               case TSRG2:
                  return '\t' + oName + ' ' + oDesc + ' ' + mName;
               case PG:
                  int start = Integer.parseInt(this.getMetadata().getOrDefault("start_line", "0"));
                  int end = Integer.parseInt(this.getMetadata().getOrDefault("end_line", "0"));
                  return "    " + (start == 0 && end == 0 ? "" : start + ":" + end + ":") + InternalUtils.toSource(oName, oDesc) + " -> " + mName;
               case TINY1:
                  return "METHOD\t" + oOwner + '\t' + oDesc + '\t' + oName + '\t' + mName;
               case TINY:
                  return "\tm\t" + oDesc + '\t' + oName + '\t' + mName;
               default:
                  throw new UnsupportedOperationException("Unknown format: " + format);
            }
         }

         @Override
         public String toString() {
            return this.write(IMappingFile.Format.SRG, false);
         }

         public MappingFile.Cls getParent() {
            return Cls.this;
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public Object getParent() {
            return this.getParent();
         }

         class Parameter extends MappingFile.Node implements IMappingFile.IParameter {
            private final int index;

            protected Parameter(int index, String original, String mapped, Map<String, String> metadata) {
               super(original, mapped, metadata);
               this.index = index;
            }

            public IMappingFile.IMethod getParent() {
               return Method.this;
            }

            @Override
            public int getIndex() {
               return this.index;
            }

            @Override
            public String write(IMappingFile.Format format, boolean reversed) {
               String oName = !reversed ? this.getOriginal() : this.getMapped();
               String mName = !reversed ? this.getMapped() : this.getOriginal();
               switch (format) {
                  case SRG:
                  case XSRG:
                  case CSRG:
                  case TSRG:
                  case PG:
                  case TINY1:
                     return null;
                  case TSRG2:
                     return "\t\t" + this.getIndex() + ' ' + oName + ' ' + mName;
                  case TINY:
                     return "\t\tp\t" + this.getIndex() + '\t' + oName + '\t' + mName;
                  default:
                     throw new UnsupportedOperationException("Unknown format: " + format);
               }
            }

            // $VF: synthetic method
            // $VF: bridge method
            @Override
            public Object getParent() {
               return this.getParent();
            }
         }
      }
   }

   abstract class Node implements IMappingFile.INode {
      private final String original;
      private final String mapped;
      private final Map<String, String> metadata;

      protected Node(String original, String mapped, Map<String, String> metadata) {
         this.original = original;
         this.mapped = mapped;
         this.metadata = metadata.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(metadata);
      }

      @Override
      public String getOriginal() {
         return this.original;
      }

      @Override
      public String getMapped() {
         return this.mapped;
      }

      @Override
      public Map<String, String> getMetadata() {
         return this.metadata;
      }
   }

   class Package extends MappingFile.Node implements IMappingFile.IPackage {
      protected Package(String original, String mapped, Map<String, String> metadata) {
         super(original, mapped, metadata);
      }

      @Nullable
      @Override
      public String write(IMappingFile.Format format, boolean reversed) {
         String sorig = this.getOriginal().isEmpty() ? "." : this.getOriginal();
         String smap = this.getMapped().isEmpty() ? "." : this.getMapped();
         if (reversed) {
            String tmp = sorig;
            sorig = smap;
            smap = tmp;
         }

         switch (format) {
            case SRG:
            case XSRG:
               return "PK: " + sorig + ' ' + smap;
            case CSRG:
            case TSRG:
            case TSRG2:
               return this.getOriginal() + "/ " + this.getMapped() + '/';
            case PG:
            case TINY1:
               return null;
            default:
               throw new UnsupportedOperationException("Unknown format: " + format);
         }
      }

      @Override
      public String toString() {
         return this.write(IMappingFile.Format.SRG, false);
      }
   }
}
