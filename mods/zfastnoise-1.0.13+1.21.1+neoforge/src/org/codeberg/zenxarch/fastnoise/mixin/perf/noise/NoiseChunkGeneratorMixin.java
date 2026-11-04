package org.codeberg.zenxarch.fastnoise.mixin.perf.noise;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.codeberg.zenxarch.fastnoise.noise.FastChunkSection;
import org.codeberg.zenxarch.fastnoise.noise.FastWorldgen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseChunkGeneratorMixin {
   @Shadow
   @Final
   private Holder<NoiseGeneratorSettings> settings;

   @Shadow
   protected abstract NoiseChunk createNoiseChunk(ChunkAccess var1, StructureManager var2, Blender var3, RandomState var4);

   @Shadow
   protected abstract ChunkAccess doFill(Blender var1, StructureManager var2, RandomState var3, ChunkAccess var4, int var5, int var6);

   @WrapMethod(method = "lambda$fillFromNoise$11")
   private ChunkAccess zenxarch$populateNoise(
      ChunkAccess chunk,
      int cellHeight,
      NoiseSettings generationShapeConfig,
      int minimumY,
      Blender blender,
      StructureManager structureAccessor,
      RandomState noiseConfig,
      int minimumCellY,
      Operation<ChunkAccess> op
   ) {
      if (this.getClass() != NoiseBasedChunkGenerator.class) {
         return (ChunkAccess)op.call(new Object[]{chunk, cellHeight, generationShapeConfig, minimumY, blender, structureAccessor, noiseConfig, minimumCellY});
      } else if (SharedConstants.debugVoidTerrain(chunk.getPos())) {
         return chunk;
      } else {
         BlockState defaultBlock = ((NoiseGeneratorSettings)this.settings.value()).defaultBlock();
         if (defaultBlock != FastWorldgen.AIR && !chunk.isUpgrading() && FastWorldgen.isEmpty(chunk)) {
            FastChunkSection[] fastSections = new FastChunkSection[chunk.getSections().length];
            NoiseChunk chunkNoiseSampler = chunk.getOrCreateNoiseChunk(chunkx -> this.createNoiseChunk(chunkx, structureAccessor, blender, noiseConfig));
            BlockState defaultBlockState = ((NoiseGeneratorSettings)this.settings.value()).defaultBlock();
            FastWorldgen.populateNoise(chunkNoiseSampler, defaultBlockState, chunk, minimumCellY, cellHeight, fastSections, minimumY);
            return chunk;
         } else {
            return this.doFill(blender, structureAccessor, noiseConfig, chunk, minimumCellY, cellHeight);
         }
      }
   }
}
