package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;

public class dM {
   private String a;
   private JsonNode b;
   private cp c;
   private String d;
   private JsonNode e;
   private JsonNode f;
   private String g;
   private cr h;
   private df i;

   public String a() {
      return this.a;
   }

   public JsonNode b() {
      return this.b;
   }

   public cp c() {
      return this.c;
   }

   public String d() {
      return this.d;
   }

   public JsonNode e() {
      return this.e;
   }

   public JsonNode f() {
      return this.f;
   }

   public String g() {
      return this.g;
   }

   public cp a(URI var1) {
      return this.h.a(var1, this.i.d());
   }

   public cp a(URI var1, cT var2) {
      return var2 != null ? this.h.a(var1, var2) : this.a(var1);
   }

   public cr h() {
      return this.h;
   }

   public static dM.a i() {
      return new dM.a();
   }

   static class a {
      private dM a = new dM();

      public dM.a a(String var1) {
         this.a.a = var1;
         return this;
      }

      public dM.a a(JsonNode var1) {
         this.a.b = var1;
         return this;
      }

      public dM.a a(cp var1) {
         this.a.c = var1;
         return this;
      }

      public dM.a b(String var1) {
         this.a.d = var1;
         return this;
      }

      public dM.a b(JsonNode var1) {
         this.a.e = var1;
         return this;
      }

      public dM.a c(JsonNode var1) {
         this.a.f = var1;
         return this;
      }

      public dM.a c(String var1) {
         this.a.g = var1;
         return this;
      }

      public dM.a a(cr var1) {
         this.a.h = var1;
         return this;
      }

      public dM.a a(df var1) {
         this.a.i = var1;
         return this;
      }

      public dM a() {
         return this.a;
      }
   }
}
