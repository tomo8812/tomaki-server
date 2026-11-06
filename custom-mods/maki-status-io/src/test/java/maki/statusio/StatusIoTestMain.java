package maki.statusio;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class StatusIoTestMain {
    public static void main(String[] args) throws Exception {
        coalescesWhileSlowWriteIsBlocked();
        publishesAtomicallyToRealFiles();
        System.out.println("StatusIo tests: PASS");
    }

    private static void coalescesWhileSlowWriteIsBlocked() throws Exception {
        CountDownLatch firstStarted = new CountDownLatch(1);
        CountDownLatch releaseFirst = new CountDownLatch(1);
        List<String> writes = java.util.Collections.synchronizedList(new ArrayList<>());
        StatusIoEngine.Sink sink = (destination, contents) -> {
            writes.add(contents);
            if (writes.size() == 1) {
                firstStarted.countDown();
                if (!releaseFirst.await(2, TimeUnit.SECONDS)) throw new AssertionError("test release timeout");
            }
            return new StatusIoEngine.WriteStats(0L, 0L);
        };
        StatusIoEngine engine = new StatusIoEngine(
            "maki-status-io-test-slow", 4, 10_000L, sink, System.getLogger("MakiStatusIoTest"));
        Path destination = Path.of("status.json");

        require(engine.publish(destination, "one"), "first snapshot rejected");
        require(firstStarted.await(2, TimeUnit.SECONDS), "worker did not start");
        long started = System.nanoTime();
        require(engine.publish(destination, "two"), "second snapshot rejected");
        require(engine.publish(destination, "three"), "third snapshot rejected");
        long submitMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);
        require(submitMillis < 250L, "publish blocked behind slow I/O for " + submitMillis + "ms");
        releaseFirst.countDown();
        require(engine.flush(2_000L), "flush timed out");
        require(writes.equals(List.of("one", "three")), "expected [one, three], got " + writes);
    }

    private static void publishesAtomicallyToRealFiles() throws Exception {
        Path directory = Files.createTempDirectory("maki-status-io-test");
        try {
            Path destination = directory.resolve("status.json");
            StatusIoEngine engine = new StatusIoEngine(
                "maki-status-io-test-file", 4, 10_000L, StatusIo::writeSnapshot,
                System.getLogger("MakiStatusIoTest"));
            require(engine.publish(destination, "{\"sequence\":1}\n"), "first snapshot rejected");
            require(engine.flush(2_000L), "first flush timed out");
            require(Files.readString(destination).equals("{\"sequence\":1}\n"), "first JSON mismatch");
            require(engine.publish(destination, "{\"sequence\":2}\n"), "second snapshot rejected");
            require(engine.flush(2_000L), "second flush timed out");
            require(Files.readString(destination).equals("{\"sequence\":2}\n"), "latest JSON mismatch");
            require(!Files.exists(directory.resolve("status.json.tmp")), "temporary file left after success");
        } finally {
            try (var paths = Files.walk(directory)) {
                paths.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (Exception ignored) {}
                });
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
