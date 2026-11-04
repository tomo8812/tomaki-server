package org.valkyrienskies.mod.mixin.feature.thrown_velocity;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1676;
import net.minecraft.class_1937;
import net.minecraft.class_8046;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_1676.class)
public abstract class MixinProjectile extends class_1297 implements class_8046 {
   @Shadow
   @Nullable
   public abstract class_1297 method_24921();

   public MixinProjectile(class_1299<?> entityType, class_1937 level) {
      super(entityType, level);
   }

   @Inject(method = "tick", at = @At("HEAD"))
   private void preFirstTick(CallbackInfo ci) {
      class_1297 owner = this.method_24921();
      if (this.field_5953 && owner != null) {
         Ship ship = VSGameUtilsKt.getShipMountedTo(owner);
         EntityDraggingInformation info = ((IEntityDraggingInformationProvider)owner).getDraggingInformation();
         if (ship == null && info.isEntityBeingDraggedByAShip()) {
            ship = ValkyrienSkies.getShipById(this.method_37908(), info.getLastShipStoodOn());
         }

         VSGameUtilsKt.applyShipVelocity(this, ship);
      }
   }
}
