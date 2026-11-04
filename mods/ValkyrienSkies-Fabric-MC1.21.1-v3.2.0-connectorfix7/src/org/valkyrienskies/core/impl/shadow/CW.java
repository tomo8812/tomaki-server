package org.valkyrienskies.core.impl.shadow;

import java.util.SortedSet;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkUnwatchTask;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTask;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTasks;

public final class CW implements VsiChunkWatchTasks {
   private final SortedSet<VsiChunkWatchTask> a;
   private final SortedSet<VsiChunkUnwatchTask> b;

   public CW(SortedSet<VsiChunkWatchTask> var1, SortedSet<VsiChunkUnwatchTask> var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      super();
      this.a = var1;
      this.b = var2;
   }

   @Override
   public final SortedSet<VsiChunkWatchTask> getWatchTasks() {
      return this.a;
   }

   @Override
   public final SortedSet<VsiChunkUnwatchTask> getUnwatchTasks() {
      return this.b;
   }

   @Override
   public final SortedSet<VsiChunkWatchTask> component1() {
      return this.a;
   }

   @Override
   public final SortedSet<VsiChunkUnwatchTask> component2() {
      return this.b;
   }

   private static CW a(SortedSet<VsiChunkWatchTask> var0, SortedSet<VsiChunkUnwatchTask> var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      return new CW(var0, var1);
   }

   // $VF: synthetic method
   private static CW a(CW var0, SortedSet var1, SortedSet var2, int var3) {
      if ((var3 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.b;
      }

      SortedSet var5 = var2;
      SortedSet var4 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var5, "");
      return new CW(var4, var5);
   }

   @Override
   public final String toString() {
      return "ChunkWatchTasksImpl(watchTasks=" + this.a + ", unwatchTasks=" + this.b + ")";
   }

   @Override
   public final int hashCode() {
      return this.a.hashCode() * 31 + this.b.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof CW var2)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.a, var2.a) ? false : Intrinsics.areEqual(this.b, var2.b);
      }
   }
}
