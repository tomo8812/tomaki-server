package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class GL implements ek<ObjectMapper> {
   private final GK a;

   private GL(GK var1) {
      this.a = var1;
   }

   private ObjectMapper a() {
      GK var1 = this.a;
      return ev.b(GK.c());
   }

   private static GL a(GK var0) {
      return new GL(var0);
   }

   private static ObjectMapper b(GK var0) {
      return ev.b(GK.c());
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      GK var1 = this.a;
      return ev.b(GK.c());
   }
}
