package org.valkyrienskies.mod.mixin.feature.ship_debug_overlay;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_124;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3218;
import net.minecraft.class_340;
import net.minecraft.class_3965;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.world.ServerShipWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_340.class)
public abstract class MixinDebugScreenOverlay {
   @Shadow
   @Final
   private class_310 field_2079;
   @Shadow
   private class_239 field_2082;

   @Shadow
   protected abstract class_1937 method_1840();

   @Inject(method = "getGameInformation", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1))
   private void addShipCountInformation(CallbackInfoReturnable<List<String>> cir, @Local List<String> list) {
      class_1937 l = this.method_1840();
      if (l instanceof class_3218) {
         ServerShipWorld world = VSGameUtilsKt.getShipObjectWorld((class_3218)l);
         list.add("Ships Loaded: " + world.getLoadedShips().size() + "/" + world.getAllShips().size());
      }
   }

   @Inject(method = "getGameInformation", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 6, shift = Shift.AFTER))
   private void addPlayerDraggingInformation(CallbackInfoReturnable<List<String>> cir, @Local List<String> list) {
      EntityDraggingInformation info = ((IEntityDraggingInformationProvider)this.field_2079.field_1724).getDraggingInformation();
      if (info != null && info.isEntityBeingDraggedByAShip()) {
         Long shipId = info.getLastShipStoodOn();
         if (shipId != null) {
            Ship ship = VSGameUtilsKt.getAllShips(this.method_1840()).getById(shipId);
            if (ship != null) {
               list.add("Dragged by: " + VSGameUtilsKt.getAllShips(this.method_1840()).getById(info.getLastShipStoodOn()).getSlug());
            }
         }
      }
   }

   @Inject(method = "getSystemInformation", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
   private void addShipInformation(CallbackInfoReturnable<List<String>> cir, @Local List<String> list) {
      class_1937 l = this.method_1840();
      class_2338 blockPos = ((class_3965)this.field_2082).method_17777();
      Ship ship = VSGameUtilsKt.getShipManagingPos(l, blockPos);
      LoadedServerShip lsship = l instanceof class_3218 ? VSGameUtilsKt.getLoadedShipManagingPos((class_3218)l, blockPos) : null;
      if (ship != null) {
         list.add("");
         list.add(class_124.field_1073 + "Targeted Ship: " + ship.getSlug());
         if (lsship != null) {
            list.add("Static: " + lsship.isStatic());
            list.add("Mass: " + lsship.getInertiaData().getMass() + " kg");
            list.add("");
         }

         Vector3dc scale = ship.getTransform().getShipToWorldScaling();
         list.add(String.format(Locale.ROOT, "Ship Scale: %.3f / %.3f / %.3f", scale.x(), scale.y(), scale.z()));
         Vector3dc linVel = ship.getVelocity();
         list.add(String.format(Locale.ROOT, "Linear Velocity: %.3f / %.3f / %.3f, total: %.3f m/s", linVel.x(), linVel.y(), linVel.z(), linVel.length()));
         Vector3dc angVel = ship.getOmega();
         list.add(String.format(Locale.ROOT, "Angular Velocity: %.3f / %.3f / %.3f", angVel.x(), angVel.y(), angVel.z()));
         class_243 pos = VSGameUtilsKt.toWorldCoordinates(ship, blockPos.method_46558());
         list.add(String.format(Locale.ROOT, "Targeted World Position: %.3f / %.3f / %.3f", pos.method_10216(), pos.method_10214(), pos.method_10215()));
      }
   }
}
