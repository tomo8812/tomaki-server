package maki.millenairelod.mixin;

import maki.millenairelod.Compat;
import org.millenaire.village.Village;
import org.millenaire.village.VillageId;
import org.millenaire.village.VillageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VillageManager.class, remap = false)
public abstract class VillageManagerMixin {
   @Inject(method = "addVillage", at = @At("TAIL"))
   private void compat$villageAdded(Village village, CallbackInfo ci) {
      Compat.invalidateVillageMembership();
   }

   @Inject(method = "removeVillage", at = @At("TAIL"))
   private void compat$villageRemoved(VillageId id, CallbackInfo ci) {
      Compat.invalidateVillageMembership();
   }

   @Inject(method = "clear", at = @At("TAIL"))
   private void compat$villagesCleared(CallbackInfo ci) {
      Compat.invalidateVillageMembership();
   }
}
