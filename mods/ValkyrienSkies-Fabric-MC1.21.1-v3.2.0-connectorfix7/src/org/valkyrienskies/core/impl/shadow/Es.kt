package org.valkyrienskies.core.impl.shadow

import com.fasterxml.jackson.databind.JsonNode
import java.util.LinkedHashMap
import java.util.function.Supplier
import org.joml.Matrix4dc
import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.attachment.AttachmentHolder
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.DragController
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider
import org.valkyrienskies.core.api.ships.ServerTickListener
import org.valkyrienskies.core.api.ships.ShipPhysicsListener
import org.valkyrienskies.core.api.ships.WingManager
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.api.ships.properties.PhysPose
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.api.LoadedServerShipInternal
import org.valkyrienskies.core.impl.api.ServerShipInternal
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import org.valkyrienskies.core.impl.util.serialization.VSJacksonUtil
import org.valkyrienskies.core.internal.ShipTeleportData

public class Es(p0: ShipData) : Eq(var1), AttachmentHolder, LoadedServerShipInternal, ServerShipInternal {
   public open val q: ShipData

   public open var s: Int
      internal open set(p0) {
         this.c = var1
      }


   public final val r: Fs<JsonNode>
   public final val l: MutableMap<Class<*>, ShipPhysicsListener>
   public final val x: MutableMap<Class<*>, ServerTickListener>

   init {
      this.b = var1
      val var10003: Fq = a
      val var10004: JsonNode = VSJacksonUtil.INSTANCE.deltaMapper.valueToTree(this.b)
      this.d = Fs<>(var10003, var10004)
      this.e = LinkedHashMap<>()
      this.f = LinkedHashMap<>()
      val var3: Ey = Ey()
      val var2: Eh = Eh(false, 1, null)
      WingManager.createWingGroup$default(var3, false, 1, null)
      this.setAttachment(var3)
      this.setAttachment(var2)

      for (var5 in this.b.attachmentHolder.getAllAttachments()) {
         this.a(var5.getClass(), var5)
      }
   }

   public open val a: IShipActiveChunksSet

   public open val b: Vector3dc
      public open get() {
         return this.b.getAngularVelocity()
      }


   public open val c: ChunkClaim

   public open var d: String

   public open val f: Long

   public open val g: ShipInertiaData

   public open var h: Boolean

   public open var i: PhysPose?

   public open val j: BodyKinematics

   public open val k: Vector3dc
      public open get() {
         return this.b.getOmega()
      }


   public open val m: ShipTransform
      public open get() {
         return this.b.getPrevTickShipTransform()
      }


   public open val n: ShipTransform

   public open val o: AABBic?

   public open val p: IShipActiveChunksSet
      public open get() {
         return this.b.getShipActiveChunksSet()
      }


   public open val t: Matrix4dc
      public open get() {
         return this.b.getShipToWorld()
      }


   public open val u: ShipTransform
      public open get() {
         return this.b.getShipTransform()
      }


   public open val v: AABBic?
      public open get() {
         return this.b.getShipVoxelAABB()
      }


   public open var w: String?

   public open val y: ShipTransform
      public open get() {
         return this.b.getTransform()
      }


   public open var z: ServerShipTransformProvider?

   public open val A: Vector3dc
      public open get() {
         return this.b.getVelocity()
      }


   public open val C: AABBdc

   public open val D: Matrix4dc
      public open get() {
         return this.b.getWorldToShip()
      }


   public override fun areVoxelsFullyLoaded(): Boolean {
      return this.b.areVoxelsFullyLoaded()
   }

   public override fun asShipDataCommon(): ShipDataCommon {
      return this.b.asShipDataCommon()
   }

   public override fun onLoadChunk(p0: Int, p1: Int) {
      this.b.onLoadChunk(chunkX, chunkZ)
   }

   public override fun onSetBlock(p0: Int, p1: Int, p2: Int, p3: Boolean, p4: Double, p5: Double, p6: Boolean) {
      this.b.onSetBlock(posX, posY, posZ, newBlockIsAir, oldBlockMass, newBlockMass, isRunningOnServer)
   }

   public override fun onUnloadChunk(p0: Int, p1: Int) {
      this.b.onUnloadChunk(chunkX, chunkZ)
   }

   @Deprecated(message = "Since VS 2.5, attachments can only be saved to a LoadedServerShip", replaceWith = @ReplaceWith(expression = "this.setAttachment(clazz, value)", imports = []), level = DeprecationLevel.ERROR)
   public override fun <T> saveAttachment(p0: Class<Any>, p1: Any?): Nothing {
      this.b.saveAttachment(clazz, value)
      return null
   }

   public override fun setFromTransform(p0: BodyTransform) {
      this.b.setFromTransform(transform)
   }

   public override fun unsafeSetKinematics(p0: BodyKinematics) {
      this.b.unsafeSetKinematics(kinematics)
   }

   public override fun unsafeSetTransform(p0: BodyTransform) {
      this.b.unsafeSetTransform(transform)
   }

   public override fun updateShipAABBGenerator(p0: Int, p1: Int, p2: Int, p3: Boolean) {
      this.b.updateShipAABBGenerator(posX, posY, posZ, set)
   }

   public open val B: WingManager?
      public open get() {
         return this.getAttachment(Ey.class)
      }


   public open val e: DragController?
      public open get() {
         return this.getAttachment(Eh.class)
      }


   public override fun isOnPortalCoolDown(): Boolean {
      return this.b.portalCoolDown > 0
   }

   public override fun handleInsidePortal() {
      this.b.portalCoolDown = 300
   }

   public override fun decayPortalCoolDown() {
      if (this.isOnPortalCoolDown()) {
         this.b.portalCoolDown = this.b.portalCoolDown + -1
      }
   }

   private fun a(p0: Class<*>, p1: Any?) {
      if (var2 == null) {
         this.e.remove(var1)
         this.f.remove(var1)
      } else {
         if (var2 is ShipPhysicsListener) {
            this.e.put(var1, (ShipPhysicsListener)var2)
         }

         if (var2 is ServerShipUser) {
            (var2 as ServerShipUser).ship = this as ServerShip
         }

         if (var2 is ServerTickListener) {
            this.f.put(var1, (ServerTickListener)var2)
         }
      }
   }

   public override fun teleportShip(p0: ShipTeleportData) {
      this.c = this.s + 1
      this.b.kinematics = teleportData.createNewShipTransform(this.q)
      this.b.needsUpdating = true
   }

   public override fun <T : Any> getAttachment(p0: Class<Any>): Any? {
      if (clazz == WingManager::class.java) {
         val var10000: WingManager = this.B
         return (T)(var10000 as Any)
      } else {
         return (T)this.b.attachmentHolder.getAttachment(clazz)
      }
   }

   public override fun <T : Any> getOrPutAttachment(p0: Class<Any>, p1: Supplier<Any>): Any {
      val var4: Any = this.b.attachmentHolder.getOrPutAttachment(clazz, supplier)
      this.a(var4.getClass(), var4)
      return (T)var4
   }

   public override fun <T : Any> setAttachment(p0: Any): Any? {
      this.a(value.getClass(), value)
      return (T)this.b.attachmentHolder.setAttachment(value)
   }

   public override fun <T : Any> removeAttachment(p0: Class<Any>): Any? {
      this.a(clazz, null)
      return (T)this.b.attachmentHolder.removeAttachment(clazz)
   }

   public override fun getAllAttachments(): Iterable<Any> {
      return this.b.attachmentHolder.getAllAttachments()
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun a(): ShipDataCommon {
      this.b as ShipDataCommon
   }

   public companion object a_ {
      private const val a: Int = 300
   }
}
