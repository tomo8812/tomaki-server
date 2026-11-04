package org.valkyrienskies.core.api.ships

import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.attachment.AttachmentHolder
import org.valkyrienskies.core.api.util.GameTickOnly

@GameTickOnly
public interface LoadedServerShip : AttachmentHolder, LoadedShip, ServerShip {
   @VsBeta
   public val wingManager: WingManager?

   public val dragController: DragController?

   @Deprecated(message = "Use the AttachmentHolder methods setAttachment/removeAttachment", replaceWith = @ReplaceWith(expression = "setAttachment(value)", imports = []))
   public open fun <T> setAttachment(clazz: Class<Any>, value: Any?): Any? {
      val var10000: Any
      if (value == null) {
         var10000 = this.removeAttachment(clazz)
      } else {
         var10000 = this.setAttachment(value)
      }

      return var10000
   }

   @VsBeta
   public override fun <T : Any> getAttachment(clazz: Class<Any>): Any? {
      return (T)this.getAttachment(clazz)
   }
}
