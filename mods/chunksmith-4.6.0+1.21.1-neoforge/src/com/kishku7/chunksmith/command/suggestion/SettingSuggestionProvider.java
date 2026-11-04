package com.kishku7.chunksmith.command.suggestion;

import com.kishku7.chunksmith.command.ConfigSettings;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;

public class SettingSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
   public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
      try {
         String input = ((String)context.getArgument("type", String.class)).toLowerCase(Locale.ROOT);
         ConfigSettings.names().forEach(name -> {
            if (name.toLowerCase(Locale.ROOT).contains(input)) {
               builder.suggest(name);
            }
         });
      } catch (IllegalArgumentException e) {
         ConfigSettings.names().forEach(builder::suggest);
      }

      return builder.buildFuture();
   }
}
