package dev.emi.emi.util;

import java.util.Map;

public record InheritanceMap<V>(Map<Class<?>, V> map) {
   public Class<?> getKey(Class<?> clazz) {
      for (Class<?> w = clazz; w != null; w = w.getSuperclass()) {
         if (this.map.containsKey(w)) {
            return w;
         }

         if (w == Object.class) {
            break;
         }
      }

      for (Class<?> i : clazz.getInterfaces()) {
         if (this.map.containsKey(i)) {
            return i;
         }
      }

      return null;
   }

   public V get(Class<?> clazz) {
      clazz = this.getKey(clazz);
      return clazz != null ? this.map.get(clazz) : null;
   }
}
