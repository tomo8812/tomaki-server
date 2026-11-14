package com.kishku7.chunksmith.api.event.task;

import com.kishku7.chunksmith.event.Event;

public record GenerationCompleteEvent(String world) implements Event {
}
