package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.shape.ShapeType;
import com.kishku7.chunksmith.util.Input;
import com.kishku7.chunksmith.util.Translator;
import java.util.List;
import java.util.Optional;

public class ShapeCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public ShapeCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      Optional<String> inputShape = arguments.next().flatMap(Input::tryShape);
      if (inputShape.isEmpty()) {
         sender.sendMessage("help_shape");
      } else {
         String shape = inputShape.get();
         this.chunky.getSelection().shape(shape);
         sender.sendMessagePrefixed("format_shape", Translator.translate("shape_" + shape));
      }
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return arguments.size() == 1 ? ShapeType.all() : List.of();
   }
}
