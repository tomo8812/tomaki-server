package com.kishku7.chunksmith.command.suggestion;

import com.kishku7.chunksmith.iterator.PatternType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;

public class PatternSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
   public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
      try {
         String input = (String)context.getArgument("pattern", String.class);
         PatternType.ALL.forEach(pattern -> {
            if (pattern.contains(input.toLowerCase())) {
               builder.suggest(pattern);
            }
         });
      } catch (IllegalArgumentException e) {
         PatternType.ALL.forEach(builder::suggest);
      }

      return builder.buildFuture();
   }
}
