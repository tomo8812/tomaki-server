package reloc.net.minecraftforge.fart.api;

import java.io.Closeable;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;
import reloc.net.minecraftforge.fart.internal.RenamerBuilder;

public interface Renamer extends Closeable {
   void run(File var1, File var2);

   static Renamer.Builder builder() {
      return new RenamerBuilder();
   }

   interface Builder {
      Renamer.Builder lib(File var1);

      Renamer.Builder lib(Path var1);

      Renamer.Builder map(File var1);

      Renamer.Builder addClassProvider(ClassProvider var1);

      Renamer.Builder withJvmClasspath();

      Renamer.Builder add(Transformer var1);

      Renamer.Builder add(Transformer.Factory var1);

      Renamer.Builder threads(int var1);

      Renamer.Builder logger(Consumer<String> var1);

      Renamer.Builder debug(Consumer<String> var1);

      Renamer.Builder setCollectAbstractParams(boolean var1);

      Renamer.Builder ignoreJarPathPrefix(String... var1);

      Renamer build();
   }
}
