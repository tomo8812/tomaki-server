@file:SourceDebugExtension(["SMAP\nShipSettings.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipSettings.kt\norg/valkyrienskies/mod/common/util/ShipSettingsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,52:1\n1#2:53\n*E\n"])

package org.valkyrienskies.mod.common.util

import kotlin.jvm.internal.SourceDebugExtension

public final val settings: ShipSettings
   public final get() {
      var var10000: ShipSettings = `$this$settings`.getAttachment(ShipSettings.class)
      if (var10000 == null) {
         val var1: ShipSettings = ShipSettings(false, false, false, 7, null)
         `$this$settings`.setAttachment(var1)
         var10000 = var1
      }

      return var10000
   }


public final val settings: ClientShipSettings
   public final get() {
      return ClientShipSettings(null, 1, null)
   }

