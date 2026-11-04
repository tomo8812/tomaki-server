package org.millenaire.block.mock;

import net.minecraft.util.StringRepresentable;

public enum CropType implements StringRepresentable {
   WHEAT("wheat"),
   RICE("rice"),
   TURMERIC("turmeric"),
   SUGAR_CANE("sugarcane"),
   POTATO("potato"),
   NETHER_WART("netherwart"),
   VINE("vine"),
   MAIZE("maize"),
   CACAO("cacao"),
   CARROT("carrot"),
   FLOWER("flower"),
   COTTON("cotton"),
   MILLET("millet"),
   PUMPKIN("pumpkin");

   private final String serializedName;

   CropType(String serializedName) {
      this.serializedName = serializedName;
   }

   public String getSerializedName() {
      return this.serializedName;
   }

   // $VF: synthetic method
   private static CropType[] $values() {
      return new CropType[]{WHEAT, RICE, TURMERIC, SUGAR_CANE, POTATO, NETHER_WART, VINE, MAIZE, CACAO, CARROT, FLOWER, COTTON, MILLET, PUMPKIN};
   }
}
