package org.valkyrienskies.core.internal.config

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.FIELD])
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target([ElementType.FIELD])
annotation class ConfigEntry(
   val description: String = "",
   val min: Double = java.lang.Double.NaN,
   val max: Double = java.lang.Double.NaN
)
