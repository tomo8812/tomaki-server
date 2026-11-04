package org.valkyrienskies.core.internal.config

public interface VsiConfigModelEntry<T> : VsiConfigNode {
   public val getValue: () -> Any

   public val setValue: (Any) -> Unit

   public val min: Any?

   public val max: Any?

   public val name: String

   public val description: String?

   public val default: Any
}
