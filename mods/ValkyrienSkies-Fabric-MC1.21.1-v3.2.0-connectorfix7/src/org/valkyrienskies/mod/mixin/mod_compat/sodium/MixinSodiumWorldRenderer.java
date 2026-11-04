package org.valkyrienskies.mod.mixin.mod_compat.sodium;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.SortedSet;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_3191;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4599;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_824;
import net.minecraft.class_4597.class_4598;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSClientGameUtils;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.assembly.SeamlessChunksManager;
import org.valkyrienskies.mod.mixinducks.mod_compat.sodium.RenderSectionManagerDuck;

@Mixin(SodiumWorldRenderer.class)
public abstract class MixinSodiumWorldRenderer {
   @Shadow
   private class_638 level;
   @Shadow
   private RenderSectionManager renderSectionManager;
   @Unique
   private SortedRenderLists currentRenderLists;

   @Redirect(method = "renderBlockEntity", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"))
   private static void renderShipBlockEntityInShipyard(
      class_4587 instance,
      double d,
      double e,
      double f,
      class_4587 ignore,
      class_4599 bufferBuilders,
      Long2ObjectMap<SortedSet<class_3191>> blockBreakingProgressions,
      float tickDelta,
      class_4598 immediate,
      double camX,
      double camY,
      double camZ,
      class_824 dispatcher,
      class_2586 entity
   ) {
      class_2338 pos = entity.method_11016();
      if (dispatcher.field_4348 instanceof class_638 level) {
         ClientShip ship = VSGameUtilsKt.getShipObjectManagingPos(level, pos);
         if (ship == null) {
            instance.method_22904(d, e, f);
         } else {
            VSClientGameUtils.transformRenderWithShip(ship.getRenderTransform(), instance, pos, camX, camY, camZ);
         }
      }
   }

   @Shadow
   protected abstract void renderBlockEntities(
      class_4587 var1,
      class_4599 var2,
      Long2ObjectMap<SortedSet<class_3191>> var3,
      float var4,
      class_4598 var5,
      double var6,
      double var8,
      double var10,
      class_824 var12,
      class_746 var13,
      LocalBooleanRef var14
   );

   @Redirect(
      method = "renderBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderBuffers;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;Lnet/minecraft/client/Camera;FLcom/llamalad7/mixinextras/sugar/ref/LocalBooleanRef;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/caffeinemc/mods/sodium/client/render/SodiumWorldRenderer;renderBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderBuffers;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDDLnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;Lnet/minecraft/client/player/LocalPlayer;Lcom/llamalad7/mixinextras/sugar/ref/LocalBooleanRef;)V"
      )
   )
   public void renderShipBlockEntities(
      SodiumWorldRenderer instance,
      class_4587 matrices,
      class_4599 bufferBuilders,
      Long2ObjectMap<SortedSet<class_3191>> blockBreakingProgressions,
      float tickDelta,
      class_4598 immediate,
      double x,
      double y,
      double z,
      class_824 blockEntityRenderer,
      class_746 player,
      LocalBooleanRef isGlowing
   ) {
      this.renderBlockEntities(matrices, bufferBuilders, blockBreakingProgressions, tickDelta, immediate, x, y, z, blockEntityRenderer, player, isGlowing);

      for (SortedRenderLists renderLists : ((RenderSectionManagerDuck)this.renderSectionManager).vs_getShipRenderLists().values()) {
         this.currentRenderLists = renderLists;
         this.renderBlockEntities(matrices, bufferBuilders, blockBreakingProgressions, tickDelta, immediate, x, y, z, blockEntityRenderer, player, isGlowing);
      }

      this.currentRenderLists = null;
   }

   @ModifyExpressionValue(
      method = "renderBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderBuffers;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDDLnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;Lnet/minecraft/client/player/LocalPlayer;Lcom/llamalad7/mixinextras/sugar/ref/LocalBooleanRef;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;getRenderLists()Lnet/caffeinemc/mods/sodium/client/render/chunk/lists/SortedRenderLists;"
      )
   )
   private SortedRenderLists redirectGetRenderLists(SortedRenderLists original) {
      return this.currentRenderLists != null ? this.currentRenderLists : original;
   }

   @Inject(method = "isEntityVisible", at = @At("HEAD"), cancellable = true)
   private void isEntityVisible(class_1297 entity, CallbackInfoReturnable<Boolean> cir) {
      if (VSGameUtilsKt.isBlockInShipyard(this.level, entity.method_19538())) {
         cir.setReturnValue(true);
      }
   }

   @Inject(method = "setupTerrain", at = @At("HEAD"))
   private void drainShipChunksBeforeLightUpdate(class_4184 camera, Viewport viewport, boolean spectator, boolean updateChunksImmediately, CallbackInfo ci) {
      SeamlessChunksManager manager = SeamlessChunksManager.get();
      if (manager != null) {
         manager.drainDeferredBatch();
      }
   }
}
