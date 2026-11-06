package maki.modsobserver.mixin;

import maki.modsobserver.NbtCopyObserver;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompoundTag.class)
public abstract class CompoundTagMixin {
    @Inject(method = "copy()Lnet/minecraft/nbt/CompoundTag;", at = @At("HEAD"))
    private void maki$observeCompoundCopyStart(CallbackInfoReturnable<CompoundTag> cir) {
        NbtCopyObserver.enterCompound((CompoundTag)(Object)this);
    }

    @Inject(method = "copy()Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"))
    private void maki$observeCompoundCopyEnd(CallbackInfoReturnable<CompoundTag> cir) {
        NbtCopyObserver.exitCompound();
    }
}
