package org.valkyrienskies.mod.common.util

import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashSet
import java.util.Queue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer
import kotlin.jvm.internal.SourceDebugExtension
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.core.internal.joints.VSJoint
import org.valkyrienskies.core.internal.joints.VSJointAndId
import org.valkyrienskies.core.internal.world.VsiPhysLevel

@SourceDebugExtension(["SMAP\nGameToPhysicsAdapter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GameToPhysicsAdapter.kt\norg/valkyrienskies/mod/common/util/GameToPhysicsAdapter\n+ 2 Util.kt\norg/valkyrienskies/core/util/UtilKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,376:1\n42#2,2:377\n42#2,2:379\n42#2,2:381\n42#2,2:383\n42#2,2:385\n42#2,2:387\n42#2,2:389\n42#2,2:391\n42#2,2:393\n42#2,2:395\n42#2,2:399\n42#2,2:401\n42#2,2:403\n1863#3,2:397\n1863#3,2:405\n*S KotlinDebug\n*F\n+ 1 GameToPhysicsAdapter.kt\norg/valkyrienskies/mod/common/util/GameToPhysicsAdapter\n*L\n44#1:377,2\n54#1:379,2\n58#1:381,2\n68#1:383,2\n72#1:385,2\n83#1:387,2\n87#1:389,2\n98#1:391,2\n124#1:393,2\n127#1:395,2\n141#1:399,2\n143#1:401,2\n144#1:403,2\n139#1:397,2\n147#1:405,2\n*E\n"])
public class GameToPhysicsAdapter {
   private final val worldForces: ConcurrentLinkedQueue<Pair<Long, org.valkyrienskies.mod.common.util.GameToPhysicsAdapter.ForceAtPos>> =
      ConcurrentLinkedQueue()
      private final val worldTorques: ConcurrentLinkedQueue<Pair<Long, Vector3dc>> = ConcurrentLinkedQueue()
   private final val modelForces: ConcurrentLinkedQueue<Pair<Long, org.valkyrienskies.mod.common.util.GameToPhysicsAdapter.ForceAtPos>> =
      ConcurrentLinkedQueue()
      private final val modelTorques: ConcurrentLinkedQueue<Pair<Long, Vector3dc>> = ConcurrentLinkedQueue()
   private final val bodyForces: ConcurrentLinkedQueue<Pair<Long, org.valkyrienskies.mod.common.util.GameToPhysicsAdapter.ForceAtPos>> =
      ConcurrentLinkedQueue()
      private final val bodyTorques: ConcurrentLinkedQueue<Pair<Long, Vector3dc>> = ConcurrentLinkedQueue()
   private final val worldToModelForces: ConcurrentLinkedQueue<Pair<Long, org.valkyrienskies.mod.common.util.GameToPhysicsAdapter.ForceAtPos>> =
      ConcurrentLinkedQueue()
      private final val worldToBodyForces: ConcurrentLinkedQueue<Pair<Long, org.valkyrienskies.mod.common.util.GameToPhysicsAdapter.ForceAtPos>> =
      ConcurrentLinkedQueue()
      private final val addedJoints: ConcurrentHashMap<Pair<VSJoint, Consumer<Int>>, Int> = ConcurrentHashMap()
   private final val updatedJoints: ConcurrentLinkedQueue<VSJointAndId> = ConcurrentLinkedQueue()
   private final val deletedJoints: ConcurrentLinkedQueue<Int> = ConcurrentLinkedQueue()
   private final val shipToJointIds: ConcurrentHashMap<Long, Set<Int>> = ConcurrentHashMap()
   private final val jointById: ConcurrentHashMap<Int, VSJoint> = ConcurrentHashMap()
   private final val toBeStatic: ConcurrentLinkedQueue<Pair<Long, Boolean>> = ConcurrentLinkedQueue()
   private final val enablePairs: ConcurrentLinkedQueue<Pair<Long, Long>> = ConcurrentLinkedQueue()
   private final val disablePairs: ConcurrentLinkedQueue<Pair<Long, Long>> = ConcurrentLinkedQueue()
   private final val shipToLiquidOverlap: ConcurrentHashMap<Long, Double> = ConcurrentHashMap()

   public fun physTick(physLevel: PhysLevel, delta: Double) {
      var callbackQueue: Queue = this.worldForces

      while (true) {
         var var10000: Any = callbackQueue.poll()
         if (var10000 == null) {
            callbackQueue = this.worldTorques

            while (true) {
               var10000 = callbackQueue.poll()
               if (var10000 == null) {
                  callbackQueue = this.modelForces

                  while (true) {
                     var10000 = callbackQueue.poll()
                     if (var10000 == null) {
                        callbackQueue = this.modelTorques

                        while (true) {
                           var10000 = callbackQueue.poll()
                           if (var10000 == null) {
                              callbackQueue = this.bodyForces

                              while (true) {
                                 var10000 = callbackQueue.poll()
                                 if (var10000 == null) {
                                    callbackQueue = this.bodyTorques

                                    while (true) {
                                       var10000 = callbackQueue.poll()
                                       if (var10000 == null) {
                                          callbackQueue = this.worldToModelForces

                                          while (true) {
                                             var10000 = callbackQueue.poll()
                                             if (var10000 == null) {
                                                callbackQueue = this.worldToBodyForces

                                                while (true) {
                                                   var10000 = callbackQueue.poll()
                                                   if (var10000 == null) {
                                                      val var21: ArrayList = ArrayList()
                                                      HashMap<>(this.addedJoints).forEach({ p0: Any, p1: Any ->
                                                         `$tmp0`(p0, p1)
                                                      })
                                                      var `$this$pollUntilEmpty$ivx`: Queue = this.updatedJoints

                                                      while (true) {
                                                         var10000 = `$this$pollUntilEmpty$ivx`.poll()
                                                         if (var10000 == null) {
                                                            `$this$pollUntilEmpty$ivx` = this.deletedJoints

                                                            while (true) {
                                                               var10000 = `$this$pollUntilEmpty$ivx`.poll()
                                                               if (var10000 == null) {
                                                                  this.shipToJointIds.clear()
                                                                  this.jointById.clear()
                                                                  this.shipToJointIds.putAll((physLevel as VsiPhysLevel).getJointsByShipIds())
                                                                  this.jointById.putAll((physLevel as VsiPhysLevel).getAllJoints())

                                                                  for (var73 in var21) {
                                                                     ((var73 as Pair).component1() as Consumer)
                                                                        .accept(((var73 as Pair).component2() as java.lang.Number).intValue())
                                                                     }

                                                                  `$this$pollUntilEmpty$ivx` = this.toBeStatic

                                                                  while (true) {
                                                                     var10000 = `$this$pollUntilEmpty$ivx`.poll()
                                                                     if (var10000 == null) {
                                                                        `$this$pollUntilEmpty$ivx` = this.enablePairs

                                                                        while (true) {
                                                                           var10000 = `$this$pollUntilEmpty$ivx`.poll()
                                                                           if (var10000 == null) {
                                                                              `$this$pollUntilEmpty$ivx` = this.disablePairs

                                                                              while (true) {
                                                                                 var10000 = `$this$pollUntilEmpty$ivx`.poll()
                                                                                 if (var10000 == null) {
                                                                                    this.shipToLiquidOverlap.clear()

                                                                                    for (var77 in (physLevel as VsiPhysLevel).getAllPhysShips()) {
                                                                                       this.shipToLiquidOverlap
                                                                                          .put((var77 as PhysShip).getId(), (var77 as PhysShip).liquidOverlap)
                                                                                       }

                                                                                    return
                                                                                 }

                                                                                 physLevel.disableCollisionBetween(
                                                                                    ((var10000 as Pair).getFirst() as java.lang.Number).longValue(),
                                                                                    ((var10000 as Pair).getSecond() as java.lang.Number).longValue()
                                                                                 )
                                                                              }
                                                                           }

                                                                           physLevel.enableCollisionBetween(
                                                                              ((var10000 as Pair).getFirst() as java.lang.Number).longValue(),
                                                                              ((var10000 as Pair).getSecond() as java.lang.Number).longValue()
                                                                           )
                                                                        }
                                                                     }

                                                                     val var68: Pair = var10000 as Pair
                                                                     val var91: PhysShip = physLevel.getShipById(
                                                                        ((var10000 as Pair).getFirst() as java.lang.Number).longValue()
                                                                     )
                                                                     if (var91 != null) {
                                                                        var91.isStatic = var68.getSecond()
                                                                     }
                                                                  }
                                                               }

                                                               val var66: Int = var10000 as Int
                                                               val var89: VsiPhysLevel = physLevel as VsiPhysLevel
                                                               var89.removeJoint(var66)
                                                            }
                                                         }

                                                         (physLevel as VsiPhysLevel)
                                                            .updateJoint((var10000 as VSJointAndId).jointId, (var10000 as VSJointAndId).joint)
                                                         }
                                                   }

                                                   val var36: Pair = var10000 as Pair
                                                   val var64: PhysShip = physLevel.getShipById(((var10000 as Pair).getFirst() as java.lang.Number).longValue())
                                                   if ((var36.getSecond() as GameToPhysicsAdapter.ForceAtPos).pos != null) {
                                                      if (var64 != null) {
                                                         val var97: Vector3dc = (var36.getSecond() as GameToPhysicsAdapter.ForceAtPos).force
                                                         val var101: Vector3dc = (var36.getSecond() as GameToPhysicsAdapter.ForceAtPos).pos
                                                         var64.applyWorldForceToBodyPos(var97, var101)
                                                      }
                                                   } else if (var64 != null) {
                                                      PhysShip.applyWorldForceToBodyPos$default(
                                                         var64, (var36.getSecond() as GameToPhysicsAdapter.ForceAtPos).force, null, 2, null
                                                      )
                                                   }
                                                }
                                             }

                                             val var35: Pair = var10000 as Pair
                                             val var63: PhysShip = physLevel.getShipById(((var10000 as Pair).getFirst() as java.lang.Number).longValue())
                                             if ((var35.getSecond() as GameToPhysicsAdapter.ForceAtPos).pos != null) {
                                                if (var63 != null) {
                                                   val var96: Vector3dc = (var35.getSecond() as GameToPhysicsAdapter.ForceAtPos).force
                                                   val var100: Vector3dc = (var35.getSecond() as GameToPhysicsAdapter.ForceAtPos).pos
                                                   var63.applyWorldForceToModelPos(var96, var100)
                                                }
                                             } else if (var63 != null) {
                                                PhysShip.applyWorldForceToModelPos$default(
                                                   var63, (var35.getSecond() as GameToPhysicsAdapter.ForceAtPos).force, null, 2, null
                                                )
                                             }
                                          }
                                       }

                                       val var34: Pair = var10000 as Pair
                                       val var62: PhysShip = physLevel.getShipById(((var10000 as Pair).getFirst() as java.lang.Number).longValue())
                                       if (var62 != null) {
                                          var62.applyBodyTorque(var34.getSecond() as Vector3dc)
                                       }
                                    }
                                 }

                                 val var33: Pair = var10000 as Pair
                                 val var61: PhysShip = physLevel.getShipById(((var10000 as Pair).getFirst() as java.lang.Number).longValue())
                                 if ((var33.getSecond() as GameToPhysicsAdapter.ForceAtPos).pos != null) {
                                    if (var61 != null) {
                                       val var95: Vector3dc = (var33.getSecond() as GameToPhysicsAdapter.ForceAtPos).force
                                       val var99: Vector3dc = (var33.getSecond() as GameToPhysicsAdapter.ForceAtPos).pos
                                       var61.applyBodyForce(var95, var99)
                                    }
                                 } else if (var61 != null) {
                                    PhysShip.applyBodyForce$default(var61, (var33.getSecond() as GameToPhysicsAdapter.ForceAtPos).force, null, 2, null)
                                 }
                              }
                           }

                           val var32: Pair = var10000 as Pair
                           val var60: PhysShip = physLevel.getShipById(((var10000 as Pair).getFirst() as java.lang.Number).longValue())
                           if (var60 != null) {
                              var60.applyModelTorque(var32.getSecond() as Vector3dc)
                           }
                        }
                     }

                     val var31: Pair = var10000 as Pair
                     val var59: PhysShip = physLevel.getShipById(((var10000 as Pair).getFirst() as java.lang.Number).longValue())
                     if ((var31.getSecond() as GameToPhysicsAdapter.ForceAtPos).pos != null) {
                        if (var59 != null) {
                           val var94: Vector3dc = (var31.getSecond() as GameToPhysicsAdapter.ForceAtPos).force
                           val var98: Vector3dc = (var31.getSecond() as GameToPhysicsAdapter.ForceAtPos).pos
                           var59.applyModelForce(var94, var98)
                        }
                     } else if (var59 != null) {
                        PhysShip.applyModelForce$default(var59, (var31.getSecond() as GameToPhysicsAdapter.ForceAtPos).force, null, 2, null)
                     }
                  }
               }

               val var30: Pair = var10000 as Pair
               val var58: PhysShip = physLevel.getShipById(((var10000 as Pair).getFirst() as java.lang.Number).longValue())
               if (var58 != null) {
                  var58.applyWorldTorque(var30.getSecond() as Vector3dc)
               }
            }
         }

         val `$this$forEach$iv`: Pair = var10000 as Pair
         val pair: PhysShip = physLevel.getShipById(((var10000 as Pair).getFirst() as java.lang.Number).longValue())
         if ((`$this$forEach$iv`.getSecond() as GameToPhysicsAdapter.ForceAtPos).pos != null) {
            if (pair != null) {
               val var10001: Vector3dc = (`$this$forEach$iv`.getSecond() as GameToPhysicsAdapter.ForceAtPos).force
               val var10002: Vector3dc = (`$this$forEach$iv`.getSecond() as GameToPhysicsAdapter.ForceAtPos).pos
               pair.applyWorldForce(var10001, var10002)
            }
         } else if (pair != null) {
            PhysShip.applyWorldForce$default(pair, (`$this$forEach$iv`.getSecond() as GameToPhysicsAdapter.ForceAtPos).force, null, 2, null)
         }
      }
   }

   public fun applyWorldForce(ship: Long, forceInWorld: Vector3dc, posInWorld: Vector3dc?) {
      this.worldForces.add(TuplesKt.to(ship, GameToPhysicsAdapter.ForceAtPos(forceInWorld, posInWorld)))
   }

   public fun applyWorldTorque(ship: Long, torqueInWorld: Vector3dc) {
      this.worldTorques.add(TuplesKt.to(ship, torqueInWorld))
   }

   public fun applyModelForce(ship: Long, forceInShip: Vector3dc, posInShip: Vector3dc?) {
      this.modelForces.add(TuplesKt.to(ship, GameToPhysicsAdapter.ForceAtPos(forceInShip, posInShip)))
   }

   public fun applyModelTorque(ship: Long, torqueInShip: Vector3dc) {
      this.modelTorques.add(TuplesKt.to(ship, torqueInShip))
   }

   public fun applyWorldForceToModelPos(ship: Long, forceInWorld: Vector3dc, posInShip: Vector3dc) {
      this.worldToModelForces.add(TuplesKt.to(ship, GameToPhysicsAdapter.ForceAtPos(forceInWorld, posInShip)))
   }

   public fun applyBodyForce(ship: Long, forceInBody: Vector3dc, posInBody: Vector3dc = Vector3d() as Vector3dc) {
      this.bodyForces.add(TuplesKt.to(ship, GameToPhysicsAdapter.ForceAtPos(forceInBody, posInBody)))
   }

   public fun applyBodyTorque(ship: Long, torqueInBody: Vector3dc) {
      this.bodyTorques.add(TuplesKt.to(ship, torqueInBody))
   }

   public fun applyWorldForceToBodyPos(ship: Long, forceInWorld: Vector3dc, posInBody: Vector3dc = Vector3d() as Vector3dc) {
      this.worldToBodyForces.add(TuplesKt.to(ship, GameToPhysicsAdapter.ForceAtPos(forceInWorld, posInBody)))
   }

   @Deprecated(message = "Use applyWorldForceToBodyPos instead")
   public fun applyInvariantForce(ship: Long, force: Vector3dc) {
      applyWorldForceToBodyPos$default(this, ship, force, null, 4, null)
   }

   @Deprecated(message = "Use applyWorldTorque instead")
   public fun applyInvariantTorque(ship: Long, torque: Vector3dc) {
      this.applyWorldTorque(ship, torque)
   }

   @Deprecated(message = "Use applyBodyForce instead")
   public fun applyRotDependentForce(ship: Long, force: Vector3dc) {
      applyBodyForce$default(this, ship, force, null, 4, null)
   }

   @Deprecated(message = "Use applyBodyTorque instead")
   public fun applyRotDependentTorque(ship: Long, torque: Vector3dc) {
      this.applyBodyTorque(ship, torque)
   }

   @Deprecated(message = "Use applyWorldForceToBodyPos instead")
   public fun applyInvariantForceToPos(ship: Long, force: Vector3dc, pos: Vector3dc) {
      this.applyWorldForceToBodyPos(ship, force, pos)
   }

   @Deprecated(message = "Use applyBodyForce instead")
   public fun applyRotDependentForceToPos(ship: Long, force: Vector3dc, pos: Vector3dc) {
      this.applyBodyForce(ship, force, pos)
   }

   public fun setStatic(ship: Long, b: Boolean) {
      this.toBeStatic.add(TuplesKt.to(ship, b))
   }

   public fun addJoint(joint: VSJoint, delay: Int = 0, function: Consumer<Int>) {
      this.addedJoints.put(TuplesKt.to(joint, function), delay)
   }

   public fun updateJoint(jointAndId: VSJointAndId) {
      this.updatedJoints.add(jointAndId)
   }

   public fun removeJoint(jointId: Int) {
      this.deletedJoints.add(jointId)
   }

   public fun getJointById(jointId: Int): VSJoint? {
      return this.jointById.get(jointId)
   }

   public fun getJointsFromShip(shipId: Long): Set<Int>? {
      return this.shipToJointIds.get(shipId)
   }

   public fun getAllJoints(): Map<Int, VSJoint> {
      return MapsKt.toMap(this.jointById)
   }

   public fun getJointsByShipIds(): Map<Long, Set<Int>> {
      return MapsKt.toMap(this.shipToJointIds)
   }

   public fun enableCollisionBetween(shipA: Long, shipB: Long) {
      this.enablePairs.add(TuplesKt.to(shipA, shipB))
   }

   public fun disableCollisionBetween(shipA: Long, shipB: Long) {
      this.disablePairs.add(TuplesKt.to(shipA, shipB))
   }

   public fun getAllConnectedShips(start: Long): List<Long> {
      val visited: java.util.Set = LinkedHashSet()
      val queue: ArrayDeque = ArrayDeque()
      queue.add(start)
      visited.add(start)

      while (!(queue as java.util.Collection).isEmpty()) {
         val currentShip: Long = (queue.removeFirst() as java.lang.Number).longValue()
         val var10000: java.util.Set = this.getJointsFromShip(currentShip)
         if (var10000 != null) {
            val var8: java.util.Iterator = var10000.iterator()

            while (var8.hasNext()) {
               val var14: VSJoint = this.getJointById((var8.next() as java.lang.Number).intValue())
               if (var14 != null) {
                  var var10001: java.lang.Long = var14.shipId0
                  val var15: java.lang.Long
                  if (var10001 != null && currentShip == var10001) {
                     var15 = var14.shipId1
                  } else {
                     var10001 = var14.shipId1
                     var15 = if (var10001 != null && currentShip == var10001) var14.shipId0 else null
                  }

                  if (var15 != null && visited.add(var15)) {
                     queue.add(var15)
                  }
               }
            }
         }
      }

      return CollectionsKt.toList(visited)
   }

   public fun getLiquidOverlap(id: Long): Double? {
      return this.shipToLiquidOverlap.get(id)
   }

   private data class ForceAtPos(force: Vector3dc, pos: Vector3dc?) {
      public final val force: Vector3dc
      public final val pos: Vector3dc?

      init {
         this.force = force
         this.pos = pos
      }

      public operator fun component1(): Vector3dc {
         return this.force
      }

      public operator fun component2(): Vector3dc? {
         return this.pos
      }

      public fun copy(force: Vector3dc = this.force, pos: Vector3dc? = this.pos): org.valkyrienskies.mod.common.util.GameToPhysicsAdapter.ForceAtPos {
         return GameToPhysicsAdapter.ForceAtPos(force, pos)
      }

      public override fun toString(): String {
         return "ForceAtPos(force=${this.force}, pos=${this.pos})"
      }

      public override fun hashCode(): Int {
         return this.force.hashCode() * 31 + (if (this.pos == null) 0 else this.pos.hashCode())
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is GameToPhysicsAdapter.ForceAtPos
               && this.force == (other as GameToPhysicsAdapter.ForceAtPos).force
               && this.pos == (other as GameToPhysicsAdapter.ForceAtPos).pos
            }
      }
   }
}
