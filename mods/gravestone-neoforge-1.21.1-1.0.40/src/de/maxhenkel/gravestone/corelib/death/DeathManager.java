package de.maxhenkel.gravestone.corelib.death;

import de.maxhenkel.gravestone.corelib.CommonUtils;
import de.maxhenkel.gravestone.corelib.Logger;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

public class DeathManager {
   public static LevelResource DEATHS = new LevelResource("deaths");

   public static void addDeath(ServerPlayer player, Death death) {
      try {
         File deathFile = getDeathFile(player, death.getId());
         deathFile.getParentFile().mkdirs();
         NbtIo.write(death.toNBT(player.registryAccess()), deathFile.toPath());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Nullable
   public static Death getDeath(ServerPlayer player, UUID id) {
      return getDeath(player.serverLevel(), player.getUUID(), id);
   }

   @Nullable
   public static Death getDeath(ServerLevel world, UUID playerUUID, UUID id) {
      try {
         CompoundTag data = NbtIo.read(getDeathFile(world, playerUUID, id).toPath());
         return data == null ? null : Death.fromNBT(world.registryAccess(), data);
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   @Nullable
   public static Death getDeath(Provider provider, File file) {
      try {
         CompoundTag data = NbtIo.read(file.toPath());
         return data == null ? null : Death.fromNBT(provider, data);
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   @Nullable
   public static Death getDeath(ServerLevel world, UUID id) {
      File deathFolder = getDeathFolder(world);
      File[] players = deathFolder.listFiles((dir, name) -> name.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
      if (players == null) {
         return null;
      }

      for (File f : players) {
         if (f.isDirectory()) {
            File[] files = f.listFiles((dir, name) -> name.equals(id.toString() + ".dat"));
            if (files != null && files.length > 0) {
               return getDeath(world, UUID.fromString(f.getName()), id);
            }
         }
      }

      return null;
   }

   public static List<Death> getDeaths(ServerPlayer player) {
      return getDeaths(player);
   }

   public static List<Death> getDeaths(ServerLevel world, UUID playerUUID) {
      File playerDeathFolder = getPlayerDeathFolder(world, playerUUID);
      if (!playerDeathFolder.exists()) {
         return Collections.emptyList();
      }

      File[] deaths = playerDeathFolder.listFiles((dir, name) -> name.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.dat$"));
      return deaths == null ? Collections.emptyList() : Arrays.stream(deaths).map(f -> {
         try {
            return Death.fromNBT(world.registryAccess(), NbtIo.read(f.toPath()));
         } catch (Exception e) {
            e.printStackTrace();
            return null;
         }
      }).filter(Objects::nonNull).sorted(Comparator.comparingLong(Death::getTimestamp).reversed()).collect(Collectors.toList());
   }

   public static boolean removeDeath(ServerLevel world, Death death) {
      File file = getDeathFile(world, death.getPlayerUUID(), death.getId());
      return file.exists() ? file.delete() : true;
   }

   public static void removeDeathsOlderThan(ServerLevel world, long age) {
      long now = System.currentTimeMillis();
      File deathFolder = getDeathFolder(world);
      File[] players = deathFolder.listFiles((dir, name) -> name.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
      if (players != null) {
         for (File f : players) {
            if (f.isDirectory()) {
               File[] deaths = f.listFiles(file -> now - file.lastModified() > age);
               if (deaths != null) {
                  for (File d : deaths) {
                     Death death = getDeath(world.registryAccess(), d);
                     if (death != null && now - death.getTimestamp() > age && d.delete()) {
                        Logger.INSTANCE
                           .info(
                              "Removed death file of '{}' ({}), since it is older than {} milliseconds", death.getPlayerName(), death.getId().toString(), age
                           );
                     }
                  }
               }
            }
         }
      }
   }

   public static File getDeathFile(ServerPlayer player, UUID id) {
      return new File(getPlayerDeathFolder(player), id.toString() + ".dat");
   }

   public static File getDeathFile(ServerLevel world, UUID playerUUID, UUID id) {
      return new File(getPlayerDeathFolder(world, playerUUID), id.toString() + ".dat");
   }

   public static File getPlayerDeathFolder(ServerPlayer player) {
      return getPlayerDeathFolder(player.serverLevel(), player.getUUID());
   }

   public static File getPlayerDeathFolder(ServerLevel world, UUID uuid) {
      return new File(getDeathFolder(world), uuid.toString());
   }

   public static File getDeathFolder(ServerLevel world) {
      return CommonUtils.getWorldFolder(world, DEATHS);
   }
}
