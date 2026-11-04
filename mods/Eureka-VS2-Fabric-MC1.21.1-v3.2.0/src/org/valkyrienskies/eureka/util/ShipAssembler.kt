package org.valkyrienskies.eureka.util

import com.google.common.collect.Sets
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedHashMap
import kotlin.enums.EnumEntries
import kotlin.enums.EnumEntriesKt
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KProperty
import net.minecraft.class_1657
import net.minecraft.class_1923
import net.minecraft.class_1937
import net.minecraft.class_2246
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_2470
import net.minecraft.class_2488
import net.minecraft.class_2680
import net.minecraft.class_2818
import net.minecraft.class_2826
import net.minecraft.class_3218
import net.minecraft.class_3222
import net.minecraft.class_2338.class_2339
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.Logger
import org.joml.AxisAngle4d
import org.joml.Matrix4d
import org.joml.Vector3d
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.networking.simple.SimplePacket
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.core.internal.world.VsiPlayer
import org.valkyrienskies.core.util.datastructures.BlockPos2ObjectOpenHashMap
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.core.util.datastructures.SingleChunkDenseBlockPosSet
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.networking.PacketRestartChunkUpdates
import org.valkyrienskies.mod.common.networking.PacketStopChunkUpdates
import org.valkyrienskies.mod.common.util.MinecraftPlayer
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.util.ClassLogger
import org.valkyrienskies.mod.util.LoggingKt
import org.valkyrienskies.mod.util.RelocationUtilKt

@SourceDebugExtension(["SMAP\nShipAssembler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipAssembler.kt\norg/valkyrienskies/eureka/util/ShipAssembler\n+ 2 DenseBlockPosSet.kt\norg/valkyrienskies/core/util/datastructures/DenseBlockPosSet\n+ 3 BlockPos2ObjectOpenHashMap.kt\norg/valkyrienskies/core/util/datastructures/BlockPos2ObjectOpenHashMap\n+ 4 SingleChunkDenseBlockPosSet.kt\norg/valkyrienskies/core/util/datastructures/SingleChunkDenseBlockPosSet\n+ 5 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 6 Util.kt\norg/valkyrienskies/core/util/UtilKt\n+ 7 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 8 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,285:1\n84#2:286\n85#2:289\n86#2,2:305\n88#2:314\n89#2:334\n68#3,2:287\n72#3,3:315\n77#3:333\n24#4:290\n25#4:293\n26#4,2:297\n28#4:304\n29#4,3:307\n32#4:311\n33#4:313\n25#4,3:318\n28#4,4:326\n32#4,2:331\n13477#5,2:291\n13479#5:312\n10#6,3:294\n66#6,5:299\n14#6:310\n66#6,5:321\n14#6:330\n1#7:335\n1368#8:336\n1454#8,5:337\n1557#8:342\n1628#8,3:343\n1863#8,2:346\n1863#8,2:348\n1734#8,3:350\n1863#8,2:353\n*S KotlinDebug\n*F\n+ 1 ShipAssembler.kt\norg/valkyrienskies/eureka/util/ShipAssembler\n*L\n45#1:286\n45#1:289\n45#1:305,2\n45#1:314\n45#1:334\n45#1:287,2\n45#1:315,3\n45#1:333\n45#1:290\n45#1:293\n45#1:297,2\n45#1:304\n45#1:307,3\n45#1:311\n45#1:313\n45#1:318,3\n45#1:326,4\n45#1:331,2\n45#1:291,2\n45#1:312\n45#1:294,3\n45#1:299,5\n45#1:310\n45#1:321,5\n45#1:330\n156#1:336\n156#1:337,5\n157#1:342\n157#1:343,3\n161#1:346,2\n269#1:348,2\n208#1:350,3\n211#1:353,2\n*E\n"])
public object ShipAssembler {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties = arrayOf(
      Reflection.property1(PropertyReference1Impl(ShipAssembler.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1)
   );

   private final val logger: Logger by LoggingKt.logger().provideDelegate_j8jxBIw/* $VF was: provideDelegate-j8jxBIw */(INSTANCE, $$delegatedProperties[0])
      private final get() {
         return ClassLogger.getValue_impl/* $VF was: getValue-impl */(logger$delegate, this, $$delegatedProperties[0])
      }


   public fun collectBlockPositions(level: class_3218, center: class_2338, predicate: (class_2680) -> Boolean): HashSet<class_2338>? {
      val blocks: DenseBlockPosSet = DenseBlockPosSet()
      blocks.add(VectorConversionsMCKt.toJOML(center as class_2382) as Vector3ic)
      if (!this.bfs(level, center, blocks, predicate)) {
         return null
      } else {
         val blockPositions: HashSet = HashSet()
         val `this_$iv$iv`: BlockPos2ObjectOpenHashMap = blocks.getChunks()
         if (`this_$iv$iv`.getContainsNullKey()) {
            val var10000: Int = `this_$iv$iv`.getKeys()[`this_$iv$iv`.getN() * 3]
            val var10001: Int = `this_$iv$iv`.getKeys()[`this_$iv$iv`.getN() * 3 + 1]
            val var10002: Int = `this_$iv$iv`.getKeys()[`this_$iv$iv`.getN() * 3 + 2]
            val `this_$iv$ivx`: SingleChunkDenseBlockPosSet = `this_$iv$iv`.getValues()[`this_$iv$iv`.getN()] as SingleChunkDenseBlockPosSet
            val `chunkZ$iv`: Int = var10002
            val `chunkY$iv`: Int = var10001
            val `chunkX$iv`: Int = var10000
            val `$this$forEachIndexed$iv$iv$iv`: ByteArray = `this_$iv$ivx`.getData()
            var `index$iv$iv$iv`: Int = 0

            for (`item$iv$iv$iv` in `$this$forEachIndexed$iv$iv$iv`) {
               val `index$iv$iv`: Int = `index$iv$iv$iv`++
               val `$this$iterateBits$iv$iv$iv`: Byte = `item$iv$iv$iv`

               for (`i$iv$iv$iv` in 7 downTo 0) {
                  if ((`$this$iterateBits$iv$iv$iv` and 1 shl `i$iv$iv$iv`) != 0) {
                     val `index$iv$iv$ivx`: Int = `index$iv$iv` * 8 + `i$iv$iv$iv`
                     val `dimensions$iv$iv$iv`: Vector3ic = SingleChunkDenseBlockPosSet.Companion.getDimensions()
                     val `z$iv$iv$iv`: Int = `index$iv$iv$ivx` / (`dimensions$iv$iv$iv`.x() * `dimensions$iv$iv$iv`.y())
                     blockPositions.add(
                        class_2338(
                           (`index$iv$iv$ivx` - `z$iv$iv$iv` * `dimensions$iv$iv$iv`.x() * `dimensions$iv$iv$iv`.y()) % `dimensions$iv$iv$iv`.x()
                              + (`chunkX$iv` shl 4),
                           (`index$iv$iv$ivx` - `z$iv$iv$iv` * `dimensions$iv$iv$iv`.x() * `dimensions$iv$iv$iv`.y()) / `dimensions$iv$iv$iv`.x()
                              + (`chunkY$iv` shl 4),
                           `z$iv$iv$iv` + (`chunkZ$iv` shl 4)
                        )
                     )
                  }
               }
            }
         }

         for (`pos$iv$iv` in `this_$iv$iv`.getN() downTo 0) {
            if (`this_$iv$iv`.getKeys()[`pos$iv$iv` * 3] != 0
               || `this_$iv$iv`.getKeys()[`pos$iv$iv` * 3 + 1] != 0
               || `this_$iv$iv`.getKeys()[`pos$iv$iv` * 3 + 2] != 0) {
               val var96: Int = `this_$iv$iv`.getKeys()[`pos$iv$iv` * 3]
               val var101: Int = `this_$iv$iv`.getKeys()[`pos$iv$iv` * 3 + 1]
               val var103: Int = `this_$iv$iv`.getKeys()[`pos$iv$iv` * 3 + 2]
               val `this_$iv$ivx`: SingleChunkDenseBlockPosSet = `this_$iv$iv`.getValues()[`pos$iv$iv`] as SingleChunkDenseBlockPosSet
               val var53: Int = var103
               val var54: Int = var101
               val var55: Int = var96
               val var59: ByteArray = `this_$iv$ivx`.getData()
               var var61: Int = 0

               for (var64 in var59) {
                  val var66: Int = var61++
                  val var68: Byte = var64

                  for (var70 in 7 downTo 0) {
                     if ((var68 and 1 shl var70) != 0) {
                        val var75: Int = var66 * 8 + var70
                        val var76: Vector3ic = SingleChunkDenseBlockPosSet.Companion.getDimensions()
                        val var78: Int = var75 / (var76.x() * var76.y())
                        blockPositions.add(
                           class_2338(
                              (var75 - var78 * var76.x() * var76.y()) % var76.x() + (var55 shl 4),
                              (var75 - var78 * var76.x() * var76.y()) / var76.x() + (var54 shl 4),
                              var78 + (var53 shl 4)
                           )
                        )
                     }
                  }
               }
            }
         }

         return blockPositions
      }
   }

   public fun finishAssembly(level: class_3218, blockPositions: Set<class_2338>): ServerShip {
      this.clearRestingSnowLayers(level, blockPositions)
      return org.valkyrienskies.mod.common.assembly.ShipAssembler.assembleToShip(level, blockPositions, 1.0)
   }

   public fun collectBlocks(level: class_3218, center: class_2338, predicate: (class_2680) -> Boolean): ServerShip? {
      val var10000: HashSet = this.collectBlockPositions(level, center, predicate)
      return if (var10000 != null) INSTANCE.finishAssembly(level, var10000) else null
   }

   private fun clearRestingSnowLayers(level: class_3218, blockPositions: Set<class_2338>) {
      val above: class_2339 = class_2339()

      for (pos in blockPositions) {
         above.method_10103(pos.method_10263(), pos.method_10264() + 1, pos.method_10260())
         if (!blockPositions.contains(above) && level.method_8320(above as class_2338).method_26204() is class_2488) {
            level.method_8652(above as class_2338, class_2246.field_10124.method_9564(), 2)
         }
      }
   }

   private fun roundToNearestMultipleOf(number: Double, multiple: Double): Double {
      return multiple * Math.rint(number / multiple)
   }

   private fun snapRotation(direction: AxisAngle4d): AxisAngle4d {
      val x: Double = Math.abs(direction.x)
      val y: Double = Math.abs(direction.y)
      val z: Double = Math.abs(direction.z)
      val angle: Double = this.roundToNearestMultipleOf(direction.angle, Math.PI / 2)
      val var10000: AxisAngle4d
      if (x > y && x > z) {
         val var12: AxisAngle4d = direction.set(angle, Math.signum(direction.x), 0.0, 0.0)
         var10000 = var12
      } else if (y > x && y > z) {
         val var11: AxisAngle4d = direction.set(angle, 0.0, Math.signum(direction.y), 0.0)
         var10000 = var11
      } else {
         val var10: AxisAngle4d = direction.set(angle, 0.0, 0.0, Math.signum(direction.z))
         var10000 = var10
      }

      return var10000
   }

   private fun rotationFromAxisAngle(axis: AxisAngle4d): class_2470 {
      if (Math.abs(axis.y) < 0.1) {
         return class_2470.field_11467
      } else {
         if (Math.signum(axis.y) < 0.0) {
            axis.y = 1.0
            axis.angle = (Math.PI * 2) - axis.angle
            axis.angle %= Math.PI * 2
         }

         if (axis.angle < 0.001) {
            return class_2470.field_11467
         } else if (Math.abs(axis.angle - (Math.PI / 2)) < 0.001) {
            return class_2470.field_11465
         } else if (Math.abs(axis.angle - Math.PI) < 0.001) {
            return class_2470.field_11464
         } else if (Math.abs(axis.angle - (Math.PI * 3.0 / 2.0)) < 0.001) {
            return class_2470.field_11463
         } else {
            this.logger.warn("failed to convert $axis into a rotation")
            return class_2470.field_11467
         }
      }
   }

   public fun unfillShip(level: class_3218, ship: ServerShip, shipCenter: class_2338, center: class_2338) {
      ship.setStatic(true)
      val rotation: class_2470 = this.rotationFromAxisAngle(INSTANCE.snapRotation(AxisAngle4d(ship.getTransform().getShipToWorldRotation())))
      val var28: ShipTransform = ship.getTransform()
      val var22: Matrix4d = Matrix4d()
         .translate(var28.getPositionInWorld())
         .rotate(INSTANCE.snapRotation(AxisAngle4d(var28.getShipToWorldRotation())))
         .scale(var28.getShipToWorldScaling())
         .translate(-var28.getPositionInShip().x(), -var28.getPositionInShip().y(), -var28.getPositionInShip().z())
         val var25: Vector3d = Vector3d()
      val var29: java.util.Map = LinkedHashMap()
      ship.getActiveChunksSet().forEach({ chunkX: Int, chunkZ: Int ->
         `$chunksToBeUpdated`.put(class_1923(chunkX, chunkZ), Pair(class_1923(chunkX, chunkZ), class_1923(chunkX, chunkZ)))
      })
      val `$i$f$forEach`: java.lang.Iterable = CollectionsKt.toList(var29.values())
      val triple: java.util.Collection = ArrayList()

      for (var17 in `$i$f$forEach`) {
         CollectionsKt.addAll(triple, TuplesKt.toList(var17 as Pair))
      }

      val chunkPoses: java.util.List = triple as java.util.List
      val var39: java.lang.Iterable = triple as java.util.List
      val `destination$iv$ivx`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(triple as java.util.List, 10))

      for (var49 in var39) {
         `destination$iv$ivx`.add(VectorConversionsMCKt.toJOML(var49 as class_1923))
      }

      val var32: java.util.List = `destination$iv$ivx` as java.util.List
      val var10000: java.util.List = level.method_18456()

      for (var43 in var10000) {
         val var45: class_3222 = var43 as class_3222
         val var50: SimplePacketNetworking = VSGameUtilsKt.getVsCore().getSimplePacketNetworking()
         val var10001: SimplePacket = PacketStopChunkUpdates(var32) as SimplePacket
         val var10002: MinecraftPlayer = VSGameUtilsKt.getPlayerWrapper(var45 as class_1657)
         var50.sendToClient1(var10001, var10002 as VsiPlayer)
      }

      val var35: HashSet = Sets.newHashSet()
      ship.getActiveChunksSet()
         .forEach(
            { chunkX: Int, chunkZ: Int ->
               val chunk: class_2818 = `$level`.method_8497(chunkX, chunkZ)
               var sectionIndex: Int = 0

               for (var9 in chunk.method_12006().length..sectionIndex) {
                  val section: class_2826 = chunk.method_12006()[sectionIndex]
                  if (section != null && !section.method_38292()) {
                     val bottomY: Int = sectionIndex shl 4

                     repeat(15) { x ->
                        repeat(15) { y ->
                           repeat(15) { z ->
                              val state: class_2680 = section.method_12254(x, y, z)
                              if (!state.method_26215()) {
                                 val realX: Int = (chunkX shl 4) + x
                                 val realY: Int = bottomY + y + `$level`.method_31607()
                                 val realZ: Int = (chunkZ shl 4) + z
                                 val inWorldPos: Vector3d = `$shipToWorld`.transformPosition(
                                       `$alloc0`.set((double)realX + 0.5, (double)realY + 0.5, (double)((chunkZ shl 4) + z) + 0.5)
                                    )
                                    .floor()
                                    val inWorldBlockPos: class_2338 = class_2338((int)inWorldPos.x, (int)inWorldPos.y, (int)inWorldPos.z)
                                 val inShipPos: class_2338 = class_2338(realX, realY, realZ)
                                 `$toUpdate`.add(Triple(inShipPos, inWorldBlockPos, state))
                                 RelocationUtilKt.relocateBlock(`$level` as class_1937, inShipPos, inWorldBlockPos, false, null, `$rotation`)
                              }
                           }
                        }
                     }
                  }
               }
            }
         )
         val var53: java.util.Iterator = var35.iterator()
      val var38: java.util.Iterator = var53

      while (var38.hasNext()) {
         val var41: Triple = var38.next() as Triple
         RelocationUtilKt.updateBlock(level as class_1937, var41.getFirst() as class_2338, var41.getSecond() as class_2338, var41.getThird() as class_2680)
      }

      val var54: MinecraftServer = level.method_8503()
      VSGameUtilsKt.executeIf(
         var54,
         { 
            val `$this$all$iv`: java.lang.Iterable = `$chunkPoses`
            var var10000: Boolean
            if (`$chunkPoses` is java.util.Collection && (`$chunkPoses` as java.util.Collection).isEmpty()) {
               var10000 = true
            } else {
               val var4: java.util.Iterator = `$this$all$iv`.iterator()

               while (true) {
                  if (!var4.hasNext()) {
                     var10000 = true
                     break
                  }

                  if (!VSGameUtilsKt.isTickingChunk(`$level` as class_1937, var4.next() as class_1923)) {
                     var10000 = false
                     break
                  }
               }
            }

            var10000
         },
         { 
            val var10000: java.util.List = `$level`.method_18456()

            for (`element$iv` in var10000) {
               val player: class_3222 = `element$iv` as class_3222
               val `$this$unfillShip_u24lambda_u2415_u24lambda_u2414_u24lambda_u2413`: SimplePacketNetworking = VSGameUtilsKt.getVsCore()
                  .getSimplePacketNetworking()
                  val var10001: SimplePacket = PacketRestartChunkUpdates(`$chunkPosesJOML`) as SimplePacket
               val var10002: MinecraftPlayer = VSGameUtilsKt.getPlayerWrapper(player as class_1657)
               `$this$unfillShip_u24lambda_u2415_u24lambda_u2414_u24lambda_u2413`.sendToClient1(var10001, var10002 as VsiPlayer)
            }
         }
      )
   }

   private fun bfs(level: class_3218, start: class_2338, blocks: DenseBlockPosSet, predicate: (class_2680) -> Boolean): Boolean {
      val blacklist: DenseBlockPosSet = DenseBlockPosSet()
      val stack: ObjectArrayList = ObjectArrayList()
      this.directions(start, { it: class_2338 ->
         if (!`$blacklist`.contains(it.method_10263(), it.method_10264(), it.method_10260())) {
            `$blacklist`.add(it.method_10263(), it.method_10264(), it.method_10260())
            `$stack`.push(it)
         }

         Unit.INSTANCE
      })

      while (!stack.isEmpty()) {
         val pos: class_2338 = stack.pop() as class_2338
         val var10001: class_2680 = level.method_8320(pos)
         if (predicate(var10001) as java.lang.Boolean) {
            blocks.add(pos.method_10263(), pos.method_10264(), pos.method_10260())
            this.directions(pos, { it: class_2338 ->
               if (!`$blacklist`.contains(it.method_10263(), it.method_10264(), it.method_10260())) {
                  `$blacklist`.add(it.method_10263(), it.method_10264(), it.method_10260())
                  `$stack`.push(it)
               }

               Unit.INSTANCE
            })
         }

         if (EurekaConfig.SERVER.maxShipBlocks > 0 and blocks.size() > EurekaConfig.SERVER.maxShipBlocks) {
            this.logger.info("Stopped ship assembly due too many blocks")
            return false
         }
      }

      if (EurekaConfig.SERVER.maxShipBlocks > 0) {
         this.logger.info("Assembled ship with ${blocks.size()} blocks, out of ${EurekaConfig.SERVER.maxShipBlocks} allowed")
      }

      return true
   }

   private fun directions(center: class_2338, lambda: (class_2338) -> Unit) {
      if (!EurekaConfig.SERVER.diagonals) {
         for (`element$iv` in ShipAssembler.EntriesMappings.entries$0 as java.lang.Iterable) {
            val var12: class_2338 = center.method_10093(`element$iv` as class_2350)
            lambda(var12)
         }
      } else {
         for (x in -1..1) {
            for (y in -1..1) {
               for (z in -1..1) {
                  if (x != 0 || y != 0 || z != 0) {
                     val var10001: class_2338 = center.method_10069(x, y, z)
                     lambda(var10001)
                  }
               }
            }
         }
      }
   }

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class EntriesMappings {
      @JvmStatic
      @JvmSynthetic
      public EnumEntries<class_2350> entries$0 = EnumEntriesKt.enumEntries(class_2350.values() as Array<java.lang.Enum>);
   }
}
