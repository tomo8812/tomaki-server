package dev.diorama.compat;

import dev.diorama.Claims;
import dev.diorama.Diorama;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

public final class MineColonies {
   private static final String API = "com.minecolonies.api.";
   private static final int LOOK_TICKS = 20;
   private static final int NO_COLONY = 0;
   private static boolean started;
   private static boolean failed;
   private static Method managerInstance;
   private static Method colonyView;
   private static @Nullable Method claimData;
   private static @Nullable Method claimColony;
   private static @Nullable Method chunkColony;
   private static Method colonyName;
   private static Method colonyColour;
   private static Method colonyPermissions;
   private static Method hasPermission;
   private static Object mapBorder;
   private static final int FAILED_LOOKS = 30;
   private static int ticks;
   private static int failedLooks;
   private static final Map<ResourceKey<Level>, Map<Long, Integer>> handed = new HashMap<>();

   private MineColonies() {
   }

   public static boolean installed() {
      return ModList.get().isLoaded("minecolonies");
   }

   public static boolean available() {
      if (!started) {
         started = true;
         if (installed()) {
            try {
               lookUp();
               Diorama.LOGGER.info("MineColonies is installed: showing its colonies' land on the map");
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
               fail(e);
            }
         } else {
            failed = true;
         }
      }

      return !failed;
   }

   private static void lookUp() throws ReflectiveOperationException {
      Class<?> manager = Class.forName("com.minecolonies.api.colony.IColonyManager");
      managerInstance = manager.getMethod("getInstance");
      colonyView = manager.getMethod("getColonyView", int.class, ResourceKey.class);

      try {
         claimData = manager.getMethod("getClaimData", ResourceKey.class);
         claimColony = Class.forName("com.minecolonies.api.colony.claim.IChunkClaimData").getMethod("getOwningColony");
      } catch (NoSuchMethodException | ClassNotFoundException e) {
         claimData = null;
         claimColony = null;
         chunkColony = Class.forName("com.minecolonies.api.util.ColonyUtils").getMethod("getOwningColony", LevelChunk.class);
      }

      Class<?> colony = Class.forName("com.minecolonies.api.colony.IColony");
      colonyName = colony.getMethod("getName");
      colonyColour = colony.getMethod("getTeamColonyColor");
      colonyPermissions = colony.getMethod("getPermissions");
      Class<?> action = Class.forName("com.minecolonies.api.colony.permissions.Action");
      hasPermission = Class.forName("com.minecolonies.api.colony.permissions.IPermissions").getMethod("hasPermission", Player.class, action);
      mapBorder = action.getField("MAP_BORDER").get(null);
   }

   public static void cleared() {
      handed.clear();
      ticks = 0;
   }

   public static void tick(Minecraft mc) {
      if (mc.level != null && mc.player != null && ticks++ % 20 == 0 && available()) {
         try {
            look(mc);
            failedLooks = 0;
         } catch (InvocationTargetException | RuntimeException e) {
            Diorama.LOGGER.debug("MineColonies couldn't give its colonies' land", e);
            if (++failedLooks >= 30) {
               fail(e);
            }
         } catch (ReflectiveOperationException | LinkageError e) {
            fail(e);
         }
      }
   }

   private static void look(Minecraft mc) throws ReflectiveOperationException {
      ResourceKey<Level> dimension = mc.level.dimension();
      Object manager = managerInstance.invoke(null);
      Map<Long, Integer> known = handed.computeIfAbsent(dimension, d -> new HashMap<>());
      Map<Integer, Boolean> shown = new HashMap<>();
      Map<Long, Integer> seen = new HashMap<>();
      if (claimData != null) {
         for (Entry<?, ?> claim : ((Map)claimData.invoke(manager, dimension)).entrySet()) {
            if (claim.getValue() != null) {
               ChunkPos pos = (ChunkPos)claim.getKey();
               seen.put(key(pos.getMinBlockX() >> 4, pos.getMinBlockZ() >> 4), (Integer)claimColony.invoke(claim.getValue()));
            }
         }
      } else {
         int radius = (Integer)mc.options.renderDistance().get() + 2;
         int centreX = mc.player.blockPosition().getX() >> 4;
         int centreZ = mc.player.blockPosition().getZ() >> 4;

         for (int x = centreX - radius; x <= centreX + radius; x++) {
            for (int z = centreZ - radius; z <= centreZ + radius; z++) {
               LevelChunk chunk = mc.level.getChunkSource().getChunkNow(x, z);
               if (chunk != null && !chunk.isEmpty()) {
                  seen.put(key(x, z), (Integer)chunkColony.invoke(null, chunk));
               }
            }
         }
      }

      for (Entry<Long, Integer> chunk : seen.entrySet()) {
         int colony = chunk.getValue();
         if (colony != 0 && !shown.computeIfAbsent(colony, id -> shows(manager, id, dimension, mc.player))) {
            colony = 0;
         }

         hand(dimension, known, chunk.getKey(), colony);
      }

      Iterator<Entry<Long, Integer>> it = known.entrySet().iterator();

      while (it.hasNext()) {
         Entry<Long, Integer> chunk = it.next();
         if (!seen.containsKey(chunk.getKey())) {
            long at = chunk.getKey();
            if (claimData == null && shown.computeIfAbsent(chunk.getValue(), id -> shows(manager, id, dimension, mc.player))) {
               claim(dimension, (int)(at >> 32), (int)at, chunk.getValue());
            } else {
               Claims.updated(dimension, (int)(at >> 32), (int)at, MineColonies.Colony.class, null, false);
               it.remove();
            }
         }
      }
   }

   private static void hand(ResourceKey<Level> dimension, Map<Long, Integer> known, long at, int colony) {
      int x = (int)(at >> 32);
      int z = (int)at;
      if (colony == 0) {
         if (known.remove(at) != null) {
            Claims.updated(dimension, x, z, MineColonies.Colony.class, null, false);
         }
      } else {
         claim(dimension, x, z, colony);
         known.put(at, colony);
      }
   }

   private static void claim(ResourceKey<Level> dimension, int x, int z, int colony) {
      Claims.Owner now = Claims.owner(dimension, x, z);
      if (now == null || now instanceof MineColonies.Colony other && other.id() != colony) {
         Claims.updated(dimension, x, z, MineColonies.Colony.class, new MineColonies.Colony(dimension, colony), false);
      }
   }

   private static long key(int chunkX, int chunkZ) {
      return (long)chunkX << 32 | chunkZ & 4294967295L;
   }

   private static boolean shows(Object manager, int id, ResourceKey<Level> dimension, Player player) {
      try {
         Object view = colonyView.invoke(manager, id, dimension);
         return view != null && (Boolean)hasPermission.invoke(colonyPermissions.invoke(view), player, mapBorder);
      } catch (ReflectiveOperationException | RuntimeException e) {
         Diorama.LOGGER.debug("MineColonies couldn't give a colony", e);
         return false;
      }
   }

   private static void fail(Throwable e) {
      if (!failed) {
         failed = true;
         Diorama.LOGGER.warn("Couldn't read MineColonies' colonies; their land won't show on the map", e);
      }
   }

   public record Colony(ResourceKey<Level> dimension, int id) implements Claims.Owner {
      @Override
      public @Nullable Optional<Claims.Claim> look() {
         Minecraft mc = Minecraft.getInstance();
         if (MineColonies.available() && mc.player != null) {
            try {
               Object view = MineColonies.colonyView.invoke(MineColonies.managerInstance.invoke(null), this.id, this.dimension);
               if (view != null && (Boolean)MineColonies.hasPermission.invoke(MineColonies.colonyPermissions.invoke(view), mc.player, MineColonies.mapBorder)) {
                  TextColor colour = MineColonies.colonyColour.invoke(view) instanceof ChatFormatting team ? TextColor.fromLegacyFormat(team) : null;
                  String name = MineColonies.colonyName.invoke(view) instanceof String s ? s : "";
                  return Optional.of(new Claims.Claim(this, colour == null ? 16777215 : colour.getValue() & 16777215, name, false, true));
               } else {
                  return Optional.empty();
               }
            } catch (InvocationTargetException e) {
               Diorama.LOGGER.debug("MineColonies couldn't give a colony", e);
               return null;
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
               MineColonies.fail(e);
               return null;
            }
         } else {
            return null;
         }
      }
   }
}
