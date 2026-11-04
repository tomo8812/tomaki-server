package org.valkyrienskies.mod.mixin.world.entity;

import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.MinecraftPlayer;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.mixinducks.world.entity.PlayerDuck;

@Mixin(class_1657.class)
public abstract class MixinPlayer extends class_1309 implements PlayerDuck {
   @Unique
   private final MinecraftPlayer vsPlayer = new MinecraftPlayer(class_1657.class.cast(this));
   @Unique
   private class_243 queuedPositionUpdate = null;
   @Unique
   private boolean handledMovePacket = false;

   protected MixinPlayer(class_1299<? extends class_1309> entityType, class_1937 level) {
      super(entityType, level);
   }

   @Override
   public MinecraftPlayer vs_getPlayer() {
      return this.vsPlayer;
   }

   @Override
   public class_243 vs_getQueuedPositionUpdate() {
      return this.queuedPositionUpdate;
   }

   @Override
   public void vs_setQueuedPositionUpdate(class_243 queuedPositionUpdate) {
      this.queuedPositionUpdate = queuedPositionUpdate;
   }

   @Override
   public boolean vs_handledMovePacket() {
      return this.handledMovePacket;
   }

   @Override
   public void vs_setHandledMovePacket(boolean handledMovePacket) {
      this.handledMovePacket = handledMovePacket;
   }

   @Shadow
   public abstract double method_55754();

   @Inject(method = "canInteractWithBlock", at = @At("RETURN"), cancellable = true)
   private void includeShipsInDistanceCheck(class_2338 blockPos, double reachDistance, CallbackInfoReturnable<Boolean> cir) {
      if (!cir.getReturnValueZ()) {
         Ship ship = VSGameUtilsKt.getShipManagingPos(this.method_37908(), blockPos);
         if (ship != null) {
            double e = this.method_55754() + reachDistance;
            class_243 eyePosInShip = VectorConversionsMCKt.toMinecraft(
               ship.getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(this.method_33571()))
            );
            double distanceSq = new class_238(blockPos).method_49271(eyePosInShip)
               * ship.getTransform().getShipToWorldScaling().x()
               * ship.getTransform().getShipToWorldScaling().x();
            cir.setReturnValue(distanceSq < e * e);
         }
      }
   }
}
