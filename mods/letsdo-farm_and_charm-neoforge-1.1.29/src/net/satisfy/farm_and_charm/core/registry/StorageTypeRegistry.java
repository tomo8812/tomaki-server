package net.satisfy.farm_and_charm.core.registry;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.FarmAndCharm;

public class StorageTypeRegistry {
   public static final ResourceLocation TOOL_RACK = FarmAndCharm.identifier("tool_rack");
   public static final ResourceLocation WINDOW_SILL = FarmAndCharm.identifier("window_sill");
   public static final ResourceLocation CHICKEN_NEST = FarmAndCharm.identifier("chicken_nest");

   public static Set<Block> registerBlocks(Set<Block> blocks) {
      blocks.add((Block)ObjectRegistry.TOOL_RACK.get());
      blocks.add((Block)ObjectRegistry.WINDOW_SILL.get());
      blocks.add((Block)ObjectRegistry.CHICKEN_NEST.get());
      return blocks;
   }
}
