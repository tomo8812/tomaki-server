package org.valkyrienskies.core.api.util

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import kotlin/RequiresOptIn.Level

@Retention(AnnotationRetention.BINARY)
@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FIELD])
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR])
@RequiresOptIn(message = "This is a Physics Thread only class or method. Ensure you are not accessing it, directly or by reference, from the Game Thread!", level = kotlin.RequiresOptIn.Level.WARNING)
annotation class PhysTickOnly(

)
