package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Collections;

@Deprecated
public abstract class yW implements yZ {
   private static int a;
   private yY b;
   private Collection<yX> c;
   private yf d;
   private boolean e;
   private int f;
   private int g;

   protected yW() {
      byte var1 = 100;
      this.f = 100;
   }

   protected final boolean a() {
      return this.e;
   }

   protected final yf b() {
      return this.d;
   }

   protected final yY c() {
      return this.b;
   }

   protected final Collection<yX> d() {
      return Collections.unmodifiableCollection(this.c);
   }

   @Override
   public final void a(int var1) {
      this.f = 100;
   }

   @Override
   public final int e() {
      return this.f;
   }

   @Override
   public final int f() {
      return this.g;
   }

   protected final void g() {
      if (++this.g > this.f) {
         throw new pc(this.f);
      }
   }

   @Override
   public final yp a(yY var1, Collection<yX> var2, yf var3, boolean var4) {
      this.b = var1;
      this.c = var2;
      this.d = var3;
      this.e = var4;
      this.g = 0;
      return this.h();
   }

   protected abstract yp h();
}
