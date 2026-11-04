package org.valkyrienskies.mod.util

import kotlin.jvm.internal.Intrinsics
import net.minecraft.class_1297
import net.minecraft.class_2940
import net.minecraft.class_2941
import net.minecraft.class_2945

@JvmSynthetic
public inline fun <reified T : class_1297, R> defineSynced(serializer: class_2941<Any>): EntityDataDelegate<Any> {
   Intrinsics.reifiedOperationMarker(4, "T")
   val var10002: class_2940 = class_2945.method_12791(class_1297::class.java, serializer)
   return EntityDataDelegate(var10002)
}
