package org.valkyrienskies.eureka.util

import it.unimi.dsi.fastutil.ints.IntArrayList
import kotlin.reflect.KProperty

public class KtContainerData : IKtContainerData {
   private final var values: IntArrayList = IntArrayList()

   public override operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ContainerDataDelegate {
      this.values.add(0)
      return ContainerDataDelegate(this.values.size() - 1, this)
   }

   fun method_17390(i: Int): Int {
      this.values.getInt(i)
   }

   fun method_17391(i: Int, value: Int) {
      this.values.set(i, value)
   }

   fun method_17389(): Int {
      this.values.size()
   }

   public fun clone(): ClonedKtContainerData {
      return ClonedKtContainerData(this)
   }
}
