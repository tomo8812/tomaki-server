package org.millenaire.world.sitescore;

import java.util.Set;
import java.util.TreeSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.millenaire.tag.ModTags;

public record ArtificialScan(int mask, int blockEntities, Set<String> blocks, Set<String> entityTypes) {
   private static final Set<BlockEntityType<?>> NATURAL = Set.of(
      BlockEntityType.BEEHIVE,
      BlockEntityType.MOB_SPAWNER,
      BlockEntityType.BRUSHABLE_BLOCK,
      BlockEntityType.TRIAL_SPAWNER,
      BlockEntityType.VAULT,
      BlockEntityType.SCULK_SENSOR,
      BlockEntityType.CALIBRATED_SCULK_SENSOR,
      BlockEntityType.SCULK_SHRIEKER,
      BlockEntityType.SCULK_CATALYST,
      BlockEntityType.END_GATEWAY,
      BlockEntityType.END_PORTAL
   );

   public boolean any() {
      return this.mask != 0 || this.blockEntities > 0;
   }

   public static ArtificialScan compute(ServerLevel level, LevelChunk chunk, int[] heights) {
      int baseX = chunk.getPos().getMinBlockX();
      int baseZ = chunk.getPos().getMinBlockZ();
      int mask = 0;
      Set<String> blocks = new TreeSet<>();
      MutableBlockPos pos = new MutableBlockPos();
      int side = 4;

      for (int j = 0; j < side; j++) {
         for (int i = 0; i < side; i++) {
            int idx = j * side + i;
            int x = baseX + 2 + 4 * i;
            int z = baseZ + 2 + 4 * j;
            int ground = heights[idx];
            int top = chunk.getHeight(Types.WORLD_SURFACE, x & 15, z & 15) - 1;

            for (int y = top; y >= ground - 1 && y >= level.getMinBuildHeight(); y--) {
               BlockState state = chunk.getBlockState(pos.set(x, y, z));
               if (state.is(ModTags.Blocks.SITE_ARTIFICIAL)) {
                  mask |= 1 << idx;
                  blocks.add(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
                  break;
               }
            }
         }
      }

      int entities = 0;
      Set<String> types = new TreeSet<>();

      for (BlockEntity be : chunk.getBlockEntities().values()) {
         BlockPos p = be.getBlockPos();
         if (!NATURAL.contains(be.getType()) && p.getY() >= heights[nearestSample(p.getX() - baseX, p.getZ() - baseZ)]) {
            entities++;
            types.add(String.valueOf(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType())));
         }
      }

      return new ArtificialScan(mask, entities, blocks, types);
   }

   static int nearestSample(int lx, int lz) {
      int side = 4;
      int i = Math.clamp(Math.round((lx - 2) / 4.0F), 0, side - 1);
      int j = Math.clamp(Math.round((lz - 2) / 4.0F), 0, side - 1);
      return j * side + i;
   }
}
