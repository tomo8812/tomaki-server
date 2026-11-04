package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class st {
   final sp a;
   sm b;
   sm c;
   final List<sl> d;

   st(sp var1) {
      this.a = var1;
      this.b = null;
      this.c = null;
      this.d = new ArrayList<>();
   }

   public final sp a() {
      return this.a;
   }

   final void a(sl var1) {
      this.d.add(var1);
   }

   final sl a(st var1) {
      for (sl var3 : this.d) {
         for (sl var5 : var1.d) {
            if (var3 == var5) {
               return var3;
            }
         }
      }

      return null;
   }

   final void a(sm var1) {
      this.b = var1;
      this.a(var1.d);
   }

   public final sm b() {
      return this.b;
   }

   final void b(sm var1) {
      this.c = var1;
      this.a(var1.d);
   }

   public final sm c() {
      return this.c;
   }
}
