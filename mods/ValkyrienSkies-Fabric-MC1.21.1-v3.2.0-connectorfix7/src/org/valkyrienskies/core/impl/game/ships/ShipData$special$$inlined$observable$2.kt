package org.valkyrienskies.core.impl.game.ships

import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty
import org.valkyrienskies.core.api.ships.properties.PhysPose

// $VF: local visibility outside of methodSupplier
internal class `ShipData$special$$inlined$observable$2` : ObservableProperty<PhysPose> {
   @JvmSynthetic
   ShipData this$0;

   fun `ShipData$special$$inlined$observable$2`(`$initialValue`: Any, var2: ShipData) {
      super(`$initialValue`)
      this.this$0 = var2
   }

   protected open fun afterChange(property: KProperty<*>, oldValue: Any, newValue: Any) {
      if (!(newValue as PhysPose == oldValue as PhysPose)) {
         this.this$0.needsUpdating = true
      }
   }
}
