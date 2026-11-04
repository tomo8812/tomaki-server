package org.valkyrienskies.core.impl.game.ships

import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

// $VF: local visibility outside of methodSupplier
internal class `ShipData$special$$inlined$observable$1` : ObservableProperty<java.lang.Boolean> {
   @JvmSynthetic
   ShipData this$0;

   fun `ShipData$special$$inlined$observable$1`(`$initialValue`: Any, var2: ShipData) {
      super(`$initialValue`)
      this.this$0 = var2
   }

   protected open fun afterChange(property: KProperty<*>, oldValue: Any, newValue: Any) {
      if (newValue as java.lang.Boolean != oldValue as java.lang.Boolean) {
         this.this$0.needsUpdating = true
      }
   }
}
