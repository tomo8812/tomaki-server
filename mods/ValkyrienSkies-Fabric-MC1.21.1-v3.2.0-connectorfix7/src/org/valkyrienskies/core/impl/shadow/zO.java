package org.valkyrienskies.core.impl.shadow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public final class zO {
   private static int a;
   private static int b;
   private static int c;
   private static int d;
   private static int e;
   private static int f;
   private int g = 5;
   private URL h = null;
   private double i = 0.0;
   private double j = 0.0;
   private zu k = null;
   private BufferedReader l = null;
   private final zD m;

   public zO() {
      this.m = new zD();
   }

   @Deprecated
   private zO(zE var1) {
      this.m = var1.a;
   }

   private zO(zF var1) {
      this.m = new zD(var1);
   }

   private double a() {
      switch (this.g) {
         case 0:
            zO var13 = this;
            if (this.k != null) {
               zu var15 = var13.k;
               if (var13.k.b.size() != 0) {
                  var15 = var13.k;
                  if (!var13.k.d) {
                     throw new oX(px.DISTRIBUTION_NOT_LOADED);
                  }

                  return var15.b();
               }
            }

            throw new oX(px.DIGEST_NOT_INITIALIZED);
         case 1:
            zO var12 = this;
            if (this.l == null) {
               var12.f();
            }

            String var2;
            if ((var2 = var12.l.readLine()) == null) {
               zO var14 = var12;
               if (var12.l != null) {
                  var14.l.close();
                  var14.l = null;
               }

               var12.f();
               if ((var2 = var12.l.readLine()) == null) {
                  throw new oX(px.URL_CONTAINS_NO_DATA, var12.h);
               }
            }

            return Double.parseDouble(var2);
         case 2:
            zO var10 = this;
            double var7 = 2.0 * var10.i;
            double var5 = 0.0;
            zD var11 = this.m;
            return this.m.a(var5, var7, false);
         case 3:
            zO var9 = this;
            return this.m.b(var9.i);
         case 4:
            zO var1 = this;
            return this.m.a(var1.i, var1.j);
         case 5:
            return this.i;
         default:
            throw new oX(
               px.UNKNOWN_MODE, this.g, "DIGEST_MODE", 0, "REPLAY_MODE", 1, "UNIFORM_MODE", 2, "EXPONENTIAL_MODE", 3, "GAUSSIAN_MODE", 4, "CONSTANT_MODE", 5
            );
      }
   }

   private void a(double[] var1) {
      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = this.a();
      }
   }

   private double[] a(int var1) {
      double[] var2 = new double[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = this.a();
      }

      return var2;
   }

   private void b() {
      short var1 = 1000;
      zO var3 = this;
      this.k = new zu(1000, var3.m.c());
      var3.k.a(var3.h);
      zu var2 = var3.k;
      var3.i = var3.k.c.b();
      var2 = var3.k;
      var3.j = var3.k.c.e();
   }

   private void b(int var1) {
      this.k = new zu(1000, this.m.c());
      this.k.a(this.h);
      zu var2 = this.k;
      this.i = this.k.c.b();
      zu var3 = this.k;
      this.j = this.k.c.e();
   }

   private int c() {
      return this.g;
   }

   private void c(int var1) {
      this.g = var1;
   }

   private URL d() {
      return this.h;
   }

   private void a(String var1) {
      this.h = new URL(var1);
   }

   private void a(URL var1) {
      this.h = var1;
   }

   private zu e() {
      return this.k;
   }

   private void f() {
      if (this.l != null) {
         try {
            this.l.close();
            this.l = null;
         } catch (IOException var1) {
         }
      }

      this.l = new BufferedReader(new InputStreamReader(this.h.openStream(), "UTF-8"));
   }

   private void g() {
      if (this.l != null) {
         this.l.close();
         this.l = null;
      }
   }

   private double h() {
      return this.i;
   }

   private void a(double var1) {
      this.i = var1;
   }

   private double i() {
      return this.j;
   }

   private void b(double var1) {
      this.j = var1;
   }

   private void a(long var1) {
      this.m.a(var1);
   }

   private double j() {
      if (this.k != null) {
         zu var1 = this.k;
         if (this.k.b.size() != 0) {
            return this.k.j();
         }
      }

      throw new oX(px.DIGEST_NOT_INITIALIZED);
   }

   private double k() {
      if (this.l == null) {
         this.f();
      }

      String var1;
      if ((var1 = this.l.readLine()) == null) {
         this.g();
         this.f();
         if ((var1 = this.l.readLine()) == null) {
            throw new oX(px.URL_CONTAINS_NO_DATA, this.h);
         }
      }

      return Double.parseDouble(var1);
   }

   private double l() {
      double var4 = 2.0 * this.i;
      double var2 = 0.0;
      zD var1 = this.m;
      return this.m.a(var2, var4, false);
   }

   private double m() {
      return this.m.b(this.i);
   }

   private double n() {
      return this.m.a(this.i, this.j);
   }
}
