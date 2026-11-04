package org.valkyrienskies.core.impl.shadow;

import io.netty.buffer.ByteBuf;
import kotlin.jvm.internal.Intrinsics;

public final class Fs<T> {
   public final Fq<T> a;
   public T b;

   public Fs(Fq<T> var1, T var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
      this.b = (T)var2;
   }

   private T a() {
      return this.b;
   }

   public final ByteBuf a(T var1, ByteBuf var2) {
      Intrinsics.checkNotNullParameter(var2, "");
      this.a.a(this.b, (T)var1, var2);
      this.b = (T)var1;
      return var2;
   }
}
