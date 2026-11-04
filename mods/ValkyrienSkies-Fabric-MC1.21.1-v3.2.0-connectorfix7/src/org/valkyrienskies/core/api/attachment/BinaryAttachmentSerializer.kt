package org.valkyrienskies.core.api.attachment

import java.io.DataInput
import java.io.DataOutput
import org.valkyrienskies.core.api.VsBeta

@VsBeta
public interface BinaryAttachmentSerializer<T> : AttachmentSerializer {
   public abstract fun serialize(attachment: Any, output: DataOutput) {
   }

   public abstract fun deserialize(input: DataInput): Any? {
   }
}
