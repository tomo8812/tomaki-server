package com.kishku7.chunksmith.api;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.api.event.task.GenerationCompleteEvent;
import com.kishku7.chunksmith.api.event.task.GenerationProgressEvent;
import com.kishku7.chunksmith.platform.World;
import com.kishku7.chunksmith.util.Input;
import com.kishku7.chunksmith.util.Parameter;
import java.util.function.Consumer;

public class ChunksmithAPIImpl implements ChunksmithAPI {
   private final Chunksmith chunky;

   public ChunksmithAPIImpl(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public int version() {
      return 0;
   }

   @Override
   public boolean isRunning(String world) {
      return this.chunky.getGenerationTasks().containsKey(world);
   }

   @Override
   public boolean startTask(String world, String shape, double centerX, double centerZ, double radiusX, double radiusZ, String pattern) {
      return this.startTask(world, shape, centerX, centerZ, radiusX, radiusZ, pattern, 0.0);
   }

   @Override
   public boolean startTask(String world, String shape, double centerX, double centerZ, double radiusX, double radiusZ, String pattern, double lodHorizon) {
      World implWorld = Input.tryWorld(this.chunky, world).orElse(null);
      if (implWorld == null) {
         return false;
      }

      if (this.chunky.getGenerationTasks().containsKey(world)) {
         return false;
      }

      Selection selection = Selection.builder(this.chunky, implWorld)
         .shape(shape)
         .center(centerX, centerZ)
         .radiusX(radiusX)
         .radiusZ(radiusZ)
         .pattern(Parameter.of(pattern))
         .horizon(Math.max(0.0, lodHorizon))
         .build();
      GenerationTask task = new GenerationTask(this.chunky, selection);
      this.chunky.getGenerationTasks().put(world, task);
      this.chunky.getScheduler().runTask(task);
      return true;
   }

   @Override
   public boolean pauseTask(String world) {
      GenerationTask task = this.chunky.getGenerationTasks().get(world);
      if (task == null) {
         return false;
      }

      task.stop(false);
      return true;
   }

   @Override
   public boolean continueTask(String world) {
      World implWorld = Input.tryWorld(this.chunky, world).orElse(null);
      if (implWorld == null) {
         return false;
      }

      GenerationTask task = this.chunky.getTaskLoader().loadTask(implWorld).orElse(null);
      if (task != null && !task.isCancelled()) {
         if (this.chunky.getGenerationTasks().containsKey(world)) {
            return false;
         }

         this.chunky.getGenerationTasks().put(world, task);
         this.chunky.getScheduler().runTask(task);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean cancelTask(String world) {
      World implWorld = Input.tryWorld(this.chunky, world).orElse(null);
      if (implWorld == null) {
         return false;
      }

      if (!this.chunky.getGenerationTasks().containsKey(world)) {
         return false;
      }

      this.chunky.getGenerationTasks().remove(world).stop(true);
      this.chunky.getTaskLoader().cancelTask(implWorld);
      return true;
   }

   @Override
   public void onGenerationProgress(Consumer<GenerationProgressEvent> event) {
      this.chunky.getEventBus().subscribe(GenerationProgressEvent.class, event);
   }

   @Override
   public void onGenerationComplete(Consumer<GenerationCompleteEvent> event) {
      this.chunky.getEventBus().subscribe(GenerationCompleteEvent.class, event);
   }
}
