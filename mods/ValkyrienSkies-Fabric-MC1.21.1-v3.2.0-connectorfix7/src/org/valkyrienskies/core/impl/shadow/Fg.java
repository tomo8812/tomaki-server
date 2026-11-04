package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.event.RegisteredListener;

public final class Fg {
   public static final void a(Iterable<? extends RegisteredListener> var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         ((RegisteredListener)var2.next()).unregister();
      }
   }
}
