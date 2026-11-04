package org.valkyrienskies.mod.common.world

import java.util.function.Predicate
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2391
import net.minecraft.class_2393
import net.minecraft.class_2680
import net.minecraft.class_2818
import net.minecraft.class_2826
import net.minecraft.class_3218
import net.minecraft.class_2338.class_2339
import net.minecraft.server.MinecraftServer
import org.joml.Matrix4dc
import org.joml.Vector3d
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.internal.world.VsiServerShipWorld
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.config.VSGameConfig

public object ShipPlantMower {
   private const val MIN_SPEED_SQ: Double = 1.0E-4
   private const val MAX_AXIS_SPAN: Double = 512.0
   private const val MAX_BLOCKS_PER_TICK: Int = 32768

   private final val kelpPredicate: Predicate<class_2680> = { it: class_2680 ->
      INSTANCE.isKelp(it)
   }

   private final var blockBudget: Int
   private final val scratchShipPos: Vector3d = Vector3d()
   private final val scratchBlockPos: class_2339 = class_2339()

   @JvmStatic
   public fun tick(shipWorld: VsiServerShipWorld, server: MinecraftServer) {
      if (VSGameConfig.SERVER.shipsDestroyPlants) {
         blockBudget = 32768

         for (ship in shipWorld.loadedShips) {
            if (blockBudget <= 0) {
               break
            }

            if (ship.getShipAABB() != null && !(ship.getVelocity().lengthSquared() < 1.0E-4)) {
               val var10000: class_3218 = VSGameUtilsKt.getLevelFromDimensionId(server, ship.getChunkClaimDimension())
               if (var10000 != null) {
                  INSTANCE.mow(ship, var10000)
               }
            }
         }
      }
   }

   private fun mow(ship: LoadedServerShip, level: class_3218) {
      val var10000: AABBdc = ship.getWorldAABB()
      if (var10000 != null) {
         if (!(var10000.maxX() - var10000.minX() > 512.0) && !(var10000.maxY() - var10000.minY() > 512.0) && !(var10000.maxZ() - var10000.minZ() > 512.0)) {
            val minX: Int = (int)Math.floor(var10000.minX())
            val maxX: Int = (int)Math.floor(var10000.maxX())
            val minZ: Int = (int)Math.floor(var10000.minZ())
            val maxZ: Int = (int)Math.floor(var10000.maxZ())
            val minY: Int = Math.max((int)Math.floor(var10000.minY()), level.method_31607())
            val maxY: Int = Math.min((int)Math.floor(var10000.maxY()), level.method_31600() - 1)
            if (minY <= maxY) {
               val worldToShip: Matrix4dc = ship.getTransform().worldToShip
               var cx: Int = minX shr 4
               val var12: Int = maxX shr 4
               if (cx <= maxX shr 4) {
                  while (true) {
                     var cz: Int = minZ shr 4
                     val var14: Int = maxZ shr 4
                     if (cz <= maxZ shr 4) {
                        while (true) {
                           val var29: class_2818 = level.method_14178().method_21730(cx, cz)
                           if (var29 != null) {
                              val chunk: class_2818 = var29
                              var sy: Int = Math.max(minY shr 4, level.method_32891())
                              val var17: Int = Math.min(maxY shr 4, level.method_31597() - 1)
                              if (sy <= var17) {
                                 while (true) {
                                    val section: class_2826 = chunk.method_38259(level.method_31603(sy))
                                    if (!section.method_38292() && section.method_19523(kelpPredicate)) {
                                       val x0: Int = Math.max(minX, cx shl 4)
                                       val x1: Int = Math.min(maxX, (cx shl 4) + 15)
                                       val y0: Int = Math.max(minY, sy shl 4)
                                       val y1: Int = Math.min(maxY, (sy shl 4) + 15)
                                       val z0: Int = Math.max(minZ, cz shl 4)
                                       val z1: Int = Math.min(maxZ, (cz shl 4) + 15)
                                       blockBudget -= (x1 - x0 + 1) * (y1 - y0 + 1) * (z1 - z0 + 1)
                                       var y: Int = y0
                                       if (y0 <= y1) {
                                          while (true) {
                                             var x: Int = x0
                                             if (x0 <= x1) {
                                                while (true) {
                                                   var z: Int = z0
                                                   if (z0 <= z1) {
                                                      while (true) {
                                                         val state: class_2680 = section.method_12254(x and 15, y and 15, z and 15)
                                                         if (this.isKelp(state) && this.occupiesWorldBlock(worldToShip, level, x, y, z)) {
                                                            this.mowKelp(level, class_2338(x, y, z), state)
                                                         }

                                                         if (z == z1) {
                                                            break
                                                         }

                                                         z++
                                                      }
                                                   }

                                                   if (x == x1) {
                                                      break
                                                   }

                                                   x++
                                                }
                                             }

                                             if (y == y1) {
                                                break
                                             }

                                             y++
                                          }
                                       }

                                       if (blockBudget <= 0) {
                                          return
                                       }
                                    }

                                    if (sy == var17) {
                                       break
                                    }

                                    sy++
                                 }
                              }
                           }

                           if (cz == var14) {
                              break
                           }

                           cz++
                        }
                     }

                     if (cx == var12) {
                        break
                     }

                     cx++
                  }
               }
            }
         }
      }
   }

   private fun occupiesWorldBlock(worldToShip: Matrix4dc, level: class_3218, x: Int, y: Int, z: Int): Boolean {
      val shipPos: Vector3d = worldToShip.transformPosition((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, scratchShipPos)
      return !level.method_8320(scratchBlockPos.method_10103((int)Math.floor(shipPos.x), (int)Math.floor(shipPos.y), (int)Math.floor(shipPos.z)) as class_2338)
         .method_26215()
      }

   private fun mowKelp(level: class_3218, pos: class_2338, state: class_2680) {
      level.method_8652(pos, state.method_26227().method_15759(), 18)
   }

   private fun isKelp(state: class_2680): Boolean {
      val block: class_2248 = state.method_26204()
      return block is class_2393 || block is class_2391
   }
}
