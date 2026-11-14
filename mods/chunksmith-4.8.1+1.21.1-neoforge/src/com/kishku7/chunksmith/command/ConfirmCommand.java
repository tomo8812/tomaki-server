package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.platform.Sender;
import java.util.List;
import java.util.Optional;

public class ConfirmCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public ConfirmCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      Optional<Runnable> pendingAction = this.chunky.getPendingAction(sender);
      if (pendingAction.isEmpty()) {
         sender.sendMessagePrefixed("format_confirm");
      } else {
         pendingAction.get().run();
      }
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return List.of();
   }
}
