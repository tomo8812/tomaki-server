package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;

public final class Gn<S> implements Gu<S> {
   private final List<S> b;

   public Gn(S... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      if (var1.length == 0) {
         String var2 = "Failed requirement.";
         throw new IllegalArgumentException(var2.toString());
      }

      this.b = ArraysKt.asList(var1);
   }

   @Override
   public final String a(List<? extends S> var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      if (!fd.b(var1, this.b)) {
         return "Required exact stages " + this.b;
      } else {
         return var2 && !fd.c(var1, this.b) ? "Required exact stages " + this.b : null;
      }
   }
}
