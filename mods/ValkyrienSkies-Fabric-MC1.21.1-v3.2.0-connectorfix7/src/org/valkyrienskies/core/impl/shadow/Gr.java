package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

public final class Gr<S> implements Gu<S> {
   private final List<Gx<S>> b;

   public Gr(List<? extends Gx<S>> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.b = var1;
      if (this.b.isEmpty()) {
         String var2 = "Failed requirement.";
         throw new IllegalArgumentException(var2.toString());
      }
   }

   private List<Gx<S>> a() {
      return this.b;
   }

   @Override
   public final String a(List<? extends S> var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      var2 = 0;

      for (int var3 : var1) {
         List var4 = this.b;
         int var5 = 0;
         Iterator var10 = var4.iterator();

         int var10000;
         while (true) {
            if (!var10.hasNext()) {
               var10000 = -1;
               break;
            }

            if (((Gx)var10.next()).a(var3)) {
               var10000 = var5;
               break;
            }

            var5++;
         }

         var3 = var10000;
         if (var10000 != -1) {
            if (var3 < var2) {
               return "Required stages matching predicate in the following order: " + this.b;
            }

            var2 = var3;
         }
      }

      return null;
   }
}
