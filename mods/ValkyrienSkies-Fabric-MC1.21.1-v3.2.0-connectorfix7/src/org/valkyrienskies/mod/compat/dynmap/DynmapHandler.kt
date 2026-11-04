package org.valkyrienskies.mod.compat.dynmap

import kotlin.jvm.internal.SourceDebugExtension
import kotlin.random.Random
import net.minecraft.class_3218
import org.dynmap.DynmapCommonAPI
import org.dynmap.DynmapCommonAPIListener
import org.dynmap.markers.GenericMarker
import org.dynmap.markers.Marker
import org.dynmap.markers.MarkerAPI
import org.dynmap.markers.MarkerIcon
import org.dynmap.markers.MarkerSet
import org.dynmap.markers.PolyLineMarker
import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.internal.ships.VsiQueryableShipData
import org.valkyrienskies.mod.common.config.VSGameConfig

@SourceDebugExtension(["SMAP\nDynmapHandler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DynmapHandler.kt\norg/valkyrienskies/mod/compat/dynmap/DynmapHandler\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,166:1\n1863#2,2:167\n1863#2,2:169\n1863#2,2:171\n1863#2,2:173\n*S KotlinDebug\n*F\n+ 1 DynmapHandler.kt\norg/valkyrienskies/mod/compat/dynmap/DynmapHandler\n*L\n39#1:167,2\n43#1:169,2\n47#1:171,2\n51#1:173,2\n*E\n"])
public abstract class DynmapHandler : DynmapCommonAPIListener {
   public final var api: DynmapCommonAPI?

   public open fun apiEnabled(dynmapCommonAPI: DynmapCommonAPI) {
      this.api = dynmapCommonAPI
   }

   public open fun register() {
      DynmapCommonAPIListener.register(this)
   }

   public fun updateMarkers(level: class_3218) {
      val worldName: java.lang.String = this.getWorldName(level)
      val allShips: VsiQueryableShipData = shipObjectWorld.allShips
      val var10000: MarkerSet = this.getOrCreateMarkerSet()
      if (var10000 != null) {
         val markerSet: MarkerSet = var10000
         this.clearUnusedIconMarkers(var10000, allShips)
         if (VSGameConfig.SERVER.Dynmap.showIconMarkers) {
            for (`element$iv` in allShips) {
               this.renderShipIconMarker(`element$iv` as ServerShip, markerSet, worldName)
            }
         }

         this.clearUnusedPolylineMarkers(markerSet, allShips)
         if (VSGameConfig.SERVER.Dynmap.showPolylineMarkers) {
            for (var14 in allShips) {
               this.renderShipPolylineMarker(var14 as ServerShip, markerSet, worldName)
            }
         }
      }
   }

   private fun clearUnusedIconMarkers(markerSet: MarkerSet, data: QueryableShipData<ServerShip>) {
      val var10000: java.util.Set = markerSet.getMarkers()
      if (var10000 != null) {
         for (`element$iv` in var10000) {
            val marker: Marker = `element$iv` as Marker
            this.clearUnusedMarker(marker as GenericMarker, data, VSGameConfig.SERVER.Dynmap.showIconMarkers)
         }
      }
   }

   private fun clearUnusedPolylineMarkers(markerSet: MarkerSet, data: QueryableShipData<ServerShip>) {
      val var10000: java.util.Set = markerSet.getPolyLineMarkers()
      if (var10000 != null) {
         for (`element$iv` in var10000) {
            val marker: PolyLineMarker = `element$iv` as PolyLineMarker
            this.clearUnusedMarker(marker as GenericMarker, data, VSGameConfig.SERVER.Dynmap.showPolylineMarkers)
         }
      }
   }

   private fun clearUnusedMarker(marker: GenericMarker, data: QueryableShipData<ServerShip>, enabled: Boolean) {
      val var10000: java.lang.String = marker.getMarkerID()
      if (data.getById(java.lang.Long.parseLong(StringsKt.replace$default(var10000, "ship", "", false, 4, null))) == null || !enabled) {
         marker.deleteMarker()
      }
   }

   private fun renderShipIconMarker(data: ServerShip, markerSet: MarkerSet, world: String) {
      val pos: Vector3dc = data.getTransform().positionInWorld
      val label: java.lang.String = this.createShipLabel(data)
      val icon: MarkerIcon = this.getOrCreateIcon()
      val var10000: Marker = markerSet.findMarker("ship${data.getId()}")
      if (var10000 == null) {
         val `$this$renderShipIconMarker_u24lambda_u244`: DynmapHandler = this
         markerSet.createMarker("ship${data.getId()}", label, true, world, pos.x(), pos.y(), pos.z(), icon, true)
      } else {
         var10000.setDescription(label)
         var10000.setLocation(world, pos.x(), pos.y(), pos.z())
         var10000.setMarkerIcon(icon)
      }
   }

   private fun renderShipPolylineMarker(data: ServerShip, markerSet: MarkerSet, world: String) {
      val arrays: Triple = this.getArraysFromAABB(data.getWorldAABB())
      val label: java.lang.String = this.createShipLabel(data)
      var var10000: PolyLineMarker = markerSet.findPolyLineMarker("ship${data.getId()}")
      if (var10000 == null) {
         val `$this$renderShipPolylineMarker_u24lambda_u245`: DynmapHandler = this
         val self: PolyLineMarker = markerSet.createPolyLineMarker(
            "ship${data.getId()}",
            label,
            true,
            world,
            arrays.getFirst() as DoubleArray,
            arrays.getSecond() as DoubleArray,
            arrays.getThird() as DoubleArray,
            true
         )
         if (self != null) {
            self.setLineStyle(5, self.getLineOpacity(), Random.Default.nextInt(0, 16777215))
         }

         var10000 = self
         if (self == null) {
            return
         }
      }

      var10000.setDescription(label)
      var10000.setCornerLocations(arrays.getFirst() as DoubleArray, arrays.getSecond() as DoubleArray, arrays.getThird() as DoubleArray)
   }

   private fun getOrCreateMarkerSet(): MarkerSet? {
      if (this.api != null) {
         val var10000: MarkerAPI = this.api.getMarkerAPI()
         if (var10000 != null) {
            val var3: MarkerSet = var10000.getMarkerSet("valkyrienskies")
            if (var3 != null) {
               return var3
            }
         }
      }

      val `$this$getOrCreateMarkerSet_u24lambda_u246`: DynmapHandler = this
      if (this.api != null) {
         val var4: MarkerAPI = `$this$getOrCreateMarkerSet_u24lambda_u246`.api.getMarkerAPI()
         if (var4 != null) {
            return var4.createMarkerSet("valkyrienskies", "VS Ship Markers", null, true)
         }
      }

      return null
   }

   private fun getArraysFromAABB(aabb: AABBdc): Triple<DoubleArray, DoubleArray, DoubleArray> {
      return Triple(
         doubleArrayOf(
            aabb.minX(),
            aabb.minX(),
            aabb.minX(),
            aabb.minX(),
            aabb.minX(),
            aabb.minX(),
            aabb.maxX(),
            aabb.maxX(),
            aabb.minX(),
            aabb.maxX(),
            aabb.maxX(),
            aabb.maxX(),
            aabb.maxX(),
            aabb.maxX(),
            aabb.minX(),
            aabb.minX(),
            aabb.maxX()
         ),
         doubleArrayOf(
            aabb.minY(),
            aabb.maxY(),
            aabb.maxY(),
            aabb.minY(),
            aabb.minY(),
            aabb.maxY(),
            aabb.maxY(),
            aabb.minY(),
            aabb.minY(),
            aabb.minY(),
            aabb.minY(),
            aabb.maxY(),
            aabb.maxY(),
            aabb.maxY(),
            aabb.maxY(),
            aabb.minY(),
            aabb.minY()
         ),
         doubleArrayOf(
            aabb.minZ(),
            aabb.minZ(),
            aabb.maxZ(),
            aabb.maxZ(),
            aabb.minZ(),
            aabb.minZ(),
            aabb.minZ(),
            aabb.minZ(),
            aabb.minZ(),
            aabb.minZ(),
            aabb.maxZ(),
            aabb.maxZ(),
            aabb.minZ(),
            aabb.maxZ(),
            aabb.maxZ(),
            aabb.maxZ(),
            aabb.maxZ()
         )
      )
   }

   private fun createShipLabel(ship: ServerShip): String {
      var label: java.lang.String = "<h1>${ship.slug}</h1>"
      if (VSGameConfig.SERVER.Dynmap.showShipId) {
         label = "$label<p><strong>Ship ID: </strong>${ship.getId()}</p>"
      }

      if (VSGameConfig.SERVER.Dynmap.showShipMass) {
         label = "$label<p><strong>Ship Mass: </strong>${ship.inertiaData.getMass()} kg</p>"
      }

      return label
   }

   public abstract fun getWorldName(level: class_3218): String {
   }

   public abstract fun getOrCreateIcon(): MarkerIcon? {
   }
}
