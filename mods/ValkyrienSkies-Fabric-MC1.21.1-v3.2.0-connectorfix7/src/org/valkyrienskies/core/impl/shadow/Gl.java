package org.valkyrienskies.core.impl.shadow;

import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public final class Gl {
   private static <S> Gi<S> a(S var0, Gu<S>... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return new Gk<>(var0, ArraysKt.asList(var1), false, false, null, 24, null);
   }

   public static final <S> Gi<S> a(S var0, Function1<? super Gj<S>, Unit> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      var0 = new Gj<>(var0);
      var1.invoke(var0);
      return var0.c();
   }
}
