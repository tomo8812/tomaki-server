package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;

/** A claim is also a persistent tombstone: its payload is never automatically replayed. */
final class PromotionClaim {
    private final Path claimed;
    private boolean needsEntityWrite;
    private boolean entityCountKnown=true;
    private boolean classifying;
    private boolean loaded;
    private boolean entitiesRestored;
    private boolean chunkWritten;
    private boolean entitiesWritten;
    private boolean needsPoiWrite;
    private boolean poiWritten;
    private boolean failed;
    private int pendingPublications;
    private boolean retirementQueued;

    private PromotionClaim(Path claimed, boolean needsEntityWrite) {
        this.claimed = claimed;
        this.needsEntityWrite = needsEntityWrite;
    }

    static Path claimedPath(Path available) {
        Path region=available.getParent();
        Path dimension=region.getParent().getParent();
        return RecoveryClaims.root(dimension).resolve(region.getFileName()).resolve(available.getFileName()+".in-use");
    }

    static PromotionClaim take(Path available, boolean hasEntities) throws IOException {
        try(var ignored=CachePathLocks.lock(claimedPath(available))) {
        Path target = claimedPath(available);
        if (RecoveryClaims.claimExists(target) || !Files.isRegularFile(available)) return null;
        Files.createDirectories(target.getParent());
        // Same filesystem. If atomic rename is unavailable, refuse promotion.
        // A copy/delete fallback could expose the same snapshot twice after a crash.
        Files.move(available, target, StandardCopyOption.ATOMIC_MOVE);
        return new PromotionClaim(target, hasEntities);
        }
    }

    static boolean publish(Path temporary, Path available) throws IOException {
        try(var ignored=CachePathLocks.lock(claimedPath(available))) {
        if (RecoveryClaims.claimExists(claimedPath(available))) return false;
        try { Files.move(temporary, available, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
        catch (AtomicMoveNotSupportedException unsupported) { Files.move(temporary, available, StandardCopyOption.REPLACE_EXISTING); }
        return true;
        }
    }

    /** Delete only an unclaimed snapshot superseded by successfully persisted normal FULL. */
    static long discardAvailable(Path available) throws IOException {
        try(var ignored=CachePathLocks.lock(claimedPath(available))) {
            if(RecoveryClaims.claimExists(claimedPath(available)))return 0L;
            long bytes=Files.isRegularFile(available)?Files.size(available):0L;
            Files.deleteIfExists(available);
            return bytes;
        }
    }

    static PromotionClaim adoptLive(Path available, boolean capturePending) throws IOException {
        try(var ignored=CachePathLocks.lock(claimedPath(available))) {
        if (RecoveryClaims.claimExists(claimedPath(available))) return null;
        PromotionClaim claim = take(available,true); // Conservatively retain unknown entity payload until an entity ACK.
        if(claim!=null) claim.entityCountKnown=false;
        if (claim==null && capturePending) {
            Path marker=claimedPath(available);
            Files.createDirectories(marker.getParent());
            Files.createFile(marker); // Also prevents a delayed capture from making old state available again.
            claim=new PromotionClaim(marker,false);
        }
        if (claim!=null) { claim.loaded(); claim.entitiesRestored(); }
        return claim;
        }
    }

    synchronized boolean loaded() {
        if (loaded || failed) return false;
        loaded = true;
        return true;
    }

    synchronized void chunkWrite(boolean success) {
        if (success) chunkWritten = true;
        else failed = true;
    }

    synchronized void entityWrite(boolean success) {
        if (success) entitiesWritten = true;
        else failed = true;
    }

    synchronized void requirePoiWrite() { needsPoiWrite = true; }
    synchronized boolean requiresPoiWrite() { return needsPoiWrite && !poiWritten && !failed; }
    synchronized void poiWrite(boolean success) {
        if (success) poiWritten = true;
        else failed = true;
    }
    synchronized void failed() { failed = true; }
    synchronized void entitiesRestored() { entitiesRestored = true; }
    synchronized void publicationPending() { pendingPublications++; }
    synchronized void publicationResolved() { if (pendingPublications > 0) pendingPublications--; }
    synchronized boolean acceptsEntityWrite() { return entitiesRestored && !failed; }
    synchronized boolean beginClassification() {
        if(entityCountKnown || classifying || failed) return false;
        classifying=true;
        return true;
    }
    synchronized void classificationRejected() { classifying=false; }
    synchronized void classified(boolean explicitlyEmpty) {
        entityCountKnown=true;
        classifying=false;
        if(explicitlyEmpty) needsEntityWrite=false;
    }
    synchronized boolean canRetire() {
        return loaded && !failed && chunkWritten && pendingPublications == 0
            && (!needsEntityWrite || entitiesWritten) && (!needsPoiWrite || poiWritten);
    }
    synchronized boolean beginRetirement() {
        if (retirementQueued || !canRetire()) return false;
        retirementQueued=true;
        return true;
    }
    synchronized void retirementRejected() { retirementQueued=false; }
    Path path() { return claimed; }
}
