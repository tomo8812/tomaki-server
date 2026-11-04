package org.valkyrienskies.core.impl.api_impl.physics.blockstates

import javax.inject.Inject
import kotlin.jvm.internal.DefaultConstructorMarker
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.physics.blockstates.LiquidBlockShape
import org.valkyrienskies.core.api.physics.blockstates.LiquidState

public data class LiquidStateImpl private constructor(shape: LiquidBlockShape, density: Double, dragCoefficient: Double, velocity: Vector3dc) : LiquidState {
   public open val shape: LiquidBlockShape
   public open val density: Double
   public open val dragCoefficient: Double
   public open val velocity: Vector3dc

   init {
      this.shape = shape
      this.density = density
      this.dragCoefficient = dragCoefficient
      this.velocity = velocity
   }

   public override fun toString(): String {
      return "LiquidState(shape=${this.shape}, density=${this.density}, dragCoefficient=${this.dragCoefficient}, velocity=${this.velocity})"
   }

   public operator fun component1(): LiquidBlockShape {
      return this.shape
   }

   public operator fun component2(): Double {
      return this.density
   }

   public operator fun component3(): Double {
      return this.dragCoefficient
   }

   public operator fun component4(): Vector3dc {
      return this.velocity
   }

   public fun copy(
      shape: LiquidBlockShape = this.shape,
      density: Double = this.density,
      dragCoefficient: Double = this.dragCoefficient,
      velocity: Vector3dc = this.velocity
   ): LiquidStateImpl {
      return LiquidStateImpl(shape, density, dragCoefficient, velocity)
   }

   public override fun hashCode(): Int {
      return ((this.shape.hashCode() * 31 + java.lang.Double.hashCode(this.density)) * 31 + java.lang.Double.hashCode(this.dragCoefficient)) * 31
         + this.velocity.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is LiquidStateImpl
            && this.shape == (other as LiquidStateImpl).shape
            && java.lang.Double.compare(this.density, (other as LiquidStateImpl).density) == 0
            && java.lang.Double.compare(this.dragCoefficient, (other as LiquidStateImpl).dragCoefficient) == 0
            && this.velocity == (other as LiquidStateImpl).velocity
         }
   }

   @JvmSynthetic
   fun LiquidStateImpl(shape: LiquidBlockShape, density: Double, dragCoefficient: Double, velocity: Vector3dc, `$constructor_marker`: DefaultConstructorMarker) {
      this(shape, density, dragCoefficient, velocity)
   }

   public class BuilderImpl @Inject  public constructor() : LiquidState.Builder {
      private final var shape: LiquidBlockShape?
      private final var density: Double?
      private final var dragCoefficient: Double?
      private final var velocity: Vector3d = Vector3d()

      public open fun boxShape(boundingBox: AABBic): org.valkyrienskies.core.impl.api_impl.physics.blockstates.LiquidStateImpl.BuilderImpl {
         InternalUtilsKt.require4bits("boundingBox", boundingBox)
         this.shape = BoxBlockShapeImpl(AABBi(boundingBox))
         return this
      }

      public open fun shape(shape: LiquidBlockShape): org.valkyrienskies.core.impl.api_impl.physics.blockstates.LiquidStateImpl.BuilderImpl {
         this.shape = shape
         return this
      }

      public open fun density(density: Double): org.valkyrienskies.core.impl.api_impl.physics.blockstates.LiquidStateImpl.BuilderImpl {
         this.density = density
         return this
      }

      public open fun dragCoefficient(dragCoefficient: Double): org.valkyrienskies.core.impl.api_impl.physics.blockstates.LiquidStateImpl.BuilderImpl {
         this.dragCoefficient = dragCoefficient
         return this
      }

      public open fun velocity(velocity: Vector3dc): org.valkyrienskies.core.impl.api_impl.physics.blockstates.LiquidStateImpl.BuilderImpl {
         this.velocity.set(velocity)
         return this
      }

      public open fun build(): LiquidStateImpl {
         val var3: java.lang.Double = this.dragCoefficient
         if (this.shape == null) {
            throw IllegalArgumentException("shape must be set".toString())
         } else if (this.density == null) {
            throw IllegalArgumentException("density must be set".toString())
         } else if (this.dragCoefficient == null) {
            throw IllegalArgumentException("dragCoefficient must be set".toString())
         } else {
            return LiquidStateImpl(this.shape, this.density, var3, this.velocity, null)
         }
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun boxShape(boundingBox: AABBic): LiquidState.Builder {
         this.boxShape(boundingBox) as LiquidState.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun shape(shape: LiquidBlockShape): LiquidState.Builder {
         this.shape(shape) as LiquidState.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun density(density: Double): LiquidState.Builder {
         this.density(density) as LiquidState.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun dragCoefficient(dragCoefficient: Double): LiquidState.Builder {
         this.dragCoefficient(dragCoefficient) as LiquidState.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun velocity(velocity: Vector3dc): LiquidState.Builder {
         this.velocity(velocity) as LiquidState.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun build(): LiquidState {
         this.build() as LiquidState
      }
   }
}
