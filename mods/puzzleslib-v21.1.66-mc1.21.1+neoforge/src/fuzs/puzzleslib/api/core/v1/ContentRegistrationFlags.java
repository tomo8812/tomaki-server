package fuzs.puzzleslib.api.core.v1;

import org.jetbrains.annotations.ApiStatus.Internal;

public enum ContentRegistrationFlags {
   @Deprecated
   BIOME_MODIFICATIONS,
   DYNAMIC_RENDERERS,
   @Deprecated
   COPY_RECIPES;

   @Internal
   public static void throwForFlag(ContentRegistrationFlags flag) {
      throw new IllegalStateException("%s#%s is missing".formatted(ContentRegistrationFlags.class.getSimpleName(), flag));
   }

   // $VF: synthetic method
   private static ContentRegistrationFlags[] $values() {
      return new ContentRegistrationFlags[]{BIOME_MODIFICATIONS, DYNAMIC_RENDERERS, COPY_RECIPES};
   }
}
