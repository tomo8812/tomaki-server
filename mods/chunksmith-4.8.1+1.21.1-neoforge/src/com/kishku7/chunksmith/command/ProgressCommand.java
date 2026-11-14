package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.platform.Sender;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ProgressCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public ProgressCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      Map<String, GenerationTask> generationTasks = this.chunky.getGenerationTasks();
      if (generationTasks.isEmpty()) {
         sender.sendMessagePrefixed("format_progress_no_tasks");
      } else {
         generationTasks.values()
            .stream()
            .sorted(Comparator.comparing(task -> task.getProgress().getWorld()))
            .forEach(task -> task.getProgress().sendUpdate(sender));
      }
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return List.of();
   }
}
