package org.valkyrienskies.core.impl.program

import com.fasterxml.jackson.databind.ObjectMapper
import javax.inject.Inject
import org.joml.Matrix3dc
import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.attachment.AttachmentRegistration
import org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.event.ListenableEvent
import org.valkyrienskies.core.api.event.SingleEvent
import org.valkyrienskies.core.api.events.CollisionEvent
import org.valkyrienskies.core.api.events.JointDeserializedEvent
import org.valkyrienskies.core.api.events.JointSerializedEvent
import org.valkyrienskies.core.api.events.MergeEvent
import org.valkyrienskies.core.api.events.PhysTickEvent
import org.valkyrienskies.core.api.events.ShipLoadEvent
import org.valkyrienskies.core.api.events.ShipLoadEventClient
import org.valkyrienskies.core.api.events.ShipUnloadEventClient
import org.valkyrienskies.core.api.events.SplitEvent
import org.valkyrienskies.core.api.events.StartUpdateRenderTransformsEvent
import org.valkyrienskies.core.api.events.TickEndEvent
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.world.ServerShipWorld
import org.valkyrienskies.core.impl.game.ships.ShipObjectClientWorldComponent.Factory
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.core.impl.shadow.EX
import org.valkyrienskies.core.impl.shadow.Fl
import org.valkyrienskies.core.impl.shadow.Fm
import org.valkyrienskies.core.impl.shadow.FP.a
import org.valkyrienskies.core.internal.ShipTeleportData
import org.valkyrienskies.core.internal.VsiCoreClient
import org.valkyrienskies.core.internal.collision.VsiEntityPolygonCollider
import org.valkyrienskies.core.internal.collision.VsiSolidShapeUtils
import org.valkyrienskies.core.internal.config.VsiConfigModel
import org.valkyrienskies.core.internal.hooks.VsiCoreHooks
import org.valkyrienskies.core.internal.physics.blockstates.VsiBlockState
import org.valkyrienskies.core.internal.world.VsiClientShipWorld
import org.valkyrienskies.core.internal.world.VsiPipeline
import org.valkyrienskies.core.internal.world.VsiServerShipWorld
import org.valkyrienskies.core.internal.world.chunks.VsiBlockTypes
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate

public class VSCoreClientImpl @Inject  public constructor(base: VSCoreImpl, shipWorldComponentFactory: Factory) : VSCoreInternal, VsiCoreClient {
   private final val base: VSCoreImpl
   private final val shipWorldComponentFactory: Factory

   init {
      this.base = base
      this.shipWorldComponentFactory = shipWorldComponentFactory
   }

   public open val blockTypes: VsiBlockTypes

   public open var clientUsesUDP: Boolean

   public open val collisionEndEvent: SingleEvent<CollisionEvent>

   public open val collisionPersistEvent: SingleEvent<CollisionEvent>

   public open val collisionStartEvent: SingleEvent<CollisionEvent>

   public open val configurator: Fm

   public open val defaultMapper: ObjectMapper

   public open val dummyShipWorldClient: VsiClientShipWorld

   public open val dummyShipWorldServer: VsiServerShipWorld

   public open val entityPolygonCollider: VsiEntityPolygonCollider

   public open val hooks: EX

   public open val jointDeserializedEvent: SingleEvent<JointDeserializedEvent>

   public open val jointSerializedEvent: SingleEvent<JointSerializedEvent>

   public open val mergeEvent: SingleEvent<MergeEvent>

   public open val networking: Fl

   public open val physTickEvent: SingleEvent<PhysTickEvent>

   public open val pipelineComponentFactory: a

   public open val shipLoadEvent: ListenableEvent<ShipLoadEvent>

   public open val shipLoadEventClient: ListenableEvent<ShipLoadEventClient>

   public open val shipUnloadEventClient: ListenableEvent<ShipUnloadEventClient>

   public open val simplePacketNetworking: SimplePacketNetworking

   public open val solidShapeUtils: VsiSolidShapeUtils

   public open val splitEvent: SingleEvent<SplitEvent>

   public open val startUpdateRenderTransformsEvent: ListenableEvent<StartUpdateRenderTransformsEvent>

   public open val tickEndEvent: ListenableEvent<TickEndEvent>

   public open val transformFactory: org.valkyrienskies.core.api.bodies.properties.BodyTransform.Factory

   public override fun deleteShips(world: ServerShipWorld, ships: List<ServerShip>) {
      this.base.deleteShips(world, ships)
   }

   public override fun getServerConfig(): VsiConfigModel {
      return this.base.getServerConfig()
   }

   @Deprecated(message = "This method isn't tied to a world, which is bad if we want to have different chunk allocation strategies per world in the future.", replaceWith = @ReplaceWith(expression = "ShipWorld.isChunkInShipyard(chunkX, chunkZ, DimensionId)", imports = []))
   public override fun isChunkInShipyard(chunkX: Int, chunkZ: Int): Boolean {
      return this.base.isChunkInShipyard(chunkX, chunkZ)
   }

   @VsBeta
   public override fun <T> newAttachmentRegistrationBuilder(attachmentClass: Class<Any>): Builder<Any> {
      return this.base.newAttachmentRegistrationBuilder(attachmentClass)
   }

   @VsBeta
   public override fun newBodyKinematics(
      velocity: Vector3dc,
      angularVelocity: Vector3dc,
      position: Vector3dc,
      rotation: Quaterniondc,
      scaling: Vector3dc,
      positionInModel: Vector3dc
   ): BodyKinematics {
      return this.base.newBodyKinematics(velocity, angularVelocity, position, rotation, scaling, positionInModel)
   }

   @VsBeta
   public override fun newBodyKinematics(velocity: Vector3dc, angularVelocity: Vector3dc, transform: BodyTransform): BodyKinematics {
      return this.base.newBodyKinematics(velocity, angularVelocity, transform)
   }

   @VsBeta
   public override fun newBodyKinematicsBuilder(): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
      return this.base.newBodyKinematicsBuilder()
   }

   public override fun newBodyTransform(position: Vector3dc, rotation: Quaterniondc, scaling: Vector3dc, positionInModel: Vector3dc): BodyTransform {
      return this.base.newBodyTransform(position, rotation, scaling, positionInModel)
   }

   @VsBeta
   public override fun newBodyTransformBuilder(): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
      return this.base.newBodyTransformBuilder()
   }

   public override fun newChunkClaim(claimX: Int, claimZ: Int): ChunkClaim {
      return this.base.newChunkClaim(claimX, claimZ)
   }

   public override fun newChunkClaimFromChunkPos(chunkX: Int, chunkZ: Int): ChunkClaim {
      return this.base.newChunkClaimFromChunkPos(chunkX, chunkZ)
   }

   public override fun newDeleteTerrainUpdate(chunkX: Int, chunkY: Int, chunkZ: Int): VsiTerrainUpdate {
      return this.base.newDeleteTerrainUpdate(chunkX, chunkY, chunkZ)
   }

   public override fun newDenseTerrainUpdateBuilder(chunkX: Int, chunkY: Int, chunkZ: Int): org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate.Builder {
      return this.base.newDenseTerrainUpdateBuilder(chunkX, chunkY, chunkZ)
   }

   public override fun newEmptyVoxelShapeUpdate(chunkX: Int, chunkY: Int, chunkZ: Int, overwrite: Boolean): VsiTerrainUpdate {
      return this.base.newEmptyVoxelShapeUpdate(chunkX, chunkY, chunkZ, overwrite)
   }

   public override fun newLiquidStateBuilder(): org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder {
      return this.base.newLiquidStateBuilder()
   }

   public override fun newPipeline(): VsiPipeline {
      return this.base.newPipeline()
   }

   public override fun newPipeline(data: ByteArray): VsiPipeline {
      return this.base.newPipeline(data)
   }

   public override fun newPipelineLegacyData(queryableShipDataBytes: ByteArray, chunkAllocatorBytes: ByteArray): VsiPipeline {
      return this.base.newPipelineLegacyData(queryableShipDataBytes, chunkAllocatorBytes)
   }

   public override fun newShipInertiaData(centerOfMassInShip: Vector3dc, mass: Double, momentOfInertiaTensor: Matrix3dc): ShipInertiaData {
      return this.base.newShipInertiaData(centerOfMassInShip, mass, momentOfInertiaTensor)
   }

   public override fun newShipTeleportData(
      newPos: Vector3dc,
      newRot: Quaterniondc,
      newVel: Vector3dc,
      newOmega: Vector3dc,
      newDimension: String?,
      newScale: Double?,
      newPosInShip: Vector3dc?
   ): ShipTeleportData {
      return this.base.newShipTeleportData(newPos, newRot, newVel, newOmega, newDimension, newScale, newPosInShip)
   }

   public override fun newShipTransform(positionInWorld: Vector3dc, positionInShip: Vector3dc, shipToWorldRotation: Quaterniondc, shipToWorldScaling: Vector3dc): ShipTransform {
      return this.base.newShipTransform(positionInWorld, positionInShip, shipToWorldRotation, shipToWorldScaling)
   }

   public override fun newSolidStateBoxesShapeBuilder(): org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder {
      return this.base.newSolidStateBoxesShapeBuilder()
   }

   public override fun newSolidStateBuilder(): org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder {
      return this.base.newSolidStateBuilder()
   }

   public override fun newSparseTerrainUpdateBuilder(chunkX: Int, chunkY: Int, chunkZ: Int): org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate.Builder {
      return this.base.newSparseTerrainUpdateBuilder(chunkX, chunkY, chunkZ)
   }

   @VsBeta
   public override fun <T> registerAttachment(attachmentClass: Class<Any>) {
      this.base.registerAttachment(attachmentClass)
   }

   @VsBeta
   public override fun <T> registerAttachment(attachmentClass: Class<Any>, registrationBuilder: (Builder<Any>) -> Unit) {
      this.base.registerAttachment(attachmentClass, registrationBuilder)
   }

   @VsBeta
   public override fun <T> registerAttachment(registration: AttachmentRegistration<Any>) {
      this.base.registerAttachment(registration)
   }

   @VsBeta
   public override fun registerAttachmentForRemoval(attachmentKey: String) {
      this.base.registerAttachmentForRemoval(attachmentKey)
   }

   public override fun registerBlockStates(states: Iterable<VsiBlockState>) {
      this.base.registerBlockStates(states)
   }

   public override fun renameShip(ship: ServerShip, newName: String) {
      this.base.renameShip(ship, newName)
   }

   public override fun scaleShip(world: ServerShipWorld, ship: ServerShip, newScale: Double) {
      this.base.scaleShip(world, ship, newScale)
   }

   public override fun serializePipeline(pipeline: VsiPipeline): ByteArray {
      return this.base.serializePipeline(pipeline)
   }

   public override fun teleportShip(world: ServerShipWorld, ship: ServerShip, teleportData: ShipTeleportData) {
      this.base.teleportShip(world, ship, teleportData)
   }

   public override fun newShipWorldClient(): VsiClientShipWorld {
      return this.shipWorldComponentFactory.newShipObjectClientWorldComponent().newWorld()
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getCollisionEndEvent(): ListenableEvent {
      this.collisionEndEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getCollisionPersistEvent(): ListenableEvent {
      this.collisionPersistEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getCollisionStartEvent(): ListenableEvent {
      this.collisionStartEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getHooks(): VsiCoreHooks {
      this.hooks as VsiCoreHooks
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getJointDeserializedEvent(): ListenableEvent {
      this.jointDeserializedEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getJointSerializedEvent(): ListenableEvent {
      this.jointSerializedEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getMergeEvent(): ListenableEvent {
      this.mergeEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getPhysTickEvent(): ListenableEvent {
      this.physTickEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getSplitEvent(): ListenableEvent {
      this.splitEvent as ListenableEvent
   }
}
