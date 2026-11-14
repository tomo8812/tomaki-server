package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.util.StructureFaultReporter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureStart.class)
public abstract class StructureStartMixin {
   @Shadow
   @Final
   private Structure structure;

   @Inject(method = "placeInChunk", at = @At("HEAD"), require = 0)
   private void chunksmith$pushFaultContext(
      WorldGenLevel level,
      StructureManager structureManager,
      ChunkGenerator generator,
      RandomSource random,
      BoundingBox chunkBB,
      ChunkPos chunkPos,
      CallbackInfo ci
   ) {
      String id = null;

      try {
         Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
         ResourceLocation key = registry.getKey(this.structure);
         if (key != null) {
            id = key.toString();
         }
      } catch (Throwable var11) {
      }

      StructureFaultReporter.get().pushContext(id, chunkPos.x, chunkPos.z);
   }

   @Inject(method = "placeInChunk", at = @At("RETURN"), require = 0)
   private void chunksmith$popFaultContext(
      WorldGenLevel level,
      StructureManager structureManager,
      ChunkGenerator generator,
      RandomSource random,
      BoundingBox chunkBB,
      ChunkPos chunkPos,
      CallbackInfo ci
   ) {
      StructureFaultReporter.get().popContext();
   }
}
