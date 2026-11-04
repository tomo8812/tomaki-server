package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.platform.World;
import com.kishku7.chunksmith.util.AutoPause;
import com.kishku7.chunksmith.util.Input;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PauseCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public PauseCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      AutoPause.clear();
      Map<String, GenerationTask> generationTasks = this.chunky.getGenerationTasks();
      if (generationTasks.isEmpty()) {
         sender.sendMessagePrefixed("format_pause_no_tasks");
      } else if (arguments.size() > 0) {
         Optional<World> world = Input.tryWorld(this.chunky, arguments.joined());
         if (!world.isEmpty() && generationTasks.containsKey(world.get().getName())) {
            generationTasks.get(world.get().getName()).stop(false);
            sender.sendMessagePrefixed("format_pause", world.get().getName());
         } else {
            sender.sendMessage("help_pause");
         }
      } else {
         for (GenerationTask generationTask : this.chunky.getGenerationTasks().values()) {
            generationTask.stop(false);
            sender.sendMessagePrefixed("format_pause", generationTask.getSelection().world().getName());
         }
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
