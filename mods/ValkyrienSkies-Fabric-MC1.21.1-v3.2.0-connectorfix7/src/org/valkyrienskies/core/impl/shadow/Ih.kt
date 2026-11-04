package org.valkyrienskies.core.impl.shadow

public data class Ih(data: Int = 0) {
   @JvmStatic
   public Ih.a a = Ih.a(null);
   public int b;

   init {
      this.b = var1
   }

   @JvmName(name = "a")
   fun a(): Int {
      this.b
   }

   @JvmName(name = "b")
   fun b(): Byte {
      (byte)(this.b and 15)
   }

   @JvmName(name = "c")
   fun c(): Byte {
      (byte)(this.b shr 4 and 15)
   }

   @JvmName(name = "d")
   fun d(): Byte {
      (byte)(this.b shr 8 and 15)
   }

   @JvmName(name = "e")
   fun e(): Byte {
      (byte)(this.b shr 12 and 15)
   }

   @JvmName(name = "f")
   fun f(): Byte {
      (byte)(this.b shr 16 and 15)
   }

   @JvmName(name = "g")
   fun g(): Byte {
      (byte)(this.b shr 20 and 15)
   }

   @JvmName(name = "h")
   fun h(): Boolean {
      this.b shr 24 == 0
   }

   fun i(): Int {
      this.b
   }

   @JvmStatic
   fun a(var0: Int): Ih {
      Ih(var0)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: Ih, var1: Int, var2: Int): Ih {
      if ((var2 and 1) != 0) {
         var1 = var0.b
      }

      Ih(var1)
   }

   public override fun toString(): String {
      return "Ih(a=${this.b})"
   }

   public override fun hashCode(): Int {
      return Integer.hashCode(this.b)
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is Ih && this.b == (other as Ih).b
      }
   }

   fun Ih() {
      this(0, 1, null)
   }

   public companion object a {
      @JvmStatic
      fun a(var0: Byte, var1: Byte, var2: Byte, var3: Byte, var4: Byte, var5: Byte): Ih {
         Ih(var0 or var1 shl 4 or var2 shl 8 or var3 shl 12 or var4 shl 16 or var5 shl 20 or 16777216)
      }

      @JvmStatic
      fun a(var0: Int): Ih {
         Ih(var0)
      }

      @JvmStatic
      fun a(): Ih {
         Ih(0, 1, null)
      }

      fun b(): Ih {
         a((byte)0, (byte)0, (byte)0, (byte)15, (byte)15, (byte)15)
      }
   }
}
