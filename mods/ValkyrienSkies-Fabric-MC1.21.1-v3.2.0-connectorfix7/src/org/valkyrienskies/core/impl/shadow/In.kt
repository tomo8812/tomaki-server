package org.valkyrienskies.core.impl.shadow

import java.util.Arrays
import org.joml.Vector3ic

public data class In(regionX: Int, regionY: Int, regionZ: Int, voxelDataRaw: IntArray = IntArray(4096)) : Iq {
   @JvmStatic
   public In.a a = In.a(null);
   public int b;
   public int c;
   public int d;
   public int[] e;

   init {
      this.b = var1
      this.c = var2
      this.d = var3
      this.e = var4
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

   @JvmName(name = "d")
   override final fun d(): It {
      It.DENSE
   }

   fun a(var1: Int, var2: Int, var3: Int, var4: Int) {
      this.e[b(var1, var2, var3)] = var4
   }

   fun a(var1: Int, var2: Int, var3: Int): Int {
      this.e[b(var1, var2, var3)]
   }

   fun a(var1: (Int?, Int?, Int?) -> Int) {
      var var2: Int = 0

      while (var2 < 16) {
         val var3: Int = var2++
         var var4: Int = 0

         while (var4 < 16) {
            val var5: Int = var4++
            var var6: Int = 0

            while (var6 < 16) {
               val var7: Int = var6++
               this.a(var7, var3, var5, (var1(var7, var3, var5) as java.lang.Number).intValue())
            }
         }
      }
   }

   fun a(var1: (Int?, Int?, Int?, Int?) -> Unit) {
      var var2: Int = 0

      while (var2 < 16) {
         val var3: Int = var2++
         var var4: Int = 0

         while (var4 < 16) {
            val var5: Int = var4++
            var var6: Int = 0

            while (var6 < 16) {
               val var7: Int = var6++
               var1(var7, var3, var5, this.a(var7, var3, var5))
            }
         }
      }
   }

   @JvmStatic
   fun b(var0: (Int?, Int?, Int?) -> Unit) {
      var var1: Int = 0

      while (var1 < 16) {
         val var2: Int = var1++
         var var3: Int = 0

         while (var3 < 16) {
            val var4: Int = var3++
            var var5: Int = 0

            while (var5 < 16) {
               var0(var5++, var2, var4)
            }
         }
      }
   }

   @JvmStatic
   fun b(var0: Int, var1: Int, var2: Int): Int {
      var0 or var2 shl 4 or var1 shl 8
   }

   fun e(): IntArray {
      this.e
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true
      } else if (!(this.getClass() == (if (other == null) null else other.getClass()))) {
         return false
      } else if (other == null) {
         throw NullPointerException("null cannot be cast to non-null type org.valkyrienskies.physics_api.voxel.updates.DenseVoxelShapeUpdate")
      } else {
         return this.b == (other as In).b && this.c == (other as In).c && this.d == (other as In).d && Arrays.equals(this.e, (other as In).e)
      }
   }

   public override fun hashCode(): Int {
      return ((this.b * 31 + this.c) * 31 + this.d) * 31 + Arrays.hashCode(this.e)
   }

   fun f(): Int {
      this.b
   }

   fun g(): Int {
      this.c
   }

   fun h(): Int {
      this.d
   }

   fun i(): IntArray {
      this.e
   }

   @JvmStatic
   fun a(var0: Int, var1: Int, var2: Int, var3: IntArray): In {
      In(var0, var1, var2, var3)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: In, var1: Int, var2: Int, var3: Int, var4: IntArray, var5: Int): In {
      if ((var5 and 1) != 0) {
         var1 = var0.b
      }

      if ((var5 and 2) != 0) {
         var2 = var0.c
      }

      if ((var5 and 4) != 0) {
         var3 = var0.d
      }

      if ((var5 and 8) != 0) {
         var4 = var0.e
      }

      In(var1, var2, var3, var4)
   }

   public override fun toString(): String {
      return "In(a=${this.b}, b=${this.c}, c=${this.d}, d=${Arrays.toString(this.e)})"
   }

   public companion object a {
      @JvmStatic
      public fun a(p0: Vector3ic): In {
         return In(var0.x(), var0.y(), var0.z(), null, 8, null)
      }
   }
}
