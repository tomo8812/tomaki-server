package dev.diorama.data;

import dev.diorama.Diorama;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.core.SectionPos;

public final class SeenBlocks {
   private static final int MAGIC = 1330467633;
   private final ConcurrentHashMap<Long, long[]> sections = new ConcurrentHashMap<>();
   private volatile boolean changed;

   public boolean isSeen(int x, int y, int z) {
      long[] bits = this.sections.get(SectionPos.asLong(x >> 4, y >> 4, z >> 4));
      if (bits == null) {
         return false;
      }

      int i = index(x, y, z);
      return (bits[i >>> 6] & 1L << i) != 0L;
   }

   public boolean markSeen(int x, int y, int z) {
      long[] bits = this.sections.computeIfAbsent(SectionPos.asLong(x >> 4, y >> 4, z >> 4), k -> new long[64]);
      int i = index(x, y, z);
      long mask = 1L << i;
      if ((bits[i >>> 6] & mask) != 0L) {
         return false;
      }

      bits[i >>> 6] = bits[i >>> 6] | mask;
      this.changed = true;
      return true;
   }

   private static int index(int x, int y, int z) {
      return (y & 15) << 8 | (z & 15) << 4 | x & 15;
   }

   public static SeenBlocks load(Path file) {
      SeenBlocks seen = new SeenBlocks();
      if (!Files.isRegularFile(file)) {
         return seen;
      }

      try (
         InputStream in = Files.newInputStream(file);
         DataInputStream data = new DataInputStream(new InflaterInputStream(in));
      ) {
         if (data.readInt() == 1330467633) {
            int count = data.readInt();

            for (int s = 0; s < count; s++) {
               long key = data.readLong();
               long[] bits = new long[64];

               for (int i = 0; i < 64; i++) {
                  bits[i] = data.readLong();
               }

               seen.sections.put(key, bits);
            }

            return seen;
         } else {
            return seen;
         }
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't read fog-of-war progress from {}", file, e);
         return seen;
      }
   }

   public void save(Path file) {
      if (this.changed) {
         this.changed = false;

         try {
            Files.createDirectories(file.getParent());
            Path temp = file.resolveSibling(file.getFileName() + ".tmp");

            try (
               OutputStream out = Files.newOutputStream(temp);
               DataOutputStream data = new DataOutputStream(new DeflaterOutputStream(out));
            ) {
               data.writeInt(1330467633);
               data.writeInt(this.sections.size());

               for (Entry<Long, long[]> entry : this.sections.entrySet()) {
                  data.writeLong(entry.getKey());

                  for (long word : entry.getValue()) {
                     data.writeLong(word);
                  }
               }
            }

            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (IOException | RuntimeException e) {
            this.changed = true;
            Diorama.LOGGER.error("Couldn't save fog-of-war progress to {}", file, e);
         }
      }
   }
}
