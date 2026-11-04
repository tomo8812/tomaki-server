package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.util.Input;
import java.util.List;
import java.util.Optional;

public class QuietCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public QuietCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      Optional<Integer> newQuiet = arguments.next().flatMap(Input::tryInteger);
      if (newQuiet.isEmpty()) {
         sender.sendMessage("help_quiet");
      } else {
         int quietInterval = Math.max(0, newQuiet.get());
         this.chunky.getConfig().setUpdateInterval(quietInterval);
         sender.sendMessagePrefixed("format_quiet", quietInterval);
      }
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return List.of();
   }
}
