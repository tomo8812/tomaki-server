package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public final class iT<E> extends iM<E> implements Serializable {
   private static final long d = 6897789178562232073L;
   private static final int e = 20;
   private transient iM.d<E> f;
   private transient int g;
   private int h = 20;

   public iT() {
      this(20);
   }

   private iT(Collection<? extends E> var1) {
      super(var1);
   }

   private iT(int var1) {
      this.a();
   }

   private int c() {
      return this.h;
   }

   private void a(int var1) {
      this.h = var1;
      iT var2 = this;

      while (var2.g > var2.h) {
         var2.e();
      }
   }

   private void d() {
      while (this.g > this.h) {
         this.e();
      }
   }

   private iM.d<E> e() {
      if (this.g == 0) {
         return null;
      }

      iM.d var1 = this.f;
      this.f = var1.b;
      var1.b = null;
      this.g--;
      return var1;
   }

   private boolean f() {
      return this.g >= this.h;
   }

   private void b(iM.d<E> var1) {
      iT var2 = this;
      if (this.g < var2.h) {
         iM.d var3 = this.f;
         var1.a = null;
         var1.b = var3;
         var1.a(null);
         this.f = var1;
         this.g++;
      }
   }

   @Override
   protected final iM.d<E> a(E var1) {
      iM.d var2;
      if ((var2 = this.e()) == null) {
         return super.a((E)var1);
      }

      var2.a(var1);
      return var2;
   }

   @Override
   protected final void a(iM.d<E> var1) {
      super.a(var1);
      this.b(var1);
   }

   @Override
   protected final void b() {
      int var1 = Math.min(super.b, this.h - this.g);
      iM.d var2 = super.a.b;

      for (int var3 = 0; var3 < var1; var3++) {
         iM.d var4 = var2;
         var2 = var2.b;
         this.b(var4);
      }

      super.b();
   }

   private void b(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      this.a(var1);
   }

   private void b(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a(var1);
   }
}
