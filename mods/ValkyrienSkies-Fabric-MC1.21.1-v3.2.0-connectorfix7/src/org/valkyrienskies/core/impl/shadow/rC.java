package org.valkyrienskies.core.impl.shadow;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public final class rC extends qP<rv> {
   public rC() {
      super("{", "}", "; ", BS.a(Locale.getDefault()));
   }

   public rC(NumberFormat var1) {
      super("{", "}", "; ", var1);
   }

   private rC(String var1, String var2, String var3) {
      super(var1, var2, var3, BS.a(Locale.getDefault()));
   }

   private rC(String var1, String var2, String var3, NumberFormat var4) {
      super(var1, var2, var3, var4);
   }

   public static rC a() {
      return a(Locale.getDefault());
   }

   private static rC a(Locale var0) {
      return new rC(BS.a(var0));
   }

   @Override
   public final StringBuffer a(qO<rv> var1, StringBuffer var2, FieldPosition var3) {
      var1 = var1;
      return this.a(var2, var3, var1.c, var1.d);
   }

   private rB b(String var1) {
      ParsePosition var2 = new ParsePosition(0);
      rB var3 = this.b(var1, var2);
      if (var2.getIndex() == 0) {
         throw new oZ(var1, var2.getErrorIndex(), rB.class);
      } else {
         return var3;
      }
   }

   private rB b(String var1, ParsePosition var2) {
      double[] var3;
      return (var3 = this.a(2, var1, var2)) == null ? null : new rB(var3[0], var3[1]);
   }

   // $VF: synthetic method
   @Override
   public final qO a(String var1, ParsePosition var2) {
      return this.b(var1, var2);
   }

   // $VF: synthetic method
   @Override
   public final qO a(String var1) {
      String var2 = var1;
      rC var4 = this;
      ParsePosition var3 = new ParsePosition(0);
      rB var5 = var4.b(var2, var3);
      if (var3.getIndex() == 0) {
         throw new oZ(var2, var3.getErrorIndex(), rB.class);
      } else {
         return var5;
      }
   }
}
