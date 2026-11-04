package org.valkyrienskies.core.impl.shadow

public interface GY : AutoCloseable {
   @JvmStatic
   GY.a a = GY.a.a;
   @JvmStatic
   int b = 0;
   @JvmStatic
   int c = 0;
   @JvmStatic
   int d = 0;

   fun a(var1: Id, var2: Hm)

   fun a(var1: Ib, var2: Hm)

   fun a(var1: Int, var2: Int, var3: Int): Ia

   fun a(var1: Int, var2: Hm): Id

   fun a(var1: Hm): MutableMap<Int, Id>

   fun b(var1: Int, var2: Hm): Ib

   fun b(var1: Hm): MutableMap<Int, Id>

   fun c(var1: Int, var2: Hm): Ia

   fun c(var1: Hm): MutableMap<Int, Ia>

   fun a(): Boolean

   @JvmName(name = "b")
   fun b(): Id

   @JvmName(name = "c")
   fun c(): Ib

   @JvmName(name = "d")
   fun d(): Ia

   // $VF: Class flags could not be determined
   internal class a {
      @JvmStatic
      @JvmSynthetic
      GY.a a = GY.a();
      @JvmStatic
      private int b;
      @JvmStatic
      private int c;
      @JvmStatic
      private int d;

      fun a() {
      }
   }
}
