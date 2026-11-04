package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public final class GH<A, B> extends StdSerializer<A> {
   private final Function1<A, B> a;

   public GH(Function1<? super A, ? extends B> var1, Class<A> var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      super(var2);
      this.a = var1;
   }

   @Override
   public final void serialize(A value, JsonGenerator gen, SerializerProvider provider) {
      Intrinsics.checkNotNullParameter(gen, "");
      Intrinsics.checkNotNullParameter(provider, "");
      provider.defaultSerializeValue(this.a.invoke(value), gen);
   }
}
