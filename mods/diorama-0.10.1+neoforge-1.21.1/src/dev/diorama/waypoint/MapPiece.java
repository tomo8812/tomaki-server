package dev.diorama.waypoint;

import dev.diorama.MapSession;
import dev.diorama.MapStyle;
import dev.diorama.Maps;
import dev.diorama.Projection;
import dev.diorama.ServerRules;
import dev.diorama.bake.BiomeTints;
import dev.diorama.bake.BlockLooks;
import dev.diorama.render.MapLevel;
import dev.diorama.render.MapViewport;
import dev.diorama.render.gpu.MapCanvas;
import dev.diorama.render.gpu.MapTexture;
import dev.diorama.render.gpu.Sampling;
import java.nio.file.Path;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.jspecify.annotations.Nullable;

final class MapPiece {
   private static final double ZOOM = 1.5;
   private static final int FACE_TEXTURE = 64;
   private static final int TEXELS_PER_ROW = 4;
   private static final int OUTLINE = -770698216;
   private static final int SHADOW = 1073741824;
   private static final int CAVE = -14934492;
   private static final int UNKNOWN = -12895687;
   private static final double SUN_AZIMUTH_Z = 0.479;
   private static final float FACE_SKY = 0.5F;
   private static final float FACE_VARIATION = 0.12F;
   private static final long FACE_REFRESH_MS = 1000L;
   private static final float[] SRGB_TO_LINEAR = new float[256];
   private final int width;
   private final int depth;
   private final int side;
   private final MapViewport viewport = new MapViewport();
   private final int[] facePixels = new int[4096];
   private @Nullable MapTexture face;
   private MapPiece.@Nullable FaceKey faceKey;
   private long faceBuilt;
   private MapPiece.@Nullable SavedMap saved;

   MapPiece(int width, int depth, int side) {
      this.width = width;
      this.depth = depth;
      this.side = Math.min(side, 16);
   }

   int outerWidth() {
      return this.width + 2;
   }

   int outerHeight() {
      return this.depth + this.side + 2;
   }

   int tipX() {
      return 1 + this.width / 2;
   }

   int tipY() {
      return 1 + this.depth / 2;
   }

   void draw(GuiGraphics graphics, int x, int y) {
      this.draw(graphics, x, y, null);
   }

   void draw(GuiGraphics graphics, int x, int y, MapPiece.@Nullable Place place) {
      graphics.fill(x + 3, y - 1, x + this.outerWidth() + 3, y + this.outerHeight() - 1, 1073741824);
      graphics.fill(x, y, x + this.outerWidth(), y + this.outerHeight(), -770698216);
      int left = x + 1;
      int top = y + 1;
      Minecraft mc = Minecraft.getInstance();
      Entity viewer = MapSession.viewer();
      ClientLevel level = mc.level;
      MapStyle style = MapStyle.get();
      BlockLooks looks = BlockLooks.get();
      if (viewer != null && level != null && looks != null) {
         MapSession session = MapSession.get();
         boolean here = place == null || place.dimension().equals(level.dimension().location().toString());
         MapLevel map = !here ? this.savedMap(place.dimension()) : (session != null ? session.map() : null);
         MapViewport.View view = place == null
            ? new MapViewport.View(
               left,
               top,
               this.width,
               this.depth,
               Projection.u(viewer.getX(), viewer.getZ()),
               Projection.v(viewer.getX(), viewer.getY(), viewer.getZ()),
               1.5,
               Integer.MIN_VALUE
            )
            : new MapViewport.View(
               left,
               top,
               this.width,
               this.depth,
               Projection.u(place.x() + 0.5, place.z() + 0.5),
               Projection.v(place.x() + 0.5, place.y(), place.z() + 0.5),
               1.5,
               Integer.MIN_VALUE
            );
         BlockPos ground = place == null ? viewer.blockPosition() : new BlockPos(place.x(), place.y(), place.z());
         if (map == null || place == null && !MapViewport.showsAnything(map, view)) {
            if (map != null) {
               MapViewport.ready(map, view);
            }

            graphics.fill(left, top, left + this.width, top + this.depth, argb(grassTop(looks, level, ground, style)));
         } else {
            this.viewport.draw(graphics, map, view);
         }

         int faceTop = top + this.depth;
         if (!here) {
            graphics.fill(left, faceTop, left + this.width, faceTop + this.side - 1, -12895687);
            graphics.fill(left, faceTop + this.side - 1, left + this.width, faceTop + this.side, closed(-12895687));
         } else {
            int x0 = (int)Math.floor(view.minU());
            int columns = Math.min(64, (int)Math.floor(view.minU() + this.width / 1.5) - x0 + 1);
            double edge = view.minV() + this.depth / 1.5;
            MapLevel fogged = place != null && map != null && map.roofless() && map.fogOfWar() ? map : null;
            MapTexture texture = this.face(level, looks, style, x0, columns, edge, ground, place == null, fogged);
            graphics.enableScissor(left, faceTop, left + this.width, faceTop + this.side);
            MapCanvas.of(graphics)
               .rect(
                  texture,
                  Sampling.CLAMP_CRISP,
                  view.screenX(x0),
                  faceTop,
                  view.screenX(x0 + columns),
                  faceTop + this.side,
                  0.0F,
                  0.0F,
                  columns / 64.0F,
                  this.side * 4 / 64.0F,
                  -1
               );
            graphics.disableScissor();
         }
      } else {
         graphics.fill(left, top, left + this.width, top + this.depth + this.side, -9532598);
      }
   }

   private MapTexture face(
      ClientLevel level, BlockLooks looks, MapStyle style, int x0, int columns, double edge, BlockPos player, boolean madeUp, @Nullable MapLevel fogged
   ) {
      if (this.face == null) {
         this.face = MapTexture.create("diorama map piece", 64, 1);
      }

      MapPiece.FaceKey key = new MapPiece.FaceKey(level, x0, columns, Math.round(edge * 8.0), style, looks, madeUp, fogged != null);
      long now = System.currentTimeMillis();
      if (key.equals(this.faceKey) && now - this.faceBuilt < 1000L) {
         return this.face;
      }

      this.faceKey = key;
      this.faceBuilt = now;
      Arrays.fill(this.facePixels, 0);
      MutableBlockPos pos = new MutableBlockPos();

      for (int c = 0; c < columns; c++) {
         int bx = x0 + c;
         int[] hit = surface(level, looks, bx, edge);
         if (hit != null && fogged != null && !fogged.isSeen(bx, hit[0], hit[1])) {
            hit = null;
         }

         if (hit == null && !madeUp) {
            for (int row = 0; row < this.side; row++) {
               int plain = abgr(row == this.side - 1 ? closed(-12895687) : -12895687);

               for (int t = 0; t < 4; t++) {
                  this.facePixels[(row * 4 + t) * 64 + c] = plain;
               }
            }
         } else {
            for (int row = 0; row < this.side; row++) {
               BlockState state;
               if (hit != null) {
                  pos.set(facing(bx, hit[1], true), hit[0] - row, facing(bx, hit[1], false));
                  state = Projection.facing().turned(level.getBlockState(pos));
               } else {
                  int pz = Projection.facing().blockZ(player.getX(), player.getZ());
                  pos.set(facing(bx, pz, true), player.getY() - 1 - row, facing(bx, pz, false));
                  state = (row == 0 ? Blocks.GRASS_BLOCK : (row <= 3 ? Blocks.DIRT : Blocks.STONE)).defaultBlockState();
               }

               for (int t = 0; t < 4; t++) {
                  int abgr = this.texel(level, looks, style, state, pos, row, t);
                  this.facePixels[(row * 4 + t) * 64 + c] = abgr;
               }
            }
         }
      }

      this.face.write(0, 0, 0, 64, this.facePixels);
      return this.face;
   }

   private int texel(ClientLevel level, BlockLooks looks, MapStyle style, BlockState state, BlockPos pos, int row, int t) {
      int id = looks.ensure(Block.BLOCK_STATE_REGISTRY.getId(state));
      if (id >= 0 && id < looks.kind.length && looks.kind[id] != 0 && looks.kind[id] != 5) {
         boolean grass = looks.tint[id] == 1;
         boolean topEdge = row == 0 && t < (grass ? 2 : 1);
         float[] rgb = albedo(looks, level, id, pos, topEdge, row == 0 && grass && !topEdge);
         float nudge = 1.0F + Math.max(style.blockVariation(), 0.12F) * blockNoise(pos.getX(), pos.getY(), pos.getZ());
         float[] light = faceLight(style, pos.getY());
         float close = row == this.side - 1 ? 0.75F : 1.0F;
         return toAbgr(grade(style, rgb[0] * light[0] * nudge * close, rgb[1] * light[1] * nudge * close, rgb[2] * light[2] * nudge * close));
      } else {
         return abgr(-14934492);
      }
   }

   private static int @Nullable [] surface(ClientLevel level, BlockLooks looks, int x, double v) {
      double sin = Projection.sin();
      double cos = Projection.cos();
      int minY = level.getMinBuildHeight();
      boolean ceiling = level.dimensionType().hasCeiling();
      int start;
      if (ceiling) {
         start = Math.min(level.getMaxBuildHeight() - 1, minY + level.dimensionType().logicalHeight() - 1);
      } else {
         int z = (int)Math.floor(v / sin);
         int y = level.getHeight(Types.MOTION_BLOCKING, facing(x, z, true), facing(x, z, false));

         for (int i = 0; i < 4; i++) {
            z = (int)Math.floor((v + y * cos) / sin);
            y = level.getHeight(Types.MOTION_BLOCKING, facing(x, z, true), facing(x, z, false));
         }

         if (y <= minY) {
            return null;
         }

         start = Math.min(level.getMaxBuildHeight() - 1, y + 16);
      }

      boolean underRoof = !ceiling;
      MutableBlockPos pos = new MutableBlockPos();

      for (int y = start; y >= minY; y--) {
         int zIn = (int)Math.floor((v + (y + 1) * cos) / sin);
         int zOut = (int)Math.floor((v + y * cos) / sin);

         for (int z = zIn; z >= zOut; z--) {
            BlockState state = level.getBlockState(pos.set(facing(x, z, true), y, facing(x, z, false)));
            if (!underRoof) {
               if (state.isAir()) {
                  underRoof = true;
               }
            } else if (ground(looks, state)) {
               return new int[]{y, z};
            }
         }
      }

      return null;
   }

   private static int facing(int x, int z, boolean worldX) {
      return worldX ? Projection.facing().worldBlockX(x, z) : Projection.facing().worldBlockZ(x, z);
   }

   private static boolean ground(BlockLooks looks, BlockState state) {
      int id = looks.ensure(Block.BLOCK_STATE_REGISTRY.getId(state));
      if (id >= 0 && id < looks.kind.length) {
         byte kind = looks.kind[id];
         return kind != 0 && kind != 5 && !state.is(BlockTags.LEAVES) && !state.is(BlockTags.LOGS);
      } else {
         return false;
      }
   }

   private static float[] albedo(BlockLooks looks, ClientLevel level, int id, BlockPos pos, boolean top, boolean bare) {
      int base = top ? looks.top[id] : looks.side[id];
      int tinted = top ? looks.topTinted[id] : looks.sideTinted[id];
      float w = bare ? 0.0F : (top ? looks.topTintWeight[id] : looks.sideTintWeight[id]);

      int tint = switch (looks.tint[id]) {
         case 1 -> BiomeColors.getAverageGrassColor(level, pos);
         case 2, 3 -> BiomeColors.getAverageFoliageColor(level, pos);
         case 4 -> level.getBlockTint(pos, BiomeTints.water());
         default -> looks.constantTint[id] < 0 ? 16777215 : looks.constantTint[id];
      };
      float bw = 1.0F - w;
      return new float[]{
         SRGB_TO_LINEAR[base >> 16 & 0xFF] * bw + SRGB_TO_LINEAR[tinted >> 16 & 0xFF] * SRGB_TO_LINEAR[tint >> 16 & 0xFF] * w,
         SRGB_TO_LINEAR[base >> 8 & 0xFF] * bw + SRGB_TO_LINEAR[tinted >> 8 & 0xFF] * SRGB_TO_LINEAR[tint >> 8 & 0xFF] * w,
         SRGB_TO_LINEAR[base & 0xFF] * bw + SRGB_TO_LINEAR[tinted & 0xFF] * SRGB_TO_LINEAR[tint & 0xFF] * w
      };
   }

   private static float[] faceLight(MapStyle style, int y) {
      double elevation = Math.toRadians(style.sunElevation());
      double lambert = Math.max(0.0, Math.cos(elevation) * 0.479) / Math.sin(elevation);
      float direct = (float)(style.sunStrength() * lambert);
      float ambient = style.ambient() * (1.0F - style.skyOcclusion() * 0.42499995F);
      float shade = (1.0F + style.heightShade() * (float)Math.max(-1.0, Math.min(1.0, (y + 0.5 - 64.0) / 64.0))) * 0.82F;
      float[] sky = style.shadowTint();
      float[] sun = style.sunTint();
      return new float[]{
         (ambient * sky[0] + direct * sun[0]) * shade, (ambient * sky[1] + direct * sun[1]) * shade, (ambient * sky[2] + direct * sun[2]) * shade
      };
   }

   private static int grassTop(BlockLooks looks, ClientLevel level, BlockPos pos, MapStyle style) {
      int id = looks.lookId(Blocks.GRASS_BLOCK.defaultBlockState());
      float[] rgb = albedo(looks, level, id, pos, true, false);
      float ambient = style.ambient();
      float shade = 1.0F + style.heightShade() * (float)Math.max(-1.0, Math.min(1.0, (pos.getY() - 64) / 64.0));
      float[] sky = style.shadowTint();
      float[] sun = style.sunTint();
      float[] light = new float[3];

      for (int i = 0; i < 3; i++) {
         light[i] = (ambient * sky[i] + style.sunStrength() * sun[i]) * shade;
      }

      return toAbgr(grade(style, rgb[0] * light[0], rgb[1] * light[1], rgb[2] * light[2]));
   }

   private static float[] grade(MapStyle style, float r, float g, float b) {
      float l = 0.2126F * r + 0.7152F * g + 0.0722F * b;
      float s = style.saturation();
      float e = style.exposure();
      return new float[]{srgb((l + (r - l) * s) * e), srgb((l + (g - l) * s) * e), srgb((l + (b - l) * s) * e)};
   }

   private static float srgb(float linear) {
      double v = Math.max(0.0F, Math.min(1.0F, linear));
      return (float)(v <= 0.0031308 ? v * 12.92 : 1.055 * Math.pow(v, 0.4166666666666667) - 0.055);
   }

   private static int toAbgr(float[] rgb) {
      return 0xFF000000 | Math.round(rgb[2] * 255.0F) << 16 | Math.round(rgb[1] * 255.0F) << 8 | Math.round(rgb[0] * 255.0F);
   }

   private static int closed(int argb) {
      int r = Math.round((argb >> 16 & 0xFF) * 0.75F);
      int g = Math.round((argb >> 8 & 0xFF) * 0.75F);
      int b = Math.round((argb & 0xFF) * 0.75F);
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   private @Nullable MapLevel savedMap(String dimension) {
      Maps maps = Maps.get();
      if (maps != null && !ServerRules.mapOff(dimension)) {
         Path folder = maps.tileFolder(dimension, maps.active(dimension));
         if (this.saved == null || !this.saved.folder().equals(folder) || this.saved.style() != MapStyle.get()) {
            this.closeSaved();
            this.saved = new MapPiece.SavedMap(folder, MapStyle.get(), MapLevel.saved(folder));
         }

         this.saved.map().setFogOfWar(ServerRules.fogOfWar(dimension.equals("minecraft:the_nether"), this.saved.map().roofless()));
         return this.saved.map();
      } else {
         this.closeSaved();
         return null;
      }
   }

   void tick() {
      if (this.saved != null) {
         this.saved.map().update();
      }
   }

   private void closeSaved() {
      if (this.saved != null) {
         this.saved.map().close();
         this.saved = null;
      }
   }

   private static int abgr(int argb) {
      return argb & -16711936 | (argb & 0xFF) << 16 | argb >> 16 & 0xFF;
   }

   private static int argb(int abgr) {
      return abgr(abgr);
   }

   private static float blockNoise(int x, int y, int z) {
      int h = x * 73856093 ^ y * 19349663 ^ z * 83492791;
      h ^= h >>> 13;
      h *= 1540483477;
      h ^= h >>> 15;
      return (h & 65535) / 32767.5F - 1.0F;
   }

   void close() {
      this.viewport.close();
      this.closeSaved();
      if (this.face != null) {
         this.face.close();
         this.face = null;
      }

      this.faceKey = null;
   }

   static {
      for (int i = 0; i < 256; i++) {
         double v = i / 255.0;
         SRGB_TO_LINEAR[i] = (float)(v <= 0.04045 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4));
      }
   }

   private record FaceKey(ClientLevel level, int x0, int columns, long edge, MapStyle style, @Nullable BlockLooks looks, boolean madeUp, boolean fogged) {
   }

   record Place(String dimension, int x, int y, int z) {
   }

   private record SavedMap(Path folder, MapStyle style, MapLevel map) {
   }
}
