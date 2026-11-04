package org.valkyrienskies.mod.mixin.feature.sound.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Map;
import net.minecraft.class_1113;
import net.minecraft.class_1140;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4227;
import net.minecraft.class_638;
import net.minecraft.class_9054;
import net.minecraft.class_4235.class_4236;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.client.audio.VelocityTickableSoundInstance;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;
import org.valkyrienskies.mod.mixinducks.com.mojang.blaze3d.audio.HasOpenALVelocity;

@Mixin(class_1140.class)
public abstract class MixinSoundEngine {
   @Shadow
   protected abstract float method_4853(class_1113 var1);

   @Shadow
   protected abstract float method_4849(class_1113 var1);

   @WrapOperation(at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0), method = "tickNonPaused")
   private Object redirectGet(Map<?, ?> instance, Object obj, Operation<Object> get) {
      if (obj instanceof VelocityTickableSoundInstance soundInstance) {
         class_4236 handle = (class_4236)instance.get(soundInstance);
         float f = this.method_4853(soundInstance);
         float g = this.method_4849(soundInstance);
         class_243 vec3 = new class_243(soundInstance.method_4784(), soundInstance.method_4779(), soundInstance.method_4778());
         Vector3dc velocity = soundInstance.getVelocity();
         handle.method_19735(channel -> {
            channel.method_19647(f);
            channel.method_19639(g);
            channel.method_19641(vec3);
            ((HasOpenALVelocity)channel).setVelocity(velocity);
         });
         return null;
      } else {
         return get.call(new Object[]{instance, obj});
      }
   }

   @WrapOperation(
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Listener;setTransform(Lcom/mojang/blaze3d/audio/ListenerTransform;)V"),
      method = "*"
   )
   private void injectListenerVelocity(class_4227 listener, class_9054 listenerTransform, Operation<Void> setTransform) {
      class_1657 player = class_310.method_1551().field_1724;
      class_638 level = class_310.method_1551().field_1687;
      ((HasOpenALVelocity)listener).setVelocity(new Vector3d());
      if (level != null && player != null) {
         ClientShip mounted = (ClientShip)VSGameUtilsKt.getShipMountedTo(player);
         if (mounted != null) {
            ((HasOpenALVelocity)listener).setVelocity(mounted.getVelocity());
         } else {
            EntityDraggingInformation dragInfo = ((IEntityDraggingInformationProvider)player).getDraggingInformation();
            if (dragInfo.isEntityBeingDraggedByAShip()) {
               Vector3dc playerVel = dragInfo.getAddedMovementLastTick();
               ((HasOpenALVelocity)listener).setVelocity(playerVel);
            }
         }
      }

      setTransform.call(new Object[]{listener, listenerTransform});
   }
}
