package org.valkyrienskies.core.impl.shadow;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public final class rt extends qP<rc> {
   public rt() {
      super("{", "}", "; ", BS.a(Locale.getDefault()));
   }

   public rt(NumberFormat var1) {
      super("{", "}", "; ", var1);
   }

   private rt(String var1, String var2, String var3) {
      super(var1, var2, var3, BS.a(Locale.getDefault()));
   }

   private rt(String var1, String var2, String var3, NumberFormat var4) {
      super(var1, var2, var3, var4);
   }

   public static rt a() {
      return a(Locale.getDefault());
   }

   public static rt a(Locale var0) {
      return new rt(BS.a(var0));
   }

   @Override
   public final StringBuffer a(qO<rc> var1, StringBuffer var2, FieldPosition var3) {
      var1 = var1;
      return this.a(var2, var3, var1.i, var1.j, var1.k);
   }

   private rs b(String var1) {
      ParsePosition var2 = new ParsePosition(0);
      rs var3 = this.b(var1, var2);
      if (var2.getIndex() == 0) {
         throw new oZ(var1, var2.getErrorIndex(), rs.class);
      } else {
         return var3;
      }
   }

   private rs b(String var1, ParsePosition var2) {
      double[] var3;
      return (var3 = this.a(3, var1, var2)) == null ? null : new rs(var3[0], var3[1], var3[2]);
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
      rt var4 = this;
      ParsePosition var3 = new ParsePosition(0);
      rs var5 = var4.b(var2, var3);
      if (var3.getIndex() == 0) {
         throw new oZ(var2, var3.getErrorIndex(), rs.class);
      } else {
         return var5;
      }
   }
}
