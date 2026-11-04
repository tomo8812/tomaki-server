package org.valkyrienskies.mod.mixin.mod_compat.etf;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(value = class_2586.class, priority = 1200)
public abstract class MixinBlockEntity implements ETFEntity {
   public float etf$distanceTo(class_1297 entity) {
      class_638 level = class_310.method_1551().field_1687;
      class_243 aW = VSGameUtilsKt.toWorldCoordinates(level, class_243.method_24953(this.etf$getBlockPos()));
      class_243 bW = VSGameUtilsKt.toWorldCoordinates(level, entity.method_19538());
      double dist = aW.method_1022(bW);
      return (float)dist;
   }
}
