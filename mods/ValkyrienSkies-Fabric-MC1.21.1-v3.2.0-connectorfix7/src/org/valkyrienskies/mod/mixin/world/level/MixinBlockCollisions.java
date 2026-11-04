package org.valkyrienskies.mod.mixin.world.level;

import java.util.function.BiFunction;
import net.minecraft.class_1297;
import net.minecraft.class_1941;
import net.minecraft.class_238;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_3532;
import net.minecraft.class_3980;
import net.minecraft.class_5329;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.util.BugFixUtil;

@Mixin(class_5329.class)
public class MixinBlockCollisions {
   @Shadow
   @Final
   @Mutable
   private class_238 field_25169;
   @Shadow
   @Final
   @Mutable
   private class_3980 field_25171;
   @Shadow
   @Final
   @Mutable
   private class_265 field_25173;

   @Inject(method = "<init>", at = @At("RETURN"))
   private void postInit(class_1941 collisionGetter, class_1297 entity, class_238 aabb, boolean bl, BiFunction biFunction, CallbackInfo ci) {
      if (BugFixUtil.INSTANCE.isCollisionBoxTooBig(aabb)) {
         class_238 newBox = new class_238(aabb.field_1323, aabb.field_1322, aabb.field_1321, aabb.field_1323, aabb.field_1322, aabb.field_1321);
         this.field_25173 = class_259.method_1078(newBox);
         this.field_25169 = newBox;
         int i = class_3532.method_15357(newBox.field_1323 - 1.0E-7) - 1;
         int j = class_3532.method_15357(newBox.field_1320 + 1.0E-7) + 1;
         int k = class_3532.method_15357(newBox.field_1322 - 1.0E-7) - 1;
         int l = class_3532.method_15357(newBox.field_1325 + 1.0E-7) + 1;
         int m = class_3532.method_15357(newBox.field_1321 - 1.0E-7) - 1;
         int n = class_3532.method_15357(newBox.field_1324 + 1.0E-7) + 1;
         this.field_25171 = new class_3980(i, k, m, j, l, n);
      }
   }
}
