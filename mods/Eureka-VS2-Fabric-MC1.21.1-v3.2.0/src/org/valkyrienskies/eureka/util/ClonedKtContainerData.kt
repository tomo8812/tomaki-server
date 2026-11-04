package org.valkyrienskies.eureka.util

import kotlin.reflect.KProperty

public class ClonedKtContainerData(parent: KtContainerData) : IKtContainerData {
   public final val parent: KtContainerData
   private final var count: Int

   init {
      this.parent = parent
   }

   public override operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ContainerDataDelegate {
      return ContainerDataDelegate(this.count++, this)
   }

   fun method_17390(i: Int): Int {
      this.parent.method_17390(i)
   }

   fun method_17391(i: Int, value: Int) {
      this.parent.method_17391(i, value)
   }

   fun method_17389(): Int {
      this.parent.method_17389()
   }
}
