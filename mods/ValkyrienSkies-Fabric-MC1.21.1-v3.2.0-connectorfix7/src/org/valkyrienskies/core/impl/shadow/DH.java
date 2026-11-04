package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class DH<N extends DH<N>> implements Comparable<N> {
   private static final Comparator<Comparable<Object>> a = new Comparator<Comparable<Object>>() {
      private static int a(Comparable<Object> var0, Comparable<Object> var1) {
         return var0.compareTo(var1);
      }

      // $VF: synthetic method
      @Override
      public final int compare(Object var1, Object var2) {
         Comparable var10000 = (Comparable)var1;
         Comparable var3 = (Comparable)var2;
         return var10000.compareTo(var3);
      }
   };
   public N i;
   public N j;
   public N k;
   public boolean l;

   public boolean b() {
      return false;
   }

   public void c() {
   }

   public final boolean d() {
      return this.j == null;
   }

   public final N e() {
      DH var1 = this;

      while (var1.i != null) {
         var1 = var1.i;
      }

      return (N)var1;
   }

   public final N f() {
      if (this.d()) {
         return null;
      }

      DH var1 = this;

      while (!var1.j.d()) {
         var1 = var1.j;
      }

      return (N)var1;
   }

   public final N g() {
      if (this.d()) {
         return null;
      }

      DH var1 = this;

      while (!var1.k.d()) {
         var1 = var1.k;
      }

      return (N)var1;
   }

   public final N h() {
      if (!this.j.d()) {
         DH var2 = this.j;

         while (!var2.k.d()) {
            var2 = var2.k;
         }

         return (N)var2;
      } else {
         if (this.i == null) {
            return null;
         }

         DH var1 = this;

         while (var1.i != null && var1.i.j == var1) {
            var1 = var1.i;
         }

         return var1.i;
      }
   }

   public final N i() {
      if (!this.k.d()) {
         DH var2 = this.k;

         while (!var2.j.d()) {
            var2 = var2.j;
         }

         return (N)var2;
      } else {
         if (this.i == null) {
            return null;
         }

         DH var1 = this;

         while (var1.i != null && var1.i.k == var1) {
            var1 = var1.i;
         }

         return var1.i;
      }
   }

   private boolean a() {
      if (!this.d() && !this.k.d()) {
         DH var1 = this.k;
         this.k = var1.j;
         DH var2 = this;
         if (!this.k.d()) {
            this.k.i = (N)var2;
         }

         var1.i = this.i;
         this.i = (N)var1;
         var1.j = (N)var2;
         if (var1.i != null) {
            if (var1.i.j == this) {
               var1.i.j = (N)var1;
            } else {
               var1.i.k = (N)var1;
            }
         }

         this.b();
         return var1.b();
      } else {
         throw new IllegalArgumentException("The node or its right child is a leaf");
      }
   }

   private boolean m() {
      if (!this.d() && !this.j.d()) {
         DH var1 = this.j;
         this.j = var1.k;
         DH var2 = this;
         if (!this.j.d()) {
            this.j.i = (N)var2;
         }

         var1.i = this.i;
         this.i = (N)var1;
         var1.k = (N)var2;
         if (var1.i != null) {
            if (var1.i.j == this) {
               var1.i.j = (N)var1;
            } else {
               var1.i.k = (N)var1;
            }
         }

         this.b();
         return var1.b();
      } else {
         throw new IllegalArgumentException("The node or its left child is a leaf");
      }
   }

   private void a(boolean var1) {
      if (!this.l) {
         throw new IllegalArgumentException("The node must be red");
      }

      boolean var2 = var1;
      if (var1) {
         this.b();
      }

      DH var3 = this;

      while (var3.i != null && var3.i.l) {
         DH var4 = var3.i;
         DH var5 = var3.i.i;
         if (!var3.i.i.j.l || !var5.k.l) {
            if (var4.j == var3) {
               if (var5.k == var4) {
                  var4.m();
                  var3 = var4;
                  var4 = var4.i;
               }
            } else if (var5.j == var4) {
               var4.a();
               var3 = var4;
               var4 = var4.i;
            }

            if (var4.j == var3) {
               boolean var6 = var5.m();
               if (var1) {
                  var2 = var6;
               }
            } else {
               boolean var7 = var5.a();
               if (var1) {
                  var2 = var7;
               }
            }

            var4.l = false;
            var5.l = true;
            var3 = var4;
            break;
         }

         var5.j.l = false;
         var5.k.l = false;
         var5.l = true;
         if (var2 && (var2 = var4.b())) {
            var2 = var5.b();
         }

         var3 = var5;
      }

      if (var3.i == null) {
         var3.l = false;
      }

      if (var2) {
         var3 = var3.i;

         while (var3 != null && var3.b()) {
            var3 = var3.i;
         }
      }
   }

   private void n() {
      this.a(true);
   }

   private N b(boolean var1) {
      this.a(var1);
      return this.e();
   }

   public final N j() {
      this.a(true);
      return this.e();
   }

   private static Comparator<N> o() {
      var var0 = a;
      return a;
   }

   private N a(N var1, boolean var2, Comparator<? super N> var3) {
      if (this.i != null) {
         throw new IllegalArgumentException("This is not the root of a tree");
      }

      DH var4 = this;
      if (this.d()) {
         var1.l = false;
         var1.j = (N)var4;
         var1.k = (N)var4;
         var1.i = null;
         var1.b();
         return (N)var1;
      }

      if (var3 == null) {
         var3 = a;
         var3 = a;
      }

      DH var5 = var4;

      while (true) {
         int var6;
         if ((var6 = var3.compare(var1, var5)) < 0) {
            if (var5.j.d()) {
               var1.j = var5.j;
               var1.k = var5.j;
               var5.j = (N)var1;
               var1.i = (N)var5;
               break;
            }

            var5 = var5.j;
         } else {
            if (var6 <= 0 && !var2) {
               var1.i = null;
               return (N)var4;
            }

            if (var5.k.d()) {
               var1.j = var5.k;
               var1.k = var5.k;
               var5.k = (N)var1;
               var1.i = (N)var5;
               break;
            }

            var5 = var5.k;
         }
      }

      var1.l = true;
      return (N)var1.j();
   }

   private N p() {
      DH var1;
      boolean var2 = (var1 = this.i()).l;
      DH var3 = var1.j;
      DH var4 = var1.k;
      DH var5 = var1.i;
      var1.l = this.l;
      var1.j = this.j;
      var1.k = this.k;
      var1.i = this.i;
      if (this.i != null) {
         if (this.i.j == this) {
            this.i.j = (N)var1;
         } else {
            this.i.k = (N)var1;
         }
      }

      DH var6 = this;
      this.l = var2;
      this.j = (N)var3;
      this.k = (N)var4;
      if (var5 == this) {
         this.i = (N)var1;
         this.i.k = (N)var6;
      } else {
         this.i = (N)var5;
         this.i.j = (N)var6;
      }

      var1.k.i = (N)var1;
      if (!var1.j.d()) {
         var1.j.i = (N)var1;
      }

      if (!this.k.d()) {
         this.k.i = (N)var6;
      }

      return (N)var1;
   }

   private void q() {
      DH var1 = this;
      boolean var2 = true;
      boolean var3 = false;
      boolean var4 = false;

      while (true) {
         DH var5 = var1.i;
         if (!var1.l) {
            if (var1.j.l || var1.k.l) {
               if (var1 == var5.j) {
                  if (!var1.j.l) {
                     var1.a();
                     var1 = var1.i;
                  }
               } else if (!var1.k.l) {
                  var1.m();
                  var1 = var1.i;
               }

               var1.l = var5.l;
               var5.l = false;
               if (var1 == var5.j) {
                  var1.j.l = false;
                  var2 = var5.m();
               } else {
                  var1.k.l = false;
                  var2 = var5.a();
               }

               var3 = var4;
               var4 = false;
               break;
            }

            var1.l = true;
            if (var5.l) {
               var5.l = false;
               break;
            }

            if (var2 && !var3) {
               var2 = var5.b();
            }

            DH var6 = var5.i;
            if (var5.i == null) {
               break;
            }

            if (var6.j == var5) {
               var1 = var6.k;
            } else {
               var1 = var6.j;
            }

            var3 = var4;
            var4 = false;
         } else {
            var5.l = true;
            var1.l = false;
            if (var5.j == var1) {
               var2 = var5.m();
               var1 = var5.j;
            } else {
               var2 = var5.a();
               var1 = var5.k;
            }

            var3 = true;
            var4 = true;
         }
      }

      DH var7 = var1.i;
      if (var2 && var7 != null) {
         if (!var3) {
            var2 = var7.b();
         }

         if (var2 && var7.i != null) {
            var7 = var7.i;
            if (!var4) {
               var2 = var7.b();
            }

            if (var2) {
               var7 = var7.i;

               while (var7 != null && var7.b()) {
                  var7 = var7.i;
               }
            }
         }
      }
   }

   public final void k() {
      if (this.d()) {
         throw new IllegalArgumentException("Attempted to remove a leaf node");
      }

      DH var1;
      if (!this.j.d() && !this.k.d()) {
         var1 = this.p();
      } else {
         var1 = null;
      }

      DH var2;
      if (!this.j.d()) {
         var2 = this.j;
      } else if (!this.k.d()) {
         var2 = this.k;
      } else {
         var2 = null;
      }

      if (var2 != null) {
         var2.i = this.i;
         if (this.i != null) {
            if (this.i.j == this) {
               this.i.j = (N)var2;
            } else {
               this.i.k = (N)var2;
            }
         }

         var2.l = false;
         if (var2.i != null) {
            var2 = var2.i;

            while (var2 != null && var2.b()) {
               var2 = var2.i;
            }
         }
      } else if (this.i != null) {
         var2 = this.j;
         DH var3 = this.i;
         if (this.i.j == this) {
            var3.j = (N)var2;
            var2 = var3.k;
         } else {
            var3.k = (N)var2;
            var2 = var3.j;
         }

         if (!this.l) {
            var2.q();
         } else {
            while (var3 != null && var3.b()) {
               var3 = var3.i;
            }
         }
      }

      if (var1 != null) {
         var1.b();
         var2 = var1.i;

         while (var2 != null && var2.b()) {
            var2 = var2.i;
         }
      }

      this.i = null;
      this.j = null;
      this.k = null;
      this.l = true;
   }

   public final N l() {
      if (this.d()) {
         throw new IllegalArgumentException("Attempted to remove a leaf node");
      }

      DH var1;
      if (this.i != null) {
         var1 = this.i;
      } else if (!this.j.d()) {
         var1 = this.j;
      } else {
         if (this.k.d()) {
            return this.j;
         }

         var1 = this.k;
      }

      this.k();
      return (N)var1.e();
   }

   private static <N extends DH<N>> N a(Iterator<? extends N> var0, int var1, int var2, N var3) {
      if (var1 == 0) {
         return (N)var3;
      }

      DH var4 = a(var0, (var1 - 1) / 2, var2 - 1, var3);
      DH var5 = (DH)var0.next();
      DH var6 = a(var0, var1 / 2, var2 - 1, var3);
      var5.l = var2 % 4 == 1;
      var5.j = (N)var4;
      var5.k = (N)var6;
      if (!var4.d()) {
         var4.i = (N)var5;
      }

      if (!var6.d()) {
         var6.i = (N)var5;
      }

      var5.b();
      return (N)var5;
   }

   public static <N extends DH<N>> N a(Collection<? extends N> var0, N var1) {
      int var2;
      if ((var2 = var0.size()) == 0) {
         return (N)var1;
      }

      int var3 = 0;

      for (int var4 = var2; var4 > 0; var4 /= 2) {
         var3++;
      }

      DH var5;
      (var5 = a(var0.iterator(), var2, var3, var1)).i = null;
      var5.l = false;
      return (N)var5;
   }

   private N a(N var1, N var2, int var3, int var4) {
      DH var5 = this;
      var1 = var1;
      DH var6;
      if (var3 <= var4) {
         var6 = null;

         for (int var7 = var4; var7 > var3; var1 = var1.j) {
            if (!var1.l) {
               var7--;
            }

            var6 = var1;
         }

         if (var1.l) {
            var6 = var1;
            var1 = var1.j;
         }
      } else {
         var6 = null;

         for (int var9 = var3; var9 > var4; var5 = var5.k) {
            if (!var5.l) {
               var9--;
            }

            var6 = var5;
         }

         if (var5.l) {
            var6 = var5;
            var5 = var5.k;
         }
      }

      var2.l = true;
      var2.i = (N)var6;
      if (var6 != null) {
         if (var3 < var4) {
            var6.j = (N)var2;
         } else {
            var6.k = (N)var2;
         }
      }

      var2.j = (N)var5;
      if (!var5.d()) {
         var5.i = (N)var2;
      }

      var2.k = (N)var1;
      if (!var1.d()) {
         var1.i = (N)var2;
      }

      return (N)var2.j();
   }

   private int r() {
      int var1 = 0;

      for (DH var2 = this; var2 != null; var2 = var2.k) {
         if (!var2.l) {
            var1++;
         }
      }

      return var1 - 1;
   }

   public final N a(N var1, N var2) {
      if (this.i == null && var1.i == null) {
         return this.a((N)var1, (N)var2, this.r(), var1.r());
      } else {
         throw new IllegalArgumentException("The node is not the root of a tree");
      }
   }

   public final N a(N var1) {
      if (this.i != null || var1.i != null) {
         throw new IllegalArgumentException("The node is not the root of a tree");
      }

      if (this.d()) {
         return (N)var1;
      }

      if (var1.d()) {
         return (N)this;
      }

      DH var2;
      DH var3 = (var2 = var1.f()).l();
      return this.a((N)var3, (N)var2);
   }

   public final N[] a(N var1) {
      if (this.i != null) {
         throw new IllegalArgumentException("This is not the root of a tree");
      }

      if (!this.d() && !var1.d()) {
         if (var1.e() != this) {
            throw new IllegalArgumentException("The split node does not belong to this tree");
         }

         DH var2;
         boolean var3;
         int var4;
         DH var5;
         int var6;
         DH var7;
         int var8;
         if (!var1.j.d()) {
            var2 = var1;
            var3 = false;
            var4 = var1.k.r();
            var5 = var1.j;
            var6 = var4;
            var5.i = null;
            if (var5.l) {
               var5.l = false;
               var6++;
            }

            var7 = var1.g().k;
            var8 = 0;
         } else {
            DH var9 = var1;

            while (var9.i != null && var9.i.j == var9) {
               var9 = var9.i;
            }

            var2 = var9.i;
            var3 = true;
            var4 = var9.r();
            var7 = var9;
            var8 = var4;
            var7.i = null;
            if (var7.l) {
               var7.l = false;
               var8++;
            }

            var5 = var1.j;
            var6 = 0;
         }

         while (var2 != null) {
            DH var14 = var2.i;
            boolean var13 = var2.i != null && var14.k == var2;
            int var10 = var4 + (var2.l ? 0 : 1);
            DH var11 = var3 ? var2.j : var2.k;
            if (var3 && var6 >= var4 && !var2.l) {
               if (var6 > var4) {
                  var5.l = true;
               }

               var2.i = null;
               var2.k = (N)var5;
               if (!var5.d()) {
                  var5.i = (N)var2;
               }

               var5 = var2;
               var2.b();
               var6 = var4 + 1;
            } else if (!var3 && var8 >= var4 && !var2.l) {
               if (var8 > var4) {
                  var7.l = true;
               }

               var2.i = null;
               var2.j = (N)var7;
               if (!var7.d()) {
                  var7.i = (N)var2;
               }

               var7 = var2;
               var2.b();
               var8 = var4 + 1;
            } else {
               var11.i = null;
               if (var11.l) {
                  var11.l = false;
                  var4++;
               }

               boolean var12 = !var11.d() && var11.j.l && var11.k.l;
               if (var3) {
                  var5 = var11.a((N)var5, (N)var2, var4, var6);
                  if (var6 < var4 && (!var12 || var5.j.l)) {
                     var6 = var4;
                  } else {
                     var6 = var4 + 1;
                  }
               } else {
                  var7 = var7.a((N)var11, (N)var2, var8, var4);
                  if (var8 < var4 && (!var12 || var7.j.l)) {
                     var8 = var4;
                  } else {
                     var8 = var4 + 1;
                  }
               }
            }

            var2 = var14;
            var3 = var13;
            var4 = var10;
         }

         DH[] var15;
         (var15 = (DH[])Array.newInstance(this.getClass(), 2))[0] = var5;
         var15[1] = var7;
         return (N[])var15;
      } else {
         throw new IllegalArgumentException("The root or the split node is a leaf");
      }
   }

   private int c(N var1) {
      if (!this.d() && !var1.d()) {
         int var2 = 0;
         DH var3 = this;
         int var4 = 0;
         var1 = var1;

         while (var3.i != null && var1.i != null) {
            do {
               var3 = var3.i;
               var2++;
               if (var3 == var1) {
                  return var2 - var4;
               }
            } while (var3.i != null && var2 < 2 * var4);

            while (var4 < 2 * var2) {
               var1 = var1.i;
               var4++;
               if (var1 == var3) {
                  return var2 - var4;
               }

               if (var1.i == null) {
                  break;
               }
            }
         }

         while (var3.i != null) {
            var3 = var3.i;
            var2++;
         }

         while (var1.i != null) {
            var1 = var1.i;
            var4++;
         }

         if (var3 != var1) {
            throw new IllegalArgumentException("The nodes do not belong to the same tree");
         } else {
            return var2 - var4;
         }
      } else {
         throw new IllegalArgumentException("One of the nodes is a leaf node");
      }
   }

   private N d(N var1) {
      int var2 = this.c((N)var1);
      DH var3 = this;
      var1 = var1;
      if (var2 >= 0) {
         for (int var4 = 0; var4 < var2; var4++) {
            var3 = var3.i;
         }
      } else {
         for (int var6 = 0; var6 < -var2; var6++) {
            var1 = var1.i;
         }
      }

      while (var3 != var1) {
         var3 = var3.i;
         var1 = var1.i;
      }

      return (N)var1;
   }

   public final int b(N var1) {
      if (this == var1) {
         return 0;
      }

      int var2 = this.c((N)var1);
      DH var3 = this;
      DH var4 = var1;
      if (var2 > 0) {
         for (int var5 = 0; var5 + 1 < var2; var5++) {
            var3 = var3.i;
         }

         if (var3.i == var1) {
            if (var1.j == var3) {
               return -1;
            }

            return 1;
         }

         var3 = var3.i;
      } else if (var2 < 0) {
         for (int var6 = 0; var6 + 1 < -var2; var6++) {
            var4 = var4.i;
         }

         if (var4.i == this) {
            if (this.j == var4) {
               return 1;
            }

            return -1;
         }

         var4 = var4.i;
      }

      while (var3.i != var4.i) {
         var3 = var3.i;
         var4 = var4.i;
      }

      return var3.i.j == var3 ? -1 : 1;
   }

   private void s() {
      if (this.j != null || this.k != null || this.i != null || this.l) {
         throw new RuntimeException("A leaf node's \"left\", \"right\", \"parent\", or isRed field is incorrect");
      }
   }

   private void a(int var1, Set<DI<N>> var2) {
      while (true) {
         DH var3 = this;
         if (this.j == null || this.k == null) {
            this.s();
            if (var1 != 1) {
               throw new RuntimeException("Not all root-to-leaf paths have the same number of black nodes");
            }

            return;
         }

         if (!var2.add(new DI<>(var3))) {
            throw new RuntimeException("The tree contains a repeated non-leaf node");
         }

         if (this.l) {
            if (!this.j.d() && this.j.l || !this.k.d() && this.k.l) {
               throw new RuntimeException("A red node has a red child");
            }

            var1 = var1;
         } else {
            if (var1 == 0) {
               throw new RuntimeException("Not all root-to-leaf paths have the same number of black nodes");
            }

            var1--;
         }

         if (!this.j.d() && this.j.i != this) {
            throw new RuntimeException("left.parent != this");
         }

         if (!this.k.d() && this.k.i != this) {
            throw new RuntimeException("right.parent != this");
         }

         var3 = this.j;
         DH var4 = this.k;
         var3.a(var1, var2);
         var1 = var1;
         this = var4;
      }
   }

   private void t() {
      while (true) {
         this.c();
         if (this.j == null) {
            return;
         }

         DH var1 = this.j;
         DH var2 = this.k;
         var1.t();
         this = var2;
      }
   }

   private void u() {
      if (this.d()) {
         this.s();
      } else {
         if (this.i == null && this.l) {
            throw new RuntimeException("The root is red");
         }

         HashSet var1 = new HashSet();
         int var2 = 0;

         for (DH var3 = this; var3 != null; var3 = var3.j) {
            if (!var1.add(new DI<>(var3))) {
               throw new RuntimeException("The tree contains a repeated non-leaf node");
            }

            if (!var3.l) {
               var2++;
            }
         }

         this.a(var2, new HashSet<>());
      }
   }

   private void v() {
      DH var1 = this;
      if (this.d()) {
         var1.s();
      } else {
         if (var1.i == null && var1.l) {
            throw new RuntimeException("The root is red");
         }

         HashSet var2 = new HashSet();
         int var3 = 0;

         for (DH var4 = var1; var4 != null; var4 = var4.j) {
            if (!var2.add(new DI<>(var4))) {
               throw new RuntimeException("The tree contains a repeated non-leaf node");
            }

            if (!var4.l) {
               var3++;
            }
         }

         var1.a(var3, new HashSet<>());
      }

      this.t();
   }

   private void a(Comparator<? super N> var1, N var2, N var3) {
      while (!this.d()) {
         DH var4 = this;
         if (var2 != null && var1.compare(var4, var2) < 0) {
            throw new RuntimeException("The nodes are not ordered correctly");
         }

         if (var3 != null && var1.compare(var4, var3) > 0) {
            throw new RuntimeException("The nodes are not ordered correctly");
         }

         DH var5 = this.j;
         DH var6 = this.k;
         var5.a(var1, (N)var2, (N)var4);
         var2 = var4;
         var1 = var1;
         this = var6;
      }
   }

   private void a(Comparator<? super N> var1) {
      if (var1 == null) {
         var1 = a;
         var1 = a;
      }

      this.a(var1, null, null);
   }

   // $VF: synthetic method
   @Override
   public int compareTo(Object var1) {
      return this.b((N)var1);
   }
}
