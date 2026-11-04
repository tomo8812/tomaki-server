package org.valkyrienskies.core.impl.shadow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import kotlin.Pair;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;

public final class EU {
   public static final <V> Long2ObjectMap<V> a(Pair<Long, ? extends V>... var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      Long2ObjectOpenHashMap var1;
      MapsKt.putAll((Map)(var1 = new Long2ObjectOpenHashMap()), var0);
      return (Long2ObjectMap<V>)var1;
   }

   public static final <V> Long2ObjectMap<? extends V> a() {
      Long2ObjectMap var10000 = Long2ObjectMaps.emptyMap();
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      return var10000;
   }
}
