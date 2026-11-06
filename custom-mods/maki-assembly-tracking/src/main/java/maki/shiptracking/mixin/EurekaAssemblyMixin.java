package maki.shiptracking.mixin;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import maki.shiptracking.AssemblyTracking;
@Mixin(targets="org.valkyrienskies.eureka.util.ShipAssembler",remap=false)
public abstract class EurekaAssemblyMixin {
    @Inject(method="finishAssembly(Lnet/minecraft/server/level/ServerLevel;Ljava/util/Set;)Lorg/valkyrienskies/core/api/ships/ServerShip;",at=@At("RETURN"),require=1)
    private void maki$after(ServerLevel level,Set<BlockPos> blocks,CallbackInfoReturnable<ServerShip> ci){
        if(ci.getReturnValue()!=null)AssemblyTracking.assembled(level,blocks);
    }
}
