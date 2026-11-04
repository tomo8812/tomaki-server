package org.valkyrienskies.core.internal.world.chunks

import java.util.SortedSet

public interface VsiChunkWatchTasks {
   public val watchTasks: SortedSet<VsiChunkWatchTask>

   public val unwatchTasks: SortedSet<VsiChunkUnwatchTask>

   @JvmSynthetic
   public open operator fun component1(): SortedSet<VsiChunkWatchTask> {
      return this.watchTasks
   }

   @JvmSynthetic
   public open operator fun component2(): SortedSet<VsiChunkUnwatchTask> {
      return this.unwatchTasks
   }
}
