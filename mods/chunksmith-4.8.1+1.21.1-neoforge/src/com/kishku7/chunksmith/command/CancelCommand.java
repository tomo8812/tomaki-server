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

public class CancelCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public CancelCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      Map<String, GenerationTask> generationTasks = this.chunky.getGenerationTasks();
      Map<String, TrimCommand.Task> trimTasks = this.chunky.getTrimTasks();
      if (generationTasks.isEmpty() && this.chunky.getTaskLoader().loadTasks().stream().allMatch(GenerationTask::isCancelled) && trimTasks.isEmpty()) {
         sender.sendMessagePrefixed("format_cancel_no_tasks");
      } else {
         Runnable cancelAction;
         if (arguments.size() > 0) {
            Optional<World> world = Input.tryWorld(this.chunky, arguments.joined());
            if (world.isEmpty()) {
               sender.sendMessage("help_cancel");
               return;
            }

            cancelAction = () -> {
               sender.sendMessagePrefixed("format_cancel", world.get().getName());
               this.chunky.getTaskLoader().cancelTask(world.get());
               if (this.chunky.getGenerationTasks().containsKey(world.get().getName())) {
                  this.chunky.getGenerationTasks().remove(world.get().getName()).stop(true);
               }

               if (this.chunky.getTrimTasks().containsKey(world.get().getName())) {
                  this.chunky.getTrimTasks().remove(world.get().getName()).setCancelled(true);
               }
            };
         } else {
            cancelAction = () -> {
               sender.sendMessagePrefixed("format_cancel_all");
               this.chunky.getTaskLoader().cancelTasks();
               this.chunky.getGenerationTasks().values().forEach(generationTask -> generationTask.stop(true));
               this.chunky.getGenerationTasks().clear();
               this.chunky.getScheduler().cancelTasks();
               this.chunky.getTrimTasks().values().forEach(trimTask -> trimTask.setCancelled(true));
               this.chunky.getTrimTasks().clear();
            };
         }

         this.chunky.setPendingAction(sender, cancelAction);
         sender.sendMessagePrefixed("format_cancel_confirm", "/cs confirm");
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
