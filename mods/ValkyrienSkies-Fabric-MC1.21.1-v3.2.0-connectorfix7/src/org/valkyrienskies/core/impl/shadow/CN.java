package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import kotlin.jvm.internal.Intrinsics;

public final class CN implements CK {
   private final ObjectMapper a;

   @Inject
   public CN(ObjectMapper var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
   }

   @Override
   public final JsonNode a(Object var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      JsonNode var10000 = this.a.valueToTree(var1);
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      return var10000;
   }

   @Override
   public final Object a(JsonNode var1, Class<?> var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Object var10000 = this.a.treeToValue(var1, var2);
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      return var10000;
   }
}
