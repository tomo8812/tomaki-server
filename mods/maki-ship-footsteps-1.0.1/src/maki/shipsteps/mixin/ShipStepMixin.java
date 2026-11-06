package maki.shipsteps.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(value = Entity.class, remap = false)
public abstract class ShipStepMixin {
   @Unique
   private long maki$ship;
   @Unique
   private boolean maki$sampled;
   @Unique
   private double maki$x;
   @Unique
   private double maki$z;
   @Unique
   private int maki$lastMotionTick;
   @Unique
   private boolean maki$moved;

   @Inject(
      method = "move",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getMovementEmission()Lnet/minecraft/world/entity/Entity$MovementEmission;")
   )
   private void maki$sampleDeckMotion(MoverType mover, Vec3 movement, CallbackInfo ci) {
      if (this instanceof Player player && !player.isLocalPlayer()) {
         EntityDraggingInformation dragging = ((IEntityDraggingInformationProvider)player).getDraggingInformation();
         Long ship = dragging.getLastShipStoodOn();
         Vector3dc relative = player.level().isClientSide ? dragging.getRelativePositionOnShip() : dragging.getServerRelativePlayerPosition();
         if (ship != null && relative != null && player.onGround() && !player.isPassenger()) {
            if (this.maki$sampled && this.maki$ship == ship) {
               double dx = relative.x() - this.maki$x;
               double dz = relative.z() - this.maki$z;
               double distanceSquared = dx * dx + dz * dz;
               if (distanceSquared > 1.0E-4 && distanceSquared < 16.0) {
                  this.maki$moved = true;
                  this.maki$lastMotionTick = player.tickCount;
               } else if (distanceSquared >= 16.0 || player.tickCount - this.maki$lastMotionTick > 1) {
                  this.maki$moved = false;
               }
            } else {
               this.maki$moved = false;
            }

            this.maki$ship = ship;
            this.maki$x = relative.x();
            this.maki$z = relative.z();
            this.maki$sampled = true;
         } else {
            this.maki$sampled = false;
            this.maki$moved = false;
         }
      }
   }

   @ModifyArgs(
      method = "move",
      at = @At(
         value = "INVOKE",
         ordinal = 0,
         target = "Lnet/minecraft/world/entity/Entity;vibrationAndSoundEffectsFromBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;ZZLnet/minecraft/world/phys/Vec3;)Z"
      ),
      require = 1,
      allow = 1
   )
   private void maki$useDeckForSound(Args args) {
      if (this instanceof Player player && (Boolean)args.get(2) && player.onGround()) {
         BlockPos deck = player.getOnPos();
         Ship ship = VSGameUtilsKt.getShipManagingPos(player.level(), deck);
         if (ship != null) {
            boolean moving = player.isLocalPlayer()
               ? player.xxa != 0.0F || player.zza != 0.0F
               : this.maki$sampled && this.maki$ship == ship.getId() && this.maki$moved;
            if (!moving) {
               args.set(2, false);
            } else {
               BlockPos vanilla = (BlockPos)args.get(0);
               if (!deck.equals(vanilla) && VSGameUtilsKt.getShipManagingPos(player.level(), vanilla) == null) {
                  BlockState state = player.level().getBlockState(deck);
                  if (!state.isAir()) {
                     args.set(0, deck);
                     args.set(1, state);
                  }
               }
            }
         }
      }
   }
}
