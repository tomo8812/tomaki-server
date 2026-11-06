package maki.modsobserver.mixin;

import java.util.function.BooleanSupplier;
import maki.modsobserver.ModsObserver;
import maki.modsobserver.NbtCopyObserver;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "tickServer", at = @At("HEAD"))
    private void maki$modsObserverTickStart(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer)(Object)this;
        ModsObserver.tickStart(server);
        NbtCopyObserver.tickStart(server);
    }

    @Inject(method = "tickServer", at = @At("TAIL"))
    private void maki$modsObserverTickEnd(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer)(Object)this;
        NbtCopyObserver.tickEnd(server);
        ModsObserver.tickEnd(server);
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void maki$modsObserverStop(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer)(Object)this;
        NbtCopyObserver.shutdown(server);
        ModsObserver.shutdown(server);
    }
}
