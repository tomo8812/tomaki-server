package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Provider;

public final class CO implements ek<CN> {
   private final Provider<ObjectMapper> a;

   private CO(Provider<ObjectMapper> var1) {
      this.a = var1;
   }

   private CN a() {
      ObjectMapper var1 = this.a.get();
      return new CN(var1);
   }

   public static CO a(Provider<ObjectMapper> var0) {
      return new CO(var0);
   }

   private static CN a(ObjectMapper var0) {
      return new CN(var0);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      ObjectMapper var1 = this.a.get();
      return new CN(var1);
   }
}
