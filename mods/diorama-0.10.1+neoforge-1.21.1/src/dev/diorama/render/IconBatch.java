package dev.diorama.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.diorama.render.gpu.MapCanvas;
import dev.diorama.render.gpu.MapTexture;
import dev.diorama.render.gpu.Sampling;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class IconBatch {
   private static final float CELL = 2.0F;
   private static final int MAX_CELLS = 65536;
   private static int[] grid = new int[0];
   private static final ArrayDeque<IconBatch> SPARE = new ArrayDeque<>();
   private static final ArrayDeque<IconBatch> SPARE_MEASURING = new ArrayDeque<>();
   private static final int KEEP_QUADS = 4096;
   private GuiGraphics graphics;
   private final boolean batched;
   private final boolean measuring;
   private float[] xyz = new float[0];
   private float[] uvs = new float[0];
   private int[] colors = new int[0];
   private int[] textureIds = new int[0];
   private int[] runs = new int[0];
   private int quads;
   private final List<Object> textures = new ArrayList<>();
   private final Map<Object, Integer> textureIndex = new HashMap<>();
   private final List<Integer> iconStarts = new ArrayList<>(List.of(0));
   private int run;
   private int lastTexture = -1;
   private final Vector3f at = new Vector3f();
   private long[] order = new long[0];
   private float[] bounds = new float[0];
   private int[] layers = new int[0];
   private float[] groupXyz = new float[0];
   private float[] groupUvs = new float[0];
   private int[] groupColors = new int[0];
   private final List<IconBatch.ItemDraw> items = new ArrayList<>();
   private int itemsDrawn;

   private IconBatch(GuiGraphics graphics, boolean batched, boolean measuring) {
      this.graphics = graphics;
      this.batched = batched;
      this.measuring = measuring;
      this.textures.add(null);
   }

   public static IconBatch direct(GuiGraphics graphics) {
      return new IconBatch(graphics, false, false);
   }

   public static IconBatch batched(GuiGraphics graphics) {
      return kept(graphics, false);
   }

   public static IconBatch measuring(GuiGraphics graphics) {
      return kept(graphics, true);
   }

   private static IconBatch kept(GuiGraphics graphics, boolean measuring) {
      IconBatch batch = (measuring ? SPARE_MEASURING : SPARE).poll();
      if (batch == null) {
         return new IconBatch(graphics, true, measuring);
      }

      batch.graphics = graphics;
      return batch;
   }

   private void handBack(int used) {
      this.clear();
      this.textures.clear();
      this.textures.add(null);
      this.textureIndex.clear();
      this.itemsDrawn = 0;
      this.graphics = null;
      if (this.colors.length > 4096 && this.colors.length > used * 4) {
         this.xyz = new float[0];
         this.uvs = new float[0];
         this.colors = new int[0];
         this.textureIds = new int[0];
         this.runs = new int[0];
         this.order = new long[0];
         this.bounds = new float[0];
         this.layers = new int[0];
         this.groupXyz = new float[0];
         this.groupUvs = new float[0];
         this.groupColors = new int[0];
      }

      (this.measuring ? SPARE_MEASURING : SPARE).push(this);
   }

   public GuiGraphics graphics() {
      return this.graphics;
   }

   public void fill(float x0, float y0, float x1, float y1, int argb) {
      if (!this.batched) {
         MapCanvas.of(this.graphics).solid(new float[]{x0, y0, x0, y1, x1, y1, x1, y0}, argb);
      } else {
         this.add(0, x0, y0, x1, y1, 0.0F, 0.0F, 0.0F, 0.0F, argb);
      }
   }

   public void blit(ResourceLocation texture, int x, int y, int u, int v, int w, int h, int texW, int texH, int argb) {
      if (!this.batched) {
         MapCanvas.of(this.graphics).blit(texture, x, y, u, v, w, h, texW, texH, argb);
      } else {
         this.add(this.textureId(texture), x, y, x + w, y + h, (float)u / texW, (float)v / texH, (float)(u + w) / texW, (float)(v + h) / texH, argb);
      }
   }

   public void rect(MapTexture texture, Sampling sampling, float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, int argb) {
      if (!this.batched) {
         MapCanvas.of(this.graphics).rect(texture, sampling, x0, y0, x1, y1, u0, v0, u1, v1, argb);
      } else {
         this.add(this.textureId(new IconBatch.SampledTexture(texture, sampling)), x0, y0, x1, y1, u0, v0, u1, v1, argb);
      }
   }

   public void playerFace(AbstractClientPlayer player, int x, int y, int size, int argb) {
      if (this.measuring) {
         this.add(0, x, y, x + size, y + size, 0.0F, 0.0F, 0.0F, 0.0F, argb);
      } else {
         MapCanvas.of(this.graphics).playerFace(player, x, y, size, argb);
      }
   }

   public boolean inside(Circle circle) {
      for (int p = 0; p < this.quads * 4; p++) {
         if (!circle.contains(this.xyz[p * 3], this.xyz[p * 3 + 1])) {
            return false;
         }
      }

      return true;
   }

   public void clear() {
      this.quads = 0;
      this.iconStarts.clear();
      this.iconStarts.add(0);
      this.run = 0;
      this.lastTexture = -1;
      this.items.clear();
   }

   public void item(ItemStack stack, float x, float y, float scale) {
      if (!this.measuring) {
         if (!this.batched) {
            this.drawItem(stack, x, y, scale);
         } else {
            this.run++;
            this.items.add(new IconBatch.ItemDraw(stack, x, y, scale, this.iconStarts.size() - 1, this.run, this.quads));
            this.run++;
            this.lastTexture = -1;
         }
      }
   }

   private void drawItem(ItemStack stack, float x, float y, float scale) {
      GuiGraphics graphics = this.graphics;
      graphics.pose().pushPose();
      graphics.pose().translate(x, y, 0.0F);
      graphics.pose().scale(scale, scale, 1.0F);
      PoseStack pose = graphics.pose();
      pose.translate(0.0F, 0.0F, 10 * Math.min(this.itemsDrawn++, 800));
      graphics.renderItem(stack, 0, 0);
      graphics.pose().popPose();
   }

   public void endIcon() {
      if (this.batched && this.quads > this.iconStarts.get(this.iconStarts.size() - 1)) {
         this.iconStarts.add(this.quads);
      }

      this.run = 0;
      this.lastTexture = -1;
   }

   private int textureId(Object texture) {
      return this.textureIndex.computeIfAbsent(texture, key -> {
         this.textures.add(key);
         return this.textures.size() - 1;
      });
   }

   private void add(int texture, float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, int argb) {
      if (this.quads == this.colors.length) {
         int n = Math.max(64, this.quads * 2);
         this.xyz = Arrays.copyOf(this.xyz, n * 12);
         this.uvs = Arrays.copyOf(this.uvs, n * 8);
         this.colors = Arrays.copyOf(this.colors, n);
         this.textureIds = Arrays.copyOf(this.textureIds, n);
         this.runs = Arrays.copyOf(this.runs, n);
      }

      if (this.lastTexture >= 0 && texture != this.lastTexture) {
         this.run++;
      }

      this.lastTexture = texture;
      int q = this.quads++;
      this.colors[q] = argb;
      this.textureIds[q] = texture;
      this.runs[q] = this.run;
      this.corner(q, 0, x0, y0, u0, v0);
      this.corner(q, 1, x0, y1, u0, v1);
      this.corner(q, 2, x1, y1, u1, v1);
      this.corner(q, 3, x1, y0, u1, v0);
   }

   private void corner(int q, int i, float x, float y, float u, float v) {
      int p = (q * 4 + i) * 3;
      Matrix4f pose = this.graphics.pose().last().pose();
      Vector3f at = pose.transformPosition(x, y, 0.0F, this.at);
      this.xyz[p] = at.x;
      this.xyz[p + 1] = at.y;
      this.xyz[p + 2] = at.z;
      int t = (q * 4 + i) * 2;
      this.uvs[t] = u;
      this.uvs[t + 1] = v;
   }

   public void draw() {
      int used = this.quads;
      this.drawCollected();
      if (this.batched) {
         this.handBack(used);
      }
   }

   private void drawCollected() {
      this.endIcon();
      int icons = this.iconStarts.size() - 1;
      if (!this.measuring) {
         if (this.batched && icons != 0) {
            int[] layers = this.layers(icons);
            int maxLayer = Arrays.stream(layers, 0, icons).max().orElse(0);
            int maxRun = Arrays.stream(this.runs, 0, this.quads).max().orElse(0);
            boolean sorted = maxLayer <= 32767 && maxRun <= 255 && this.textures.size() <= 65536 && this.quads <= 16777216;
            int quads = this.quads;
            if (this.order.length < quads) {
               this.order = new long[this.colors.length];
            }

            long[] order = this.order;

            for (int icon = 0; icon < icons; icon++) {
               for (int q = this.iconStarts.get(icon); q < this.iconStarts.get(icon + 1); q++) {
                  order[q] = sorted ? (long)layers[icon] << 48 | (long)this.runs[q] << 40 | (long)this.textureIds[q] << 24 | q : q;
               }
            }

            if (sorted) {
               Arrays.sort(order, 0, quads);
            }

            long[] itemKeys = new long[this.items.size()];
            Integer[] itemOrder = new Integer[this.items.size()];

            for (int i = 0; i < itemKeys.length; i++) {
               IconBatch.ItemDraw item = this.items.get(i);
               itemKeys[i] = sorted ? (long)layers[item.icon()] << 8 | item.run() : item.quadsBefore();
               itemOrder[i] = i;
            }

            Arrays.sort(itemOrder, (a, b) -> Long.compare(itemKeys[a], itemKeys[b]));
            int nextItem = 0;
            MapCanvas canvas = MapCanvas.of(this.graphics);
            if (this.groupColors.length < quads) {
               this.groupXyz = new float[this.colors.length * 12];
               this.groupUvs = new float[this.colors.length * 8];
               this.groupColors = new int[this.colors.length];
            }

            float[] groupXyz = this.groupXyz;
            float[] groupUvs = this.groupUvs;
            int[] groupColors = this.groupColors;
            int start = 0;

            while (start < quads) {
               long place = sorted ? order[start] >>> 40 : order[start];

               while (nextItem < itemOrder.length && (sorted ? itemKeys[itemOrder[nextItem]] < place : itemKeys[itemOrder[nextItem]] <= place)) {
                  IconBatch.ItemDraw item = this.items.get(itemOrder[nextItem++]);
                  this.drawItem(item.stack(), item.x(), item.y(), item.scale());
               }

               long stop = !sorted && nextItem < itemOrder.length ? itemKeys[itemOrder[nextItem]] : Long.MAX_VALUE;
               int texture0 = this.textureIds[quad(order[start], sorted)];
               int count = 0;

               for (int i = start;
                  i < quads && (sorted ? order[i] >>> 24 == order[start] >>> 24 : this.textureIds[(int)order[i]] == texture0 && order[i] < stop);
                  i++
               ) {
                  int q = quad(order[i], sorted);
                  System.arraycopy(this.xyz, q * 12, groupXyz, count * 12, 12);
                  System.arraycopy(this.uvs, q * 8, groupUvs, count * 8, 8);
                  groupColors[count++] = this.colors[q];
               }

               Object texture = this.textures.get(texture0);
               if (texture == null) {
                  canvas.solids(groupXyz, groupColors, count);
               } else if (texture instanceof ResourceLocation id) {
                  canvas.blits(id, groupXyz, groupUvs, groupColors, count);
               } else {
                  IconBatch.SampledTexture sampled = (IconBatch.SampledTexture)texture;
                  canvas.rects(sampled.texture(), sampled.sampling(), groupXyz, groupUvs, groupColors, count);
               }

               start += count;
            }

            while (nextItem < itemOrder.length) {
               IconBatch.ItemDraw item = this.items.get(itemOrder[nextItem++]);
               this.drawItem(item.stack(), item.x(), item.y(), item.scale());
            }

            this.items.clear();
         } else {
            for (IconBatch.ItemDraw item : this.items) {
               this.drawItem(item.stack(), item.x(), item.y(), item.scale());
            }

            this.items.clear();
         }
      }
   }

   private static int quad(long key, boolean sorted) {
      return sorted ? (int)(key & 16777215L) : (int)key;
   }

   private int[] layers(int icons) {
      if (this.layers.length < icons) {
         this.bounds = new float[icons * 8];
         this.layers = new int[icons * 2];
      }

      float[] bounds = this.bounds;
      float minX = Float.MAX_VALUE;
      float minY = Float.MAX_VALUE;
      float maxX = -Float.MAX_VALUE;
      float maxY = -Float.MAX_VALUE;

      for (int icon = 0; icon < icons; icon++) {
         float x0 = Float.MAX_VALUE;
         float y0 = Float.MAX_VALUE;
         float x1 = -Float.MAX_VALUE;
         float y1 = -Float.MAX_VALUE;

         for (int p = this.iconStarts.get(icon) * 4; p < this.iconStarts.get(icon + 1) * 4; p++) {
            x0 = Math.min(x0, this.xyz[p * 3]);
            x1 = Math.max(x1, this.xyz[p * 3]);
            y0 = Math.min(y0, this.xyz[p * 3 + 1]);
            y1 = Math.max(y1, this.xyz[p * 3 + 1]);
         }

         bounds[icon * 4] = x0;
         bounds[icon * 4 + 1] = y0;
         bounds[icon * 4 + 2] = x1;
         bounds[icon * 4 + 3] = y1;
         minX = Math.min(minX, x0);
         minY = Math.min(minY, y0);
         maxX = Math.max(maxX, x1);
         maxY = Math.max(maxY, y1);
      }

      float cell = Math.max(2.0F, (float)Math.sqrt((maxX - minX) * (maxY - minY) / 65536.0F));
      int columns = (int)((maxX - minX) / cell) + 1;
      int rows = (int)((maxY - minY) / cell) + 1;
      if (grid.length < columns * rows) {
         grid = new int[columns * rows];
      } else {
         Arrays.fill(grid, 0, columns * rows, 0);
      }

      int[] above = grid;
      int[] layers = this.layers;

      for (int icon = 0; icon < icons; icon++) {
         int c0 = (int)((bounds[icon * 4] - minX) / cell);
         int r0 = (int)((bounds[icon * 4 + 1] - minY) / cell);
         int c1 = (int)((bounds[icon * 4 + 2] - minX) / cell);
         int r1 = (int)((bounds[icon * 4 + 3] - minY) / cell);
         int layer = 0;

         for (int r = r0; r <= r1; r++) {
            for (int c = c0; c <= c1; c++) {
               layer = Math.max(layer, above[r * columns + c]);
            }
         }

         layers[icon] = layer;

         for (int r = r0; r <= r1; r++) {
            Arrays.fill(above, r * columns + c0, r * columns + c1 + 1, layer + 1);
         }
      }

      return layers;
   }

   private record ItemDraw(ItemStack stack, float x, float y, float scale, int icon, int run, int quadsBefore) {
   }

   private record SampledTexture(MapTexture texture, Sampling sampling) {
   }
}
