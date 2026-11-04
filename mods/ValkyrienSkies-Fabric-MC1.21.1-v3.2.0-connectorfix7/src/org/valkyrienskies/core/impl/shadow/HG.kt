package org.valkyrienskies.core.impl.shadow

import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3ic
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic

public data class HG(p0: Vector3ic, p1: Vector3ic, p2: AABBic, p3: GY, p4: Boolean = false, p5: Int = 0, p6: Vector3dc = Vector3d() as Vector3dc) : Hp {
   public final val e: Vector3ic
   public final val d: Vector3ic
   public final val f: AABBic
   private final val c: GY
   public open var b: Boolean
   public open val g: Int
   public open var h: Vector3dc
   private final var a: Boolean

   init {
      this.a = var1
      this.b = var2
      this.c = var3
      this.d = var4
      this.e = var5
      this.f = var6
      this.g = var7
      this.h = true
   }

   public override fun a(p0: Int, p1: Int, p2: Int, p3: Ik): Ik? {
      this.h = false
      return null
   }

   public override fun b(p0: Int, p1: Int, p2: Int, p3: Ik) {
      this.h = false
   }

   public override fun a(p0: Int, p1: Int, p2: Int): Ik? {
      this.h = false
      return null
   }

   public override fun b(p0: Int, p1: Int, p2: Int) {
      this.h = false
   }

   public override fun c(p0: Int, p1: Int, p2: Int): Ik? {
      this.h = false
      return null
   }

   public override fun d(p0: Int, p1: Int, p2: Int) {
      this.h = false
   }

   public override fun a() {
      this.h = true
   }

   public override fun i_(): Boolean {
      return this.h
   }

   public override fun e(p0: Int, p1: Int, p2: Int): Ik? {
      return null
   }

   public override fun f(p0: Int, p1: Int, p2: Int): Boolean {
      return true
   }

   public override fun g(p0: Int, p1: Int, p2: Int): Ij? {
      return null
   }

   public override fun a(p0: Int, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int) {
   }

   public override fun a(p0: AABBi): Boolean {
      var1.setMin(0, 0, 0)
      var1.setMax(0, 0, 0)
      return true
   }

   public override fun e(): GY {
      return this.d
   }

   public override fun close() {
   }

   public operator fun j(): Vector3ic {
      return this.a
   }

   public operator fun k(): Vector3ic {
      return this.b
   }

   public operator fun l(): AABBic {
      return this.c
   }

   private operator fun m(): GY {
      return this.d
   }

   public operator fun n(): Boolean {
      return this.e
   }

   public operator fun o(): Int {
      return this.f
   }

   public operator fun p(): Vector3dc {
      return this.g
   }

   @JvmStatic
   public fun a(p0: Vector3ic = ..., p1: Vector3ic = ..., p2: AABBic = ..., p3: GY = ..., p4: Boolean = ..., p5: Int = ..., p6: Vector3dc = ...): HG {
      return HG(var0, var1, var2, var3, var4, var5, var6)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: HG, var1: Vector3ic, var2: Vector3ic, var3: AABBic, var4: GY, var5: Boolean, var6: Int, var7: Vector3dc, var8: Int): HG {
      if ((var8 and 1) != 0) {
         var1 = var0.a
      }

      if ((var8 and 2) != 0) {
         var2 = var0.b
      }

      if ((var8 and 4) != 0) {
         var3 = var0.c
      }

      if ((var8 and 8) != 0) {
         var4 = var0.d
      }

      if ((var8 and 16) != 0) {
         var5 = var0.e
      }

      if ((var8 and 32) != 0) {
         var6 = var0.f
      }

      if ((var8 and 64) != 0) {
         var7 = var0.g
      }

      HG(var1, var2, var3, var4, var5, var6, var7)
   }

   public override fun toString(): String {
      return "HG(e=${this.a}, d=${this.b}, f=${this.c}, c=${this.d}, b=${this.e}, g=${this.f}, h=${this.g})"
   }

   public override fun hashCode(): Int {
      val var10000: Int = (((this.a.hashCode() * 31 + this.b.hashCode()) * 31 + this.c.hashCode()) * 31 + this.d.hashCode()) * 31
      var var10001: Byte = this.e
      if (this.e) {
         var10001 = 1
      }

      return ((var10000 + var10001) * 31 + Integer.hashCode(this.f)) * 31 + this.g.hashCode()
   }

   public override operator fun equals(p0: Any?): Boolean {
      label58@
      if (this === other) {
         return true
      } else {
         return other is HG
            && this.a == (other as HG).a
            && this.b == (other as HG).b
            && this.c == (other as HG).c
            && this.d == (other as HG).d
            && this.e == (other as HG).e
            && this.f == (other as HG).f
            && this.g == (other as HG).g
         }
   }
}
