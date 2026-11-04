package org.valkyrienskies.core.impl.shadow;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public final class ra extends qP<qU> {
   public ra() {
      super("{", "}", "; ", BS.a(Locale.getDefault()));
   }

   public ra(NumberFormat var1) {
      super("{", "}", "; ", var1);
   }

   private ra(String var1, String var2) {
      super(var1, var2, "; ", BS.a(Locale.getDefault()));
   }

   private ra(String var1, String var2, NumberFormat var3) {
      super(var1, var2, "; ", var3);
   }

   public static ra a() {
      return a(Locale.getDefault());
   }

   private static ra a(Locale var0) {
      return new ra(BS.a(var0));
   }

   @Override
   public final StringBuffer a(qO<qU> var1, StringBuffer var2, FieldPosition var3) {
      var1 = var1;
      return this.a(var2, var3, var1.d);
   }

   private qZ b(String var1) {
      ParsePosition var2 = new ParsePosition(0);
      qZ var3 = this.b(var1, var2);
      if (var2.getIndex() == 0) {
         throw new oZ(var1, var2.getErrorIndex(), qZ.class);
      } else {
         return var3;
      }
   }

   private qZ b(String var1, ParsePosition var2) {
      double[] var3;
      return (var3 = this.a(1, var1, var2)) == null ? null : new qZ(var3[0]);
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
      ra var4 = this;
      ParsePosition var3 = new ParsePosition(0);
      qZ var5 = var4.b(var2, var3);
      if (var3.getIndex() == 0) {
         throw new oZ(var2, var3.getErrorIndex(), qZ.class);
      } else {
         return var5;
      }
   }
}
