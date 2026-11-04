package reloc.net.minecraftforge.srgutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface INamedMappingFile {
   static INamedMappingFile load(File path) throws IOException {
      try (InputStream in = new FileInputStream(path)) {
         return load(in);
      }
   }

   static INamedMappingFile load(InputStream in) throws IOException {
      return InternalUtils.loadNamed(in);
   }

   List<String> getNames();

   IMappingFile getMap(String var1, String var2);

   default void write(Path path, IMappingFile.Format format) throws IOException {
      this.write(path, format, this.getNames().toArray(new String[this.getNames().size()]));
   }

   void write(Path var1, IMappingFile.Format var2, String... var3) throws IOException;
}
