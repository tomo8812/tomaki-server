package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.util.Translator;
import java.util.List;

public class SilentCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public SilentCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      this.chunky.getConfig().setSilent(!this.chunky.getConfig().isSilent());
      String status = Translator.translate(this.chunky.getConfig().isSilent() ? "enabled" : "disabled");
      sender.sendMessagePrefixed("format_silent", status);
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return List.of();
   }
}
