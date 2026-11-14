package org.millenaire.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.millenaire.world.sitescore.SiteScoreLog;

public final class SiteLabelCommand {
   private static final List<String> LABELS = List.of("good", "ok", "bad");

   private SiteLabelCommand() {
   }

   public static void registerUnder(LiteralArgumentBuilder<CommandSourceStack> dev) {
      dev.then(
         Commands.literal("sitelabel")
            .then(
               Commands.argument("x", IntegerArgumentType.integer())
                  .then(
                     Commands.argument("z", IntegerArgumentType.integer())
                        .then(
                           Commands.argument("label", StringArgumentType.word())
                              .suggests((ctx, b) -> SharedSuggestionProvider.suggest(LABELS, b))
                              .then(Commands.argument("category", StringArgumentType.word()).executes(SiteLabelCommand::run))
                        )
                  )
            )
      );
   }

   private static int run(CommandContext<CommandSourceStack> ctx) {
      CommandSourceStack source = (CommandSourceStack)ctx.getSource();
      int x = IntegerArgumentType.getInteger(ctx, "x");
      int z = IntegerArgumentType.getInteger(ctx, "z");
      String label = StringArgumentType.getString(ctx, "label");
      String category = StringArgumentType.getString(ctx, "category");
      if (!LABELS.contains(label)) {
         source.sendFailure(Component.literal("Label must be one of " + LABELS));
         return 0;
      }

      SiteScoreLog.logLabel(source.getLevel(), x, z, label, category, source.getTextName());

      ChatFormatting colour = switch (label) {
         case "good" -> ChatFormatting.GREEN;
         case "bad" -> ChatFormatting.RED;
         case "ok" -> ChatFormatting.GOLD;
         default -> ChatFormatting.GRAY;
      };
      source.sendSuccess(() -> Component.literal("[Site] (x " + x + ", z " + z + ") labelled " + label + " (was " + category + ")").withStyle(colour), false);
      return 1;
   }
}
