package org.valkyrienskies.mod.fabric.mixin.compat.cc_restitched;

import dan200.computercraft.client.sound.SpeakerSound;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPosition;
import net.minecraft.class_1102;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_3419;
import net.minecraft.class_5819;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.client.audio.VelocityTickableSoundInstance;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(SpeakerSound.class)
public abstract class MixinSpeakerSound extends class_1102 implements VelocityTickableSoundInstance {
   @Unique
   private SpeakerPosition speakerPosition;
   @Unique
   private Ship ship;

   protected MixinSpeakerSound(class_2960 arg, class_3419 arg2, class_5819 arg3) {
      super(arg, arg2, arg3);
   }

   @Inject(method = "setPosition", at = @At("RETURN"), remap = false)
   private void isOnShip(SpeakerPosition position, CallbackInfo ci) {
      this.speakerPosition = position;
      this.ship = VSGameUtilsKt.getShipManagingPos(position.level(), position.position());
      if (this.ship != null) {
         class_243 worldPos = VSGameUtilsKt.toWorldCoordinates(this.speakerPosition.level(), this.speakerPosition.position());
         this.field_5439 = worldPos.field_1352;
         this.field_5450 = worldPos.field_1351;
         this.field_5449 = worldPos.field_1350;
      }
   }

   @Inject(method = "tick", at = @At("HEAD"))
   private void updateWorldPos(CallbackInfo ci) {
      if (this.ship != null) {
         class_243 worldPos = VSGameUtilsKt.toWorldCoordinates(this.speakerPosition.level(), this.speakerPosition.position());
         this.field_5439 = worldPos.field_1352;
         this.field_5450 = worldPos.field_1351;
         this.field_5449 = worldPos.field_1350;
      }
   }

   @NotNull
   @Override
   public Vector3dc getVelocity() {
      return this.ship != null ? this.ship.getVelocity() : new Vector3d();
   }
}
