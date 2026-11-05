package maki.millenairelod.mixin;

import maki.millenairelod.Compat;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.building.BuildingInstance;
import org.millenaire.village.Village;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Village.class, remap = false)
public abstract class VillageMixin {
   @Inject(method = "addBuilding", at = @At("TAIL"))
   private void compat$buildingAdded(BuildingInstance var1, CallbackInfo var2) {
      Compat.invalidateVillage((Village)this);
   }

   @Inject(method = "forgetBuilding", at = @At("RETURN"))
   private void compat$buildingRemoved(ServerLevel var1, BuildingInstance var2, CallbackInfoReturnable<Boolean> var3) {
      if (Boolean.TRUE.equals(var3.getReturnValue())) {
         Compat.invalidateVillage((Village)this);
      }
   }
}
