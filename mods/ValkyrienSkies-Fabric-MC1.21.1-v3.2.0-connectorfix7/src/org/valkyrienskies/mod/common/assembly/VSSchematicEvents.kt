package org.valkyrienskies.mod.common.assembly

import net.minecraft.class_1792
import net.minecraft.class_2487
import net.minecraft.class_3218
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.util.events.EventEmitterImpl

public object VSSchematicEvents {
   public final val onCopy: EventEmitterImpl<org.valkyrienskies.mod.common.assembly.VSSchematicEvents.OnCopy> = EventEmitterImpl()
   public final val onPasteBeforeBlocksAreLoaded: EventEmitterImpl<org.valkyrienskies.mod.common.assembly.VSSchematicEvents.OnPasteBeforeBlocksAreLoaded> =
      EventEmitterImpl()
      public final val onPasteAfterBlocksAreLoaded: EventEmitterImpl<org.valkyrienskies.mod.common.assembly.VSSchematicEvents.OnPasteAfterBlocksAreLoaded> =
      EventEmitterImpl()
      public final val pasteSurvivalCost: EventEmitterImpl<org.valkyrienskies.mod.common.assembly.VSSchematicEvents.PasteSurvivalCost> = EventEmitterImpl()

   public data class OnCopy(level: class_3218,
      shipsToBeSaved: List<ServerShip>,
      centerPositions: Map<Long, Vector3dc>,
      saveData: MutableMap<String, class_2487>
   ) {
      public final val level: class_3218
      public final val shipsToBeSaved: List<ServerShip>
      public final val centerPositions: Map<Long, Vector3dc>
      public final val saveData: MutableMap<String, class_2487>

      init {
         this.level = level
         this.shipsToBeSaved = shipsToBeSaved
         this.centerPositions = centerPositions
         this.saveData = saveData
      }

      public operator fun component1(): class_3218 {
         return this.level
      }

      public operator fun component2(): List<ServerShip> {
         return this.shipsToBeSaved
      }

      public operator fun component3(): Map<Long, Vector3dc> {
         return this.centerPositions
      }

      public operator fun component4(): MutableMap<String, class_2487> {
         return this.saveData
      }

      public fun copy(
         level: class_3218 = this.level,
         shipsToBeSaved: List<ServerShip> = this.shipsToBeSaved,
         centerPositions: Map<Long, Vector3dc> = this.centerPositions,
         saveData: MutableMap<String, class_2487> = this.saveData
      ): org.valkyrienskies.mod.common.assembly.VSSchematicEvents.OnCopy {
         return VSSchematicEvents.OnCopy(level, shipsToBeSaved, centerPositions, saveData)
      }

      public override fun toString(): String {
         return "OnCopy(level=${this.level}, shipsToBeSaved=${this.shipsToBeSaved}, centerPositions=${this.centerPositions}, saveData=${this.saveData})"
      }

      public override fun hashCode(): Int {
         return ((this.level.hashCode() * 31 + this.shipsToBeSaved.hashCode()) * 31 + this.centerPositions.hashCode()) * 31 + this.saveData.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is VSSchematicEvents.OnCopy
               && this.level == (other as VSSchematicEvents.OnCopy).level
               && this.shipsToBeSaved == (other as VSSchematicEvents.OnCopy).shipsToBeSaved
               && this.centerPositions == (other as VSSchematicEvents.OnCopy).centerPositions
               && this.saveData == (other as VSSchematicEvents.OnCopy).saveData
            }
      }
   }

   public data class OnPasteAfterBlocksAreLoaded(level: class_3218,
      loadedShips: Map<Long, ServerShip>,
      centerPositions: Map<Long, Pair<Vector3dc, Vector3dc>>,
      saveData: Map<String, class_2487>
   ) {
      public final val level: class_3218
      public final val loadedShips: Map<Long, ServerShip>
      public final val centerPositions: Map<Long, Pair<Vector3dc, Vector3dc>>
      public final val saveData: Map<String, class_2487>

      init {
         this.level = level
         this.loadedShips = loadedShips
         this.centerPositions = centerPositions
         this.saveData = saveData
      }

      public operator fun component1(): class_3218 {
         return this.level
      }

      public operator fun component2(): Map<Long, ServerShip> {
         return this.loadedShips
      }

      public operator fun component3(): Map<Long, Pair<Vector3dc, Vector3dc>> {
         return this.centerPositions
      }

      public operator fun component4(): Map<String, class_2487> {
         return this.saveData
      }

      public fun copy(
         level: class_3218 = this.level,
         loadedShips: Map<Long, ServerShip> = this.loadedShips,
         centerPositions: Map<Long, Pair<Vector3dc, Vector3dc>> = this.centerPositions,
         saveData: Map<String, class_2487> = this.saveData
      ): org.valkyrienskies.mod.common.assembly.VSSchematicEvents.OnPasteAfterBlocksAreLoaded {
         return VSSchematicEvents.OnPasteAfterBlocksAreLoaded(level, loadedShips, centerPositions, saveData)
      }

      public override fun toString(): String {
         return "OnPasteAfterBlocksAreLoaded(level=${this.level}, loadedShips=${this.loadedShips}, centerPositions=${this.centerPositions}, saveData=${this.saveData})"
      }

      public override fun hashCode(): Int {
         return ((this.level.hashCode() * 31 + this.loadedShips.hashCode()) * 31 + this.centerPositions.hashCode()) * 31 + this.saveData.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is VSSchematicEvents.OnPasteAfterBlocksAreLoaded
               && this.level == (other as VSSchematicEvents.OnPasteAfterBlocksAreLoaded).level
               && this.loadedShips == (other as VSSchematicEvents.OnPasteAfterBlocksAreLoaded).loadedShips
               && this.centerPositions == (other as VSSchematicEvents.OnPasteAfterBlocksAreLoaded).centerPositions
               && this.saveData == (other as VSSchematicEvents.OnPasteAfterBlocksAreLoaded).saveData
            }
      }
   }

   public data class OnPasteBeforeBlocksAreLoaded(level: class_3218,
      partiallyLoadedShips: Map<Long, ServerShip>,
      newShip: Pair<Long, ServerShip>,
      centerPositions: Map<Long, Pair<Vector3dc, Vector3dc>>,
      saveData: Map<String, class_2487>
   ) {
      public final val level: class_3218
      public final val partiallyLoadedShips: Map<Long, ServerShip>
      public final val newShip: Pair<Long, ServerShip>
      public final val centerPositions: Map<Long, Pair<Vector3dc, Vector3dc>>
      public final val saveData: Map<String, class_2487>

      init {
         this.level = level
         this.partiallyLoadedShips = partiallyLoadedShips
         this.newShip = newShip
         this.centerPositions = centerPositions
         this.saveData = saveData
      }

      public operator fun component1(): class_3218 {
         return this.level
      }

      public operator fun component2(): Map<Long, ServerShip> {
         return this.partiallyLoadedShips
      }

      public operator fun component3(): Pair<Long, ServerShip> {
         return this.newShip
      }

      public operator fun component4(): Map<Long, Pair<Vector3dc, Vector3dc>> {
         return this.centerPositions
      }

      public operator fun component5(): Map<String, class_2487> {
         return this.saveData
      }

      public fun copy(
         level: class_3218 = this.level,
         partiallyLoadedShips: Map<Long, ServerShip> = this.partiallyLoadedShips,
         newShip: Pair<Long, ServerShip> = this.newShip,
         centerPositions: Map<Long, Pair<Vector3dc, Vector3dc>> = this.centerPositions,
         saveData: Map<String, class_2487> = this.saveData
      ): org.valkyrienskies.mod.common.assembly.VSSchematicEvents.OnPasteBeforeBlocksAreLoaded {
         return VSSchematicEvents.OnPasteBeforeBlocksAreLoaded(level, partiallyLoadedShips, newShip, centerPositions, saveData)
      }

      public override fun toString(): String {
         return "OnPasteBeforeBlocksAreLoaded(level=${this.level}, partiallyLoadedShips=${this.partiallyLoadedShips}, newShip=${this.newShip}, centerPositions=${this.centerPositions}, saveData=${this.saveData})"
      }

      public override fun hashCode(): Int {
         return (((this.level.hashCode() * 31 + this.partiallyLoadedShips.hashCode()) * 31 + this.newShip.hashCode()) * 31 + this.centerPositions.hashCode())
               * 31
            + this.saveData.hashCode()
         }

      public override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is VSSchematicEvents.OnPasteBeforeBlocksAreLoaded
               && this.level == (other as VSSchematicEvents.OnPasteBeforeBlocksAreLoaded).level
               && this.partiallyLoadedShips == (other as VSSchematicEvents.OnPasteBeforeBlocksAreLoaded).partiallyLoadedShips
               && this.newShip == (other as VSSchematicEvents.OnPasteBeforeBlocksAreLoaded).newShip
               && this.centerPositions == (other as VSSchematicEvents.OnPasteBeforeBlocksAreLoaded).centerPositions
               && this.saveData == (other as VSSchematicEvents.OnPasteBeforeBlocksAreLoaded).saveData
            }
      }
   }

   public data class PasteSurvivalCost(saveData: Map<String, class_2487>, addCost: (Map<class_1792, Int>) -> Unit) {
      public final val saveData: Map<String, class_2487>
      public final val addCost: (Map<class_1792, Int>) -> Unit

      init {
         this.saveData = saveData
         this.addCost = addCost
      }

      public operator fun component1(): Map<String, class_2487> {
         return this.saveData
      }

      public operator fun component2(): (Map<class_1792, Int>) -> Unit {
         return this.addCost
      }

      public fun copy(saveData: Map<String, class_2487> = this.saveData, addCost: (Map<class_1792, Int>) -> Unit = this.addCost): org.valkyrienskies.mod.common.assembly.VSSchematicEvents.PasteSurvivalCost {
         return VSSchematicEvents.PasteSurvivalCost(saveData, addCost)
      }

      public override fun toString(): String {
         return "PasteSurvivalCost(saveData=${this.saveData}, addCost=${this.addCost})"
      }

      public override fun hashCode(): Int {
         return this.saveData.hashCode() * 31 + this.addCost.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is VSSchematicEvents.PasteSurvivalCost
               && this.saveData == (other as VSSchematicEvents.PasteSurvivalCost).saveData
               && this.addCost == (other as VSSchematicEvents.PasteSurvivalCost).addCost
            }
      }
   }
}
