package reloc.net.minecraftforge.fart.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Remapper;
import reloc.net.minecraftforge.fart.api.ClassProvider;
import reloc.net.minecraftforge.srgutils.IMappingFile;

public class EnhancedRemapper extends Remapper {
   protected final ClassProvider classProvider;
   private final IMappingFile map;
   private final Map<String, Optional<EnhancedRemapper.MClass>> resolved = new ConcurrentHashMap<>();
   private final Consumer<String> log;
   private final Map<String, Object> locks = new ConcurrentHashMap<>();

   public EnhancedRemapper(ClassProvider classProvider, IMappingFile map, Consumer<String> log) {
      this.classProvider = classProvider;
      this.map = map;
      this.log = log;
   }

   public String mapModuleName(String name) {
      return name;
   }

   public String mapAnnotationAttributeName(String descriptor, String name) {
      return name;
   }

   public String mapInvokeDynamicMethodName(String name, String descriptor) {
      return name;
   }

   public String mapMethodName(String owner, String name, String descriptor) {
      return this.getClass(owner).flatMap(c -> c.getMethod(name, descriptor)).map(EnhancedRemapper.MClass.MMethod::getMapped).orElse(name);
   }

   public String mapRecordComponentName(String owner, String name, String descriptor) {
      return this.mapFieldName(owner, name, descriptor);
   }

   public String mapFieldName(String owner, String name, String descriptor) {
      return this.getClass(owner).flatMap(c -> c.getField(name, descriptor)).map(EnhancedRemapper.MClass.MField::getMapped).orElse(name);
   }

   public String mapPackageName(String name) {
      return this.map.remapPackage(name);
   }

   public String map(String name) {
      return this.getClass(name).map(EnhancedRemapper.MClass::getMapped).orElse(this.map.remapClass(name));
   }

   public String mapParameterName(String owner, String methodName, String methodDescriptor, int index, String paramName) {
      return this.getClass(owner).flatMap(c -> c.getMethod(methodName, methodDescriptor)).map(m -> m.mapParameter(index, paramName)).orElse(paramName);
   }

   public Object mapValue(Object value) {
      if (value instanceof Handle) {
         Handle handle = (Handle)value;
         boolean isFieldHandle = handle.getTag() <= 4;
         return new Handle(
            handle.getTag(),
            this.mapType(handle.getOwner()),
            isFieldHandle
               ? this.mapFieldName(handle.getOwner(), handle.getName(), handle.getDesc())
               : this.mapMethodName(handle.getOwner(), handle.getName(), handle.getDesc()),
            isFieldHandle ? this.mapDesc(handle.getDesc()) : this.mapMethodDesc(handle.getDesc()),
            handle.isInterface()
         );
      } else {
         return super.mapValue(value);
      }
   }

   public Optional<EnhancedRemapper.MClass> getClass(String cls) {
      if (cls != null && cls.charAt(0) != '[') {
         Optional<EnhancedRemapper.MClass> ret = this.resolved.get(cls);
         if (ret == null) {
            synchronized (this.locks.computeIfAbsent(cls.intern(), s -> new Object())) {
               ret = this.resolved.get(cls);
               if (ret == null) {
                  ret = this.computeClass(cls);
               }
            }
         }

         return ret;
      } else {
         return Optional.empty();
      }
   }

   private ClassProvider getClassProvider() {
      return this.classProvider;
   }

   private IMappingFile getMap() {
      return this.map;
   }

   private Optional<EnhancedRemapper.MClass> computeClass(String cls) {
      Optional<? extends ClassProvider.IClassInfo> icls = this.getClassProvider().getClass(cls);
      IMappingFile.IClass mcls = this.map.getClass(cls);
      return !icls.isPresent() && mcls == null ? Optional.empty() : Optional.of(new EnhancedRemapper.MClass(cls, icls.orElse(null), mcls));
   }

   public class MClass {
      private final ClassProvider.IClassInfo icls;
      private final IMappingFile.IClass mcls;
      private final String mappedName;
      private final List<EnhancedRemapper.MClass> parents;
      private final Map<String, Optional<EnhancedRemapper.MClass.MField>> fields = new ConcurrentHashMap<>();
      private Collection<Optional<EnhancedRemapper.MClass.MField>> fieldsView = Collections.unmodifiableCollection(this.fields.values());
      private final Map<String, Optional<EnhancedRemapper.MClass.MMethod>> methods = new ConcurrentHashMap<>();
      private Collection<Optional<EnhancedRemapper.MClass.MMethod>> methodsView = Collections.unmodifiableCollection(this.methods.values());

      MClass(String cls, ClassProvider.IClassInfo icls, IMappingFile.IClass mcls) {
         if (icls == null && mcls == null) {
            throw new IllegalArgumentException("Can't pass in both nulls..");
         }

         this.icls = icls;
         this.mcls = mcls;
         this.mappedName = mcls == null ? EnhancedRemapper.this.getMap().remapClass(icls.getName()) : mcls.getMapped();
         EnhancedRemapper.this.resolved.put(cls, Optional.of(this));
         if (icls != null) {
            List<EnhancedRemapper.MClass> parents = new ArrayList<>();
            EnhancedRemapper.this.getClass(icls.getSuper()).ifPresent(parents::add);
            icls.getInterfaces().stream().map(EnhancedRemapper.this::getClass).forEach(o -> o.ifPresent(parents::add));
            this.parents = Collections.unmodifiableList(parents);
            icls.getFields().stream().map(f -> new EnhancedRemapper.MClass.MField(f, mcls == null ? null : mcls.getField(f.getName()))).forEach(f -> {
               Optional var10000 = this.fields.put(f.getKey(), Optional.of(f));
            });
            icls.getMethods()
               .stream()
               .map(m -> new EnhancedRemapper.MClass.MMethod(m, mcls == null ? null : mcls.getMethod(m.getName(), m.getDescriptor())))
               .forEach(m -> {
                  Optional var10000 = this.methods.put(m.getKey(), Optional.of(m));
               });
         } else {
            this.parents = Collections.emptyList();
            mcls.getFields().stream().map(f -> new EnhancedRemapper.MClass.MField(null, f)).forEach(f -> {
               Optional var10000 = this.fields.put(f.getKey(), Optional.of(f));
            });
            mcls.getMethods().stream().map(m -> new EnhancedRemapper.MClass.MMethod(null, m)).forEach(m -> {
               Optional var10000 = this.methods.put(m.getKey(), Optional.of(m));
            });
         }

         for (EnhancedRemapper.MClass parentCls : this.parents) {
            for (Optional<EnhancedRemapper.MClass.MField> fldOpt : parentCls.getFields()) {
               if (fldOpt.isPresent()) {
                  EnhancedRemapper.MClass.MField fld = fldOpt.get();
                  Optional<EnhancedRemapper.MClass.MField> existing = this.fields.get(fld.getKey());
                  if (existing == null || !existing.isPresent()) {
                     this.fields.put(fld.getKey(), fldOpt);
                  }
               }
            }

            for (Optional<EnhancedRemapper.MClass.MMethod> mtdOpt : parentCls.getMethods()) {
               if (mtdOpt.isPresent()) {
                  EnhancedRemapper.MClass.MMethod mtd = mtdOpt.get();
                  if (!parentCls.isInterface() || mtd.isInterfaceInheritable()) {
                     Optional<EnhancedRemapper.MClass.MMethod> existingOpt = this.methods.get(mtd.getKey());
                     if (existingOpt != null && existingOpt.isPresent()) {
                        EnhancedRemapper.MClass.MMethod existing = existingOpt.get();
                        if (!existing.hasMapping() && !existing.getName().equals(mtd.getMapped())) {
                           if (!existing.getMapped().equals(mtd.getMapped())) {
                              EnhancedRemapper.this.log
                                 .accept(
                                    "Conflicting propagated mapping for " + existing + " from " + mtd + ": " + existing.getMapped() + " -> " + mtd.getMapped()
                                 );
                           }

                           existing.setMapped(mtd.getMapped());
                        } else if (!mtd.hasMapping() && !mtd.getName().equals(existing.getMapped())) {
                           if (!mtd.getMapped().equals(existing.getMapped())) {
                              EnhancedRemapper.this.log
                                 .accept(
                                    "Conflicting propagated mapping for " + mtd + " from " + existing + ": " + mtd.getMapped() + " -> " + existing.getMapped()
                                 );
                           }

                           mtd.setMapped(existing.getMapped());
                        }
                     } else {
                        this.methods.put(mtd.getKey(), mtdOpt);
                     }
                  }
               }
            }
         }
      }

      public String getName() {
         return this.icls != null ? this.icls.getName() : this.mcls.getOriginal();
      }

      public String getMapped() {
         return this.mappedName;
      }

      public int getAccess() {
         return this.icls == null ? 2 : this.icls.getAccess();
      }

      public boolean isInterface() {
         return (this.getAccess() & 512) != 0;
      }

      public Collection<Optional<EnhancedRemapper.MClass.MField>> getFields() {
         return this.fieldsView;
      }

      public Optional<EnhancedRemapper.MClass.MField> getField(String name, @Nullable String desc) {
         if (desc == null) {
            return this.fields.computeIfAbsent(name, k -> Optional.empty());
         }

         Optional<EnhancedRemapper.MClass.MField> ret = this.fields.get(name + desc);
         if (ret == null) {
            ret = this.getField(name, null);
            this.fields.put(name + desc, ret);
         }

         return ret;
      }

      public Collection<Optional<EnhancedRemapper.MClass.MMethod>> getMethods() {
         return this.methodsView;
      }

      public Optional<EnhancedRemapper.MClass.MMethod> getMethod(String name, String desc) {
         return this.methods.computeIfAbsent(name + desc, k -> Optional.empty());
      }

      @Override
      public String toString() {
         return this.getName();
      }

      public class MField {
         private final ClassProvider.IFieldInfo ifld;
         private final IMappingFile.IField mfld;
         private final String mappedName;
         private final String key;

         MField(ClassProvider.IFieldInfo ifld, IMappingFile.IField mfld) {
            this.ifld = ifld;
            this.mfld = mfld;
            this.mappedName = mfld == null ? ifld.getName() : mfld.getMapped();
            this.key = this.getDescriptor() == null ? this.getName() : this.getName() + this.getDescriptor();
         }

         public String getName() {
            return this.ifld != null ? this.ifld.getName() : this.mfld.getOriginal();
         }

         public String getDescriptor() {
            return this.ifld != null ? this.ifld.getDescriptor() : this.mfld.getDescriptor();
         }

         public String getMapped() {
            return this.mappedName;
         }

         public String getKey() {
            return this.key;
         }

         @Override
         public String toString() {
            return MClass.this.getName() + '/' + this.getName() + ' ' + this.getDescriptor();
         }
      }

      public class MMethod {
         private final ClassProvider.IMethodInfo imtd;
         private final IMappingFile.IMethod mmtd;
         private String mappedName;
         private final String[] params;
         private final String key;

         MMethod(ClassProvider.IMethodInfo imtd, IMappingFile.IMethod mmtd) {
            this.imtd = imtd;
            this.mmtd = mmtd;
            if (mmtd != null && !mmtd.getDescriptor().contains("()")) {
               List<String> tmp = new ArrayList<>();
               if (imtd != null && (imtd.getAccess() & 8) == 0) {
                  tmp.add("this");
               }

               Type[] args = Type.getArgumentTypes(mmtd.getDescriptor());

               for (int x = 0; x < args.length; x++) {
                  String name = mmtd.remapParameter(x, null);
                  tmp.add(name);
                  if (args[x].getSize() == 2) {
                     tmp.add(name);
                  }
               }

               this.params = tmp.toArray(new String[tmp.size()]);
            } else {
               this.params = null;
            }

            this.key = this.getName() + this.getDescriptor();
         }

         public String getName() {
            return this.imtd != null ? this.imtd.getName() : this.mmtd.getOriginal();
         }

         public String getDescriptor() {
            return this.imtd != null ? this.imtd.getDescriptor() : this.mmtd.getDescriptor();
         }

         public String getMapped() {
            return this.mappedName == null ? (this.mmtd == null ? this.getName() : this.mmtd.getMapped()) : this.mappedName;
         }

         public String getKey() {
            return this.key;
         }

         public void setMapped(String name) {
            this.mappedName = name;
         }

         public boolean hasMapping() {
            return this.mmtd != null;
         }

         public int getAccess() {
            return this.imtd == null ? 2 : this.imtd.getAccess();
         }

         public boolean isInterfaceInheritable() {
            return (this.getAccess() & 10) == 0;
         }

         public String mapParameter(int index, String name) {
            return this.params != null && index >= 0 && index < this.params.length ? this.params[index] : name;
         }

         @Override
         public String toString() {
            return MClass.this.getName() + '/' + this.getName() + this.getDescriptor();
         }
      }
   }
}
