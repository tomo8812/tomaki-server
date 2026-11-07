package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

final class PromotionClaim {
   private final Path claimed;
   private boolean needsEntityWrite;
   private boolean entityCountKnown = true;
   private boolean classifying;
   private boolean loaded;
   private boolean entitiesRestored;
   private boolean chunkWritten;
   private boolean entitiesWritten;
   private boolean failed;
   private int pendingPublications;
   private boolean retirementQueued;

   private PromotionClaim(Path claimed, boolean needsEntityWrite) {
      this.claimed = claimed;
      this.needsEntityWrite = needsEntityWrite;
   }

   static Path claimedPath(Path available) {
      Path region = available.getParent();
      Path dimension = region.getParent().getParent();
      return RecoveryClaims.root(dimension).resolve(region.getFileName()).resolve(available.getFileName() + ".in-use");
   }

   static PromotionClaim take(Path available, boolean hasEntities) throws IOException {
      try (CachePathLocks.Guard ignored = CachePathLocks.lock(claimedPath(available))) {
         Path target = claimedPath(available);
         if (!RecoveryClaims.claimExists(target) && Files.isRegularFile(available)) {
            Files.createDirectories(target.getParent());
            Files.move(available, target, StandardCopyOption.ATOMIC_MOVE);
            return new PromotionClaim(target, hasEntities);
         } else {
            return null;
         }
      }
   }

   static boolean publish(Path temporary, Path available) throws IOException {
      try (CachePathLocks.Guard ignored = CachePathLocks.lock(claimedPath(available))) {
         if (RecoveryClaims.claimExists(claimedPath(available))) {
            return false;
         }

         try {
            Files.move(temporary, available, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException unsupported) {
            Files.move(temporary, available, StandardCopyOption.REPLACE_EXISTING);
         }

         return true;
      }
   }

   static PromotionClaim adoptLive(Path available, boolean capturePending) throws IOException {
      try (CachePathLocks.Guard ignored = CachePathLocks.lock(claimedPath(available))) {
         if (RecoveryClaims.claimExists(claimedPath(available))) {
            return null;
         }

         PromotionClaim claim = take(available, true);
         if (claim != null) {
            claim.entityCountKnown = false;
         }

         if (claim == null && capturePending) {
            Path marker = claimedPath(available);
            Files.createDirectories(marker.getParent());
            Files.createFile(marker);
            claim = new PromotionClaim(marker, false);
         }

         if (claim != null) {
            claim.loaded();
            claim.entitiesRestored();
         }

         return claim;
      }
   }

   synchronized boolean loaded() {
      if (!this.loaded && !this.failed) {
         this.loaded = true;
         return true;
      } else {
         return false;
      }
   }

   synchronized void chunkWrite(boolean success) {
      if (success) {
         this.chunkWritten = true;
      } else {
         this.failed = true;
      }
   }

   synchronized void entityWrite(boolean success) {
      if (success) {
         this.entitiesWritten = true;
      } else {
         this.failed = true;
      }
   }

   synchronized void failed() {
      this.failed = true;
   }

   synchronized void entitiesRestored() {
      this.entitiesRestored = true;
   }

   synchronized void publicationPending() {
      this.pendingPublications++;
   }

   synchronized void publicationResolved() {
      if (this.pendingPublications > 0) {
         this.pendingPublications--;
      }
   }

   synchronized boolean acceptsEntityWrite() {
      return this.entitiesRestored && !this.failed;
   }

   synchronized boolean beginClassification() {
      if (!this.entityCountKnown && !this.classifying && !this.failed) {
         this.classifying = true;
         return true;
      } else {
         return false;
      }
   }

   synchronized void classificationRejected() {
      this.classifying = false;
   }

   synchronized void classified(boolean explicitlyEmpty) {
      this.entityCountKnown = true;
      this.classifying = false;
      if (explicitlyEmpty) {
         this.needsEntityWrite = false;
      }
   }

   synchronized boolean canRetire() {
      return this.loaded && !this.failed && this.chunkWritten && this.pendingPublications == 0 && (!this.needsEntityWrite || this.entitiesWritten);
   }

   synchronized boolean beginRetirement() {
      if (!this.retirementQueued && this.canRetire()) {
         this.retirementQueued = true;
         return true;
      } else {
         return false;
      }
   }

   synchronized void retirementRejected() {
      this.retirementQueued = false;
   }

   Path path() {
      return this.claimed;
   }
}
