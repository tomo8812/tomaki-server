package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.util.WorldgenOverreachReporter;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStep;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldGenRegion.class)
public abstract class WorldGenRegionMixin {
   @Shadow
   @Final
   private ChunkStep generatingStep;
   @Shadow
   @Final
   private ServerLevel level;
   @Shadow
   private Supplier<String> currentlyGenerating;

   @Shadow
   public abstract ChunkPos getCenter();

   @Redirect(method = "ensureCanWrite", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;logAndPauseIfInIde(Ljava/lang/String;)V"))
   private void chunksmith$captureFarWrite(String message, BlockPos pos) {
      try {
         String feature = this.currentlyGenerating == null ? null : String.valueOf(this.currentlyGenerating.get());
         String step = String.valueOf(this.generatingStep.targetStatus());
         String dimension = this.level.dimension().location().toString();
         ChunkPos center = this.getCenter();
         int writeRadius = this.generatingStep.blockStateWriteRadius();
         WorldgenOverreachReporter.get()
            .record(
               feature,
               step,
               dimension,
               center.x,
               center.z,
               SectionPos.blockToSectionCoord(pos.getX()),
               SectionPos.blockToSectionCoord(pos.getZ()),
               pos.getY(),
               writeRadius
            );
      } catch (Throwable var8) {
      }
   }
}
