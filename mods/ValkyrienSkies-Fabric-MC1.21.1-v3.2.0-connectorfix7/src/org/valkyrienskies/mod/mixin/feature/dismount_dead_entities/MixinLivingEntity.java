package org.valkyrienskies.mod.mixin.feature.dismount_dead_entities;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_3481;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_1309.class)
public abstract class MixinLivingEntity extends class_1297 {
   @Unique
   private static final Logger VS$LOGGER = LogManager.getLogger();

   public MixinLivingEntity(class_1299<?> entityType, class_1937 level) {
      super(entityType, level);
   }

   @Inject(method = "dismountVehicle", at = @At("HEAD"), cancellable = true)
   private void preDismountVehicle(class_1297 entity, CallbackInfo ci) {
      if ((
            !this.method_31481() && entity.method_31481()
               || this.method_37908().method_8477(entity.method_24515())
                  && this.method_37908().method_8320(entity.method_24515()).method_26164(class_3481.field_21780)
         )
         && VSGameUtilsKt.isBlockInShipyard(this.method_37908(), entity.method_19538())) {
         Ship ship = VSGameUtilsKt.getShipManagingPos(this.method_37908(), entity.method_19538());
         if (ship != null) {
            Vector3dc transformedPos = ship.getTransform().getShipToWorld().transformPosition(VectorConversionsMCKt.toJOML(entity.method_19538()));
            double d = Math.max(this.method_23318(), transformedPos.y());
            class_243 vec3 = new class_243(this.method_23317(), d, this.method_23321());
            this.method_33567(vec3.field_1352, vec3.field_1351, vec3.field_1350);
            ci.cancel();
         } else {
            VS$LOGGER.debug("Modifying strange dismount");
            class_243 vec3 = new class_243(this.method_23317(), this.method_23318(), this.method_23321());
            this.method_33567(vec3.field_1352, vec3.field_1351, vec3.field_1350);
            ci.cancel();
         }
      }
   }
}
