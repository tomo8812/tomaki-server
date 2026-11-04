package org.valkyrienskies.mod.api_impl.events

import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1297
import net.minecraft.class_1923
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_310
import net.minecraft.class_3218
import net.minecraft.server.MinecraftServer
import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.VsCoreApi
import org.valkyrienskies.core.api.attachment.AttachmentRegistration
import org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.event.ListenableEvent
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
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.world.ClientShipWorld
import org.valkyrienskies.core.api.world.ServerShipWorld
import org.valkyrienskies.core.api.world.ShipWorld
import org.valkyrienskies.core.util.events.EventEmitterImpl
import org.valkyrienskies.mod.api.VsApi
import org.valkyrienskies.mod.api.events.PostRenderShipEvent
import org.valkyrienskies.mod.api.events.PreRenderShipEvent
import org.valkyrienskies.mod.api.events.RegisterBlockStateEvent
import org.valkyrienskies.mod.common.IShipObjectWorldClientProvider
import org.valkyrienskies.mod.common.IShipObjectWorldServerProvider
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.entity.ShipMountedToData
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import org.valkyrienskies.mod.compat.SableCompat

@SourceDebugExtension(["SMAP\nVsApiImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VsApiImpl.kt\norg/valkyrienskies/mod/api_impl/events/VsApiImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,117:1\n1#2:118\n*E\n"])
public class VsApiImpl(core: VsCoreApi) : VsApi, VsCoreApi {
   private final val core: VsCoreApi
   public open val registerBlockStateEvent: EventEmitterImpl<RegisterBlockStateEvent>
   public open val preRenderShipEvent: EventEmitterImpl<PreRenderShipEvent>
   public open val postRenderShipEvent: EventEmitterImpl<PostRenderShipEvent>

   init {
      this.core = core
      this.registerBlockStateEvent = EventEmitterImpl<>()
      this.preRenderShipEvent = EventEmitterImpl<>()
      this.postRenderShipEvent = EventEmitterImpl<>()
   }

   @VsBeta
   public override fun <T : Any?> newAttachmentRegistrationBuilder(attachmentClass: Class<Any>): Builder<Any> {
      return this.core.newAttachmentRegistrationBuilder(attachmentClass)
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
      return this.core.newBodyKinematics(velocity, angularVelocity, position, rotation, scaling, positionInModel)
   }

   @VsBeta
   public override fun newBodyKinematics(velocity: Vector3dc, angularVelocity: Vector3dc, transform: BodyTransform): BodyKinematics {
      return this.core.newBodyKinematics(velocity, angularVelocity, transform)
   }

   @VsBeta
   public override fun newBodyKinematicsBuilder(): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
      return this.core.newBodyKinematicsBuilder()
   }

   public override fun newBodyTransform(position: Vector3dc, rotation: Quaterniondc, scaling: Vector3dc, positionInModel: Vector3dc): BodyTransform {
      return this.core.newBodyTransform(position, rotation, scaling, positionInModel)
   }

   @VsBeta
   public override fun newBodyTransformBuilder(): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
      return this.core.newBodyTransformBuilder()
   }

   @VsBeta
   public override fun <T : Any?> registerAttachment(attachmentClass: Class<Any>) {
      this.core.registerAttachment(attachmentClass)
   }

   @VsBeta
   public override fun <T : Any?> registerAttachment(attachmentClass: Class<Any>, registrationBuilder: (Builder<Any>) -> Unit) {
      this.core.registerAttachment(attachmentClass, registrationBuilder)
   }

   @VsBeta
   public override fun <T : Any?> registerAttachment(registration: AttachmentRegistration<Any>) {
      this.core.registerAttachment(registration)
   }

   @VsBeta
   public override fun registerAttachmentForRemoval(attachmentKey: String) {
      this.core.registerAttachmentForRemoval(attachmentKey)
   }

   override fun getCollisionEndEvent(): ListenableEvent<CollisionEvent> {
      this.core.collisionEndEvent
   }

   override fun getCollisionPersistEvent(): ListenableEvent<CollisionEvent> {
      this.core.collisionPersistEvent
   }

   override fun getCollisionStartEvent(): ListenableEvent<CollisionEvent> {
      this.core.collisionStartEvent
   }

   override fun getJointDeserializedEvent(): ListenableEvent<JointDeserializedEvent> {
      this.core.jointDeserializedEvent
   }

   override fun getJointSerializedEvent(): ListenableEvent<JointSerializedEvent> {
      this.core.jointSerializedEvent
   }

   override fun getMergeEvent(): ListenableEvent<MergeEvent> {
      this.core.mergeEvent
   }

   override fun getPhysTickEvent(): ListenableEvent<PhysTickEvent> {
      this.core.physTickEvent
   }

   override fun getShipLoadEvent(): ListenableEvent<ShipLoadEvent> {
      this.core.shipLoadEvent
   }

   override fun getShipLoadEventClient(): ListenableEvent<ShipLoadEventClient> {
      this.core.shipLoadEventClient
   }

   override fun getShipUnloadEventClient(): ListenableEvent<ShipUnloadEventClient> {
      this.core.shipUnloadEventClient
   }

   override fun getSplitEvent(): ListenableEvent<SplitEvent> {
      this.core.splitEvent
   }

   override fun getStartUpdateRenderTransformsEvent(): ListenableEvent<StartUpdateRenderTransformsEvent> {
      this.core.startUpdateRenderTransformsEvent
   }

   override fun getTickEndEvent(): ListenableEvent<TickEndEvent> {
      this.core.tickEndEvent
   }

   override fun getTransformFactory(): BodyTransform.Factory {
      this.core.transformFactory
   }

   public override fun isShipMountingEntity(entity: class_1297): Boolean {
      return entity is ShipMountingEntity
   }

   public override fun getDimensionId(level: class_1937): String {
      return dimensionId
   }

   public override fun getShipMountedTo(entity: class_1297?): Ship? {
      return if (entity != null) VSGameUtilsKt.getShipMountedTo(entity) else null
   }

   public override fun getMountPosInShip(entity: class_1297?): Vector3dc? {
      if (entity != null) {
         val var10000: ShipMountedToData = VSGameUtilsKt.getShipMountedToData$default(entity, null, 2, null)
         if (var10000 != null) {
            return var10000.mountPosInShip
         }
      }

      return null
   }

   public override fun getMountPosInShip(entity: class_1297?, partialTicks: Float): Vector3dc? {
      if (entity != null) {
         val var10000: ShipMountedToData = VSGameUtilsKt.getShipMountedToData(entity, partialTicks)
         if (var10000 != null) {
            return var10000.mountPosInShip
         }
      }

      return null
   }

   public override fun getServerShipWorld(): ServerShipWorld? {
      return this.getServerShipWorld(currentServer)
   }

   public override fun getServerShipWorld(server: MinecraftServer?): ServerShipWorld? {
      return if (server as IShipObjectWorldServerProvider != null) (server as IShipObjectWorldServerProvider).shipObjectWorld else null
   }

   public override fun getClientShipWorld(): ClientShipWorld? {
      return this.getClientShipWorld(class_310.method_1551())
   }

   public override fun getShipWorld(level: class_1937?): ShipWorld? {
      return if (level is class_3218) this.getServerShipWorld((level as class_3218).method_8503()) else (this.getClientShipWorld() as? ShipWorld)
   }

   public override fun isChunkInShipyard(level: class_1937?, chunkX: Int, chunkZ: Int): Boolean {
      if (level == null) {
         return false
      } else {
         if (!sable || !SableCompat.INSTANCE.isChunkInSublevel(level, chunkX, chunkZ)) {
            val var10000: ShipWorld = this.getShipWorld(level)
            if (var10000 != null && var10000.isChunkInShipyard(chunkX, chunkZ, this.getDimensionId(level))) {
               return true
            }
         }

         return false
      }
   }

   public override fun getClientShipWorld(client: class_310?): ClientShipWorld? {
      return if (client as IShipObjectWorldClientProvider != null) (client as IShipObjectWorldClientProvider).shipObjectWorld else null
   }

   public override fun getShipManagingBlock(level: class_1937?, pos: class_2338?): Ship? {
      return if (pos != null) (if (level != null) VSGameUtilsKt.getShipManagingPos(level, pos) else null) else null
   }

   public override fun getShipManagingChunk(level: class_1937?, pos: class_1923?): Ship? {
      return if (pos != null) (if (level != null) VSGameUtilsKt.getShipManagingPos(level, pos) else null) else null
   }

   public override fun getShipManagingChunk(level: class_1937?, chunkX: Int, chunkZ: Int): Ship? {
      return if (level != null) VSGameUtilsKt.getShipManagingPos(level, chunkX, chunkZ) else null
   }

   public override fun getShipsIntersecting(level: class_1937?, aabb: AABBdc?): Iterable<Ship> {
      return if (level != null && aabb != null) VSGameUtilsKt.getShipsIntersecting(level, aabb) else CollectionsKt.emptyList()
   }

   public override fun getShipsIntersecting(level: class_1937?, x: Double, y: Double, z: Double): Iterable<Ship> {
      return this.getShipsIntersecting(level, AABBd(x, y, z, x, y, z))
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun getRegisterBlockStateEvent(): ListenableEvent {
      this.registerBlockStateEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun getPreRenderShipEvent(): ListenableEvent {
      this.preRenderShipEvent as ListenableEvent
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun getPostRenderShipEvent(): ListenableEvent {
      this.postRenderShipEvent as ListenableEvent
   }
}
