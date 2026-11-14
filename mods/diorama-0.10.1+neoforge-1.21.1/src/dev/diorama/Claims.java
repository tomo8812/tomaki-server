package dev.diorama;

import dev.diorama.compat.FtbChunks;
import dev.diorama.compat.MineColonies;
import dev.diorama.compat.OpenPartiesAndClaims;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class Claims {
   public static final int REGION_CHUNKS = 1024;
   private static final int TEAM_CHECK_TICKS = 40;
   private static final Claims.Snapshot NONE = new Claims.Snapshot(null, Map.of());
   private static volatile Claims.Snapshot current = NONE;
   private static final Map<ResourceKey<Level>, Map<Claims.Region, Claims.Claim[]>> known = new HashMap<>();
   private static final Map<Claims.Owner, Claims.Claim> owners = new HashMap<>();
   private static final Map<ResourceKey<Level>, Set<Claims.Region>> changed = new HashMap<>();
   private static int ticks;
   private static long tickNanos;
   private static int tickUpdates;
   private static long slowestTick;
   private static String slowestTickParts = "";

   private Claims() {
   }

   public static Claims.Snapshot get() {
      return current;
   }

   public static boolean supported() {
      return FtbChunks.available() || OpenPartiesAndClaims.available() || MineColonies.available();
   }

   public static int index(int chunkX, int chunkZ) {
      return (chunkZ & 31) << 5 | chunkX & 31;
   }

   public static void updated(
      ResourceKey<Level> dimension, int chunkX, int chunkZ, Class<? extends Claims.Owner> source, Claims.@Nullable Owner owner, boolean forceLoaded
   ) {
      Claims.Claim plain = owner == null ? null : owners.get(owner);
      if (plain == null && owner != null) {
         Optional<Claims.Claim> found = owner.look();
         if (found != null && found.isPresent()) {
            plain = found.get();
            owners.put(owner, plain);
         }
      }

      Claims.Claim claim = plain == null ? null : (forceLoaded ? plain.asForceLoaded() : plain);
      Claims.Region region = new Claims.Region(chunkX >> 5, chunkZ >> 5);
      Map<Claims.Region, Claims.Claim[]> regions = known.computeIfAbsent(dimension, d -> new HashMap<>());
      Claims.Claim[] chunks = regions.get(region);
      if (chunks == null) {
         if (claim == null) {
            return;
         }

         chunks = new Claims.Claim[1024];
         regions.put(region, chunks);
      }

      int i = index(chunkX, chunkZ);
      if (claim != null || chunks[i] == null || chunks[i].owner().getClass() == source) {
         if (!Objects.equals(chunks[i], claim)) {
            chunks[i] = claim;
            changed.computeIfAbsent(dimension, d -> new HashSet<>()).add(region);
         }
      }
   }

   public static Claims.@Nullable Owner owner(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
      Map<Claims.Region, Claims.Claim[]> regions = known.get(dimension);
      Claims.Claim[] chunks = regions == null ? null : regions.get(new Claims.Region(chunkX >> 5, chunkZ >> 5));
      Claims.Claim claim = chunks == null ? null : chunks[index(chunkX, chunkZ)];
      return claim == null ? null : claim.owner();
   }

   public static void spent(long nanos) {
      tickNanos += nanos;
      tickUpdates++;
   }

   public static void tick(Minecraft mc) {
      long start = System.nanoTime();
      OpenPartiesAndClaims.tick();
      MineColonies.tick(mc);
      if (++ticks % 40 == 0) {
         checkOwners();
      }

      boolean shown = mc.level != null && Settings.get().claims != Settings.ClaimsShown.OFF && supported();
      ResourceKey<Level> dimension = shown ? mc.level.dimension() : null;
      Claims.Snapshot snapshot = current;
      if (dimension == null) {
         if (snapshot != NONE) {
            current = NONE;
         }
      } else if (!dimension.equals(snapshot.dimension)) {
         current = new Claims.Snapshot(dimension, copyAll(dimension));
      } else if (changed.containsKey(dimension)) {
         Map<Claims.Region, Claims.Claim[]> regions = new HashMap<>(snapshot.regions);
         Map<Claims.Region, Claims.Claim[]> all = known.getOrDefault(dimension, Map.of());

         for (Claims.Region region : changed.get(dimension)) {
            Claims.Claim[] chunks = all.get(region);
            if (chunks != null && !isEmpty(chunks)) {
               regions.put(region, (Claims.Claim[])chunks.clone());
            } else {
               regions.remove(region);
            }
         }

         current = new Claims.Snapshot(dimension, Map.copyOf(regions));
      }

      for (Entry<ResourceKey<Level>, Set<Claims.Region>> entry : changed.entrySet()) {
         Map<Claims.Region, Claims.Claim[]> all = known.get(entry.getKey());
         if (all != null) {
            for (Claims.Region region : entry.getValue()) {
               Claims.Claim[] chunks = all.get(region);
               if (chunks != null && isEmpty(chunks)) {
                  all.remove(region);
               }
            }
         }
      }

      changed.clear();
      long own = System.nanoTime() - start;
      if (tickNanos + own > slowestTick) {
         slowestTick = tickNanos + own;
         slowestTickParts = tickUpdates + " updates in " + tickNanos / 1000L + " us, snapshot in " + own / 1000L + " us";
      }

      tickNanos = 0L;
      tickUpdates = 0;
   }

   private static void checkOwners() {
      Map<Claims.Owner, Claims.Claim> renamed = new HashMap<>();

      for (Entry<Claims.Owner, Claims.Claim> entry : owners.entrySet()) {
         Optional<Claims.Claim> now = entry.getKey().look();
         if (now != null && !now.equals(Optional.of(entry.getValue()))) {
            renamed.put(entry.getKey(), now.orElse(null));
         }
      }

      if (!renamed.isEmpty()) {
         for (Entry<Claims.Owner, Claims.Claim> entry : renamed.entrySet()) {
            if (entry.getValue() == null) {
               owners.remove(entry.getKey());
            } else {
               owners.put(entry.getKey(), entry.getValue());
            }
         }

         for (Entry<ResourceKey<Level>, Map<Claims.Region, Claims.Claim[]>> dimension : known.entrySet()) {
            for (Entry<Claims.Region, Claims.Claim[]> region : dimension.getValue().entrySet()) {
               Claims.Claim[] chunks = region.getValue();

               for (int i = 0; i < chunks.length; i++) {
                  if (chunks[i] != null && renamed.containsKey(chunks[i].owner())) {
                     Claims.Claim team = renamed.get(chunks[i].owner());
                     chunks[i] = team == null ? null : chunks[i].as(team);
                     changed.computeIfAbsent(dimension.getKey(), d -> new HashSet<>()).add(region.getKey());
                  }
               }
            }
         }
      }
   }

   private static Map<Claims.Region, Claims.Claim[]> copyAll(ResourceKey<Level> dimension) {
      Map<Claims.Region, Claims.Claim[]> copy = new HashMap<>();

      for (Entry<Claims.Region, Claims.Claim[]> region : known.getOrDefault(dimension, Map.of()).entrySet()) {
         if (!isEmpty(region.getValue())) {
            copy.put(region.getKey(), (Claims.Claim[])region.getValue().clone());
         }
      }

      return Map.copyOf(copy);
   }

   private static boolean isEmpty(Claims.Claim[] chunks) {
      for (Claims.Claim claim : chunks) {
         if (claim != null) {
            return false;
         }
      }

      return true;
   }

   public static void forget(ResourceKey<Level> dimension, Class<? extends Claims.Owner> source) {
      Map<Claims.Region, Claims.Claim[]> regions = known.get(dimension);
      if (regions != null) {
         for (Entry<Claims.Region, Claims.Claim[]> region : regions.entrySet()) {
            Claims.Claim[] chunks = region.getValue();

            for (int i = 0; i < chunks.length; i++) {
               if (chunks[i] != null && chunks[i].owner().getClass() == source) {
                  chunks[i] = null;
                  changed.computeIfAbsent(dimension, d -> new HashSet<>()).add(region.getKey());
               }
            }
         }
      }
   }

   public static void clear() {
      known.clear();
      owners.clear();
      changed.clear();
      current = NONE;
      OpenPartiesAndClaims.cleared();
      MineColonies.cleared();
   }

   public static long resetSlowestTick() {
      long slowest = slowestTick;
      slowestTick = 0L;
      return slowest;
   }

   public static String slowestTickParts() {
      return slowestTickParts;
   }

   public record Claim(Claims.Owner owner, int rgb, String name, boolean forceLoaded, boolean titled) {
      public Claims.Claim asForceLoaded() {
         return new Claims.Claim(this.owner, this.rgb, this.name, true, this.titled);
      }

      Claims.Claim as(Claims.Claim team) {
         return this.forceLoaded ? team.asForceLoaded() : team;
      }
   }

   public interface Owner {
      @Nullable Optional<Claims.Claim> look();
   }

   private record Region(int x, int z) {
   }

   public static final class Snapshot {
      private final @Nullable ResourceKey<Level> dimension;
      private final Map<Claims.Region, Claims.Claim[]> regions;

      Snapshot(@Nullable ResourceKey<Level> dimension, Map<Claims.Region, Claims.Claim[]> regions) {
         this.dimension = dimension;
         this.regions = regions;
      }

      public boolean isEmpty() {
         return this.regions.isEmpty();
      }

      public Claims.@Nullable Claim at(int chunkX, int chunkZ) {
         Claims.Claim[] region = this.regions.get(new Claims.Region(chunkX >> 5, chunkZ >> 5));
         return region == null ? null : region[Claims.index(chunkX, chunkZ)];
      }

      public boolean mayHaveAny(int chunkX0, int chunkZ0, int chunkX1, int chunkZ1) {
         if (this.regions.isEmpty()) {
            return false;
         }

         int rx0 = chunkX0 >> 5;
         int rz0 = chunkZ0 >> 5;
         int rx1 = chunkX1 >> 5;
         int rz1 = chunkZ1 >> 5;
         if ((long)(rx1 - rx0 + 1) * (rz1 - rz0 + 1) > this.regions.size()) {
            for (Claims.Region region : this.regions.keySet()) {
               if (region.x() >= rx0 && region.x() <= rx1 && region.z() >= rz0 && region.z() <= rz1) {
                  return true;
               }
            }

            return false;
         } else {
            for (int rx = rx0; rx <= rx1; rx++) {
               for (int rz = rz0; rz <= rz1; rz++) {
                  if (this.regions.containsKey(new Claims.Region(rx, rz))) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public boolean differsIn(Claims.Snapshot other, int chunkX0, int chunkZ0, int chunkX1, int chunkZ1) {
         if (other == this) {
            return false;
         }

         if (!Objects.equals(other.dimension, this.dimension)) {
            return true;
         }

         for (int rx = chunkX0 >> 5; rx <= chunkX1 >> 5; rx++) {
            for (int rz = chunkZ0 >> 5; rz <= chunkZ1 >> 5; rz++) {
               Claims.Region region = new Claims.Region(rx, rz);
               Claims.Claim[] mine = this.regions.get(region);
               Claims.Claim[] theirs = other.regions.get(region);
               if (mine != theirs && !Arrays.equals(mine, theirs)) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
