package maki.shipanchor.mixin;

import java.util.Optional;
import maki.shipanchor.AnchorMover;
import maki.shipanchor.ShipAnchorState;
import maki.shipanchor.SleepAnchorAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(value = LivingEntity.class, remap = false)
public abstract class LivingSleepAnchorMixin implements SleepAnchorAccess {
   @Unique
   private static final long MAKI$NO_SHIP = Long.MIN_VALUE;
   @Unique
   private long maki$shipId = Long.MIN_VALUE;
   @Unique
   private int maki$bedX;
   @Unique
   private int maki$bedY;
   @Unique
   private int maki$bedZ;
   @Unique
   private Vector3d maki$localAnchor;

   @Inject(method = "startSleeping(Lnet/minecraft/core/BlockPos;)V", at = @At("RETURN"), remap = false)
   private void maki$capturePostPlacementSleepAnchor(BlockPos var1, CallbackInfo var2) {
      LivingEntity var3 = (LivingEntity)(Object)this;
      if (var3 instanceof Player) {
         this.maki$capture(var3, var1);
      }
   }

   @Inject(method = "tick()V", at = @At("TAIL"), remap = false)
   private void maki$followSleepAnchor(CallbackInfo var1) {
      LivingEntity var2 = (LivingEntity)(Object)this;
      if (var2 instanceof Player && var2.isSleeping()) {
         Optional<BlockPos> var3 = var2.getSleepingPos();
         if (var3.isEmpty()) {
            this.maki$clear();
         } else {
            BlockPos var4 = var3.get();
            LoadedShip var5 = VSGameUtilsKt.getLoadedShipManagingPos(var2.level(), var4);
            if (var5 == null) {
               this.maki$clear();
            } else {
               if (this.maki$localAnchor == null || this.maki$shipId != var5.getId() || !this.maki$sameBed(var4)) {
                  this.maki$capture(var2, var4);
                  if (this.maki$localAnchor == null || this.maki$shipId != var5.getId()) {
                     return;
                  }
               }

               ((ShipAnchorState)var2).maki$markShipAnchored();
               Vector3d var6 = var5.getShipToWorld().transformPosition(new Vector3d(this.maki$localAnchor));
               AnchorMover.move(var2, var6);
               var2.setDeltaMovement(Vec3.ZERO);
               var2.fallDistance = 0.0F;
            }
         }
      } else {
         this.maki$clear();
      }
   }

   @Inject(method = "stopSleeping()V", at = @At("HEAD"), remap = false)
   private void maki$releaseSleepAnchorBeforeWake(CallbackInfo var1) {
      this.maki$clear();
   }

   @Override
   public boolean maki$isSleepAnchorActive() {
      LivingEntity var1 = (LivingEntity)(Object)this;
      if (var1 instanceof Player && var1.isSleeping() && this.maki$localAnchor != null && this.maki$shipId != Long.MIN_VALUE) {
         Optional<BlockPos> var2 = var1.getSleepingPos();
         if (!var2.isEmpty() && this.maki$sameBed(var2.get())) {
            LoadedShip var3 = VSGameUtilsKt.getLoadedShipManagingPos(var1.level(), (Vec3i)var2.get());
            return var3 != null && var3.getId() == this.maki$shipId;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Unique
   private void maki$capture(LivingEntity var1, BlockPos var2) {
      LoadedShip var3 = VSGameUtilsKt.getLoadedShipManagingPos(var1.level(), var2);
      if (var3 == null) {
         this.maki$clear();
      } else {
         Vector3d var4 = var3.getWorldToShip().transformPosition(new Vector3d(var1.getX(), var1.getY(), var1.getZ()));
         if (!var4.isFinite()) {
            this.maki$clear();
         } else {
            this.maki$shipId = var3.getId();
            this.maki$bedX = var2.getX();
            this.maki$bedY = var2.getY();
            this.maki$bedZ = var2.getZ();
            this.maki$localAnchor = var4;
         }
      }
   }

   @Unique
   private boolean maki$sameBed(BlockPos var1) {
      return this.maki$bedX == var1.getX() && this.maki$bedY == var1.getY() && this.maki$bedZ == var1.getZ();
   }

   @Unique
   private void maki$clear() {
      this.maki$shipId = Long.MIN_VALUE;
      this.maki$localAnchor = null;
   }
}
