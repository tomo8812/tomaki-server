package org.valkyrienskies.mod.mixin.feature.entity_fall_particles;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.mixin.accessors.entity.EntityAccessor;

@Mixin(class_1309.class)
public abstract class MixinLivingEntity extends class_1297 {
   public MixinLivingEntity(class_1299<?> entityType, class_1937 level) {
      super(entityType, level);
   }

   @Inject(method = "checkFallDamage", at = @At("HEAD"))
   private void toShip(double d, boolean bl, class_2680 blockState, class_2338 blockPos, CallbackInfo ci, @Share("originalPos") LocalRef<class_243> originalPos) {
      Ship ship = VSGameUtilsKt.getShipManagingPos(this.method_37908(), blockPos);
      if (ship != null) {
         originalPos.set(this.method_19538());
         Vector3d posInShip = ship.getTransform().getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(this.method_19538()));
         ((EntityAccessor)this).setPosNoUpdates(VectorConversionsMCKt.toMinecraft(posInShip));
      }
   }

   @Inject(method = "checkFallDamage", at = @At("RETURN"))
   private void toWorld(
      double d, boolean bl, class_2680 blockState, class_2338 blockPos, CallbackInfo ci, @Share("originalPos") LocalRef<class_243> originalPos
   ) {
      if (originalPos.get() != null) {
         ((EntityAccessor)this).setPosNoUpdates((class_243)originalPos.get());
      }
   }
}
