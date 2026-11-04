package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class GO implements ek<ObjectMapper> {
   private final GK a;

   private GO(GK var1) {
      this.a = var1;
   }

   private ObjectMapper a() {
      GK var1 = this.a;
      return ev.b(GK.b());
   }

   public static GO a(GK var0) {
      return new GO(var0);
   }

   private static ObjectMapper b(GK var0) {
      return ev.b(GK.b());
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      GK var1 = this.a;
      return ev.b(GK.b());
   }
}
