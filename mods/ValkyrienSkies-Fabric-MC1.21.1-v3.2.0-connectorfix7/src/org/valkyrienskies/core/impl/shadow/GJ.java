package org.valkyrienskies.core.impl.shadow;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.AnnotationTarget;
import kotlin.annotation.Retention;
import kotlin.annotation.Target;

@Target(
   allowedTargets = {
         AnnotationTarget.ANNOTATION_CLASS,
         AnnotationTarget.FUNCTION,
         AnnotationTarget.PROPERTY_GETTER,
         AnnotationTarget.PROPERTY_SETTER,
         AnnotationTarget.CONSTRUCTOR,
         AnnotationTarget.FIELD
   }
)
@Retention(AnnotationRetention.RUNTIME)
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE})
public @interface GJ {
}
