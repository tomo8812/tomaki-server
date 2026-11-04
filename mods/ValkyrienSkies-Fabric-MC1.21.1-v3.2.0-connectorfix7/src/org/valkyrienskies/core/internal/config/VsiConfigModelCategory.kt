package org.valkyrienskies.core.internal.config

public interface VsiConfigModelCategory : VsiConfigNode {
   public val title: String

   public val children: Map<String, VsiConfigNode>

   public abstract fun addEntry(name: String, entry: VsiConfigModelEntry<*>) {
   }

   public abstract fun addCategory(cat: VsiConfigModelCategory) {
   }
}
