package org.valkyrienskies.core.api

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin/RequiresOptIn.Level
import org.jetbrains.annotations.ApiStatus

@Retention(RetentionPolicy.RUNTIME)
@ApiStatus.Experimental
@RequiresOptIn(message = "This is an experimental API, it may be changed or removed without warning in future versions of Valkyrien Skies. Addons that use this in production code are recommended to catch a potential LinkageError or NotImplementedError and recover gracefully if this API throws or is not available.", level = kotlin.RequiresOptIn.Level.WARNING)
annotation class VsBeta(

)
