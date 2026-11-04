package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class wY implements Serializable, kW, wO {
   private static final long c = -4531815507568396090L;
   final transient tu a;
   final double b;

   private wY(double[] var1, double var2) {
      this(new sA(var1), var2);
   }

   private wY(sA var1, double var2) {
      this.a = var1;
      this.b = var2;
   }

   public final tu a() {
      return this.a;
   }

   public final double b() {
      return this.b;
   }

   @Override
   public final double a(double[] var1) {
      sA var2 = new sA(var1, false);
      wY var3 = this;
      return this.a.b(var2) + var3.b;
   }

   private double a(sA var1) {
      return this.a.b(var1) + this.b;
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof wY) {
         other = (wY)other;
         return this.b == ((wY)other).b && this.a.equals(((wY)other).a);
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return Double.valueOf(this.b).hashCode() ^ this.a.hashCode();
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      td.a(this.a, var1);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      td.a(this, "coefficients", var1);
   }
}
