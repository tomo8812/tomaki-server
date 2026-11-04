package org.valkyrienskies.mod.common.config

import net.minecraft.class_2960
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType

private data class VSBlockStateInfo(id: class_2960,
   priority: Int,
   mass: Double,
   friction: Double,
   elasticity: Double,
   type: VsiBlockType?,
   noCollisionOverride: Boolean?
) {
   public final val id: class_2960
   public final val priority: Int
   public final val mass: Double
   public final val friction: Double
   public final val elasticity: Double
   public final val type: VsiBlockType?
   public final val noCollisionOverride: Boolean?

   init {
      this.id = id
      this.priority = priority
      this.mass = mass
      this.friction = friction
      this.elasticity = elasticity
      this.type = type
      this.noCollisionOverride = noCollisionOverride
   }

   public operator fun component1(): class_2960 {
      return this.id
   }

   public operator fun component2(): Int {
      return this.priority
   }

   public operator fun component3(): Double {
      return this.mass
   }

   public operator fun component4(): Double {
      return this.friction
   }

   public operator fun component5(): Double {
      return this.elasticity
   }

   public operator fun component6(): VsiBlockType? {
      return this.type
   }

   public operator fun component7(): Boolean? {
      return this.noCollisionOverride
   }

   public fun copy(
      id: class_2960 = this.id,
      priority: Int = this.priority,
      mass: Double = this.mass,
      friction: Double = this.friction,
      elasticity: Double = this.elasticity,
      type: VsiBlockType? = this.type,
      noCollisionOverride: Boolean? = this.noCollisionOverride
   ): VSBlockStateInfo {
      return VSBlockStateInfo(id, priority, mass, friction, elasticity, type, noCollisionOverride)
   }

   public override fun toString(): String {
      return "VSBlockStateInfo(id=${this.id}, priority=${this.priority}, mass=${this.mass}, friction=${this.friction}, elasticity=${this.elasticity}, type=${this.type}, noCollisionOverride=${this.noCollisionOverride})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 ((this.id.hashCode() * 31 + Integer.hashCode(this.priority)) * 31 + java.lang.Double.hashCode(this.mass)) * 31
                                    + java.lang.Double.hashCode(this.friction)
                              )
                              * 31
                           + java.lang.Double.hashCode(this.elasticity)
                     )
                     * 31
                  + (if (this.type == null) 0 else this.type.hashCode())
            )
            * 31
         + (if (this.noCollisionOverride == null) 0 else this.noCollisionOverride.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label58@
      if (this === other) {
         return true
      } else {
         return other is VSBlockStateInfo
            && this.id == (other as VSBlockStateInfo).id
            && this.priority == (other as VSBlockStateInfo).priority
            && java.lang.Double.compare(this.mass, (other as VSBlockStateInfo).mass) == 0
            && java.lang.Double.compare(this.friction, (other as VSBlockStateInfo).friction) == 0
            && java.lang.Double.compare(this.elasticity, (other as VSBlockStateInfo).elasticity) == 0
            && this.type == (other as VSBlockStateInfo).type
            && this.noCollisionOverride == (other as VSBlockStateInfo).noCollisionOverride
         }
   }
}
