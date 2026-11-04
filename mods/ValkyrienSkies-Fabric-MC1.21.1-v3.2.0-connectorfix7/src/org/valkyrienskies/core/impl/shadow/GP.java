package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class GP implements ek<ObjectMapper> {
   private final GK a;

   private GP(GK var1) {
      this.a = var1;
   }

   private ObjectMapper a() {
      GK var1 = this.a;
      return ev.b(GK.e());
   }

   private static GP a(GK var0) {
      return new GP(var0);
   }

   private static ObjectMapper b(GK var0) {
      return ev.b(GK.e());
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      GK var1 = this.a;
      return ev.b(GK.e());
   }
}
