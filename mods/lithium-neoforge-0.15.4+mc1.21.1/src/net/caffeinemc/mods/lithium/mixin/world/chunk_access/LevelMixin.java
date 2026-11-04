package net.caffeinemc.mods.lithium.mixin.world.chunk_access;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor {
   @Overwrite
   public LevelChunk getChunkAt(BlockPos pos) {
      return (LevelChunk)this.getChunk(pos);
   }

   public ChunkAccess getChunk(BlockPos pos) {
      return this.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, true);
   }

   @Overwrite
   public LevelChunk getChunk(int chunkX, int chunkZ) {
      return (LevelChunk)this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
   }

   public ChunkAccess getChunk(int chunkX, int chunkZ, ChunkStatus status) {
      return this.getChunk(chunkX, chunkZ, status, true);
   }

   public BlockGetter getChunkForCollisions(int chunkX, int chunkZ) {
      return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Overwrite
   public ChunkAccess getChunk(int param1, int nullx) {
      return this.getChunk(var1, nullx);
   }
}
