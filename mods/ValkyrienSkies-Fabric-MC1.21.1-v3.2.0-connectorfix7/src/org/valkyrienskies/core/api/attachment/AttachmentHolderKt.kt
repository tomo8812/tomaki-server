package org.valkyrienskies.core.api.attachment

import java.util.function.Supplier
import kotlin.jvm.internal.Intrinsics
import org.valkyrienskies.core.api.VsBeta

@VsBeta
@JvmSynthetic
public inline fun <reified T : Any> AttachmentHolder.getAttachment(): Any? {
   Intrinsics.reifiedOperationMarker(4, "T")
   return (T)`$this$getAttachment`.getAttachment(Object::class.java)
}

@VsBeta
@JvmSynthetic
public inline fun <reified T : Any> AttachmentHolder.getOrPutAttachment(supplier: Supplier<Any>): Any {
   Intrinsics.reifiedOperationMarker(4, "T")
   return (T)`$this$getOrPutAttachment`.getOrPutAttachment(Object::class.java, supplier)
}

@VsBeta
@JvmSynthetic
public inline fun <reified T : Any> AttachmentHolder.removeAttachment(): Any? {
   Intrinsics.reifiedOperationMarker(4, "T")
   return (T)`$this$removeAttachment`.removeAttachment(Object::class.java)
}
