package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class GM implements ek<ObjectMapper> {
   private final GK a;

   private GM(GK var1) {
      this.a = var1;
   }

   private ObjectMapper a() {
      GK var1 = this.a;
      return ev.b(GK.a());
   }

   public static GM a(GK var0) {
      return new GM(var0);
   }

   private static ObjectMapper b(GK var0) {
      return ev.b(GK.a());
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      GK var1 = this.a;
      return ev.b(GK.a());
   }
}
