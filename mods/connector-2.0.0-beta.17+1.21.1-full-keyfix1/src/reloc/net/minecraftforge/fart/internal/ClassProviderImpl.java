package reloc.net.minecraftforge.fart.internal;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import reloc.net.minecraftforge.fart.api.ClassProvider;

public class ClassProviderImpl implements ClassProvider {
   private final List<FileSystem> fileSystems;
   private final Map<String, Path> sources;
   private final Map<String, Optional<? extends ClassProvider.IClassInfo>> classInfos;
   @Nullable
   private final Map<String, Optional<? extends ClassProvider.IClassInfo>> classCache;

   ClassProviderImpl(
      List<FileSystem> fileSystems, Map<String, Path> sources, Map<String, Optional<? extends ClassProvider.IClassInfo>> classInfos, boolean cacheAll
   ) {
      this.fileSystems = Collections.unmodifiableList(fileSystems);
      this.sources = Collections.unmodifiableMap(sources);
      this.classInfos = Collections.unmodifiableMap(classInfos);
      this.classCache = cacheAll ? new ConcurrentHashMap<>() : null;
   }

   @Override
   public Optional<? extends ClassProvider.IClassInfo> getClass(String name) {
      return this.classCache != null ? this.classCache.computeIfAbsent(name, this::computeClassInfo) : this.computeClassInfo(name);
   }

   @Override
   public Optional<byte[]> getClassBytes(String cls) {
      Path source = this.sources.get(cls);
      if (source == null) {
         return Optional.empty();
      }

      try {
         byte[] data = Util.toByteArray(Files.newInputStream(source));
         return Optional.of(data);
      } catch (IOException e) {
         throw new RuntimeException("Could not get data to compute class info in file: " + source.toAbsolutePath(), e);
      }
   }

   private Optional<? extends ClassProvider.IClassInfo> computeClassInfo(String name) {
      return this.classInfos.containsKey(name) ? this.classInfos.get(name) : this.getClassBytes(name).map(ClassProviderImpl.ClassInfo::new);
   }

   @Override
   public void close() throws IOException {
      for (FileSystem fs : this.fileSystems) {
         fs.close();
      }
   }

   private static class Access {
      private static int[] ACC = new int[23];
      private static String[] NAME = new String[23];
      private final int value;
      private String toString;

      private static void put(int idx, int acc, String name) {
         ACC[idx] = acc;
         NAME[idx] = name;
      }

      public Access(int value) {
         this.value = value;
      }

      public int getValue() {
         return this.value;
      }

      @Override
      public String toString() {
         if (this.toString == null) {
            List<String> ret = new ArrayList<>();

            for (int x = 0; x < ACC.length; x++) {
               if ((this.value & ACC[x]) != 0) {
                  ret.add(NAME[x]);
               }
            }

            if (ret.isEmpty()) {
               this.toString = "default";
            } else {
               this.toString = ret.stream().collect(Collectors.joining(" "));
            }
         }

         return this.toString;
      }

      static {
         int idx = 0;
         put(idx++, 1, "public");
         put(idx++, 2, "private");
         put(idx++, 4, "protected");
         put(idx++, 8, "static");
         put(idx++, 16, "final");
         put(idx++, 32, "super");
         put(idx++, 32, "synchronized");
         put(idx++, 32, "open");
         put(idx++, 32, "transitive");
         put(idx++, 64, "volatile");
         put(idx++, 64, "bridge");
         put(idx++, 64, "static_phase");
         put(idx++, 128, "varargs");
         put(idx++, 128, "transient");
         put(idx++, 256, "native");
         put(idx++, 512, "interface");
         put(idx++, 1024, "abstract");
         put(idx++, 2048, "strict");
         put(idx++, 4096, "synthetic");
         put(idx++, 8192, "annotation");
         put(idx++, 16384, "enum");
         put(idx++, 32768, "mandated");
         put(idx++, 32768, "module");
      }
   }

   public static class ClassInfo implements ClassProvider.IClassInfo {
      private final String name;
      private final ClassProviderImpl.Access access;
      private final String superName;
      private final List<String> interfaces;
      private final Map<String, ClassProviderImpl.ClassInfo.FieldInfo> fields;
      private Collection<ClassProviderImpl.ClassInfo.FieldInfo> fieldsView;
      private final Map<String, ClassProviderImpl.ClassInfo.MethodInfo> methods;
      private Collection<ClassProviderImpl.ClassInfo.MethodInfo> methodsView;

      public ClassInfo(byte[] data) {
         ClassReader reader = new ClassReader(data);
         ClassNode node = new ClassNode();
         reader.accept(node, 1);
         this.name = node.name;
         this.access = new ClassProviderImpl.Access(node.access);
         this.superName = node.superName;
         this.interfaces = node.interfaces.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(node.interfaces);
         if (!node.methods.isEmpty()) {
            this.methods = Collections.unmodifiableMap(
               node.methods
                  .stream()
                  .map(x$0 -> new ClassProviderImpl.ClassInfo.MethodInfo(x$0))
                  .collect(Collectors.toMap(m -> m.getName() + m.getDescriptor(), Function.identity()))
            );
         } else {
            this.methods = null;
         }

         if (!node.fields.isEmpty()) {
            this.fields = Collections.unmodifiableMap(
               node.fields
                  .stream()
                  .map(x$0 -> new ClassProviderImpl.ClassInfo.FieldInfo(x$0))
                  .collect(Collectors.toMap(ClassProviderImpl.ClassInfo.FieldInfo::getName, Function.identity()))
            );
         } else {
            this.fields = null;
         }
      }

      ClassInfo(Class<?> node) {
         this.name = Util.nameToBytecode(node);
         this.access = new ClassProviderImpl.Access(node.getModifiers());
         this.superName = Util.nameToBytecode(node.getSuperclass());
         this.interfaces = Collections.unmodifiableList(
            Arrays.stream(node.getInterfaces()).map(c -> Util.nameToBytecode((Class<?>)c)).collect(Collectors.toList())
         );
         Map<String, ClassProviderImpl.ClassInfo.MethodInfo> mtds = Stream.concat(
               Arrays.stream(node.getConstructors()).map(x$0 -> new ClassProviderImpl.ClassInfo.MethodInfo((Constructor<?>)x$0)),
               Arrays.stream(node.getDeclaredMethods()).map(x$0 -> new ClassProviderImpl.ClassInfo.MethodInfo(x$0))
            )
            .collect(Collectors.toMap(m -> m.getName() + m.getDescriptor(), Function.identity()));
         this.methods = mtds.isEmpty() ? null : Collections.unmodifiableMap(mtds);
         Field[] flds = node.getDeclaredFields();
         if (flds != null && flds.length > 0) {
            this.fields = Collections.unmodifiableMap(
               Arrays.asList(flds)
                  .stream()
                  .map(x$0 -> new ClassProviderImpl.ClassInfo.FieldInfo(x$0))
                  .collect(Collectors.toMap(ClassProviderImpl.ClassInfo.FieldInfo::getName, Function.identity()))
            );
         } else {
            this.fields = null;
         }
      }

      @Override
      public String getName() {
         return this.name;
      }

      @Nullable
      @Override
      public String getSuper() {
         return this.superName;
      }

      @Override
      public int getAccess() {
         return this.access.getValue();
      }

      public ClassProviderImpl.Access getAccessLevel() {
         return this.access;
      }

      @Override
      public Collection<String> getInterfaces() {
         return this.interfaces;
      }

      @Override
      public Collection<? extends ClassProvider.IFieldInfo> getFields() {
         if (this.fieldsView == null) {
            this.fieldsView = this.fields == null ? Collections.emptyList() : Collections.unmodifiableCollection(this.fields.values());
         }

         return this.fieldsView;
      }

      @Override
      public Optional<? extends ClassProvider.IFieldInfo> getField(String name) {
         return this.fields == null ? Optional.empty() : Optional.ofNullable(this.fields.get(name));
      }

      @Override
      public Collection<? extends ClassProvider.IMethodInfo> getMethods() {
         if (this.methodsView == null) {
            this.methodsView = this.methods == null ? Collections.emptyList() : Collections.unmodifiableCollection(this.methods.values());
         }

         return this.methodsView;
      }

      @Override
      public Optional<? extends ClassProvider.IMethodInfo> getMethod(String name, String desc) {
         return this.methods == null ? Optional.empty() : Optional.ofNullable(this.methods.get(name + desc));
      }

      @Override
      public String toString() {
         return this.getAccessLevel().toString() + ' ' + this.getName();
      }

      private class FieldInfo implements ClassProvider.IFieldInfo {
         private final String name;
         private final String desc;
         private final ClassProviderImpl.Access access;

         public FieldInfo(FieldNode node) {
            this.name = node.name;
            this.desc = node.desc;
            this.access = new ClassProviderImpl.Access(node.access);
         }

         public FieldInfo(Field node) {
            this.name = node.getName();
            this.desc = Type.getType(node.getType()).getDescriptor();
            this.access = new ClassProviderImpl.Access(node.getModifiers());
         }

         @Override
         public int getAccess() {
            return this.access.getValue();
         }

         public ClassProviderImpl.Access getAccessLevel() {
            return this.access;
         }

         @Override
         public String getName() {
            return this.name;
         }

         @Override
         public String getDescriptor() {
            return this.desc;
         }

         @Override
         public String toString() {
            return this.getAccessLevel().toString() + ' ' + ClassInfo.this.getName() + '/' + this.getName() + ' ' + this.getDescriptor();
         }
      }

      private class MethodInfo implements ClassProvider.IMethodInfo {
         private final String name;
         private final String desc;
         private final ClassProviderImpl.Access access;

         MethodInfo(MethodNode node) {
            this.name = node.name;
            this.desc = node.desc;
            this.access = new ClassProviderImpl.Access(node.access);
         }

         MethodInfo(Method node) {
            this.name = node.getName();
            this.desc = Type.getMethodDescriptor(node);
            this.access = new ClassProviderImpl.Access(node.getModifiers());
         }

         MethodInfo(Constructor<?> node) {
            this.name = "<init>";
            this.desc = Type.getConstructorDescriptor(node);
            this.access = new ClassProviderImpl.Access(node.getModifiers());
         }

         @Override
         public int getAccess() {
            return this.access.getValue();
         }

         public ClassProviderImpl.Access getAccessLevel() {
            return this.access;
         }

         @Override
         public String getName() {
            return this.name;
         }

         @Override
         public String getDescriptor() {
            return this.desc;
         }

         @Override
         public String toString() {
            return this.getAccessLevel().toString() + ' ' + ClassInfo.this.getName() + '/' + this.getName() + this.getDescriptor();
         }
      }
   }
}
