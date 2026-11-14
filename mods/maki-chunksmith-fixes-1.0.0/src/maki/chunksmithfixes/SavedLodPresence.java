package maki.chunksmithfixes;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class SavedLodPresence {
   private static final Map<Path, WeakReference<CsLodPresenceIndex>> INDEXES = new HashMap<>();

   private SavedLodPresence() {
   }

   public static synchronized void register(Path root, CsLodPresenceIndex index) {
      INDEXES.put(root.toAbsolutePath().normalize(), new WeakReference<>(index));
   }

   public static void written(Path root, CsLodChunk record) {
      CsLodPresenceIndex index;
      synchronized (SavedLodPresence.class) {
         WeakReference<CsLodPresenceIndex> reference = INDEXES.get(root.toAbsolutePath().normalize());
         index = reference == null ? null : reference.get();
      }

      if (index != null) {
         index.markLod(record.getChunkX(), record.getChunkZ());
      }
   }

   public static synchronized void clear() {
      INDEXES.clear();
   }
}
