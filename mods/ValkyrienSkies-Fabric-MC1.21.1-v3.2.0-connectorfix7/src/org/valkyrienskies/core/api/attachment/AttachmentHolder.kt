package org.valkyrienskies.core.api.attachment

import java.util.function.Supplier
import org.valkyrienskies.core.api.VsBeta

@VsBeta
public interface AttachmentHolder {
   public abstract fun <T : Any> getAttachment(clazz: Class<Any>): Any? {
   }

   public abstract fun <T : Any> getOrPutAttachment(clazz: Class<Any>, supplier: Supplier<Any>): Any {
   }

   public abstract fun <T : Any> setAttachment(value: Any): Any? {
   }

   public abstract fun <T : Any> removeAttachment(clazz: Class<Any>): Any? {
   }

   public abstract fun getAllAttachments(): Iterable<Any> {
   }
}
