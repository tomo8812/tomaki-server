package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.platform.World;
import com.kishku7.chunksmith.util.Input;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContinueCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public ContinueCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      List<GenerationTask> loadTasks;
      if (arguments.size() > 0) {
         Optional<World> world = Input.tryWorld(this.chunky, arguments.joined());
         if (world.isEmpty()) {
            sender.sendMessage("help_continue");
            return;
         }

         loadTasks = this.chunky.getTaskLoader().loadTask(world.get()).map(List::of).orElse(List.of());
      } else {
         loadTasks = this.chunky.getTaskLoader().loadTasks();
      }

      if (loadTasks.stream().allMatch(GenerationTask::isCancelled)) {
         sender.sendMessagePrefixed("format_continue_no_tasks");
      } else {
         Map<String, GenerationTask> generationTasks = this.chunky.getGenerationTasks();
         loadTasks.stream().filter(task -> !task.isCancelled()).forEach(generationTask -> {
            World world = generationTask.getSelection().world();
            if (!generationTasks.containsKey(world.getName())) {
               generationTasks.put(world.getName(), generationTask);
               this.chunky.getScheduler().runTask(generationTask);
               sender.sendMessagePrefixed("format_continue", world.getName());
            } else if (generationTasks.get(world.getName()).isStopping()) {
               sender.sendMessagePrefixed("format_task_stopping", world.getName());
            } else {
               sender.sendMessagePrefixed("format_started_already", world.getName());
            }
         });
      }
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      if (arguments.size() == 1) {
         List<String> suggestions = new ArrayList<>();
         this.chunky.getServer().getWorlds().forEach(world -> suggestions.add(world.getName()));
         return suggestions;
      } else {
         return List.of();
      }
   }
}
