package maki.chunksmithcontroller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class DiskAdmission {
   private volatile boolean blocked;
   private volatile long usable = -1L;
   private long sampled;

   void sample(Path world, Settings settings, long now) {
      if (this.sampled == 0L || now - this.sampled >= 5000L) {
         this.sampled = now;

         try {
            this.update(Files.getFileStore(world).getUsableSpace(), settings);
         } catch (IOException error) {
            this.blocked = true;
            this.usable = -1L;
         }
      }
   }

   void update(long available, Settings settings) {
      this.usable = available;
      double gib = available / 1.0737418E9F;
      if (gib < settings.diskPauseGiB) {
         this.blocked = true;
      } else if (gib >= settings.diskResumeGiB) {
         this.blocked = false;
      }
   }

   boolean blocked() {
      return this.blocked;
   }

   long usableBytes() {
      return this.usable;
   }

   void reset() {
      this.sampled = 0L;
      this.usable = -1L;
      this.blocked = false;
   }
}
