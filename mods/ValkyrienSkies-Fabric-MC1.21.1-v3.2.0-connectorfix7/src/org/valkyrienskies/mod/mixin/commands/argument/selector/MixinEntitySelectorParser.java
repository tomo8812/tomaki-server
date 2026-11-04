package org.valkyrienskies.mod.mixin.commands.argument.selector;

import com.mojang.brigadier.StringReader;
import net.minecraft.class_2300;
import net.minecraft.class_2303;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_2303.class)
public class MixinEntitySelectorParser {
   @Shadow
   @Final
   private StringReader field_10860;

   @Shadow
   private void method_9878() {
      throw new IllegalStateException();
   }

   @Shadow
   public class_2300 method_9871() {
      throw new IllegalStateException();
   }

   @Inject(method = "parse", at = @At("HEAD"), cancellable = true)
   private void preParse(CallbackInfoReturnable<class_2300> cir) {
      if (this.field_10860.canRead(2) && this.field_10860.peek() == '@' && this.field_10860.peek(1) == 'v') {
         this.method_9878();
         cir.setReturnValue(this.method_9871());
      }
   }
}
