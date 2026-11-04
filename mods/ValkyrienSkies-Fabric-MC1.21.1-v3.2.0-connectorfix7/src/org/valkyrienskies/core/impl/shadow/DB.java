package org.valkyrienskies.core.impl.shadow;

import java.util.Random;

public final class DB {
   private static final <unrepresentable> a = new ThreadLocal<Random>() {
      private static Random a() {
         return new Random();
      }

      // $VF: synthetic method
      @Override
      protected final Object initialValue() {
         return new Random();
      }
   };
   private final int b;

   public DB() {
      this.b = ((Random)a.get()).nextInt();
   }

   private DB(Random var1) {
      this.b = var1.nextInt();
   }

   @Override
   public final int hashCode() {
      return this.b;
   }
}
