package net.caffeinemc.mods.lithium.common.world.listeners;

import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;

public interface WorldBorderListenerOnce extends BorderChangeListener {
   void lithium$onWorldBorderShapeChange(WorldBorder var1);

   default void onAreaReplaced(WorldBorder border) {
      this.lithium$onWorldBorderShapeChange(border);
   }

   default void onBorderSizeSet(WorldBorder border, double size) {
      this.lithium$onWorldBorderShapeChange(border);
   }

   default void onBorderSizeLerping(WorldBorder border, double fromSize, double toSize, long time) {
      this.lithium$onWorldBorderShapeChange(border);
   }

   default void onBorderCenterSet(WorldBorder border, double centerX, double centerZ) {
      this.lithium$onWorldBorderShapeChange(border);
   }

   default void onBorderSetWarningTime(WorldBorder border, int warningTime) {
   }

   default void onBorderSetWarningBlocks(WorldBorder border, int warningBlockDistance) {
   }

   default void onBorderSetDamagePerBlock(WorldBorder border, double damagePerBlock) {
   }

   default void onBorderSetDamageSafeZOne(WorldBorder border, double safeZoneRadius) {
   }
}
