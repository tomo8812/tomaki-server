package org.valkyrienskies.mod.mixin.mod_compat.sodium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import net.caffeinemc.mods.sodium.client.SodiumClientMod;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.TaskQueueType;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.OcclusionSectionCollector;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.SortBehavior;
import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.mixinducks.mod_compat.sodium.RenderSectionManagerDuck;

@Mixin(value = RenderSectionManager.class, remap = false)
public abstract class MixinRenderSectionManager implements RenderSectionManagerDuck {
   @Unique
   private final WeakHashMap<ClientShip, SortedRenderLists> shipRenderLists = new WeakHashMap<>();
   @Shadow
   @Final
   private class_638 level;
   @Shadow
   private SortedRenderLists renderLists;
   @Shadow
   private Map<TaskQueueType, ArrayDeque<RenderSection>> taskLists;
   @Shadow
   @Final
   private SortBehavior sortBehavior;

   @Override
   public WeakHashMap<ClientShip, SortedRenderLists> vs_getShipRenderLists() {
      return this.shipRenderLists;
   }

   @Shadow
   protected abstract RenderSection getRenderSection(int var1, int var2, int var3);

   @Shadow
   public abstract void tickVisibleRenders();

   @Inject(at = @At("TAIL"), method = "createTerrainRenderList", cancellable = true)
   private void afterIterateChunks(class_4184 camera, Viewport viewport, int frame, boolean spectator, CallbackInfoReturnable ci) {
      for (ClientShip ship : VSGameUtilsKt.getShipObjectWorld(class_310.method_1551()).getLoadedShips()) {
         OcclusionSectionCollector collector = new OcclusionSectionCollector(
            frame,
            SodiumClientMod.options().performance.chunkBuildDeferMode.getImportantRebuildQueueType(),
            this.sortBehavior.getDeferMode().getImportantRebuildQueueType()
         );
         ship.getActiveChunksSet().forEach((x, z) -> {
            class_2818 levelChunk = this.level.method_8497(x, z);

            for (int y = this.level.method_32891(); y < this.level.method_31597(); y++) {
               class_2826 levelChunkSection = levelChunk.method_38259(y - this.level.method_32891());
               if (!levelChunkSection.method_38292()) {
                  RenderSection section = this.getRenderSection(x, y, z);
                  if (section != null) {
                     collector.visit(section);
                  }
               }
            }
         });
         this.shipRenderLists.put(ship, collector.createRenderLists(viewport));
         ci.setReturnValue(ci.getReturnValueZ() | collector.needsRevisitForPendingUpdates());

         for (Entry<TaskQueueType, ArrayDeque<RenderSection>> entry : collector.getTaskLists().entrySet()) {
            this.taskLists.get(entry.getKey()).addAll(entry.getValue());
         }
      }
   }

   @WrapMethod(method = "tickVisibleRenders")
   private void tickVisibleShipRenders(Operation<Void> original) {
      original.call(new Object[0]);
      SortedRenderLists trueRenderLists = this.renderLists;

      for (SortedRenderLists currentShipRenderLists : this.shipRenderLists.values()) {
         this.renderLists = currentShipRenderLists;
         original.call(new Object[0]);
      }

      this.renderLists = trueRenderLists;
   }

   @Inject(at = @At("TAIL"), method = "resetRenderLists")
   private void afterResetLists(CallbackInfo ci) {
      this.shipRenderLists.clear();
   }
}
