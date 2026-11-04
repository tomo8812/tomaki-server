package org.valkyrienskies.core.impl.shadow;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public final class Gj<S> extends Gg<S> {
   private final S c;
   private Function1<? super String, Unit> d;
   public boolean b;
   private boolean e;

   public Gj(S var1) {
      this.c = (S)var1;
      this.e = true;
   }

   private Function1<String, Unit> d() {
      return this.d;
   }

   private void b(Function1<? super String, Unit> var1) {
      this.d = var1;
   }

   private void e() {
      this.e = false;
   }

   public final void b() {
      this.b = true;
   }

   public final Gi<S> c() {
      return new Gk<>(this.c, this.a(), this.b, this.e, this.d);
   }
}
