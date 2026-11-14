package dev.diorama.waypoint;

import dev.diorama.MapSession;
import dev.diorama.ServerRules;
import dev.diorama.Settings;
import dev.diorama.compat.HudElement;
import dev.diorama.render.Pins;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class WaypointHud implements HudElement {
   private static final float NAME_RADIUS = 40.0F;
   private static final double NAME_DISTANCE = 48.0;
   private final WaypointBeams beams = new WaypointBeams();

   @Override
   public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
      Minecraft mc = Minecraft.getInstance();
      Entity viewer = MapSession.viewer();
      WaypointStore store = WaypointStore.get();
      if (viewer != null && mc.level != null && store != null && Settings.get().waypointsInWorld && !mc.options.hideGui && ServerRules.minimapAllowed()) {
         Vec3 eye = WorldProjection.camera();
         if (eye != null) {
            float width = graphics.guiWidth();
            float height = graphics.guiHeight();
            Font font = mc.font;
            String dimension = mc.level.dimension().location().toString();
            List<Waypoint> waypoints = store.shownIn(dimension, store.in(dimension));
            float scale = Pins.scale(Settings.get().waypointWorldSize);
            float textScale = Pins.textScale(Settings.get().waypointWorldSize);
            float margin = 28.0F * Math.max(1.0F, scale);
            Settings.Beams beamsShown = Settings.get().waypointBeams;
            if (beamsShown != Settings.Beams.OFF) {
               float z = 0.0F;
               z = graphics.pose().last().pose().m32();
               this.beams.begin(mc.level, eye, width, height, z);

               try {
                  for (Waypoint waypoint : waypoints) {
                     if (WaypointBeams.has(waypoint, beamsShown) && WaypointStore.isShown(waypoint) && WaypointStore.isNear(waypoint, viewer)) {
                        this.beams.add(waypoint);
                     }
                  }

                  this.beams.draw(graphics);
               } finally {
                  this.beams.end();
               }
            }

            for (Waypoint waypoint : waypoints) {
               if (WaypointStore.isShown(waypoint) && WaypointStore.isNear(waypoint, viewer)) {
                  double dx = waypoint.x + 0.5 - eye.x;
                  double dy = waypoint.y + 1.0 - eye.y;
                  double dz = waypoint.z + 0.5 - eye.z;
                  float[] screen = WorldProjection.project(waypoint.x + 0.5, waypoint.y + 1.0, waypoint.z + 0.5, width, height);
                  if (screen != null) {
                     float sx = screen[0];
                     float sy = screen[1];
                     double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                     Settings.WaypointLabels labels = Settings.get().waypointLabels;
                     String text = null;
                     if (labels != Settings.WaypointLabels.NONE) {
                        text = Math.round(distance) + " m";
                        boolean nearCrosshair = Math.hypot(sx - width / 2.0F, sy - height / 2.0F) < 40.0;
                        if (labels == Settings.WaypointLabels.ALWAYS || labels == Settings.WaypointLabels.AUTO && (nearCrosshair || distance < 48.0)) {
                           text = waypoint.shownName() + " · " + text;
                        }
                     }

                     int w = text == null ? 0 : font.width(text);
                     float side = Math.max(margin, (w / 2.0F + 2.0F) * textScale);
                     float below = Math.max(margin, 3.0F * scale + 10.0F * textScale);
                     if (!(sx < -side) && !(sy < -below) && !(sx > width + side) && !(sy > height + margin)) {
                        graphics.pose().pushPose();
                        graphics.pose().translate(sx, sy, 0.0F);

                        try {
                           Pins.standing(graphics, waypoint, 0.0F, 0.0F, 15, scale);
                           if (text != null) {
                              graphics.pose().translate(-w * textScale / 2.0F, textScale + 3.0F * scale, 0.0F);
                              graphics.pose().scale(textScale, textScale, 1.0F);
                              graphics.fill(-2, -1, w + 2, 9, Integer.MIN_VALUE);
                              graphics.drawString(font, text, 0, 0, -1, false);
                           }
                        } finally {
                           graphics.pose().popPose();
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
