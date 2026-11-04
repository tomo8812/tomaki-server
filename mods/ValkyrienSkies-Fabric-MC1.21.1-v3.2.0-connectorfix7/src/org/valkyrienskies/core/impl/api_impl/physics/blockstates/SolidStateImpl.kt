package org.valkyrienskies.core.impl.api_impl.physics.blockstates

import javax.inject.Inject
import javax.inject.Provider
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.DefaultConstructorMarker
import org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape
import org.valkyrienskies.core.api.physics.blockstates.SolidBlockShape
import org.valkyrienskies.core.api.physics.blockstates.SolidState

public data class SolidStateImpl private constructor(elasticity: Double, friction: Double, hardness: Double, shape: SolidBlockShape) : SolidState {
   public open val elasticity: Double
   public open val friction: Double
   public open val hardness: Double
   public open val shape: SolidBlockShape

   init {
      this.elasticity = elasticity
      this.friction = friction
      this.hardness = hardness
      this.shape = shape
   }

   public operator fun component1(): Double {
      return this.elasticity
   }

   public operator fun component2(): Double {
      return this.friction
   }

   public operator fun component3(): Double {
      return this.hardness
   }

   public operator fun component4(): SolidBlockShape {
      return this.shape
   }

   public fun copy(
      elasticity: Double = this.elasticity,
      friction: Double = this.friction,
      hardness: Double = this.hardness,
      shape: SolidBlockShape = this.shape
   ): SolidStateImpl {
      return SolidStateImpl(elasticity, friction, hardness, shape)
   }

   public override fun toString(): String {
      return "SolidStateImpl(elasticity=${this.elasticity}, friction=${this.friction}, hardness=${this.hardness}, shape=${this.shape})"
   }

   public override fun hashCode(): Int {
      return ((java.lang.Double.hashCode(this.elasticity) * 31 + java.lang.Double.hashCode(this.friction)) * 31 + java.lang.Double.hashCode(this.hardness))
            * 31
         + this.shape.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is SolidStateImpl
            && java.lang.Double.compare(this.elasticity, (other as SolidStateImpl).elasticity) == 0
            && java.lang.Double.compare(this.friction, (other as SolidStateImpl).friction) == 0
            && java.lang.Double.compare(this.hardness, (other as SolidStateImpl).hardness) == 0
            && this.shape == (other as SolidStateImpl).shape
         }
   }

   @JvmSynthetic
   fun SolidStateImpl(elasticity: Double, friction: Double, hardness: Double, shape: SolidBlockShape, `$constructor_marker`: DefaultConstructorMarker) {
      this(elasticity, friction, hardness, shape)
   }

   public class BuilderImpl @Inject  public constructor(newBoxesBlockShapeBuilder: Provider<BoxesBlockShape.Builder>) : SolidState.Builder {
      private final val newBoxesBlockShapeBuilder: Provider<BoxesBlockShape.Builder>
      private final var elasticity: Double?
      private final var friction: Double?
      private final var hardness: Double?
      private final var shape: SolidBlockShape?

      init {
         this.newBoxesBlockShapeBuilder = newBoxesBlockShapeBuilder
      }

      public open fun elasticity(elasticity: Double): org.valkyrienskies.core.impl.api_impl.physics.blockstates.SolidStateImpl.BuilderImpl {
         this.elasticity = elasticity
         return this
      }

      public open fun friction(friction: Double): org.valkyrienskies.core.impl.api_impl.physics.blockstates.SolidStateImpl.BuilderImpl {
         this.friction = friction
         return this
      }

      public open fun hardness(hardness: Double): org.valkyrienskies.core.impl.api_impl.physics.blockstates.SolidStateImpl.BuilderImpl {
         this.hardness = hardness
         return this
      }

      public open fun shape(shape: SolidBlockShape): org.valkyrienskies.core.impl.api_impl.physics.blockstates.SolidStateImpl.BuilderImpl {
         this.shape = shape
         return this
      }

      public open fun boxesShape(shapeBuilder: (BoxesBlockShape.Builder) -> Unit): org.valkyrienskies.core.impl.api_impl.physics.blockstates.SolidStateImpl.BuilderImpl {
         val var3: Any = this.newBoxesBlockShapeBuilder.get()
         shapeBuilder(var3)
         this.shape = (var3 as BoxesBlockShape.Builder).build()
         return this
      }

      public open fun build(): SolidStateImpl {
         if (this.elasticity == null) {
            throw IllegalArgumentException("elasticity must be set".toString())
         } else {
            val var1: Double = this.elasticity.doubleValue()
            if (this.friction == null) {
               throw IllegalArgumentException("friction must be set".toString())
            } else {
               val var3: Double = this.friction.doubleValue()
               if (this.hardness == null) {
                  throw IllegalArgumentException("hardness must be set".toString())
               } else {
                  val var5: Double = this.hardness.doubleValue()
                  if (this.shape == null) {
                     throw IllegalArgumentException("shape must be set".toString())
                  } else {
                     return SolidStateImpl(var1, var3, var5, this.shape, null)
                  }
               }
            }
         }
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun elasticity(elasticity: Double): SolidState.Builder {
         this.elasticity(elasticity) as SolidState.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun friction(friction: Double): SolidState.Builder {
         this.friction(friction) as SolidState.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun hardness(hardness: Double): SolidState.Builder {
         this.hardness(hardness) as SolidState.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun shape(shape: SolidBlockShape): SolidState.Builder {
         this.shape(shape) as SolidState.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun boxesShape(shapeBuilder: Function1): SolidState.Builder {
         this.boxesShape(shapeBuilder) as SolidState.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun build(): SolidState {
         this.build() as SolidState
      }
   }
}
