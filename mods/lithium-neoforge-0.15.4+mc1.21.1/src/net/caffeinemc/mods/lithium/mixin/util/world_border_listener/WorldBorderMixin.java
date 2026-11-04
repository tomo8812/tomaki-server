package net.caffeinemc.mods.lithium.mixin.util.world_border_listener;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.caffeinemc.mods.lithium.common.world.listeners.WorldBorderListenerOnce;
import net.caffeinemc.mods.lithium.common.world.listeners.WorldBorderPositionListenerMulti;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.border.WorldBorder.BorderExtent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldBorder.class)
public abstract class WorldBorderMixin {
   @Shadow
   private BorderExtent extent;
   @Unique
   private final WorldBorderPositionListenerMulti worldBorderPositionListenerMulti = new WorldBorderPositionListenerMulti();

   @Shadow
   public abstract void addListener(BorderChangeListener var1);

   @Inject(method = "<init>", at = @At("RETURN"))
   private void registerSimpleWorldBorderListenerMulti(CallbackInfo ci) {
      this.addListener(this.worldBorderPositionListenerMulti);
   }

   @Inject(method = "addListener", at = @At("HEAD"), cancellable = true)
   private void addSimpleListenerOnce(BorderChangeListener listener, CallbackInfo ci) {
      if (listener instanceof WorldBorderListenerOnce simpleListener) {
         ci.cancel();
         this.worldBorderPositionListenerMulti.add(simpleListener);
      }
   }

   @WrapOperation(
      method = "tick",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/border/WorldBorder$BorderExtent;update()Lnet/minecraft/world/level/border/WorldBorder$BorderExtent;"
      )
   )
   public BorderExtent getUpdatedArea(BorderExtent instance, Operation<BorderExtent> original) {
      BorderExtent prevExtent = this.extent;
      BorderExtent newExtent = (BorderExtent)original.call(new Object[]{instance});
      if (newExtent != prevExtent) {
         this.worldBorderPositionListenerMulti.onAreaReplaced((WorldBorder)this);
      }

      return newExtent;
   }
}
