package org.embeddedt.modernfix.common.mixin.perf.optimize_surface_rules;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import org.embeddedt.modernfix.chunk.ExtendedPalettedContainer;
import org.embeddedt.modernfix.world.gen.ExtendedSurfaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
   private static void mfix$accumulate(Set<ResourceKey<Biome>> chunkBiomes, LevelChunkSection section) {
      Palette<Holder<Biome>> palette = ((ExtendedPalettedContainer)section.getBiomes()).mfix$getPalette();
      if (palette.getSize() == 1) {
         chunkBiomes.add((ResourceKey<Biome>)((Holder)palette.valueFor(0)).unwrapKey().orElseThrow());
      } else {
         section.getBiomes().getAll(holder -> chunkBiomes.add((ResourceKey)holder.unwrapKey().orElseThrow()));
      }
   }

   private static Set<ResourceKey<Biome>> mfix$obtainBiomes(WorldGenRegion region, ChunkPos center, int chunkRadius) {
      Set<ResourceKey<Biome>> chunkBiomes = new ReferenceOpenHashSet();

      for (int z = center.z - chunkRadius; z <= center.z + chunkRadius; z++) {
         for (int x = center.x - chunkRadius; x <= center.x + chunkRadius; x++) {
            ChunkAccess chunk = region.getChunk(x, z);

            for (LevelChunkSection section : chunk.getSections()) {
               mfix$accumulate(chunkBiomes, section);
            }
         }
      }

      return chunkBiomes;
   }

   @Inject(
      method = "buildSurface(Lnet/minecraft/server/level/WorldGenRegion;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V"
      )
   )
   private void mfix$findNearbyBiomes(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk, CallbackInfo ci) {
      try {
         ExtendedSurfaceContext.COMPUTED_POSSIBLE_BIOMES.set(mfix$obtainBiomes(level, chunk.getPos(), 1));
      } catch (Exception e) {
         ExtendedSurfaceContext.COMPUTED_POSSIBLE_BIOMES.remove();
      }
   }
}
