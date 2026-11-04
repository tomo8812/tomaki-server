package net.satisfy.bakery.core.registry;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.satisfy.bakery.core.recipe.Filling;

public class DataComponentRegistry {
   private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create("bakery", Registries.DATA_COMPONENT_TYPE);
   public static final RegistrySupplier<DataComponentType<String>> PERFECT_JAM = COMPONENTS.register(
      "perfect_jam", () -> DataComponentType.builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build()
   );
   public static final RegistrySupplier<DataComponentType<Filling>> FILLING = COMPONENTS.register(
      "filling", () -> DataComponentType.builder().persistent(Filling.CODEC).networkSynchronized(Filling.STREAM_CODEC).build()
   );

   public static void init() {
      COMPONENTS.register();
   }
}
