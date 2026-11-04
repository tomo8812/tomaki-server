package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class rX<S extends qN> implements Iterable<rM<S>> {
   private List<rM<S>> a = new ArrayList<>();

   public final void a(rM<S> var1) {
      for (rM var3 : this.a) {
         if (var1 == var3) {
            return;
         }
      }

      this.a.add(var1);
   }

   public final void a(Iterable<rM<S>> var1) {
      for (rM var2 : var1) {
         this.a(var2);
      }
   }

   @Override
   public final Iterator<rM<S>> iterator() {
      return this.a.iterator();
   }
}
