package org.valkyrienskies.core.impl.shadow;

public class nZ implements kM<nX> {
   private static int h;
   private static int i;
   private static int j;
   private static int k;
   private static int l;
   private static String m;
   private static String n;
   private static String o;
   private static String p;
   private static String q;
   private static String r;
   private static String s;
   private static String t;
   private static String u;
   final int a;
   final nX b;
   final nX c;
   final nX d;
   private final nX v;
   private final nX[] w;
   private final nX x;
   private final nX y;
   private final nX z;
   final nX e;
   private final nX[] A;
   private final nX B;
   private final nX[] C;
   final nX f;
   private final nX[] D;
   private final nX E;
   private final nX[] F;
   private final nX G;
   int g;
   private int H;

   private nZ(int var1) {
      this(var1, true);
   }

   private nZ(int var1, boolean var2) {
      this.a = var1 < 13 ? 4 : (var1 + 3) / 4;
      this.g = nZ.a.ROUND_HALF_EVEN$7d08181e;
      this.H = 0;
      this.b = new nX(this, 0);
      this.c = new nX(this, 1);
      this.d = new nX(this, 2);
      if (var2) {
         synchronized (nZ.class) {
            var1 = var1 < 67 ? 200 : 3 * var1;
            if (m == null || m.length() < var1 - 3) {
               nZ var15 = new nZ(var1, false);
               nX var3 = new nX(var15, 1);
               nX var4 = new nX(var15, 2);
               nX var5 = new nX(var15, 3);
               nX var6;
               m = (var6 = var4.Q()).toString();
               n = var3.i(var6).toString();
               o = (var6 = var5.Q()).toString();
               p = var3.i(var6).toString();
               nX var7 = var5;
               var6 = var4;
               var5 = var3;
               nX var8;
               nX var9 = (var8 = var6.Q()).g(var5);
               nX var10 = var6.f(var6);
               nX var11 = var6;
               nX var12 = var6.h(var7.g(var6.h(var8)));

               for (int var21 = 1; var21 < 20; var21++) {
                  var7 = var9;
                  var8 = var9.h(var9);
                  var8 = var5.g(var8.h(var8)).Q().Q();
                  var9 = var5.g(var8).i(var5.f(var8));
                  var11 = var11.h(var10);
                  var8 = (var8 = var5.f(var9)).h(var8);
                  var12 = var12.h(var8.h(var8)).g(var11.h(var9).h(var5.f(var9).f(var9.h(var9))));
                  if (var9.equals(var7)) {
                     break;
                  }
               }

               q = var5.i(var12).toString();
               var6 = var3;
               var5 = var3;
               var7 = new nX(var6);
               var8 = new nX(var6);
               var9 = new nX(var6);
               var10 = new nX(var6);
               var11 = new nX(var6);

               for (int var33 = 0; var33 < 10000; var33++) {
                  var11 = var11.h(var5);
                  var7 = var7.f(var11.i(var9));
                  var10 = var10.f(var6);
                  var9 = var9.h(var10);
                  if (var7.equals(var8)) {
                     break;
                  }

                  var8 = new nX(var7);
               }

               r = var7.toString();
               s = b(var4, var3, var4).toString();
               t = b(new nX(var15, 5), var3, var4).toString();
               u = b(new nX(var15, 10), var3, var4).toString();
            }

            this.v = new nX(this, m);
            this.w = this.b(m);
            this.x = new nX(this, n);
            this.y = new nX(this, o);
            this.z = new nX(this, p);
            this.e = new nX(this, q);
            this.A = this.b(q);
            this.B = new nX(this, r);
            this.C = this.b(r);
            this.f = new nX(this, s);
            this.D = this.b(s);
            this.E = new nX(this, t);
            this.F = this.b(t);
            this.G = new nX(this, u);
         }
      } else {
         this.v = null;
         this.w = null;
         this.x = null;
         this.y = null;
         this.z = null;
         this.e = null;
         this.A = null;
         this.B = null;
         this.C = null;
         this.f = null;
         this.D = null;
         this.E = null;
         this.F = null;
         this.G = null;
      }
   }

   public final int d() {
      return this.a;
   }

   private void b(int var1) {
      this.g = var1;
   }

   public final int e() {
      return this.g;
   }

   private int p() {
      return this.H;
   }

   private void q() {
      this.H = 0;
   }

   private void c(int var1) {
      this.H = var1 & 31;
   }

   public final void a(int var1) {
      this.H |= var1 & 31;
   }

   private nX r() {
      return new nX(this);
   }

   private nX a(byte var1) {
      return new nX(this, var1);
   }

   private nX d(int var1) {
      return new nX(this, var1);
   }

   private nX a(long var1) {
      return new nX(this, var1);
   }

   private nX a(double var1) {
      return new nX(this, var1);
   }

   private static nX a(nX var0) {
      return new nX(var0);
   }

   public final nX a(String var1) {
      return new nX(this, var1);
   }

   public final nX a(byte var1, byte var2) {
      return new nX(this, var1, var2);
   }

   public final nX f() {
      return this.b;
   }

   public final nX g() {
      return this.c;
   }

   @Override
   public final Class<? extends kN<nX>> c() {
      return nX.class;
   }

   public final nX h() {
      return this.d;
   }

   private nX s() {
      return this.v;
   }

   public final nX[] i() {
      return (nX[])this.w.clone();
   }

   private nX t() {
      return this.x;
   }

   private nX u() {
      return this.y;
   }

   private nX v() {
      return this.z;
   }

   public final nX j() {
      return this.e;
   }

   public final nX[] k() {
      return (nX[])this.A.clone();
   }

   private nX w() {
      return this.B;
   }

   public final nX[] l() {
      return (nX[])this.C.clone();
   }

   public final nX m() {
      return this.f;
   }

   public final nX[] n() {
      return (nX[])this.D.clone();
   }

   private nX x() {
      return this.E;
   }

   public final nX[] o() {
      return (nX[])this.F.clone();
   }

   private nX y() {
      return this.G;
   }

   private nX[] b(String var1) {
      nX[] var2 = new nX[2];
      boolean var3 = true;
      int var4 = 0;
      int var5 = 0;
      char[] var6 = new char[var1.length()];

      for (int var7 = 0; var7 < var6.length; var7++) {
         var6[var7] = var1.charAt(var7);
         if (var6[var7] >= '1' && var6[var7] <= '9') {
            var3 = false;
         }

         if (var6[var7] == '.') {
            var5 += (400 - var5) % 4;
            var3 = false;
         }

         if (var5 == this.a / 2 << 2) {
            var4 = var7;
            break;
         }

         if (var6[var7] >= '0' && var6[var7] <= '9' && !var3) {
            var5++;
         }
      }

      var2[0] = new nX(this, new String(var6, 0, var4));

      for (int var8 = 0; var8 < var6.length; var8++) {
         var6[var8] = var1.charAt(var8);
         if (var6[var8] >= '0' && var6[var8] <= '9' && var8 < var4) {
            var6[var8] = '0';
         }
      }

      var2[1] = new nX(this, new String(var6));
      return var2;
   }

   private static void e(int var0) {
      if (m == null || m.length() < var0 - 3) {
         nZ var5 = new nZ(var0, false);
         nX var1 = new nX(var5, 1);
         nX var2 = new nX(var5, 2);
         nX var3 = new nX(var5, 3);
         nX var4;
         m = (var4 = var2.Q()).toString();
         n = var1.i(var4).toString();
         o = (var4 = var3.Q()).toString();
         p = var1.i(var4).toString();
         q = a(var1, var2, var3).toString();
         r = a(var1, var1).toString();
         s = b(var2, var1, var2).toString();
         t = b(new nX(var5, 5), var1, var2).toString();
         u = b(new nX(var5, 10), var1, var2).toString();
      }
   }

   private static nX a(nX var0, nX var1, nX var2) {
      nX var3;
      nX var4 = (var3 = var1.Q()).g(var0);
      nX var5 = var1.f(var1);
      nX var6 = var1;
      var1 = var1.h(var2.g(var1.h(var3)));

      for (int var9 = 1; var9 < 20; var9++) {
         var3 = var4;
         nX var11 = var4.h(var4);
         nX var12 = var0.g(var11.h(var11)).Q().Q();
         var4 = var0.g(var12).i(var0.f(var12));
         var6 = var6.h(var5);
         nX var7;
         var7 = (var7 = var0.f(var4)).h(var7);
         var1 = var1.h(var7.h(var7)).g(var6.h(var4).h(var0.f(var4).f(var4.h(var4))));
         if (var4.equals(var3)) {
            break;
         }
      }

      return var0.i(var1);
   }

   private static nX a(nX var0, nX var1) {
      nX var2 = new nX(var1);
      nX var3 = new nX(var1);
      nX var4 = new nX(var1);
      nX var5 = new nX(var1);
      nX var6 = new nX(var1);

      for (int var7 = 0; var7 < 10000; var7++) {
         var6 = var6.h(var0);
         var2 = var2.f(var6.i(var4));
         var5 = var5.f(var1);
         var4 = var4.h(var5);
         if (var2.equals(var3)) {
            break;
         }

         var3 = new nX(var2);
      }

      return var2;
   }

   private static nX b(nX var0, nX var1, nX var2) {
      byte var3 = 1;
      var0 = var0.f(new nX(var0.e, -1)).i(var0.f(var1));
      var1 = new nX(var0);
      nX var4 = new nX(var0);
      nX var5 = new nX(var1);

      for (int var6 = 0; var6 < 10000; var6++) {
         var4 = var4.h(var0).h(var0);
         var3 += 2;
         nX var7 = var4.j(var3);
         if ((var1 = var1.f(var7)).equals(var5)) {
            break;
         }

         var5 = new nX(var1);
      }

      return var1.h(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN b() {
      return this.c;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN a() {
      return this.b;
   }

   public enum a {
      public static final int ROUND_DOWN$7d08181e = 1;
      public static final int ROUND_UP$7d08181e = 2;
      public static final int ROUND_HALF_UP$7d08181e = 3;
      public static final int ROUND_HALF_DOWN$7d08181e = 4;
      public static final int ROUND_HALF_EVEN$7d08181e = 5;
      public static final int ROUND_HALF_ODD$7d08181e = 6;
      public static final int ROUND_CEIL$7d08181e = 7;
      public static final int ROUND_FLOOR$7d08181e = 8;
      // $VF: synthetic field
      private static final int[] $VALUES$492aff43 = new int[]{
         ROUND_DOWN$7d08181e,
         ROUND_UP$7d08181e,
         ROUND_HALF_UP$7d08181e,
         ROUND_HALF_DOWN$7d08181e,
         ROUND_HALF_EVEN$7d08181e,
         ROUND_HALF_ODD$7d08181e,
         ROUND_CEIL$7d08181e,
         ROUND_FLOOR$7d08181e
      };

      public static int[] values$5a699a5c() {
         return (int[])$VALUES$492aff43.clone();
      }

      private a(String var1, int var2) {
         super(var1, var2);
      }
   }
}
