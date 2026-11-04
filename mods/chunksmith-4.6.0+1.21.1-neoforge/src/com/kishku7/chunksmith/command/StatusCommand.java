package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.lod.net.CsLodControl;
import com.kishku7.chunksmith.platform.Sender;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StatusCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public StatusCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      sender.sendMessagePrefixed("format_status_version", this.chunky.getVersion().toString());
      Map<String, GenerationTask> generationTasks = this.chunky.getGenerationTasks();
      if (generationTasks.isEmpty()) {
         sender.sendMessage("format_status_no_tasks");
      } else {
         generationTasks.values()
            .stream()
            .sorted(Comparator.comparing(task -> task.getProgress().getWorld()))
            .forEach(task -> task.getProgress().sendUpdate(sender));
      }

      sender.sendMessage("format_status_lod", CsLodControl.describe().orElse("not available on this platform"));

      for (String world : LodHorizon.armedWorlds()) {
         sender.sendMessage("format_status_horizon", world, LodHorizon.discarded(world));
      }
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return List.of();
   }
}
