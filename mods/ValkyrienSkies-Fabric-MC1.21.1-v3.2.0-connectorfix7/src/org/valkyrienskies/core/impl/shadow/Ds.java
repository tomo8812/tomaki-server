package org.valkyrienskies.core.impl.shadow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.ships.properties.ChunkClaim;

public final class Ds<T> {
   private final Long2ObjectMap<T> a = (Long2ObjectMap<T>)(new Long2ObjectOpenHashMap());

   public final void a(ChunkClaim var1, T var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      long var3 = var1.toLong();
      if (this.a.containsKey(var3)) {
         throw new IllegalArgumentException("Tried adding " + var2 + " at " + var1 + ", but a value already exists at " + var1);
      }

      this.a.put(var3, var2);
   }

   public final void a(ChunkClaim var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      long var2 = var1.toLong();
      if (this.a.remove(var2) == null) {
         throw new IllegalArgumentException("Tried to remove data at " + var1 + ", but that claim wasn't in the chunk claim map!");
      }
   }

   private T b(ChunkClaim var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return (T)this.a.get(var1.toLong());
   }

   public final T a(int var1, int var2) {
      long var3 = ChunkClaim.Companion.getClaimThenToLong(var1, var2);
      return (T)this.a.get(var3);
   }
}
