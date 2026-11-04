package maki.shipedge.mixin;

import maki.shipedge.ShipEdgeContact;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = 1100, remap = false)
public abstract class ShipEdgeMixin {
   @Shadow
   protected abstract boolean isStayingOnGroundSurface();

   @Inject(
      method = "maybeBackOffFromEdge(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/MoverType;)Lnet/minecraft/world/phys/Vec3;",
      at = @At("HEAD"),
      cancellable = true,
      remap = false
   )
   private void maki$oneTickShipContact(Vec3 var1, MoverType var2, CallbackInfoReturnable<Vec3> var3) {
      Player var4 = (Player)this;
      Vec3 var5 = ShipEdgeContact.query(var4, var1, var2, this.isStayingOnGroundSurface());
      if (var5 != null) {
         var3.setReturnValue(var5);
      }
   }
}
