package fuzs.puzzleslib.api.entity.v1;

import fuzs.puzzleslib.api.util.v1.CompoundTagHelper;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ServerLevelAccessor;

public final class VariantUtils {
   public static final String TAG_VARIANT = "variant";

   private VariantUtils() {
   }

   public static <T> Holder<T> getDefaultOrAny(RegistryAccess registryAccess, ResourceKey<T> key) {
      Registry<T> registry = registryAccess.registryOrThrow(key.registryKey());
      return (Holder<T>)registry.getHolder(key).or(registry::getAny).orElseThrow();
   }

   public static <T> Holder<T> getAny(RegistryAccess registryAccess, ResourceKey<? extends Registry<T>> registryKey) {
      return (Holder<T>)registryAccess.registryOrThrow(registryKey).getAny().orElseThrow();
   }

   public static <T> void writeVariant(CompoundTag output, Holder<T> variant) {
      variant.unwrapKey().ifPresent(resourceKey -> CompoundTagHelper.store(output, "variant", ResourceLocation.CODEC, resourceKey.location()));
   }

   public static <T> Optional<Holder<T>> readVariant(CompoundTag input, ResourceKey<? extends Registry<T>> registryKey, Provider lookup) {
      return CompoundTagHelper.<ResourceLocation>read(input, "variant", ResourceLocation.CODEC)
         .map(id -> ResourceKey.create(registryKey, id))
         .flatMap(key -> lookup.lookupOrThrow(key.registryKey()).get(key));
   }

   public static <T> Optional<Reference<T>> selectVariantToSpawn(ServerLevelAccessor serverLevel, ResourceKey<Registry<T>> registryKey) {
      return serverLevel.registryAccess().registryOrThrow(registryKey).getRandom(serverLevel.getRandom());
   }

   public static <T> Optional<Holder<T>> selectVariantToSpawn(ServerLevelAccessor serverLevel, ResourceKey<Registry<T>> registryKey, TagKey<T> tagKey) {
      return serverLevel.registryAccess().registryOrThrow(registryKey).getRandomElementOf(tagKey, serverLevel.getRandom());
   }
}
