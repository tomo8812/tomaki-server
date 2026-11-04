package fuzs.puzzleslib.api.config.v3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Config {
   String name() default "";

   String[] description() default {};

   String[] category() default {};

   boolean worldRestart() default false;

   boolean gameRestart() default false;

   @Target(ElementType.FIELD)
   @Retention(RetentionPolicy.RUNTIME)
   @interface AllowedValues {
      String[] values();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.FIELD)
   @interface DoubleRange {
      double min() default Double.MIN_VALUE;

      double max() default Double.MAX_VALUE;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.FIELD)
   @interface IntRange {
      int min() default Integer.MIN_VALUE;

      int max() default Integer.MAX_VALUE;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.FIELD)
   @interface LongRange {
      long min() default Long.MIN_VALUE;

      long max() default Long.MAX_VALUE;
   }
}
