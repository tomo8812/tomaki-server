package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Collections;

public abstract class wZ extends xp {
   private wY b;
   private Collection<wW> c;
   private boolean d;

   protected wZ() {
      super(null);
   }

   protected final boolean b() {
      return this.d;
   }

   protected final wY d() {
      return this.b;
   }

   protected final Collection<wW> m() {
      return Collections.unmodifiableCollection(this.c);
   }

   @Override
   public wQ c(wO... var1) {
      return super.c(var1);
   }

   @Override
   public void b(wO... var1) {
      super.b(var1);
      wO[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var5[var3]) instanceof wY) {
            this.b = (wY)var4;
         } else if (var4 instanceof wX) {
            wX var6;
            this.c = Collections.unmodifiableSet((var6 = (wX)var4).a);
         } else if (var4 instanceof xb) {
            xb var7;
            this.d = (var7 = (xb)var4).a;
         }
      }
   }

   // $VF: synthetic method
   @Override
   public Object a(wO[] var1) {
      return this.c(var1);
   }
}
