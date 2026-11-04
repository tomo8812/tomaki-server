package net.satisfy.herbalbrews.core.registry;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.satisfy.herbalbrews.core.effects.BalancedEffect;
import net.satisfy.herbalbrews.core.effects.BondingEffect;
import net.satisfy.herbalbrews.core.effects.DeeprushEffect;
import net.satisfy.herbalbrews.core.effects.FeralEffect;
import net.satisfy.herbalbrews.core.effects.FortuneEffect;
import net.satisfy.herbalbrews.core.effects.LifeleechEffect;
import net.satisfy.herbalbrews.core.effects.ToughEffect;
import net.satisfy.herbalbrews.core.util.HerbalBrewsIdentifier;

public class EffectRegistry {
   private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create("herbalbrews", Registries.MOB_EFFECT);
   private static final Registrar<MobEffect> MOB_EFFECTS_REGISTRAR = MOB_EFFECTS.getRegistrar();
   public static final RegistrySupplier<MobEffect> DEEPRUSH = registerEffect("deeprush", DeeprushEffect::new);
   public static final RegistrySupplier<MobEffect> BONDING = registerEffect("bonding", BondingEffect::new);
   public static final RegistrySupplier<MobEffect> FORTUNE = registerEffect("fortune", FortuneEffect::new);
   public static final RegistrySupplier<MobEffect> BALANCED = registerEffect("balanced", BalancedEffect::new);
   public static final RegistrySupplier<MobEffect> FERAL = registerEffect("feral", FeralEffect::new);
   public static final RegistrySupplier<MobEffect> LIFELEECH = registerEffect("lifeleech", LifeleechEffect::new);
   public static final RegistrySupplier<MobEffect> TOUGH = registerEffect("tough", ToughEffect::new);

   private static RegistrySupplier<MobEffect> registerEffect(String name, Supplier<MobEffect> effect) {
      return Platform.isNeoForge() ? MOB_EFFECTS.register(name, effect) : MOB_EFFECTS_REGISTRAR.register(HerbalBrewsIdentifier.identifier(name), effect);
   }

   public static void init() {
      MOB_EFFECTS.register();
   }
}
