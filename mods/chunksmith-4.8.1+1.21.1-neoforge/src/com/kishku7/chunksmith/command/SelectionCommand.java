package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.util.Formatting;
import com.kishku7.chunksmith.util.Translator;
import java.util.List;

public class SelectionCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public SelectionCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      Selection current = this.chunky.getSelection().build();
      sender.sendMessagePrefixed("format_selection");
      sender.sendMessage("format_selection_world", current.world().getName());
      sender.sendMessage("format_selection_shape", Translator.translate("shape_" + current.shape()));
      sender.sendMessage("format_selection_center", Formatting.number(current.centerX()), Formatting.number(current.centerZ()));
      double radiusX = current.radiusX();
      double radiusZ = current.radiusZ();
      if (radiusX == radiusZ) {
         sender.sendMessage("format_selection_radius", Formatting.number(radiusX));
      } else {
         sender.sendMessage("format_selection_radii", Formatting.number(radiusX), Formatting.number(radiusZ));
      }
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return List.of();
   }
}
