package dev.diorama;

import dev.diorama.bake.BiomeTints;
import dev.diorama.bake.BlockLooks;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

final class DepthGauge {
   static final int WIDTH = 12;
   private static final int FROM_RIGHT = 28;
   private static final int TOP = 44;
   private static final int FROM_BOTTOM = 34;
   private static final int HEAD = 10;
   private static final int STRIPS = 4;
   static final int STEP = 4;
   private static final int PLATE = -1072688104;
   private static final int OPEN = -15394786;
   private static final int BAND = -12604939;
   private static final long REREAD_TICKS = 20L;
   private @Nullable BlockPos readAt;
   private long readTick;
   private int columnTop;
   private int columnBottom;
   private int topFace;
   private int[] colours = new int[0];
   private int x0;
   private int top;
   private int bottom;
   private double lo;
   private double hi;
   private float bandY;
   private boolean dragging;
   private int preview;

   boolean dragging() {
      return this.dragging;
   }

   void layout(ClientLevel level, BlockPos feet, int layer, int screenWidth, int screenHeight) {
      this.read(level, feet);
      this.x0 = screenWidth - 28;
      this.top = 44;
      this.bottom = Math.max(94, screenHeight - 34);
      if (!this.dragging) {
         this.lo = level.getMinBuildHeight();
         this.hi = Math.max(Math.max(this.columnTop + 1, feet.getY()), layer);
      }
   }

   int upButtonX() {
      return this.x0 - 2;
   }

   int upButtonY() {
      return this.top - 14;
   }

   int downButtonY() {
      return this.bottom + 5;
   }

   boolean contains(double x, double y) {
      return x >= this.x0 - 3 && x < this.x0 + 12 + 6 && y >= this.top + 10 - 6 && y < this.bottom + 2;
   }

   void grab(ClientLevel level, double y) {
      this.dragging = true;
      this.preview = this.layerAt(level, y);
   }

   void drag(ClientLevel level, double y) {
      if (this.dragging) {
         this.preview = this.layerAt(level, y);
      }
   }

   int release() {
      this.dragging = false;
      return this.preview;
   }

   void cancel() {
      this.dragging = false;
   }

   float[] band() {
      return new float[]{this.x0 + 6.0F, this.bandY};
   }

   private double screenY(double wy) {
      double span = this.bottom - (this.top + 10);
      return this.top + 10 + (this.hi - wy) / Math.max(1.0, this.hi - this.lo) * span;
   }

   private int layerAt(ClientLevel level, double y) {
      double span = this.bottom - (this.top + 10);
      double wy = this.hi - (y - this.top - 10.0) / span * (this.hi - this.lo);
      int layer = (int)Math.round((wy + 2.0) / 4.0) * 4;
      int max = Math.min(level.getMaxBuildHeight() - 1 + 1, (int)Math.ceil(this.hi / 4.0) * 4);
      return Math.max(level.getMinBuildHeight() + 4, Math.min(max, layer));
   }

   void draw(GuiGraphics graphics, Font font, double feetY, int layer, int avoidX, int avoidTop, int avoidBottom) {
      int x1 = this.x0 + 12;
      int ground = (int)Math.round(this.screenY(this.columnTop + 1));
      graphics.fill(this.x0 - 2, this.top + 10 - 6, x1 + 6, this.bottom + 2, -1072688104);
      graphics.fill(this.x0, ground - 4, x1, ground, this.topFace);
      int stripWidth = 3;
      int runStart = ground;
      int runBlock = this.blockAt(ground);

      for (int row = ground + 1; row <= this.bottom; row++) {
         int block = row < this.bottom ? this.blockAt(row) : Integer.MIN_VALUE;
         if (block != runBlock) {
            int index = runBlock - this.columnBottom;
            if (index >= 0 && index < this.colours.length / 4) {
               for (int s = 0; s < 4; s++) {
                  graphics.fill(this.x0 + s * stripWidth, runStart, this.x0 + (s + 1) * stripWidth, row, this.colours[index * 4 + s]);
               }
            }

            runStart = row;
            runBlock = block;
         }
      }

      graphics.fill(x1, ground - 2, x1 + 4, this.bottom, -1879048192);
      int shown = this.dragging ? this.preview : layer;
      int sy0 = (int)Math.round(this.screenY(shown));
      int sy1 = Math.max(sy0 + 3, (int)Math.round(this.screenY(shown - 4)));
      graphics.fill(this.x0 - 3, sy0 - 1, x1 + 3, sy1 + 1, -1);
      graphics.fill(this.x0 - 2, sy0, x1 + 2, sy1, -12604939);
      int mid = (sy0 + sy1) / 2;
      this.bandY = (sy0 + sy1) / 2.0F;
      label(graphics, font, "y=" + shown, mid, this.x0 - 5, -1, avoidX, avoidTop, avoidBottom);
      int py = (int)Math.round(this.screenY(feetY));
      graphics.fill(this.x0 - 5, py - 2, this.x0 - 4, py + 3, -1);
      graphics.fill(this.x0 - 4, py - 1, this.x0 - 3, py + 2, -1);
      graphics.fill(this.x0 - 3, py, this.x0 - 2, py + 1, -1);
      int ly = Math.abs(py - mid) >= 11 ? py : (py <= mid ? mid - 11 : mid + 11);
      label(graphics, font, Component.translatable("diorama.map.you").getString(), ly, this.x0 - 6, -2039584, avoidX, avoidTop, avoidBottom);
   }

   private static void label(GuiGraphics graphics, Font font, String text, int y, int right, int colour, int avoidX, int avoidTop, int avoidBottom) {
      if (y + 5 > avoidTop && y - 5 < avoidBottom) {
         right = Math.min(right, avoidX);
      }

      int w = font.width(text);
      graphics.fill(right - 4 - w, y - 5, right, y + 5, -1072688104);
      graphics.drawString(font, text, right - 2 - w, y - 4, colour, false);
   }

   private int blockAt(int row) {
      double span = this.bottom - (this.top + 10);
      return (int)Math.floor(this.hi - (row + 0.5 - this.top - 10.0) / span * (this.hi - this.lo));
   }

   private void read(ClientLevel level, BlockPos feet) {
      long tick = level.getGameTime();
      if (!feet.equals(this.readAt) || tick - this.readTick >= 20L || tick < this.readTick) {
         this.readAt = feet.immutable();
         this.readTick = tick;
         BlockLooks looks = BlockLooks.get();
         int minY = level.getMinBuildHeight();
         MutableBlockPos pos = new MutableBlockPos();
         int groundY = minY;

         for (int y = level.getMaxBuildHeight() - 1; y >= minY; y--) {
            BlockState state = level.getBlockState(pos.set(feet.getX(), y, feet.getZ()));
            if (!open(looks, state) && !state.is(BlockTags.LEAVES)) {
               groundY = y;
               break;
            }
         }

         this.columnTop = groundY;
         this.columnBottom = minY;
         int blocks = groundY - minY + 1;
         this.colours = new int[blocks * 4];

         for (int y = minY; y <= groundY; y++) {
            BlockState state = level.getBlockState(pos.set(feet.getX(), y, feet.getZ()));
            int base = open(looks, state) ? -15394786 : colour(looks, level, pos, state, false);

            for (int s = 0; s < 4; s++) {
               this.colours[(y - minY) * 4 + s] = base == -15394786 ? -15394786 : vary(base, y, s);
            }
         }

         BlockState topState = level.getBlockState(pos.set(feet.getX(), groundY, feet.getZ()));
         this.topFace = colour(looks, level, pos, topState, true);
      }
   }

   private static boolean open(@Nullable BlockLooks looks, BlockState state) {
      if (state.isAir()) {
         return true;
      }

      if (looks == null) {
         return false;
      }

      byte kind = looks.kind[looks.lookId(state)];
      return kind == 0 || kind == 5;
   }

   private static int colour(@Nullable BlockLooks looks, ClientLevel level, BlockPos pos, BlockState state, boolean top) {
      if (looks == null) {
         return -8421505;
      }

      int id = looks.lookId(state);
      int base = top ? looks.top[id] : looks.side[id];
      int tinted = top ? looks.topTinted[id] : looks.sideTinted[id];
      float w = top ? looks.topTintWeight[id] : looks.sideTintWeight[id];

      int tint = switch (looks.tint[id]) {
         case 1 -> BiomeColors.getAverageGrassColor(level, pos);
         case 2, 3 -> BiomeColors.getAverageFoliageColor(level, pos);
         case 4 -> level.getBlockTint(pos, BiomeTints.water());
         default -> looks.constantTint[id] < 0 ? 16777215 : looks.constantTint[id];
      };
      int r = mix(base >> 16, tinted >> 16, tint >> 16, w);
      int g = mix(base >> 8, tinted >> 8, tint >> 8, w);
      int b = mix(base, tinted, tint, w);
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   private static int mix(int base, int tinted, int tint, float w) {
      float c = (base & 0xFF) * (1.0F - w) + (tinted & 0xFF) * (tint & 0xFF) / 255.0F * w;
      return Math.max(0, Math.min(255, Math.round(c)));
   }

   private static int vary(int argb, int y, int strip) {
      int h = y * -1640531535 + strip * -2048144789;
      h ^= h >>> 15;
      h *= 739982445;
      h ^= h >>> 12;
      float f = 0.84F * (1.0F + ((h & 0xFF) / 255.0F - 0.5F) * 0.12F);
      int r = Math.min(255, Math.round((argb >> 16 & 0xFF) * f));
      int g = Math.min(255, Math.round((argb >> 8 & 0xFF) * f));
      int b = Math.min(255, Math.round((argb & 0xFF) * f));
      return 0xFF000000 | r << 16 | g << 8 | b;
   }
}
