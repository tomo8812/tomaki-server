package maki.lodcpu.mixin;

import com.kishku7.chunksmith.command.CancelCommand;
import com.kishku7.chunksmith.command.CommandArguments;
import com.kishku7.chunksmith.command.ContinueCommand;
import com.kishku7.chunksmith.command.PauseCommand;
import com.kishku7.chunksmith.command.StartCommand;
import com.kishku7.chunksmith.platform.Sender;
import maki.lodcpu.PlayerCenter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {PauseCommand.class, CancelCommand.class, ContinueCommand.class, StartCommand.class}, remap = false)
public abstract class NativeCommandMixin {
   @Inject(method = "execute(Lcom/kishku7/chunksmith/platform/Sender;Lcom/kishku7/chunksmith/command/CommandArguments;)V", at = @At("HEAD"), require = 1)
   private void maki$command(Sender var1, CommandArguments var2, CallbackInfo var3) {
      NativeCommandMixin var4 = this;
      if (!(var4 instanceof PauseCommand) && !(var4 instanceof CancelCommand)) {
         PlayerCenter.nativeResume();
      } else {
         PlayerCenter.nativePause();
      }
   }
}
