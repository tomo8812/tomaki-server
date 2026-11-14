package dev.diorama.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.diorama.MapSession;
import dev.diorama.render.gpu.GpuTileBaker;
import dev.diorama.waypoint.WorldProjection;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
   @Inject(method = "render(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("HEAD"))
   private void diorama$gpuBakes(CallbackInfo ci) {
      boolean baked = GpuTileBaker.frame();
      MapSession.frame(baked);
   }

   @Inject(
      method = "renderLevel",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;resetProjectionMatrix(Lorg/joml/Matrix4f;)V", ordinal = 0)
   )
   private void diorama$captureWorldProjection(CallbackInfo ci, @Local(ordinal = 0) Matrix4f projectionMatrix) {
      Camera camera = ((GameRenderer)this).getMainCamera();
      WorldProjection.capture(projectionMatrix, new Matrix4f().rotation(camera.rotation().conjugate(new Quaternionf())), camera.getPosition());
   }
}
