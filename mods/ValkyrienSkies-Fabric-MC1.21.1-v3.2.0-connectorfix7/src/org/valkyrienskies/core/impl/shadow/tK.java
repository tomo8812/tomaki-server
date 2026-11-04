package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class tK<T extends tL> implements Serializable {
   private static final long a = -3442297081515880464L;
   public final List<T> b = new ArrayList<>();

   public final void a(T var1) {
      this.b.add((T)var1);
   }

   public final List<T> b() {
      return this.b;
   }
}
