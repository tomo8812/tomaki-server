package maki.idlepregen.mixin;

import java.util.function.BooleanSupplier;
import maki.idlepregen.IdlePregenHooks;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
   @Inject(method = "tickServer", at = @At("HEAD"))
   private void maki$idlePregenTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
      MinecraftServer server = (MinecraftServer)this;
      IdlePregenHooks.beginServerTick(server);
      IdlePregenHooks.tick(server);
   }

   @Inject(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerConnectionListener;tick()V", shift = Shift.AFTER))
   private void maki$idlePregenAfterConnections(CallbackInfo ci) {
      IdlePregenHooks.tick((MinecraftServer)this);
   }

   @Inject(method = "stopServer", at = @At("HEAD"))
   private void maki$idlePregenStop(CallbackInfo ci) {
      IdlePregenHooks.shutdown((MinecraftServer)this);
   }
}
