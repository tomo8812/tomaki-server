package org.millenaire.block;

import net.minecraft.util.StringRepresentable;

public enum BancoProgress implements StringRepresentable {
   WET("wet"),
   DRYING1("drying1"),
   DRYING2("drying2"),
   DRY("dry");

   private final String name;

   BancoProgress(String name) {
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $VF: synthetic method
   private static BancoProgress[] $values() {
      return new BancoProgress[]{WET, DRYING1, DRYING2, DRY};
   }
}
