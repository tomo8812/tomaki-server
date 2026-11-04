package org.valkyrienskies.mod.mixin.feature.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.class_2314;
import net.minecraft.class_2316;
import net.minecraft.class_2319;
import net.minecraft.class_2378;
import net.minecraft.class_2314.class_7217;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.command.arguments.RelativeVector3Argument;
import org.valkyrienskies.mod.common.command.arguments.ShipArgument;
import org.valkyrienskies.mod.common.command.arguments.ShipArgumentInfo;

@Mixin(class_2316.class)
public class MixinArgumentTypeInfos {
   @Shadow
   private static <A extends ArgumentType<?>, T extends class_7217<A>> class_2314<A, T> method_10017(
      class_2378<class_2314<?, ?>> arg, String string, Class<? extends A> class_, class_2314<A, T> arg2
   ) {
      throw new IllegalStateException();
   }

   @Inject(method = "bootstrap", at = @At("TAIL"))
   private static void postBootstrap(class_2378<class_2314<?, ?>> registry, CallbackInfoReturnable<class_2314<?, ?>> ci) {
      method_10017(registry, "valkyrienskies:ship_argument", ShipArgument.class, new ShipArgumentInfo());
      method_10017(registry, "valkyrienskies:relative_vector3_argument", RelativeVector3Argument.class, class_2319.method_41999(RelativeVector3Argument::new));
   }
}
