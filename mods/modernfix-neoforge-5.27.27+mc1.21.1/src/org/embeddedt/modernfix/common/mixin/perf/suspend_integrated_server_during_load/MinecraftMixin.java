package org.embeddedt.modernfix.common.mixin.perf.suspend_integrated_server_during_load;

import net.minecraft.client.Minecraft;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
@ClientOnlyMixin
public class MinecraftMixin {
   @Redirect(method = "doWorldLoad", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;yield()V"))
   private void sleepInsteadOfYield() {
      try {
         Thread.sleep(16L);
      } catch (InterruptedException var2) {
      }
   }
}
