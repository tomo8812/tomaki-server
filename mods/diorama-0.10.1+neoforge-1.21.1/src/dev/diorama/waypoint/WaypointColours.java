package dev.diorama.waypoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class WaypointColours {
   public static final int COLUMNS = 16;
   private static final String[] CLASSIC_NAMES = new String[]{
      "red", "orange", "amber", "green", "teal", "sky", "indigo", "violet", "pink", "white", "silver", "slate", "brown", "lime", "ice", "blossom"
   };
   private static final int[] DYES = new int[]{
      16383998, 10329495, 4673362, 1908001, 8606770, 11546150, 16351261, 16701501, 8439583, 6192150, 1481884, 3847130, 3949738, 8991416, 13061821, 15961002
   };
   private static final String[] DYE_NAMES = new String[]{
      "white", "light_gray", "gray", "black", "brown", "red", "orange", "yellow", "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink"
   };
   public static final List<WaypointColours.Swatch> ALL;

   private WaypointColours() {
   }

   public static int rows() {
      return (ALL.size() + 16 - 1) / 16;
   }

   public static WaypointColours.@Nullable Swatch named(int argb) {
      for (WaypointColours.Swatch swatch : ALL) {
         if ((swatch.argb() & 16777215) == (argb & 16777215)) {
            return swatch;
         }
      }

      return null;
   }

   public static int random(int avoid) {
      ThreadLocalRandom rng = ThreadLocalRandom.current();
      float avoidHue = hsv(avoid)[0];

      float hue;
      do {
         hue = rng.nextFloat();
      } while (hueDistance(hue, avoidHue) < 0.16666667F);

      return rgb(hue, 0.6F + rng.nextFloat() * 0.35F, 0.8F + rng.nextFloat() * 0.2F);
   }

   private static float hueDistance(float a, float b) {
      float d = Math.abs(a - b) % 1.0F;
      return Math.min(d, 1.0F - d);
   }

   public static int rgb(float hue, float saturation, float value) {
      float h = (hue - (float)Math.floor(hue)) * 6.0F;
      int sector = (int)h % 6;
      float f = h - (int)h;
      float p = value * (1.0F - saturation);
      float q = value * (1.0F - saturation * f);
      float t = value * (1.0F - saturation * (1.0F - f));

      float[] c = switch (sector) {
         case 0 -> new float[]{value, t, p};
         case 1 -> new float[]{q, value, p};
         case 2 -> new float[]{p, value, t};
         case 3 -> new float[]{p, q, value};
         case 4 -> new float[]{t, p, value};
         default -> new float[]{value, p, q};
      };
      return 0xFF000000 | Math.round(c[0] * 255.0F) << 16 | Math.round(c[1] * 255.0F) << 8 | Math.round(c[2] * 255.0F);
   }

   public static float[] hsv(int argb) {
      float r = (argb >> 16 & 0xFF) / 255.0F;
      float g = (argb >> 8 & 0xFF) / 255.0F;
      float b = (argb & 0xFF) / 255.0F;
      float max = Math.max(r, Math.max(g, b));
      float min = Math.min(r, Math.min(g, b));
      float d = max - min;
      float hue = 0.0F;
      if (d > 0.0F) {
         if (max == r) {
            hue = (g - b) / d / 6.0F;
         } else if (max == g) {
            hue = ((b - r) / d + 2.0F) / 6.0F;
         } else {
            hue = ((r - g) / d + 4.0F) / 6.0F;
         }
      }

      return new float[]{hue - (float)Math.floor(hue), max == 0.0F ? 0.0F : d / max, max};
   }

   public static int shade(float hue, float t) {
      return t <= 0.5F ? rgb(hue, t * 2.0F, 1.0F) : rgb(hue, 1.0F, 1.0F - (t - 0.5F) * 2.0F * 0.9F);
   }

   public static float shadeOf(int argb) {
      float[] hsv = hsv(argb);
      return hsv[2] >= 0.99F ? hsv[1] / 2.0F : 0.5F + Math.min(1.0F, (1.0F - hsv[2]) / 0.9F) / 2.0F;
   }

   public static String hex(int argb) {
      return String.format(Locale.ROOT, "#%06X", argb & 16777215);
   }

   public static @Nullable Integer parse(String text) {
      String t = text.trim();
      if (t.startsWith("#")) {
         t = t.substring(1);
      }

      if (t.length() == 3) {
         t = "" + t.charAt(0) + t.charAt(0) + t.charAt(1) + t.charAt(1) + t.charAt(2) + t.charAt(2);
      }

      if (t.length() != 6) {
         return null;
      }

      try {
         return 0xFF000000 | Integer.parseInt(t, 16);
      } catch (NumberFormatException e) {
         return null;
      }
   }

   static {
      List<WaypointColours.Swatch> all = new ArrayList<>();

      for (int i = 0; i < Waypoint.PALETTE.length; i++) {
         all.add(new WaypointColours.Swatch(Waypoint.PALETTE[i], Component.translatable("diorama.colour.classic." + CLASSIC_NAMES[i])));
      }

      for (int i = 0; i < DYES.length; i++) {
         all.add(new WaypointColours.Swatch(0xFF000000 | DYES[i], Component.translatable("color.minecraft." + DYE_NAMES[i])));
      }

      ALL = Collections.unmodifiableList(all);
   }

   public record Swatch(int argb, Component name) {
   }
}
