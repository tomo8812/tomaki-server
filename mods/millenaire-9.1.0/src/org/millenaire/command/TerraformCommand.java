package org.millenaire.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.millenaire.config.MillenaireServerConfig;
import org.millenaire.culture.ModCultures;
import org.millenaire.village.TerraformProject;
import org.millenaire.village.Village;
import org.millenaire.village.VillageSavedData;
import org.millenaire.village.VillageTerraformManager;
import org.millenaire.world.PlacedLocation;
import org.millenaire.world.TerraformJobCompiler;
import org.millenaire.world.TerraformPlanner;

public final class TerraformCommand {
   private static final SuggestionProvider<CommandSourceStack> PLAN_SET_SUGGESTIONS = (ctx, builder) -> SharedSuggestionProvider.suggest(
      ModCultures.getAllBuildingPlanSets().keySet().stream().map(ResourceLocation::getPath), builder
   );

   private TerraformCommand() {
   }

   public static void registerUnder(LiteralArgumentBuilder<CommandSourceStack> parent) {
      parent.then(
         ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                       "terraform"
                                    )
                                    .then(Commands.literal("status").executes(TerraformCommand::status)))
                                 .then(Commands.literal("enable").then(Commands.argument("on", BoolArgumentType.bool()).executes(TerraformCommand::enable))))
                              .then(
                                 Commands.literal("profile")
                                    .then(
                                       Commands.argument("name", StringArgumentType.word())
                                          .suggests(
                                             (c, b) -> SharedSuggestionProvider.suggest(List.of("base", "extended", "extended-open", "mountain", "max"), b)
                                          )
                                          .executes(TerraformCommand::profile)
                                    )
                              ))
                           .then(
                              Commands.literal("dryrun")
                                 .then(
                                    Commands.argument("plan_set", ResourceLocationArgument.id())
                                       .suggests(PLAN_SET_SUGGESTIONS)
                                       .executes(TerraformCommand::dryRun)
                                 )
                           ))
                        .then(
                           Commands.literal("plan")
                              .then(
                                 Commands.argument("plan_set", ResourceLocationArgument.id()).suggests(PLAN_SET_SUGGESTIONS).executes(TerraformCommand::plan)
                              )
                        ))
                     .then(Commands.literal("rush").executes(TerraformCommand::rush)))
                  .then(Commands.literal("verify").executes(TerraformCommand::verify)))
               .then(Commands.literal("cancel").executes(TerraformCommand::cancel)))
            .then(
               Commands.literal("snapshot")
                  .then(Commands.argument("plan_set", ResourceLocationArgument.id()).suggests(PLAN_SET_SUGGESTIONS).executes(TerraformCommand::snapshot))
            )
      );
   }

   private static int status(CommandContext<CommandSourceStack> ctx) {
      Village v = nearestVillage(ctx);
      if (v == null) {
         return 0;
      }

      TerraformProject p = v.getTerraformProject();
      int[] h = v.getTerraformHistory();
      String history = String.format(" | history planned=%d finished=%d clean=%d handed=%d", h[0], h[1], h[2], h[3]);
      if (p == null) {
         say(ctx, v.getVillageName() + ": no earthwork" + (VillageTerraformManager.isEnabled() ? "" : " (villagerTerraforming is off)") + history);
         return 1;
      }

      say(
         ctx,
         String.format(
            "%s: %s for %s at %s pad y=%d — %d/%d ops, cut %d fill %d, %d worker(s)%s%s",
            v.getVillageName(),
            p.status(),
            p.planSetId(),
            p.location().position().toShortString(),
            p.padY(),
            p.doneOps(),
            p.totalOps(),
            p.cutVolume(),
            p.fillVolume(),
            p.activeWorkers(),
            p.status() == TerraformProject.Status.READY
               ? " — " + VillageTerraformManager.describeReadyBlocker(((CommandSourceStack)ctx.getSource()).getLevel(), v)
               : "",
            history
         )
      );
      ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();

      for (Entry<UUID, List<Integer>> claim : p.claimsSnapshot().entrySet()) {
         if (!claim.getValue().isEmpty()) {
            TerraformJobCompiler.Op op = p.op(claim.getValue().get(0));
            Entity worker = level.getEntity(claim.getKey());
            String at = worker == null
               ? "unloaded"
               : worker.blockPosition().toShortString() + String.format(", %.0f blocks away", Math.sqrt(worker.blockPosition().distSqr(op.pos())));
            say(
               ctx,
               String.format(
                     "  %s: %d ops held, next %s %s at %s",
                     claim.getKey().toString().substring(0, 8),
                     claim.getValue().size(),
                     op.kind(),
                     op.cut() ? "cut" : "fill",
                     op.pos().toShortString()
                  )
                  + " — worker at "
                  + at
            );
         }
      }

      return 1;
   }

   private static int enable(CommandContext<CommandSourceStack> ctx) {
      boolean on = BoolArgumentType.getBool(ctx, "on");
      MillenaireServerConfig.SERVER.villagerTerraforming.set(on);
      MillenaireServerConfig.SERVER.villagerTerraforming.save();
      say(ctx, "villagerTerraforming = " + on);
      return 1;
   }

   private static int profile(CommandContext<CommandSourceStack> ctx) {
      String name = StringArgumentType.getString(ctx, "name");
      if (!VillageTerraformManager.applyProfile(name)) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("unknown profile " + name + " (base, extended, extended-open, mountain, max)"));
         return 0;
      } else {
         MillenaireServerConfig c = MillenaireServerConfig.SERVER;
         say(
            ctx,
            String.format(
               "profile %s: volume %d, reach %d, altitude gate ±%d, fill water %s (depth ≤ %d, open water %s), retaining wall %d",
               name,
               c.terraformMaxVolume.get(),
               c.terraformMaxReach.get(),
               c.terraformAltitudeGate.get(),
               c.terraformFillWater.get(),
               c.terraformMaxWaterDepth.get(),
               c.terraformReclaimOpenWater.get(),
               c.terraformRetainingWall.get()
            )
         );
         return 1;
      }
   }

   private static int dryRun(CommandContext<CommandSourceStack> ctx) {
      Village v = nearestVillage(ctx);
      if (v == null) {
         return 0;
      }

      ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
      VillageTerraformManager.SiteQuery q = query(ctx, level, v);
      if (q == null) {
         return 0;
      }

      PlacedLocation direct = q.findWithoutEarthwork(v);
      if (direct != null) {
         say(ctx, q.planSet().id() + " fits without earthwork at " + direct.position().toShortString());
      }

      TerraformPlanner.Result r = VillageTerraformManager.planOnly(level, v, q.plan(), q.map(), q.constraints(), q.margins());
      say(ctx, describe(r));
      return r.plan() != null ? 1 : 0;
   }

   private static int plan(CommandContext<CommandSourceStack> ctx) {
      Village v = nearestVillage(ctx);
      if (v == null) {
         return 0;
      } else if (!VillageTerraformManager.isEnabled()) {
         ((CommandSourceStack)ctx.getSource())
            .sendFailure(Component.literal("villagerTerraforming is off — the project would be dropped on the next growth tick"));
         return 0;
      } else if (v.getTerraformProject() != null) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("village already has an earthwork (cancel it first)"));
         return 0;
      } else {
         ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
         VillageTerraformManager.SiteQuery q = query(ctx, level, v);
         if (q == null) {
            return 0;
         } else {
            TerraformProject p = VillageTerraformManager.plan(
               level, v, q.planSet(), q.levelDef(), q.variant(), q.plan(), q.map(), q.constraints(), q.margins(), level.getGameTime()
            );
            if (p == null) {
               ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("no earthwork found (see server log)"));
               return 0;
            } else {
               return status(ctx);
            }
         }
      }
   }

   private static int rush(CommandContext<CommandSourceStack> ctx) {
      Village v = nearestVillage(ctx);
      if (v == null) {
         return 0;
      } else if (v.getTerraformProject() == null) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("no earthwork to rush"));
         return 0;
      } else {
         int changed = VillageTerraformManager.rush(((CommandSourceStack)ctx.getSource()).getLevel(), v);
         say(ctx, "rushed: " + changed + " blocks changed, site READY");
         return changed;
      }
   }

   private static int verify(CommandContext<CommandSourceStack> ctx) {
      Village v = nearestVillage(ctx);
      if (v == null) {
         return 0;
      } else {
         TerraformProject p = v.getTerraformProject();
         if (p == null) {
            ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("no earthwork to verify"));
            return 0;
         } else {
            say(
               ctx,
               v.getVillageName()
                  + ": "
                  + p.doneOps()
                  + "/"
                  + p.totalOps()
                  + " done, world check: "
                  + VillageTerraformManager.verify(((CommandSourceStack)ctx.getSource()).getLevel(), p)
            );
            return 1;
         }
      }
   }

   private static int cancel(CommandContext<CommandSourceStack> ctx) {
      Village v = nearestVillage(ctx);
      if (v == null) {
         return 0;
      }

      boolean had = v.getTerraformProject() != null;
      v.setTerraformProject(null);
      say(ctx, had ? "earthwork dropped" : "no earthwork");
      return had ? 1 : 0;
   }

   private static int snapshot(CommandContext<CommandSourceStack> ctx) {
      Village v = nearestVillage(ctx);
      if (v == null) {
         return 0;
      }

      ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
      VillageTerraformManager.SiteQuery q = query(ctx, level, v);
      if (q == null) {
         return 0;
      }

      String name = v.getVillageName().replaceAll("[^A-Za-z0-9_-]", "_")
         + "-"
         + q.planSet().id().getPath().replace('/', '_')
         + "-"
         + level.getGameTime()
         + ".snapshot.gz";
      Path file = ((CommandSourceStack)ctx.getSource()).getServer().getServerDirectory().resolve("millenaire-terraform").resolve(name);

      try {
         q.snapshot(level, v).write(file);
      } catch (IOException e) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("snapshot failed: " + e.getMessage()));
         return 0;
      }

      say(ctx, "snapshot written to " + file.toAbsolutePath());
      return 1;
   }

   private static String describe(TerraformPlanner.Result r) {
      TerraformPlanner.Plan p = r.plan();
      return p == null
         ? "no earthwork: " + r.reason() + " | " + r.stats()
         : String.format(
            "earthwork at %s pad y=%d: cut %d, fill %d over %d columns (%d water), slopes cut %.2f / fill %.2f, walls cut %d / fill %d, ramp %d, gate ±%d | %s",
            p.location().position().toShortString(),
            p.padY(),
            p.cutVolume(),
            p.fillVolume(),
            p.modifiedColumns(),
            p.waterColumns(),
            p.cutSlope(),
            p.fillSlope(),
            p.cutWall(),
            p.fillWall(),
            p.rampColumns(),
            p.gate(),
            r.stats()
         );
   }

   private static VillageTerraformManager.SiteQuery query(CommandContext<CommandSourceStack> ctx, ServerLevel level, Village v) {
      ResourceLocation raw = ResourceLocationArgument.getId(ctx, "plan_set");
      ResourceLocation id = raw.getNamespace().equals("minecraft") ? ResourceLocation.fromNamespaceAndPath("millenaire", raw.getPath()) : raw;
      VillageTerraformManager.SiteQuery q = VillageTerraformManager.buildQuery(level, v, id);
      if (q == null) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("unknown plan set " + id));
      }

      return q;
   }

   private static Village nearestVillage(CommandContext<CommandSourceStack> ctx) {
      CommandSourceStack src = (CommandSourceStack)ctx.getSource();
      ServerPlayer player = src.getPlayer();
      BlockPos pos = player != null ? player.blockPosition() : BlockPos.containing(src.getPosition());
      Village v = VillageSavedData.get(src.getLevel()).getVillageManager().findNearestVillage(pos, 5000.0);
      if (v == null) {
         src.sendFailure(Component.translatable("command.millenaire.error.no_village_radius", new Object[]{5000}));
      }

      return v;
   }

   private static void say(CommandContext<CommandSourceStack> ctx, String message) {
      ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal(message), false);
   }
}
