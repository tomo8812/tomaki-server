package org.embeddedt.modernfix.common.mixin.perf.capability_list_compaction;

import com.llamalad7.mixinextras.sugar.Local;
import net.neoforged.neoforge.capabilities.BaseCapability;
import net.neoforged.neoforge.capabilities.CapabilityHooks;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.embeddedt.modernfix.neoforge.caps.CapProviderGetter;
import org.embeddedt.modernfix.neoforge.caps.ITrackingCapEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CapabilityHooks.class, remap = false)
public class CapabilityHooksMixin {
   @Inject(method = "init", at = @At("RETURN"))
   private static void deduplicateCaps(CallbackInfo ci, @Local(ordinal = 0) RegisterCapabilitiesEvent event) {
      if (event instanceof ITrackingCapEvent) {
         for (BaseCapability<?, ?> cap : ((ITrackingCapEvent)event).mfix$getTrackedCaps()) {
            CapProviderGetter.deduplicateCap(cap);
         }
      }
   }
}
