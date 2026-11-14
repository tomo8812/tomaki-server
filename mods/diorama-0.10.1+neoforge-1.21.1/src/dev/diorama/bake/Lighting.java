package dev.diorama.bake;

import dev.diorama.MapStyle;

final class Lighting {
   private static final float CAVE_KNEE = 0.5F;
   static final float[] CAVE_FOG = new float[]{0.008F, 0.01F, 0.015F};
   static final float[] CAVE_CUT = new float[]{0.022F, 0.023F, 0.027F};
   static final float[] CAVE_CUT_DARK = new float[]{0.0065F, 0.0068F, 0.0085F};
   private static final float CAVE_CEILING = 0.8F;
   private final MapStyle style;
   final double sunX;
   final double sunY;
   final double sunZ;

   Lighting(MapStyle style) {
      this.style = style;
      double[] sun = CraftShadow.sun(style);
      this.sunX = sun[0];
      this.sunY = sun[1];
      this.sunZ = sun[2];
   }

   void surface(double lambert, boolean shadowed, float ao, float sky, float emission, float facing, double y, float[] out) {
      MapStyle style = this.style;
      float aoSun = Math.max(0.0F, 1.0F - style.cornerOcclusion() * (1.0F - ao));
      float aoSky = Math.min(ao, aoSun);
      float direct = lambert > 0.0 && !shadowed ? (float)(style.sunStrength() * lambert) * aoSun : 0.0F;
      float ambient = style.ambient() * aoSky;
      float occlusion = style.skyOcclusion();
      if (occlusion > 0.0F) {
         ambient *= 1.0F - occlusion * (1.0F - (0.15F + 0.85F * sky));
      }

      float[] skyTint = style.shadowTint();
      float[] sunTint = style.sunTint();
      float r = ambient * skyTint[0] + direct * sunTint[0];
      float g = ambient * skyTint[1] + direct * sunTint[1];
      float b = ambient * skyTint[2] + direct * sunTint[2];
      float heightShade = style.heightShade();
      if (heightShade != 0.0F) {
         float above = (float)Math.max(-1.0, Math.min(1.0, (y - 64.0) / 64.0));
         float f = 1.0F + heightShade * above;
         r *= f;
         g *= f;
         b *= f;
      }

      boolean wantSun = out.length > 3;
      float lit = wantSun ? luminance(r, g, b) : 0.0F;
      float sun = wantSun
         ? luminance(direct * sunTint[0], direct * sunTint[1], direct * sunTint[2])
            / Math.max(
               1.0E-6F,
               luminance(ambient * skyTint[0] + direct * sunTint[0], ambient * skyTint[1] + direct * sunTint[1], ambient * skyTint[2] + direct * sunTint[2])
            )
         : 0.0F;
      if (emission > 0.0F) {
         float glow = 0.35F + emission * 0.85F;
         r = Math.max(r, glow);
         g = Math.max(g, glow);
         b = Math.max(b, glow);
      }

      if (wantSun) {
         out[3] = emission > 0.0F ? sun * Math.min(1.0F, lit / Math.max(1.0E-6F, luminance(r, g, b))) : sun;
      }

      out[0] = r * facing;
      out[1] = g * facing;
      out[2] = b * facing;
   }

   static float caveWater(float blockLight, int skyLight) {
      return 0.42F + (float)Math.pow(Math.max(blockLight, skyLight * 0.8F) / 15.0F, 1.5) * 0.8F;
   }

   static void cave(float blockLight, int skyLight, float ao, float fade, float emission, float facing, float[] out) {
      float bl = blockLight / 15.0F;
      float sl = skyLight / 15.0F * 0.8F;
      float warm = (float)Math.pow(bl, 1.5);
      float cool = (float)Math.pow(sl, 1.5);
      float base = 0.42F;
      float r = (base * 0.92F + warm * 0.9F + cool * 0.6F) * ao * fade;
      float g = (base * 0.96F + warm * 0.75F + cool * 0.65F) * ao * fade;
      float b = (base * 1.08F + warm * 0.5F + cool * 0.75F) * ao * fade;
      float peak = Math.max(r, Math.max(g, b));
      if (peak > 0.5F) {
         float eased = 0.5F + 0.3F * (float)(1.0 - Math.exp(-(peak - 0.5F) / 0.3F));
         r *= eased / peak;
         g *= eased / peak;
         b *= eased / peak;
      }

      if (emission > 0.0F) {
         float glow = 0.35F + emission * 0.85F;
         r = Math.max(r, glow);
         g = Math.max(g, glow);
         b = Math.max(b, glow);
      }

      out[0] = r * facing;
      out[1] = g * facing;
      out[2] = b * facing;
   }

   static float luminance(float r, float g, float b) {
      return 0.2126F * r + 0.7152F * g + 0.0722F * b;
   }
}
