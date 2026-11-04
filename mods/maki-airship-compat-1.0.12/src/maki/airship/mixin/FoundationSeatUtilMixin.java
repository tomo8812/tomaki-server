package maki.airship.mixin;

import maki.airship.SeatCompat;
import maki.airship.ShipFurnitureSeats;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.seat.ChairEntity;
import net.satisfy.foundation.seat.SeatUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SeatUtil.class, remap = false)
public abstract class FoundationSeatUtilMixin {
   @Inject(
      method = "onUse(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;D)Lnet/minecraft/world/ItemInteractionResult;",
      at = @At("HEAD"),
      cancellable = true,
      remap = false
   )
   private static void maki$useWorkingShipVehicle(
      Level var0, Player var1, InteractionHand var2, BlockHitResult var3, double var4, CallbackInfoReturnable<ItemInteractionResult> var6
   ) {
      ItemInteractionResult var7 = ShipFurnitureSeats.use(var0, var1, var2, var3, var4);
      if (var7 != null) {
         var6.setReturnValue(var7);
      }
   }

   @Inject(
      method = "addChairEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/satisfy/foundation/seat/ChairEntity;Lnet/minecraft/core/BlockPos;)Z",
      at = @At("RETURN"),
      remap = false
   )
   private static void maki$rememberSeat(Level var0, BlockPos var1, ChairEntity var2, BlockPos var3, CallbackInfoReturnable<Boolean> var4) {
      if (Boolean.TRUE.equals(var4.getReturnValue())) {
         SeatCompat.remember(var0, var1, var2, var3);
      }
   }

   @Redirect(
      method = "onUse(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;D)Lnet/minecraft/world/ItemInteractionResult;",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;startRiding(Lnet/minecraft/world/entity/Entity;)Z"),
      remap = false
   )
   private static boolean maki$mountShipChair(Player var0, Entity var1) {
      return SeatCompat.mount(var0, var1);
   }
}
