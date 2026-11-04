package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class wX implements wO {
   final Set<wW> a = new LinkedHashSet<>();

   private wX(wW... var1) {
      wW[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wW var4 = var5[var3];
         this.a.add(var4);
      }
   }

   private wX(Collection<wW> var1) {
      this.a.addAll(var1);
   }

   public final Collection<wW> a() {
      return Collections.unmodifiableSet(this.a);
   }
}
