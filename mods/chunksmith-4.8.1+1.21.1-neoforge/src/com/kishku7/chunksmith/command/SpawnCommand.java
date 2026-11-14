package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.util.Formatting;
import java.util.List;

public class SpawnCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public SpawnCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      this.chunky.getSelection().spawn();
      Selection current = this.chunky.getSelection().build();
      sender.sendMessagePrefixed("format_center", Formatting.number(current.centerX()), Formatting.number(current.centerZ()));
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return List.of();
   }
}
