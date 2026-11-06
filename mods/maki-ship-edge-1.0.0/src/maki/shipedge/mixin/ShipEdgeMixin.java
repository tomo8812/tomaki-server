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
   private void maki$oneTickShipContact(Vec3 movement, MoverType mover, CallbackInfoReturnable<Vec3> cir) {
      Player player = (Player)this;
      Vec3 adjusted = ShipEdgeContact.query(player, movement, mover, this.isStayingOnGroundSurface());
      if (adjusted != null) {
         cir.setReturnValue(adjusted);
      }
   }
}
