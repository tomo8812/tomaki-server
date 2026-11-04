package org.valkyrienskies.core.api.attachment

import org.valkyrienskies.core.api.VsBeta

@VsBeta
public interface AttachmentRegistration<T> {
   public val attachmentClass: Class<Any>

   public val key: String

   public val serializer: AttachmentSerializer

   public interface Builder<T> {
      public val attachmentClass: Class<Any>

      public var key: String

      public var serializer: AttachmentSerializer

      public abstract fun useJacksonSerializer(): org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder<Any> {
      }

      public abstract fun useLegacySerializer(): org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder<Any> {
      }

      public abstract fun useTransientSerializer(): org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder<Any> {
      }

      public open fun serializer(serializer: AttachmentSerializer): org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder<Any> {
         val it: AttachmentRegistration.Builder = this
         this.serializer = serializer
         return this
      }

      public open fun key(key: String): org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder<Any> {
         val it: AttachmentRegistration.Builder = this
         this.key = key
         return this
      }

      public abstract fun <NEW> useUpdater(newAttachmentClass: Class<Any>, updater: (Any) -> Any?): org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder<
            Any
         > {
      }

      public abstract fun build(): AttachmentRegistration<Any> {
      }
   }
}
