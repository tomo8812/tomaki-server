package maki.shiptracking.mixin;

import java.util.Set;
import maki.shiptracking.AssemblyTracking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;

@Mixin(targets = "org.valkyrienskies.eureka.util.ShipAssembler", remap = false)
public abstract class EurekaAssemblyMixin {
   @Inject(
      method = "finishAssembly(Lnet/minecraft/server/level/ServerLevel;Ljava/util/Set;)Lorg/valkyrienskies/core/api/ships/ServerShip;",
      at = @At("RETURN"),
      require = 1
   )
   private void maki$after(ServerLevel var1, Set<BlockPos> var2, CallbackInfoReturnable<ServerShip> var3) {
      if (var3.getReturnValue() != null) {
         AssemblyTracking.assembled(var1, var2);
      }
   }
}
