package org.embeddedt.modernfix.common.mixin.feature.measure_time;

import net.minecraft.server.packs.resources.SimpleReloadInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SimpleReloadInstance.class)
public class SimpleReloadInstanceMixin {
   private static final boolean ENABLE_DEBUG_RELOADER = Boolean.getBoolean("modernfix.debugReloader");

   @ModifyVariable(method = "create", at = @At("HEAD"), argsOnly = true)
   private static boolean enableDebugReloader(boolean bl) {
      return bl || ENABLE_DEBUG_RELOADER;
   }
}
