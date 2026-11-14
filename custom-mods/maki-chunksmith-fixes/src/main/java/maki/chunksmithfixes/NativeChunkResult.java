package maki.chunksmithfixes;

import java.util.concurrent.CompletionException;
import net.minecraft.server.level.ChunkResult;

public final class NativeChunkResult {
    private NativeChunkResult() {}
    /** Called after native ticket release/settle registration, before Void completion. */
    public static Void complete(ChunkResult<?> result) {
        if (result == null || !result.isSuccess()) {
            throw new CompletionException(new IllegalStateException(
                "Chunksmith FULL request failed: " + (result == null ? "null ChunkResult" : result.getError())));
        }
        return null;
    }
}
