package org.valkyrienskies.core.api.ships

import kotlin.jvm.internal.Intrinsics

@Deprecated(message = "Since VS 2.5, attachments can only be set on a LoadedServerShip; make sure to import org.valkyrienskies.core.api.attachment.setAttachment and remove old imports on org.valkyrienskies.core.api.ships.saveAttachment", replaceWith = @ReplaceWith(expression = "this.setAttachment(value)", imports = ["org.valkyrienskies.core.api.attachment.setAttachment"]), level = DeprecationLevel.ERROR)
@JvmSynthetic
public inline fun <reified T> ServerShip.saveAttachment(value: Any?): Nothing {
   Intrinsics.reifiedOperationMarker(4, "T")
   `$this$saveAttachment`.saveAttachment(Object::class.java, value)
   throw KotlinNothingValueException()
}

@Deprecated(message = "Since VS 2.5, attachments can only be retrieved from a LoadedServerShip; make sure to import org.valkyrienskies.core.api.attachment.getAttachment and remove old imports on org.valkyrienskies.core.api.ships.getAttachment", replaceWith = @ReplaceWith(expression = "this.getAttachment<T>()", imports = ["org.valkyrienskies.core.api.attachment.getAttachment"]), level = DeprecationLevel.ERROR)
@JvmSynthetic
public inline fun <reified T : Any> ServerShip.getAttachment(): Any? {
   Intrinsics.reifiedOperationMarker(4, "T")
   return (T)`$this$getAttachment`.getAttachment(Object::class.java)
}
