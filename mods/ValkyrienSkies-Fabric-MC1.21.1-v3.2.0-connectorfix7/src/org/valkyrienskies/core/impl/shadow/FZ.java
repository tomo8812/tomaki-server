package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KProperty;

public final class FZ {
   public static final <T> T a(dT<T> var0, KProperty<?> var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      return (T)var0.get();
   }

   public static final <T> T a(Provider<T> var0, KProperty<?> var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      return (T)var0.get();
   }
}
