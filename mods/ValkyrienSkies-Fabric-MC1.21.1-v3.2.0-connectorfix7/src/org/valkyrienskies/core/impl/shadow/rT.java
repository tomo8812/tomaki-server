package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

final class rT<S extends qN> {
   sb<S> a = null;
   sb<S> b = null;
   final rX<S> c = new rX<>();
   final rX<S> d = new rX<>();

   rT(rM<S> var1, sb<S> var2) {
      this.a(var1, var2, new ArrayList<>());
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void a(rM<S> var1, sb<S> var2, List<rM<S>> var3) {
      if (var1.a == null) {
         if ((Boolean)var1.e) {
            this.b(var2, var3);
         } else {
            this.a(var2, var3);
         }
      } else {
         rV var4 = var1.a.c();
         sb.a var6 = var2.a(var4);
         switch (<unrepresentable>.a[var6.c() - 1]) {
            case 1:
               this.a(var1.b, var2, var3);
               return;
            case 2:
               this.a(var1.c, var2, var3);
               return;
            case 3:
               var3.add(var1);
               this.a(var1.b, var6.a, var3);
               this.a(var1.c, var6.b, var3);
               var3.remove(var3.size() - 1);
               return;
            default:
               throw new oY();
         }
      }
   }

   private void a(sb<S> var1, List<rM<S>> var2) {
      if (this.a == null) {
         this.a = var1;
      } else {
         this.a = this.a.a(var1);
      }

      this.c.a(var2);
   }

   private void b(sb<S> var1, List<rM<S>> var2) {
      if (this.b == null) {
         this.b = var1;
      } else {
         this.b = this.b.a(var1);
      }

      this.d.a(var2);
   }

   public final boolean a() {
      return this.a != null && !this.a.b();
   }

   public final sb<S> b() {
      return this.a;
   }

   public final rX<S> c() {
      return this.c;
   }

   public final boolean d() {
      return this.b != null && !this.b.b();
   }

   public final sb<S> e() {
      return this.b;
   }

   public final rX<S> f() {
      return this.d;
   }
}
