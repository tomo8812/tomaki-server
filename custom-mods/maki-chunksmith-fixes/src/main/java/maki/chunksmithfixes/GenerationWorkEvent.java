package maki.chunksmithfixes;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.neoforged.bus.api.Event;

/** A producer owns the work; the controller only schedules and observes it. */
public final class GenerationWorkEvent extends Event {
    private final Supplier<CompletableFuture<Void>> start;
    public GenerationWorkEvent(Supplier<CompletableFuture<Void>> start){this.start=start;}
    public CompletableFuture<Void> start(){return start.get();}
}
