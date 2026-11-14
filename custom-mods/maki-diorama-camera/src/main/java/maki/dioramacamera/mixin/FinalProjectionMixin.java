package maki.dioramacamera.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.diorama.waypoint.WorldProjection;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Capture after mounted-ship camera setup, at the single main-world render call. */
@Mixin(value=GameRenderer.class,remap=false)
public abstract class FinalProjectionMixin {
    @WrapOperation(method="renderLevel(Lnet/minecraft/client/DeltaTracker;)V",
            at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"),
            remap=false)
    private void maki$finalWorldProjection(LevelRenderer level, DeltaTracker delta, boolean outline,
            Camera camera, GameRenderer renderer, LightTexture light, Matrix4f view, Matrix4f projection,
            Operation<Void> original) {
        WorldProjection.capture(projection,view,camera.getPosition());
        original.call(level,delta,outline,camera,renderer,light,view,projection);
    }
}
