package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public class oA<T extends oC> extends od {
   private final double[] b;
   private final List<T> c;

   public oA(List<Co<Double, T>> var1) {
      this(new zR(), var1);
   }

   public oA(zF var1, List<Co<Double, T>> var2) {
      super(var1, ((oC)((Co)var2.get(0)).b).a());
      int var9 = var2.size();
      int var3 = this.a();
      double var5 = 0.0;

      for (int var4 = 0; var4 < var9; var4++) {
         Co var7;
         if (((oC)(var7 = (Co)var2.get(var4)).b).a() != var3) {
            throw new oS(((oC)var7.b).a(), var3);
         }

         if ((Double)var7.a < 0.0) {
            throw new pj((Number)var7.a);
         }

         var5 += (Double)var7.a;
      }

      if (Double.isInfinite(var5)) {
         throw new oU(px.OVERFLOW);
      }

      this.c = new ArrayList<>();
      this.b = new double[var9];

      for (int var10 = 0; var10 < var9; var10++) {
         Co var11 = (Co)var2.get(var10);
         this.b[var10] = (Double)var11.a / var5;
         this.c.add((T)var11.b);
      }
   }

   @Override
   public final double a(double[] var1) {
      double var2 = 0.0;

      for (int var4 = 0; var4 < this.b.length; var4++) {
         var2 += this.b[var4] * this.c.get(var4).a(var1);
      }

      return var2;
   }

   @Override
   public final double[] b() {
      double[] var1 = null;
      double var2 = super.a.nextDouble();
      double var4 = 0.0;

      for (int var6 = 0; var6 < this.b.length; var6++) {
         var4 += this.b[var6];
         if (var2 <= var4) {
            var1 = this.c.get(var6).b();
            break;
         }
      }

      if (var1 == null) {
         var1 = this.c.get(this.b.length - 1).b();
      }

      return var1;
   }

   @Override
   public final void a(long var1) {
      super.a(var1);

      for (int var3 = 0; var3 < this.c.size(); var3++) {
         this.c.get(var3).a(var3 + 1 + var1);
      }
   }

   public final List<Co<Double, T>> c() {
      ArrayList var1 = new ArrayList(this.b.length);

      for (int var2 = 0; var2 < this.b.length; var2++) {
         var1.add(new Co<>(this.b[var2], this.c.get(var2)));
      }

      return var1;
   }
}
