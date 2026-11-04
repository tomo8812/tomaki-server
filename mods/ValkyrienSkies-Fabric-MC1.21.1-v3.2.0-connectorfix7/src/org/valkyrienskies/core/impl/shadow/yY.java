package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Deprecated
public final class yY implements Serializable {
   private static final long c = -4531815507568396090L;
   final transient tu a;
   final double b;

   private yY(double[] var1, double var2) {
      this(new sA(var1), var2);
   }

   private yY(sA var1, double var2) {
      this.a = var1;
      this.b = var2;
   }

   public final tu a() {
      return this.a;
   }

   public final double b() {
      return this.b;
   }

   public final double a(double[] var1) {
      return this.a.b(new sA(var1, false)) + this.b;
   }

   private double a(tu var1) {
      return this.a.b(var1) + this.b;
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof yY) {
         other = (yY)other;
         return this.b == ((yY)other).b && this.a.equals(((yY)other).a);
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
