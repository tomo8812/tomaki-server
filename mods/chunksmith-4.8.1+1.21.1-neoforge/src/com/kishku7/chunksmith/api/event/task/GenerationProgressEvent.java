package com.kishku7.chunksmith.api.event.task;

import com.kishku7.chunksmith.event.Event;

public record GenerationProgressEvent(
   String world, long chunks, boolean complete, float progress, long hours, long minutes, long seconds, double rate, long x, long z
) implements Event {
}
