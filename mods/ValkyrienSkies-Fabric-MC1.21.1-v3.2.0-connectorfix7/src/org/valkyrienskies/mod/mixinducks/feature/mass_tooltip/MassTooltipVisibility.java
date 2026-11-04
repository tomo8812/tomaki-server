package org.valkyrienskies.mod.mixinducks.feature.mass_tooltip;

import net.minecraft.class_1836;

public enum MassTooltipVisibility {
   ALWAYS,
   ADVANCED,
   DISABLED;

   public boolean isVisible(class_1836 flag) {
      return this.equals(ALWAYS) || this.equals(ADVANCED) && flag.method_8035();
   }

   // $VF: synthetic method
   private static MassTooltipVisibility[] $values() {
      return new MassTooltipVisibility[]{ALWAYS, ADVANCED, DISABLED};
   }
}
