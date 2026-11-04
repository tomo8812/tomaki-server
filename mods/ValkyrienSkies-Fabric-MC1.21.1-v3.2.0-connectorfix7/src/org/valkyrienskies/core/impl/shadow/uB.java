package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class uB implements uY {
   private final Collection<String> a = new ArrayList<>();

   private uB(String... var1) {
      String[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         String var4 = var5[var3];
         this.a.add(var4);
      }
   }

   private uB(Collection<String> var1) {
      this.a.addAll(var1);
   }

   @Override
   public final Collection<String> a() {
      return this.a;
   }

   @Override
   public final boolean a(String var1) {
      Iterator var2 = this.a.iterator();

      while (var2.hasNext()) {
         if (((String)var2.next()).equals(var1)) {
            return true;
         }
      }

      return false;
   }

   private void b(String var1) {
      if (!this.a(var1)) {
         throw new ve(var1);
      }
   }
}
