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
   private void maki$sampleDeckMotion(MoverType var1, Vec3 var2, CallbackInfo var3) {
      if (this instanceof Player var4 && !var4.isLocalPlayer()) {
         EntityDraggingInformation var14 = ((IEntityDraggingInformationProvider)var4).getDraggingInformation();
         Long var6 = var14.getLastShipStoodOn();
         Vector3dc var7 = var4.level().isClientSide ? var14.getRelativePositionOnShip() : var14.getServerRelativePlayerPosition();
         if (var6 != null && var7 != null && var4.onGround() && !var4.isPassenger()) {
            if (this.maki$sampled && this.maki$ship == var6) {
               double var8 = var7.x() - this.maki$x;
               double var10 = var7.z() - this.maki$z;
               double var12 = var8 * var8 + var10 * var10;
               if (var12 > 1.0E-4 && var12 < 16.0) {
                  this.maki$moved = true;
                  this.maki$lastMotionTick = var4.tickCount;
               } else if (var12 >= 16.0 || var4.tickCount - this.maki$lastMotionTick > 1) {
                  this.maki$moved = false;
               }
            } else {
               this.maki$moved = false;
            }

            this.maki$ship = var6;
            this.maki$x = var7.x();
            this.maki$z = var7.z();
            this.maki$sampled = true;
         } else {
            this.maki$sampled = this.maki$moved = false;
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
   private void maki$useDeckForSound(Args var1) {
      if (this instanceof Player var2 && (Boolean)var1.get(2) && var2.onGround()) {
         BlockPos var8 = var2.getOnPos();
         Ship var4 = VSGameUtilsKt.getShipManagingPos(var2.level(), var8);
         if (var4 != null) {
            boolean var5 = var2.isLocalPlayer() ? var2.xxa != 0.0F || var2.zza != 0.0F : this.maki$sampled && this.maki$ship == var4.getId() && this.maki$moved;
            if (!var5) {
               var1.set(2, false);
            } else {
               BlockPos var6 = (BlockPos)var1.get(0);
               if (!var8.equals(var6) && VSGameUtilsKt.getShipManagingPos(var2.level(), var6) == null) {
                  BlockState var7 = var2.level().getBlockState(var8);
                  if (!var7.isAir()) {
                     var1.set(0, var8);
                     var1.set(1, var7);
                  }
               }
            }
         }
      }
   }
}
