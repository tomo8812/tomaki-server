package org.valkyrienskies.mod.fabric.mixin.compat.connectiblechains.client;

import com.github.legoatoom.connectiblechains.client.render.entity.ChainKnotEntityRenderer;
import com.github.legoatoom.connectiblechains.client.render.entity.state.ChainKnotEntityRenderState;
import com.github.legoatoom.connectiblechains.client.render.entity.state.ChainKnotEntityRenderState.ChainData;
import com.github.legoatoom.connectiblechains.entity.ChainKnotEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.class_1297;
import net.minecraft.class_2374;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4604;
import net.minecraft.class_638;
import org.joml.Quaternionf;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.CompatUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(ChainKnotEntityRenderer.class)
public abstract class MixinChainKnotEntityRenderer {
   @Unique
   class_638 valkyrienskies$level;

   @Inject(
      method = "render(Lcom/github/legoatoom/connectiblechains/entity/ChainKnotEntity;Lcom/github/legoatoom/connectiblechains/client/render/entity/state/ChainKnotEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
      at = @At("HEAD")
   )
   private void saveEntity(
      ChainKnotEntity entity, ChainKnotEntityRenderState state, class_4587 matrices, class_4597 vertexConsumers, int light, float tickDelta, CallbackInfo ci
   ) {
      this.valkyrienskies$level = (class_638)entity.method_37908();
   }

   @WrapOperation(
      method = "shouldRender(Lcom/github/legoatoom/connectiblechains/entity/ChainKnotEntity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/culling/Frustum;isVisible(Lnet/minecraft/world/phys/AABB;)Z"),
      require = 0
   )
   private boolean adjustHolderAABB(class_4604 frustum, class_238 aabb, Operation<Boolean> original, @Local class_1297 chainHolder) {
      return frustum.method_23093(VSGameUtilsKt.transformRenderAABBToWorld((class_638)chainHolder.method_37908(), chainHolder.method_19538(), aabb));
   }

   @Inject(method = "renderChainLink", at = @At(value = "NEW", target = "org/joml/Vector3f"))
   private void adjustPositions(
      class_4587 matrices,
      class_4597 vertexConsumerProvider,
      ChainData chainData,
      CallbackInfo ci,
      @Local(ordinal = 1) LocalRef<class_243> srcPos,
      @Local(ordinal = 2) LocalRef<class_243> destPos
   ) {
      if (this.valkyrienskies$level != null) {
         ClientShip srcShip = VSGameUtilsKt.getLoadedShipManagingPos(this.valkyrienskies$level, (class_2374)srcPos.get());
         ClientShip destShip = VSGameUtilsKt.getLoadedShipManagingPos(this.valkyrienskies$level, (class_2374)destPos.get());
         if (srcShip != null || destShip != null) {
            class_243 newSrcPos = CompatUtil.INSTANCE.toSameSpaceAs(this.valkyrienskies$level, (class_243)srcPos.get(), (Ship)null);
            class_243 newDestPos = CompatUtil.INSTANCE.toSameSpaceAs(this.valkyrienskies$level, (class_243)destPos.get(), (Ship)null);
            srcPos.set(newSrcPos);
            if (srcShip != null) {
               Vector3dc scale = srcShip.getRenderTransform().getScaling();
               destPos.set(newSrcPos.method_1019(newDestPos.method_1020(newSrcPos).method_18805(1.0 / scale.x(), 1.0 / scale.y(), 1.0 / scale.z())));
               matrices.method_22907(new Quaternionf(srcShip.getRenderTransform().getShipToWorldRotation()).invert());
            } else {
               destPos.set(newDestPos);
            }
         }
      }
   }
}
