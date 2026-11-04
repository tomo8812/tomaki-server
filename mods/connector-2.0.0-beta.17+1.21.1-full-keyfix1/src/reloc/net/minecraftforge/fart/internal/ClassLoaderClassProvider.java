package reloc.net.minecraftforge.fart.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import reloc.net.minecraftforge.fart.api.ClassProvider;

public class ClassLoaderClassProvider implements ClassProvider {
   private final ClassLoader classLoader;

   public ClassLoaderClassProvider(@Nullable ClassLoader classLoader) {
      this.classLoader = classLoader == null ? this.getClass().getClassLoader() : classLoader;
   }

   @Override
   public Optional<? extends ClassProvider.IClassInfo> getClass(String name) {
      try {
         Class<?> cls = Class.forName(name.replace('/', '.'), false, this.classLoader);
         return Optional.of(new ClassProviderImpl.ClassInfo(cls));
      } catch (ClassNotFoundException | NoClassDefFoundError ex) {
         return Optional.empty();
      }
   }

   @Override
   public Optional<byte[]> getClassBytes(String cls) {
      String resource = cls + ".class";

      try (InputStream is = this.classLoader.getResourceAsStream(resource)) {
         if (is != null) {
            return Optional.of(Util.toByteArray(is));
         }
      } catch (IOException e) {
         throw new RuntimeException("Could not get data to compute class info in file: " + resource, e);
      }

      return Optional.empty();
   }

   @Override
   public void close() throws IOException {
   }
}
