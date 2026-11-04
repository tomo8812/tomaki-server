package dev.terminalmc.clientsort.mixin.client.accessor;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTabs.class)
public interface CreativeModeTabsAccessor {
   @Accessor("CACHED_PARAMETERS")
   static void clientsort$setCachedParameters(ItemDisplayParameters newParams) {
      throw new AssertionError();
   }
}
