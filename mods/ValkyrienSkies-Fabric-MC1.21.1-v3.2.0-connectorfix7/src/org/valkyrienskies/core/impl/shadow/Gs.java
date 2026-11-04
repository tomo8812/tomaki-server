package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

public final class Gs<S> implements Gu<S> {
   private final List<S> b;

   public Gs(S... var1) {
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
      return var2 && !var1.containsAll(this.b)
         ? "Required all stages " + this.b + " - missing " + CollectionsKt.minus(this.b, CollectionsKt.toSet(var1)) + ")"
         : null;
   }
}
