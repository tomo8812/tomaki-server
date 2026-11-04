package org.valkyrienskies.core.impl.game.ships

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.inject.Inject
import kotlin.jvm.internal.MutablePropertyReference1
import kotlin.jvm.internal.MutablePropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.api.ships.properties.PhysPose
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.api.ServerShipInternal
import org.valkyrienskies.core.impl.bodies.properties.BodyKinematicsFactory
import org.valkyrienskies.core.impl.chunk_tracking.ShipActiveChunksSet
import org.valkyrienskies.core.impl.shadow.CA
import org.valkyrienskies.core.impl.shadow.Cy
import org.valkyrienskies.core.impl.shadow.Dt
import org.valkyrienskies.core.impl.shadow.Dv
import org.valkyrienskies.core.impl.shadow.Eh
import org.valkyrienskies.core.impl.shadow.GJ

public class ShipData(id: Long,
      slug: String?,
      chunkClaim: ChunkClaim,
      chunkClaimDimension: String,
      inertiaData: ShipInertiaDataImpl,
      kinematics: BodyKinematics,
      shipAABB: AABBdc,
      shipVoxelAABB: AABBic?,
      shipActiveChunksSet: IShipActiveChunksSet,
      isStatic: Boolean = false,
      kinematicTarget: PhysPose? = null,
      attachmentHolder: CA,
      portalCoolDown: Int = 0
   ) : ShipDataCommon(id, slug, chunkClaim, chunkClaimDimension, kinematics, shipAABB, shipVoxelAABB, shipActiveChunksSet),
   ServerShipInternal {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties;

   @GJ
   public open val inertiaData: ShipInertiaDataImpl

   public final val attachmentHolder: CA
   internal final var portalCoolDown: Int

   public open var isStatic: Boolean
      public open get() {
         return this.isStatic$delegate.getValue(this, $$delegatedProperties[0]) as java.lang.Boolean
      }

      public open set(<set-?>) {
         this.isStatic$delegate.setValue(this, $$delegatedProperties[0], `<set-?>`)
      }


   public open var kinematicTarget: PhysPose?
      public open get() {
         return this.kinematicTarget$delegate.getValue(this, $$delegatedProperties[1]) as PhysPose
      }

      public open set(<set-?>) {
         this.kinematicTarget$delegate.setValue(this, $$delegatedProperties[1], `<set-?>`)
      }


   @JsonIgnore
   private final val missingLoadedChunks: IShipActiveChunksSet

   @JsonIgnore
   private final val shipAABBGenerator: Dv

   @JsonIgnore
   public open var transformProvider: ServerShipTransformProvider?

   @JsonIgnore
   public final var needsUpdating: Boolean

   @JsonIgnore
   public final var loadFreezeUntilGameTick: Long

   init {
      this.inertiaData = inertiaData
      this.attachmentHolder = attachmentHolder
      this.portalCoolDown = portalCoolDown
      this.isStatic$delegate = ShipData$special$$inlined$observable$1(isStatic, this) as ReadWriteProperty
      this.kinematicTarget$delegate = ShipData$special$$inlined$observable$2(kinematicTarget, this) as ReadWriteProperty
      this.missingLoadedChunks = ShipActiveChunksSet.Companion.create()
      this.shipAABBGenerator = Dt(null, 1, null)
      this.loadFreezeUntilGameTick = -1L
      shipActiveChunksSet.forEach({ chunkX: Int, chunkZ: Int ->
         `this$0`.missingLoadedChunks.add(chunkX, chunkZ)
      })
   }

   public override fun onSetBlock(
      posX: Int,
      posY: Int,
      posZ: Int,
      newBlockIsAir: Boolean,
      oldBlockMass: Double,
      newBlockMass: Double,
      isRunningOnServer: Boolean
   ) {
      super.onSetBlock(posX, posY, posZ, newBlockIsAir, oldBlockMass, newBlockMass, isRunningOnServer)
      this.inertiaData.onSetBlockUseSphereMOI(posX, posY, posZ, oldBlockMass, newBlockMass)
      if (isRunningOnServer) {
         this.updateShipAABBGenerator(posX, posY, posZ, !newBlockIsAir)
         val var10000: Eh = this.attachmentHolder.getAttachment(Eh.class)
         if (var10000 != null) {
            var10000.b = this.shipAABB
         }
      }

      this.needsUpdating = true
   }

   public override fun updateShipAABBGenerator(posX: Int, posY: Int, posZ: Int, set: Boolean) {
      if (set) {
         this.shipAABBGenerator.add(posX, posY, posZ)
      } else {
         this.shipAABBGenerator.remove(posX, posY, posZ)
      }

      val var5: AABBi
      if ((var5 = this.shipAABBGenerator.a()) != null) {
         var5.maxX++
         var5.maxY++
         var5.maxZ++
      }

      this.shipAABB = var5 as AABBic
      this.needsUpdating = true
   }

   public override fun onLoadChunk(chunkX: Int, chunkZ: Int) {
      if (this.chunkClaim.contains(chunkX, chunkZ)) {
         this.missingLoadedChunks.remove(chunkX, chunkZ)
      }
   }

   public override fun onUnloadChunk(chunkX: Int, chunkZ: Int) {
      if (this.chunkClaim.contains(chunkX, chunkZ) && this.activeChunksSet.contains(chunkX, chunkZ)) {
         this.missingLoadedChunks.add(chunkX, chunkZ)
      }
   }

   public override fun areVoxelsFullyLoaded(): Boolean {
      return this.missingLoadedChunks.size == 0
   }

   public override fun unsafeSetTransform(transform: BodyTransform) {
      this.kinematics = this.kinematics.withTransform(transform)
      this.needsUpdating = true
   }

   public override fun asShipDataCommon(): ShipDataCommon {
      return this
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getInertiaData(): ShipInertiaData {
      this.inertiaData as ShipInertiaData
   }

   @JvmStatic
   fun {
      val var0: Array<KProperty>
      (var0 = arrayOfNulls(2))[0] = Reflection.mutableProperty1(
         MutablePropertyReference1Impl(ShipData.class, "isStatic", "isStatic()Z", 0) as MutablePropertyReference1
      )
      var0[1] = Reflection.mutableProperty1(
         MutablePropertyReference1Impl(ShipData.class, "kinematicTarget", "getKinematicTarget()Lorg/valkyrienskies/core/api/ships/properties/PhysPose;", 0) as MutablePropertyReference1
      )
      $$delegatedProperties = var0
   }

   public class Factory @Inject  public constructor(attachmentHolderFactory: Cy) {
      private final val attachmentHolderFactory: Cy

      init {
         this.attachmentHolderFactory = attachmentHolderFactory
      }

      public fun createEmpty(
         slug: String,
         shipId: Long,
         chunkClaim: ChunkClaim,
         chunkClaimDimension: String,
         shipCenterInWorldCoordinates: Vector3dc,
         shipCenterInShipCoordinates: Vector3dc,
         scaling: Double = 1.0,
         isStatic: Boolean = false
      ): ShipData {
         val var11: Cy = this.attachmentHolderFactory
         val var10000: Any = this.attachmentHolderFactory.a.get()
         val var13: CA = var10000 as CA
         val var14: BodyKinematicsFactory = BodyKinematicsFactory.INSTANCE
         val var10001: Vector3dc = Vector3d()
         val var10002: Vector3dc = Vector3d()
         val var10004: Quaterniond = Quaterniond().fromAxisAngleDeg(0.0, 1.0, 0.0, 0.0)
         val var12: BodyKinematics = var14.create(var10001, var10002, shipCenterInWorldCoordinates, var10004, Vector3d(scaling), shipCenterInShipCoordinates)
         val var10006: ShipInertiaDataImpl = ShipInertiaDataImpl.Companion.newEmptyShipInertiaData()
         val var10008: BodyTransform = var12.transform
         return ShipData(
            shipId,
            slug,
            chunkClaim,
            chunkClaimDimension,
            var10006,
            var12,
            (var10008 as ShipTransform).createEmptyAABB(),
            null,
            ShipActiveChunksSet.Companion.create(),
            isStatic,
            null,
            var13,
            0,
            5120,
            null
         )
      }
   }
}
