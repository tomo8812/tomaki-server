package com.kishku7.chunksmith.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;

public class TrimModeSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
   private static final List<String> TRIM_MODES = List.of("inside", "outside");

   public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
      try {
         String input = (String)context.getArgument("trimMode", String.class);
         TRIM_MODES.forEach(shape -> {
            if (shape.contains(input.toLowerCase())) {
               builder.suggest(shape);
            }
         });
      } catch (IllegalArgumentException e) {
         TRIM_MODES.forEach(builder::suggest);
      }

      return builder.buildFuture();
   }
}
