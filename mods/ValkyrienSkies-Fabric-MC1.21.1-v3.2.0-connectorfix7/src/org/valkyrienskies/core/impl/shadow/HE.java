package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;

public final class HE implements Ik {
   private final GY a;
   private boolean b;
   private boolean c;

   public HE(GY var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
      this.b = true;
   }

   @Override
   public final void a(Iq var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.b = false;
   }

   @Override
   public final void a(Hm var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.b = true;
   }

   @Override
   public final boolean a() {
      return this.b;
   }

   @Override
   public final boolean b() {
      return this.c;
   }

   @Override
   public final GY c() {
      return this.a;
   }

   @Override
   public final void close() {
      this.c = true;
   }
}
