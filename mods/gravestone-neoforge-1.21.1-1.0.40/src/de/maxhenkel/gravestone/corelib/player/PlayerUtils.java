package de.maxhenkel.gravestone.corelib.player;

import de.maxhenkel.gravestone.corelib.Logger;
import java.lang.reflect.Field;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.player.Player;

public class PlayerUtils {
   public static byte getModel(Player player) {
      try {
         Field dataPlayerModeCustomisation = Player.class.getDeclaredField("DATA_PLAYER_MODE_CUSTOMISATION");
         dataPlayerModeCustomisation.setAccessible(true);
         return (Byte)player.getEntityData().get((EntityDataAccessor)dataPlayerModeCustomisation.get(null));
      } catch (Exception e) {
         Logger.INSTANCE.error("Error getting player model", e);
         return 0;
      }
   }
}
