package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public final class Ab<T extends Ac<T>> implements Serializable {
   private static final long c = -3442297081515880464L;
   final List<T> a;
   final T b;

   public Ab(T var1) {
      this.b = (T)var1;
      this.a = new ArrayList<>();
   }

   public final void a(T var1) {
      this.a.add((T)var1);
   }

   public final List<T> a() {
      return this.a;
   }

   public final T b() {
      return this.b;
   }
}
