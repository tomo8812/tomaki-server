package com.kishku7.chunksmith.listeners.bossbar;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.event.task.GenerationTaskUpdateEvent;
import com.kishku7.chunksmith.platform.ServerLevelHolder;
import com.kishku7.chunksmith.platform.World;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.level.Level;

public class BossBarTaskUpdateListener implements Consumer<GenerationTaskUpdateEvent> {
   private final Map<ResourceLocation, ServerBossEvent> bossBars;

   public BossBarTaskUpdateListener(Map<ResourceLocation, ServerBossEvent> bossBars) {
      this.bossBars = bossBars;
   }

   public void accept(GenerationTaskUpdateEvent event) {
      GenerationTask task = event.generationTask();
      Chunksmith chunky = task.getChunky();
      World world = task.getSelection().world();
      ResourceLocation worldIdentifier = ResourceLocation.tryParse(world.getKey());
      if (worldIdentifier != null && world instanceof ServerLevelHolder serverWorld) {
         ServerBossEvent bossBar = this.bossBars.computeIfAbsent(worldIdentifier, x -> this.createNewBossBar(worldIdentifier));
         boolean silent = chunky.getConfig().isSilent();
         if (silent == bossBar.isVisible()) {
            bossBar.setVisible(!silent);
         }

         MinecraftServer server = serverWorld.getWorld().getServer();

         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.hasPermissions(2)) {
               bossBar.addPlayer(player);
            } else {
               bossBar.removePlayer(player);
            }
         }

         GenerationTask.Progress progress = task.getProgress();
         bossBar.setName(
            Component.nullToEmpty(
               String.format(
                  "%s | %s%% | %s:%s:%s",
                  worldIdentifier,
                  String.format("%.2f", progress.getPercentComplete()),
                  String.format("%01d", progress.getHours()),
                  String.format("%02d", progress.getMinutes()),
                  String.format("%02d", progress.getSeconds())
               )
            )
         );
         bossBar.setProgress(progress.getPercentComplete() / 100.0F);
         if (progress.isComplete()) {
            bossBar.removeAllPlayers();
            this.bossBars.remove(worldIdentifier);
         }
      }
   }

   private ServerBossEvent createNewBossBar(ResourceLocation worldIdentifier) {
      ServerBossEvent bossBar = new ServerBossEvent(Component.nullToEmpty(worldIdentifier.toString()), bossBarColor(worldIdentifier), BossBarOverlay.PROGRESS);
      bossBar.setDarkenScreen(false);
      bossBar.setPlayBossMusic(false);
      bossBar.setCreateWorldFog(false);
      return bossBar;
   }

   private static BossBarColor bossBarColor(ResourceLocation worldIdentifier) {
      BossBarColor bossBarColor;
      if (Level.OVERWORLD.location().equals(worldIdentifier)) {
         bossBarColor = BossBarColor.GREEN;
      } else if (Level.NETHER.location().equals(worldIdentifier)) {
         bossBarColor = BossBarColor.RED;
      } else if (Level.END.location().equals(worldIdentifier)) {
         bossBarColor = BossBarColor.PURPLE;
      } else {
         bossBarColor = BossBarColor.BLUE;
      }

      return bossBarColor;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void accept(Object param1) {
      this.accept((GenerationTaskUpdateEvent)var1);
   }
}
