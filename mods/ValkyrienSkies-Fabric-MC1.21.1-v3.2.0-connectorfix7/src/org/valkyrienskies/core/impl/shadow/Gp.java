package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;

public final class Gp<S> implements Gu<S> {
   private final List<S> b;

   public Gp(S... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.b = ArraysKt.asList(var1);
   }

   @Override
   public final String a(List<? extends S> var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      if (var2) {
         return null;
      }

      if (this.b.isEmpty() && SetsKt.setOf(var1).size() != var1.size()) {
         return "Expected no duplicates";
      }

      Iterable var7;
      if (!((var7 = this.b) instanceof Collection) || !((Collection)var7).isEmpty()) {
         for (Object var3 : var7) {
            Iterable var4;
            int var10000;
            if ((var4 = var1) instanceof Collection && ((Collection)var4).isEmpty()) {
               var10000 = 0;
            } else {
               int var5 = 0;
               Iterator var9 = var4.iterator();

               while (var9.hasNext()) {
                  if (Intrinsics.areEqual(var9.next(), var3)) {
                     if (++var5 < 0) {
                        CollectionsKt.throwCountOverflow();
                     }
                  }
               }

               var10000 = var5;
            }

            if (var10000 > 1) {
               return true ? "Expected no duplicates of the following stages: " + this.b : null;
            }
         }
      }

      return false ? "Expected no duplicates of the following stages: " + this.b : null;
   }
}
