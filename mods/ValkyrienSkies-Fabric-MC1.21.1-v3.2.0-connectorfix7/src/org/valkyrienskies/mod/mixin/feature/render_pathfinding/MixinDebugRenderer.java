package org.valkyrienskies.mod.mixin.feature.render_pathfinding;

import net.minecraft.class_4587;
import net.minecraft.class_863;
import net.minecraft.class_868;
import net.minecraft.class_4597.class_4598;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.config.VSGameConfig;

@Mixin(class_863.class)
public class MixinDebugRenderer {
   @Shadow
   @Final
   public class_868 field_4523;

   @Inject(method = "render", at = @At("HEAD"))
   void render(class_4587 matrixStack, class_4598 buffer, double camX, double camY, double camZ, CallbackInfo ci) {
      if (VSGameConfig.COMMON.ADVANCED.getRenderPathfinding()) {
         this.field_4523.method_23109(matrixStack, buffer, camX, camY, camZ);
      }
   }
}
