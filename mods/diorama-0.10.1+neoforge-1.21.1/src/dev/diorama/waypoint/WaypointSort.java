package dev.diorama.waypoint;

import java.util.Comparator;
import java.util.Locale;
import java.util.function.ToDoubleFunction;
import org.jspecify.annotations.Nullable;

public enum WaypointSort {
   DISTANCE("distance"),
   NAME("name"),
   INITIALS("initials"),
   COLOUR("colour");

   private static final float HUE_START = 0.041666668F;
   private final String id;

   WaypointSort(String id) {
      this.id = id;
   }

   public String key() {
      return "diorama.waypoints.sort." + this.id;
   }

   public String directionKey(boolean reversed) {
      return this.key() + (reversed ? ".reversed" : ".usual");
   }

   public WaypointSort next() {
      return values()[(this.ordinal() + 1) % values().length];
   }

   public static WaypointSort named(@Nullable String name) {
      for (WaypointSort sort : values()) {
         if (sort.name().equals(name)) {
            return sort;
         }
      }

      return DISTANCE;
   }

   public Comparator<Waypoint> comparator(boolean reversed, ToDoubleFunction<Waypoint> distance) {
      Comparator<Waypoint> byName = Comparator.comparing(WaypointSort::nameKey);

      Comparator<Waypoint> order = switch (this) {
         case DISTANCE -> {
            Comparator<Waypoint> nearest = Comparator.comparingDouble(w -> {
               double d = distance.applyAsDouble(w);
               return Double.isNaN(d) ? 0.0 : d;
            });
            yield Comparator.<Waypoint, Boolean>comparing(w -> Double.isNaN(distance.applyAsDouble(w))).thenComparing(reversed ? nearest.reversed() : nearest);
         }
         case NAME -> reversed ? byName.reversed() : byName;
         case INITIALS -> {
            Comparator<Waypoint> initials = Comparator.<Waypoint, Boolean>comparing(w -> !w.icon.isEmpty())
               .thenComparing(w -> w.icon.isEmpty() ? w.label().toLowerCase(Locale.ROOT) : w.icon);
            yield reversed ? initials.reversed() : initials;
         }
         case COLOUR -> {
            Comparator<Waypoint> colour = Comparator.comparingDouble(w -> colourKey(w.color));
            yield reversed ? colour.reversed() : colour;
         }
      };
      return Comparator.<Waypoint, Boolean>comparing(w -> !w.visible).thenComparing(order).thenComparing(byName);
   }

   private static String nameKey(Waypoint waypoint) {
      return waypoint.shownName().toLowerCase(Locale.ROOT);
   }

   static double colourKey(int argb) {
      float[] hsv = WaypointColours.hsv(argb);
      if (!(hsv[1] < 0.15F) && !(hsv[2] < 0.12F)) {
         float hue = hsv[0] + 0.041666668F;
         return hue - Math.floor(hue);
      } else {
         return 2.0F - hsv[2];
      }
   }

   // $VF: synthetic method
   private static WaypointSort[] $values() {
      return new WaypointSort[]{DISTANCE, NAME, INITIALS, COLOUR};
   }
}
