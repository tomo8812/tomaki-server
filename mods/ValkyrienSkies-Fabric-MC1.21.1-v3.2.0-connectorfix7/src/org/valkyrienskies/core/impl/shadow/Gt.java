package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import java.util.function.Predicate;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

public final class Gt<S> implements Gu<S> {
   private final Predicate<Thread> b;
   private final S[] c;

   public Gt(Predicate<Thread> var1, S... var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      super();
      this.b = var1;
      this.c = (S[])var2;
      if (this.c.length == 0) {
         String var3 = "Failed requirement.";
         throw new IllegalArgumentException(var3.toString());
      }
   }

   @Override
   public final String a(List<? extends S> var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      return ArraysKt.contains(this.c, CollectionsKt.last(var1)) && !this.b.test(Thread.currentThread())
         ? "Stages " + this.c + " require a different thread. Current thread: " + Thread.currentThread()
         : null;
   }
}
