package org.valkyrienskies.core.impl.shadow;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.world.connectivity.DoubleAugmentation;
import org.valkyrienskies.core.api.world.connectivity.DoubleComponentAugmentation;

public final class DZ {
   public final Map<String, DoubleAugmentation> a = new LinkedHashMap<>();

   public final DoubleAugmentation a(String var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      if (this.a.containsKey(var1)) {
         Object var10000 = this.a.get(var1);
         Intrinsics.checkNotNull(var10000);
         return (DoubleAugmentation)var10000;
      } else {
         Ed var2 = new Ed(var1);
         this.a.put(var1, var2);
         return var2;
      }
   }

   private DoubleAugmentation c(String var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      if (this.a.containsKey(var1)) {
         Object var10000 = this.a.get(var1);
         Intrinsics.checkNotNull(var10000);
         return (DoubleAugmentation)var10000;
      } else {
         Eb var2 = new Eb(var1);
         this.a.put(var1, var2);
         return var2;
      }
   }

   public final DoubleComponentAugmentation b(String var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      if (this.a.containsKey(var1)) {
         Object var10000 = this.a.get(var1);
         Intrinsics.checkNotNull(var10000);
         return (DoubleComponentAugmentation)var10000;
      } else {
         Ee var2 = new Ee(var1);
         this.a.put(var1, var2);
         return var2;
      }
   }
}
