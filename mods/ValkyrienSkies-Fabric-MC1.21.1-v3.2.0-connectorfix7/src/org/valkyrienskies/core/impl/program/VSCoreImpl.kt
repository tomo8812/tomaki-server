package org.valkyrienskies.core.impl.program

import com.fasterxml.jackson.databind.ObjectMapper
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.jvm.internal.MutablePropertyReference0
import kotlin.jvm.internal.MutablePropertyReference0Impl
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import org.apache.logging.log4j.Logger
import org.joml.Matrix3d
import org.joml.Matrix3dc
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.attachment.AttachmentRegistration
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.bodies.properties.BodyTransform.Factory
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
import org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape
import org.valkyrienskies.core.api.physics.blockstates.LiquidState
import org.valkyrienskies.core.api.physics.blockstates.SolidState
import org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.world.ServerShipWorld
import org.valkyrienskies.core.impl.api_impl.config.VsiConfigModelImpl
import org.valkyrienskies.core.impl.bodies.properties.BodyKinematicsFactory
import org.valkyrienskies.core.impl.bodies.properties.BodyTransformFactory
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.core.impl.game.ChunkClaimImpl
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl
import org.valkyrienskies.core.impl.game.ships.ShipInertiaDataImpl
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.VSPipelineSerializer
import org.valkyrienskies.core.impl.hooks.VSEvents
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.core.impl.shadow.CF
import org.valkyrienskies.core.impl.shadow.CH
import org.valkyrienskies.core.impl.shadow.CP
import org.valkyrienskies.core.impl.shadow.DN
import org.valkyrienskies.core.impl.shadow.DS
import org.valkyrienskies.core.impl.shadow.Dk
import org.valkyrienskies.core.impl.shadow.EI
import org.valkyrienskies.core.impl.shadow.ET
import org.valkyrienskies.core.impl.shadow.EV
import org.valkyrienskies.core.impl.shadow.EW
import org.valkyrienskies.core.impl.shadow.EX
import org.valkyrienskies.core.impl.shadow.Eh
import org.valkyrienskies.core.impl.shadow.Ei
import org.valkyrienskies.core.impl.shadow.Ej
import org.valkyrienskies.core.impl.shadow.Ey
import org.valkyrienskies.core.impl.shadow.FN
import org.valkyrienskies.core.impl.shadow.FQ
import org.valkyrienskies.core.impl.shadow.Fb
import org.valkyrienskies.core.impl.shadow.Fl
import org.valkyrienskies.core.impl.shadow.Fm
import org.valkyrienskies.core.impl.shadow.Im
import org.valkyrienskies.core.impl.shadow.Io
import org.valkyrienskies.core.impl.shadow.Ix
import org.valkyrienskies.core.impl.shadow.FP.a
import org.valkyrienskies.core.impl.util.serialization.VSJacksonUtil
import org.valkyrienskies.core.internal.ShipTeleportData
import org.valkyrienskies.core.internal.VsiCoreCommands
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
import org.valkyrienskies.core.util.events.EventEmitterImpl

@Singleton
public class VSCoreImpl @Inject  public constructor(networking: Fl,
      hooks: EX,
      configurator: Fm,
      tcp: Fb,
      pipelineComponentFactory: a,
      pipelineSerializer: VSPipelineSerializer,
      blockTypes: DN,
      simplePacketNetworking: SimplePacketNetworking,
      solidShapeUtils: VsiSolidShapeUtils,
      newLiquidStateBuilder: Provider<Builder>,
      newSolidStateBuilder: Provider<org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder>,
      newBoxesBlockShapeBuilder: Provider<org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder>,
      attachmentRegistry: CH
   ) :
   VSCoreInternal,
   VsiCoreCommands {
   public open val networking: Fl
   public open val hooks: EX
   public open val configurator: Fm
   public open val pipelineComponentFactory: a
   private final val pipelineSerializer: VSPipelineSerializer
   public open val blockTypes: DN
   public open val simplePacketNetworking: SimplePacketNetworking
   public open val solidShapeUtils: VsiSolidShapeUtils
   private final val newLiquidStateBuilder: Provider<Builder>
   private final val newSolidStateBuilder: Provider<org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder>
   private final val newBoxesBlockShapeBuilder: Provider<org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder>
   private final val attachmentRegistry: CH
   @JvmSynthetic
   private DS $$delegate_0;

   @VsBeta
   public open val collisionStartEvent: SingleEvent<CollisionEvent>

   @VsBeta
   public open val collisionEndEvent: SingleEvent<CollisionEvent>

   @VsBeta
   public open val collisionPersistEvent: SingleEvent<CollisionEvent>

   @VsBeta
   public open val physTickEvent: SingleEvent<PhysTickEvent>

   @VsBeta
   public open val jointSerializedEvent: SingleEvent<JointSerializedEvent>

   @VsBeta
   public open val jointDeserializedEvent: SingleEvent<JointDeserializedEvent>

   @VsBeta
   public open val mergeEvent: SingleEvent<MergeEvent>

   @VsBeta
   public open val splitEvent: SingleEvent<SplitEvent>

   @Deprecated(message = "Surely we can do better than this")
   public open var clientUsesUDP: Boolean
      public open get() {
         val var1: Fl = this.clientUsesUDP$receiver
         return this.clientUsesUDP$receiver.d
      }

      public open set(<set-?>) {
         val var3: Fl = this.clientUsesUDP$receiver
         this.clientUsesUDP$receiver.d = `<set-?>`
      }


   init {
      this.networking = networking
      this.hooks = hooks
      this.configurator = configurator
      this.pipelineComponentFactory = pipelineComponentFactory
      this.pipelineSerializer = pipelineSerializer
      this.blockTypes = blockTypes
      this.simplePacketNetworking = simplePacketNetworking
      this.solidShapeUtils = solidShapeUtils
      this.newLiquidStateBuilder = newLiquidStateBuilder
      this.newSolidStateBuilder = newSolidStateBuilder
      this.newBoxesBlockShapeBuilder = newBoxesBlockShapeBuilder
      this.attachmentRegistry = attachmentRegistry
      this.$$delegate_0 = DS.INSTANCE
      this.configurator.configure(tcp)
      this.networking.e()
      this.configureAttachments()
      this.collisionStartEvent = EventEmitterImpl<>()
      this.collisionEndEvent = EventEmitterImpl<>()
      this.collisionPersistEvent = EventEmitterImpl<>()
      this.physTickEvent = EventEmitterImpl<>()
      this.jointSerializedEvent = EventEmitterImpl<>()
      this.jointDeserializedEvent = EventEmitterImpl<>()
      this.mergeEvent = EventEmitterImpl<>()
      this.splitEvent = EventEmitterImpl<>()
      this.clientUsesUDP$receiver = this.networking
   }

   public override fun deleteShips(world: ServerShipWorld, ships: List<ServerShip>) {
      this.$$delegate_0.deleteShips(world, ships)
   }

   public override fun renameShip(ship: ServerShip, newName: String) {
      this.$$delegate_0.renameShip(ship, newName)
   }

   public override fun scaleShip(world: ServerShipWorld, ship: ServerShip, newScale: Double) {
      this.$$delegate_0.scaleShip(world, ship, newScale)
   }

   public override fun teleportShip(world: ServerShipWorld, ship: ServerShip, teleportData: ShipTeleportData) {
      this.$$delegate_0.teleportShip(world, ship, teleportData)
   }

   private fun configureAttachments() {
      this.registerAttachment(Ey::class.java, {
         `$this$registerAttachment`.useTransientSerializer()
      })
      this.registerAttachment(Eh::class.java, {
         `$this$registerAttachment`.useTransientSerializer()
      })
   }

   public open val transformFactory: Factory
      public open get() {
         return BodyTransformFactory.INSTANCE
      }


   @VsBeta
   public open val shipLoadEvent: ListenableEvent<ShipLoadEvent>
      public open get() {
         return VSEvents.INSTANCE.shipLoadEvent
      }


   @VsBeta
   public open val shipLoadEventClient: ListenableEvent<ShipLoadEventClient>
      public open get() {
         return VSEvents.INSTANCE.shipLoadEventClient
      }


   @VsBeta
   public open val shipUnloadEventClient: ListenableEvent<ShipUnloadEventClient>
      public open get() {
         return VSEvents.INSTANCE.shipUnloadEventClient
      }


   @VsBeta
   public open val startUpdateRenderTransformsEvent: ListenableEvent<StartUpdateRenderTransformsEvent>
      public open get() {
         return VSEvents.INSTANCE.startUpdateRenderTransformsEvent
      }


   @VsBeta
   public open val tickEndEvent: ListenableEvent<TickEndEvent>
      public open get() {
         return VSEvents.INSTANCE.tickEndEvent
      }


   @VsBeta
   public override fun newBodyKinematics(velocity: Vector3dc, angularVelocity: Vector3dc, transform: BodyTransform): BodyKinematics {
      return BodyKinematicsFactory.INSTANCE.create(velocity, angularVelocity, transform)
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
      return BodyKinematicsFactory.INSTANCE.create(velocity, angularVelocity, position, rotation, scaling, positionInModel)
   }

   @VsBeta
   public override fun newBodyKinematicsBuilder(): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
      return BodyKinematicsFactory.INSTANCE.newBuilder()
   }

   public override fun newBodyTransform(position: Vector3dc, rotation: Quaterniondc, scaling: Vector3dc, positionInModel: Vector3dc): BodyTransform {
      return this.transformFactory.create(position, rotation, scaling, positionInModel)
   }

   @VsBeta
   public override fun newBodyTransformBuilder(): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
      return this.transformFactory.newBuilder()
   }

   public open val entityPolygonCollider: VsiEntityPolygonCollider
      public open get() {
         return Dk.INSTANCE
      }


   public open val dummyShipWorldClient: VsiClientShipWorld
      public open get() {
         return Ei.INSTANCE
      }


   public open val dummyShipWorldServer: VsiServerShipWorld
      public open get() {
         return Ej.INSTANCE
      }


   public override fun registerBlockStates(states: Iterable<VsiBlockState>) {
      val var2: CP = this.blockTypes.b

      for (var3 in states) {
         var2.b(var3)
      }

      var2.h = true
   }

   public override fun newEmptyVoxelShapeUpdate(chunkX: Int, chunkY: Int, chunkZ: Int, overwrite: Boolean): VsiTerrainUpdate {
      return EW(Io(chunkX, chunkY, chunkZ, overwrite))
   }

   public override fun newDeleteTerrainUpdate(chunkX: Int, chunkY: Int, chunkZ: Int): VsiTerrainUpdate {
      return EW(Im(chunkX, chunkY, chunkZ))
   }

   public override fun newDenseTerrainUpdateBuilder(chunkX: Int, chunkY: Int, chunkZ: Int): org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate.Builder {
      return ET(chunkX, chunkY, chunkZ)
   }

   public override fun newSparseTerrainUpdateBuilder(chunkX: Int, chunkY: Int, chunkZ: Int): org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate.Builder {
      return EV(chunkX, chunkY, chunkZ)
   }

   public open fun newPipelineLegacyData(queryableShipDataBytes: ByteArray, chunkAllocatorBytes: ByteArray): FQ {
      return this.fromModule(this.pipelineSerializer.deserializeLegacy(queryableShipDataBytes, chunkAllocatorBytes))
   }

   public open fun newPipeline(): FQ {
      return this.fromModule(EI.a.a())
   }

   public open fun newPipeline(data: ByteArray): FQ {
      return this.fromModule(this.pipelineSerializer.deserialize(data))
   }

   internal fun `newVSPhysicsPipelineStage$impl`(): FN? {
      Companion.loadKrunch()
      return this.pipelineComponentFactory.newPipelineComponent(EI.a.a()).newPhysicsStage()
   }

   public override fun serializePipeline(pipeline: VsiPipeline): ByteArray {
      return this.pipelineSerializer.serialize(pipeline as FQ)
   }

   public override fun newChunkClaim(claimX: Int, claimZ: Int): ChunkClaim {
      return ChunkClaimImpl(claimX, claimZ)
   }

   public override fun newChunkClaimFromChunkPos(chunkX: Int, chunkZ: Int): ChunkClaim {
      return ChunkClaimImpl(ChunkClaim.Companion.getClaimXIndex(chunkX), ChunkClaim.Companion.getClaimZIndex(chunkZ))
   }

   public override fun newSolidStateBuilder(): org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder {
      val var10000: Any = this.newSolidStateBuilder.get()
      return var10000 as SolidState.Builder
   }

   public override fun newLiquidStateBuilder(): Builder {
      val var10000: Any = this.newLiquidStateBuilder.get()
      return var10000 as LiquidState.Builder
   }

   public override fun newSolidStateBoxesShapeBuilder(): org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder {
      val var10000: Any = this.newBoxesBlockShapeBuilder.get()
      return var10000 as BoxesBlockShape.Builder
   }

   public override fun newShipTransform(positionInWorld: Vector3dc, positionInShip: Vector3dc, shipToWorldRotation: Quaterniondc, shipToWorldScaling: Vector3dc): ShipTransform {
      return ShipTransformImpl.Companion.create(positionInWorld, positionInShip, shipToWorldRotation, shipToWorldScaling)
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
      return ShipTeleportDataImpl(newPos, newRot, newVel, newOmega, newDimension, newScale, newPosInShip)
   }

   public override fun newShipInertiaData(centerOfMassInShip: Vector3dc, mass: Double, momentOfInertiaTensor: Matrix3dc): ShipInertiaData {
      return ShipInertiaDataImpl(Vector3d(centerOfMassInShip), mass, Matrix3d(momentOfInertiaTensor))
   }

   public override fun getServerConfig(): VsiConfigModel {
      return VsiConfigModelImpl.Companion.build(VSCoreConfig.SERVER)
   }

   public open val defaultMapper: ObjectMapper
      public open get() {
         return VSJacksonUtil.INSTANCE.defaultMapper
      }


   @Deprecated(message = "This method isn't tied to a world, which is bad if we want to have different chunk allocation strategies per world in the future.", replaceWith = @ReplaceWith(expression = "ShipWorld.isChunkInShipyard(chunkX, chunkZ, DimensionId)", imports = []))
   public override fun isChunkInShipyard(chunkX: Int, chunkZ: Int): Boolean {
      chunkX = ChunkClaim.Companion.getClaimXIndex(chunkX)
      chunkZ = ChunkClaim.Companion.getClaimZIndex(chunkZ)
      return (-7000 <= chunkX && chunkX < 7001) and (3000 <= chunkZ && chunkZ < 7001)
   }

   @JvmStatic
   fun `getClientUsesUDP$delegate`(`<this>`: VSCoreImpl) {
      Reflection.mutableProperty0(
         MutablePropertyReference0Impl(`<this>`.clientUsesUDP$receiver, Fl.class, "clientUsesUDP", "getClientUsesUDP()Z", 0) as MutablePropertyReference0
      )
   }

   public override fun <T> newAttachmentRegistrationBuilder(attachmentClass: Class<Any>): org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder<
         Any
      > {
      return CF(attachmentClass)
   }

   public override fun <T> registerAttachment(registration: AttachmentRegistration<Any>) {
      this.attachmentRegistry.a(registration)
   }

   public override fun <T> registerAttachment(
      attachmentClass: Class<Any>,
      registrationBuilder: (org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder<Any>) -> Unit
   ) {
      this.attachmentRegistry.a(attachmentClass, registrationBuilder)
   }

   public override fun registerAttachmentForRemoval(attachmentKey: String) {
      this.attachmentRegistry.a(attachmentKey)
   }

   private fun fromModule(module: EI): FQ {
      Companion.loadKrunch()
      return this.pipelineComponentFactory.newPipelineComponent(module).newPipeline()
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getHooks(): VsiCoreHooks {
      this.hooks as VsiCoreHooks
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getBlockTypes(): VsiBlockTypes {
      this.blockTypes as VsiBlockTypes
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getCollisionStartEvent(): ListenableEvent {
      this.collisionStartEvent as ListenableEvent
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
   override final fun getPhysTickEvent(): ListenableEvent {
      this.physTickEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getJointSerializedEvent(): ListenableEvent {
      this.jointSerializedEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getJointDeserializedEvent(): ListenableEvent {
      this.jointDeserializedEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getMergeEvent(): ListenableEvent {
      this.mergeEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getSplitEvent(): ListenableEvent {
      this.splitEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun newPipelineLegacyData(queryableShipDataBytes: ByteArray, chunkAllocatorBytes: ByteArray): VsiPipeline {
      this.newPipelineLegacyData(queryableShipDataBytes, chunkAllocatorBytes) as VsiPipeline
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun newPipeline(): VsiPipeline {
      this.newPipeline() as VsiPipeline
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun newPipeline(data: ByteArray): VsiPipeline {
      this.newPipeline(data) as VsiPipeline
   }

   public companion object {
      @JvmStatic
      @JvmSynthetic
      KProperty<Object>[] $$delegatedProperties;

      private final val logger: Logger
         private final get() {
            val var10000: Logger = VSCoreImpl.logger$delegate
            return var10000
         }


      public fun loadKrunch() {
         try {
            val var1: java.lang.String
            val var2: java.lang.CharSequence
            if ((var2 = var1 = System.getenv("KRUNCH_TEST_BINARY")) != null && var2.length() != 0) {
               this.logger.info("Loading Krunch testing binaries from $var1")
               Ix.a(var1)
            } else {
               Ix.a()
            }
         } catch (var3: Exception) {
            this.logger.error("Failed to load Krunch binary!", var3)
         }
      }

      @JvmStatic
      fun {
         val var0: Array<KProperty>
         (var0 = arrayOfNulls(1))[0] = Reflection.property1(
            PropertyReference1Impl(VSCoreImpl.Companion.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
         )
         $$delegatedProperties = var0
      }
   }
}
