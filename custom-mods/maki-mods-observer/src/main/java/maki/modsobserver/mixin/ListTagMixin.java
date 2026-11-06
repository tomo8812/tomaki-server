package maki.modsobserver.mixin;

import maki.modsobserver.NbtCopyObserver;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ListTag.class)
public abstract class ListTagMixin {
    @Inject(method = "copy()Lnet/minecraft/nbt/ListTag;", at = @At("HEAD"))
    private void maki$observeListCopyStart(CallbackInfoReturnable<ListTag> cir) {
        NbtCopyObserver.enterList((ListTag)(Object)this);
    }

    @Inject(method = "copy()Lnet/minecraft/nbt/ListTag;", at = @At("RETURN"))
    private void maki$observeListCopyEnd(CallbackInfoReturnable<ListTag> cir) {
        NbtCopyObserver.exitList();
    }
}
