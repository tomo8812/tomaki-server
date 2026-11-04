package org.valkyrienskies.core.internal.config

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.FIELD])
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target([ElementType.FIELD])
annotation class ConfigCategory(
   val title: String
)
