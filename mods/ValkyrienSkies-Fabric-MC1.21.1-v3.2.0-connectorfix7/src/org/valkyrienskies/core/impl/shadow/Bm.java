package org.valkyrienskies.core.impl.shadow;

public final class Bm {
   private static final Bi a = new Bi();
   private static final Bk b = new Bk();
   private static final Bn c = new Bn();
   private static final Bo d = new Bo();

   private Bm() {
   }

   private static Bl b(int var0, int var1, double var2) {
      return a.a(var0, var1, var2);
   }

   private static Bl c(int var0, int var1, double var2) {
      return b.a(var0, var1, var2);
   }

   private static Bl d(int var0, int var1, double var2) {
      return c.a(var0, var1, var2);
   }

   private static Bl e(int var0, int var1, double var2) {
      return d.a(var0, var1, var2);
   }

   static void a(int var0, int var1, double var2) {
      if (var0 <= 0) {
         throw new pk(px.NUMBER_OF_TRIALS, var0);
      } else if (var1 < 0) {
         throw new pj(px.NEGATIVE_NUMBER_OF_SUCCESSES, var1);
      } else if (var1 > var0) {
         throw new pm(px.NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE, var1, var0, true);
      } else if (var2 <= 0.0 || var2 >= 1.0) {
         throw new po(px.OUT_OF_BOUNDS_CONFIDENCE_LEVEL, var2, 0, 1);
      }
   }
}
