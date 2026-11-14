package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.iterator.PatternType;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.util.Input;
import com.kishku7.chunksmith.util.Parameter;
import com.kishku7.chunksmith.util.Translator;
import java.util.List;
import java.util.Optional;

public class PatternCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public PatternCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      Optional<String> optionalType = arguments.next().flatMap(Input::tryPattern);
      if (optionalType.isEmpty()) {
         sender.sendMessage("help_pattern");
      } else {
         String type = optionalType.get();
         Optional<String> value = arguments.next();
         if ("csv".equals(type) && value.isEmpty()) {
            sender.sendMessage("help_pattern");
         } else {
            Parameter pattern = Parameter.of(type, value.orElse(null));
            this.chunky.getSelection().pattern(pattern);
            sender.sendMessagePrefixed("format_pattern", Translator.translate("pattern_" + pattern.getType()));
         }
      }
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return arguments.size() == 1 ? PatternType.ALL : List.of();
   }
}
