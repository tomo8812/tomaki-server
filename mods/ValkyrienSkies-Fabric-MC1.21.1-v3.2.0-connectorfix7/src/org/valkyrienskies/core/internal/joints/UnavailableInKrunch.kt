package org.valkyrienskies.core.internal.joints

import java.lang.annotation.RetentionPolicy
import kotlin/RequiresOptIn.Level

@Retention(AnnotationRetention.BINARY)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@RequiresOptIn(message = "This joint does not function when the Physics Backend is set to OG Krunch, requiring PhysX instead.", level = kotlin.RequiresOptIn.Level.WARNING)
annotation class UnavailableInKrunch(

)
