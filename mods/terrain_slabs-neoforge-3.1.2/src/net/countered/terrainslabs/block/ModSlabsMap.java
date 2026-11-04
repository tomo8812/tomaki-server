package net.countered.terrainslabs.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.countered.terrainslabs.registries.ModBlocksRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class ModSlabsMap {
   private static final Map<Block, Block> SLAB_MAP = new HashMap<>();
   public static final Set<Block> SOIL_SLAB_BLOCKS = Set.of(
      (Block)ModBlocksRegistry.GRASS_SLAB.get(),
      (Block)ModBlocksRegistry.PODZOL_SLAB.get(),
      (Block)ModBlocksRegistry.MYCELIUM_SLAB.get(),
      (Block)ModBlocksRegistry.PATH_SLAB.get()
   );

   @Nullable
   public static Block getSlabForBlock(Block block) {
      return SLAB_MAP.get(block);
   }

   static {
      SLAB_MAP.put(Blocks.STONE, (Block)ModBlocksRegistry.CUSTOM_STONE_SLAB.get());
      SLAB_MAP.put(Blocks.TUFF, (Block)ModBlocksRegistry.CUSTOM_TUFF_SLAB.get());
      SLAB_MAP.put(Blocks.SANDSTONE, (Block)ModBlocksRegistry.CUSTOM_SANDSTONE_SLAB.get());
      SLAB_MAP.put(Blocks.RED_SANDSTONE, (Block)ModBlocksRegistry.CUSTOM_RED_SANDSTONE_SLAB.get());
      SLAB_MAP.put(Blocks.ANDESITE, (Block)ModBlocksRegistry.CUSTOM_ANDESITE_SLAB.get());
      SLAB_MAP.put(Blocks.DIORITE, (Block)ModBlocksRegistry.CUSTOM_DIORITE_SLAB.get());
      SLAB_MAP.put(Blocks.GRANITE, (Block)ModBlocksRegistry.CUSTOM_GRANITE_SLAB.get());
      SLAB_MAP.put(Blocks.GRASS_BLOCK, (Block)ModBlocksRegistry.GRASS_SLAB.get());
      SLAB_MAP.put(Blocks.MYCELIUM, (Block)ModBlocksRegistry.MYCELIUM_SLAB.get());
      SLAB_MAP.put(Blocks.PODZOL, (Block)ModBlocksRegistry.PODZOL_SLAB.get());
      SLAB_MAP.put(Blocks.DIRT_PATH, (Block)ModBlocksRegistry.PATH_SLAB.get());
      SLAB_MAP.put(Blocks.GRAVEL, (Block)ModBlocksRegistry.GRAVEL_SLAB.get());
      SLAB_MAP.put(Blocks.SAND, (Block)ModBlocksRegistry.SAND_SLAB.get());
      SLAB_MAP.put(Blocks.RED_SAND, (Block)ModBlocksRegistry.RED_SAND_SLAB.get());
      SLAB_MAP.put(Blocks.DIRT, (Block)ModBlocksRegistry.DIRT_SLAB.get());
      SLAB_MAP.put(Blocks.MUD, (Block)ModBlocksRegistry.MUD_SLAB.get());
      SLAB_MAP.put(Blocks.COARSE_DIRT, (Block)ModBlocksRegistry.COARSE_SLAB.get());
      SLAB_MAP.put(Blocks.SNOW_BLOCK, (Block)ModBlocksRegistry.SNOW_SLAB.get());
      SLAB_MAP.put(Blocks.PACKED_ICE, (Block)ModBlocksRegistry.PACKED_ICE_SLAB.get());
      SLAB_MAP.put(Blocks.CLAY, (Block)ModBlocksRegistry.CLAY_SLAB.get());
      SLAB_MAP.put(Blocks.DEEPSLATE, (Block)ModBlocksRegistry.DEEPSLATE_SLAB.get());
      SLAB_MAP.put(Blocks.MOSS_BLOCK, (Block)ModBlocksRegistry.MOSS_SLAB.get());
      SLAB_MAP.put(Blocks.CALCITE, (Block)ModBlocksRegistry.CALCITE_SLAB.get());
      SLAB_MAP.put(Blocks.SMOOTH_BASALT, (Block)ModBlocksRegistry.SMOOTH_BASALT_SLAB.get());
      SLAB_MAP.put(Blocks.LIGHT_BLUE_TERRACOTTA, (Block)ModBlocksRegistry.LIGHT_BLUE_TERRACOTTA_SLAB.get());
      SLAB_MAP.put(Blocks.CYAN_TERRACOTTA, (Block)ModBlocksRegistry.CYAN_TERRACOTTA_SLAB.get());
      SLAB_MAP.put(Blocks.COBBLESTONE, (Block)ModBlocksRegistry.CUSTOM_COBBLESTONE_SLAB.get());
      SLAB_MAP.put(Blocks.MOSSY_COBBLESTONE, (Block)ModBlocksRegistry.CUSTOM_MOSSY_COBBLESTONE_SLAB.get());
      SLAB_MAP.put(Blocks.COBBLED_DEEPSLATE, (Block)ModBlocksRegistry.CUSTOM_COBBLED_DEEPSLATE_SLAB.get());
      SLAB_MAP.put(Blocks.ICE, (Block)ModBlocksRegistry.ICE_SLAB.get());
      SLAB_MAP.put(Blocks.ROOTED_DIRT, (Block)ModBlocksRegistry.ROOTED_DIRT_SLAB.get());
      SLAB_MAP.put(Blocks.PACKED_MUD, (Block)ModBlocksRegistry.PACKED_MUD_SLAB.get());
      SLAB_MAP.put(Blocks.BLUE_ICE, (Block)ModBlocksRegistry.BLUE_ICE_SLAB.get());
      SLAB_MAP.put(Blocks.BLACK_TERRACOTTA, (Block)ModBlocksRegistry.BLACK_TERRACOTTA_SLAB.get());
      SLAB_MAP.put(Blocks.PRISMARINE, (Block)ModBlocksRegistry.CUSTOM_PRISMARINE_SLAB.get());
      SLAB_MAP.put(Blocks.TERRACOTTA, (Block)ModBlocksRegistry.TERRACOTTA_SLAB.get());
      SLAB_MAP.put(Blocks.RED_TERRACOTTA, (Block)ModBlocksRegistry.RED_TERRACOTTA_SLAB.get());
      SLAB_MAP.put(Blocks.ORANGE_TERRACOTTA, (Block)ModBlocksRegistry.ORANGE_TERRACOTTA_SLAB.get());
      SLAB_MAP.put(Blocks.LIGHT_GRAY_TERRACOTTA, (Block)ModBlocksRegistry.LIGHT_GRAY_TERRACOTTA_SLAB.get());
      SLAB_MAP.put(Blocks.WHITE_TERRACOTTA, (Block)ModBlocksRegistry.WHITE_TERRACOTTA_SLAB.get());
      SLAB_MAP.put(Blocks.BROWN_TERRACOTTA, (Block)ModBlocksRegistry.BROWN_TERRACOTTA_SLAB.get());
      SLAB_MAP.put(Blocks.YELLOW_TERRACOTTA, (Block)ModBlocksRegistry.YELLOW_TERRACOTTA_SLAB.get());
      SLAB_MAP.put(Blocks.SOUL_SAND, (Block)ModBlocksRegistry.SOUL_SAND_SLAB.get());
      SLAB_MAP.put(Blocks.SOUL_SOIL, (Block)ModBlocksRegistry.SOUL_SOIL_SLAB.get());
      SLAB_MAP.put(Blocks.NETHERRACK, (Block)ModBlocksRegistry.NETHERRACK_SLAB.get());
      SLAB_MAP.put(Blocks.WARPED_NYLIUM, (Block)ModBlocksRegistry.WARPED_NYLIUM_SLAB.get());
      SLAB_MAP.put(Blocks.CRIMSON_NYLIUM, (Block)ModBlocksRegistry.CRIMSON_NYLIUM_SLAB.get());
      SLAB_MAP.put(Blocks.BASALT, (Block)ModBlocksRegistry.BASALT_SLAB.get());
      SLAB_MAP.put(Blocks.BLACKSTONE, (Block)ModBlocksRegistry.CUSTOM_BLACKSTONE_SLAB.get());
      SLAB_MAP.put(Blocks.END_STONE, (Block)ModBlocksRegistry.ENDSTONE_SLAB.get());
   }
}
