package net.satisfy.brewery.core.registry;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.effect.BlackoutEffect;
import net.satisfy.brewery.core.effect.CombustionEffect;
import net.satisfy.brewery.core.effect.ExplosionEffect;
import net.satisfy.brewery.core.effect.HaleyEffect;
import net.satisfy.brewery.core.effect.HealingTouchEffect;
import net.satisfy.brewery.core.effect.IntoxicationEffect;
import net.satisfy.brewery.core.effect.LightningStrikeEffect;
import net.satisfy.brewery.core.effect.MiningEffect;
import net.satisfy.brewery.core.effect.PacifyEffect;
import net.satisfy.brewery.core.effect.PartystarterEffect;
import net.satisfy.brewery.core.effect.PintCharismaEffect;
import net.satisfy.brewery.core.effect.ProtectiveTouchEffect;
import net.satisfy.brewery.core.effect.RenewingTouchEffect;
import net.satisfy.brewery.core.effect.RepulsionEffect;
import net.satisfy.brewery.core.effect.SnowWhiteEffect;
import net.satisfy.brewery.core.effect.StoutHeartEffect;
import net.satisfy.brewery.core.effect.ToxicTouchEffect;

public class MobEffectRegistry {
   private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create("brewery", Registries.MOB_EFFECT);
   private static final Registrar<MobEffect> MOB_EFFECTS_REGISTRAR = MOB_EFFECTS.getRegistrar();
   public static final RegistrySupplier<MobEffect> DRUNK = registerEffect("drunk", IntoxicationEffect::new);
   public static final RegistrySupplier<MobEffect> BLACKOUT = registerEffect("blackout", () -> new BlackoutEffect().setBlendDuration(22));
   public static final RegistrySupplier<MobEffect> MINING = registerEffect("mining", () -> new MiningEffect(MobEffectCategory.BENEFICIAL, 7032618));
   public static final RegistrySupplier<MobEffect> PACIFY = registerEffect("pacify", () -> new PacifyEffect(MobEffectCategory.BENEFICIAL, 8969710));
   public static final RegistrySupplier<MobEffect> REPULSION = registerEffect("repulsion", () -> new RepulsionEffect(MobEffectCategory.BENEFICIAL, 16729156));
   public static final RegistrySupplier<MobEffect> LIGHTNING_STRIKE = registerEffect(
      "lightning_strike", () -> new LightningStrikeEffect(MobEffectCategory.BENEFICIAL, 15132160)
   );
   public static final RegistrySupplier<MobEffect> EXPLOSION = registerEffect("explosion", () -> new ExplosionEffect(MobEffectCategory.BENEFICIAL, 16742144));
   public static final RegistrySupplier<MobEffect> COMBUSTION = registerEffect("combustion", () -> new CombustionEffect(MobEffectCategory.BENEFICIAL, 13378048));
   public static final RegistrySupplier<MobEffect> TOXICTOUCH = registerEffect("toxictouch", () -> new ToxicTouchEffect(MobEffectCategory.HARMFUL, 43588));
   public static final RegistrySupplier<MobEffect> RENEWINGTOUCH = registerEffect(
      "renewingtouch", () -> new RenewingTouchEffect(MobEffectCategory.BENEFICIAL, 6750105)
   );
   public static final RegistrySupplier<MobEffect> HEALINGTOUCH = registerEffect(
      "healingtouch", () -> new HealingTouchEffect(MobEffectCategory.BENEFICIAL, 16737996)
   );
   public static final RegistrySupplier<MobEffect> PROTECTIVETOUCH = registerEffect(
      "protectivetouch", () -> new ProtectiveTouchEffect(MobEffectCategory.BENEFICIAL, 3381759)
   );
   public static final RegistrySupplier<MobEffect> PARTYSTARTER = registerEffect(
      "partystarter", () -> new PartystarterEffect(MobEffectCategory.BENEFICIAL, 16724906)
   );
   public static final RegistrySupplier<MobEffect> SNOWWHITE = registerEffect("snowwhite", () -> new SnowWhiteEffect(MobEffectCategory.BENEFICIAL, 14745599));
   public static final RegistrySupplier<MobEffect> PINTCHARISMA = registerEffect(
      "pintcharisma", () -> new PintCharismaEffect(MobEffectCategory.BENEFICIAL, 16766720)
   );
   public static final RegistrySupplier<MobEffect> HALEY = registerEffect("haley", () -> new HaleyEffect(MobEffectCategory.BENEFICIAL, 16751103));
   public static final RegistrySupplier<MobEffect> STOUTHEART = registerEffect("stoutheart", StoutHeartEffect::new);

   private static RegistrySupplier<MobEffect> registerEffect(String name, Supplier<MobEffect> effect) {
      return Platform.isNeoForge() ? MOB_EFFECTS.register(name, effect) : MOB_EFFECTS_REGISTRAR.register(Brewery.identifier(name), effect);
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
