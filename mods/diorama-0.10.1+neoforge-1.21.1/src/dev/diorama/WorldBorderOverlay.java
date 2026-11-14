package dev.diorama;

import dev.diorama.drawing.Drawing;
import dev.diorama.drawing.DrawingOverlay;
import dev.diorama.render.MapViewport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.jspecify.annotations.Nullable;

public final class WorldBorderOverlay {
   private static final float MARGIN = 8.0F;
   private static final float SAMPLE = 2.0F;
   private static final int MAX_SAMPLES = 4096;
   private static final int TREE_REACH = 48;
   private final DrawingOverlay lines = new DrawingOverlay();
   private final Drawing[] sides = new Drawing[]{new Drawing(), new Drawing(), new Drawing(), new Drawing()};
   private final List<Drawing> shown = new ArrayList<>(4);
   private float[] heights = new float[256];
   private double[] points = new double[512];
   private final MutableBlockPos pos = new MutableBlockPos();
   private @Nullable ChunkAccess chunk;
   private int chunkX;
   private int chunkZ;
   private boolean chunkRead;

   public void draw(GuiGraphics graphics, Font font, MapViewport.View view, ClientLevel level, Entity viewer) {
      List<Drawing> sides = this.sides(view, level, viewer);
      if (!sides.isEmpty()) {
         this.lines.draw(graphics, font, view, sides, null, null, false);
      }
   }

   public List<Drawing> sides(MapViewport.View view, ClientLevel level, Entity viewer) {
      this.shown.clear();
      WorldBorder border = level.getWorldBorder();
      Facing facing = Projection.facing();
      double minX = border.getMinX();
      double minZ = border.getMinZ();
      double maxX = border.getMaxX();
      double maxZ = border.getMaxZ();
      double ax = facing.x(minX, minZ);
      double bx = facing.x(maxX, maxZ);
      double az = facing.z(minX, minZ);
      double bz = facing.z(maxX, maxZ);
      double west = Math.min(ax, bx);
      double east = Math.max(ax, bx);
      double north = Math.min(az, bz);
      double south = Math.max(az, bz);
      double margin = 8.0 / view.zoom();
      double left = view.minU() - margin;
      double right = view.minU() + view.width() / view.zoom() + margin;
      double top = view.minV() - margin;
      double bottom = view.minV() + view.height() / view.zoom() + margin;
      if (!(east < left) && !(west > right)) {
         int floor = level.getMinBuildHeight();
         int ceiling = level.getMaxBuildHeight() - 1 + 1;
         if (!(Projection.v(floor, south) < top) && !(Projection.v(ceiling, north) > bottom)) {
            boolean westShown = west >= left;
            boolean eastShown = east <= right;
            boolean northShown = Projection.v(floor, north) >= top;
            boolean southShown = Projection.v(ceiling, south) <= bottom;
            if (!westShown && !eastShown && !northShown && !southShown) {
               return this.shown;
            }

            boolean flatGround = view.layer() != Integer.MIN_VALUE || level.dimensionType().hasCeiling();
            double feet = viewer.getY();
            float flat = (float)(view.layer() != Integer.MIN_VALUE ? Math.min(feet, view.layer()) : feet);
            float fallback = flatGround ? flat : level.getSeaLevel();
            int color = 0xFF000000 | border.getStatus().getColor();
            this.chunkRead = false;
            int northBlock = (int)Math.floor(north);
            int southBlock = (int)Math.ceil(south) - 1;
            int westBlock = (int)Math.floor(west);
            int eastBlock = (int)Math.ceil(east) - 1;
            double from = Math.max(west, left);
            double to = Math.min(east, right);
            float[] corners = new float[]{Float.NaN, Float.NaN, Float.NaN, Float.NaN};

            for (int side = 0; side < 2; side++) {
               if (side == 0 ? northShown : southShown) {
                  double z = side == 0 ? north : south;
                  int blockZ = side == 0 ? northBlock : southBlock;
                  int count = this.across(view, level, facing, from, to, westBlock, eastBlock, blockZ, flatGround ? flat : Float.NaN, fallback);
                  if (westShown) {
                     corners[side * 2] = this.heights[0];
                  }

                  if (eastShown) {
                     corners[side * 2 + 1] = this.heights[count - 1];
                  }

                  int kept = 0;

                  for (int i = 0; i < count; i++) {
                     if (i <= 0 || i >= count - 1 || this.heights[i - 1] != this.heights[i] || this.heights[i + 1] != this.heights[i]) {
                        this.points[kept * 2] = count == 1 ? from : from + (to - from) * i / (count - 1);
                        this.points[kept * 2 + 1] = Projection.v(this.heights[i], z);
                        kept++;
                     }
                  }

                  this.show(side, color, Arrays.copyOf(this.points, kept * 2), facing);
               }
            }

            for (int side = 0; side < 2; side++) {
               if (side == 0 ? westShown : eastShown) {
                  double u = side == 0 ? west : east;
                  int blockX = side == 0 ? westBlock : eastBlock;
                  float atNorth = corners[side];
                  float atSouth = corners[2 + side];
                  if (Float.isNaN(atNorth)) {
                     atNorth = flatGround ? flat : this.groundOr(level, facing, blockX, northBlock, fallback);
                  }

                  if (Float.isNaN(atSouth)) {
                     atSouth = flatGround ? flat : this.groundOr(level, facing, blockX, southBlock, fallback);
                  }

                  double northV = Projection.v(atNorth, north);
                  double southV = Projection.v(atSouth, south);
                  double start = Math.max(top, Math.min(northV, southV));
                  double end = Math.min(bottom, Math.max(northV, southV));
                  if (start <= end) {
                     this.show(2 + side, color, new double[]{u, start, u, end}, facing);
                  }
               }
            }

            this.chunk = null;
            return this.shown;
         } else {
            return this.shown;
         }
      } else {
         return this.shown;
      }
   }

   private void show(int side, int color, double[] points, Facing facing) {
      Drawing line = this.sides[side];
      line.kind = Drawing.Kind.STROKE;
      line.color = color;
      line.points = points;
      line.turns = facing.turns;
      this.shown.add(line);
   }

   private int across(
      MapViewport.View view, ClientLevel level, Facing facing, double from, double to, int westBlock, int eastBlock, int blockZ, float flat, float fallback
   ) {
      if (!Float.isNaN(flat)) {
         this.heights[0] = flat;
         this.heights[1] = flat;
         return 2;
      }

      double step = Math.max(1.0, 2.0 / view.zoom());
      int count = (int)Math.max(2.0, Math.min(4096.0, Math.ceil((to - from) / step) + 1.0));
      if (this.heights.length < count) {
         this.heights = new float[count];
         this.points = new double[count * 2];
      }

      int known = -1;

      for (int i = 0; i < count; i++) {
         int blockX = (int)Math.max(westBlock, Math.min(eastBlock, Math.floor(from + (to - from) * i / (count - 1))));
         float height = this.ground(level, facing.worldBlockX(blockX, blockZ), facing.worldBlockZ(blockX, blockZ));
         if (Float.isNaN(height)) {
            height = known >= 0 ? this.heights[known] : Float.NaN;
         } else {
            if (known < 0) {
               Arrays.fill(this.heights, 0, i, height);
            }

            known = i;
         }

         this.heights[i] = height;
      }

      if (known < 0) {
         Arrays.fill(this.heights, 0, count, fallback);
      }

      return count;
   }

   private float groundOr(ClientLevel level, Facing facing, int blockX, int blockZ, float fallback) {
      float height = this.ground(level, facing.worldBlockX(blockX, blockZ), facing.worldBlockZ(blockX, blockZ));
      return Float.isNaN(height) ? fallback : height;
   }

   private float ground(ClientLevel level, int x, int z) {
      int chunkX = x >> 4;
      int chunkZ = z >> 4;
      if (!this.chunkRead || chunkX != this.chunkX || chunkZ != this.chunkZ) {
         this.chunk = level.getChunkSource().getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
         this.chunkX = chunkX;
         this.chunkZ = chunkZ;
         this.chunkRead = true;
      }

      ChunkAccess chunk = this.chunk;
      if (chunk == null) {
         return Float.NaN;
      }

      int top = chunk.getHeight(Types.MOTION_BLOCKING, x & 15, z & 15);
      int lowest = Math.max(level.getMinBuildHeight(), top - 48);

      for (int y = top; y >= lowest; y--) {
         BlockState state = chunk.getBlockState(this.pos.set(x, y, z));
         if (!state.isAir() && !state.is(BlockTags.LEAVES) && !state.is(BlockTags.LOGS)) {
            return y + 1;
         }
      }

      return top + 1;
   }
}
