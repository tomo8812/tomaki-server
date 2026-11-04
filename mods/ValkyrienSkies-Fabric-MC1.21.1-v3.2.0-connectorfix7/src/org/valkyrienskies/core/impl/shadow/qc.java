package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public abstract class qc extends NumberFormat implements Serializable {
   private static final long a = -6981118387974191891L;
   private NumberFormat b;
   private NumberFormat c;

   protected qc() {
      this(a(Locale.getDefault()));
   }

   protected qc(NumberFormat var1) {
      this(var1, (NumberFormat)var1.clone());
   }

   protected qc(NumberFormat var1, NumberFormat var2) {
      this.c = var1;
      this.b = var2;
   }

   protected static NumberFormat a() {
      return a(Locale.getDefault());
   }

   protected static NumberFormat a(Locale var0) {
      NumberFormat var1;
      (var1 = NumberFormat.getNumberInstance(var0)).setMaximumFractionDigits(0);
      var1.setParseIntegerOnly(true);
      return var1;
   }

   public final NumberFormat b() {
      return this.b;
   }

   public final NumberFormat c() {
      return this.c;
   }

   private void a(NumberFormat var1) {
      if (var1 == null) {
         throw new pl(px.DENOMINATOR_FORMAT);
      }

      this.b = var1;
   }

   private void b(NumberFormat var1) {
      if (var1 == null) {
         throw new pl(px.NUMERATOR_FORMAT);
      }

      this.c = var1;
   }

   protected static void a(String var0, ParsePosition var1) {
      b(var0, var1);
      var1.setIndex(var1.getIndex() - 1);
   }

   protected static char b(String var0, ParsePosition var1) {
      int var2 = var1.getIndex();
      int var3 = var0.length();
      char var4 = 0;
      if (var2 < var3) {
         char var5;
         while (Character.isWhitespace(var5 = var0.charAt(var2++)) && var2 < var3) {
         }

         var1.setIndex(var2);
         if (var2 < var3) {
            var4 = var5;
         }
      }

      return var4;
   }

   @Override
   public StringBuffer format(double value, StringBuffer buffer, FieldPosition position) {
      return this.format(Double.valueOf(value), buffer, position);
   }

   @Override
   public StringBuffer format(long value, StringBuffer buffer, FieldPosition position) {
      return this.format(Long.valueOf(value), buffer, position);
   }
}
