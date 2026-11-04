package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

public final class Gh<S> {
   public final ArrayList<Gx<S>> a = new ArrayList<>();

   private void a(Gx<S> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.add(var1);
   }

   public final void a(S var1) {
      this.a.add(Gx.a.a((S)var1));
   }

   public final void a(S... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.add(Gx.a.a((S[])Arrays.copyOf(var1, var1.length)));
   }

   public final List<Gx<S>> a() {
      return CollectionsKt.toList(this.a);
   }
}
