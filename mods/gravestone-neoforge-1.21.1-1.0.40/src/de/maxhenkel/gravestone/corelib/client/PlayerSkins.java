package de.maxhenkel.gravestone.corelib.client;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class PlayerSkins {
   @Nullable
   private static final Field gameProfileCacheField;
   private static HashMap<UUID, GameProfile> PLAYERS;

   public static ResourceLocation getSkin(UUID uuid, String name) {
      return getSkin(getGameProfile(uuid, name));
   }

   public static ResourceLocation getSkin(Player player) {
      return getSkin(player.getGameProfile());
   }

   public static ResourceLocation getSkin(GameProfile gameProfile) {
      return Minecraft.getInstance().getSkinManager().getInsecureSkin(gameProfile).texture();
   }

   public static GameProfile getGameProfile(UUID uuid, String name) {
      if (PLAYERS.containsKey(uuid)) {
         return PLAYERS.get(uuid);
      }

      GameProfile gameProfile = new GameProfile(uuid, name);
      if (gameProfileCacheField == null) {
         return gameProfile;
      }

      try {
         GameProfileCache cache = (GameProfileCache)gameProfileCacheField.get(null);
         cache.getAsync(name).thenAccept(p -> p.ifPresent(value -> PLAYERS.put(uuid, value)));
         return gameProfile;
      } catch (Exception e) {
         PLAYERS.put(uuid, gameProfile);
         return gameProfile;
      }
   }

   public static boolean isSlim(UUID uuid) {
      PlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(uuid);
      return networkplayerinfo == null ? (uuid.hashCode() & 1) == 1 : networkplayerinfo.getSkin().model().equals(Model.SLIM);
   }

   public static boolean isSlim(Player player) {
      return isSlim(player.getUUID());
   }

   static {
      Field gameProfileCache = null;
      Field[] fields = SkullBlockEntity.class.getDeclaredFields();

      for (Field field : fields) {
         if (field.getType().equals(GameProfileCache.class)) {
            field.setAccessible(true);
            gameProfileCache = field;
            break;
         }
      }

      gameProfileCacheField = gameProfileCache;
      PLAYERS = new HashMap<>();
   }
}
