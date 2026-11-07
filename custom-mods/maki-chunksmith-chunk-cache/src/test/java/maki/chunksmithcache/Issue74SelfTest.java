package maki.chunksmithcache;

import java.util.concurrent.atomic.AtomicInteger;

public final class Issue74SelfTest {
    public static void main(String[] args) throws Exception {
        AtomicInteger reservations = new AtomicInteger();
        var rejected = BackfillAdmission.evaluate(false, () -> "dependency-scope-loaded", () -> {
            reservations.incrementAndGet(); return true;
        });
        require(!rejected.ready() && !rejected.afterDrain() && reservations.get()==0, "preflight must not reserve");
        AtomicInteger checks = new AtomicInteger();
        var race = BackfillAdmission.evaluate(false,
            () -> checks.incrementAndGet()==1 ? null : "dependency-scope-loaded", () -> true);
        require(!race.ready() && race.afterDrain() && checks.get()==2, "race must reject after drain");
        checks.set(0);
        var heldRace = BackfillAdmission.evaluate(true, () -> {
            checks.incrementAndGet(); return "generated-or-pending";
        }, () -> true);
        require(!heldRace.ready() && heldRace.afterDrain() && checks.get()==1, "held reservation must recheck");
        var wait = BackfillAdmission.evaluate(false, () -> null, () -> false);
        require(!wait.ready() && wait.rejection()==null, "drain wait must not start");
        var ready = BackfillAdmission.evaluate(false, () -> null, () -> true);
        require(ready.ready() && ready.afterDrain(), "safe candidate should start");
        LatencyHistogram times = new LatencyHistogram();
        times.record(1_000_000); times.record(4_000_000); times.record(1_000_000_001L);
        require(times.percentile(.5)==4 && times.percentile(.95)==4000, "histogram upper bounds");
        require(times.toString().contains("over1000=1"), "threshold nanosecond precision");
        times.record(Long.MAX_VALUE);
        require(times.percentile(1)==Long.MAX_VALUE, "overflow bucket");
        String world = bytecode("com.kishku7.chunksmith.platform.NeoForgeWorld");
        String task = bytecode("com.kishku7.chunksmith.GenerationTask");
        require(section(world, "private void lambda$getChunkAtAsync$8(").contains("LodSupport.offer:"), "exact FULL offer call site");
        require(occurrences(section(world, "public java.util.concurrent.CompletableFuture<java.lang.Void> getChunkAtAsync("), "Method ticketSafePoint:")==2, "ticket-safe-point call sites");
        require(occurrences(section(task, "private java.util.concurrent.CompletionStage lambda$run$1("), "World.getChunkAtAsync:")==2, "adapter request call sites");
        String completion = section(task, "private void lambda$run$2(");
        require(completion.contains("System.currentTimeMillis:") && completion.contains("Method adjustFromChunkLatency:(J)V"), "native dispatch wall-time boundary");
        require(section(task, "private void adjustFromChunkLatency(").contains("Field maxChunkMillis:J"), "native slow threshold field");
        System.out.println("Issue74SelfTest passed (admission/race/histogram/installed Chunksmith ABI)");
    }
    private static String bytecode(String name) throws Exception {
        String javap = java.nio.file.Path.of(System.getProperty("java.home"), "bin", "javap").toString();
        Process process = new ProcessBuilder(javap, "-classpath", System.getProperty("issue74.chunksmithJar"), "-c", "-p", name)
            .redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        require(process.waitFor()==0, "javap must succeed");
        return output;
    }
    private static String section(String text, String header) {
        int start = text.indexOf(header);
        require(start>=0, "missing method " + header);
        int end = text.indexOf("\n  private ", start+header.length());
        int publicEnd = text.indexOf("\n  public ", start+header.length());
        if (end<0 || publicEnd>=0 && publicEnd<end) end=publicEnd;
        return end<0 ? text.substring(start) : text.substring(start,end);
    }
    private static int occurrences(String text, String needle) {
        return (text.length()-text.replace(needle, "").length())/needle.length();
    }
    private static void require(boolean value, String reason) {
        if (!value) throw new AssertionError(reason);
    }
}
