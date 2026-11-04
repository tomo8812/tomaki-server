package org.valkyrienskies.mod.common.util

import java.util.HashMap
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_3218
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.DragController
import org.valkyrienskies.core.api.ships.LoadedServerShip

@SourceDebugExtension(["SMAP\nDragInfoReporter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DragInfoReporter.kt\norg/valkyrienskies/mod/common/util/DragInfoReporter\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,19:1\n1863#2,2:20\n*S KotlinDebug\n*F\n+ 1 DragInfoReporter.kt\norg/valkyrienskies/mod/common/util/DragInfoReporter\n*L\n13#1:20,2\n*E\n"])
public object DragInfoReporter {
   public final val shipDragValues: HashMap<Long, Vector3dc> = HashMap()
   public final val shipLiftValues: HashMap<Long, Vector3dc> = HashMap()

   public fun tick(level: class_3218) {
      for (`element$iv` in shipObjectWorld.loadedShips) {
         val ship: LoadedServerShip = `element$iv` as LoadedServerShip
         var var10000: java.util.Map = shipDragValues
         var var10001: java.lang.Long = ship.getId()
         var var10002: DragController = ship.dragController
         var10000.put(var10001, var10002.getDragForce())
         var10000 = shipLiftValues
         var10001 = ship.getId()
         var10002 = ship.dragController
         var10000.put(var10001, var10002.getLiftForce())
      }
   }
}
