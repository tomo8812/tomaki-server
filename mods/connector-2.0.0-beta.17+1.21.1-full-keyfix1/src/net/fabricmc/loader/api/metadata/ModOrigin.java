package net.fabricmc.loader.api.metadata;

import java.nio.file.Path;
import java.util.List;

public interface ModOrigin {
   ModOrigin.Kind getKind();

   List<Path> getPaths();

   String getParentModId();

   String getParentSubLocation();

   enum Kind {
      PATH,
      NESTED,
      UNKNOWN;

      // $VF: synthetic method
      private static ModOrigin.Kind[] $values() {
         return new ModOrigin.Kind[]{PATH, NESTED, UNKNOWN};
      }
   }
}
