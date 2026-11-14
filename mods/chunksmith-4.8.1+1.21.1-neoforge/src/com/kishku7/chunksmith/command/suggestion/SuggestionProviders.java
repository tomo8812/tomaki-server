package com.kishku7.chunksmith.command.suggestion;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;

public final class SuggestionProviders {
   public static final SuggestionProvider<CommandSourceStack> PATTERNS = new PatternSuggestionProvider();
   public static final SuggestionProvider<CommandSourceStack> SHAPES = new ShapeSuggestionProvider();
   public static final SuggestionProvider<CommandSourceStack> TRIM_MODES = new TrimModeSuggestionProvider();
   public static final SuggestionProvider<CommandSourceStack> SETTINGS = new SettingSuggestionProvider();

   private SuggestionProviders() {
   }
}
