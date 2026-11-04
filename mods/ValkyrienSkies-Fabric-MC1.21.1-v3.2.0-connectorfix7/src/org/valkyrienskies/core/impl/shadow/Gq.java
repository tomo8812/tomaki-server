package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

public final class Gq<S> implements Gu<S> {
   private final List<S> b;

   public Gq(List<? extends S> var1) {
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
      var2 = 0;

      for (Object var3 : var1) {
         if (this.b.contains(var3) && !Intrinsics.areEqual(var3, this.b.get(var2)) && var2 + 1 < this.b.size()) {
            if (!Intrinsics.areEqual(var3, this.b.get(++var2))) {
               return "Required stages in the following order: " + this.b;
            }
         }
      }

      return null;
   }
}
