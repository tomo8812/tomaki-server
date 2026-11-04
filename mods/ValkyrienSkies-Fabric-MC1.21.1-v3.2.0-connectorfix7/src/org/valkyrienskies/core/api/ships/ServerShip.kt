package org.valkyrienskies.core.api.ships

import org.jetbrains.annotations.ApiStatus
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData

public interface ServerShip : Ship {
   public var slug: String?

   public var isStatic: Boolean

   public val inertiaData: ShipInertiaData

   public var transformProvider: ServerShipTransformProvider?

   @ApiStatus.Experimental
   public abstract fun unsafeSetTransform(transform: BodyTransform) {
   }

   @Deprecated(message = "Since VS 2.5, attachments can only be saved to a LoadedServerShip", replaceWith = @ReplaceWith(expression = "this.setAttachment(clazz, value)", imports = []), level = DeprecationLevel.ERROR)
   @JvmSynthetic
   public open fun <T> saveAttachment(clazz: Class<Any>, value: Any?): Nothing {
      throw NotImplementedError(
         "An operation is not implemented: Tried to save an attachment on an unloaded ship. Since VS 2.5, attachments can only be saved using LoadedServerShip"
      )
   }

   @Deprecated(message = "Since VS 2.5, attachments can only be retrieved from a LoadedServerShip", replaceWith = @ReplaceWith(expression = "this.getAttachment(clazz)", imports = []), level = DeprecationLevel.ERROR)
   @JvmSynthetic
   public open fun <T : Any> getAttachment(clazz: Class<Any>): Any? {
      throw NotImplementedError(
         "An operation is not implemented: Tried to retrieve an attachment on an unloaded ship. Since VS 2.5, attachments can only be saved using LoadedServerShip"
      )
   }
}
