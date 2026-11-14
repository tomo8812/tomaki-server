package org.millenaire.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.millenaire.village.BuildingFinalizer;
import org.millenaire.village.Village;
import org.millenaire.village.VillageGrowthManager;
import org.millenaire.village.VillageSavedData;

public final class GrowCommand {
   private static final int MAX_MISSES = 10;

   private GrowCommand() {
   }

   public static void registerUnder(LiteralArgumentBuilder<CommandSourceStack> parent) {
      parent.then(Commands.literal("grow").then(Commands.argument("n", IntegerArgumentType.integer(1, 200)).executes(GrowCommand::execute)));
   }

   private static int execute(CommandContext<CommandSourceStack> ctx) {
      CommandSourceStack src = (CommandSourceStack)ctx.getSource();
      int n = IntegerArgumentType.getInteger(ctx, "n");
      ServerLevel level = src.getLevel();
      ServerPlayer player = src.getPlayer();
      BlockPos searchPos = player != null ? player.blockPosition() : BlockPos.containing(src.getPosition());
      Village v = VillageSavedData.get(level).getVillageManager().findNearestVillage(searchPos, 5000.0);
      if (v == null) {
         src.sendFailure(Component.translatable("command.millenaire.error.no_village_radius", new Object[]{5000}));
         return 0;
      }

      Set<ResourceLocation> noSpot = new HashSet<>();
      int placed = 0;
      int misses = 0;

      while (placed < n && VillageGrowthManager.rushCandidateCount(level, v, noSpot) > 0) {
         int before = noSpot.size();
         boolean ok = VillageGrowthManager.rushOneProject(level, v, null, noSpot);
         if (ok) {
            placed++;
            misses = 0;
            v.getPathManager().markPathsDirty();
         } else if (noSpot.size() == before) {
            if (++misses >= 10) {
               break;
            }
         }
      }

      boolean exhausted = VillageGrowthManager.rushCandidateCount(level, v, noSpot) == 0;
      if (placed > 0) {
         BuildingFinalizer.applyVillageUpdates(level, v);
      }

      v.markDirty();
      int p = placed;
      int skipped = n - placed;
      src.sendSuccess(() -> Component.translatable("command.millenaire.grow.success", new Object[]{p, skipped}), false);
      String state;
      if (!noSpot.isEmpty()) {
         state = "No spot for: "
            + noSpot.stream().<CharSequence>map(ResourceLocation::getPath).sorted().collect(Collectors.joining(", "))
            + (exhausted ? ". Nothing else left to build." : ".");
      } else if (exhausted) {
         state = "Nothing left to build.";
      } else if (placed < n) {
         state = "Stopped: 10 projects in a row failed for another reason than space.";
      } else {
         state = "More projects remain.";
      }

      src.sendSuccess(() -> Component.literal(state), false);
      return placed;
   }
}
