package maki.shipanchor.mixin;

import maki.shipanchor.AnchorMover;
import maki.shipanchor.ShipAnchorState;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;

@Mixin(value = Entity.class, priority = 500, remap = false)
public abstract class EntityPassengerAnchorMixin implements ShipAnchorState {
   @Unique
   private boolean maki$shipAnchorHandoffPending;

   @Inject(method = "positionRider(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"), remap = false)
   private void maki$applyShipLocalPassengerAnchor(Entity var1, CallbackInfo var2) {
      Entity var3 = (Entity)(Object)this;
      if (var1 != null && var1.getVehicle() == var3) {
         ShipMountedToData var4 = VSGameUtilsKt.getShipMountedToData(var1, null);
         if (var4 != null && var4.getShipMountedTo() != null && var4.getMountPosInShip() != null) {
            ((ShipAnchorState)var1).maki$markShipAnchored();
            Vector3d var5 = var4.getShipMountedTo().getShipToWorld().transformPosition(new Vector3d(var4.getMountPosInShip()));
            AnchorMover.move(var1, var5);
         }
      }
   }

   @Override
   public void maki$markShipAnchored() {
      this.maki$shipAnchorHandoffPending = true;
   }

   @Override
   public boolean maki$consumeShipAnchorHandoff() {
      boolean var1 = this.maki$shipAnchorHandoffPending;
      this.maki$shipAnchorHandoffPending = false;
      return var1;
   }
}
