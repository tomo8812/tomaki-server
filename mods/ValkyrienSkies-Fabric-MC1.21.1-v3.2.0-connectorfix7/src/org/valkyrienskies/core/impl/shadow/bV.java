package org.valkyrienskies.core.impl.shadow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class bV {
   static final String a = "com.networknt.schema.CollectorKey";
   private Map<String, Object> b = new HashMap<>();
   private Map<String, Object> c = new HashMap<>();

   public static bV a() {
      return (bV)cW.a("com.networknt.schema.CollectorKey");
   }

   public <E> void a(String var1, bU<E> var2) {
      this.b.put(var1, var2);
   }

   public <E> void a(String var1, Object var2) {
      this.b.put(var1, var2);
   }

   public Object a(String var1) {
      return this.b.get(var1) instanceof bU && this.c.get(var1) != null ? this.c.get(var1) : this.b.get(var1);
   }

   public Map<String, Object> b() {
      HashMap var1;
      (var1 = new HashMap()).putAll(this.b);
      var1.putAll(this.c);
      return var1;
   }

   public void b(String var1, Object var2) {
      Object var3;
      if ((var3 = this.b.get(var1)) instanceof bU) {
         bU var4;
         (var4 = (bU)var3).a(var2);
      }
   }

   public void c() {
      this.b = new HashMap<>();
      this.c = new HashMap<>();
   }

   void d() {
      Iterator var1 = this.b.entrySet().iterator();

      while (var1.hasNext()) {
         Entry var2;
         if ((var2 = (Entry)var1.next()).getValue() instanceof bU) {
            bU var3 = (bU)var2.getValue();
            this.c.put((String)var2.getKey(), var3.a());
         }
      }
   }
}
