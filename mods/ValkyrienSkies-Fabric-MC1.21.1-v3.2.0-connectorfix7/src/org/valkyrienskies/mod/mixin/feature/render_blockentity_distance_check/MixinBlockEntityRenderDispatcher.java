package org.valkyrienskies.mod.mixin.feature.render_blockentity_distance_check;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_4184;
import net.minecraft.class_824;
import net.minecraft.class_827;
import org.joml.Matrix4dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_824.class)
public class MixinBlockEntityRenderDispatcher {
   @Shadow
   public class_1937 field_4348;
   @Shadow
   public class_4184 field_4344;

   @WrapOperation(
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;shouldRender(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/phys/Vec3;)Z"
      )
   )
   private <E extends class_2586> boolean isTileEntityInRenderRange(class_827<E> instance, E blockEntity, class_243 cameraPos, Operation<Boolean> operation) {
      if ((Boolean)operation.call(new Object[]{instance, blockEntity, cameraPos})) {
         return true;
      } else {
         class_2338 bePos = blockEntity.method_11016();
         if (VSGameUtilsKt.getLoadedShipManagingPos(this.field_4348, bePos) instanceof ClientShip ship) {
            Matrix4dc m = ship.getRenderTransform().getShipToWorld();
            return new class_243(
                  m.m00() * bePos.method_10263() + m.m10() * bePos.method_10264() + m.m20() * bePos.method_10260() + m.m30(),
                  m.m01() * bePos.method_10263() + m.m11() * bePos.method_10264() + m.m21() * bePos.method_10260() + m.m31(),
                  m.m02() * bePos.method_10263() + m.m12() * bePos.method_10264() + m.m22() * bePos.method_10260() + m.m32()
               )
               .method_24802(this.field_4344.method_19326(), instance.method_33893());
         } else {
            return false;
         }
      }
   }
}
