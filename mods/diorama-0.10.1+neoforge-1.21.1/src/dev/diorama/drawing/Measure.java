package dev.diorama.drawing;

import dev.diorama.render.MapViewport;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class Measure {
   private double @Nullable [] from;
   private double @Nullable [] to;
   private Measure.End fromEnd = new Measure.End();
   private Measure.End toEnd = new Measure.End();
   private boolean dragging;

   void start(double[] mapPoint, CompletableFuture<@Nullable BlockPos> pick) {
      this.from = mapPoint;
      this.to = mapPoint;
      this.fromEnd = new Measure.End();
      this.toEnd = new Measure.End();
      this.fromEnd.ask(pick);
      this.toEnd.ask(pick);
      this.dragging = true;
   }

   void move(double[] mapPoint, CompletableFuture<@Nullable BlockPos> pick) {
      this.to = mapPoint;
      this.toEnd.ask(pick);
   }

   void end() {
      this.dragging = false;
   }

   boolean dragging() {
      return this.dragging;
   }

   boolean shown() {
      return this.from != null;
   }

   void clear() {
      this.from = null;
      this.to = null;
      this.fromEnd = new Measure.End();
      this.toEnd = new Measure.End();
      this.dragging = false;
   }

   int blocks() {
      this.fromEnd.refresh();
      this.toEnd.refresh();
      BlockPos a = this.fromEnd.block;
      BlockPos b = this.toEnd.block;
      return a != null && b != null ? (int)Math.round(Math.hypot(b.getX() - a.getX(), b.getZ() - a.getZ())) : -1;
   }

   Component reading() {
      this.fromEnd.refresh();
      this.toEnd.refresh();
      BlockPos a = this.fromEnd.block;
      BlockPos b = this.toEnd.block;
      if (a != null && b != null) {
         int dx = b.getX() - a.getX();
         int dy = b.getY() - a.getY();
         int dz = b.getZ() - a.getZ();
         List<Component> ways = new ArrayList<>();
         if (dx != 0) {
            ways.add(Component.translatable(dx > 0 ? "diorama.draw.measure.east" : "diorama.draw.measure.west", new Object[]{Math.abs(dx)}));
         }

         if (dz != 0) {
            ways.add(Component.translatable(dz > 0 ? "diorama.draw.measure.south" : "diorama.draw.measure.north", new Object[]{Math.abs(dz)}));
         }

         Component out = Component.translatable("diorama.draw.measure.blocks", new Object[]{this.blocks()});
         if (!ways.isEmpty()) {
            Component joined = ways.get(0);

            for (int i = 1; i < ways.size(); i++) {
               joined = joined.copy().append(", ").append(ways.get(i));
            }

            out = out.copy().append(" · ").append(joined);
         }

         if (dy != 0) {
            out = out.copy()
               .append(" · ")
               .append(Component.translatable(dy > 0 ? "diorama.draw.measure.up" : "diorama.draw.measure.down", new Object[]{Math.abs(dy)}));
         }

         return out;
      } else {
         boolean looking = a == null && !this.fromEnd.answered || b == null && !this.toEnd.answered;
         return Component.translatable(looking ? "diorama.draw.measure.looking" : "diorama.draw.measure.unknown");
      }
   }

   @Nullable Drawing line() {
      return this.from != null && this.to != null
         ? new Drawing(Drawing.Kind.STROKE, "", -1, new double[]{this.from[0], this.from[1], this.to[0], this.to[1]}, "")
         : null;
   }

   void drawReading(GuiGraphics graphics, Font font, MapViewport.View view) {
      if (this.to != null) {
         String text = this.reading().getString();
         int width = font.width(text);
         int x = Math.round(view.screenX(this.to[0])) - width / 2;
         int y = Math.round(view.screenY(this.to[1])) - 16;
         x = Math.max(view.x() + 2, Math.min(view.x() + view.width() - width - 2, x));
         y = Math.max(view.y() + 18, Math.min(view.y() + view.height() - 26, y));
         graphics.fill(x - 2, y - 2, x + width + 1, y + 9, -1073741824);
         graphics.drawString(font, text, x, y, -1, true);
      }
   }

   private static final class End {
      private @Nullable CompletableFuture<@Nullable BlockPos> pick;
      private @Nullable BlockPos block;
      private boolean answered;

      void ask(CompletableFuture<@Nullable BlockPos> pick) {
         this.pick = pick;
         this.refresh();
      }

      void refresh() {
         CompletableFuture<BlockPos> pending = this.pick;
         if (pending != null && pending.isDone()) {
            this.block = !pending.isCompletedExceptionally() && !pending.isCancelled() ? pending.join() : null;
            this.answered = true;
            this.pick = null;
         }
      }
   }

   public interface Picker {
      CompletableFuture<@Nullable BlockPos> at(double var1, double var3);
   }
}
