package net.satisfy.candlelight.core.registry;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.satisfy.candlelight.Candlelight;
import net.satisfy.candlelight.core.effect.RefreshedEffect;
import net.satisfy.candlelight.core.effect.WellServedEffect;

public class MobEffectRegistry {
   private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create("candlelight", Registries.MOB_EFFECT);
   private static final Registrar<MobEffect> MOB_EFFECTS_REGISTRAR = MOB_EFFECTS.getRegistrar();
   public static final RegistrySupplier<MobEffect> REFRESHED = registerEffect("refreshed", RefreshedEffect::new);
   public static final RegistrySupplier<MobEffect> WELL_SERVED = registerEffect("well_served", WellServedEffect::new);

   private static RegistrySupplier<MobEffect> registerEffect(String name, Supplier<MobEffect> effect) {
      return Platform.isNeoForge() ? MOB_EFFECTS.register(name, effect) : MOB_EFFECTS_REGISTRAR.register(Candlelight.identifier(name), effect);
   }

   public static void init() {
      MOB_EFFECTS.register();
   }

   public static Holder<MobEffect> holder(RegistrySupplier<MobEffect> supplier) {
      return BuiltInRegistries.MOB_EFFECT
         .getResourceKey((MobEffect)supplier.get())
         .<Holder<MobEffect>>flatMap(BuiltInRegistries.MOB_EFFECT::getHolder)
         .orElseThrow();
   }
}
