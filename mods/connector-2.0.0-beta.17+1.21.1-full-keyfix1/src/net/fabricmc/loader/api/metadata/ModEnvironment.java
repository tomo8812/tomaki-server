package net.fabricmc.loader.api.metadata;

import net.fabricmc.api.EnvType;

public enum ModEnvironment {
   CLIENT,
   SERVER,
   UNIVERSAL;

   public boolean matches(EnvType type) {
      return switch (this) {
         case CLIENT -> type == EnvType.CLIENT;
         case SERVER -> type == EnvType.SERVER;
         case UNIVERSAL -> true;
      };
   }

   // $VF: synthetic method
   private static ModEnvironment[] $values() {
      return new ModEnvironment[]{CLIENT, SERVER, UNIVERSAL};
   }
}
