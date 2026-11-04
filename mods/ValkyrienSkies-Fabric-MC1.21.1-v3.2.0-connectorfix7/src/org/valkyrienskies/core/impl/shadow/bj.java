package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import java.util.Collections;
import java.util.List;

public class bj<W> implements aD {
   private final Class<W> a;

   public bj(Class<W> var1) {
      this.a = var1;
   }

   private boolean a(ResolvedType var1) {
      return this.a.isAssignableFrom(var1.getErasedType());
   }

   protected final boolean a(aB<?, ?> var1) {
      ResolvedType var2 = var1.a.getType();
      if (var1.c && aU.c(var2)) {
         var2 = var1.e.d(var2);
      }

      return this.a(var2);
   }

   private List<ResolvedType> b(aB<?, ?> var1) {
      return this.a(var1.d) ? Collections.singletonList(var1.a(this.a, 0)) : null;
   }

   @Override
   public void a(aL var1) {
      var1.c.b(this::b);
      var1.d.b(this::b);
   }
}
