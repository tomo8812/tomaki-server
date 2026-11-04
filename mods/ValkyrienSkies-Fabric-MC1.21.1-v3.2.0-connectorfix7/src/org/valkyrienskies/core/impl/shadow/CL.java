package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public final class CL {
   final Class<?> a;
   final Class<?> b;
   final Function1<Object, Object> c;

   public CL(Class<?> var1, Class<?> var2, Function1<Object, ? extends Object> var3) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   public final Class<?> a() {
      return this.a;
   }

   public final Class<?> b() {
      return this.b;
   }

   public final Function1<Object, Object> c() {
      return this.c;
   }
}
