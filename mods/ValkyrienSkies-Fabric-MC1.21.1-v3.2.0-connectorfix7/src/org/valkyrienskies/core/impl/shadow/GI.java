package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public final class GI {
   // $VF: synthetic method
   private static <A, B> void a(SimpleModule var0, Function1<? super A, ? extends B> var1, Function1<? super B, ? extends A> var2) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.reifiedOperationMarker(4, "A");
      Class var10001 = Object.class;
      Intrinsics.reifiedOperationMarker(4, "A");
      var0.addSerializer(var10001, (JsonSerializer<Object>)(new GH<>(var1, Object.class)));
      Intrinsics.reifiedOperationMarker(4, "A");
      var10001 = Object.class;
      Intrinsics.reifiedOperationMarker(4, "A");
      Class var10005 = Object.class;
      Intrinsics.reifiedOperationMarker(4, "B");
      var0.addDeserializer(var10001, (JsonDeserializer<? extends A>)(new GG<>(var2, var10005, Object.class)));
   }
}
