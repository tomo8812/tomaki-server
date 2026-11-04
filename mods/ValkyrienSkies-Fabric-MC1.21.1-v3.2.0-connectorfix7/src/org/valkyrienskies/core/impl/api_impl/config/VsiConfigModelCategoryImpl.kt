package org.valkyrienskies.core.impl.api_impl.config

import java.util.LinkedHashMap
import org.valkyrienskies.core.internal.config.VsiConfigModelCategory
import org.valkyrienskies.core.internal.config.VsiConfigModelEntry
import org.valkyrienskies.core.internal.config.VsiConfigNode

public data class VsiConfigModelCategoryImpl(title: String, children: MutableMap<String, VsiConfigNode> = LinkedHashMap() as java.util.Map) :
   VsiConfigModelCategory {
   public open val title: String
   public open val children: MutableMap<String, VsiConfigNode>

   init {
      this.title = title
      this.children = children
   }

   public override fun addEntry(name: String, entry: VsiConfigModelEntry<*>) {
      this.children.put(name, entry)
   }

   public override fun addCategory(cat: VsiConfigModelCategory) {
      this.children.put(cat.title, cat)
   }

   public operator fun component1(): String {
      return this.title
   }

   public operator fun component2(): MutableMap<String, VsiConfigNode> {
      return this.children
   }

   public fun copy(title: String = this.title, children: MutableMap<String, VsiConfigNode> = this.children): VsiConfigModelCategoryImpl {
      return VsiConfigModelCategoryImpl(title, children)
   }

   public override fun toString(): String {
      return "VsiConfigModelCategoryImpl(title=${this.title}, children=${this.children})"
   }

   public override fun hashCode(): Int {
      return this.title.hashCode() * 31 + this.children.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is VsiConfigModelCategoryImpl
            && this.title == (other as VsiConfigModelCategoryImpl).title
            && this.children == (other as VsiConfigModelCategoryImpl).children
         }
   }
}
