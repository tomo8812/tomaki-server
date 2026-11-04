package net.satisfy.bakery.core.recipe;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;
import net.satisfy.bakery.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

public enum CakeAnimation implements StringRepresentable {
   NONE("none", 0),
   SHRINK("shrink", 12),
   SPREAD("spread", 16),
   SPLIT("split", 10),
   FLATTEN("flatten", 25),
   KNEAD("knead", 40),
   ROLL_UP("roll_up", 6),
   SLICE("slice", 10);

   public static final int KNEAD_BEAT = 4;
   public static final int PRESS_DURATION = 6;
   public static final int ROLL_UP_PRESSES = 5;
   public static final EnumCodec<CakeAnimation> CODEC = StringRepresentable.fromEnum(CakeAnimation::values);
   public static final StreamCodec<ByteBuf, CakeAnimation> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(index -> values()[index], Enum::ordinal);
   private final String name;
   private final int duration;

   CakeAnimation(String name, int duration) {
      this.name = name;
      this.duration = duration;
   }

   public int duration() {
      return this.duration;
   }

   public int configuredDuration() {
      if (!PlatformHelper.animationsEnabled() && !this.isPressing()) {
         return 1;
      }

      return switch (this) {
         case SPREAD -> PlatformHelper.getJamDuration();
         case SPLIT, SLICE -> PlatformHelper.getKnifeDuration();
         case FLATTEN, KNEAD, ROLL_UP -> 6;
         default -> this.duration;
      };
   }

   public boolean isPressing() {
      return this == KNEAD || this == FLATTEN || this == ROLL_UP;
   }

   public int configuredPresses() {
      return switch (this) {
         case KNEAD -> PlatformHelper.getKneadPresses();
         case ROLL_UP -> 5;
         default -> PlatformHelper.getRollingPinPresses();
      };
   }

   public float verticalScale(float progress, int duration) {
      return switch (this) {
         case SHRINK -> {
            float bounce = Mth.sin(Math.min(progress / 0.3F, 1.0F) * (float) Math.PI) * 0.12F;
            yield Math.max(0.0F, shrinkScale(progress) * (1.0F - bounce));
         }
         default -> 1.0F;
      };
   }

   public static float shrinkScale(float progress) {
      return progress < 0.3F ? 1.0F : 1.0F - easeInBack((progress - 0.3F) / 0.7F);
   }

   public static float easeInBack(float t) {
      float c1 = 1.70158F;
      return (c1 + 1.0F) * t * t * t - c1 * t * t;
   }

   public static float easeOutCubic(float t) {
      float inverse = 1.0F - t;
      return 1.0F - inverse * inverse * inverse;
   }

   public static float easeOutBack(float t) {
      float c1 = 1.70158F;
      float shifted = t - 1.0F;
      return 1.0F + (c1 + 1.0F) * shifted * shifted * shifted + c1 * shifted * shifted;
   }

   public static CakeAnimation byName(String name) {
      CakeAnimation animation = (CakeAnimation)CODEC.byName(name);
      return animation == null ? NONE : animation;
   }

   @NotNull
   public String getSerializedName() {
      return this.name;
   }

   // $VF: synthetic method
   private static CakeAnimation[] $values() {
      return new CakeAnimation[]{NONE, SHRINK, SPREAD, SPLIT, FLATTEN, KNEAD, ROLL_UP, SLICE};
   }
}
