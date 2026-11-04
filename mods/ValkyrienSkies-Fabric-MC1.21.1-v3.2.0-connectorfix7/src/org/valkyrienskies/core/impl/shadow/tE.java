package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class tE<T extends kN<T>> implements Serializable, sT<T> {
   private static final long a = 7841233292190413362L;
   private final kM<T> b;
   private final Cn<T> c;
   private final int d;

   private tE(kM<T> var1) {
      this(var1, 0);
   }

   private tE(kM<T> var1, int var2) {
      this.b = var1;
      this.d = var2;
      this.c = new Cn<>(var1);
   }

   private tE(tE<T> var1, int var2) {
      this.b = var1.b;
      this.d = var1.d + var2;
      this.c = new Cn<>(var1.c);
   }

   private tE(kM<T> var1, int var2, int var3) {
      this.b = var1;
      this.d = var2;
      this.c = new Cn<>(var1, var3);
   }

   private tE(kM<T> var1, T[] var2) {
      Ci.a(var2);
      this.b = var1;
      this.d = var2.length;
      this.c = new Cn<>(var1);

      for (int var4 = 0; var4 < var2.length; var4++) {
         kN var3 = var2[var4];
         this.c.a(var4, (T)var3);
      }
   }

   private tE(tE<T> var1) {
      this.b = var1.b;
      this.d = var1.d;
      this.c = new Cn<>(var1.c);
   }

   private Cn<T> f() {
      return this.c;
   }

   private sT<T> a(tE<T> var1) {
      this.c(var1.d);
      tE var2 = (tE)this.b();
      Cn.a var5 = var1.c.a();

      while (var5.a()) {
         var5.d();
         int var3 = var5.b();
         kN var4 = var5.c();
         if (this.c.b(var3)) {
            var2.a(var3, this.c.a(var3).a((T)var4));
         } else {
            var2.a(var3, (T)var4);
         }
      }

      return var2;
   }

   private sT<T> b(tE<T> var1) {
      tE var2 = new tE<>(this, var1.d);
      Cn.a var3 = var1.c.a();

      while (var3.a()) {
         var3.d();
         var2.a(var3.b() + this.d, (T)var3.c());
      }

      return var2;
   }

   @Override
   public final sT<T> h(sT<T> var1) {
      if (var1 instanceof tE) {
         return this.b((tE<T>)var1);
      }

      int var2 = var1.g();
      tE var3 = new tE<>(this, var2);

      for (int var4 = 0; var4 < var2; var4++) {
         var3.a(var4 + this.d, (T)var1.a(var4));
      }

      return var3;
   }

   @Override
   public final sT<T> i(T var1) {
      Ci.a(var1);
      tE var2;
      (var2 = new tE<>(this, 1)).a(this.d, (T)var1);
      return var2;
   }

   @Override
   public final sT<T> b() {
      return new tE<>(this);
   }

   @Override
   public final T e(sT<T> var1) {
      this.c(var1.g());
      kN var2 = this.b.a();

      for (Cn.a var3 = this.c.a(); var3.a(); var2 = var2.a(var1.a(var3.b()).c(var3.c()))) {
         var3.d();
      }

      return (T)var2;
   }

   @Override
   public final sT<T> d(sT<T> var1) {
      this.c(var1.g());
      tE var2;
      Cn.a var3 = (var2 = new tE<>(this)).c.a();

      while (var3.a()) {
         var3.d();
         var2.a(var3.b(), (T)var3.c().d(var1.a(var3.b())));
      }

      return var2;
   }

   @Override
   public final sT<T> c(sT<T> var1) {
      this.c(var1.g());
      tE var2;
      Cn.a var3 = (var2 = new tE<>(this)).c.a();

      while (var3.a()) {
         var3.d();
         var2.a(var3.b(), (T)var3.c().c(var1.a(var3.b())));
      }

      return var2;
   }

   @Deprecated
   @Override
   public final T[] e() {
      return this.h();
   }

   @Override
   public final int g() {
      return this.d;
   }

   @Override
   public final T a(int var1) {
      this.b(var1);
      return this.c.a(var1);
   }

   @Override
   public final kM<T> a() {
      return this.b;
   }

   @Override
   public final sT<T> a(int var1, int var2) {
      if (var2 < 0) {
         throw new pj(px.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, var2);
      }

      this.b(var1);
      this.b(var1 + var2 - 1);
      tE var3 = new tE<>(this.b, var2);
      var2 = var1 + var2;
      Cn.a var4 = this.c.a();

      while (var4.a()) {
         var4.d();
         int var5;
         if ((var5 = var4.b()) >= var1 && var5 < var2) {
            var3.a(var5 - var1, (T)var4.c());
         }
      }

      return var3;
   }

   @Override
   public final sT<T> a(T var1) {
      return this.b().b((T)var1);
   }

   @Override
   public final sT<T> b(T var1) {
      for (int var2 = 0; var2 < this.d; var2++) {
         this.a(var2, this.a(var2).a((T)var1));
      }

      return this;
   }

   @Override
   public final sT<T> g(T var1) {
      return this.b().h((T)var1);
   }

   @Override
   public final sT<T> h(T var1) {
      Cn.a var2 = this.c.a();

      while (var2.a()) {
         var2.d();
         this.c.a(var2.b(), (T)var2.c().d(var1));
      }

      return this;
   }

   @Override
   public final sT<T> c() {
      return this.b().d();
   }

   @Override
   public final sT<T> d() {
      for (int var1 = 0; var1 < this.d; var1++) {
         this.a(var1, this.b.b().d(this.a(var1)));
      }

      return this;
   }

   @Override
   public final sT<T> e(T var1) {
      return this.b().f((T)var1);
   }

   @Override
   public final sT<T> f(T var1) {
      Cn.a var2 = this.c.a();

      while (var2.a()) {
         var2.d();
         this.c.a(var2.b(), (T)var2.c().c(var1));
      }

      return this;
   }

   @Override
   public final sT<T> c(T var1) {
      return this.b().d((T)var1);
   }

   @Override
   public final sT<T> d(T var1) {
      return this.b(this.b.a().b((T)var1));
   }

   private sQ<T> c(tE<T> var1) {
      int var2 = var1.d;
      tD var5 = new tD<>(this.b, this.d, var2);
      Cn.a var3 = this.c.a();

      while (var3.a()) {
         var3.d();
         Cn.a var4 = var1.c.a();

         while (var4.a()) {
            var4.d();
            var5.a(var3.b(), var4.b(), (T)var3.c().c(var4.c()));
         }
      }

      return var5;
   }

   @Override
   public final sQ<T> g(sT<T> var1) {
      if (var1 instanceof tE) {
         return this.c((tE<T>)var1);
      }

      int var2 = var1.g();
      tD var3 = new tD<>(this.b, this.d, var2);
      Cn.a var4 = this.c.a();

      while (var4.a()) {
         var4.d();
         int var5 = var4.b();
         kN var6 = var4.c();

         for (int var7 = 0; var7 < var2; var7++) {
            var3.a(var5, var7, (T)var6.c(var1.a(var7)));
         }
      }

      return var3;
   }

   @Override
   public final sT<T> f(sT<T> var1) {
      this.c(var1.g());
      return var1.e(this.e(var1).d((T)var1.e(var1)));
   }

   @Override
   public final void j(T var1) {
      Ci.a(var1);

      for (int var2 = 0; var2 < this.d; var2++) {
         this.a(var2, (T)var1);
      }
   }

   @Override
   public final void a(int var1, T var2) {
      Ci.a(var2);
      this.b(var1);
      this.c.a(var1, (T)var2);
   }

   @Override
   public final void a(int var1, sT<T> var2) {
      this.b(var1);
      this.b(var1 + var2.g() - 1);
      int var3 = var2.g();

      for (int var4 = 0; var4 < var3; var4++) {
         this.a(var4 + var1, (T)var2.a(var4));
      }
   }

   private tE<T> d(tE<T> var1) {
      this.c(var1.d);
      tE var2 = (tE)this.b();
      Cn.a var4 = var1.c.a();

      while (var4.a()) {
         var4.d();
         int var3 = var4.b();
         if (this.c.b(var3)) {
            var2.a(var3, this.c.a(var3).b((T)var4.c()));
         } else {
            var2.a(var3, this.b.a().b((T)var4.c()));
         }
      }

      return var2;
   }

   @Override
   public final sT<T> b(sT<T> var1) {
      if (var1 instanceof tE) {
         return this.d((tE<T>)var1);
      }

      int var2 = var1.g();
      this.c(var2);
      tE var3 = new tE<>(this);

      for (int var4 = 0; var4 < var2; var4++) {
         if (this.c.b(var4)) {
            var3.a(var4, this.c.a(var4).b((T)var1.a(var4)));
         } else {
            var3.a(var4, this.b.a().b((T)var1.a(var4)));
         }
      }

      return var3;
   }

   @Override
   public final T[] h() {
      kN[] var1 = Ch.a(this.b, this.d);

      for (Cn.a var2 = this.c.a(); var2.a(); var1[var2.b()] = var2.c()) {
         var2.d();
      }

      return (T[])var1;
   }

   private void b(int var1) {
      if (var1 < 0 || var1 >= this.d) {
         throw new po(var1, 0, this.d - 1);
      }
   }

   private void b(int var1, int var2) {
      int var3 = this.d;
      if (var1 < 0 || var1 >= var3) {
         throw new po(px.INDEX, var1, 0, var3 - 1);
      }

      if (var2 < 0 || var2 >= var3) {
         throw new po(px.INDEX, var2, 0, var3 - 1);
      }

      if (var2 < var1) {
         throw new pn(px.INITIAL_ROW_AFTER_FINAL_ROW, var2, var1, false);
      }
   }

   private void c(int var1) {
      if (this.d != var1) {
         throw new oS(this.d, var1);
      }
   }

   @Override
   public final sT<T> a(sT<T> var1) {
      if (var1 instanceof tE) {
         return this.a((tE<T>)var1);
      }

      int var2 = var1.g();
      this.c(var2);
      tE var3 = new tE<>(this.b, this.d);

      for (int var4 = 0; var4 < var2; var4++) {
         var3.a(var4, (T)var1.a(var4).a(this.a(var4)));
      }

      return var3;
   }

   private T a(sV<T> var1) {
      int var2 = this.d;

      for (int var3 = 0; var3 < var2; var3++) {
         this.a(var3);
      }

      return (T)var1.c();
   }

   private T a(sV<T> var1, int var2, int var3) {
      this.b(var2, var3);

      for (int var5 = var2; var5 <= var3; var5++) {
         this.a(var5);
      }

      return (T)var1.c();
   }

   private T b(sV<T> var1) {
      sV var2 = var1;
      tE var5 = this;
      int var3 = this.d;

      for (int var4 = 0; var4 < var3; var4++) {
         var5.a(var4);
      }

      return (T)var2.c();
   }

   private T b(sV<T> var1, int var2, int var3) {
      int var4 = var3;
      var3 = var2;
      sV var7 = var1;
      tE var6 = this;
      this.b(var3, var4);

      for (int var9 = var3; var9 <= var4; var9++) {
         var6.a(var9);
      }

      return (T)var7.c();
   }

   private T a(sU<T> var1) {
      int var2 = this.d;

      for (int var3 = 0; var3 < var2; var3++) {
         this.a(var3);
         this.a(var3, (T)var1.b());
      }

      return (T)var1.c();
   }

   private T a(sU<T> var1, int var2, int var3) {
      this.b(var2, var3);

      for (int var5 = var2; var5 <= var3; var5++) {
         this.a(var5);
         this.a(var5, (T)var1.b());
      }

      return (T)var1.c();
   }

   private T b(sU<T> var1) {
      return this.a(var1);
   }

   private T b(sU<T> var1, int var2, int var3) {
      return this.a(var1, var2, var3);
   }

   @Override
   public final int hashCode() {
      int var1 = 31 + (this.b == null ? 0 : this.b.hashCode());
      var1 = var1 * 31 + this.d;
      Cn.a var2 = this.c.a();

      while (var2.a()) {
         var2.d();
         int var3 = var2.c().hashCode();
         var1 = var1 * 31 + var3;
      }

      return var1;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof tE)) {
         return false;
      }

      obj = (tE)obj;
      if (this.b == null) {
         if (((tE)obj).b != null) {
            return false;
         }
      } else if (!this.b.equals(((tE)obj).b)) {
         return false;
      }

      if (this.d != ((tE)obj).d) {
         return false;
      }

      Cn.a var2 = this.c.a();

      while (var2.a()) {
         var2.d();
         if (!((tE)obj).a(var2.b()).equals(var2.c())) {
            return false;
         }
      }

      var2 = ((tE)obj).c.a();

      while (var2.a()) {
         var2.d();
         if (!var2.c().equals(this.a(var2.b()))) {
            return false;
         }
      }

      return true;
   }
}
