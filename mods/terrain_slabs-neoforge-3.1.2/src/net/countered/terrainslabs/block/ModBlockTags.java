package net.countered.terrainslabs.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
   public static final TagKey<Block> DIRT_SLABS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("terrain_slabs", "dirt_slabs"));
   public static final TagKey<Block> TERRACOTTA_SLABS = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("terrain_slabs", "terracotta_slabs")
   );
   public static final TagKey<Block> ON_TOP_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("terrain_slabs", "on_top_blocks"));
}
