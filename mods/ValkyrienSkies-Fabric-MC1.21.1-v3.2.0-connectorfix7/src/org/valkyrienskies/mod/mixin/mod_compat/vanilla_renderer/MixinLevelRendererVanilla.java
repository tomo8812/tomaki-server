package org.valkyrienskies.mod.mixin.mod_compat.vanilla_renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.ListIterator;
import java.util.WeakHashMap;
import net.minecraft.class_1921;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_284;
import net.minecraft.class_291;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4604;
import net.minecraft.class_5944;
import net.minecraft.class_638;
import net.minecraft.class_757;
import net.minecraft.class_761;
import net.minecraft.class_765;
import net.minecraft.class_769;
import net.minecraft.class_9779;
import net.minecraft.class_846.class_849;
import net.minecraft.class_846.class_851;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.primitives.AABBd;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.util.datastructures.BlockPos2ByteOpenHashMap;
import org.valkyrienskies.mod.common.VSClientGameUtils;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.assembly.SeamlessChunksManager;
import org.valkyrienskies.mod.common.config.ShipRenderer;
import org.valkyrienskies.mod.common.config.ShipRendererKt;
import org.valkyrienskies.mod.common.hooks.VSGameEvents;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.mixinducks.client.render.IVSViewAreaMethods;
import org.valkyrienskies.mod.mixinducks.client.render.LevelRendererVanillaDuck;
import org.valkyrienskies.mod.mixinducks.mod_compat.vanilla_renderer.LevelRendererDuck;

@Mixin(value = class_761.class, priority = 999)
public abstract class MixinLevelRendererVanilla implements LevelRendererDuck, LevelRendererVanillaDuck {
   @Unique
   private final WeakHashMap<ClientShip, ObjectArrayList<class_851>> vs$shipRenderChunks = new WeakHashMap<>();
   @Shadow
   private class_638 field_4085;
   @Shadow
   @Final
   @Mutable
   private ObjectArrayList<class_851> field_45616;
   @Shadow
   @Nullable
   private class_769 field_4112;
   @Shadow
   @Final
   private class_310 field_4088;
   @Unique
   private BlockPos2ByteOpenHashMap vs$visibileShipChunks = new BlockPos2ByteOpenHashMap();

   @Redirect(method = "compileSections", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;distSqr(Lnet/minecraft/core/Vec3i;)D"), require = 0)
   private double includeShipChunksInNearChunks(class_2338 b, class_2382 v) {
      return VSGameUtilsKt.squaredDistanceBetweenInclShips(
         this.field_4085, b.method_10263(), b.method_10264(), b.method_10260(), v.method_10263(), v.method_10264(), v.method_10260()
      );
   }

   @Override
   public void vs$setNeedsFrustumUpdate() {
   }

   @Inject(method = "setupRender", at = @At("HEAD"))
   private void vs$drainShipChunksBeforeRender(class_4184 camera, class_4604 frustum, boolean bl, boolean bl2, CallbackInfo ci) {
      SeamlessChunksManager manager = SeamlessChunksManager.get();
      if (manager != null) {
         manager.drainDeferredBatch();
      }
   }

   @Inject(method = "setupRender", at = @At("RETURN"))
   private void preSetupRender(class_4184 camera, class_4604 frustum, boolean bl, boolean bl2, CallbackInfo ci) {
      this.vs$addShipVisibleChunks(frustum);
   }

   @Override
   public void vs$addShipVisibleChunks(class_4604 frustum) {
      IVSViewAreaMethods shipViewArea = (IVSViewAreaMethods)this.field_4112;

      for (ClientShip shipObject : VSGameUtilsKt.getShipObjectWorld(this.field_4085).getLoadedShips()) {
         if (ShipRendererKt.getShipRenderer(shipObject) == ShipRenderer.VANILLA
            && frustum.method_23093(VectorConversionsMCKt.toMinecraft(shipObject.getRenderAABB()))) {
            shipObject.getActiveChunksSet()
               .forEach(
                  (x, z) -> {
                     class_2818 levelChunk = this.field_4085.method_8497(x, z);

                     for (int y = this.field_4085.method_32891(); y < this.field_4085.method_31597(); y++) {
                        if (!this.vs$visibileShipChunks.contains(x, y, z)) {
                           class_2826 levelChunkSection = levelChunk.method_38259(y - this.field_4085.method_32891());
                           if (!levelChunkSection.method_38292()) {
                              class_851 renderChunk = shipViewArea.vs$getShipRenderSection(x, y, z);
                              if (renderChunk == null) {
                                 renderChunk = shipViewArea.vs$getOrCreateShipRenderSection(x, y, z);
                              }

                              if (renderChunk != null) {
                                 AABBd b2 = new AABBd((x << 4) - 0.6, (y << 4) - 0.6, (z << 4) - 0.6, (x << 4) + 15.6, (y << 4) + 15.6, (z << 4) + 15.6)
                                    .transform(shipObject.getRenderTransform().getShipToWorld());
                                 if (frustum.method_23093(VectorConversionsMCKt.toMinecraft(b2))) {
                                    this.vs$shipRenderChunks.computeIfAbsent(shipObject, k -> new ObjectArrayList()).add(renderChunk);
                                    this.vs$visibileShipChunks.put(x, y, z, (byte)1);
                                    this.field_45616.add(renderChunk);
                                 }
                              }
                           }
                        }
                     }
                  }
               );
         }
      }
   }

   @Inject(method = "*", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;clear()V"))
   private void clearShipChunks(CallbackInfo ci) {
      this.vs$shipRenderChunks.forEach((ship, chunks) -> chunks.clear());
      this.vs$visibileShipChunks = new BlockPos2ByteOpenHashMap();
   }

   @WrapOperation(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"), method = "renderLevel")
   private void fixRenderTransforms(
      class_4587 instance,
      double renderX,
      double renderY,
      double renderZ,
      Operation<Void> original,
      class_9779 deltaTracker,
      boolean bl,
      class_4184 camera,
      class_757 gameRenderer,
      class_765 lightTexture,
      Matrix4f matrix4f,
      Matrix4f matrix4f2
   ) {
      class_243 camPos = camera.method_19326();
      double blockPosX = renderX + camPos.field_1352;
      double blockPosY = renderY + camPos.field_1351;
      double blockPosZ = renderZ + camPos.field_1350;
      Ship ship = VSGameUtilsKt.getShipManagingPos(this.field_4085, blockPosX, blockPosY, blockPosZ);
      if (ship != null) {
         VSClientGameUtils.transformRenderWithShip(
            ((ClientShip)ship).getRenderTransform(), instance, blockPosX, blockPosY, blockPosZ, camPos.field_1352, camPos.field_1351, camPos.field_1350
         );
      } else {
         original.call(new Object[]{instance, renderX, renderY, renderZ});
      }
   }

   @WrapOperation(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"
      ),
      method = "renderLevel"
   )
   private void redirectRenderChunkLayer(
      class_761 receiver,
      class_1921 renderType,
      double camX,
      double camY,
      double camZ,
      Matrix4f poseMatrix,
      Matrix4f projectionMatrix,
      Operation<Void> renderChunkLayer
   ) {
      renderChunkLayer.call(new Object[]{receiver, renderType, camX, camY, camZ, poseMatrix, projectionMatrix});
      class_4587 eventPoseStack = new class_4587();
      eventPoseStack.method_34425(poseMatrix);
      VSGameEvents.INSTANCE
         .getShipsStartRendering()
         .emit(new VSGameEvents.ShipStartRenderEvent(receiver, renderType, eventPoseStack, camX, camY, camZ, projectionMatrix));
      class_4587 poseStack = new class_4587();
      poseStack.method_34425(poseMatrix);
      this.vs$shipRenderChunks
         .forEach(
            (ship, chunks) -> {
               poseStack.method_22903();
               Vector3dc center = ship.getRenderTransform().getWorldToShip().transformPosition(new Vector3d(camX, camY, camZ));
               VSClientGameUtils.transformRenderWithShip(ship.getRenderTransform(), poseStack, center.x(), center.y(), center.z(), camX, camY, camZ);
               VSGameEvents.ShipRenderEvent event = new VSGameEvents.ShipRenderEvent(
                  receiver, renderType, poseStack, camX, camY, camZ, projectionMatrix, ship, chunks
               );
               VSGameEvents.INSTANCE.getRenderShip().emit(event);
               this.renderChunkLayer(renderType, poseStack, center.x(), center.y(), center.z(), projectionMatrix, chunks);
               VSGameEvents.INSTANCE.getPostRenderShip().emit(event);
               poseStack.method_22909();
            }
         );
   }

   @Unique
   private void renderChunkLayer(
      class_1921 renderType, class_4587 poseStack, double d, double e, double f, Matrix4f matrix4f, ObjectList<class_851> chunksToRender
   ) {
      RenderSystem.assertOnRenderThread();
      renderType.method_23516();
      this.field_4088.method_16011().method_15396("filterempty");
      this.field_4088.method_16011().method_15403(() -> "render_" + renderType);
      boolean bl = renderType != class_1921.method_23583();
      ListIterator objectListIterator = chunksToRender.listIterator(bl ? 0 : chunksToRender.size());
      class_5944 shaderInstance = RenderSystem.getShader();

      for (int k = 0; k < 12; k++) {
         int l = RenderSystem.getShaderTexture(k);
         shaderInstance.method_34583("Sampler" + k, l);
      }

      if (shaderInstance.field_29470 != null) {
         shaderInstance.field_29470.method_1250(poseStack.method_23760().method_23761());
      }

      if (shaderInstance.field_29471 != null) {
         shaderInstance.field_29471.method_1250(matrix4f);
      }

      if (shaderInstance.field_29474 != null) {
         shaderInstance.field_29474.method_1253(RenderSystem.getShaderColor());
      }

      if (shaderInstance.field_29477 != null) {
         shaderInstance.field_29477.method_1251(RenderSystem.getShaderFogStart());
      }

      if (shaderInstance.field_29478 != null) {
         shaderInstance.field_29478.method_1251(RenderSystem.getShaderFogEnd());
      }

      if (shaderInstance.field_29479 != null) {
         shaderInstance.field_29479.method_1253(RenderSystem.getShaderFogColor());
      }

      if (shaderInstance.field_36373 != null) {
         shaderInstance.field_36373.method_35649(RenderSystem.getShaderFogShape().method_40036());
      }

      if (shaderInstance.field_29472 != null) {
         shaderInstance.field_29472.method_1250(RenderSystem.getTextureMatrix());
      }

      if (shaderInstance.field_29481 != null) {
         shaderInstance.field_29481.method_1251(RenderSystem.getShaderGameTime());
      }

      RenderSystem.setupShaderLights(shaderInstance);
      shaderInstance.method_34586();
      class_284 uniform = shaderInstance.field_29482;

      while (bl ? objectListIterator.hasNext() : objectListIterator.hasPrevious()) {
         class_851 renderChunk = bl ? (class_851)objectListIterator.next() : (class_851)objectListIterator.previous();
         class_849 compiledSection = renderChunk.method_3677();
         if (!compiledSection.method_3641(renderType)) {
            class_291 vertexBuffer = renderChunk.method_3656(renderType);
            class_2338 blockPos = renderChunk.method_3670();
            if (uniform != null) {
               uniform.method_1249((float)(blockPos.method_10263() - d), (float)(blockPos.method_10264() - e), (float)(blockPos.method_10260() - f));
               uniform.method_1300();
            }

            vertexBuffer.method_1353();
            vertexBuffer.method_35665();
         }
      }

      if (uniform != null) {
         uniform.method_34413(new Vector3f());
      }

      shaderInstance.method_34585();
      class_291.method_1354();
      this.field_4088.method_16011().method_15407();
      renderType.method_23518();
   }
}
