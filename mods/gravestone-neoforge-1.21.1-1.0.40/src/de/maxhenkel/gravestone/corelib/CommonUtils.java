package de.maxhenkel.gravestone.corelib;

import java.io.File;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;

public class CommonUtils {
   public static File getWorldFolder(ServerLevel serverWorld, LevelResource folderName) {
      return serverWorld.getServer().getWorldPath(folderName).toFile();
   }
}
