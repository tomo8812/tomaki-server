package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

public final class Gm<S> implements Gu<S> {
   private final Gu<S>[] b;

   public Gm(Gu<S>... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.b = var1;
   }

   @Override
   public final String a(List<? extends S> var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Gu[] var3 = this.b;
      var3 = this.b;
      var var4 = new ArrayList();
      var3 = var3;
      int var5 = 0;

      for (int var6 = var3.length; var5 < var6; var5++) {
         Gu var7;
         String var10000 = (var7 = var3[var5]).a(var1, var2);
         if (var10000 != null) {
            String var11 = var10000;
            var4.add(var11);
         }
      }

      if ((var1 = var4).size() > 1) {
         return "Composite failure: " + CollectionsKt.joinToString$default(var1, null, null, null, 0, null, null, 63, null);
      } else {
         return var1.size() == 1 ? (String)CollectionsKt.first(var1) : null;
      }
   }
}
