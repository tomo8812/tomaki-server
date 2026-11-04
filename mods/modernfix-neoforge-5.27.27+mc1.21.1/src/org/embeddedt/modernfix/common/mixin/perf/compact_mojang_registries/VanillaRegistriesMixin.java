package org.embeddedt.modernfix.common.mixin.perf.compact_mojang_registries;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.registries.VanillaRegistries;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VanillaRegistries.class)
public class VanillaRegistriesMixin {
   private static Provider STATIC_PROVIDER;

   @WrapMethod(method = "createLookup")
   private static Provider modernfix$memoizeLookup(Operation<Provider> original) {
      synchronized (VanillaRegistries.class) {
         if (STATIC_PROVIDER == null) {
            STATIC_PROVIDER = (Provider)original.call(new Object[0]);
         }

         return STATIC_PROVIDER;
      }
   }
}
