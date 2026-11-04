package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

public final class Go<S> implements Gu<S> {
   private final List<S> b;

   public Go(List<? extends S> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.b = var1;
      if (this.b.isEmpty()) {
         String var2 = "Failed requirement.";
         throw new IllegalArgumentException(var2.toString());
      }
   }

   @Override
   public final String a(List<? extends S> var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      return var2 && !Intrinsics.areEqual(var1.subList(var1.size() - this.b.size(), var1.size()), this.b) ? "Required final stages to be " + this.b : null;
   }
}
