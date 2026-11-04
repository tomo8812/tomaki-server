package reloc.net.minecraftforge.fart.api;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import reloc.net.minecraftforge.fart.internal.ClassLoaderClassProvider;
import reloc.net.minecraftforge.fart.internal.ClassProviderBuilderImpl;

public interface ClassProvider extends Closeable {
   static ClassProvider.Builder builder() {
      return new ClassProviderBuilderImpl();
   }

   static ClassProvider fromPaths(Path... paths) {
      ClassProvider.Builder builder = builder().shouldCacheAll(true);

      for (Path path : paths) {
         builder.addLibrary(path);
      }

      return builder.build();
   }

   static ClassProvider fromJvmClasspath() {
      return new ClassLoaderClassProvider(null);
   }

   static ClassProvider fromClassLoader(@Nullable ClassLoader classLoader) {
      return new ClassLoaderClassProvider(classLoader);
   }

   Optional<? extends ClassProvider.IClassInfo> getClass(String var1);

   Optional<byte[]> getClassBytes(String var1);

   interface Builder {
      ClassProvider.Builder addLibrary(Path var1);

      ClassProvider.Builder addClass(String var1, byte[] var2);

      ClassProvider.Builder shouldCacheAll(boolean var1);

      ClassProvider build();
   }

   interface IClassInfo {
      int getAccess();

      String getName();

      @Nullable
      String getSuper();

      Collection<String> getInterfaces();

      Collection<? extends ClassProvider.IFieldInfo> getFields();

      Optional<? extends ClassProvider.IFieldInfo> getField(String var1);

      Collection<? extends ClassProvider.IMethodInfo> getMethods();

      Optional<? extends ClassProvider.IMethodInfo> getMethod(String var1, String var2);
   }

   interface IFieldInfo {
      int getAccess();

      String getName();

      String getDescriptor();
   }

   interface IMethodInfo {
      int getAccess();

      String getName();

      String getDescriptor();
   }
}
