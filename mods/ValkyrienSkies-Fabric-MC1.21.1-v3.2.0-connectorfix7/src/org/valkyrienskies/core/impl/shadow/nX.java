package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public class nX implements kO<nX> {
   private static int f;
   private static int g;
   private static int h;
   private static int i;
   private static byte j;
   private static byte k;
   private static byte l;
   private static byte m;
   private static final String n = "NaN";
   private static final String o = "Infinity";
   private static final String p = "-Infinity";
   private static final String q = "add";
   private static final String r = "multiply";
   private static final String s = "divide";
   private static final String t = "sqrt";
   private static final String u = "align";
   private static final String v = "trunc";
   private static final String w = "nextAfter";
   private static final String x = "lessThan";
   private static final String y = "greaterThan";
   private static final String z = "newInstance";
   protected int[] a;
   protected byte b;
   protected int c;
   protected byte d;
   final nZ e;

   protected nX(nZ var1) {
      this.a = new int[var1.a];
      this.b = 1;
      this.c = 0;
      this.d = 0;
      this.e = var1;
   }

   protected nX(nZ var1, byte var2) {
      this(var1, (long)var2);
   }

   protected nX(nZ var1, int var2) {
      this(var1, (long)var2);
   }

   protected nX(nZ var1, long var2) {
      this.a = new int[var1.a];
      this.d = 0;
      this.e = var1;
      boolean var5 = false;
      if (var2 == Long.MIN_VALUE) {
         var5 = true;
         var2++;
      }

      if (var2 < 0L) {
         this.b = -1;
         var2 = -var2;
      } else {
         this.b = 1;
      }

      for (this.c = 0; var2 != 0L; this.c++) {
         System.arraycopy(this.a, this.a.length - this.c, this.a, this.a.length - 1 - this.c, this.c);
         this.a[this.a.length - 1] = (int)(var2 % 10000L);
         var2 /= 10000L;
      }

      if (var5) {
         for (int var6 = 0; var6 < this.a.length - 1; var6++) {
            if (this.a[var6] != 0) {
               this.a[var6]++;
               return;
            }
         }
      }
   }

   protected nX(nZ var1, double var2) {
      this.a = new int[var1.a];
      this.b = 1;
      this.c = 0;
      this.d = 0;
      this.e = var1;
      long var4;
      long var6 = (var4 = Double.doubleToLongBits(var2)) & 4503599627370495L;
      int var8;
      if ((var8 = (int)((var4 & 9218868437227405312L) >> 52) - 1023) == -1023) {
         if (var2 == 0.0) {
            if ((var4 & Long.MIN_VALUE) != 0L) {
               this.b = -1;
            }

            return;
         }

         var8++;

         while ((var6 & 4503599627370496L) == 0L) {
            var8--;
            var6 <<= 1;
         }

         var6 &= 4503599627370495L;
      }

      if (var8 == 1024) {
         if (var2 != var2) {
            this.b = 1;
            this.d = 3;
         } else if (var2 < 0.0) {
            this.b = -1;
            this.d = 1;
         } else {
            this.b = 1;
            this.d = 1;
         }
      } else {
         nX var9 = new nX(var1, var6).i(new nX(var1, 4503599627370496L)).f(var1.c).h(oa.a(var1.d, var8));
         if ((var4 & Long.MIN_VALUE) != 0L) {
            var9 = var9.P();
         }

         System.arraycopy(var9.a, 0, this.a, 0, this.a.length);
         this.b = var9.b;
         this.c = var9.c;
         this.d = var9.d;
      }
   }

   public nX(nX var1) {
      this.a = (int[])var1.a.clone();
      this.b = var1.b;
      this.c = var1.c;
      this.d = var1.d;
      this.e = var1.e;
   }

   protected nX(nZ var1, String var2) {
      this.a = new int[var1.a];
      this.b = 1;
      this.c = 0;
      this.d = 0;
      this.e = var1;
      boolean var9 = false;
      nZ var3 = this.e;
      char[] var16 = new char[(this.e.a << 2) + 8];
      if (var2.equals("Infinity")) {
         this.b = 1;
         this.d = 1;
      } else if (var2.equals("-Infinity")) {
         this.b = -1;
         this.d = 1;
      } else if (var2.equals("NaN")) {
         this.b = 1;
         this.d = 3;
      } else {
         int var4;
         if ((var4 = var2.indexOf("e")) == -1) {
            var4 = var2.indexOf("E");
         }

         int var6 = 0;
         String var5;
         if (var4 != -1) {
            var5 = var2.substring(0, var4);
            var2 = var2.substring(var4 + 1);
            boolean var7 = false;

            for (int var8 = 0; var8 < var2.length(); var8++) {
               if (var2.charAt(var8) == '-') {
                  var7 = true;
               } else if (var2.charAt(var8) >= '0' && var2.charAt(var8) <= '9') {
                  var6 = var6 * 10 + var2.charAt(var8) - 48;
               }
            }

            if (var7) {
               var6 = -var6;
            }
         } else {
            var5 = var2;
         }

         if (var5.indexOf("-") != -1) {
            this.b = -1;
         }

         var4 = 0;
         int var14 = 0;

         while (var5.charAt(var4) < '1' || var5.charAt(var4) > '9') {
            if (var9 && var5.charAt(var4) == '0') {
               var14--;
            }

            if (var5.charAt(var4) == '.') {
               var9 = true;
            }

            if (++var4 == var5.length()) {
               break;
            }
         }

         int var19 = 4;
         var16[0] = '0';
         var16[1] = '0';
         var16[2] = '0';
         var16[3] = '0';
         int var21 = 0;

         while (var4 != var5.length() && var19 != (this.a.length << 2) + 4 + 1) {
            if (var5.charAt(var4) == '.') {
               var9 = true;
               var14 = var21;
               var4++;
            } else if (var5.charAt(var4) >= '0' && var5.charAt(var4) <= '9') {
               var16[var19] = var5.charAt(var4);
               var19++;
               var4++;
               var21++;
            } else {
               var4++;
            }
         }

         if (var9 && var19 != 4) {
            while (true) {
               var19--;
               if (var19 == 4 || var16[var19] != '0') {
                  break;
               }

               var21--;
            }
         }

         if (var9 && var21 == 0) {
            var14 = 0;
         }

         if (!var9) {
            var14 = var19 - 4;
         }

         var4 = var21 - 1 + 4;

         while (var4 > 4 && var16[var4] == '0') {
            var4--;
         }

         int var10 = (400 - var14 - var6 % 4) % 4;
         var19 = 4 - var10;
         int var15 = var14 + var10;

         while (var4 - var19 < this.a.length << 2) {
            for (int var11 = 0; var11 < 4; var11++) {
               var16[++var4] = '0';
            }
         }

         for (int var12 = this.a.length - 1; var12 >= 0; var12--) {
            this.a[var12] = (var16[var19] - '0') * 1000 + (var16[var19 + 1] - '0') * 100 + (var16[var19 + 2] - '0') * 10 + (var16[var19 + 3] - '0');
            var19 += 4;
         }

         this.c = (var15 + var6) / 4;
         if (var19 < var16.length) {
            this.h((var16[var19] - '0') * 1000);
         }
      }
   }

   protected nX(nZ var1, byte var2, byte var3) {
      this.e = var1;
      this.a = new int[var1.a];
      this.b = var2;
      this.c = 0;
      this.d = var3;
   }

   public nX C() {
      return new nX(this.e);
   }

   public nX a(byte var1) {
      return new nX(this.e, var1);
   }

   public nX e(int var1) {
      return new nX(this.e, var1);
   }

   public nX a(long var1) {
      return new nX(this.e, 4503599627370496L);
   }

   public nX h(double var1) {
      return new nX(this.e, var1);
   }

   public nX a(nX var1) {
      nZ var2 = this.e;
      var2 = var1.e;
      if (this.e.a != var1.e.a) {
         this.e.a(1);
         var2 = this.e;
         nX var5;
         (var5 = this.a(this.e.b)).d = 3;
         return this.a(1, "newInstance", var1, var5);
      } else {
         return new nX(var1);
      }
   }

   public nX a(String var1) {
      return new nX(this.e, var1);
   }

   public nX a(byte var1, byte var2) {
      return this.e.a(var1, var2);
   }

   public final nZ D() {
      return this.e;
   }

   public final int E() {
      nZ var1 = this.e;
      return this.e.a;
   }

   public final nX F() {
      nZ var1 = this.e;
      return this.e.b;
   }

   public final nX G() {
      nZ var1 = this.e;
      return this.e.c;
   }

   public final nX H() {
      nZ var1 = this.e;
      return this.e.d;
   }

   private void S() {
      for (int var1 = this.a.length - 1; var1 > 0; var1--) {
         this.a[var1] = this.a[var1 - 1];
      }

      this.a[0] = 0;
      this.c--;
   }

   protected final void I() {
      for (int var1 = 0; var1 < this.a.length - 1; var1++) {
         this.a[var1] = this.a[var1 + 1];
      }

      this.a[this.a.length - 1] = 0;
      this.c++;
   }

   private int k(int var1) {
      int var2 = 0;
      boolean var3 = false;
      int var4;
      int var5;
      if ((var5 = var4 = this.c - var1) < 0) {
         var5 = -var5;
      }

      if (var4 == 0) {
         return 0;
      }

      if (var5 > this.a.length + 1) {
         Arrays.fill(this.a, 0);
         this.c = var1;
         this.e.a(16);
         this.a(16, "align", this, this);
         return 0;
      }

      for (int var6 = 0; var6 < var5; var6++) {
         if (var4 < 0) {
            if (var2 != 0) {
               var3 = true;
            }

            var2 = this.a[0];
            this.I();
         } else {
            this.S();
         }
      }

      if (var3) {
         this.e.a(16);
         this.a(16, "align", this, this);
      }

      return var2;
   }

   public final boolean b(nX var1) {
      nZ var2 = this.e;
      var2 = var1.e;
      if (this.e.a != var1.e.a) {
         this.e.a(1);
         nX var4;
         (var4 = this.a(this.F())).d = 3;
         this.a(1, "lessThan", var1, var4);
         return false;
      } else if (this.J() || var1.J()) {
         this.e.a(1);
         this.a(1, "lessThan", var1, this.a(this.F()));
         return false;
      } else {
         return b(this, var1) < 0;
      }
   }

   public final boolean c(nX var1) {
      nZ var2 = this.e;
      var2 = var1.e;
      if (this.e.a != var1.e.a) {
         this.e.a(1);
         nX var4;
         (var4 = this.a(this.F())).d = 3;
         this.a(1, "greaterThan", var1, var4);
         return false;
      } else if (this.J() || var1.J()) {
         this.e.a(1);
         this.a(1, "greaterThan", var1, this.a(this.F()));
         return false;
      } else {
         return b(this, var1) > 0;
      }
   }

   private boolean T() {
      if (this.J()) {
         this.e.a(1);
         this.a(1, "lessThan", this, this.a(this.F()));
         return false;
      } else {
         return this.b < 0 || this.a[this.a.length - 1] == 0 && !this.Y();
      }
   }

   private boolean U() {
      if (this.J()) {
         this.e.a(1);
         this.a(1, "lessThan", this, this.a(this.F()));
         return false;
      } else {
         return this.b < 0 && (this.a[this.a.length - 1] != 0 || this.Y());
      }
   }

   private boolean V() {
      if (this.J()) {
         this.e.a(1);
         this.a(1, "lessThan", this, this.a(this.F()));
         return false;
      } else {
         return this.b > 0 || this.a[this.a.length - 1] == 0 && !this.Y();
      }
   }

   private boolean W() {
      if (this.J()) {
         this.e.a(1);
         this.a(1, "lessThan", this, this.a(this.F()));
         return false;
      } else {
         return this.b > 0 && (this.a[this.a.length - 1] != 0 || this.Y());
      }
   }

   private nX X() {
      nX var1;
      (var1 = this.a(this)).b = 1;
      return var1;
   }

   private boolean Y() {
      return this.d == 1;
   }

   public final boolean J() {
      return this.d == 3 || this.d == 2;
   }

   private boolean Z() {
      if (this.J()) {
         this.e.a(1);
         this.a(1, "lessThan", this, this.a(this.F()));
         return false;
      } else {
         return this.a[this.a.length - 1] == 0 && !this.Y();
      }
   }

   @Override
   public boolean equals(Object other) {
      if (other instanceof nX) {
         other = (nX)other;
         if (!this.J() && !((nX)other).J()) {
            nZ var2 = this.e;
            var2 = ((nX)other).e;
            if (this.e.a == ((nX)other).e.a) {
               if (b(this, (nX)other) == 0) {
                  return true;
               }

               return false;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 17 + (this.Z() ? 0 : this.b << 8) + (this.d << 16) + this.c + Arrays.hashCode(this.a);
   }

   public final boolean d(nX var1) {
      if (!this.J() && !var1.J()) {
         nZ var2 = this.e;
         var2 = var1.e;
         if (this.e.a == var1.e.a) {
            if (!this.c(var1) && !this.b(var1)) {
               return false;
            }

            return true;
         }
      }

      return false;
   }

   private static int b(nX var0, nX var1) {
      if (var0.a[var0.a.length - 1] == 0 && var1.a[var1.a.length - 1] == 0 && var0.d == 0 && var1.d == 0) {
         return 0;
      }

      if (var0.b != var1.b) {
         return var0.b == -1 ? -1 : 1;
      }

      if (var0.d == 1 && var1.d == 0) {
         return var0.b;
      }

      if (var0.d == 0 && var1.d == 1) {
         return -var1.b;
      }

      if (var0.d == 1 && var1.d == 1) {
         return 0;
      }

      if (var1.a[var1.a.length - 1] != 0 && var0.a[var1.a.length - 1] != 0) {
         if (var0.c < var1.c) {
            return -var0.b;
         }

         if (var0.c > var1.c) {
            return var0.b;
         }
      }

      for (int var2 = var0.a.length - 1; var2 >= 0; var2--) {
         if (var0.a[var2] > var1.a[var2]) {
            return var0.b;
         }

         if (var0.a[var2] < var1.a[var2]) {
            return -var0.b;
         }
      }

      return 0;
   }

   public final nX K() {
      return this.l(nZ.a.ROUND_HALF_EVEN$7d08181e);
   }

   public final nX L() {
      return this.l(nZ.a.ROUND_FLOOR$7d08181e);
   }

   private nX aa() {
      return this.l(nZ.a.ROUND_CEIL$7d08181e);
   }

   public final nX e(nX var1) {
      if ((var1 = this.g(this.i(var1).l(nZ.a.ROUND_HALF_EVEN$7d08181e).h(var1))).a[this.a.length - 1] == 0) {
         var1.b = this.b;
      }

      return var1;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private nX l(int var1) {
      boolean var2 = false;
      if (this.J()) {
         return this.a(this);
      }

      if (this.d == 1) {
         return this.a(this);
      }

      if (this.a[this.a.length - 1] == 0) {
         return this.a(this);
      }

      if (this.c < 0) {
         this.e.a(16);
         nX var7 = this.a(this.F());
         return this.a(16, "trunc", this, var7);
      }

      if (this.c >= this.a.length) {
         return this.a(this);
      }

      nX var3 = this.a(this);

      for (int var4 = 0; var4 < this.a.length - var3.c; var4++) {
         var2 |= var3.a[var4] != 0;
         var3.a[var4] = 0;
      }

      if (var2) {
         switch (<unrepresentable>.a[var1 - 1]) {
            case 1:
               if (var3.b == -1) {
                  var3 = var3.f(this.e(-1));
               }
               break;
            case 2:
               if (var3.b == 1) {
                  var3 = var3.f(this.G());
               }
               break;
            default:
               nX var8 = this.a("0.5");
               nX var5;
               (var5 = this.g(var3)).b = 1;
               if (var5.c(var8)) {
                  (var5 = this.a(this.G())).b = this.b;
                  var3 = var3.f(var5);
               }

               if (var5.equals(var8) && var3.c > 0 && (var3.a[this.a.length - var3.c] & 1) != 0) {
                  nX var6;
                  (var6 = this.a(this.G())).b = this.b;
                  var3 = var3.f(var6);
               }
         }

         this.e.a(16);
         return this.a(16, "trunc", this, var3);
      } else {
         return var3;
      }
   }

   public final int M() {
      int var2 = 0;
      nX var1;
      if ((var1 = this.l(nZ.a.ROUND_HALF_EVEN$7d08181e)).c(this.e(Integer.MAX_VALUE))) {
         return Integer.MAX_VALUE;
      }

      if (var1.b(this.e(Integer.MIN_VALUE))) {
         return Integer.MIN_VALUE;
      }

      for (int var3 = this.a.length - 1; var3 >= this.a.length - var1.c; var3--) {
         var2 = var2 * 10000 + var1.a[var3];
      }

      if (var1.b == -1) {
         var2 = -var2;
      }

      return var2;
   }

   public final int N() {
      return this.c - 1;
   }

   public final nX f(int var1) {
      nX var2;
      (var2 = this.a(this.G())).c = var1 + 1;
      return var2;
   }

   public final int O() {
      if (this.a[this.a.length - 1] > 1000) {
         return (this.c << 2) - 1;
      } else if (this.a[this.a.length - 1] > 100) {
         return (this.c << 2) - 2;
      } else {
         return this.a[this.a.length - 1] > 10 ? (this.c << 2) - 3 : (this.c << 2) - 4;
      }
   }

   public final nX g(int var1) {
      nX var2 = this.a(this.G());
      if (var1 >= 0) {
         var2.c = var1 / 4 + 1;
      } else {
         var2.c = (var1 + 1) / 4;
      }

      switch ((var1 % 4 + 4) % 4) {
         case 0:
            break;
         case 1:
            var2 = var2.i(10);
            break;
         case 2:
            var2 = var2.i(100);
            break;
         default:
            var2 = var2.i(1000);
      }

      return var2;
   }

   private int m(int var1) {
      var1 = 10000 - var1;

      for (int var2 = 0; var2 < this.a.length; var2++) {
         this.a[var2] = 10000 - this.a[var2] - 1;
      }

      int var7 = var1 / 10000;
      var1 -= var7 * 10000;

      for (int var3 = 0; var3 < this.a.length; var3++) {
         int var4;
         var7 = (var4 = this.a[var3] + var7) / 10000;
         this.a[var3] = var4 - var7 * 10000;
      }

      return var1;
   }

   public final nX f(nX var1) {
      nZ var2 = this.e;
      var2 = var1.e;
      if (this.e.a != var1.e.a) {
         this.e.a(1);
         nX var16;
         (var16 = this.a(this.F())).d = 3;
         return this.a(1, "add", var1, var16);
      }

      if (this.d != 0 || var1.d != 0) {
         if (this.J()) {
            return this;
         }

         if (var1.J()) {
            return var1;
         }

         if (this.d == 1 && var1.d == 0) {
            return this;
         }

         if (var1.d == 1 && this.d == 0) {
            return var1;
         }

         if (var1.d == 1 && this.d == 1 && this.b == var1.b) {
            return var1;
         }

         if (var1.d == 1 && this.d == 1 && this.b != var1.b) {
            this.e.a(1);
            nX var15;
            (var15 = this.a(this.F())).d = 3;
            return this.a(1, "add", var1, var15);
         }
      }

      nX var14 = this.a(this);
      nX var3 = this.a(var1);
      nX var4 = this.a(this.F());
      byte var5 = var14.b;
      byte var6 = var3.b;
      var14.b = 1;
      var3.b = 1;
      byte var7 = var6;
      if (b(var14, var3) > 0) {
         var7 = var5;
      }

      if (var3.a[this.a.length - 1] == 0) {
         var3.c = var14.c;
      }

      if (var14.a[this.a.length - 1] == 0) {
         var14.c = var3.c;
      }

      int var8 = 0;
      int var9 = 0;
      if (var14.c < var3.c) {
         var8 = var14.k(var3.c);
      } else {
         var9 = var3.k(var14.c);
      }

      if (var5 != var6) {
         if (var5 == var7) {
            var9 = var3.m(var9);
         } else {
            var8 = var14.m(var8);
         }
      }

      int var10 = 0;

      for (int var11 = 0; var11 < this.a.length; var11++) {
         int var12;
         var10 = (var12 = var14.a[var11] + var3.a[var11] + var10) / 10000;
         var4.a[var11] = var12 - var10 * 10000;
      }

      var4.c = var14.c;
      var4.b = var7;
      if (var10 != 0 && var5 == var6) {
         int var17 = var4.a[0];
         var4.I();
         var4.a[this.a.length - 1] = var10;
         int var20;
         if ((var20 = var4.h(var17)) != 0) {
            var4 = this.a(var20, "add", var1, var4);
         }
      }

      for (int var18 = 0; var18 < this.a.length && var4.a[this.a.length - 1] == 0; var18++) {
         var4.S();
         if (var18 == 0) {
            var4.a[0] = var8 + var9;
            var8 = 0;
            var9 = 0;
         }
      }

      if (var4.a[this.a.length - 1] == 0) {
         var4.c = 0;
         if (var5 != var6) {
            var4.b = 1;
         }
      }

      int var19;
      if ((var19 = var4.h(var8 + var9)) != 0) {
         var4 = this.a(var19, "add", var1, var4);
      }

      return var4;
   }

   public final nX P() {
      nX var1;
      (var1 = this.a(this)).b = (byte)(-var1.b);
      return var1;
   }

   public final nX g(nX var1) {
      return this.f(var1.P());
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected int h(int var1) {
      boolean var5;
      label103: {
         nZ var2 = this.e;
         boolean var10000;
         switch (<unrepresentable>.a[this.e.g - 1]) {
            case 2:
               var5 = this.b == 1 && var1 != 0;
               break label103;
            case 3:
               var5 = var1 > 5000 || var1 == 5000 && (this.a[0] & 1) == 1;
               break label103;
            case 4:
               var10000 = false;
               break;
            case 5:
               var5 = var1 != 0;
               break label103;
            case 6:
               var5 = var1 >= 5000;
               break label103;
            case 7:
               var5 = var1 > 5000;
               break label103;
            case 8:
               var5 = var1 > 5000 || var1 == 5000 && (this.a[0] & 1) == 0;
               break label103;
            default:
               var10000 = this.b == -1 && var1 != 0;
         }

         var5 = var10000;
      }

      if (var5) {
         var5 = 1;

         for (int var3 = 0; var3 < this.a.length; var3++) {
            int var4;
            var5 = (var4 = this.a[var3] + var5) / 10000;
            this.a[var3] = var4 - var5 * 10000;
         }

         if (var5 != 0) {
            this.I();
            this.a[this.a.length - 1] = var5;
         }
      }

      if (this.c < -32767) {
         this.e.a(8);
         return 8;
      } else if (this.c > 32768) {
         this.e.a(4);
         return 4;
      } else if (var1 != 0) {
         this.e.a(16);
         return 16;
      } else {
         return 0;
      }
   }

   public final nX h(nX var1) {
      nZ var2 = this.e;
      var2 = var1.e;
      if (this.e.a != var1.e.a) {
         this.e.a(1);
         nX var14;
         (var14 = this.a(this.F())).d = 3;
         return this.a(1, "multiply", var1, var14);
      }

      nX var9 = this.a(this.F());
      if (this.d != 0 || var1.d != 0) {
         if (this.J()) {
            return this;
         }

         if (var1.J()) {
            return var1;
         }

         if (this.d == 1 && var1.d == 0 && var1.a[this.a.length - 1] != 0) {
            nX var13;
            (var13 = this.a(this)).b = (byte)(this.b * var1.b);
            return var13;
         }

         if (var1.d == 1 && this.d == 0 && this.a[this.a.length - 1] != 0) {
            nX var12;
            (var12 = this.a(var1)).b = (byte)(this.b * var1.b);
            return var12;
         }

         if (var1.d == 1 && this.d == 1) {
            nX var11;
            (var11 = this.a(this)).b = (byte)(this.b * var1.b);
            return var11;
         }

         if (var1.d == 1 && this.d == 0 && this.a[this.a.length - 1] == 0 || this.d == 1 && var1.d == 0 && var1.a[this.a.length - 1] == 0) {
            this.e.a(1);
            nX var10;
            (var10 = this.a(this.F())).d = 3;
            return this.a(1, "multiply", var1, var10);
         }
      }

      int[] var3 = new int[this.a.length << 1];

      for (int var4 = 0; var4 < this.a.length; var4++) {
         int var5 = 0;

         for (int var6 = 0; var6 < this.a.length; var6++) {
            int var7;
            var5 = (var7 = this.a[var4] * var1.a[var6] + var3[var4 + var6] + var5) / 10000;
            var3[var4 + var6] = var7 - var5 * 10000;
         }

         var3[var4 + this.a.length] = var5;
      }

      int var15 = (this.a.length << 1) - 1;

      for (int var16 = (this.a.length << 1) - 1; var16 >= 0; var16--) {
         if (var3[var16] != 0) {
            var15 = var16;
            break;
         }
      }

      for (int var17 = 0; var17 < this.a.length; var17++) {
         var9.a[this.a.length - var17 - 1] = var3[var15 - var17];
      }

      var9.c = this.c + var1.c + var15 - 2 * this.a.length + 1;
      var9.b = (byte)(this.b == var1.b ? 1 : -1);
      if (var9.a[this.a.length - 1] == 0) {
         var9.c = 0;
      }

      int var18;
      if (var15 > this.a.length - 1) {
         var18 = var9.h(var3[var15 - this.a.length]);
      } else {
         var18 = var9.h(0);
      }

      if (var18 != 0) {
         var9 = this.a(var18, "multiply", var1, var9);
      }

      return var9;
   }

   public final nX i(int var1) {
      return var1 >= 0 && var1 < 10000 ? this.n(var1) : this.h(this.e(var1));
   }

   private nX n(int var1) {
      nX var2 = this.a(this);
      if (this.d != 0) {
         if (this.J()) {
            return this;
         }

         if (this.d == 1 && var1 != 0) {
            return this.a(this);
         }

         if (this.d == 1 && var1 == 0) {
            this.e.a(1);
            (var2 = this.a(this.F())).d = 3;
            return this.a(1, "multiply", this.a(this.F()), var2);
         }
      }

      if (var1 >= 0 && var1 < 10000) {
         int var3 = 0;

         for (int var4 = 0; var4 < this.a.length; var4++) {
            int var5;
            var3 = (var5 = this.a[var4] * var1 + var3) / 10000;
            var2.a[var4] = var5 - var3 * 10000;
         }

         int var8 = 0;
         if (var3 != 0) {
            var8 = var2.a[0];
            var2.I();
            var2.a[this.a.length - 1] = var3;
         }

         if (var2.a[this.a.length - 1] == 0) {
            var2.c = 0;
         }

         int var9;
         if ((var9 = var2.h(var8)) != 0) {
            var2 = this.a(var9, "multiply", var2, var2);
         }

         return var2;
      } else {
         this.e.a(1);
         (var2 = this.a(this.F())).d = 3;
         return this.a(1, "multiply", var2, var2);
      }
   }

   public final nX i(nX var1) {
      int var7 = 0;
      nZ var8 = this.e;
      var8 = var1.e;
      if (this.e.a != var1.e.a) {
         this.e.a(1);
         nX var31;
         (var31 = this.a(this.F())).d = 3;
         return this.a(1, "divide", var1, var31);
      }

      nX var10 = this.a(this.F());
      if (this.d != 0 || var1.d != 0) {
         if (this.J()) {
            return this;
         }

         if (var1.J()) {
            return var1;
         }

         if (this.d == 1 && var1.d == 0) {
            (var10 = this.a(this)).b = (byte)(this.b * var1.b);
            return var10;
         }

         if (var1.d == 1 && this.d == 0) {
            (var10 = this.a(this.F())).b = (byte)(this.b * var1.b);
            return var10;
         }

         if (var1.d == 1 && this.d == 1) {
            this.e.a(1);
            (var10 = this.a(this.F())).d = 3;
            return this.a(1, "divide", var1, var10);
         }
      }

      if (var1.a[this.a.length - 1] == 0) {
         this.e.a(2);
         (var10 = this.a(this.F())).b = (byte)(this.b * var1.b);
         var10.d = 1;
         return this.a(2, "divide", var1, var10);
      }

      int[] var2 = new int[this.a.length + 1];
      int[] var3 = new int[this.a.length + 2];
      int[] var4 = new int[this.a.length + 1];
      var2[this.a.length] = 0;
      var3[this.a.length] = 0;
      var3[this.a.length + 1] = 0;
      var4[this.a.length] = 0;

      for (int var18 = 0; var18 < this.a.length; var18++) {
         var2[var18] = this.a[var18];
         var3[var18] = 0;
         var4[var18] = 0;
      }

      int var6 = 0;

      for (int var5 = this.a.length + 1; var5 >= 0; var5--) {
         int var19;
         int var11 = (var19 = var2[this.a.length] * 10000 + var2[this.a.length - 1]) / (var1.a[this.a.length - 1] + 1);
         int var12 = (var19 + 1) / var1.a[this.a.length - 1];
         boolean var9 = false;

         while (!var9) {
            var7 = (var11 + var12) / 2;
            int var20 = 0;

            for (int var13 = 0; var13 < this.a.length + 1; var13++) {
               int var14;
               var20 = (var14 = (var13 < this.a.length ? var1.a[var13] : 0) * var7 + var20) / 10000;
               var4[var13] = var14 - var20 * 10000;
            }

            int var21 = 1;

            for (int var32 = 0; var32 < this.a.length + 1; var32++) {
               int var34;
               var21 = (var34 = 9999 - var4[var32] + var2[var32] + var21) / 10000;
               var4[var32] = var34 - var21 * 10000;
            }

            if (var21 == 0) {
               var12 = var7 - 1;
            } else {
               int var22;
               if ((var22 = (var4[this.a.length] * 10000 + var4[this.a.length - 1]) / (var1.a[this.a.length - 1] + 1)) >= 2) {
                  var11 = var7 + var22;
               } else {
                  var9 = false;
                  int var33 = this.a.length - 1;

                  while (true) {
                     if (var33 >= 0) {
                        if (var1.a[var33] > var4[var33]) {
                           var9 = true;
                        }

                        if (var1.a[var33] >= var4[var33]) {
                           var33--;
                           continue;
                        }
                     }

                     if (var4[this.a.length] != 0) {
                        var9 = false;
                     }

                     if (!var9) {
                        var11 = var7 + 1;
                     }
                     break;
                  }
               }
            }
         }

         var3[var5] = var7;
         if (var7 != 0 || var6 != 0) {
            var6++;
         }

         var8 = this.e;
         if (this.e.g == nZ.a.ROUND_DOWN$7d08181e && var6 == this.a.length || var6 > this.a.length) {
            break;
         }

         var2[0] = 0;

         for (int var24 = 0; var24 < this.a.length; var24++) {
            var2[var24 + 1] = var4[var24];
         }
      }

      int var15 = this.a.length;

      for (int var25 = this.a.length + 1; var25 >= 0; var25--) {
         if (var3[var25] != 0) {
            var15 = var25;
            break;
         }
      }

      for (int var26 = 0; var26 < this.a.length; var26++) {
         var10.a[this.a.length - var26 - 1] = var3[var15 - var26];
      }

      var10.c = this.c - var1.c + var15 - this.a.length;
      var10.b = (byte)(this.b == var1.b ? 1 : -1);
      if (var10.a[this.a.length - 1] == 0) {
         var10.c = 0;
      }

      int var16;
      if (var15 > this.a.length - 1) {
         var16 = var10.h(var3[var15 - this.a.length]);
      } else {
         var16 = var10.h(0);
      }

      if (var16 != 0) {
         var10 = this.a(var16, "divide", var1, var10);
      }

      return var10;
   }

   public final nX j(int var1) {
      if (this.d != 0) {
         if (this.J()) {
            return this;
         }

         if (this.d == 1) {
            return this.a(this);
         }
      }

      if (var1 == 0) {
         this.e.a(2);
         nX var8;
         (var8 = this.a(this.F())).b = this.b;
         var8.d = 1;
         return this.a(2, "divide", this.F(), var8);
      }

      if (var1 >= 0 && var1 < 10000) {
         nX var7 = this.a(this);
         int var3 = 0;

         for (int var4 = this.a.length - 1; var4 >= 0; var4--) {
            int var5;
            int var6 = (var5 = var3 * 10000 + var7.a[var4]) / var1;
            var3 = var5 - var6 * var1;
            var7.a[var4] = var6;
         }

         if (var7.a[this.a.length - 1] == 0) {
            var7.S();
            int var9;
            int var11 = (var9 = var3 * 10000) / var1;
            var3 = var9 - var11 * var1;
            var7.a[0] = var11;
         }

         int var10;
         if ((var10 = var7.h(var3 * 10000 / var1)) != 0) {
            var7 = this.a(var10, "divide", var7, var7);
         }

         return var7;
      } else {
         this.e.a(1);
         nX var2;
         (var2 = this.a(this.F())).d = 3;
         return this.a(1, "divide", var2, var2);
      }
   }

   private nX ab() {
      nZ var1 = this.e;
      return this.e.c.i(this);
   }

   public final nX Q() {
      if (this.d == 0 && this.a[this.a.length - 1] == 0) {
         return this.a(this);
      }

      if (this.d != 0) {
         if (this.d == 1 && this.b == 1) {
            return this.a(this);
         }

         if (this.d == 3) {
            return this.a(this);
         }

         if (this.d == 2) {
            this.e.a(1);
            nX var6 = this.a(this);
            return this.a(1, "sqrt", null, var6);
         }
      }

      if (this.b == -1) {
         this.e.a(1);
         nX var5;
         (var5 = this.a(this)).d = 3;
         return this.a(1, "sqrt", null, var5);
      }

      nX var1;
      if ((var1 = this.a(this)).c < -1 || var1.c > 1) {
         var1.c = this.c / 2;
      }

      switch (var1.a[this.a.length - 1] / 2000) {
         case 0:
            var1.a[this.a.length - 1] = var1.a[this.a.length - 1] / 2 + 1;
            break;
         case 1:
         default:
            var1.a[this.a.length - 1] = 3000;
            break;
         case 2:
            var1.a[this.a.length - 1] = 1500;
            break;
         case 3:
            var1.a[this.a.length - 1] = 2200;
      }

      this.a(var1);
      nX var3 = this.F();
      this.F();

      while (var1.d(var3)) {
         nX var2;
         (var2 = this.a(var1)).b = -1;
         var2 = var2.f(this.i(var1)).j(2);
         nX var4 = var3;
         var3 = var1;
         if ((var1 = var1.f(var2)).equals(var4) || var2.a[this.a.length - 1] == 0) {
            break;
         }
      }

      return var1;
   }

   @Override
   public String toString() {
      if (this.d != 0) {
         if (this.d == 1) {
            return this.b < 0 ? "-Infinity" : "Infinity";
         } else {
            return "NaN";
         }
      } else if (this.c <= this.a.length && this.c >= -1) {
         nX var8 = this;
         char[] var9 = new char[(this.a.length << 2) + 20];
         int var10 = 1;
         int var36 = var8.c;
         boolean var38 = false;
         var9[0] = ' ';
         if (var36 <= 0) {
            var10++;
            var9[1] = '0';
            var10++;
            var9[2] = '.';
            var38 = true;
         }

         while (var36 < 0) {
            var9[var10++] = '0';
            var9[var10++] = '0';
            var9[var10++] = '0';
            var9[var10++] = '0';
            var36++;
         }

         for (int var39 = var8.a.length - 1; var39 >= 0; var39--) {
            var9[var10++] = (char)(var8.a[var39] / 1000 + 48);
            var9[var10++] = (char)(var8.a[var39] / 100 % 10 + 48);
            var9[var10++] = (char)(var8.a[var39] / 10 % 10 + 48);
            var9[var10++] = (char)(var8.a[var39] % 10 + 48);
            if (--var36 == 0) {
               var9[var10++] = '.';
               var38 = true;
            }
         }

         while (var36 > 0) {
            var9[var10++] = '0';
            var9[var10++] = '0';
            var9[var10++] = '0';
            var9[var10++] = '0';
            var36--;
         }

         if (!var38) {
            var9[var10++] = '.';
         }

         int var27 = 1;

         while (var9[var27] == '0') {
            var27++;
         }

         if (var9[var27] == '.') {
            var27--;
         }

         while (var9[var10 - 1] == '0') {
            var10--;
         }

         if (var8.b < 0) {
            var9[--var27] = '-';
         }

         return new String(var9, var27, var10 - var27);
      } else {
         nX var1 = this;
         char[] var2 = new char[this.a.length << 2];
         char[] var3 = new char[(var1.a.length << 2) + 20];
         int var4 = 0;

         for (int var5 = var1.a.length - 1; var5 >= 0; var5--) {
            var2[var4++] = (char)(var1.a[var5] / 1000 + 48);
            var2[var4++] = (char)(var1.a[var5] / 100 % 10 + 48);
            var2[var4++] = (char)(var1.a[var5] / 10 % 10 + 48);
            var2[var4++] = (char)(var1.a[var5] % 10 + 48);
         }

         var4 = 0;

         while (var4 < var2.length && var2[var4] == '0') {
            var4++;
         }

         int var6 = var4;
         int var28 = 0;
         if (var1.b == -1) {
            var28++;
            var3[0] = '-';
         }

         if (var4 == var2.length) {
            var3[var28++] = '0';
            var3[var28++] = '.';
            var3[var28++] = '0';
            var3[var28++] = 'e';
            var3[var28] = '0';
            return new String(var3, 0, 5);
         }

         var3[var28++] = var2[var4++];
         var3[var28++] = '.';

         while (var4 < var2.length) {
            var3[var28++] = var2[var4++];
         }

         var3[var28++] = 'e';
         int var37;
         int var7 = var37 = (var1.c << 2) - var6 - 1;
         if (var37 < 0) {
            var7 = -var37;
         }

         var4 = 1000000000;

         while (var4 > var7) {
            var4 /= 10;
         }

         if (var37 < 0) {
            var3[var28++] = '-';
         }

         while (var4 > 0) {
            var3[var28++] = (char)(var7 / var4 + 48);
            var7 %= var4;
            var4 /= 10;
         }

         return new String(var3, 0, var28);
      }
   }

   private String ac() {
      char[] var1 = new char[this.a.length << 2];
      char[] var2 = new char[(this.a.length << 2) + 20];
      int var3 = 0;

      for (int var4 = this.a.length - 1; var4 >= 0; var4--) {
         var1[var3++] = (char)(this.a[var4] / 1000 + 48);
         var1[var3++] = (char)(this.a[var4] / 100 % 10 + 48);
         var1[var3++] = (char)(this.a[var4] / 10 % 10 + 48);
         var1[var3++] = (char)(this.a[var4] % 10 + 48);
      }

      var3 = 0;

      while (var3 < var1.length && var1[var3] == '0') {
         var3++;
      }

      int var5 = var3;
      int var13 = 0;
      if (this.b == -1) {
         var13++;
         var2[0] = '-';
      }

      if (var3 == var1.length) {
         var2[var13++] = '0';
         var2[var13++] = '.';
         var2[var13++] = '0';
         var2[var13++] = 'e';
         var2[var13] = '0';
         return new String(var2, 0, 5);
      }

      var2[var13++] = var1[var3++];
      var2[var13++] = '.';

      while (var3 < var1.length) {
         var2[var13++] = var1[var3++];
      }

      var2[var13++] = 'e';
      int var6;
      var5 = var6 = (this.c << 2) - var5 - 1;
      if (var6 < 0) {
         var5 = -var6;
      }

      var3 = 1000000000;

      while (var3 > var5) {
         var3 /= 10;
      }

      if (var6 < 0) {
         var2[var13++] = '-';
      }

      while (var3 > 0) {
         var2[var13++] = (char)(var5 / var3 + 48);
         var5 %= var3;
         var3 /= 10;
      }

      return new String(var2, 0, var13);
   }

   private String ad() {
      char[] var1 = new char[(this.a.length << 2) + 20];
      int var2 = 1;
      int var3 = this.c;
      boolean var4 = false;
      var1[0] = ' ';
      if (var3 <= 0) {
         var2++;
         var1[1] = '0';
         var2++;
         var1[2] = '.';
         var4 = true;
      }

      while (var3 < 0) {
         var1[var2++] = '0';
         var1[var2++] = '0';
         var1[var2++] = '0';
         var1[var2++] = '0';
         var3++;
      }

      for (int var5 = this.a.length - 1; var5 >= 0; var5--) {
         var1[var2++] = (char)(this.a[var5] / 1000 + 48);
         var1[var2++] = (char)(this.a[var5] / 100 % 10 + 48);
         var1[var2++] = (char)(this.a[var5] / 10 % 10 + 48);
         var1[var2++] = (char)(this.a[var5] % 10 + 48);
         if (--var3 == 0) {
            var1[var2++] = '.';
            var4 = true;
         }
      }

      while (var3 > 0) {
         var1[var2++] = '0';
         var1[var2++] = '0';
         var1[var2++] = '0';
         var1[var2++] = '0';
         var3--;
      }

      if (!var4) {
         var1[var2++] = '.';
      }

      var3 = 1;

      while (var1[var3] == '0') {
         var3++;
      }

      if (var1[var3] == '.') {
         var3--;
      }

      while (var1[var2 - 1] == '0') {
         var2--;
      }

      if (this.b < 0) {
         var1[--var3] = '-';
      }

      return new String(var1, var3, var2 - var3);
   }

   public final nX a(int var1, String var2, nX var3, nX var4) {
      nX var5 = var4;
      switch (var1) {
         case 1:
            (var5 = this.a(this.F())).b = var4.b;
            var5.d = 3;
            break;
         case 2:
            if (this.d == 0 && this.a[this.a.length - 1] != 0) {
               (var5 = this.a(this.F())).b = (byte)(this.b * var3.b);
               var5.d = 1;
            }

            if (this.d == 0 && this.a[this.a.length - 1] == 0) {
               (var5 = this.a(this.F())).d = 3;
            }

            if (this.d == 1 || this.d == 3) {
               (var5 = this.a(this.F())).d = 3;
            }

            if (this.d == 1 || this.d == 2) {
               (var5 = this.a(this.F())).d = 3;
            }
            break;
         case 3:
         case 5:
         case 6:
         case 7:
         default:
            var5 = var4;
            break;
         case 4:
            var4.c -= 32760;
            (var5 = this.a(this.F())).b = var4.b;
            var5.d = 1;
            break;
         case 8:
            if (var4.c + this.a.length < -32767) {
               (var5 = this.a(this.F())).b = var4.b;
            } else {
               var5 = this.a(var4);
            }

            var4.c += 32760;
      }

      return var5;
   }

   private static nX k(nX var0) {
      return var0;
   }

   public final int R() {
      return this.d;
   }

   public static nX a(nX var0, nX var1) {
      nX var2;
      (var2 = var0.a(var0)).b = var1.b;
      return var2;
   }

   public nX j(nX var1) {
      nZ var3 = this.e;
      var3 = var1.e;
      if (this.e.a != var1.e.a) {
         this.e.a(1);
         nX var7;
         (var7 = this.a(this.F())).d = 3;
         return this.a(1, "nextAfter", var1, var7);
      }

      boolean var2 = false;
      if (this.b(var1)) {
         var2 = true;
      }

      if (b(this, var1) == 0) {
         return this.a(var1);
      }

      if (this.b(this.F())) {
         var2 = !var2;
      }

      nX var5;
      if (var2) {
         nX var4;
         (var4 = this.a(this.G())).c = this.c - this.a.length + 1;
         var4.b = this.b;
         if (this.equals(this.F())) {
            var4.c = -32767 - this.a.length;
         }

         var5 = this.f(var4);
      } else {
         nX var6;
         (var6 = this.a(this.G())).c = this.c;
         var6.b = this.b;
         if (this.equals(var6)) {
            var6.c = this.c - this.a.length;
         } else {
            var6.c = this.c - this.a.length + 1;
         }

         if (this.equals(this.F())) {
            var6.c = -32767 - this.a.length;
         }

         var5 = this.g(var6);
      }

      if (var5.d == 1 && this.d != 1) {
         this.e.a(16);
         var5 = this.a(16, "nextAfter", var1, var5);
      }

      if (var5.equals(this.F()) && !this.equals(this.F())) {
         this.e.a(16);
         var5 = this.a(16, "nextAfter", var1, var5);
      }

      return var5;
   }

   private double ae() {
      if (this.Y()) {
         return this.b(this.F()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
      }

      if (this.J()) {
         return Double.NaN;
      }

      nX var1 = this;
      boolean var2 = false;
      int var3;
      if ((var3 = b(this, this.F())) == 0) {
         return this.b < 0 ? -0.0 : 0.0;
      }

      if (var3 < 0) {
         var1 = this.P();
         var2 = true;
      }

      if ((var3 = (int)(var1.O() * 3.32)) < 0) {
         var3--;
      }

      for (nX var4 = oa.a(this.H(), var3); var4.b(var1) || var4.equals(var1); var3++) {
         var4 = var4.i(2);
      }

      var1 = var1.i(oa.a(this.H(), --var3));
      if (var3 > -1023) {
         var1 = var1.g(this.G());
      }

      if (var3 < -1074) {
         return 0.0;
      }

      if (var3 > 1023) {
         return var2 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
      }

      long var7;
      String var14;
      if ((var7 = Long.parseLong((var14 = var1.h(this.a(4503599627370496L)).l(nZ.a.ROUND_HALF_EVEN$7d08181e).toString()).substring(0, var14.length() - 1)))
         == 4503599627370496L) {
         var7 = 0L;
         var3++;
      }

      if (var3 <= -1023) {
         var3--;
      }

      while (var3 < -1023) {
         var3++;
         var7 >>>= 1;
      }

      long var9;
      double var11 = Double.longBitsToDouble(var9 = var7 | var3 + 1023L << 52);
      if (var2) {
         var11 = -var11;
      }

      return var11;
   }

   private double[] af() {
      double[] var1;
      (var1 = new double[2])[0] = Double.longBitsToDouble(Double.doubleToLongBits(this.ae()) & -1073741824L);
      var1[1] = this.g(this.h(var1[0])).ae();
      return var1;
   }

   @Override
   public final double d() {
      return this.ae();
   }

   private nX i(double var1) {
      return this.f(this.h(var1));
   }

   private nX j(double var1) {
      return this.g(this.h(var1));
   }

   private nX k(double var1) {
      return this.h(this.h(var1));
   }

   private nX l(double var1) {
      return this.i(this.h(var1));
   }

   private nX m(double var1) {
      return this.e(this.h(var1));
   }

   @Override
   public final long i() {
      return BY.B(this.ae());
   }

   private nX ag() {
      return !this.J() && !this.Z() ? this.e(this.b > 0 ? 1 : -1) : this;
   }

   private nX l(nX var1) {
      return (this.b < 0 || var1.b < 0) && (this.b >= 0 || var1.b >= 0) ? this.P() : this;
   }

   private nX n(double var1) {
      long var3 = Double.doubleToLongBits(var1);
      return (this.b < 0 || var3 < 0L) && (this.b >= 0 || var3 >= 0L) ? this.P() : this;
   }

   private nX o(int var1) {
      return this.h(oa.a(this.H(), var1));
   }

   private nX m(nX var1) {
      return this.h(this).f(var1.h(var1)).Q();
   }

   private nX ah() {
      return this.p(3);
   }

   private nX p(int var1) {
      return this.b >= 0 ? oa.a(this, this.G().j(var1)) : oa.a(this.P(), this.G().j(var1)).P();
   }

   private nX o(double var1) {
      return oa.a(this, this.h(var1));
   }

   private nX q(int var1) {
      return oa.a(this, var1);
   }

   private nX n(nX var1) {
      return oa.a(this, var1);
   }

   private nX ai() {
      return oa.a(this);
   }

   private nX aj() {
      return oa.a(this).g(this.G());
   }

   private nX ak() {
      return oa.b(this);
   }

   private nX al() {
      return oa.b(this.f(this.G()));
   }

   @Deprecated
   private int am() {
      return this.O();
   }

   private nX an() {
      return oa.d(this);
   }

   private nX ao() {
      return oa.c(this);
   }

   private nX ap() {
      nX var1 = this;
      return oa.c(this).i(oa.d(var1));
   }

   private nX aq() {
      nX var1 = this;
      boolean var3 = false;
      if (var1.b(var1.F())) {
         var3 = true;
      }

      nX var4;
      nX var2 = oa.f((var4 = a(var1, var1.G())).G().g(var4.h(var4)).Q().i(var4));
      if (var3) {
         nZ var5 = var4.e;
         var2 = var4.e.e.g(var2);
      }

      return var4.a(var2);
   }

   private nX ar() {
      nX var1 = this;
      return oa.f(this.i(var1.G().g(var1.h(var1)).Q()));
   }

   private nX as() {
      return oa.f(this);
   }

   private nX o(nX var1) {
      nX var2 = var1.h(var1).f(this.h(this)).Q();
      if (var1.b >= 0) {
         return this.H().h(oa.f(this.i(var2.f(var1))));
      }

      var1 = this.H().h(oa.f(this.i(var2.g(var1))));
      return this.h(var1.b <= 0 ? -Math.PI : Math.PI).g(var1);
   }

   private nX at() {
      return oa.a(this).f(oa.a(this.P())).j(2);
   }

   private nX au() {
      return oa.a(this).g(oa.a(this.P())).j(2);
   }

   private nX av() {
      nX var1 = oa.a(this);
      nX var2 = oa.a(this.P());
      return var1.g(var2).i(var1.f(var2));
   }

   private nX aw() {
      return oa.b(this.h(this).g(this.G()).Q().f(this));
   }

   private nX ax() {
      return oa.b(this.h(this).f(this.G()).Q().f(this));
   }

   private nX ay() {
      return oa.b(this.G().f(this).i(this.G().g(this))).j(2);
   }

   private nX a(nX[] var1, nX[] var2) {
      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      nX var3 = this.F();

      for (int var4 = 0; var4 < var1.length; var4++) {
         var3 = var3.f(var1[var4].h(var2[var4]));
      }

      return var3;
   }

   private nX a(double[] var1, nX[] var2) {
      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      nX var3 = this.F();

      for (int var4 = 0; var4 < var1.length; var4++) {
         var3 = var3.f(var2[var4].k(var1[var4]));
      }

      return var3;
   }

   private static nX a(nX var0, nX var1, nX var2, nX var3) {
      return var0.h(var1).f(var2.h(var3));
   }

   private static nX a(double var0, nX var2, double var3, nX var5) {
      return var2.k(var0).f(var5.k(var3));
   }

   private static nX a(nX var0, nX var1, nX var2, nX var3, nX var4, nX var5) {
      return var0.h(var1).f(var2.h(var3)).f(var4.h(var5));
   }

   private static nX a(double var0, nX var2, double var3, nX var5, double var6, nX var8) {
      return var2.k(var0).f(var5.k(var3)).f(var8.k(var6));
   }

   private static nX a(nX var0, nX var1, nX var2, nX var3, nX var4, nX var5, nX var6, nX var7) {
      return var0.h(var1).f(var2.h(var3)).f(var4.h(var5)).f(var6.h(var7));
   }

   private static nX a(double var0, nX var2, double var3, nX var5, double var6, nX var8, double var9, nX var11) {
      return var2.k(var0).f(var5.k(var3)).f(var8.k(var6)).f(var11.k(var9));
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6, double var7, kO var9, double var10, kO var12) {
      nX var10001 = var3;
      nX var10003 = (nX)var6;
      nX var10005 = (nX)var9;
      nX var26 = (nX)var12;
      double var22 = var10;
      var3 = var10005;
      double var19 = var7;
      nX var2 = var10003;
      double var16 = var4;
      nX var24 = var10001;
      double var13 = var1;
      return var24.k(var13).f(var2.k(var16)).f(var3.k(var19)).f(var26.k(var22));
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, kO var3, kO var4, kO var5, kO var6, kO var7, kO var8) {
      nX var10000 = (nX)var1;
      nX var10001 = var2;
      nX var10002 = var3;
      nX var10003 = var4;
      nX var10004 = var5;
      nX var10005 = var6;
      nX var10006 = var7;
      var8 = var8;
      var7 = var10006;
      var6 = var10005;
      var5 = var10004;
      var4 = var10003;
      var3 = var10002;
      var2 = var10001;
      return var10000.h(var2).f(var3.h(var4)).f(var5.h(var6)).f(var7.h(var8));
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6, double var7, kO var9) {
      nX var10001 = var3;
      nX var10003 = (nX)var6;
      var3 = (nX)var9;
      double var16 = var7;
      nX var2 = var10003;
      double var13 = var4;
      nX var18 = var10001;
      double var10 = var1;
      return var18.k(var10).f(var2.k(var13)).f(var3.k(var16));
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, kO var3, kO var4, kO var5, kO var6) {
      nX var10000 = (nX)var1;
      nX var10001 = var2;
      nX var10002 = var3;
      nX var10003 = var4;
      nX var10004 = var5;
      var6 = var6;
      var5 = var10004;
      var4 = var10003;
      var3 = var10002;
      var2 = var10001;
      return var10000.h(var2).f(var3.h(var4)).f(var5.h(var6));
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6) {
      nX var10001 = (nX)var3;
      nX var2 = (nX)var6;
      double var10 = var4;
      nX var12 = var10001;
      double var7 = var1;
      return var12.k(var7).f(var2.k(var10));
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, Object var3, kO var4) {
      nX var10000 = (nX)var1;
      nX var10001 = var2;
      nX var10002 = var3;
      var4 = var4;
      var3 = var10002;
      var2 = var10001;
      return var10000.h(var2).f(var3.h(var4));
   }

   // $VF: synthetic method
   @Override
   public final kO a(double[] var1, Object[] var2) {
      nX[] var3 = (nX[])var2;
      double[] var7 = var1;
      nX var5 = this;
      if (var7.length != var3.length) {
         throw new oS(var7.length, var3.length);
      }

      nX var6 = var5.F();

      for (int var4 = 0; var4 < var7.length; var4++) {
         var6 = var6.f(var3[var4].k(var7[var4]));
      }

      return var6;
   }

   // $VF: synthetic method
   @Override
   public final kO a(Object[] var1, Object[] var2) {
      nX[] var10001 = (nX[])var1;
      nX[] var3 = var2;
      var2 = var10001;
      nX var5 = this;
      if (var2.length != var3.length) {
         throw new oS(var2.length, var3.length);
      }

      nX var6 = var5.F();

      for (int var4 = 0; var4 < var2.length; var4++) {
         var6 = var6.f(var2[var4].h(var3[var4]));
      }

      return var6;
   }

   // $VF: synthetic method
   @Override
   public final kO B() {
      nX var1 = this;
      return oa.b(this.G().f(var1).i(var1.G().g(var1))).j(2);
   }

   // $VF: synthetic method
   @Override
   public final kO A() {
      nX var1 = this;
      return oa.b(this.h(var1).f(var1.G()).Q().f(var1));
   }

   // $VF: synthetic method
   @Override
   public final kO z() {
      nX var1 = this;
      return oa.b(this.h(var1).g(var1.G()).Q().f(var1));
   }

   // $VF: synthetic method
   @Override
   public final kO y() {
      nX var1 = this;
      nX var2 = oa.a(this);
      var1 = oa.a(var1.P());
      return var2.g(var1).i(var2.f(var1));
   }

   // $VF: synthetic method
   @Override
   public final kO x() {
      nX var1 = this;
      return oa.a(this).g(oa.a(var1.P())).j(2);
   }

   // $VF: synthetic method
   @Override
   public final kO w() {
      nX var1 = this;
      return oa.a(this).f(oa.a(var1.P())).j(2);
   }

   // $VF: synthetic method
   @Override
   public final kO h(Object var1) {
      nX var2 = var1;
      var1 = this;
      nX var3 = var2.h(var2).f(var1.h(var1)).Q();
      if (var2.b >= 0) {
         return var1.H().h(oa.f(var1.i(var3.f(var2))));
      }

      var2 = var1.H().h(oa.f(var1.i(var3.g(var2))));
      return var1.h(var2.b <= 0 ? -Math.PI : Math.PI).g(var2);
   }

   // $VF: synthetic method
   @Override
   public final kO v() {
      return oa.f(this);
   }

   // $VF: synthetic method
   @Override
   public final kO u() {
      nX var1 = this;
      return oa.f(this.i(var1.G().g(var1.h(var1)).Q()));
   }

   // $VF: synthetic method
   @Override
   public final kO t() {
      nX var1 = this;
      boolean var3 = false;
      if (var1.b(var1.F())) {
         var3 = true;
      }

      nX var4;
      nX var2 = oa.f((var4 = a(var1, var1.G())).G().g(var4.h(var4)).Q().i(var4));
      if (var3) {
         nZ var5 = var4.e;
         var2 = var4.e.e.g(var2);
      }

      return var4.a(var2);
   }

   // $VF: synthetic method
   @Override
   public final kO s() {
      nX var1 = this;
      return oa.c(this).i(oa.d(var1));
   }

   // $VF: synthetic method
   @Override
   public final kO r() {
      return oa.c(this);
   }

   // $VF: synthetic method
   @Override
   public final kO q() {
      return oa.d(this);
   }

   // $VF: synthetic method
   @Override
   public final kO p() {
      nX var1 = this;
      return oa.b(this.f(var1.G()));
   }

   // $VF: synthetic method
   @Override
   public final kO o() {
      return oa.b(this);
   }

   // $VF: synthetic method
   @Override
   public final kO n() {
      nX var1 = this;
      return oa.a(this).g(var1.G());
   }

   // $VF: synthetic method
   @Override
   public final kO m() {
      return oa.a(this);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kO a(kO var1) {
      nX var2 = (nX)var1;
      return oa.a(this, var2);
   }

   // $VF: synthetic method
   @Override
   public final kO d(int var1) {
      int var2 = var1;
      return oa.a(this, var2);
   }

   // $VF: synthetic method
   @Override
   public final kO g(double var1) {
      double var4 = var1;
      nX var6 = this;
      return oa.a(this, var6.h(var4));
   }

   // $VF: synthetic method
   @Override
   public final kO c(int var1) {
      return this.p(var1);
   }

   // $VF: synthetic method
   @Override
   public final kO l() {
      return this.p(3);
   }

   // $VF: synthetic method
   @Override
   public final kO k() {
      return this.Q();
   }

   // $VF: synthetic method
   @Override
   public final kN b() {
      nX var1 = this;
      nZ var2 = this.e;
      return this.e.c.i(var1);
   }

   // $VF: synthetic method
   @Override
   public final kO g(Object var1) {
      nX var2 = var1;
      var1 = this;
      return this.h(var1).f(var2.h(var2)).Q();
   }

   // $VF: synthetic method
   @Override
   public final kO b(int var1) {
      int var2 = var1;
      nX var3 = this;
      return this.h(oa.a(var3.H(), var2));
   }

   // $VF: synthetic method
   @Override
   public final kO f(double var1) {
      double var4 = var1;
      nX var8 = this;
      long var6 = Double.doubleToLongBits(var4);
      return (var8.b < 0 || var6 < 0L) && (var8.b >= 0 || var6 >= 0L) ? var8.P() : var8;
   }

   // $VF: synthetic method
   @Override
   public final kO f(Object var1) {
      nX var2 = var1;
      var1 = this;
      return (this.b < 0 || var2.b < 0) && (var1.b >= 0 || var2.b >= 0) ? var1.P() : var1;
   }

   // $VF: synthetic method
   @Override
   public final kO j() {
      nX var1 = this;
      return !this.J() && !var1.Z() ? var1.e(var1.b > 0 ? 1 : -1) : var1;
   }

   // $VF: synthetic method
   @Override
   public final kO h() {
      return this.l(nZ.a.ROUND_HALF_EVEN$7d08181e);
   }

   // $VF: synthetic method
   @Override
   public final kO g() {
      return this.l(nZ.a.ROUND_FLOOR$7d08181e);
   }

   // $VF: synthetic method
   @Override
   public final kO f() {
      return this.l(nZ.a.ROUND_CEIL$7d08181e);
   }

   // $VF: synthetic method
   @Override
   public final kO e() {
      nX var1 = this;
      nX var2;
      (var2 = this.a(var1)).b = 1;
      return var2;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kO e(Object var1) {
      return this.e((nX)var1);
   }

   // $VF: synthetic method
   @Override
   public final kO e(double var1) {
      double var4 = var1;
      nX var6 = this;
      return this.e(var6.h(var4));
   }

   // $VF: synthetic method
   @Override
   public final kO d(double var1) {
      double var4 = var1;
      nX var6 = this;
      return this.i(var6.h(var4));
   }

   // $VF: synthetic method
   @Override
   public final kO c(double var1) {
      return this.k(var1);
   }

   // $VF: synthetic method
   @Override
   public final kO b(double var1) {
      double var4 = var1;
      nX var6 = this;
      return this.g(var6.h(var4));
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1) {
      double var4 = var1;
      nX var6 = this;
      return this.f(var6.h(var4));
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kM c() {
      return this.e;
   }

   // $VF: synthetic method
   @Override
   public final kN d(Object var1) {
      return this.i((nX)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN c(Object var1) {
      return this.h((nX)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN a(int var1) {
      return this.i(var1);
   }

   // $VF: synthetic method
   @Override
   public final kN a() {
      return this.P();
   }

   // $VF: synthetic method
   @Override
   public final kN b(Object var1) {
      return this.g((nX)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN a(Object var1) {
      return this.f((nX)var1);
   }
}
