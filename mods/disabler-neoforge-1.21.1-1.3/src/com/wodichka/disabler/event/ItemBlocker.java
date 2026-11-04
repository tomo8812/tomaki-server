package com.wodichka.disabler.event;

import com.wodichka.disabler.config.DisablerConfig;
import com.wodichka.disabler.item.BlockedItemCleaner;
import com.wodichka.disabler.item.NeoForgeStorageSanitizer;
import com.wodichka.disabler.item.StorageInventoryScanner;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent.Close;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent.Open;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.ItemSmeltedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;

public final class ItemBlocker {
   private static final int INVENTORY_SWEEP_INTERVAL_TICKS = 100;
   private final StorageInventoryScanner storageScanner = new StorageInventoryScanner(NeoForgeStorageSanitizer::sanitize);
   private int ticksUntilSweep = 100;

   @SubscribeEvent
   public void onServerTick(Post event) {
      this.storageScanner.tick(event.getServer());
      if (!DisablerConfig.hasBlockedItems()) {
         this.ticksUntilSweep = 100;
      } else if (--this.ticksUntilSweep <= 0) {
         this.ticksUntilSweep = 100;
         event.getServer().getPlayerList().getPlayers().forEach(BlockedItemCleaner::sanitizePlayer);
      }
   }

   @SubscribeEvent
   public void onLogin(PlayerLoggedInEvent event) {
      sanitize(event);
   }

   @SubscribeEvent
   public void onRespawn(PlayerRespawnEvent event) {
      sanitize(event);
   }

   @SubscribeEvent
   public void onDimensionChange(PlayerChangedDimensionEvent event) {
      sanitize(event);
   }

   @SubscribeEvent
   public void onCrafted(ItemCraftedEvent event) {
      sanitize(event);
   }

   @SubscribeEvent
   public void onSmelted(ItemSmeltedEvent event) {
      sanitize(event);
   }

   @SubscribeEvent
   public void onContainerOpen(Open event) {
      BlockedItemCleaner.sanitizeMenu(event.getContainer());
      sanitize(event);
   }

   @SubscribeEvent
   public void onContainerClose(Close event) {
      BlockedItemCleaner.sanitizeMenu(event.getContainer());
      sanitize(event);
   }

   @SubscribeEvent
   public void onItemPickup(Pre event) {
      if (BlockedItemCleaner.isBlocked(event.getItemEntity().getItem())) {
         event.setCanPickup(TriState.FALSE);
         event.getItemEntity().discard();
      }
   }

   @SubscribeEvent
   public void onItemToss(ItemTossEvent event) {
      if (BlockedItemCleaner.isBlocked(event.getEntity().getItem())) {
         event.setCanceled(true);
         event.getEntity().discard();
      }
   }

   @SubscribeEvent
   public void onEntityJoin(EntityJoinLevelEvent event) {
      if (!event.getLevel().isClientSide() && event.getEntity() instanceof ItemEntity item && BlockedItemCleaner.isBlocked(item.getItem())) {
         event.setCanceled(true);
      }
   }

   private static void sanitize(PlayerEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         BlockedItemCleaner.sanitizePlayer(player);
      }
   }
}
