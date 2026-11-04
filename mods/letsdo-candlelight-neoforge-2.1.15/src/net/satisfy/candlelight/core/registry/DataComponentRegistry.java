package net.satisfy.candlelight.core.registry;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

public class DataComponentRegistry {
   private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create("candlelight", Registries.DATA_COMPONENT_TYPE);
   public static final RegistrySupplier<DataComponentType<String>> TABLE_SIGN_TEXT = DATA_COMPONENT_TYPES.register(
      "table_sign_text", () -> DataComponentType.builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build()
   );

   public static void init() {
      DATA_COMPONENT_TYPES.register();
   }
}
