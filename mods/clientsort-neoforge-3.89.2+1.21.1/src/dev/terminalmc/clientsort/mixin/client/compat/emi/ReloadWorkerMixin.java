package dev.terminalmc.clientsort.mixin.client.compat.emi;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.order.CreativeSearchOrder;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.emi.emi.runtime.EmiReloadManager$ReloadWorker", remap = false)
public abstract class ReloadWorkerMixin {
   @Inject(method = "run", at = @At("HEAD"))
   private void beforeRun(CallbackInfo ci) {
      ClientSort.updateBlockedByEmi = false;
      ClientSort.emiReloading = true;
   }

   @Inject(method = "run", at = @At("RETURN"))
   private void afterRun(CallbackInfo ci) {
      if (ClientSort.updateBlockedByEmi) {
         Minecraft.getInstance().execute(() -> {
            ClientSort.LOG.info("EMI reload finished; updating search order");
            CreativeSearchOrder.tryRefreshStackPositionMap();
         });
      }

      ClientSort.emiReloading = false;
   }
}
