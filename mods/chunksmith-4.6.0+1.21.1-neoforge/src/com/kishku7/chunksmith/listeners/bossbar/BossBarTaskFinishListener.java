package com.kishku7.chunksmith.listeners.bossbar;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.event.task.GenerationTaskFinishEvent;
import com.kishku7.chunksmith.platform.World;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;

public class BossBarTaskFinishListener implements Consumer<GenerationTaskFinishEvent> {
   private final Map<ResourceLocation, ServerBossEvent> bossBars;

   public BossBarTaskFinishListener(Map<ResourceLocation, ServerBossEvent> bossBars) {
      this.bossBars = bossBars;
   }

   public void accept(GenerationTaskFinishEvent event) {
      GenerationTask task = event.generationTask();
      World world = task.getSelection().world();
      ResourceLocation worldIdentifier = ResourceLocation.tryParse(world.getKey());
      if (worldIdentifier != null) {
         ServerBossEvent bossBar = this.bossBars.get(worldIdentifier);
         if (bossBar != null) {
            bossBar.removeAllPlayers();
            this.bossBars.remove(worldIdentifier);
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void accept(Object param1) {
      this.accept((GenerationTaskFinishEvent)var1);
   }
}
