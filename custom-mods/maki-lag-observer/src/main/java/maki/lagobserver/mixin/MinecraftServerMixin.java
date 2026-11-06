package maki.lagobserver.mixin;

import java.util.function.BooleanSupplier;
import maki.lagobserver.LagObserver;
import maki.lagobserver.NbtCopyObserver;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "tickServer", at = @At("HEAD"))
    private void maki$lagObserverTickStart(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer)(Object)this;
        LagObserver.tickStart(server);
        NbtCopyObserver.tickStart(server);
    }

    @Inject(method = "tickServer", at = @At("TAIL"))
    private void maki$lagObserverTickEnd(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer)(Object)this;
        NbtCopyObserver.tickEnd(server);
        LagObserver.tickEnd(server);
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void maki$lagObserverStop(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer)(Object)this;
        NbtCopyObserver.shutdown(server);
        LagObserver.shutdown(server);
    }
}
