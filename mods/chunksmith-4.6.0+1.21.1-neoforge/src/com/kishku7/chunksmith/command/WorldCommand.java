package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.platform.Player;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.platform.World;
import com.kishku7.chunksmith.util.Input;
import java.util.ArrayList;
import java.util.List;

public class WorldCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public WorldCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      World world;
      if (arguments.size() == 0 && sender instanceof Player player) {
         world = player.getWorld();
      } else {
         world = Input.tryWorld(this.chunky, arguments.joined()).orElse(null);
      }

      if (world == null) {
         sender.sendMessage("help_world");
      } else {
         this.chunky.getSelection().world(world);
         sender.sendMessagePrefixed("format_world", world.getName());
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
