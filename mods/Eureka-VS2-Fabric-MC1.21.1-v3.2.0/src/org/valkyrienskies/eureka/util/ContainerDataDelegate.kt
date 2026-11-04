package org.valkyrienskies.eureka.util

import kotlin.reflect.KProperty
import net.minecraft.class_3913

public class ContainerDataDelegate(id: Int, containerData: class_3913) {
   private final val id: Int
   private final val containerData: class_3913

   init {
      this.id = id
      this.containerData = containerData
   }

   public operator fun getValue(thisRef: Any, property: KProperty<*>): Int {
      return this.containerData.method_17390(this.id)
   }

   public operator fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
      this.containerData.method_17391(this.id, value)
   }
}
