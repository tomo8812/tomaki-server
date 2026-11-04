package com.kishku7.chunksmith.lod;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainer.Strategy;

public final class CsLodSectionBuilder {
   private static final Map<String, BlockState> BLOCK_CACHE = new HashMap<>();
   private static final Map<String, Holder<Biome>> BIOME_CACHE = new HashMap<>();

   private CsLodSectionBuilder() {
   }

   public static LevelChunkSection rebuild(Level level, CsLodChunk record, CsLodChunk.Section section) {
      Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
      PalettedContainer<BlockState> states = new PalettedContainer(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), Strategy.SECTION_STATES);
      PalettedContainer<Holder<Biome>> biomes = new PalettedContainer(
         biomeRegistry.asHolderIdMap(), biomeRegistry.getHolderOrThrow(Biomes.PLAINS), Strategy.SECTION_BIOMES
      );
      if (section.getUniformBlock() >= 0) {
         BlockState state = blockState(level, record.getBlockPalette().get(section.getUniformBlock()));

         for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
               for (int x = 0; x < 16; x++) {
                  states.set(x, y, z, state);
               }
            }
         }
      } else {
         int[] indices = section.getBlocks();
         int n = 0;

         for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
               for (int x = 0; x < 16; x++) {
                  states.set(x, y, z, blockState(level, record.getBlockPalette().get(indices[n++])));
               }
            }
         }
      }

      if (section.getUniformBiome() >= 0) {
         Holder<Biome> biome = biome(level, record.getBiomePalette().get(section.getUniformBiome()));

         for (int y = 0; y < 4; y++) {
            for (int z = 0; z < 4; z++) {
               for (int x = 0; x < 4; x++) {
                  biomes.set(x, y, z, biome);
               }
            }
         }
      } else {
         int[] indices = section.getBiomes();
         int n = 0;

         for (int y = 0; y < 4; y++) {
            for (int z = 0; z < 4; z++) {
               for (int x = 0; x < 4; x++) {
                  biomes.set(x, y, z, biome(level, record.getBiomePalette().get(indices[n++])));
               }
            }
         }
      }

      return new LevelChunkSection(states, biomes);
   }

   public static BlockState blockState(Level level, String value) {
      return BLOCK_CACHE.computeIfAbsent(value, key -> {
         try {
            return BlockStateParser.parseForBlock(level.registryAccess().registryOrThrow(Registries.BLOCK).asLookup(), key, false).blockState();
         } catch (CommandSyntaxException e) {
            return Blocks.AIR.defaultBlockState();
         }
      });
   }

   public static Holder<Biome> biome(Level level, String value) {
      return BIOME_CACHE.computeIfAbsent(
         value,
         key -> {
            Registry<Biome> registry = level.registryAccess().registryOrThrow(Registries.BIOME);
            return (Holder<Biome>)registry.getHolder(ResourceKey.create(Registries.BIOME, ResourceLocation.parse(key)))
               .orElseGet(() -> registry.getHolderOrThrow(ResourceKey.create(Registries.BIOME, ResourceLocation.parse("minecraft:plains"))));
         }
      );
   }
}
