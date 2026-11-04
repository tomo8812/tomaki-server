package org.valkyrienskies.mod.mixin.feature.shipyard_entities;

import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.minecraft.class_4076;
import net.minecraft.class_5572;
import net.minecraft.class_5573;
import net.minecraft.class_7927;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.impl.hooks.VSEvents;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.mixinducks.world.OfLevel;
import org.valkyrienskies.mod.mixinducks.world.OfShip;

@Mixin(class_5573.class)
public abstract class MixinEntitySectionStorage implements OfLevel {
   @Unique
   private class_1937 level;
   @Unique
   private boolean loopingShips = false;
   @Unique
   private ConcurrentHashMap<Long, class_5572<class_1297>> delayedSections = new ConcurrentHashMap<>();

   @Shadow
   public abstract void method_31777(class_238 var1, class_7927<class_5572<?>> var2);

   @Override
   public class_1937 getLevel() {
      return this.level;
   }

   @Override
   public void setLevel(class_1937 level) {
      this.level = level;
      if (level.field_9236) {
         VSEvents.ShipLoadEventClient.Companion.on(this::handleShipLoad);
      }
   }

   private void handleShipLoad(VSEvents.ShipLoadEventClient event) {
      this.delayedSections.entrySet().removeIf(entry -> {
         Long l = entry.getKey();
         class_5572<class_1297> section = entry.getValue();
         if (VSGameUtilsKt.getShipManagingPos(this.level, class_4076.method_18686(l), class_4076.method_18690(l)) == event.getShip()) {
            ((OfShip)section).setShip(event.getShip());
            return true;
         } else {
            return false;
         }
      });
   }

   @Inject(method = "createSection", at = @At("RETURN"))
   void onSectionCreate(long l, CallbackInfoReturnable<class_5572<class_1297>> cir) {
      if (VSGameUtilsKt.getShipManagingPos(this.level, class_4076.method_18686(l), class_4076.method_18690(l)) == null
         && VSGameUtilsKt.isChunkInShipyard(this.level, class_4076.method_18686(l), class_4076.method_18690(l))) {
         this.delayedSections.put(l, (class_5572<class_1297>)cir.getReturnValue());
      }

      ((OfShip)cir.getReturnValue()).setShip(VSGameUtilsKt.getShipManagingPos(this.level, class_4076.method_18686(l), class_4076.method_18690(l)));
   }

   @Inject(method = "forEachAccessibleNonEmptySection", at = @At("HEAD"))
   void shipSections(class_238 aABB, class_7927<class_5572<?>> abortableIterationConsumer, CallbackInfo ci) {
      if (this.level != null && !this.loopingShips) {
         this.loopingShips = true;
         VSGameUtilsKt.getShipsIntersecting(this.level, aABB).forEach(ship -> {
            class_238 transformedAABB = VectorConversionsMCKt.toMinecraft(VectorConversionsMCKt.toJOML(aABB).transform(ship.getWorldToShip()));

            try {
               this.method_31777(transformedAABB, abortableIterationConsumer);
            } catch (IllegalArgumentException ex) {
               ex.printStackTrace();
            }
         });
         this.loopingShips = false;
      }
   }
}
