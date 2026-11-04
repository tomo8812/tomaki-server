package org.valkyrienskies.core.internal.world.chunks

public interface VsiBlockType {
   @Deprecated(message = "This is a code smell... API consumers should not have to worry about how block types are internally represented")
   public abstract fun toInt(): Int {
   }
}
