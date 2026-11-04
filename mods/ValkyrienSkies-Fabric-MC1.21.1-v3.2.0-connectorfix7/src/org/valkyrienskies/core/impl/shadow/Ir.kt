package org.valkyrienskies.core.impl.shadow

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.shorts.ShortArrayList
import org.joml.Vector3ic

public data class Ir(regionX: Int, regionY: Int, regionZ: Int) : Iq {
   @JvmStatic
   public Ir.a a = Ir.a(null);
   public int b;
   public int c;
   public int d;
   public ShortArrayList e;
   public IntArrayList f;

   init {
      this.b = var1
      this.c = var2
      this.d = var3
      this.e = ShortArrayList()
      this.f = IntArrayList()
   }

   @JvmName(name = "a")
   override final fun a(): Int {
      this.b
   }

   @JvmName(name = "b")
   override final fun b(): Int {
      this.c
   }

   @JvmName(name = "c")
   override final fun c(): Int {
      this.d
   }

   @JvmName(name = "e")
   fun e(): ShortArrayList {
      this.e
   }

   @JvmName(name = "f")
   fun f(): IntArrayList {
      this.f
   }

   @JvmName(name = "d")
   override final fun d(): It {
      It.SPARSE
   }

   fun a(var1: Int, var2: Int, var3: Int, var4: Int) {
      this.e.add((short)a(var1, var2, var3))
      this.f.add(var4)
   }

   fun a(var1: (Int?, Int?, Int?, Int?) -> Unit) {
      var var2: Int = 0
      val var3: Int = this.e.size()

      while (var2 < var3) {
         val var4: Int = var2++
         val var9: Short
         var1((var9 = this.e.getShort(var4)) shr 8 and 15, var9 shr 4 and 15, var9 and 15, this.f.getInt(var4))
      }
   }

   @JvmStatic
   fun a(var0: Int, var1: Int, var2: Int): Int {
      var2 or var1 shl 4 or var0 shl 8
   }

   @JvmStatic
   fun a(var0: Int, var1: (Int?, Int?, Int?) -> Unit) {
      var1(var0 shr 8 and 15, var0 shr 4 and 15, var0 and 15)
   }

   fun g(): Int {
      this.b
   }

   fun h(): Int {
      this.c
   }

   fun i(): Int {
      this.d
   }

   @JvmStatic
   fun b(var0: Int, var1: Int, var2: Int): Ir {
      Ir(var0, var1, var2)
   }

   @JvmStatic
   @JvmSynthetic
   fun `b$default`(var0: Ir, var1: Int, var2: Int, var3: Int, var4: Int): Ir {
      if ((var4 and 1) != 0) {
         var1 = var0.b
      }

      if ((var4 and 2) != 0) {
         var2 = var0.c
      }

      if ((var4 and 4) != 0) {
         var3 = var0.d
      }

      Ir(var1, var2, var3)
   }

   public override fun toString(): String {
      return "Ir(a=${this.b}, b=${this.c}, c=${this.d})"
   }

   public override fun hashCode(): Int {
      return (Integer.hashCode(this.b) * 31 + Integer.hashCode(this.c)) * 31 + Integer.hashCode(this.d)
   }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is Ir && this.b == (other as Ir).b && this.c == (other as Ir).c && this.d == (other as Ir).d
      }
   }

   public companion object a {
      @JvmStatic
      public fun a(p0: Vector3ic): Ir {
         return Ir(var0.x(), var0.y(), var0.z())
      }
   }
}
