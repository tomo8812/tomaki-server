package org.valkyrienskies.mod.fabric.compat.dynmap

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.class_1937
import net.minecraft.class_3218
import org.dynmap.DynmapCommonAPI
import org.dynmap.fabric_1_20.DynmapMod
import org.dynmap.fabric_1_20.FabricServer
import org.dynmap.fabric_1_20.FabricWorld
import org.dynmap.markers.MarkerAPI
import org.dynmap.markers.MarkerIcon
import org.valkyrienskies.mod.compat.dynmap.DynmapHandler

public class FabricDynmapHandler : DynmapHandler {
   public override fun register() {
      super.register()
      ServerTickEvents.END_WORLD_TICK.register(org/valkyrienskies/mod/fabric/compat/dynmap/FabricDynmapHandler##Lambda_0_60(this))
   }

   public override fun getWorldName(level: class_3218): String {
      val var10000: java.lang.String = FabricWorld.getWorldName(DynmapMod.plugin, level as class_1937)
      return var10000
   }

   public override fun getOrCreateIcon(): MarkerIcon? {
      var var10000: DynmapCommonAPI = this.getApi()
      if (var10000 != null) {
         val var5: MarkerAPI = var10000.getMarkerAPI()
         if (var5 != null) {
            val var6: MarkerIcon = var5.getMarkerIcon("ship")
            if (var6 != null) {
               return var6
            }
         }
      }

      val `$this$getOrCreateIcon_u24lambda_u241`: FabricDynmapHandler = this
      if (DynmapMod.plugin != null) {
         val var7: FabricServer = DynmapMod.plugin.getFabricServer()
         if (var7 != null) {
            var10000 = `$this$getOrCreateIcon_u24lambda_u241`.getApi()
            if (var10000 != null) {
               val var9: MarkerAPI = var10000.getMarkerAPI()
               if (var9 != null) {
                  return var9.createMarkerIcon("ship", "ship", var7.openResource("valkyrienskies", "assets/valkyrienskies/icon.png"))
               }
            }

            return null
         }
      }

      return null
   }
}
