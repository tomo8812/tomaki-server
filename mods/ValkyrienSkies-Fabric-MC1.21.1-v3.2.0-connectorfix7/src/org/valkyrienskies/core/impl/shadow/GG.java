package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public final class GG<A, B> extends StdDeserializer<A> {
   private final Function1<B, A> a;
   private final Class<B> b;

   public GG(Function1<? super B, ? extends A> var1, Class<A> var2, Class<B> var3) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      super(var2);
      this.a = var1;
      this.b = var3;
   }

   @Override
   public final A deserialize(JsonParser p, DeserializationContext ctxt) {
      Intrinsics.checkNotNullParameter(p, "");
      Intrinsics.checkNotNullParameter(ctxt, "");
      return (A)this.a.invoke(p.getCodec().readValue(p, this.b));
   }
}
