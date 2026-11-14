package dev.diorama.waypoint;

import dev.diorama.bake.ChunkSource;
import dev.diorama.data.ChunkSnapshot;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.jspecify.annotations.Nullable;

public final class Portals {
   private static final int SAME_PORTAL = 24;
   private static final int NETHER_COLOR = -7450938;
   private static final int END_COLOR = -15555180;
   private static final Predicate<BlockState> PORTAL = Portals::isPortal;
   static final String PLAIN_NAME = "Portal";

   private Portals() {
   }

   public static boolean isPortal(BlockState state) {
      return kind(state.getBlock()) != null;
   }

   private static Waypoint.@Nullable Kind kind(Block block) {
      if (block == Blocks.NETHER_PORTAL) {
         return Waypoint.Kind.NETHER_PORTAL;
      } else if (block == Blocks.END_PORTAL) {
         return Waypoint.Kind.END_PORTAL;
      } else {
         return block == Blocks.END_GATEWAY ? Waypoint.Kind.END_GATEWAY : null;
      }
   }

   public static List<Portals.Spot> find(ChunkSnapshot chunk, int roof) {
      List<Map<Waypoint.Kind, List<int[]>>> blocks = null;
      List<Set<Waypoint.Kind>> framed = List.of(EnumSet.noneOf(Waypoint.Kind.class), EnumSet.noneOf(Waypoint.Kind.class));

      for (int i = 0; i < chunk.sectionCount(); i++) {
         PalettedContainer<BlockState> section = chunk.section(i);
         if (section != null && section.maybeHas(PORTAL)) {
            int baseY = chunk.minSectionY + i << 4;

            for (int y = 0; y < 16; y++) {
               for (int z = 0; z < 16; z++) {
                  for (int x = 0; x < 16; x++) {
                     Waypoint.Kind kind = kind(((BlockState)section.get(x, y, z)).getBlock());
                     if (kind != null) {
                        if (blocks == null) {
                           blocks = List.of(new EnumMap<>(Waypoint.Kind.class), new EnumMap<>(Waypoint.Kind.class));
                        }

                        int side = roof != Integer.MIN_VALUE && baseY + y >= roof ? 1 : 0;
                        if (!framed.get(side).contains(kind) && framed(chunk, kind, x, baseY + y, z)) {
                           framed.get(side).add(kind);
                        }

                        blocks.get(side)
                           .computeIfAbsent(kind, k -> new ArrayList<>())
                           .add(new int[]{(chunk.chunkX << 4) + x, baseY + y, (chunk.chunkZ << 4) + z});
                     }
                  }
               }
            }
         }
      }

      if (blocks == null) {
         return List.of();
      }

      List<Portals.Spot> spots = new ArrayList<>();

      for (int side = 0; side < 2; side++) {
         for (Entry<Waypoint.Kind, List<int[]>> entry : blocks.get(side).entrySet()) {
            int[] middle = entry.getValue().get(entry.getValue().size() / 2);
            spots.add(new Portals.Spot(entry.getKey(), middle[0], middle[1], middle[2], framed.get(side).contains(entry.getKey())));
         }
      }

      return spots;
   }

   private static boolean framed(ChunkSnapshot chunk, Waypoint.Kind kind, int x, int y, int z) {
      return switch (kind) {
         case NETHER_PORTAL -> {
            int top = y;

            while (block(chunk, x, top + 1, z) == Blocks.NETHER_PORTAL) {
               top++;
            }

            int bottom = y;

            while (block(chunk, x, bottom - 1, z) == Blocks.NETHER_PORTAL) {
               bottom--;
            }

            yield block(chunk, x, top + 1, z) == Blocks.OBSIDIAN || block(chunk, x, bottom - 1, z) == Blocks.OBSIDIAN;
         }
         case END_PORTAL -> endFrame(block(chunk, x - 1, y, z))
            || endFrame(block(chunk, x + 1, y, z))
            || endFrame(block(chunk, x, y, z - 1))
            || endFrame(block(chunk, x, y, z + 1));
         default -> block(chunk, x, y + 1, z) == Blocks.BEDROCK || block(chunk, x, y - 1, z) == Blocks.BEDROCK;
      };
   }

   private static boolean endFrame(@Nullable Block block) {
      return block == Blocks.END_PORTAL_FRAME || block == Blocks.BEDROCK;
   }

   private static @Nullable Block block(ChunkSnapshot chunk, int x, int y, int z) {
      int index = (y >> 4) - chunk.minSectionY;
      if (x >= 0 && x <= 15 && z >= 0 && z <= 15 && index >= 0 && index < chunk.sectionCount()) {
         PalettedContainer<BlockState> section = chunk.section(index);
         if (section != null) {
            return ((BlockState)section.get(x, y & 15, z)).getBlock();
         }

         BlockState uniform = chunk.uniformSection(index);
         return uniform == null ? null : uniform.getBlock();
      } else {
         return null;
      }
   }

   public static void chunkSeen(ChunkSnapshot chunk, String dimension, WaypointStore store, ChunkSource around) {
      int roof = around.roof();
      List<Portals.Spot> spots = find(chunk, roof);
      add(spots, dimension, store, roof);
      boolean renamed = false;

      for (Waypoint pin : store.withHidden(dimension)) {
         if (pin.kind.isPortal() && !pin.edited && pin.x >> 4 == chunk.chunkX && pin.z >> 4 == chunk.chunkZ) {
            if (onPortal(pin, chunk)) {
               for (Portals.Spot spot : spots) {
                  boolean sameSide = roof == Integer.MIN_VALUE || pin.y >= roof == spot.y() >= roof;
                  if (spot.kind() == pin.kind && sameSide && !pin.name.equals(savedName(spot)) && automatic(pin)) {
                     pin.name = savedName(spot);
                     renamed = true;
                  }
               }
            } else {
               store.forget(pin);
               int reach = 2;

               for (int dx = -reach; dx <= reach; dx++) {
                  for (int dz = -reach; dz <= reach; dz++) {
                     ChunkSnapshot near = around.chunk(chunk.chunkX + dx, chunk.chunkZ + dz);
                     if (near != null) {
                        add(find(near, roof), dimension, store, roof);
                     }
                  }
               }
            }
         }
      }

      if (renamed) {
         store.changed();
      }
   }

   private static boolean onPortal(Waypoint pin, ChunkSnapshot chunk) {
      int index = (pin.y >> 4) - chunk.minSectionY;
      PalettedContainer<BlockState> section = index >= 0 && index < chunk.sectionCount() ? chunk.section(index) : null;
      return section != null && kind(((BlockState)section.get(pin.x & 15, pin.y & 15, pin.z & 15)).getBlock()) == pin.kind;
   }

   public static void add(List<Portals.Spot> spots, String dimension, WaypointStore store, int roof) {
      if (!spots.isEmpty()) {
         List<Waypoint> known = new ArrayList<>(store.withHidden(dimension));
         List<Waypoint> pins = new ArrayList<>();

         for (Portals.Spot spot : spots) {
            boolean pinned = false;

            for (Waypoint waypoint : known) {
               boolean marksPortals = waypoint.kind == spot.kind() || !waypoint.kind.isPortal() && waypoint.icon.equals("diorama:portal");
               boolean sameSide = roof == Integer.MIN_VALUE || waypoint.y >= roof == spot.y() >= roof;
               pinned |= marksPortals && sameSide && Math.abs(waypoint.x - spot.x()) <= 24 && Math.abs(waypoint.z - spot.z()) <= 24;
            }

            if (!pinned) {
               int color = spot.kind() == Waypoint.Kind.NETHER_PORTAL ? -7450938 : -15555180;
               Waypoint pin = new Waypoint(savedName(spot), spot.x(), spot.y(), spot.z(), dimension, color);
               pin.kind = spot.kind();
               pin.icon = "diorama:portal";
               known.add(pin);
               pins.add(pin);
            }
         }

         store.addAll(pins);
      }
   }

   static Block block(Waypoint.Kind kind) {
      return kind == Waypoint.Kind.NETHER_PORTAL ? Blocks.NETHER_PORTAL : (kind == Waypoint.Kind.END_PORTAL ? Blocks.END_PORTAL : Blocks.END_GATEWAY);
   }

   static String savedName(Waypoint.Kind kind) {
      return kind == Waypoint.Kind.NETHER_PORTAL ? "Nether Portal" : (kind == Waypoint.Kind.END_PORTAL ? "End Portal" : "End Gateway");
   }

   private static String savedName(Portals.Spot spot) {
      return spot.framed() ? savedName(spot.kind()) : "Portal";
   }

   private static boolean automatic(Waypoint pin) {
      return pin.name.equals("Portal") || pin.name.equals(savedName(pin.kind));
   }

   public record Spot(Waypoint.Kind kind, int x, int y, int z, boolean framed) {
   }
}
