package org.valkyrienskies.mod.common.hooks

import it.unimi.dsi.fastutil.objects.ObjectList
import net.minecraft.class_1921
import net.minecraft.class_4587
import net.minecraft.class_5568
import net.minecraft.class_5572
import net.minecraft.class_761
import org.joml.Matrix4f
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.core.util.events.EventEmitterImpl
import org.valkyrienskies.mod.common.config.ConfigType

public object VSGameEvents {
   public final val registriesCompleted: EventEmitterImpl<Unit> = EventEmitterImpl()
   public final val tagsAreLoaded: EventEmitterImpl<Unit> = EventEmitterImpl()
   public final val configUpdated: EventEmitterImpl<Set<org.valkyrienskies.mod.common.hooks.VSGameEvents.ConfigUpdateEntry>> = EventEmitterImpl()
   public final val renderShip: EventEmitterImpl<org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipRenderEvent> = EventEmitterImpl()
   public final val postRenderShip: EventEmitterImpl<org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipRenderEvent> = EventEmitterImpl()
   public final val entitySectionSetShip: EventEmitterImpl<org.valkyrienskies.mod.common.hooks.VSGameEvents.EntitySectionSetShip> = EventEmitterImpl()
   public final val shipsStartRendering: EventEmitterImpl<org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipStartRenderEvent> = EventEmitterImpl()
   public final val renderShipSodium: EventEmitterImpl<org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipRenderEventSodium> = EventEmitterImpl()
   public final val postRenderShipSodium: EventEmitterImpl<org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipRenderEventSodium> = EventEmitterImpl()
   public final val shipsStartRenderingSodium: EventEmitterImpl<org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipStartRenderEventSodium> =
      EventEmitterImpl()
      public final val shipSplit: EventEmitterImpl<org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipSplitEvent> = EventEmitterImpl()

   public data class ConfigUpdateEntry(configType: ConfigType, category: List<String>, name: String) {
      public final val configType: ConfigType
      public final val category: List<String>
      public final val name: String

      init {
         this.configType = configType
         this.category = category
         this.name = name
      }

      public final val path: String
         public final get() {
            return CollectionsKt.joinToString$default(CollectionsKt.plus(this.category, this.name), ".", null, null, 0, null, null, 62, null)
         }


      public operator fun component1(): ConfigType {
         return this.configType
      }

      public operator fun component2(): List<String> {
         return this.category
      }

      public operator fun component3(): String {
         return this.name
      }

      public fun copy(configType: ConfigType = this.configType, category: List<String> = this.category, name: String = this.name): org.valkyrienskies.mod.common.hooks.VSGameEvents.ConfigUpdateEntry {
         return VSGameEvents.ConfigUpdateEntry(configType, category, name)
      }

      public override fun toString(): String {
         return "ConfigUpdateEntry(configType=${this.configType}, category=${this.category}, name=${this.name})"
      }

      public override fun hashCode(): Int {
         return (this.configType.hashCode() * 31 + this.category.hashCode()) * 31 + this.name.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is VSGameEvents.ConfigUpdateEntry
               && this.configType === (other as VSGameEvents.ConfigUpdateEntry).configType
               && this.category == (other as VSGameEvents.ConfigUpdateEntry).category
               && this.name == (other as VSGameEvents.ConfigUpdateEntry).name
            }
      }
   }

   public data class EntitySectionSetShip(section: class_5572<class_5568>, ship: Ship) {
      public final val section: class_5572<class_5568>
      public final val ship: Ship

      init {
         this.section = section
         this.ship = ship
      }

      public operator fun component1(): class_5572<class_5568> {
         return this.section
      }

      public operator fun component2(): Ship {
         return this.ship
      }

      public fun copy(section: class_5572<class_5568> = this.section, ship: Ship = this.ship): org.valkyrienskies.mod.common.hooks.VSGameEvents.EntitySectionSetShip {
         return VSGameEvents.EntitySectionSetShip(section, ship)
      }

      public override fun toString(): String {
         return "EntitySectionSetShip(section=${this.section}, ship=${this.ship})"
      }

      public override fun hashCode(): Int {
         return this.section.hashCode() * 31 + this.ship.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is VSGameEvents.EntitySectionSetShip
               && this.section == (other as VSGameEvents.EntitySectionSetShip).section
               && this.ship == (other as VSGameEvents.EntitySectionSetShip).ship
            }
      }
   }

   public data class ShipRenderEvent(renderer: class_761,
      renderType: class_1921,
      poseStack: class_4587,
      camX: Double,
      camY: Double,
      camZ: Double,
      projectionMatrix: Matrix4f,
      ship: ClientShip,
      chunks: ObjectList<*>
   ) {
      public final val renderer: class_761
      public final val renderType: class_1921
      public final val poseStack: class_4587
      public final val camX: Double
      public final val camY: Double
      public final val camZ: Double
      public final val projectionMatrix: Matrix4f
      public final val ship: ClientShip
      public final val chunks: ObjectList<*>

      init {
         this.renderer = renderer
         this.renderType = renderType
         this.poseStack = poseStack
         this.camX = camX
         this.camY = camY
         this.camZ = camZ
         this.projectionMatrix = projectionMatrix
         this.ship = ship
         this.chunks = chunks
      }

      public operator fun component1(): class_761 {
         return this.renderer
      }

      public operator fun component2(): class_1921 {
         return this.renderType
      }

      public operator fun component3(): class_4587 {
         return this.poseStack
      }

      public operator fun component4(): Double {
         return this.camX
      }

      public operator fun component5(): Double {
         return this.camY
      }

      public operator fun component6(): Double {
         return this.camZ
      }

      public operator fun component7(): Matrix4f {
         return this.projectionMatrix
      }

      public operator fun component8(): ClientShip {
         return this.ship
      }

      public operator fun component9(): ObjectList<*> {
         return this.chunks
      }

      public fun copy(
         renderer: class_761 = this.renderer,
         renderType: class_1921 = this.renderType,
         poseStack: class_4587 = this.poseStack,
         camX: Double = this.camX,
         camY: Double = this.camY,
         camZ: Double = this.camZ,
         projectionMatrix: Matrix4f = this.projectionMatrix,
         ship: ClientShip = this.ship,
         chunks: ObjectList<*> = this.chunks
      ): org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipRenderEvent {
         return VSGameEvents.ShipRenderEvent(renderer, renderType, poseStack, camX, camY, camZ, projectionMatrix, ship, chunks)
      }

      public override fun toString(): String {
         return "ShipRenderEvent(renderer=${this.renderer}, renderType=${this.renderType}, poseStack=${this.poseStack}, camX=${this.camX}, camY=${this.camY}, camZ=${this.camZ}, projectionMatrix=${this.projectionMatrix}, ship=${this.ship}, chunks=${this.chunks})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             (
                                                      ((this.renderer.hashCode() * 31 + this.renderType.hashCode()) * 31 + this.poseStack.hashCode()) * 31
                                                         + java.lang.Double.hashCode(this.camX)
                                                   )
                                                   * 31
                                                + java.lang.Double.hashCode(this.camY)
                                          )
                                          * 31
                                       + java.lang.Double.hashCode(this.camZ)
                                 )
                                 * 31
                              + this.projectionMatrix.hashCode()
                        )
                        * 31
                     + this.ship.hashCode()
               )
               * 31
            + this.chunks.hashCode()
         }

      public override operator fun equals(other: Any?): Boolean {
         label70@
         if (this === other) {
            return true
         } else {
            return other is VSGameEvents.ShipRenderEvent
               && this.renderer == (other as VSGameEvents.ShipRenderEvent).renderer
               && this.renderType == (other as VSGameEvents.ShipRenderEvent).renderType
               && this.poseStack == (other as VSGameEvents.ShipRenderEvent).poseStack
               && java.lang.Double.compare(this.camX, (other as VSGameEvents.ShipRenderEvent).camX) == 0
               && java.lang.Double.compare(this.camY, (other as VSGameEvents.ShipRenderEvent).camY) == 0
               && java.lang.Double.compare(this.camZ, (other as VSGameEvents.ShipRenderEvent).camZ) == 0
               && this.projectionMatrix == (other as VSGameEvents.ShipRenderEvent).projectionMatrix
               && this.ship == (other as VSGameEvents.ShipRenderEvent).ship
               && this.chunks == (other as VSGameEvents.ShipRenderEvent).chunks
            }
      }
   }

   public data class ShipRenderEventSodium(pass: Any, matrices: Any, camX: Double, camY: Double, camZ: Double, ship: ClientShip, chunks: Any) {
      public final val pass: Any
      public final val matrices: Any
      public final val camX: Double
      public final val camY: Double
      public final val camZ: Double
      public final val ship: ClientShip
      public final val chunks: Any

      init {
         this.pass = pass
         this.matrices = matrices
         this.camX = camX
         this.camY = camY
         this.camZ = camZ
         this.ship = ship
         this.chunks = chunks
      }

      public operator fun component1(): Any {
         return this.pass
      }

      public operator fun component2(): Any {
         return this.matrices
      }

      public operator fun component3(): Double {
         return this.camX
      }

      public operator fun component4(): Double {
         return this.camY
      }

      public operator fun component5(): Double {
         return this.camZ
      }

      public operator fun component6(): ClientShip {
         return this.ship
      }

      public operator fun component7(): Any {
         return this.chunks
      }

      public fun copy(
         pass: Any = this.pass,
         matrices: Any = this.matrices,
         camX: Double = this.camX,
         camY: Double = this.camY,
         camZ: Double = this.camZ,
         ship: ClientShip = this.ship,
         chunks: Any = this.chunks
      ): org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipRenderEventSodium {
         return VSGameEvents.ShipRenderEventSodium(pass, matrices, camX, camY, camZ, ship, chunks)
      }

      public override fun toString(): String {
         return "ShipRenderEventSodium(pass=${this.pass}, matrices=${this.matrices}, camX=${this.camX}, camY=${this.camY}, camZ=${this.camZ}, ship=${this.ship}, chunks=${this.chunks})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    ((this.pass.hashCode() * 31 + this.matrices.hashCode()) * 31 + java.lang.Double.hashCode(this.camX)) * 31
                                       + java.lang.Double.hashCode(this.camY)
                                 )
                                 * 31
                              + java.lang.Double.hashCode(this.camZ)
                        )
                        * 31
                     + this.ship.hashCode()
               )
               * 31
            + this.chunks.hashCode()
         }

      public override operator fun equals(other: Any?): Boolean {
         label58@
         if (this === other) {
            return true
         } else {
            return other is VSGameEvents.ShipRenderEventSodium
               && this.pass == (other as VSGameEvents.ShipRenderEventSodium).pass
               && this.matrices == (other as VSGameEvents.ShipRenderEventSodium).matrices
               && java.lang.Double.compare(this.camX, (other as VSGameEvents.ShipRenderEventSodium).camX) == 0
               && java.lang.Double.compare(this.camY, (other as VSGameEvents.ShipRenderEventSodium).camY) == 0
               && java.lang.Double.compare(this.camZ, (other as VSGameEvents.ShipRenderEventSodium).camZ) == 0
               && this.ship == (other as VSGameEvents.ShipRenderEventSodium).ship
               && this.chunks == (other as VSGameEvents.ShipRenderEventSodium).chunks
            }
      }
   }

   public data class ShipSplitEvent(ship: Long, newShip: Long, blocks: DenseBlockPosSet) {
      public final val ship: Long
      public final val newShip: Long
      public final val blocks: DenseBlockPosSet

      init {
         this.ship = ship
         this.newShip = newShip
         this.blocks = blocks
      }

      public operator fun component1(): Long {
         return this.ship
      }

      public operator fun component2(): Long {
         return this.newShip
      }

      public operator fun component3(): DenseBlockPosSet {
         return this.blocks
      }

      public fun copy(ship: Long = this.ship, newShip: Long = this.newShip, blocks: DenseBlockPosSet = this.blocks): org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipSplitEvent {
         return VSGameEvents.ShipSplitEvent(ship, newShip, blocks)
      }

      public override fun toString(): String {
         return "ShipSplitEvent(ship=${this.ship}, newShip=${this.newShip}, blocks=${this.blocks})"
      }

      public override fun hashCode(): Int {
         return (java.lang.Long.hashCode(this.ship) * 31 + java.lang.Long.hashCode(this.newShip)) * 31 + this.blocks.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is VSGameEvents.ShipSplitEvent
               && this.ship == (other as VSGameEvents.ShipSplitEvent).ship
               && this.newShip == (other as VSGameEvents.ShipSplitEvent).newShip
               && this.blocks == (other as VSGameEvents.ShipSplitEvent).blocks
            }
      }
   }

   public data class ShipStartRenderEvent(renderer: class_761,
      renderType: class_1921,
      poseStack: class_4587,
      camX: Double,
      camY: Double,
      camZ: Double,
      projectionMatrix: Matrix4f
   ) {
      public final val renderer: class_761
      public final val renderType: class_1921
      public final val poseStack: class_4587
      public final val camX: Double
      public final val camY: Double
      public final val camZ: Double
      public final val projectionMatrix: Matrix4f

      init {
         this.renderer = renderer
         this.renderType = renderType
         this.poseStack = poseStack
         this.camX = camX
         this.camY = camY
         this.camZ = camZ
         this.projectionMatrix = projectionMatrix
      }

      public operator fun component1(): class_761 {
         return this.renderer
      }

      public operator fun component2(): class_1921 {
         return this.renderType
      }

      public operator fun component3(): class_4587 {
         return this.poseStack
      }

      public operator fun component4(): Double {
         return this.camX
      }

      public operator fun component5(): Double {
         return this.camY
      }

      public operator fun component6(): Double {
         return this.camZ
      }

      public operator fun component7(): Matrix4f {
         return this.projectionMatrix
      }

      public fun copy(
         renderer: class_761 = this.renderer,
         renderType: class_1921 = this.renderType,
         poseStack: class_4587 = this.poseStack,
         camX: Double = this.camX,
         camY: Double = this.camY,
         camZ: Double = this.camZ,
         projectionMatrix: Matrix4f = this.projectionMatrix
      ): org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipStartRenderEvent {
         return VSGameEvents.ShipStartRenderEvent(renderer, renderType, poseStack, camX, camY, camZ, projectionMatrix)
      }

      public override fun toString(): String {
         return "ShipStartRenderEvent(renderer=${this.renderer}, renderType=${this.renderType}, poseStack=${this.poseStack}, camX=${this.camX}, camY=${this.camY}, camZ=${this.camZ}, projectionMatrix=${this.projectionMatrix})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    ((this.renderer.hashCode() * 31 + this.renderType.hashCode()) * 31 + this.poseStack.hashCode()) * 31
                                       + java.lang.Double.hashCode(this.camX)
                                 )
                                 * 31
                              + java.lang.Double.hashCode(this.camY)
                        )
                        * 31
                     + java.lang.Double.hashCode(this.camZ)
               )
               * 31
            + this.projectionMatrix.hashCode()
         }

      public override operator fun equals(other: Any?): Boolean {
         label58@
         if (this === other) {
            return true
         } else {
            return other is VSGameEvents.ShipStartRenderEvent
               && this.renderer == (other as VSGameEvents.ShipStartRenderEvent).renderer
               && this.renderType == (other as VSGameEvents.ShipStartRenderEvent).renderType
               && this.poseStack == (other as VSGameEvents.ShipStartRenderEvent).poseStack
               && java.lang.Double.compare(this.camX, (other as VSGameEvents.ShipStartRenderEvent).camX) == 0
               && java.lang.Double.compare(this.camY, (other as VSGameEvents.ShipStartRenderEvent).camY) == 0
               && java.lang.Double.compare(this.camZ, (other as VSGameEvents.ShipStartRenderEvent).camZ) == 0
               && this.projectionMatrix == (other as VSGameEvents.ShipStartRenderEvent).projectionMatrix
            }
      }
   }

   public data class ShipStartRenderEventSodium(pass: Any, matrices: Any, camX: Double, camY: Double, camZ: Double) {
      public final val pass: Any
      public final val matrices: Any
      public final val camX: Double
      public final val camY: Double
      public final val camZ: Double

      init {
         this.pass = pass
         this.matrices = matrices
         this.camX = camX
         this.camY = camY
         this.camZ = camZ
      }

      public operator fun component1(): Any {
         return this.pass
      }

      public operator fun component2(): Any {
         return this.matrices
      }

      public operator fun component3(): Double {
         return this.camX
      }

      public operator fun component4(): Double {
         return this.camY
      }

      public operator fun component5(): Double {
         return this.camZ
      }

      public fun copy(pass: Any = this.pass, matrices: Any = this.matrices, camX: Double = this.camX, camY: Double = this.camY, camZ: Double = this.camZ): org.valkyrienskies.mod.common.hooks.VSGameEvents.ShipStartRenderEventSodium {
         return VSGameEvents.ShipStartRenderEventSodium(pass, matrices, camX, camY, camZ)
      }

      public override fun toString(): String {
         return "ShipStartRenderEventSodium(pass=${this.pass}, matrices=${this.matrices}, camX=${this.camX}, camY=${this.camY}, camZ=${this.camZ})"
      }

      public override fun hashCode(): Int {
         return (
                  ((this.pass.hashCode() * 31 + this.matrices.hashCode()) * 31 + java.lang.Double.hashCode(this.camX)) * 31
                     + java.lang.Double.hashCode(this.camY)
               )
               * 31
            + java.lang.Double.hashCode(this.camZ)
         }

      public override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is VSGameEvents.ShipStartRenderEventSodium
               && this.pass == (other as VSGameEvents.ShipStartRenderEventSodium).pass
               && this.matrices == (other as VSGameEvents.ShipStartRenderEventSodium).matrices
               && java.lang.Double.compare(this.camX, (other as VSGameEvents.ShipStartRenderEventSodium).camX) == 0
               && java.lang.Double.compare(this.camY, (other as VSGameEvents.ShipStartRenderEventSodium).camY) == 0
               && java.lang.Double.compare(this.camZ, (other as VSGameEvents.ShipStartRenderEventSodium).camZ) == 0
            }
      }
   }
}
