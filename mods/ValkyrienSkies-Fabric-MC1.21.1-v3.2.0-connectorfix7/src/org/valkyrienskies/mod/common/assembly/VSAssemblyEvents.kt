package org.valkyrienskies.mod.common.assembly

import net.minecraft.class_2338
import net.minecraft.class_2487
import net.minecraft.class_3218
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.util.events.EventEmitterImpl

public object VSAssemblyEvents {
   public final val beforeCopy: EventEmitterImpl<org.valkyrienskies.mod.common.assembly.VSAssemblyEvents.BeforeCopy> = EventEmitterImpl()
   public final val onPasteBeforeBlocksAreLoaded: EventEmitterImpl<org.valkyrienskies.mod.common.assembly.VSAssemblyEvents.OnPasteBeforeBlocksAreLoaded> =
      EventEmitterImpl()
      public final val onPasteAfterBlocksAreLoaded: EventEmitterImpl<org.valkyrienskies.mod.common.assembly.VSAssemblyEvents.OnPasteAfterBlocksAreLoaded> =
      EventEmitterImpl()

   public data class BeforeCopy(level: class_3218,
      minPos: Vector3dc,
      maxPos: Vector3dc,
      center: Vector3dc,
      shipAssembledFrom: ServerShip?,
      blockPositions: Set<class_2338>,
      tempData: MutableMap<String, class_2487>
   ) {
      public final val level: class_3218
      public final val minPos: Vector3dc
      public final val maxPos: Vector3dc
      public final val center: Vector3dc
      public final val shipAssembledFrom: ServerShip?
      public final val blockPositions: Set<class_2338>
      public final val tempData: MutableMap<String, class_2487>

      init {
         this.level = level
         this.minPos = minPos
         this.maxPos = maxPos
         this.center = center
         this.shipAssembledFrom = shipAssembledFrom
         this.blockPositions = blockPositions
         this.tempData = tempData
      }

      public operator fun component1(): class_3218 {
         return this.level
      }

      public operator fun component2(): Vector3dc {
         return this.minPos
      }

      public operator fun component3(): Vector3dc {
         return this.maxPos
      }

      public operator fun component4(): Vector3dc {
         return this.center
      }

      public operator fun component5(): ServerShip? {
         return this.shipAssembledFrom
      }

      public operator fun component6(): Set<class_2338> {
         return this.blockPositions
      }

      public operator fun component7(): MutableMap<String, class_2487> {
         return this.tempData
      }

      public fun copy(
         level: class_3218 = this.level,
         minPos: Vector3dc = this.minPos,
         maxPos: Vector3dc = this.maxPos,
         center: Vector3dc = this.center,
         shipAssembledFrom: ServerShip? = this.shipAssembledFrom,
         blockPositions: Set<class_2338> = this.blockPositions,
         tempData: MutableMap<String, class_2487> = this.tempData
      ): org.valkyrienskies.mod.common.assembly.VSAssemblyEvents.BeforeCopy {
         return VSAssemblyEvents.BeforeCopy(level, minPos, maxPos, center, shipAssembledFrom, blockPositions, tempData)
      }

      public override fun toString(): String {
         return "BeforeCopy(level=${this.level}, minPos=${this.minPos}, maxPos=${this.maxPos}, center=${this.center}, shipAssembledFrom=${this.shipAssembledFrom}, blockPositions=${this.blockPositions}, tempData=${this.tempData})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (((this.level.hashCode() * 31 + this.minPos.hashCode()) * 31 + this.maxPos.hashCode()) * 31 + this.center.hashCode()) * 31
                              + (if (this.shipAssembledFrom == null) 0 else this.shipAssembledFrom.hashCode())
                        )
                        * 31
                     + this.blockPositions.hashCode()
               )
               * 31
            + this.tempData.hashCode()
         }

      public override operator fun equals(other: Any?): Boolean {
         label58@
         if (this === other) {
            return true
         } else {
            return other is VSAssemblyEvents.BeforeCopy
               && this.level == (other as VSAssemblyEvents.BeforeCopy).level
               && this.minPos == (other as VSAssemblyEvents.BeforeCopy).minPos
               && this.maxPos == (other as VSAssemblyEvents.BeforeCopy).maxPos
               && this.center == (other as VSAssemblyEvents.BeforeCopy).center
               && this.shipAssembledFrom == (other as VSAssemblyEvents.BeforeCopy).shipAssembledFrom
               && this.blockPositions == (other as VSAssemblyEvents.BeforeCopy).blockPositions
               && this.tempData == (other as VSAssemblyEvents.BeforeCopy).tempData
            }
      }
   }

   public data class OnPasteAfterBlocksAreLoaded(level: class_3218,
      oldShip: ServerShip?,
      newShip: ServerShip?,
      centerPosition: Pair<Vector3dc, Vector3dc>,
      tempData: Map<String, class_2487>
   ) {
      public final val level: class_3218
      public final val oldShip: ServerShip?
      public final val newShip: ServerShip?
      public final val centerPosition: Pair<Vector3dc, Vector3dc>
      public final val tempData: Map<String, class_2487>

      init {
         this.level = level
         this.oldShip = oldShip
         this.newShip = newShip
         this.centerPosition = centerPosition
         this.tempData = tempData
      }

      public operator fun component1(): class_3218 {
         return this.level
      }

      public operator fun component2(): ServerShip? {
         return this.oldShip
      }

      public operator fun component3(): ServerShip? {
         return this.newShip
      }

      public operator fun component4(): Pair<Vector3dc, Vector3dc> {
         return this.centerPosition
      }

      public operator fun component5(): Map<String, class_2487> {
         return this.tempData
      }

      public fun copy(
         level: class_3218 = this.level,
         oldShip: ServerShip? = this.oldShip,
         newShip: ServerShip? = this.newShip,
         centerPosition: Pair<Vector3dc, Vector3dc> = this.centerPosition,
         tempData: Map<String, class_2487> = this.tempData
      ): org.valkyrienskies.mod.common.assembly.VSAssemblyEvents.OnPasteAfterBlocksAreLoaded {
         return VSAssemblyEvents.OnPasteAfterBlocksAreLoaded(level, oldShip, newShip, centerPosition, tempData)
      }

      public override fun toString(): String {
         return "OnPasteAfterBlocksAreLoaded(level=${this.level}, oldShip=${this.oldShip}, newShip=${this.newShip}, centerPosition=${this.centerPosition}, tempData=${this.tempData})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (this.level.hashCode() * 31 + (if (this.oldShip == null) 0 else this.oldShip.hashCode())) * 31
                              + (if (this.newShip == null) 0 else this.newShip.hashCode())
                        )
                        * 31
                     + this.centerPosition.hashCode()
               )
               * 31
            + this.tempData.hashCode()
         }

      public override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is VSAssemblyEvents.OnPasteAfterBlocksAreLoaded
               && this.level == (other as VSAssemblyEvents.OnPasteAfterBlocksAreLoaded).level
               && this.oldShip == (other as VSAssemblyEvents.OnPasteAfterBlocksAreLoaded).oldShip
               && this.newShip == (other as VSAssemblyEvents.OnPasteAfterBlocksAreLoaded).newShip
               && this.centerPosition == (other as VSAssemblyEvents.OnPasteAfterBlocksAreLoaded).centerPosition
               && this.tempData == (other as VSAssemblyEvents.OnPasteAfterBlocksAreLoaded).tempData
            }
      }
   }

   public data class OnPasteBeforeBlocksAreLoaded(level: class_3218,
      oldShip: ServerShip?,
      newShip: ServerShip?,
      centerPosition: Pair<Vector3dc, Vector3dc>,
      tempData: Map<String, class_2487>
   ) {
      public final val level: class_3218
      public final val oldShip: ServerShip?
      public final val newShip: ServerShip?
      public final val centerPosition: Pair<Vector3dc, Vector3dc>
      public final val tempData: Map<String, class_2487>

      init {
         this.level = level
         this.oldShip = oldShip
         this.newShip = newShip
         this.centerPosition = centerPosition
         this.tempData = tempData
      }

      public operator fun component1(): class_3218 {
         return this.level
      }

      public operator fun component2(): ServerShip? {
         return this.oldShip
      }

      public operator fun component3(): ServerShip? {
         return this.newShip
      }

      public operator fun component4(): Pair<Vector3dc, Vector3dc> {
         return this.centerPosition
      }

      public operator fun component5(): Map<String, class_2487> {
         return this.tempData
      }

      public fun copy(
         level: class_3218 = this.level,
         oldShip: ServerShip? = this.oldShip,
         newShip: ServerShip? = this.newShip,
         centerPosition: Pair<Vector3dc, Vector3dc> = this.centerPosition,
         tempData: Map<String, class_2487> = this.tempData
      ): org.valkyrienskies.mod.common.assembly.VSAssemblyEvents.OnPasteBeforeBlocksAreLoaded {
         return VSAssemblyEvents.OnPasteBeforeBlocksAreLoaded(level, oldShip, newShip, centerPosition, tempData)
      }

      public override fun toString(): String {
         return "OnPasteBeforeBlocksAreLoaded(level=${this.level}, oldShip=${this.oldShip}, newShip=${this.newShip}, centerPosition=${this.centerPosition}, tempData=${this.tempData})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (this.level.hashCode() * 31 + (if (this.oldShip == null) 0 else this.oldShip.hashCode())) * 31
                              + (if (this.newShip == null) 0 else this.newShip.hashCode())
                        )
                        * 31
                     + this.centerPosition.hashCode()
               )
               * 31
            + this.tempData.hashCode()
         }

      public override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is VSAssemblyEvents.OnPasteBeforeBlocksAreLoaded
               && this.level == (other as VSAssemblyEvents.OnPasteBeforeBlocksAreLoaded).level
               && this.oldShip == (other as VSAssemblyEvents.OnPasteBeforeBlocksAreLoaded).oldShip
               && this.newShip == (other as VSAssemblyEvents.OnPasteBeforeBlocksAreLoaded).newShip
               && this.centerPosition == (other as VSAssemblyEvents.OnPasteBeforeBlocksAreLoaded).centerPosition
               && this.tempData == (other as VSAssemblyEvents.OnPasteBeforeBlocksAreLoaded).tempData
            }
      }
   }
}
