package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ki<T> {
   public final List<kh<T>> a = new ArrayList<>();
   private int b = 0;
   private int c = 0;

   public final void a(kk<T> var1) {
      this.a.add(var1);
      this.b++;
   }

   public final void a(kj<T> var1) {
      this.a.add(var1);
      this.c++;
   }

   public final void a(kg<T> var1) {
      this.a.add(var1);
      this.c++;
   }

   public final void a(kf<T> var1) {
      Iterator var2 = this.a.iterator();

      while (var2.hasNext()) {
         ((kh)var2.next()).a(var1);
      }
   }

   private int a() {
      return this.b;
   }

   private int b() {
      return this.c;
   }
}
