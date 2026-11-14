package org.millenaire.block.mock;

import net.minecraft.util.StringRepresentable;

public enum SourceType implements StringRepresentable {
   STONE("stone"),
   SAND("sand"),
   SANDSTONE("sandstone"),
   CLAY("clay"),
   GRAVEL("gravel"),
   GRANITE("granite"),
   DIORITE("diorite"),
   ANDESITE("andesite"),
   SNOW("snow"),
   ICE("ice"),
   RED_SANDSTONE("red_sandstone"),
   QUARTZ("quartz"),
   CALCITE("calcite");

   private final String serializedName;

   SourceType(String serializedName) {
      this.serializedName = serializedName;
   }

   public String getSerializedName() {
      return this.serializedName;
   }

   // $VF: synthetic method
   private static SourceType[] $values() {
      return new SourceType[]{STONE, SAND, SANDSTONE, CLAY, GRAVEL, GRANITE, DIORITE, ANDESITE, SNOW, ICE, RED_SANDSTONE, QUARTZ, CALCITE};
   }
}
