package maki.millenairelod.mixin;

import maki.millenairelod.Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.millenaire.building.BuildingInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BuildingInstance.class, remap = false)
public abstract class BuildingInstanceMixin {
   @Inject(method = "startUpgrade", at = @At("TAIL"))
   private void compat$upgradeStarted(ResourceLocation building, int level, CallbackInfo ci) {
      Compat.invalidateAllVillageRanges();
   }

   @Inject(method = "setOrigin", at = @At("TAIL"))
   private void compat$originChanged(BlockPos origin, CallbackInfo ci) {
      Compat.invalidateAllVillageRanges();
   }
}
