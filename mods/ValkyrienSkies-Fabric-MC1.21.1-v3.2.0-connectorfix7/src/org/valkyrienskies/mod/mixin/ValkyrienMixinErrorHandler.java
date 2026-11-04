package org.valkyrienskies.mod.mixin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler.ErrorAction;

public class ValkyrienMixinErrorHandler implements IMixinErrorHandler {
   private final Set<String> warnList = new HashSet<>(
      Arrays.asList(
         "org.valkyrienskies.mod.mixin.feature.water_in_ships_entity.MixinEntity",
         "org.valkyrienskies.mod.mixin.mod_compat.create_big_cannons.MixinAbstractCannonProjectile",
         "org.valkyrienskies.mod.mixin.mod_compat.create_big_cannons.MixinPitchOrientedContraptionEntity"
      )
   );

   public ErrorAction onPrepareError(IMixinConfig config, Throwable th, IMixinInfo mixin, ErrorAction action) {
      return this.warnList.contains(mixin.getClassName()) ? ErrorAction.WARN : null;
   }

   public ErrorAction onApplyError(String targetClassName, Throwable th, IMixinInfo mixin, ErrorAction action) {
      return this.warnList.contains(mixin.getClassName()) ? ErrorAction.WARN : null;
   }
}
