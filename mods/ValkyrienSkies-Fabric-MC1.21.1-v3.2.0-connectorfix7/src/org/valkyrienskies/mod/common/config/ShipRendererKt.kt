package org.valkyrienskies.mod.common.config

public final val shipRenderer: ShipRenderer
   public final get() {
      var var10000: ShipRenderer = settings.renderer
      if (var10000 == null) {
         var10000 = VSGameConfig.CLIENT.defaultRenderer
      }

      return var10000
   }

