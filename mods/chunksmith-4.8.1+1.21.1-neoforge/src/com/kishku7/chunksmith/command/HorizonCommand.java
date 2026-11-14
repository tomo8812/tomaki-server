package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.util.Formatting;
import com.kishku7.chunksmith.util.Input;
import java.util.List;
import java.util.Optional;

public class HorizonCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public HorizonCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      Optional<String> value = arguments.next();
      if (value.isEmpty()) {
         double current = this.chunky.getSelection().build().horizon();
         if (current > 0.0) {
            sender.sendMessagePrefixed("format_horizon", Formatting.number(current));
         } else {
            sender.sendMessagePrefixed("format_horizon_off");
         }

         sender.sendMessage("help_horizon");
      } else {
         String text = value.get();
         if (!"off".equalsIgnoreCase(text) && !"0".equals(text)) {
            Optional<Double> horizon = Input.tryDoubleSuffixed(text);
            if (!horizon.isEmpty() && !(horizon.get() <= 0.0) && !Input.isPastWorldLimit(horizon.get())) {
               this.chunky.getSelection().horizon(horizon.get());
               sender.sendMessagePrefixed("format_horizon", Formatting.number(horizon.get()));
            } else {
               sender.sendMessage("help_horizon");
            }
         } else {
            this.chunky.getSelection().horizon(0.0);
            sender.sendMessagePrefixed("format_horizon_off");
         }
      }
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return arguments.size() == 1 ? List.of("off") : List.of();
   }
}
