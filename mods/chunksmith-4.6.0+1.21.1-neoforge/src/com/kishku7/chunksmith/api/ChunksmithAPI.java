package com.kishku7.chunksmith.api;

import com.kishku7.chunksmith.api.event.task.GenerationCompleteEvent;
import com.kishku7.chunksmith.api.event.task.GenerationProgressEvent;
import java.util.function.Consumer;

public interface ChunksmithAPI {
   int version();

   boolean isRunning(String var1);

   boolean startTask(String var1, String var2, double var3, double var5, double var7, double var9, String var11);

   boolean startTask(String var1, String var2, double var3, double var5, double var7, double var9, String var11, double var12);

   boolean pauseTask(String var1);

   boolean continueTask(String var1);

   boolean cancelTask(String var1);

   void onGenerationProgress(Consumer<GenerationProgressEvent> var1);

   void onGenerationComplete(Consumer<GenerationCompleteEvent> var1);
}
