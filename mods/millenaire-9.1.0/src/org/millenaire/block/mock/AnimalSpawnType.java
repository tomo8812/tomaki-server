package org.millenaire.block.mock;

import net.minecraft.util.StringRepresentable;

public enum AnimalSpawnType implements StringRepresentable {
   COW("cow"),
   PIG("pig"),
   SHEEP("sheep"),
   CHICKEN("chicken"),
   SQUID("squid"),
   WOLF("wolf"),
   POLAR_BEAR("polar_bear");

   private final String serializedName;

   AnimalSpawnType(String serializedName) {
      this.serializedName = serializedName;
   }

   public String getSerializedName() {
      return this.serializedName;
   }

   // $VF: synthetic method
   private static AnimalSpawnType[] $values() {
      return new AnimalSpawnType[]{COW, PIG, SHEEP, CHICKEN, SQUID, WOLF, POLAR_BEAR};
   }
}
