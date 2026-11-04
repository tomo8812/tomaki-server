package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;

public final class ad {
   public final aq a;
   public final am b;
   public final JsonNode c;
   public am d;
   public final JsonNode e;

   public ad(aq var1, am var2, JsonNode var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.e = null;
   }

   public ad(aq var1, am var2, am var3) {
      this.a = var1;
      this.b = var2;
      this.d = var3;
      this.c = null;
      this.e = null;
   }

   private ad(aq var1, am var2, JsonNode var3, JsonNode var4) {
      this.a = var1;
      this.b = var2;
      this.c = var4;
      this.e = var3;
   }

   public final aq a() {
      return this.a;
   }

   public final am b() {
      return this.b;
   }

   public final JsonNode c() {
      return this.c;
   }

   public static ad a(aq var0, am var1, JsonNode var2) {
      return new ad(var0, var1, var2);
   }

   public static ad a(aq var0, am var1, JsonNode var2, JsonNode var3) {
      return new ad(var0, var1, var2, var3);
   }

   final am d() {
      return this.d;
   }

   public final JsonNode e() {
      return this.e;
   }
}
