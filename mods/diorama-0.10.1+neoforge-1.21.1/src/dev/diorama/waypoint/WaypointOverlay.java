package dev.diorama.waypoint;

import dev.diorama.Projection;
import dev.diorama.Settings;
import dev.diorama.render.Circle;
import dev.diorama.render.MapViewport;
import dev.diorama.render.Pins;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public final class WaypointOverlay {
   private WaypointOverlay() {
   }

   public static List<WaypointOverlay.Pin> layout(MapViewport.View view, List<Waypoint> waypoints, boolean fullscreen) {
      List<WaypointOverlay.Pin> pins = new ArrayList<>();
      int size = fullscreen ? 15 : 13;
      Settings settings = Settings.get();
      float scale = Pins.scale(fullscreen ? settings.waypointMapSize : settings.waypointMinimapSize);
      float cx = view.x() + view.width() / 2.0F;
      float cy = view.y() + view.height() / 2.0F;
      Circle circle = view.round() ? Circle.in(view) : null;

      for (Waypoint waypoint : waypoints) {
         if (WaypointStore.isShown(waypoint)) {
            float inset = Pins.edgeReach(waypoint, size) * scale;
            float halfW = Math.max(1.0F, view.width() / 2.0F - inset);
            float halfH = Math.max(1.0F, view.height() / 2.0F - inset);
            float radius = circle == null ? 0.0F : Math.max(1.0F, circle.r - inset);
            float x = view.screenX(Projection.u(waypoint.x + 0.5, waypoint.z + 0.5));
            float y = view.screenY(Projection.v(waypoint.x + 0.5, waypoint.y, waypoint.z + 0.5));
            float dx = x - cx;
            float dy = y - cy;
            if (circle != null) {
               if (Pins.fitsStanding(waypoint, size, scale, x, y, circle)) {
                  pins.add(new WaypointOverlay.Pin(waypoint, x, y, size, scale, false));
               } else {
                  float reach = Math.max(1.0F, (float)Math.hypot(dx, dy) / radius);
                  pins.add(new WaypointOverlay.Pin(waypoint, cx + dx / reach, cy + dy / reach, size, scale, true));
               }
            } else if (Pins.fitsStanding(waypoint, size, scale, x, y, view.x(), view.y(), view.x() + view.width(), view.y() + view.height())) {
               pins.add(new WaypointOverlay.Pin(waypoint, x, y, size, scale, false));
            } else {
               float reach = Math.max(1.0F, Math.max(Math.abs(dx) / halfW, Math.abs(dy) / halfH));
               pins.add(new WaypointOverlay.Pin(waypoint, cx + dx / reach, cy + dy / reach, size, scale, true));
            }
         }
      }

      return pins;
   }

   public static void draw(
      GuiGraphics graphics, MapViewport.View view, List<WaypointOverlay.Pin> pins, boolean fullscreen, WaypointOverlay.@Nullable Pin hovered
   ) {
      Minecraft mc = Minecraft.getInstance();
      Font font = mc.font;
      graphics.enableScissor(view.x(), view.y(), view.x() + view.width(), view.y() + view.height());
      float cx = view.x() + view.width() / 2.0F;
      float cy = view.y() + view.height() / 2.0F;

      for (WaypointOverlay.Pin pin : pins) {
         Waypoint waypoint = pin.waypoint();
         int s = (int)pin.size();
         if (pin.clamped()) {
            Pins.edge(graphics, waypoint, pin.x(), pin.y(), s, Math.atan2(pin.y() - cy, pin.x() - cx), pin.scale());
         } else {
            Pins.standing(graphics, waypoint, pin.x(), pin.y(), s, pin.scale());
         }

         boolean named = pin == hovered || view.zoom() >= 2.0 && !Pins.showsIcon(waypoint);
         if (fullscreen && !pin.clamped() && named) {
            String name = waypoint.shownName();
            int w = font.width(name);
            graphics.pose().pushPose();
            graphics.pose().translate(pin.x() - w / 2.0F, pin.y() + 3.0F * pin.scale(), 0.0F);
            graphics.fill(-2, -1, w + 2, 9, -1879048192);
            graphics.drawString(font, name, 0, 0, -1, false);
            graphics.pose().popPose();
         }
      }

      graphics.disableScissor();
   }

   public static WaypointOverlay.@Nullable Pin under(List<WaypointOverlay.Pin> pins, double mouseX, double mouseY) {
      WaypointOverlay.Pin best = null;
      double bestDistance = Double.MAX_VALUE;

      for (WaypointOverlay.Pin pin : pins) {
         double along = Math.max(0.0, Math.abs(pin.x() - mouseX) - Pins.stretch(pin.waypoint(), (int)pin.size()) * pin.scale() / 2.0);
         double d = Math.hypot(along, pin.headY() - mouseY);
         if (!pin.clamped()) {
            d = Math.min(d, Math.hypot(pin.x() - mouseX, pin.y() - mouseY));
         }

         if (d <= Pins.reach(pin.waypoint(), (int)pin.size()) * pin.scale() && d < bestDistance) {
            best = pin;
            bestDistance = d;
         }
      }

      return best;
   }

   public static Component describe(Waypoint waypoint, Entity viewer) {
      int distance = (int)Math.round(waypoint.distanceTo(viewer.getX(), viewer.getY(), viewer.getZ()));
      int dy = (int)Math.round(waypoint.y - viewer.getY());
      String height = dy > 2 ? " · " + dy + " above" : (dy < -2 ? " · " + -dy + " below" : "");
      return Component.literal(waypoint.shownName() + " · " + distance + " m" + height);
   }

   public record Pin(Waypoint waypoint, float x, float y, float size, float scale, boolean clamped) {
      public float headY() {
         return this.clamped ? this.y : this.y - Pins.headAbove(this.waypoint, (int)this.size) * this.scale;
      }
   }
}
