package org.valkyrienskies.mod.common

import com.google.common.collect.MapMaker
import java.util.concurrent.ConcurrentMap
import net.minecraft.class_1657
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2382
import net.minecraft.class_243
import org.joml.Matrix4dc
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.mixin.accessors.entity.EntityAccessor

public object PlayerUtil {
   private final val prevPosInfo: ConcurrentMap<class_1657, org.valkyrienskies.mod.common.PlayerUtil.TempPlayerPosInfo>

   @JvmStatic
   public fun transformPlayerTemporarily(player: class_1657, world: class_1937, blockInShip: class_2338) {
      transformPlayerTemporarily(player, VSGameUtilsKt.getLoadedShipManagingPos(world, blockInShip as class_2382))
   }

   @JvmStatic
   public fun transformPlayerTemporarily(player: class_1657, ship: LoadedShip?) {
      if (ship != null) {
         val var10000: class_1937 = player.method_37908()
         if (!VSGameUtilsKt.isBlockInShipyard(var10000, player.method_23317(), player.method_23318(), player.method_23321())) {
            val shipMatrix: java.util.Map = prevPosInfo
            val var10002: Float = player.method_36454()
            val var10003: Float = player.field_6241
            val var10004: Float = player.method_36455()
            val var10005: class_243 = player.method_19538()
            shipMatrix.put(player, PlayerUtil.TempPlayerPosInfo(var10002, var10003, var10004, var10005))
            val var9: Matrix4dc = ship.getWorldToShip()
            var var10001: class_243 = player.method_5720()
            val var10: Vector3d = var9.transformDirection(VectorConversionsMCKt.toJOML(var10001))
            var10001 = player.method_19538()
            val position: Vector3d = var9.transformPosition(VectorConversionsMCKt.toJOML(var10001))
            val yaw: Double = -Math.atan2(var10.x, var10.z)
            val pitch: Double = -Math.atan2(var10.y, Math.sqrt(var10.x * var10.x + var10.z * var10.z))
            player.method_36456((float)(yaw * (180.0 / Math.PI)))
            player.field_6241 = player.method_36454()
            player.method_36457((float)(pitch * (180.0 / Math.PI)))
            val var11: EntityAccessor = player as EntityAccessor
            var11.setPosNoUpdates(VectorConversionsMCKt.toMinecraft(position))
         }
      }
   }

   @JvmStatic
   public fun untransformPlayer(player: class_1657) {
      val var10000: PlayerUtil.TempPlayerPosInfo = prevPosInfo.remove(player)
      if (var10000 != null) {
         player.method_36457(var10000.pitch)
         player.method_36456(var10000.yaw)
         player.field_6241 = var10000.headYaw
         (player as EntityAccessor).setPosNoUpdates(var10000.pos)
      }
   }

   @JvmStatic
   public fun <T> transformPlayerTemporarily(player: class_1657, world: class_1937, blockInShip: class_2338, inside: () -> Any): Any {
      return (T)transformPlayerTemporarily(player, VSGameUtilsKt.getLoadedShipManagingPos(world, blockInShip as class_2382), inside)
   }

   @JvmStatic
   public fun <T> transformPlayerTemporarily(player: class_1657, ship: LoadedShip?, inside: () -> Any): Any {
      transformPlayerTemporarily(player, ship)

      try {
         return (T)inside()
      } finally {
         untransformPlayer(player)
      }
   }

   @JvmStatic
   fun {
      val var10000: ConcurrentMap = MapMaker().weakKeys().makeMap()
      prevPosInfo = var10000
   }

   private data class TempPlayerPosInfo(yaw: Float, headYaw: Float, pitch: Float, pos: class_243) {
      public final val yaw: Float
      public final val headYaw: Float
      public final val pitch: Float
      public final val pos: class_243

      init {
         this.yaw = yaw
         this.headYaw = headYaw
         this.pitch = pitch
         this.pos = pos
      }

      public operator fun component1(): Float {
         return this.yaw
      }

      public operator fun component2(): Float {
         return this.headYaw
      }

      public operator fun component3(): Float {
         return this.pitch
      }

      public operator fun component4(): class_243 {
         return this.pos
      }

      public fun copy(yaw: Float = this.yaw, headYaw: Float = this.headYaw, pitch: Float = this.pitch, pos: class_243 = this.pos): org.valkyrienskies.mod.common.PlayerUtil.TempPlayerPosInfo {
         return PlayerUtil.TempPlayerPosInfo(yaw, headYaw, pitch, pos)
      }

      public override fun toString(): String {
         return "TempPlayerPosInfo(yaw=${this.yaw}, headYaw=${this.headYaw}, pitch=${this.pitch}, pos=${this.pos})"
      }

      public override fun hashCode(): Int {
         return ((java.lang.Float.hashCode(this.yaw) * 31 + java.lang.Float.hashCode(this.headYaw)) * 31 + java.lang.Float.hashCode(this.pitch)) * 31
            + this.pos.hashCode()
         }

      public override operator fun equals(other: Any?): Boolean {
         label40@
         if (this === other) {
            return true
         } else {
            return other is PlayerUtil.TempPlayerPosInfo
               && java.lang.Float.compare(this.yaw, (other as PlayerUtil.TempPlayerPosInfo).yaw) == 0
               && java.lang.Float.compare(this.headYaw, (other as PlayerUtil.TempPlayerPosInfo).headYaw) == 0
               && java.lang.Float.compare(this.pitch, (other as PlayerUtil.TempPlayerPosInfo).pitch) == 0
               && this.pos == (other as PlayerUtil.TempPlayerPosInfo).pos
            }
      }
   }
}
