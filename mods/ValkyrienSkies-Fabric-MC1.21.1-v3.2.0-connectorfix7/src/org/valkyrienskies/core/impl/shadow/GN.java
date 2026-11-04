package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class GN implements ek<ObjectMapper> {
   private final GK a;

   private GN(GK var1) {
      this.a = var1;
   }

   private ObjectMapper a() {
      GK var1 = this.a;
      return ev.b(GK.d());
   }

   private static GN a(GK var0) {
      return new GN(var0);
   }

   private static ObjectMapper b(GK var0) {
      return ev.b(GK.d());
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      GK var1 = this.a;
      return ev.b(GK.d());
   }
}
