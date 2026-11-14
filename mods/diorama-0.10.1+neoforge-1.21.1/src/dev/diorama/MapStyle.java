package dev.diorama;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public record MapStyle(
   String name,
   double pitchDegrees,
   float saturation,
   float exposure,
   float[] sunTint,
   float[] shadowTint,
   float outline,
   float blockVariation,
   boolean roundedFog,
   boolean clearWater,
   float skyOcclusion,
   double sunElevation,
   float ambient,
   float sunStrength,
   float heightShade,
   float cornerOcclusion
) {
   public static final double MIN_SUN_ELEVATION = 25.0;
   public static final double MAX_SUN_ELEVATION = 60.0;
   public static final MapStyle BASELINE = new MapStyle(
      "Baseline",
      60.0,
      1.18F,
      1.06F,
      new float[]{1.06F, 1.0F, 0.9F},
      new float[]{0.9F, 0.95F, 1.08F},
      0.0F,
      0.0F,
      false,
      false,
      0.0F,
      51.3,
      0.5F,
      0.484F,
      0.0F,
      0.0F
   );
   public static final MapStyle FLAT = BASELINE.withName("Flat").withOutline(0.38F).withRoundedFog(true);
   public static final MapStyle DEFAULT = FLAT.withName("Default")
      .withPitch(50.0)
      .withGrade(1.35F, 1.16F, new float[]{1.14F, 1.0F, 0.8F}, new float[]{0.74F, 0.86F, 1.18F})
      .withSkyOcclusion(0.85F)
      .withSunElevation(38.0)
      .withLight(0.34F, 0.62F)
      .withHeightShade(0.22F)
      .withCornerOcclusion(1.5F)
      .withClearWater(true);
   public static final List<MapStyle> VARIANTS;
   private static volatile MapStyle current = DEFAULT;

   public static List<MapStyle> tour() {
      return Stream.concat(VARIANTS.stream(), Stream.of(DEFAULT)).toList();
   }

   public static MapStyle get() {
      return current;
   }

   public static void set(MapStyle style) {
      current = style;
      Projection.setPitch(style.pitchDegrees);
   }

   public boolean looksLike(MapStyle other) {
      return this.pitchDegrees == other.pitchDegrees
         && this.saturation == other.saturation
         && this.exposure == other.exposure
         && Arrays.equals(this.sunTint, other.sunTint)
         && Arrays.equals(this.shadowTint, other.shadowTint)
         && this.outline == other.outline
         && this.blockVariation == other.blockVariation
         && this.clearWater == other.clearWater
         && this.skyOcclusion == other.skyOcclusion
         && this.sunElevation == other.sunElevation
         && this.ambient == other.ambient
         && this.sunStrength == other.sunStrength
         && this.heightShade == other.heightShade
         && this.cornerOcclusion == other.cornerOcclusion;
   }

   public MapStyle withName(String name) {
      return new MapStyle(
         name,
         this.pitchDegrees,
         this.saturation,
         this.exposure,
         this.sunTint,
         this.shadowTint,
         this.outline,
         this.blockVariation,
         this.roundedFog,
         this.clearWater,
         this.skyOcclusion,
         this.sunElevation,
         this.ambient,
         this.sunStrength,
         this.heightShade,
         this.cornerOcclusion
      );
   }

   public MapStyle withPitch(double degrees) {
      return new MapStyle(
         this.name,
         degrees,
         this.saturation,
         this.exposure,
         this.sunTint,
         this.shadowTint,
         this.outline,
         this.blockVariation,
         this.roundedFog,
         this.clearWater,
         this.skyOcclusion,
         this.sunElevation,
         this.ambient,
         this.sunStrength,
         this.heightShade,
         this.cornerOcclusion
      );
   }

   public MapStyle withGrade(float saturation, float exposure, float[] sun, float[] shadow) {
      return new MapStyle(
         this.name,
         this.pitchDegrees,
         saturation,
         exposure,
         sun,
         shadow,
         this.outline,
         this.blockVariation,
         this.roundedFog,
         this.clearWater,
         this.skyOcclusion,
         this.sunElevation,
         this.ambient,
         this.sunStrength,
         this.heightShade,
         this.cornerOcclusion
      );
   }

   public MapStyle withOutline(float outline) {
      return new MapStyle(
         this.name,
         this.pitchDegrees,
         this.saturation,
         this.exposure,
         this.sunTint,
         this.shadowTint,
         outline,
         this.blockVariation,
         this.roundedFog,
         this.clearWater,
         this.skyOcclusion,
         this.sunElevation,
         this.ambient,
         this.sunStrength,
         this.heightShade,
         this.cornerOcclusion
      );
   }

   public MapStyle withBlockVariation(float variation) {
      return new MapStyle(
         this.name,
         this.pitchDegrees,
         this.saturation,
         this.exposure,
         this.sunTint,
         this.shadowTint,
         this.outline,
         variation,
         this.roundedFog,
         this.clearWater,
         this.skyOcclusion,
         this.sunElevation,
         this.ambient,
         this.sunStrength,
         this.heightShade,
         this.cornerOcclusion
      );
   }

   public MapStyle withClearWater(boolean clear) {
      return new MapStyle(
         this.name,
         this.pitchDegrees,
         this.saturation,
         this.exposure,
         this.sunTint,
         this.shadowTint,
         this.outline,
         this.blockVariation,
         this.roundedFog,
         clear,
         this.skyOcclusion,
         this.sunElevation,
         this.ambient,
         this.sunStrength,
         this.heightShade,
         this.cornerOcclusion
      );
   }

   public MapStyle withRoundedFog(boolean rounded) {
      return new MapStyle(
         this.name,
         this.pitchDegrees,
         this.saturation,
         this.exposure,
         this.sunTint,
         this.shadowTint,
         this.outline,
         this.blockVariation,
         rounded,
         this.clearWater,
         this.skyOcclusion,
         this.sunElevation,
         this.ambient,
         this.sunStrength,
         this.heightShade,
         this.cornerOcclusion
      );
   }

   public MapStyle withSkyOcclusion(float amount) {
      return new MapStyle(
         this.name,
         this.pitchDegrees,
         this.saturation,
         this.exposure,
         this.sunTint,
         this.shadowTint,
         this.outline,
         this.blockVariation,
         this.roundedFog,
         this.clearWater,
         amount,
         this.sunElevation,
         this.ambient,
         this.sunStrength,
         this.heightShade,
         this.cornerOcclusion
      );
   }

   public MapStyle withLight(float ambient, float sunStrength) {
      return new MapStyle(
         this.name,
         this.pitchDegrees,
         this.saturation,
         this.exposure,
         this.sunTint,
         this.shadowTint,
         this.outline,
         this.blockVariation,
         this.roundedFog,
         this.clearWater,
         this.skyOcclusion,
         this.sunElevation,
         ambient,
         sunStrength,
         this.heightShade,
         this.cornerOcclusion
      );
   }

   public MapStyle withHeightShade(float amount) {
      return new MapStyle(
         this.name,
         this.pitchDegrees,
         this.saturation,
         this.exposure,
         this.sunTint,
         this.shadowTint,
         this.outline,
         this.blockVariation,
         this.roundedFog,
         this.clearWater,
         this.skyOcclusion,
         this.sunElevation,
         this.ambient,
         this.sunStrength,
         amount,
         this.cornerOcclusion
      );
   }

   public MapStyle withCornerOcclusion(float amount) {
      return new MapStyle(
         this.name,
         this.pitchDegrees,
         this.saturation,
         this.exposure,
         this.sunTint,
         this.shadowTint,
         this.outline,
         this.blockVariation,
         this.roundedFog,
         this.clearWater,
         this.skyOcclusion,
         this.sunElevation,
         this.ambient,
         this.sunStrength,
         this.heightShade,
         amount
      );
   }

   public MapStyle withSunElevation(double degrees) {
      double clamped = Math.max(25.0, Math.min(60.0, degrees));
      return new MapStyle(
         this.name,
         this.pitchDegrees,
         this.saturation,
         this.exposure,
         this.sunTint,
         this.shadowTint,
         this.outline,
         this.blockVariation,
         this.roundedFog,
         this.clearWater,
         this.skyOcclusion,
         clamped,
         this.ambient,
         this.sunStrength,
         this.heightShade,
         this.cornerOcclusion
      );
   }

   static {
      VARIANTS = List.of(
         FLAT,
         DEFAULT.withName("No sky occlusion").withSkyOcclusion(0.0F),
         DEFAULT.withName("High sun").withSunElevation(BASELINE.sunElevation),
         DEFAULT.withName("Soft light").withLight(BASELINE.ambient, BASELINE.sunStrength),
         DEFAULT.withName("No height shade").withHeightShade(0.0F),
         DEFAULT.withName("Corners in shade only").withCornerOcclusion(0.0F),
         DEFAULT.withName("Steeper camera").withPitch(BASELINE.pitchDegrees),
         DEFAULT.withName("Neutral colour").withGrade(BASELINE.saturation, BASELINE.exposure, BASELINE.sunTint, BASELINE.shadowTint)
      );
   }
}
