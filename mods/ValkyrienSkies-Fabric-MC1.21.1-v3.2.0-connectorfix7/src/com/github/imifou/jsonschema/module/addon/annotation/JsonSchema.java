package com.github.imifou.jsonschema.module.addon.annotation;

import com.github.imifou.jsonschema.module.addon.TypeFormat;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Deprecated
public @interface JsonSchema {
   String title() default "";

   String description() default "";

   TypeFormat format() default TypeFormat.NONE;

   String defaultValue() default "";

   double multipleOf() default 0.0;

   double min() default Double.MIN_VALUE;

   boolean exclusiveMin() default false;

   double max() default Double.MAX_VALUE;

   boolean exclusiveMax() default false;

   int minLength() default 0;

   int maxLength() default Integer.MAX_VALUE;

   String pattern() default "";

   boolean required() default false;

   boolean ignore() default false;

   JSData[] metadata() default {};
}
