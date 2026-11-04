package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public final class GC extends SimpleModule {
   public GC() {
      SimpleModule var1
      Intrinsics.checkNotNullExpressionValue((var1 = this).addAbstractTypeMapping(LongSet.class, LongOpenHashSet.class), "")
      var1 = this
      Function1 var2 = <unrepresentable>.a
      Function1 var3 = <unrepresentable>.a
      var1.addSerializer(LongOpenHashSet.class, new GH<>(var2, LongOpenHashSet.class))
      var1.addDeserializer(LongOpenHashSet.class, (JsonDeserializer<? extends LongOpenHashSet>)(new GG<>(var3, LongOpenHashSet.class, long[].class)))
   }
}
