package org.valkyrienskies.core.impl.shadow;

import javax.inject.Inject;
import javax.inject.Named;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.impl.game.ChunkAllocator;
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider;

public final class DQ implements ChunkAllocatorProvider {
   public final ChunkAllocator a;

   @Inject
   public DQ(@Named("primary") ChunkAllocator var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
   }

   public final ChunkAllocator a() {
      return this.a;
   }

   @Override
   public final ChunkAllocator forDimension(String dimensionId) {
      Intrinsics.checkNotNullParameter(dimensionId, "");
      return this.a;
   }
}
