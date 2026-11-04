package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class df {
   private final dx a;
   private final dD b;
   private final co c;
   private final cr d;
   private cT e;
   private final Map<String, cs> f = new HashMap<>();
   private final Stack<df.a> g = new Stack<>();

   public df(dx var1, dD var2, co var3, cr var4, cT var5) {
      if (var1 == null) {
         throw new IllegalArgumentException("URIFactory must not be null");
      }

      if (var3 == null) {
         throw new IllegalArgumentException("JsonMetaSchema must not be null");
      }

      if (var4 == null) {
         throw new IllegalArgumentException("JsonSchemaFactory must not be null");
      }

      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
   }

   public cv a(String var1, String var2, JsonNode var3, cp var4, String var5) {
      return this.c.a(this, var1, var2, var3, var4, var5);
   }

   public String a(JsonNode var1) {
      return this.c.a(var1);
   }

   public dx a() {
      return this.a;
   }

   public dD b() {
      return this.b;
   }

   public cr c() {
      return this.d;
   }

   public cT d() {
      if (this.e == null) {
         this.e = new cT();
      }

      return this.e;
   }

   public void a(cT var1) {
      this.e = var1;
   }

   public void a(String var1, cs var2) {
      this.f.put(var1, var2);
   }

   public cs a(String var1) {
      return this.f.get(var1);
   }

   public df.a e() {
      return !this.g.empty() ? this.g.peek() : null;
   }

   public void a(df.a var1, String var2) {
      this.g.push(var1);
   }

   public void b(String var1) {
      this.g.pop();
   }

   protected co f() {
      return this.c;
   }

   public static class a {
      private final Map<String, ObjectNode> a = new HashMap<>();
      private boolean b = false;

      public void a(String var1, ObjectNode var2) {
         this.a.put(var1, var2);
      }

      public ObjectNode a(String var1) {
         return this.a.get(var1);
      }

      public void a() {
         this.b = true;
      }

      public boolean b() {
         return this.b;
      }

      public boolean c() {
         return !this.a.isEmpty();
      }
   }
}
