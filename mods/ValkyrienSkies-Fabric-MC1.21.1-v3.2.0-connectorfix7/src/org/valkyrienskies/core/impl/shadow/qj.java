package org.valkyrienskies.core.impl.shadow;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class qj extends qc {
   private static final long a = 3008655719530972611L;

   public qj() {
   }

   private qj(NumberFormat var1) {
      super(var1);
   }

   public qj(NumberFormat var1, NumberFormat var2) {
      super(var1, var2);
   }

   private static Locale[] e() {
      return NumberFormat.getAvailableLocales();
   }

   private static String a(qg var0) {
      Locale var1 = Locale.getDefault();
      return new qj(a(var1)).format(var0);
   }

   private static qj f() {
      return b(Locale.getDefault());
   }

   private static qj b(Locale var0) {
      return new qj(a(var0));
   }

   private static qj g() {
      Locale var0 = Locale.getDefault();
      return new ql(a(var0));
   }

   private static ql c(Locale var0) {
      return new ql(a(var0));
   }

   protected static NumberFormat d() {
      return a(Locale.getDefault());
   }

   public StringBuffer a(qg var1, StringBuffer var2, FieldPosition var3) {
      var3.setBeginIndex(0);
      var3.setEndIndex(0);
      this.c().format(var1.d, var2, var3);
      var2.append(" / ");
      this.b().format(var1.c, var2, var3);
      return var2;
   }

   @Override
   public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
      StringBuffer var4;
      if (obj instanceof qg) {
         var4 = this.a((qg)obj, toAppendTo, pos);
      } else {
         if (!(obj instanceof Number)) {
            throw new oV(px.CANNOT_FORMAT_OBJECT_TO_FRACTION);
         }

         var4 = this.a(new qg(((Number)obj).doubleValue()), toAppendTo, pos);
      }

      return var4;
   }

   private qg a(String var1) {
      ParsePosition var2 = new ParsePosition(0);
      qg var3 = this.c(var1, var2);
      if (var2.getIndex() == 0) {
         throw new oZ(var1, var2.getErrorIndex(), qg.class);
      } else {
         return var3;
      }
   }

   public qg c(String var1, ParsePosition var2) {
      int var3 = var2.getIndex();
      a(var1, var2);
      Number var4;
      if ((var4 = this.c().parse(var1, var2)) == null) {
         var2.setIndex(var3);
         return null;
      }

      int var5 = var2.getIndex();
      switch (b(var1, var2)) {
         case '\u0000':
            return new qg(var4.intValue(), 1);
         case '/':
            a(var1, var2);
            Number var7;
            if ((var7 = this.b().parse(var1, var2)) == null) {
               var2.setIndex(var3);
               return null;
            }

            return new qg(var4.intValue(), var7.intValue());
         default:
            var2.setIndex(var3);
            var2.setErrorIndex(var5);
            return null;
      }
   }

   // $VF: synthetic method
   @Override
   public Number parse(String var1) {
      String var2 = var1;
      qj var4 = this;
      ParsePosition var3 = new ParsePosition(0);
      qg var5 = var4.c(var2, var3);
      if (var3.getIndex() == 0) {
         throw new oZ(var2, var3.getErrorIndex(), qg.class);
      } else {
         return var5;
      }
   }

   // $VF: synthetic method
   @Override
   public Number parse(String var1, ParsePosition var2) {
      return this.c(var1, var2);
   }
}
