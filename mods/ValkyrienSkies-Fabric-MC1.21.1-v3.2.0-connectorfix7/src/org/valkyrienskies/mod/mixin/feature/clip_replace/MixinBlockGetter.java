package org.valkyrienskies.mod.mixin.feature.clip_replace;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;
import org.valkyrienskies.mod.mixinducks.feature.clip_replace.ClipContextDuck;

@Debug(export = true)
@Mixin(class_1922.class)
public interface MixinBlockGetter {
   @WrapMethod(method = "clip")
   default class_3965 clip(class_3959 clipContext, Operation<class_3965> original) {
      if (!(this instanceof class_1937 level && !(clipContext instanceof ClipContextDuck ccd && ccd.getRayCastExtraParameter().getUseVanillaClip()))) {
         return (class_3965)original.call(new Object[]{clipContext});
      } else if (VSGameUtilsKt.getShipManagingPos(level, clipContext.method_17747()) != VSGameUtilsKt.getShipManagingPos(level, clipContext.method_17750())) {
         LogManager.getLogger()
            .warn(
               "Trying to clip from "
                  + clipContext.method_17750()
                  + " to "
                  + clipContext.method_17747()
                  + " wich one of them is in a shipyard wich is ... sus!!"
            );
         class_243 vec3 = clipContext.method_17750().method_1020(clipContext.method_17747());
         return class_3965.method_17778(
            clipContext.method_17747(),
            class_2350.method_10142(vec3.field_1352, vec3.field_1351, vec3.field_1350),
            class_2338.method_49638(clipContext.method_17747())
         );
      } else {
         return RaycastUtilsKt.clipIncludeShipsImpl(level, clipContext, original);
      }
   }
}
