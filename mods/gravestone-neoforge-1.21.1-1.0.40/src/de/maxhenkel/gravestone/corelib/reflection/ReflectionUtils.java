package de.maxhenkel.gravestone.corelib.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReflectionUtils {
   public static boolean hasAnnotation(Field field, Class<? extends Annotation> annotationClass) {
      for (Annotation annotation : field.getAnnotations()) {
         if (annotation.annotationType().equals(annotationClass)) {
            return true;
         }
      }

      return false;
   }
}
