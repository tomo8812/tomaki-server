@file:SourceDebugExtension(["SMAP\nLoadedServerShip.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LoadedServerShip.kt\norg/valkyrienskies/core/api/ships/LoadedServerShipKt\n+ 2 AttachmentHolder.kt\norg/valkyrienskies/core/api/attachment/AttachmentHolderKt\n*L\n1#1,47:1\n80#2:48\n*S KotlinDebug\n*F\n+ 1 LoadedServerShip.kt\norg/valkyrienskies/core/api/ships/LoadedServerShipKt\n*L\n46#1:48\n*E\n"])

package org.valkyrienskies.core.api.ships

import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import org.valkyrienskies.core.api.attachment.AttachmentHolder

@Deprecated(message = "Use the version from the attachments package", replaceWith = @ReplaceWith(expression = "setAttachment(value)", imports = ["org.valkyrienskies.core.api.attachment.setAttachment"]))
@JvmSynthetic
public inline fun <reified T> LoadedServerShip.setAttachment(value: Any?): Any? {
   val var10000: Any
   if (value == null) {
      val `$this$removeAttachment$iv`: AttachmentHolder = `$this$setAttachment`
      Intrinsics.reifiedOperationMarker(4, "T")
      var10000 = `$this$removeAttachment$iv`.removeAttachment(Object::class.java)
   } else {
      var10000 = `$this$setAttachment`.setAttachment(value)
   }

   return (T)var10000
}
