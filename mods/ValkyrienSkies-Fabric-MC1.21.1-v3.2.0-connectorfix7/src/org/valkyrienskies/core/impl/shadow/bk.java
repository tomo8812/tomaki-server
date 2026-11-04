package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayDeque;
import java.util.Deque;

public final class bk implements aD, av {
   private final ThreadLocal<Deque<ResolvedType>> a = ThreadLocal.withInitial(ArrayDeque::new);

   @Override
   public final void a(aL var1) {
      var1.b.a(this);
   }

   @Override
   public final at a(ResolvedType var1, aI var2) {
      Deque var3;
      if ((var3 = this.a.get()).contains(var1)) {
         throw new IllegalArgumentException("Option.INLINE_ALL_SCHEMAS cannot be fulfilled due to a circular reference to " + var2.b().f(var1));
      }

      var2.b();
      if (aU.c(var1)) {
         return null;
      }

      var3.addLast(var1);
      ObjectNode var4 = var2.a(var1, this);
      var3.removeLast();
      return new at(var4, true);
   }
}
