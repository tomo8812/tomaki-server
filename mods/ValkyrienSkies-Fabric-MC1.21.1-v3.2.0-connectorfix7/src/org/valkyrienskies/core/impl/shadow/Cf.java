package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Cf {
   public Cc.a a;
   private final Collection<Ce> b;

   public Cf(int var1) {
      this.a = Cc.a.a().b(var1);
      this.b = new CopyOnWriteArrayList<>();
   }

   @Deprecated
   private Cf(int var1, final Cb.a var2) {
      this(var1, new Cc.a.a() {
         @Override
         public final void a(int var1) {
            var2.a(var1);
         }
      });
   }

   private Cf(int var1, <unrepresentable> var2) {
      Cc.a var10001 = Cc.a.a().b(var1);
      var2 = var2;
      Cc.a var3 = var10001;
      this.a = new Cc.a(var3.a, var3.b, var3.c, var2);
      this.b = new CopyOnWriteArrayList<>();
   }

   private void a(Ce var1) {
      this.b.add(var1);
   }

   public final void a(sJ var1) {
      for (Ce var10000 : this.b) {
         ;
      }
   }

   public final void b(sJ var1) {
      for (Ce var10000 : this.b) {
         ;
      }
   }

   public final void c(sJ var1) {
      for (Ce var10000 : this.b) {
         ;
      }
   }

   public final void d(sJ var1) {
      for (Ce var10000 : this.b) {
         ;
      }
   }

   public final int a() {
      Cc.a var1 = this.a;
      return this.a.e;
   }

   private int d() {
      Cc.a var1 = this.a;
      return this.a.b;
   }

   public final void b() {
      Cc.a var1 = this.a;
      this.a.d(1);
   }

   private void b(Ce var1) {
      this.b.remove(var1);
   }

   public final void c() {
      this.a = this.a.a(0);
   }
}
