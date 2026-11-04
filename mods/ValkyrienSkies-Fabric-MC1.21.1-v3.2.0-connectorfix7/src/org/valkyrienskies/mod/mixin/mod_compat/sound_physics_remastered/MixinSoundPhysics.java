package org.valkyrienskies.mod.mixin.mod_compat.sound_physics_remastered;

import net.minecraft.class_310;
import net.minecraft.class_638;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Pseudo
@Mixin(targets = "com.sonicether.soundphysics.SoundPhysics", remap = false)
public abstract class MixinSoundPhysics {
   @Shadow
   public static void onPlaySound(double posX, double posY, double posZ, int sourceID) {
      throw new AssertionError();
   }

   @Inject(at = @At("HEAD"), method = "onPlaySound", cancellable = true)
   private static void beforeOnPlaySound(double posX, double posY, double posZ, int sourceID, CallbackInfo ci) {
      class_638 level = class_310.method_1551().field_1687;
      if (level != null) {
         if (VSGameUtilsKt.getShipManagingPos(level, posX, posY, posZ) instanceof ClientShip ship) {
            Vector3d inWorldPos = ship.getShipToWorld().transformPosition(new Vector3d(posX, posY, posZ));
            onPlaySound(inWorldPos.x, inWorldPos.y, inWorldPos.z, sourceID);
            ci.cancel();
         }
      }
   }
}
