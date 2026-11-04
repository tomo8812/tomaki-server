package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

public final class Hy implements Hd {
   private int a;
   private final int b;

   private Hy(int var1, int var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public final int a() {
      return this.a;
   }

   @Override
   public final void a(int var1) {
      this.a = var1;
   }

   @Override
   public final int b() {
      return this.b;
   }

   @Override
   public final List<CompletableFuture<Void>> a(List<Hd.a> var1, double var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      return CollectionsKt.emptyList();
   }
}
