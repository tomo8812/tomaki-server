package org.millenaire.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import org.millenaire.Millenaire;
import org.millenaire.world.SiteHunter;
import org.millenaire.world.VillageSpawnQueue;

public final class SitesCommand {
   private static final int MAX_LISTED = 10;

   private SitesCommand() {
   }

   public static void registerUnder(LiteralArgumentBuilder<CommandSourceStack> dev) {
      dev.then(Commands.literal("sites").executes(SitesCommand::run));
   }

   private static int run(CommandContext<CommandSourceStack> ctx) {
      CommandSourceStack source = (CommandSourceStack)ctx.getSource();
      VillageSpawnQueue queue = Millenaire.getSpawnQueue();
      if (SiteHunter.ENABLED && queue != null) {
         SiteHunter hunter = queue.hunter();
         source.sendSuccess(() -> Component.literal("[Sites] " + hunter.sitesStatus()).withStyle(ChatFormatting.GRAY), false);
         BlockPos from = BlockPos.containing(source.getPosition());
         List<String> sites = hunter.sitesNear(from, 10);
         if (sites.isEmpty()) {
            source.sendSuccess(() -> Component.literal("[Sites] no open passing site yet").withStyle(ChatFormatting.YELLOW), false);
         }

         for (String line : sites) {
            String[] xz = line.substring(0, line.indexOf(32)).split(",");
            String tp = "/tp @s " + xz[0] + " ~ " + xz[1];
            Component button = Component.literal(" [tp]")
               .withStyle(
                  s -> s.withColor(ChatFormatting.AQUA)
                     .withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, tp))
                     .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal(tp)))
               );
            source.sendSuccess(() -> Component.literal("[Sites] " + line).withStyle(ChatFormatting.GREEN).append(button), false);
         }

         return sites.size();
      } else {
         source.sendFailure(Component.literal("The site hunter is off (start with -PsiteHunter=true)"));
         return 0;
      }
   }
}
