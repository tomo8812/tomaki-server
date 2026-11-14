package dev.diorama;

import com.mojang.blaze3d.platform.InputConstants;
import dev.diorama.compat.Contraptions;
import dev.diorama.compat.SubLevels;
import dev.diorama.craft.Crafts;
import dev.diorama.waypoint.Waypoint;
import dev.diorama.waypoint.WaypointEditScreen;
import dev.diorama.waypoint.WaypointListScreen;
import dev.diorama.waypoint.Waypoints;
import dev.diorama.waypoint.Waystones;
import dev.diorama.waypoint.XaeroNotice;
import dev.diorama.web.ClientWebMap;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class DioramaClient {
   public static final String CATEGORY = "key.category.diorama.map";
   public static final KeyMapping OPEN_MAP = new KeyMapping("key.diorama.open_map", 77, "key.category.diorama.map");
   public static final KeyMapping NEW_WAYPOINT = new KeyMapping("key.diorama.new_waypoint", 66, "key.category.diorama.map");
   public static final KeyMapping WAYPOINT_LIST = new KeyMapping("key.diorama.waypoints", 85, "key.category.diorama.map");
   public static final KeyMapping ZOOM_IN = new KeyMapping("key.diorama.zoom_in", 61, "key.category.diorama.map");
   public static final KeyMapping ZOOM_OUT = new KeyMapping("key.diorama.zoom_out", 45, "key.category.diorama.map");
   public static final KeyMapping SETTINGS = new KeyMapping("key.diorama.settings", InputConstants.UNKNOWN.getValue(), "key.category.diorama.map");
   public static final KeyMapping TOGGLE_MINIMAP = new KeyMapping("key.diorama.toggle_minimap", InputConstants.UNKNOWN.getValue(), "key.category.diorama.map");
   public static final KeyMapping ENLARGE_MINIMAP = new KeyMapping("key.diorama.enlarge_minimap", InputConstants.UNKNOWN.getValue(), "key.category.diorama.map");
   public static final KeyMapping TOGGLE_DIMENSION = new KeyMapping(
      "key.diorama.toggle_dimension", InputConstants.UNKNOWN.getValue(), "key.category.diorama.map"
   );
   public static final List<KeyMapping> KEY_MAPPINGS = List.of(
      OPEN_MAP, NEW_WAYPOINT, WAYPOINT_LIST, ZOOM_IN, ZOOM_OUT, SETTINGS, TOGGLE_MINIMAP, ENLARGE_MINIMAP, TOGGLE_DIMENSION
   );
   private static boolean enlargeToggled;
   private static boolean enlargeWasDown;

   private DioramaClient() {
   }

   public static boolean minimapEnlarged() {
      return Settings.get().minimapEnlargeToggle ? enlargeToggled : ENLARGE_MINIMAP.isDown();
   }

   private static void changeZoom(int step) {
      Settings s = Settings.get();
      int next = Math.max(0, Math.min(Settings.ZOOMS.length - 1, s.zoomStep() + step));
      if (next != s.zoomStep()) {
         s.zoomStep(next);
         s.save();
      }
   }

   public static void init() {
      Crafts.register(Contraptions::bodies);
      Crafts.register(SubLevels::bodies);
   }

   public static void tick(Minecraft mc) {
      Waystones.start();
      MapSession.tick(mc);
      Waypoints.watchSleep(mc);
      Waypoints.watchDeaths(mc);
      Claims.tick(mc);
      XaeroNotice.tick(mc);
      ClientWebMap.tick(mc);

      while (OPEN_MAP.consumeClick()) {
         if (mc.screen == null && mc.level != null && !ServerRules.sayMapOffHere(mc) && ServerRules.minimapAllowed()) {
            mc.setScreen(new MapScreen());
         }
      }

      while (NEW_WAYPOINT.consumeClick()) {
         Waypoint waypoint = Waypoints.atPlayer();
         if (mc.screen == null && waypoint != null && !ServerRules.sayMapOffHere(mc)) {
            mc.setScreen(new WaypointEditScreen(null, waypoint, true));
         }
      }

      while (ZOOM_IN.consumeClick()) {
         changeZoom(1);
      }

      while (ZOOM_OUT.consumeClick()) {
         changeZoom(-1);
      }

      while (SETTINGS.consumeClick()) {
         if (mc.screen == null) {
            mc.setScreen(new SettingsScreen(null));
         }
      }

      while (TOGGLE_MINIMAP.consumeClick()) {
         Settings s = Settings.get();
         s.minimap = !s.minimap;
         s.save();
      }

      boolean pressed = false;

      while (ENLARGE_MINIMAP.consumeClick()) {
         pressed = true;
      }

      if (pressed && !enlargeWasDown) {
         enlargeToggled = !enlargeToggled;
      }

      enlargeWasDown = ENLARGE_MINIMAP.isDown();
      if (mc.level == null || !Settings.get().minimapEnlargeToggle) {
         enlargeToggled = false;
      }

      while (WAYPOINT_LIST.consumeClick()) {
         if (mc.screen == null && mc.level != null && !ServerRules.sayMapOffHere(mc)) {
            mc.setScreen(new WaypointListScreen(null));
         }
      }
   }
}
