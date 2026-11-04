package org.valkyrienskies.core.impl.shadow;

import java.util.Collections;
import java.util.Set;

public class cq extends RuntimeException {
   private static final long a = -7805792737596582110L;
   private dg b;

   public cq(dg var1) {
      super(var1.f());
      this.b = var1;
   }

   public cq(String var1) {
      super(var1);
   }

   public cq(Throwable var1) {
      super(var1);
   }

   public Set<dg> a() {
      return this.b == null ? Collections.emptySet() : Collections.singleton(this.b);
   }
}
