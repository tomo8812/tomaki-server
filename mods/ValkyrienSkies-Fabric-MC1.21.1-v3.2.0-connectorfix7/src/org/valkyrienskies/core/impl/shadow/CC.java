package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Provider;

public final class CC implements ek<CA> {
   private final Provider<CH> a;
   private final Provider<ObjectMapper> b;

   private CC(Provider<CH> var1, Provider<ObjectMapper> var2) {
      this.a = var1;
      this.b = var2;
   }

   private CA a() {
      CH var10000 = this.a.get();
      ObjectMapper var2 = this.b.get();
      CH var1 = var10000;
      return new CA(var1, var2);
   }

   public static CC a(Provider<CH> var0, Provider<ObjectMapper> var1) {
      return new CC(var0, var1);
   }

   private static CA a(CH var0, ObjectMapper var1) {
      return new CA(var0, var1);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      CC var1 = this;
      CH var10000 = this.a.get();
      ObjectMapper var2 = var1.b.get();
      CH var3 = var10000;
      return new CA(var3, var2);
   }
}
