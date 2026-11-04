package org.valkyrienskies.mod.fabric.mixin.feature.fix_tp_ships_cross_dimension;

import java.util.List;
import java.util.Map;
import net.minecraft.class_1923;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_2586;
import net.minecraft.class_2791;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2843;
import net.minecraft.class_5539;
import net.minecraft.class_5562;
import net.minecraft.class_6749;
import net.minecraft.class_2818.class_5564;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_2818.class, priority = 1500)
public abstract class MixinLevelChunk extends class_2791 {
   @Final
   @Shadow
   private Map<class_2338, class_5564> field_27222;
   @Shadow
   @Final
   private static class_5562 field_27221;

   public MixinLevelChunk(
      class_1923 arg, class_2843 arg2, class_5539 arg3, class_2378<class_1959> arg4, long l, @Nullable class_2826[] args, @Nullable class_6749 arg5
   ) {
      super(arg, arg2, arg3, arg4, l, args, arg5);
   }

   @Inject(method = "clearAllBlockEntities", at = @At("HEAD"), cancellable = true)
   public void clearAllBlockEntities(CallbackInfo ci) {
      if (!this.field_34543.isEmpty()) {
         for (class_2586 be : List.copyOf(this.field_34543.values())) {
            be.method_11012();
         }

         this.field_34543.clear();
      }

      if (!this.field_27222.isEmpty()) {
         for (class_5564 ticker : List.copyOf(this.field_27222.values())) {
            ticker.method_31727(field_27221);
         }

         this.field_27222.clear();
      }

      ci.cancel();
   }
}
