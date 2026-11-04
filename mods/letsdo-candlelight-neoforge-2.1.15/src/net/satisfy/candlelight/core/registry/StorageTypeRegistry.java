package net.satisfy.candlelight.core.registry;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.satisfy.candlelight.Candlelight;

public class StorageTypeRegistry {
   public static final ResourceLocation SHELF = Candlelight.identifier("shelf");
   public static final ResourceLocation TABLE_SET = Candlelight.identifier("table_set");
   public static final ResourceLocation JEWELRY_BOX = Candlelight.identifier("jewelry_box");

   public static Set<Block> registerBlocks(Set<Block> blocks) {
      blocks.add((Block)ObjectRegistry.OAK_SHELF.get());
      blocks.add((Block)ObjectRegistry.SPRUCE_SHELF.get());
      blocks.add((Block)ObjectRegistry.BIRCH_SHELF.get());
      blocks.add((Block)ObjectRegistry.ACACIA_SHELF.get());
      blocks.add((Block)ObjectRegistry.JUNGLE_SHELF.get());
      blocks.add((Block)ObjectRegistry.DARK_OAK_SHELF.get());
      blocks.add((Block)ObjectRegistry.MANGROVE_SHELF.get());
      blocks.add((Block)ObjectRegistry.CRIMSON_SHELF.get());
      blocks.add((Block)ObjectRegistry.WARPED_SHELF.get());
      blocks.add((Block)ObjectRegistry.BAMBOO_SHELF.get());
      blocks.add((Block)ObjectRegistry.CHERRY_SHELF.get());
      blocks.add((Block)ObjectRegistry.JEWELRY_BOX.get());
      return blocks;
   }
}
