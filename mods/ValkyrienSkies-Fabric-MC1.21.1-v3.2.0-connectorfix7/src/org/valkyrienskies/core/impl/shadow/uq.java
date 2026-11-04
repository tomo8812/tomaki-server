package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public final class uq implements Serializable, Iterable<ue> {
   private static final long d = 1L;
   public final ud a;
   public final int b;
   public final int c;
   private final boolean e;
   private final boolean f;
   private final int g;
   private final long[][] h;

   uq(boolean var1, boolean var2, int var3, double[][][] var4) {
      this.b = var4.length;
      this.c = var4[0].length;
      if (this.b < 2) {
         throw new pn(this.b, 2, true);
      }

      if (this.c < 2) {
         throw new pn(this.c, 2, true);
      }

      this.e = (boolean)var1;
      this.f = var2;
      this.g = var3;
      var1 = var4[0][0].length;
      this.a = new ud(0L, var1);
      this.h = new long[this.b][this.c];

      for (int var6 = 0; var6 < this.b; var6++) {
         for (int var7 = 0; var7 < this.c; var7++) {
            this.h[var6][var7] = this.a.a(var4[var6][var7]);
         }
      }

      this.e();
   }

   private uq(int var1, boolean var2, int var3, boolean var4, int var5, ua[] var6) {
      if (var1 < 2) {
         throw new pn(var1, 2, true);
      }

      if (var3 < 2) {
         throw new pn(var3, 2, true);
      }

      this.b = var1;
      this.e = (boolean)var2;
      this.c = var3;
      this.f = var4;
      this.g = var5;
      this.h = new long[this.b][this.c];
      var2 = var6.length;
      this.a = new ud(0L, var2);

      for (int var10 = 0; var10 < var1; var10++) {
         for (int var11 = 0; var11 < var3; var11++) {
            double[] var7 = new double[var2];

            for (int var8 = 0; var8 < var2; var8++) {
               var7[var8] = var6[var8].a();
            }

            this.h[var10][var11] = this.a.a(var7);
         }
      }

      this.e();
   }

   private uq(boolean var1, boolean var2, int var3, ud var4, long[][] var5) {
      this.b = var5.length;
      this.c = var5[0].length;
      this.e = var1;
      this.f = var2;
      this.g = var3;
      this.a = var4;
      this.h = var5;
   }

   private synchronized uq d() {
      long[][] var1 = new long[this.b][this.c];

      for (int var2 = 0; var2 < this.b; var2++) {
         for (int var3 = 0; var3 < this.c; var3++) {
            var1[var2][var3] = this.h[var2][var3];
         }
      }

      return new uq(this.e, this.f, this.g, this.a.a(), var1);
   }

   @Override
   public final Iterator<ue> iterator() {
      return this.a.iterator();
   }

   public final ud a() {
      return this.a;
   }

   public final int b() {
      return this.b;
   }

   public final int c() {
      return this.c;
   }

   public final ue a(int var1, int var2) {
      if (var1 < 0 || var1 >= this.b) {
         throw new po(var1, 0, this.b - 1);
      } else if (var2 >= 0 && var2 < this.c) {
         return this.a.a(this.h[var1][var2]);
      } else {
         throw new po(var2, 0, this.c - 1);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final ue a(int var1, int var2, int var3, int var4) {
      int var5 = var4;
      var4 = var3;
      var3 = var2;
      var2 = var1;
      uq var6 = this;
      byte var13;
      switch (<unrepresentable>.a[var4 - 1]) {
         case 1:
            var13 = -1;
            break;
         case 2:
            var13 = 1;
            break;
         case 3:
            var13 = 0;
            break;
         default:
            throw new oY();
      }

      var3 += var13;
      if (var6.f) {
         if (var3 < 0) {
            var3 += var6.c;
         } else {
            var3 %= var6.c;
         }
      }

      switch (<unrepresentable>.b[var5 - 1]) {
         case 1:
            var13 = -1;
            break;
         case 2:
            var13 = 1;
            break;
         case 3:
            var13 = 0;
            break;
         default:
            throw new oY();
      }

      var2 += var13;
      if (var6.e) {
         if (var2 < 0) {
            var2 += var6.b;
         } else {
            var2 %= var6.b;
         }
      }

      int[] var10000 = var2 >= 0 && var2 < var6.b && var3 >= 0 && var3 < var6.c ? new int[]{var2, var3} : null;
      int[] var7 = var10000;
      return var10000 == null ? null : this.a(var7[0], var7[1]);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private int[] b(int var1, int var2, int var3, int var4) {
      byte var7;
      switch (<unrepresentable>.a[var3 - 1]) {
         case 1:
            var7 = -1;
            break;
         case 2:
            var7 = 1;
            break;
         case 3:
            var7 = 0;
            break;
         default:
            throw new oY();
      }

      var2 += var7;
      if (this.f) {
         if (var2 < 0) {
            var2 += this.c;
         } else {
            var2 %= this.c;
         }
      }

      switch (<unrepresentable>.b[var4 - 1]) {
         case 1:
            var7 = -1;
            break;
         case 2:
            var7 = 1;
            break;
         case 3:
            var7 = 0;
            break;
         default:
            throw new oY();
      }

      var1 += var7;
      if (this.e) {
         if (var1 < 0) {
            var1 += this.b;
         } else {
            var1 %= this.b;
         }
      }

      return var1 >= 0 && var1 < this.b && var2 >= 0 && var2 < this.c ? new int[]{var1, var2} : null;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void e() {
      ArrayList var1 = new ArrayList();
      int var2 = this.b - 1;
      int var3 = this.c - 1;

      for (int var4 = 0; var4 < this.b; var4++) {
         for (int var5 = 0; var5 < this.c; var5++) {
            var1.clear();
            switch (<unrepresentable>.c[this.g - 1]) {
               case 1:
                  if (var4 > 0) {
                     if (var5 > 0) {
                        var1.add(this.h[var4 - 1][var5 - 1]);
                     }

                     if (var5 < var3) {
                        var1.add(this.h[var4 - 1][var5 + 1]);
                     }
                  }

                  if (var4 < var2) {
                     if (var5 > 0) {
                        var1.add(this.h[var4 + 1][var5 - 1]);
                     }

                     if (var5 < var3) {
                        var1.add(this.h[var4 + 1][var5 + 1]);
                     }
                  }

                  if (this.e) {
                     if (var4 == 0) {
                        if (var5 > 0) {
                           var1.add(this.h[var2][var5 - 1]);
                        }

                        if (var5 < var3) {
                           var1.add(this.h[var2][var5 + 1]);
                        }
                     } else if (var4 == var2) {
                        if (var5 > 0) {
                           var1.add(this.h[0][var5 - 1]);
                        }

                        if (var5 < var3) {
                           var1.add(this.h[0][var5 + 1]);
                        }
                     }
                  }

                  if (this.f) {
                     if (var5 == 0) {
                        if (var4 > 0) {
                           var1.add(this.h[var4 - 1][var3]);
                        }

                        if (var4 < var2) {
                           var1.add(this.h[var4 + 1][var3]);
                        }
                     } else if (var5 == var3) {
                        if (var4 > 0) {
                           var1.add(this.h[var4 - 1][0]);
                        }

                        if (var4 < var2) {
                           var1.add(this.h[var4 + 1][0]);
                        }
                     }
                  }

                  if (this.e && this.f) {
                     if (var4 == 0 && var5 == 0) {
                        var1.add(this.h[var2][var3]);
                     } else if (var4 == 0 && var5 == var3) {
                        var1.add(this.h[var2][0]);
                     } else if (var4 == var2 && var5 == 0) {
                        var1.add(this.h[0][var3]);
                     } else if (var4 == var2 && var5 == var3) {
                        var1.add(this.h[0][0]);
                     }
                  }
               case 2:
                  break;
               default:
                  throw new oY();
            }

            if (var4 > 0) {
               var1.add(this.h[var4 - 1][var5]);
            }

            if (var4 < var2) {
               var1.add(this.h[var4 + 1][var5]);
            }

            if (this.e) {
               if (var4 == 0) {
                  var1.add(this.h[var2][var5]);
               } else if (var4 == var2) {
                  var1.add(this.h[0][var5]);
               }
            }

            if (var5 > 0) {
               var1.add(this.h[var4][var5 - 1]);
            }

            if (var5 < var3) {
               var1.add(this.h[var4][var5 + 1]);
            }

            if (this.f) {
               if (var5 == 0) {
                  var1.add(this.h[var4][var3]);
               } else if (var5 == var3) {
                  var1.add(this.h[var4][0]);
               }
            }

            ue var6 = this.a.a(this.h[var4][var5]);

            for (long var8 : var1) {
               ue var10 = this.a.a(var8);
               this.a.a(var6, var10);
            }
         }
      }
   }

   private static void f() {
      throw new IllegalStateException();
   }

   private uq.b g() {
      double[][][] var1 = new double[this.b][this.c][];

      for (int var2 = 0; var2 < this.b; var2++) {
         for (int var3 = 0; var3 < this.c; var3++) {
            var1[var2][var3] = this.a(var2, var3).c();
         }
      }

      return new uq.b(this.e, this.f, this.g, var1);
   }

   public enum a {
      public static final int RIGHT$75c8caca = 1;
      public static final int CENTER$75c8caca = 2;
      public static final int LEFT$75c8caca = 3;
      // $VF: synthetic field
      private static final int[] $VALUES$d8f568f = new int[]{RIGHT$75c8caca, CENTER$75c8caca, LEFT$75c8caca};

      public static int[] values$639f4910() {
         return (int[])$VALUES$d8f568f.clone();
      }

      private a(String var1, int var2) {
         super(var1, var2);
      }
   }

   static class b implements Serializable {
      private static final long a = 20130226L;
      private final boolean b;
      private final boolean c;
      private final int d;
      private final double[][][] e;

      b(boolean var1, boolean var2, int var3, double[][][] var4) {
         this.b = var1;
         this.c = var2;
         this.d = var3;
         this.e = var4;
      }

      private uq a() {
         return new uq(this.b, this.c, this.d, this.e);
      }
   }

   public enum c {
      public static final int UP$1c17da1c = 1;
      public static final int CENTER$1c17da1c = 2;
      public static final int DOWN$1c17da1c = 3;
      // $VF: synthetic field
      private static final int[] $VALUES$4ad76cdf = new int[]{UP$1c17da1c, CENTER$1c17da1c, DOWN$1c17da1c};

      public static int[] values$381ace1e() {
         return (int[])$VALUES$4ad76cdf.clone();
      }

      private c(String var1, int var2) {
         super(var1, var2);
      }
   }
}
