package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

public final class IR implements HW {
   public final int a;
   public final int b;
   public final List<HU> c;

   public IR(int var1, int var2, List<HU> var3) {
      Intrinsics.checkNotNullParameter(var3, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   @Override
   public final int a() {
      return this.a;
   }

   @Override
   public final int b() {
      return this.b;
   }

   @Override
   public final List<HU> c() {
      return this.c;
   }
}
