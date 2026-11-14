package com.kishku7.chunksmith.lod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public final class CsLodDimension {
   private CsLodDimension() {
   }

   public static String current() {
      Level level = Minecraft.getInstance().level;
      return level == null ? "" : of(level);
   }

   public static String of(Level level) {
      String id = level.dimension().location().toString();
      return id.replace(':', '_').replace('/', '_');
   }
}
