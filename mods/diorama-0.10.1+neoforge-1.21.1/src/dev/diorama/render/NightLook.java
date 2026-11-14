package dev.diorama.render;

import dev.diorama.MapSession;
import dev.diorama.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

public final class NightLook {
   private static final float[] MOON = new float[]{0.27F, 0.33F, 0.49F};
   private static final float[] WARM = new float[]{1.15F, 0.82F, 0.5F};
   private static final float GLOW = 1.35F;
   private static final float GLOW_CURVE = 0.9F;
   private static final int FULL_NIGHT = 11;
   private static final float EASE_SECONDS = 1.5F;
   private static float shown = -1.0F;
   private static long shownAt;
   private static ClientLevel shownFor;

   private NightLook() {
   }

   public static float forView(MapLevel map, int layer) {
      ClientLevel world = Minecraft.getInstance().level;
      MapSession session = MapSession.get();
      return layer == Integer.MIN_VALUE && !map.roofless() && world != null && session != null && session.map() == map ? amount(world) : 0.0F;
   }

   public static float amount(ClientLevel level) {
      Settings.DayNight setting = Settings.get().dayNight;
      if (setting == Settings.DayNight.DAY || level.dimensionType().hasFixedTime()) {
         return 0.0F;
      }

      if (setting == Settings.DayNight.NIGHT) {
         return 1.0F;
      }

      level.updateSkyBrightness();
      float target = Mth.clamp(level.getSkyDarken() / 11.0F, 0.0F, 1.0F);
      long now = System.nanoTime();
      if (shownFor == level && !(shown < 0.0F)) {
         float step = Math.min(1.0F, (float)(now - shownAt) / 1.0E9F / 1.5F);
         shown = shown + (target - shown) * step;
      } else {
         shown = target;
      }

      shownFor = level;
      shownAt = now;
      return shown;
   }

   public static int dim(float night, int alpha) {
      int r = Math.round(Mth.lerp(night, 1.0F, MOON[0]) * 255.0F);
      int g = Math.round(Mth.lerp(night, 1.0F, MOON[1]) * 255.0F);
      int b = Math.round(Mth.lerp(night, 1.0F, MOON[2]) * 255.0F);
      return alpha << 24 | r << 16 | g << 8 | b;
   }

   public static int light(int abgr, float light, float night) {
      float glow = (float)Math.pow(light, 0.9F) * 1.35F * night;
      int r = channel((abgr & 0xFF) * (Mth.lerp(night, 1.0F, MOON[0]) + WARM[0] * glow));
      int g = channel((abgr >> 8 & 0xFF) * (Mth.lerp(night, 1.0F, MOON[1]) + WARM[1] * glow));
      int b = channel((abgr >> 16 & 0xFF) * (Mth.lerp(night, 1.0F, MOON[2]) + WARM[2] * glow));
      return abgr & 0xFF000000 | b << 16 | g << 8 | r;
   }

   public static float[] darken(float[] rgb, float night) {
      return new float[]{rgb[0] * Mth.lerp(night, 1.0F, MOON[0]), rgb[1] * Mth.lerp(night, 1.0F, MOON[1]), rgb[2] * Mth.lerp(night, 1.0F, MOON[2])};
   }

   private static int channel(float value) {
      return Math.min(255, Math.round(value));
   }

   public static int darken(int argb, float night) {
      int r = Math.round((argb >> 16 & 0xFF) * Mth.lerp(night, 1.0F, MOON[0]));
      int g = Math.round((argb >> 8 & 0xFF) * Mth.lerp(night, 1.0F, MOON[1]));
      int b = Math.round((argb & 0xFF) * Mth.lerp(night, 1.0F, MOON[2]));
      return argb & 0xFF000000 | r << 16 | g << 8 | b;
   }

   public static int params(float night, boolean crisp, int alpha) {
      return alpha << 24 | Math.round(night * 255.0F) << 16 | (crisp ? 255 : 0) << 8;
   }

   public static int tintOf(int params) {
      return dim((params >> 16 & 0xFF) / 255.0F, params >>> 24);
   }
}
