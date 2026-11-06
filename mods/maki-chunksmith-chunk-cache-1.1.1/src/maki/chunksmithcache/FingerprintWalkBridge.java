package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

final class FingerprintWalkBridge {
   private FingerprintWalkBridge() {
   }

   static Stream<Path> walk(Path var0, FileVisitOption... var1) throws IOException {
      Path var2 = var0.toAbsolutePath().normalize().resolve("chunksmith").resolve("tasks");
      return Files.walk(var0, var1).filter(var1x -> !var1x.toAbsolutePath().normalize().startsWith(var2));
   }
}
