package org.valkyrienskies.mod.mixin.server.command;

import net.minecraft.class_2170;
import net.minecraft.class_7157;
import net.minecraft.class_2170.class_5364;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_2170.class)
public class MixinCommands {
   @Inject(at = @At("TAIL"), method = "<init>")
   public void onInit(class_5364 commandSelection, class_7157 commandBuildContext, CallbackInfo ci) {
   }
}
