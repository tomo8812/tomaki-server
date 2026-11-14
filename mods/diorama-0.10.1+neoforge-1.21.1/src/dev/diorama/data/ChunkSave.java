package dev.diorama.data;

import java.util.Collection;
import java.util.Map;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public interface ChunkSave {
   Level level();

   boolean isOpen();

   Map<Long, int[]> scan();

   ChunkTags.@Nullable Summary summary(int var1, int var2);

   int top(int var1, int var2);

   @Nullable SavedChunk read(int var1, int var2);

   default int now() {
      return (int)(System.currentTimeMillis() / 1000L);
   }

   default void refresh() {
   }

   default boolean prefetches() {
      return false;
   }

   default void prefetch(Collection<Long> chunks, boolean urgent) {
   }

   void forgetTops(long var1);

   void clear();

   void close();

   boolean serverBehind();

   final class Late extends RuntimeException {
      public Late(String message) {
         super(message, null, false, false);
      }
   }
}
