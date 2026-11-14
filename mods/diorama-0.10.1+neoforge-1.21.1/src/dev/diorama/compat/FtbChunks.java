package dev.diorama.compat;

import dev.diorama.Claims;
import dev.diorama.Diorama;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

public final class FtbChunks {
   private static final String MAP = "dev.ftb.mods.ftbchunks.client.map.";
   private static boolean started;
   private static boolean failed;
   private static Field taskDimension;
   private static Field taskChunk;
   private static Field taskTeam;
   private static Field chunkX;
   private static Field chunkZ;
   private static Field chunkForceLoaded;
   private static Field dimensionKey;
   private static Field dimensionManager;
   private static Method managerInvalid;
   private static Method teamId;
   private static Method getProperty;
   private static Object colorProperty;
   private static Object nameProperty;
   private static Method rgb;
   private static Method teamsApi;
   private static Method clientManagerLoaded;
   private static Method clientManager;
   private static Method teamById;
   private static final UUID NO_TEAM = new UUID(0L, 0L);

   private FtbChunks() {
   }

   public static boolean installed() {
      return ModList.get().isLoaded("ftbchunks");
   }

   public static boolean available() {
      if (!started) {
         started = true;
         if (installed()) {
            try {
               lookUp();
               Diorama.LOGGER.info("FTB Chunks is installed: showing its claims on the map");
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
      Class<?> task = Class.forName("dev.ftb.mods.ftbchunks.client.map.UpdateChunkFromServerTask");
      taskDimension = task.getDeclaredField("dimension");
      taskDimension.setAccessible(true);
      Class<?> dimension = Class.forName("dev.ftb.mods.ftbchunks.client.map.MapDimension");
      dimensionKey = dimension.getField("dimension");
      dimensionManager = dimension.getDeclaredField("manager");
      dimensionManager.setAccessible(true);
      managerInvalid = Class.forName("dev.ftb.mods.ftbchunks.client.map.MapManager").getMethod("isInvalid");
      taskTeam = task.getDeclaredField("teamId");
      taskTeam.setAccessible(true);

      try {
         taskChunk = task.getDeclaredField("info");
      } catch (NoSuchFieldException e) {
         taskChunk = task.getDeclaredField("chunk");
      }

      taskChunk.setAccessible(true);
      chunkX = field(taskChunk.getType(), "x");
      chunkZ = field(taskChunk.getType(), "z");
      chunkForceLoaded = field(taskChunk.getType(), "forceLoaded");
      Class<?> team = Class.forName("dev.ftb.mods.ftbteams.api.Team");
      teamId = team.getMethod("getId");
      Class<?> property = Class.forName("dev.ftb.mods.ftbteams.api.property.TeamProperty");
      getProperty = team.getMethod("getProperty", property);
      Class<?> properties = Class.forName("dev.ftb.mods.ftbteams.api.property.TeamProperties");
      colorProperty = properties.getField("COLOR").get(null);
      nameProperty = properties.getField("DISPLAY_NAME").get(null);
      rgb = Class.forName("dev.ftb.mods.ftblibrary.icon.Color4I").getMethod("rgb");
      teamsApi = Class.forName("dev.ftb.mods.ftbteams.api.FTBTeamsAPI").getMethod("api");
      Class<?> api = teamsApi.getReturnType();
      clientManagerLoaded = api.getMethod("isClientManagerLoaded");
      clientManager = api.getMethod("getClientManager");
      teamById = clientManager.getReturnType().getMethod("getTeamByID", UUID.class);
   }

   private static Field field(Class<?> owner, String name) throws NoSuchFieldException {
      Field field = owner.getDeclaredField(name);
      field.setAccessible(true);
      return field;
   }

   public static void updated(Object task) {
      if (available() && Minecraft.getInstance().isSameThread()) {
         long start = System.nanoTime();

         try {
            Object dimension = taskDimension.get(task);
            if (!(Boolean)managerInvalid.invoke(dimensionManager.get(dimension))) {
               Object chunk = taskChunk.get(task);
               ResourceKey<Level> key = (ResourceKey<Level>)dimensionKey.get(dimension);
               UUID team = (UUID)taskTeam.get(task);
               FtbChunks.Team owner = team != null && !team.equals(NO_TEAM) ? new FtbChunks.Team(team) : null;
               Claims.updated(key, chunkX.getInt(chunk), chunkZ.getInt(chunk), FtbChunks.Team.class, owner, chunkForceLoaded.getBoolean(chunk));
               return;
            }
         } catch (InvocationTargetException e) {
            Diorama.LOGGER.debug("FTB Chunks couldn't give a chunk's claim", e);
            return;
         } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            fail(e);
            return;
         } finally {
            Claims.spent(System.nanoTime() - start);
         }
      }
   }

   public static @Nullable Optional<Claims.Claim> team(UUID id) {
      if (!available()) {
         return null;
      }

      try {
         Object api = teamsApi.invoke(null);
         if (!(Boolean)clientManagerLoaded.invoke(api)) {
            return null;
         }

         Optional<?> team = (Optional<?>)teamById.invoke(clientManager.invoke(api), id);
         return team.isEmpty() ? Optional.empty() : Optional.of(owner(team.get()));
      } catch (InvocationTargetException e) {
         Diorama.LOGGER.debug("FTB Teams couldn't give a team", e);
         return null;
      } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
         fail(e);
         return null;
      }
   }

   private static Claims.Claim owner(Object team) throws ReflectiveOperationException {
      UUID id = (UUID)teamId.invoke(team);
      Object colour = getProperty.invoke(team, colorProperty);
      Object name = getProperty.invoke(team, nameProperty);
      return new Claims.Claim(
         new FtbChunks.Team(id), colour == null ? 16777215 : (Integer)rgb.invoke(colour) & 16777215, name instanceof String s ? s : "", false, false
      );
   }

   private static void fail(Throwable e) {
      if (!failed) {
         failed = true;
         Diorama.LOGGER.warn("Couldn't read FTB Chunks' claims; they won't show on the map", e);
      }
   }

   public record Team(UUID id) implements Claims.Owner {
      @Override
      public @Nullable Optional<Claims.Claim> look() {
         return FtbChunks.team(this.id);
      }
   }
}
