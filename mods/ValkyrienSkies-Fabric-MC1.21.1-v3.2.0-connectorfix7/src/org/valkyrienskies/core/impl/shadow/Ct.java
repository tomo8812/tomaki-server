package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class Ct implements Serializable, Cl {
   private static final long a = 4605318041528645258L;
   private BW b = null;
   private Map<Class<?>, Cl> c = null;

   public Ct() {
      this.c = new HashMap<>();
      this.b = new BW();
   }

   private boolean a(Class<?> var1) {
      return this.c.containsKey(var1);
   }

   private boolean a(Cl var1) {
      return this.c.containsValue(var1);
   }

   private Cl b(Class<?> var1) {
      return this.c.get(var1);
   }

   private Cl a(Class<?> var1, Cl var2) {
      return this.c.put(var1, var2);
   }

   private Cl c(Class<?> var1) {
      return this.c.remove(var1);
   }

   private void a() {
      this.c.clear();
   }

   private Set<Class<?>> b() {
      return this.c.keySet();
   }

   private Collection<Cl> c() {
      return this.c.values();
   }

   @Override
   public final double a(Object var1) {
      double var2 = Double.NaN;
      if (!(var1 instanceof Number) && !(var1 instanceof String)) {
         Class var5 = var1.getClass();
         Cl var4;
         if ((var4 = this.c.get(var5)) != null) {
            var2 = var4.a(var1);
         }
      } else {
         var2 = this.b.a(var1);
      }

      return var2;
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (other instanceof Ct) {
         other = (Ct)other;
         if (!this.b.equals(((Ct)other).b)) {
            return false;
         }

         if (this.c.size() != ((Ct)other).c.size()) {
            return false;
         }

         Iterator var2 = this.c.entrySet().iterator();

         while (var2.hasNext()) {
            Entry var3;
            if (!((Cl)(var3 = (Entry)var2.next()).getValue()).equals(((Ct)other).c.get(var3.getKey()))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      int var1 = this.b.hashCode();

      for (Cl var3 : this.c.values()) {
         var1 = var1 * 31 + var3.hashCode();
      }

      return var1;
   }
}
