package org.valkyrienskies.core.impl.shadow;

import java.util.HashMap;
import java.util.Map;

public class cW {
   private static ThreadLocal<Map<String, Object>> a = new ThreadLocal<Map<String, Object>>() {
      protected Map<String, Object> a() {
         return new HashMap<>();
      }

      // $VF: synthetic method
      @Override
      protected Object initialValue() {
         return this.a();
      }
   };

   public static Object a(String var0) {
      return a.get().get(var0);
   }

   public static void a(String var0, Object var1) {
      a.get().put(var0, var1);
   }

   public static void b(String var0) {
      a.get().remove(var0);
   }
}
