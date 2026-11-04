package fuzs.puzzleslib.impl.config.annotation;

import fuzs.puzzleslib.api.config.v3.Config;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.ModConfigSpec.LongValue;
import org.jetbrains.annotations.Nullable;

public abstract class NumberEntry<T extends Number, A extends Annotation> extends ValueEntry<T> {
   private final Class<A> rangeClazz;

   public NumberEntry(Field field, Class<A> rangeClazz) {
      super(field);
      this.rangeClazz = rangeClazz;
   }

   public Optional<A> getRangeAnnotation() {
      return Optional.ofNullable(this.field.getDeclaredAnnotation(this.rangeClazz));
   }

   public abstract T min();

   public abstract T max();

   public static final class DoubleEntry extends NumberEntry<Double, Config.DoubleRange> {
      public DoubleEntry(Field field) {
         super(field, Config.DoubleRange.class);
      }

      public DoubleValue getConfigValue(Builder builder, @Nullable Object o) {
         return builder.defineInRange(this.getName(), (Double)this.getDefaultValue(o), this.min(), this.max());
      }

      public Double min() {
         return this.getRangeAnnotation().map(Config.DoubleRange::min).orElse(Double.MIN_VALUE);
      }

      public Double max() {
         return this.getRangeAnnotation().map(Config.DoubleRange::max).orElse(Double.MAX_VALUE);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Number max() {
         return this.max();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Number min() {
         return this.min();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ConfigValue getConfigValue(Builder param1, @Nullable Object nullx) {
         return this.getConfigValue(var1, nullx);
      }
   }

   public static final class IntegerEntry extends NumberEntry<Integer, Config.IntRange> {
      public IntegerEntry(Field field) {
         super(field, Config.IntRange.class);
      }

      public IntValue getConfigValue(Builder builder, @Nullable Object o) {
         return builder.defineInRange(this.getName(), (Integer)this.getDefaultValue(o), this.min(), this.max());
      }

      public Integer min() {
         return this.getRangeAnnotation().map(Config.IntRange::min).orElse(Integer.MIN_VALUE);
      }

      public Integer max() {
         return this.getRangeAnnotation().map(Config.IntRange::max).orElse(Integer.MAX_VALUE);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Number max() {
         return this.max();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Number min() {
         return this.min();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ConfigValue getConfigValue(Builder param1, @Nullable Object nullx) {
         return this.getConfigValue(var1, nullx);
      }
   }

   public static final class LongEntry extends NumberEntry<Long, Config.LongRange> {
      public LongEntry(Field field) {
         super(field, Config.LongRange.class);
      }

      public LongValue getConfigValue(Builder builder, @Nullable Object o) {
         return builder.defineInRange(this.getName(), (Long)this.getDefaultValue(o), this.min(), this.max());
      }

      public Long min() {
         return this.getRangeAnnotation().map(Config.LongRange::min).orElse(Long.MIN_VALUE);
      }

      public Long max() {
         return this.getRangeAnnotation().map(Config.LongRange::max).orElse(Long.MAX_VALUE);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Number max() {
         return this.max();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Number min() {
         return this.min();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ConfigValue getConfigValue(Builder param1, @Nullable Object nullx) {
         return this.getConfigValue(var1, nullx);
      }
   }
}
