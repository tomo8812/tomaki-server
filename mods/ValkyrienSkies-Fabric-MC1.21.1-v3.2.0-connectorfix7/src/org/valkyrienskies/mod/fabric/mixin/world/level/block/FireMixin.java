package org.valkyrienskies.mod.fabric.mixin.world.level.block;

import net.minecraft.class_1936;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2358;
import net.minecraft.class_2680;
import net.minecraft.class_2758;
import net.minecraft.class_3218;
import net.minecraft.class_4538;
import net.minecraft.class_5819;
import net.minecraft.class_6908;
import net.minecraft.class_2338.class_2339;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_2358.class)
public abstract class FireMixin {
   @Unique
   private boolean isModifyingFireTick = false;
   @Shadow
   @Final
   public static class_2758 field_11092;

   @Inject(method = "tick", at = @At("TAIL"))
   public void fireTickMixin(class_2680 state, class_3218 level, class_2338 pos, class_5819 random, CallbackInfo ci) {
      if (!this.isModifyingFireTick) {
         this.isModifyingFireTick = true;
         double origX = pos.method_10263();
         double origY = pos.method_10264();
         double origZ = pos.method_10260();
         VSGameUtilsKt.transformToNearbyShipsAndWorld(level, origX, origY, origZ, 3.0, (x, y, z) -> {
            class_2338 newPos = class_2338.method_49637(x, y, z);
            if (level.method_22351(newPos)) {
               level.method_8650(pos, false);
            }

            int i = (Integer)state.method_11654(field_11092);
            boolean bl2 = level.method_23753(newPos).method_40220(class_6908.field_41752);
            int k = bl2 ? -50 : 0;
            this.checkBurnOut(level, newPos.method_10078(), 300 + k, random, i, class_2350.field_11039);
            this.checkBurnOut(level, newPos.method_10067(), 300 + k, random, i, class_2350.field_11034);
            this.checkBurnOut(level, newPos.method_10074(), 250 + k, random, i, class_2350.field_11036);
            this.checkBurnOut(level, newPos.method_10084(), 250 + k, random, i, class_2350.field_11033);
            this.checkBurnOut(level, newPos.method_10095(), 300 + k, random, i, class_2350.field_11035);
            this.checkBurnOut(level, newPos.method_10072(), 300 + k, random, i, class_2350.field_11043);
            class_2339 mutableBlockPos = new class_2339();

            for (int l = -1; l <= 1; l++) {
               for (int m = -1; m <= 1; m++) {
                  for (int n = -1; n <= 4; n++) {
                     if (l != 0 || n != 0 || m != 0) {
                        int o = 100;
                        if (n > 1) {
                           o += (n - 1) * 100;
                        }

                        mutableBlockPos.method_25504(newPos, l, n, m);
                        int p = this.method_10194(level, mutableBlockPos);
                        if (p > 0) {
                           int q = (p + 40 + level.method_8407().method_5461() * 7) / (i + 30);
                           if (bl2) {
                              q /= 2;
                           }

                           if (q > 0 && random.method_43048(o) <= q && (!level.method_8419() || !this.method_10192(level, mutableBlockPos))) {
                              int r = Math.min(15, i + random.method_43048(5) / 4);
                              level.method_8652(mutableBlockPos, this.method_24855(level, mutableBlockPos, r), 3);
                           }
                        }
                     }
                  }
               }
            }
         });
         this.isModifyingFireTick = false;
      }
   }

   @Inject(method = "onPlace", at = @At("HEAD"))
   public void onPlaceMixin(class_2680 state, class_1937 level, class_2338 pos, class_2680 oldState, boolean isMoving, CallbackInfo ci) {
      double origX = pos.method_10263();
      double origY = pos.method_10264();
      double origZ = pos.method_10260();
      VSGameUtilsKt.transformToNearbyShipsAndWorld(level, origX, origY, origZ, 1.0, (x, y, z) -> {
         class_2338 newPos = class_2338.method_49637(x, y, z);
         if (level.method_22351(newPos)) {
            level.method_8650(pos, false);
         }
      });
   }

   @Shadow(remap = false)
   private void checkBurnOut(class_1937 level, class_2338 pos, int chance, class_5819 random, int age, class_2350 var6) {
   }

   @Shadow
   protected boolean method_10192(class_1937 level, class_2338 pos) {
      return this.method_10192(level, pos);
   }

   @Shadow
   private int method_10194(class_4538 level, class_2338 pos) {
      return this.method_10194(level, pos);
   }

   @Shadow
   private class_2680 method_24855(class_1936 level, class_2338 pos, int age) {
      return this.method_24855(level, pos, age);
   }
}
