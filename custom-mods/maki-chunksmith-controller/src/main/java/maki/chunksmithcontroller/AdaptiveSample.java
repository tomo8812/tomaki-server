package maki.chunksmithcontroller;

/** One measured interval. LOD writes mean successful native store writes, not queued work. */
public record AdaptiveSample(double seconds, double cpu, double mspt, double heap, double gc,
        double memoryHeadroomMb, long completions, long lodWrites, boolean storeAvailable,
        int nativeWidth, int inFlight, int lodQueue, long worldWriteQueue,
        long synchronousWrites, boolean nativeHeld, double latencyP95Ms, long oomKills, long throttledMicros) {}
