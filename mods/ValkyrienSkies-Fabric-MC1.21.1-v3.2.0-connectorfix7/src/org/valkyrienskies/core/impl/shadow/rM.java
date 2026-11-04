package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class rM<S extends qN> {
   public sb<S> a;
   public rM<S> b;
   public rM<S> c;
   public rM<S> d;
   public Object e;

   public rM() {
      this.a = null;
      this.b = null;
      this.c = null;
      this.d = null;
      this.e = null;
   }

   public rM(Object var1) {
      this.a = null;
      this.b = null;
      this.c = null;
      this.d = null;
      this.e = var1;
   }

   public rM(sb<S> var1, rM<S> var2, rM<S> var3, Object var4) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = null;
      this.e = var4;
      var2.d = this;
      var3.d = this;
   }

   public final boolean a(rV<S> var1) {
      if (this.a != null) {
         this.b.d = null;
         this.c.d = null;
      }

      sb var2;
      if ((var2 = this.b(var1.f())) != null && !var2.b()) {
         this.a = var2;
         this.b = new rM<>();
         this.b.d = this;
         this.c = new rM<>();
         this.c.d = this;
         return true;
      } else {
         this.a = null;
         this.b = null;
         this.c = null;
         return false;
      }
   }

   public final rM<S> a() {
      return this.a == null ? new rM<>(this.e) : new rM<>(this.a.e(), this.b.a(), this.c.a(), this.e);
   }

   public final sb<S> b() {
      return this.a;
   }

   public final rM<S> c() {
      return this.b;
   }

   public final rM<S> d() {
      return this.c;
   }

   public final rM<S> e() {
      return this.d;
   }

   public final void a(Object var1) {
      this.e = var1;
   }

   public final Object f() {
      return this.e;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void a(rN<S> var1) {
      if (this.a == null) {
         var1.c(this);
      } else {
         switch (<unrepresentable>.a[var1.a(this) - 1]) {
            case 1:
               this.b.a(var1);
               this.c.a(var1);
               var1.b(this);
               return;
            case 2:
               this.b.a(var1);
               var1.b(this);
               this.c.a(var1);
               return;
            case 3:
               this.c.a(var1);
               this.b.a(var1);
               var1.b(this);
               return;
            case 4:
               this.c.a(var1);
               var1.b(this);
               this.b.a(var1);
               return;
            case 5:
               var1.b(this);
               this.b.a(var1);
               this.c.a(var1);
               return;
            case 6:
               var1.b(this);
               this.c.a(var1);
               this.b.a(var1);
               return;
            default:
               throw new oY();
         }
      }
   }

   private sb<S> b(sb<S> var1) {
      var1 = var1;

      for (rM var2 = this; var2.d != null && var1 != null; var2 = var2.d) {
         if (var2 == var2.d.b) {
            var1 = var1.a(var2.d.a.c()).a;
         } else {
            var1 = var1.a(var2.d.a.c()).b;
         }
      }

      return var1;
   }

   @Deprecated
   private rM<S> a(qO<S> var1) {
      return this.a(var1, 1.0E-10);
   }

   public final rM<S> a(qM<S> var1, double var2) {
      while (this.a != null) {
         double var4;
         if (BY.w(var4 = this.a.c().a(var1)) < var2) {
            return this;
         }

         if (var4 <= 0.0) {
            var1 = var1;
            this = this.c;
         } else {
            var1 = var1;
            this = this.b;
         }
      }

      return this;
   }

   public final List<rM<S>> a(qM<S> var1, double var2) {
      ArrayList var4 = new ArrayList();
      this.a(var1, var2, var4);
      return var4;
   }

   public void a(qM<S> var1, double var2, List<rM<S>> var4) {
      while (this.a != null) {
         double var5;
         if ((var5 = this.a.c().a(var1)) < -var2) {
            this.c.a(var1, var2, var4);
            return;
         }

         if (var5 > var2) {
            this.b.a(var1, var2, var4);
            return;
         }

         var4.add(this);
         this.c.a(var1, var2, var4);
         var2 = var2;
         var1 = var1;
         this = this.b;
      }
   }

   private void g() {
      if (this.a != null && this.b.a == null && this.c.a == null && (this.b.e == null && this.c.e == null || this.b.e != null && this.b.e.equals(this.c.e))) {
         this.e = this.b.e == null ? this.c.e : this.b.e;
         this.a = null;
         this.b = null;
         this.c = null;
      }
   }

   public final rM<S> a(rM<S> var1, rM.a<S> var2) {
      return this.a(var1, var2, null, false);
   }

   private rM<S> a(rM<S> var1, rM.a<S> var2, rM<S> var3, boolean var4) {
      if (this.a == null) {
         return var2.a(this, var1, var3, var4, true);
      }

      if (var1.a == null) {
         return var2.a(var1, this, var3, var4, false);
      }

      var1 = var1.a(this.a);
      if (var3 != null) {
         var1.d = var3;
         if (var4) {
            var3.b = var1;
         } else {
            var3.c = var1;
         }
      }

      this.b.a(var1.b, var2, var1, true);
      this.c.a(var1.c, var2, var1, false);
      var1.g();
      if (var1.a != null) {
         var1.a = var1.b(var1.a.c().f());
      }

      return var1;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final rM<S> a(sb<S> var1) {
      if (this.a == null) {
         return new rM<>(var1, this.a(), new rM<>(this.e), null);
      }

      rV var2 = this.a.c();
      rV var3 = var1.c();
      sb.a var4 = var1.a(var2);
      switch (<unrepresentable>.b[var4.c() - 1]) {
         case 1:
            rM var9 = this.b.a(var1);
            if (this.a.a(var3).c() == sa.PLUS$7c70dd6b) {
               var9.b = new rM<>(this.a.e(), var9.b, this.c.a(), this.e);
               var9.b.g();
               var9.b.d = var9;
            } else {
               var9.c = new rM<>(this.a.e(), var9.c, this.c.a(), this.e);
               var9.c.g();
               var9.c.d = var9;
            }

            return var9;
         case 2:
            rM var8 = this.c.a(var1);
            if (this.a.a(var3).c() == sa.PLUS$7c70dd6b) {
               var8.b = new rM<>(this.a.e(), this.b.a(), var8.b, this.e);
               var8.b.g();
               var8.b.d = var8;
            } else {
               var8.c = new rM<>(this.a.e(), this.b.a(), var8.c, this.e);
               var8.c.g();
               var8.c.d = var8;
            }

            return var8;
         case 3:
            sb.a var6 = this.a.a(var3);
            rM var5;
            (var5 = new rM<>(var1, this.b.a(var4.a), this.c.a(var4.b), null)).b.a = var6.a;
            var5.c.a = var6.b;
            rM var7 = var5.b.c;
            var5.b.c = var5.c.b;
            var5.b.c.d = var5.b;
            var5.c.b = var7;
            var5.c.b.d = var5.c;
            var5.b.g();
            var5.c.g();
            return var5;
         default:
            return var2.a(var3) ? new rM<>(var1, this.b.a(), this.c.a(), this.e) : new rM<>(var1, this.c.a(), this.b.a(), this.e);
      }
   }

   @Deprecated
   private void a(rM<S> var1, boolean var2) {
      this.a(var1, var2, new rM.b<S>() {
         @Override
         public final rM<S> a(rM<S> var1) {
            throw new oX(px.NULL_NOT_ALLOWED);
         }
      });
   }

   public final void a(rM<S> var1, boolean var2, rM.b<S> var3) {
      this.d = var1;
      if (var1 != null) {
         if (var2) {
            var1.b = this;
         } else {
            var1.c = this;
         }
      }

      if (this.a != null) {
         for (rM var5 = this; var5.d != null; var5 = var5.d) {
            rV var6 = var5.d.a.c();
            if (var5 == var5.d.b) {
               this.a = this.a.a(var6).a;
               this.b.a(var6, var3);
               this.c.a(var6, var3);
            } else {
               this.a = this.a.a(var6).b;
               this.b.b(var6, var3);
               this.c.b(var6, var3);
            }

            if (this.a == null) {
               rM var7 = var3.a(this);
               this.a = var7.a;
               this.b = var7.b;
               this.c = var7.c;
               this.e = var7.e;
               if (this.a == null) {
                  break;
               }
            }
         }

         this.g();
      }
   }

   public final rM<S> a(Object var1, Object var2, Object var3) {
      var1 = new rM(var1);

      for (rM var7 = this; var7.d != null; var7 = var7.d) {
         rL var4 = var7.d.a.e();
         rM var5 = new rM(var2);
         if (var7 == var7.d.b) {
            var1 = new rM<>(var4, var1, var5, null);
         } else {
            var1 = new rM<>(var4, var5, var1, null);
         }
      }

      return var1;
   }

   private void a(rV<S> var1, rM.b<S> var2) {
      if (this.a != null) {
         this.a = this.a.a(var1).a;
         this.b.a(var1, var2);
         this.c.a(var1, var2);
         if (this.a == null) {
            rM var4 = var2.a(this);
            this.a = var4.a;
            this.b = var4.b;
            this.c = var4.c;
            this.e = var4.e;
         }
      }
   }

   private void b(rV<S> var1, rM.b<S> var2) {
      if (this.a != null) {
         this.a = this.a.a(var1).b;
         this.b.b(var1, var2);
         this.c.b(var1, var2);
         if (this.a == null) {
            rM var4 = var2.a(this);
            this.a = var4.a;
            this.b = var4.b;
            this.c = var4.c;
            this.e = var4.e;
         }
      }
   }

   public interface a<S extends qN> {
      rM<S> a(rM<S> var1, rM<S> var2, rM<S> var3, boolean var4, boolean var5);
   }

   public interface b<S extends qN> {
      rM<S> a(rM<S> var1);
   }
}
