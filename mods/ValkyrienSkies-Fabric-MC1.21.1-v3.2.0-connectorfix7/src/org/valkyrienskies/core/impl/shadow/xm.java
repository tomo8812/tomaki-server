package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class xm extends wH<wQ> {
   final xp a;
   private final List<wQ> b = new ArrayList<>();

   private xm(xp var1, int var2, zH var3) {
      super(var1, var2, var3);
      this.a = var1;
   }

   private wQ[] m() {
      xm var1 = this;
      Collections.sort(this.b, new Comparator<wQ>(var1) {
         // $VF: synthetic field
         private xm a;

         {
            this.a = var1;
         }

         private int a(wQ var1, wQ var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            }

            if (var2x == null) {
               return -1;
            }

            double var3 = (Double)var1.c();
            double var5 = (Double)var2x.c();
            xm var7 = this.a;
            xp var8 = this.a.a;
            return this.a.a.a == xi.MINIMIZE ? Double.compare(var3, var5) : Double.compare(var5, var3);
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            wQ var10001 = (wQ)var1;
            wQ var3 = var2;
            var2 = var10001;
            var var10 = this;
            int var10000;
            if (var2 == null) {
               if (var3 != null) {
                  return 1;
               }

               var10000 = 0;
            } else {
               if (var3 == null) {
                  return -1;
               }

               double var6 = (Double)var2.c();
               double var8 = (Double)var3.c();
               xm var11;
               if ((var11 = var10.a).a.a == xi.MINIMIZE) {
                  return Double.compare(var6, var8);
               }

               var10000 = Double.compare(var8, var6);
            }

            return var10000;
         }
      });
      return this.b.toArray(new wQ[0]);
   }

   private void a(wQ var1) {
      this.b.add(var1);
   }

   @Override
   public final void d() {
      this.b.clear();
   }

   private Comparator<wQ> n() {
      return new Comparator<wQ>(this) {
         // $VF: synthetic field
         private xm a;

         {
            this.a = var1;
         }

         private int a(wQ var1, wQ var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            }

            if (var2x == null) {
               return -1;
            }

            double var3 = (Double)var1.c();
            double var5 = (Double)var2x.c();
            xm var7 = this.a;
            xp var8 = this.a.a;
            return this.a.a.a == xi.MINIMIZE ? Double.compare(var3, var5) : Double.compare(var5, var3);
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            wQ var10001 = (wQ)var1;
            wQ var3 = var2;
            var2 = var10001;
            var var10 = this;
            int var10000;
            if (var2 == null) {
               if (var3 != null) {
                  return 1;
               }

               var10000 = 0;
            } else {
               if (var3 == null) {
                  return -1;
               }

               double var6 = (Double)var2.c();
               double var8 = (Double)var3.c();
               xm var11;
               if ((var11 = var10.a).a.a == xi.MINIMIZE) {
                  return Double.compare(var6, var8);
               }

               var10000 = Double.compare(var8, var6);
            }

            return var10000;
         }
      };
   }

   // $VF: synthetic method
   @Override
   public final void a(Object var1) {
      wQ var2 = (wQ)var1;
      this.b.add(var2);
   }

   // $VF: synthetic method
   @Override
   public final Co[] b() {
      xm var1 = this;
      xm var2 = var1;
      Collections.sort(this.b, new Comparator<wQ>(var2) {
         // $VF: synthetic field
         private xm a;

         {
            this.a = var1;
         }

         private int a(wQ var1, wQ var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            }

            if (var2x == null) {
               return -1;
            }

            double var3 = (Double)var1.c();
            double var5 = (Double)var2x.c();
            xm var7 = this.a;
            xp var8 = this.a.a;
            return this.a.a.a == xi.MINIMIZE ? Double.compare(var3, var5) : Double.compare(var5, var3);
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            wQ var10001 = (wQ)var1;
            wQ var3 = var2;
            var2 = var10001;
            var var10 = this;
            int var10000;
            if (var2 == null) {
               if (var3 != null) {
                  return 1;
               }

               var10000 = 0;
            } else {
               if (var3 == null) {
                  return -1;
               }

               double var6 = (Double)var2.c();
               double var8 = (Double)var3.c();
               xm var11;
               if ((var11 = var10.a).a.a == xi.MINIMIZE) {
                  return Double.compare(var6, var8);
               }

               var10000 = Double.compare(var8, var6);
            }

            return var10000;
         }
      });
      return (wQ[])var1.b.toArray(new wQ[0]);
   }

   // $VF: synthetic method
   static xp a(xm var0) {
      return var0.a;
   }
}
