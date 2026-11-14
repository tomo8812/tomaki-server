package dev.diorama.waypoint;

import com.mojang.brigadier.tree.CommandNode;
import dev.diorama.MapSession;
import dev.diorama.Settings;
import java.time.LocalTime;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public final class Waypoints {
   private static boolean wasSleeping;
   public static final double DEATH_REACHED = 4.0;
   private static final long DEATH_GRACE_MS = 5000L;

   private Waypoints() {
   }

   public static @Nullable String dimension() {
      Minecraft mc = Minecraft.getInstance();
      return mc.level == null ? null : mc.level.dimension().location().toString();
   }

   public static @Nullable Waypoint atPlayer() {
      Entity viewer = MapSession.viewer();
      String dimension = dimension();
      return viewer != null && dimension != null ? at(viewer.blockPosition(), dimension) : null;
   }

   public static @Nullable Waypoint at(BlockPos pos, String dimension) {
      WaypointStore store = WaypointStore.get();
      if (store == null) {
         return null;
      }

      int count = store.all().size() + 1;
      List<Waypoint> all = store.all();
      int color = WaypointColours.random(all.isEmpty() ? 0 : all.get(all.size() - 1).color);
      return new Waypoint("Waypoint " + count, pos.getX(), pos.getY(), pos.getZ(), dimension, color);
   }

   public static Component dimensionName(String dimension) {
      return switch (dimension) {
         case "minecraft:overworld" -> Component.translatable("diorama.folder.overworld");
         case "minecraft:the_nether" -> Component.translatable("diorama.folder.nether");
         case "minecraft:the_end" -> Component.translatable("diorama.folder.end");
         default -> {
            String path = dimension.substring(dimension.indexOf(58) + 1).replace('_', ' ').replace('/', ' ');
            yield Component.literal(path.isEmpty() ? dimension : Character.toUpperCase(path.charAt(0)) + path.substring(1));
         }
      };
   }

   public static void addTemporary(BlockPos pos, String dimension) {
      WaypointStore store = WaypointStore.get();
      if (store != null) {
         String name = Component.translatable("diorama.waypoint.temporary").getString();
         Waypoint temporary = new Waypoint(name, pos.getX(), pos.getY(), pos.getZ(), dimension, -1);
         temporary.kind = Waypoint.Kind.TEMPORARY;
         store.add(temporary);
      }
   }

   public static boolean canTeleport() {
      return mayUse(Settings.get().teleportCommand);
   }

   public static boolean canTeleport(String dimension) {
      if (dimension.equals(dimension())) {
         return canTeleport();
      } else {
         ClientPacketListener connection = Minecraft.getInstance().getConnection();
         if (connection != null && Settings.get().teleportCommand.equals("tp @s {x} {y} {z}") && canTeleport()) {
            CommandNode<SharedSuggestionProvider> execute = connection.getCommands().getRoot().getChild("execute");
            return execute != null
               && execute.getChild("in") != null
               && connection.levels().stream().anyMatch(level -> level.location().toString().equals(dimension));
         } else {
            return false;
         }
      }
   }

   private static boolean mayUse(String command) {
      ClientPacketListener connection = Minecraft.getInstance().getConnection();
      return connection != null && connection.getCommands().getRoot().getChild(command.split(" ", 2)[0]) != null;
   }

   public static void teleport(String dimension, int x, int y, int z) {
      LocalPlayer player = Minecraft.getInstance().player;
      String command = (dimension.equals(dimension()) ? "" : "execute in " + dimension + " run ")
         + Settings.get().teleportCommand.replace("{x}", Integer.toString(x)).replace("{y}", Integer.toString(y)).replace("{z}", Integer.toString(z));
      if (player != null && command.length() <= 256) {
         player.connection.sendCommand(command);
      }
   }

   public static void watchSleep(Minecraft mc) {
      LocalPlayer player = mc.player;
      boolean sleeping = player != null && player.isSleeping();
      if (sleeping && !wasSleeping && Settings.get().bedMarkers) {
         sleeping = player.getSleepingPos().map(Waypoints::slept).orElse(true);
      }

      wasSleeping = sleeping;
   }

   private static boolean slept(BlockPos pos) {
      WaypointStore store = WaypointStore.get();
      String dimension = dimension();
      if (store != null && dimension != null) {
         for (Waypoint waypoint : store.in(dimension)) {
            if (waypoint.kind == Waypoint.Kind.BED) {
               waypoint.x = pos.getX();
               waypoint.y = pos.getY();
               waypoint.z = pos.getZ();
               store.changed();
               return true;
            }
         }

         Waypoint bed = new Waypoint("Bed", pos.getX(), pos.getY(), pos.getZ(), dimension, -1750963);
         bed.kind = Waypoint.Kind.BED;
         bed.icon = "diorama:bed";
         store.add(bed);
         return true;
      } else {
         return false;
      }
   }

   public static void watchDeaths(Minecraft mc) {
      LocalPlayer player = mc.player;
      if (player != null && player.isAlive() && mc.screen == null && Settings.get().removeReachedDeaths) {
         WaypointStore store = WaypointStore.get();
         String dimension = dimension();
         if (store != null && dimension != null) {
            long now = System.currentTimeMillis();

            for (Waypoint waypoint : store.all()) {
               if (waypoint.kind == Waypoint.Kind.DEATH
                  && waypoint.dimension.equals(dimension)
                  && now - waypoint.created > 5000L
                  && store.shown(waypoint)
                  && waypoint.distanceTo(player.getX(), player.getY(), player.getZ()) < 4.0) {
                  store.remove(waypoint);
               }
            }
         }
      }
   }

   public static void died(LocalPlayer player) {
      if (Settings.get().deathMarkers) {
         WaypointStore store = WaypointStore.get();
         String dimension = dimension();
         if (store != null && dimension != null) {
            BlockPos pos = player.blockPosition();
            Waypoint death = new Waypoint(Waypoint.deathName(LocalTime.now()), pos.getX(), pos.getY(), pos.getZ(), dimension, -10460052);
            death.kind = Waypoint.Kind.DEATH;
            store.add(death);
         }
      }
   }
}
