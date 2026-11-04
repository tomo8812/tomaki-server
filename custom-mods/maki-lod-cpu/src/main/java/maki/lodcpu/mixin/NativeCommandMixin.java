package maki.lodcpu.mixin;
import com.kishku7.chunksmith.command.*;
import com.kishku7.chunksmith.platform.Sender;
import maki.lodcpu.PlayerCenter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value={PauseCommand.class,CancelCommand.class,ContinueCommand.class,StartCommand.class},remap=false)
public abstract class NativeCommandMixin {
    @Inject(method="execute(Lcom/kishku7/chunksmith/platform/Sender;Lcom/kishku7/chunksmith/command/CommandArguments;)V",at=@At("HEAD"),require=1)
    private void maki$command(Sender sender,CommandArguments args,CallbackInfo ci){
        Object self=this;
        if(self instanceof PauseCommand||self instanceof CancelCommand)PlayerCenter.nativePause();
        else PlayerCenter.nativeResume();
    }
}
