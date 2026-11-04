package net.caffeinemc.mods.lithium.mixin.world.combined_heightmap_update;

import java.util.Map;
import net.caffeinemc.mods.lithium.common.world.chunk.heightmap.CombinedHeightmapUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess {
   public LevelChunkMixin(
      ChunkPos pos,
      UpgradeData upgradeData,
      LevelHeightAccessor heightLimitView,
      Registry<Biome> biome,
      long inhabitedTime,
      @Nullable LevelChunkSection[] sectionArrayInitializer,
      @Nullable BlendingData blendingData
   ) {
      super(pos, upgradeData, heightLimitView, biome, inhabitedTime, sectionArrayInitializer, blendingData);
   }

   @Redirect(
      method = "setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
      at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;")
   )
   private <K, V> V skipGetHeightmap(Map<K, V> heightmaps, K heightmapType) {
      return heightmapType != Types.MOTION_BLOCKING
            && heightmapType != Types.MOTION_BLOCKING_NO_LEAVES
            && heightmapType != Types.OCEAN_FLOOR
            && heightmapType != Types.WORLD_SURFACE
         ? heightmaps.get(heightmapType)
         : null;
   }

   @Redirect(
      method = "setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Heightmap;update(IIILnet/minecraft/world/level/block/state/BlockState;)Z")
   )
   private boolean skipHeightmapUpdate(Heightmap instance, int x, int y, int z, BlockState state) {
      return instance == null ? false : instance.update(x, y, z, state);
   }

   @Inject(
      method = "setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/levelgen/Heightmap;update(IIILnet/minecraft/world/level/block/state/BlockState;)Z",
         shift = Shift.BEFORE,
         ordinal = 0
      ),
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void updateHeightmapsCombined(
      BlockPos pos,
      BlockState state,
      boolean moved,
      CallbackInfoReturnable<BlockState> cir,
      int y,
      LevelChunkSection chunkSection,
      boolean bl,
      int x,
      int yMod16,
      int z,
      BlockState blockState,
      Block block
   ) {
      Heightmap heightmap0 = (Heightmap)this.heightmaps.get(Types.MOTION_BLOCKING);
      Heightmap heightmap1 = (Heightmap)this.heightmaps.get(Types.MOTION_BLOCKING_NO_LEAVES);
      Heightmap heightmap2 = (Heightmap)this.heightmaps.get(Types.OCEAN_FLOOR);
      Heightmap heightmap3 = (Heightmap)this.heightmaps.get(Types.WORLD_SURFACE);
      CombinedHeightmapUpdate.updateHeightmaps(heightmap0, heightmap1, heightmap2, heightmap3, (LevelChunk)this, x, y, z, state);
   }
}
