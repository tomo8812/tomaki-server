package maki.fndseat.mixin;

import maki.fndseat.SeatCompat;
import maki.fndseat.ShipFurnitureSeats;
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
      Level level, Player player, InteractionHand hand, BlockHitResult hit, double height, CallbackInfoReturnable<ItemInteractionResult> cir
   ) {
      ItemInteractionResult result = ShipFurnitureSeats.use(level, player, hand, hit, height);
      if (result != null) {
         cir.setReturnValue(result);
      }
   }

   @Inject(
      method = "addChairEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/satisfy/foundation/seat/ChairEntity;Lnet/minecraft/core/BlockPos;)Z",
      at = @At("RETURN"),
      remap = false
   )
   private static void maki$rememberSeat(Level level, BlockPos key, ChairEntity chair, BlockPos dismount, CallbackInfoReturnable<Boolean> cir) {
      if (Boolean.TRUE.equals(cir.getReturnValue())) {
         SeatCompat.remember(level, key, chair, dismount);
      }
   }

   @Redirect(
      method = "onUse(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;D)Lnet/minecraft/world/ItemInteractionResult;",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;startRiding(Lnet/minecraft/world/entity/Entity;)Z"),
      remap = false
   )
   private static boolean maki$mountShipChair(Player player, Entity chair) {
      return SeatCompat.mount(player, chair);
   }
}
