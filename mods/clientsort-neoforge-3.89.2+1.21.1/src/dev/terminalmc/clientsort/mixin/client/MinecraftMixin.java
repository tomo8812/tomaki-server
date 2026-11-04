package dev.terminalmc.clientsort.mixin.client;

import dev.terminalmc.clientsort.client.ClientSort;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
   @Inject(
      method = "run",
      at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;gameThread:Ljava/lang/Thread;", shift = Shift.AFTER, ordinal = 0)
   )
   private void onStart(CallbackInfo ci) {
      ClientSort.afterGameStart();
   }
}
