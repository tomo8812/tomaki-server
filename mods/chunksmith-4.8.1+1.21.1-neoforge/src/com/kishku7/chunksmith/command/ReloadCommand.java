package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.event.command.ReloadCommandEvent;
import com.kishku7.chunksmith.platform.Config;
import com.kishku7.chunksmith.platform.Sender;
import java.util.List;

public class ReloadCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public ReloadCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      String type = arguments.next().orElse(null);
      if ("tasks".equals(type)) {
         if (!this.chunky.getGenerationTasks().isEmpty()) {
            sender.sendMessagePrefixed("format_reload_tasks_running");
            return;
         }

         this.chunky.getTaskLoader().reload();
      } else {
         Config config = this.chunky.getServer().getConfig();
         config.reload();
         this.chunky.setLanguage(config.getLanguage());
         this.chunky.getEventBus().call(new ReloadCommandEvent());
      }

      sender.sendMessagePrefixed("format_reload");
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return arguments.size() == 1 ? List.of("config", "tasks") : List.of();
   }
}
