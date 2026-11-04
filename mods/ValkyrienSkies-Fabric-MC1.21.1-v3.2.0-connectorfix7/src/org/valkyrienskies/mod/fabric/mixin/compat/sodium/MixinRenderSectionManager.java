package org.valkyrienskies.mod.fabric.mixin.compat.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Objects;
import net.caffeinemc.mods.sodium.client.gl.device.CommandList;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.SortBehavior;
import net.caffeinemc.mods.sodium.client.render.viewport.CameraTransform;
import org.joml.Matrix4f;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VSClientGameUtils;
import org.valkyrienskies.mod.mixinducks.mod_compat.sodium.RenderSectionManagerDuck;

@Mixin(value = RenderSectionManager.class, remap = false)
public class MixinRenderSectionManager {
   @Shadow
   @Final
   private ChunkRenderer chunkRenderer;
   @Shadow
   @Final
   private SortBehavior sortBehavior;

   @Inject(at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/gl/device/CommandList;flush()V"), method = "renderLayer")
   private void redirectRenderLayer(
      ChunkRenderMatrices matrices, TerrainRenderPass pass, double camX, double camY, double camZ, CallbackInfo ci, @Local CommandList commandList
   ) {
      ((RenderSectionManagerDuck)this)
         .vs_getShipRenderLists()
         .forEach(
            (ship, renderList) -> {
               Matrix4f newModelView = new Matrix4f(matrices.modelView());
               Vector3dc center = ship.getRenderTransform().getPositionInShip();
               VSClientGameUtils.transformRenderWithShip(ship.getRenderTransform(), newModelView, center.x(), center.y(), center.z(), camX, camY, camZ);
               ChunkRenderMatrices newMatrices = new ChunkRenderMatrices(matrices.projection(), newModelView);
               this.chunkRenderer
                  .render(
                     newMatrices,
                     commandList,
                     renderList,
                     pass,
                     new CameraTransform(center.x(), center.y(), center.z()),
                     Objects.equals(this.sortBehavior, SortBehavior.OFF) ^ true
                  );
               commandList.close();
            }
         );
   }
}
