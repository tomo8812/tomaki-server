package org.embeddedt.modernfix.common.mixin.perf.memoize_creative_tab_build;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {
   @Unique
   private ItemDisplayParameters mfix$oldParameters;
   @Unique
   private static boolean MFIX$REBUILT_NON_CATEGORY = false;

   @Shadow
   public abstract Type getType();

   @WrapMethod(method = "buildContents")
   private synchronized void buildContentsIfChanged(ItemDisplayParameters parameters, Operation<Void> original) {
      synchronized (CreativeModeTab.class) {
         if (this.mfix$oldParameters == null
            || this.mfix$oldParameters.needsUpdate(parameters.enabledFeatures(), parameters.hasPermissions(), parameters.holders())) {
            original.call(new Object[]{parameters});
            if (this.getType() == Type.CATEGORY) {
               if (MFIX$REBUILT_NON_CATEGORY) {
                  for (CreativeModeTab tab : BuiltInRegistries.CREATIVE_MODE_TAB) {
                     if (tab.getType() != Type.CATEGORY) {
                        ((CreativeModeTabMixin)tab).mfix$oldParameters = null;
                     }
                  }

                  MFIX$REBUILT_NON_CATEGORY = false;
               }
            } else {
               MFIX$REBUILT_NON_CATEGORY = true;
            }
         }

         this.mfix$oldParameters = parameters;
      }
   }
}
