package dev.emi.emi.mixinsupport.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface InvokeTarget {
   String owner() default "this";

   String name() default "";

   String type() default "";

   String desc() default "";
}
