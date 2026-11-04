package org.valkyrienskies.core.impl.shadow

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import org.joml.Vector3d
import org.joml.primitives.AABBi
import org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape
import org.valkyrienskies.core.api.physics.blockstates.CollisionPoint
import org.valkyrienskies.core.api.physics.blockstates.LiquidState
import org.valkyrienskies.core.api.physics.blockstates.SolidState
import org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder
import org.valkyrienskies.core.internal.physics.blockstates.VsiBlockState
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType
import org.valkyrienskies.core.internal.world.chunks.VsiBlockTypes

@Singleton
public class DN @Inject  public constructor(p0: CP,
      p1: Provider<Builder>,
      p2: Provider<org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder>,
      p3: Provider<org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder>
   ) :
   VsiBlockTypes {
   public final val f: CP
   public open lateinit var d: VsiBlockType
   public open lateinit var k: VsiBlockType
   public open lateinit var m: VsiBlockType
   public open lateinit var i: VsiBlockType
   public open lateinit var g: VsiBlockType
   public open lateinit var e: VsiBlockState
   public open lateinit var l: VsiBlockState
   public open lateinit var n: VsiBlockState
   public open lateinit var j: VsiBlockState
   public open lateinit var h: VsiBlockState
   private final val a: Double
   private final val b: Double
   private final val c: Double

   init {
      this.b = var1
      this.m = 0.3
      this.n = 0.5
      this.o = 1.0
      val var7: AABBi = AABBi(0, 0, 0, 15, 15, 15)
      val var5: Array<CollisionPoint>
      (var5 = arrayOfNulls(8))[0] = CollisionPoint(0.25F, 0.25F, 0.25F, 0.25F)
      var5[1] = CollisionPoint(0.25F, 0.25F, 0.75F, 0.25F)
      var5[2] = CollisionPoint(0.25F, 0.75F, 0.25F, 0.25F)
      var5[3] = CollisionPoint(0.25F, 0.75F, 0.75F, 0.25F)
      var5[4] = CollisionPoint(0.75F, 0.25F, 0.25F, 0.25F)
      var5[5] = CollisionPoint(0.75F, 0.25F, 0.75F, 0.25F)
      var5[6] = CollisionPoint(0.75F, 0.75F, 0.25F, 0.25F)
      var5[7] = CollisionPoint(0.75F, 0.75F, 0.75F, 0.25F)
      val var32: BoxesBlockShape = (var4.get() as BoxesBlockShape.Builder).addCollisionPoints(CollectionsKt.listOf(var5)).addPositiveBox(var7).build()
      val var20: BoxesBlockShape = (var4.get() as BoxesBlockShape.Builder).addCollisionPoints(CollectionsKt.emptyList()).addNegativeBox(var7).build()
      val var33: SolidState = (var3.get() as SolidState.Builder).shape(var32).elasticity(this.m).friction(this.n).hardness(this.o).build()
      val var6: LiquidState = (var2.get() as LiquidState.Builder).boxShape(var7).density(100.0).dragCoefficient(0.3).velocity(Vector3d()).build()
      val var8: LiquidState = (var2.get() as LiquidState.Builder).boxShape(var7).density(1000.0).dragCoefficient(1.0).velocity(Vector3d()).build()
      val var9: SolidState = (var3.get() as SolidState.Builder).shape(var20).elasticity(this.m).friction(this.n).hardness(this.o).build()
      val var21: VsiBlockType = DM(0)
      this.c = var21
      val var22: VsiBlockState = VsiBlockState(null, null)
      this.h = var22
      val var23: VsiBlockState = VsiBlockState(var33, null)
      this.i = var23
      val var24: VsiBlockType = DM(this.b.b(this.l))
      this.d = var24
      val var25: VsiBlockState = VsiBlockState(null, var6)
      this.j = var25
      val var26: VsiBlockType = DM(this.b.b(this.n))
      this.e = var26
      val var27: VsiBlockState = VsiBlockState(null, var8)
      this.k = var27
      val var28: VsiBlockType = DM(this.b.b(this.j))
      this.f = var28
      val var29: VsiBlockState = VsiBlockState(var9, null)
      this.l = var29
      val var30: VsiBlockType = DM(this.b.b(this.h))
      this.g = var30
   }

   public override fun getType(p0: VsiBlockState): VsiBlockType? {
      val var3: CP = this.b
      val var4: Int
      return if ((var4 = var3.c.getInt(state)) < 0) null else DM(var4)
   }

   public override fun getState(p0: VsiBlockType): VsiBlockState? {
      val var4: CP = this.b
      return this.b.d.get((type as DM).a) as VsiBlockState
   }

   public companion object a {
      public const val a: Int = 0
      public const val b: Int = 1
      public const val c: Int = 2
   }
}
