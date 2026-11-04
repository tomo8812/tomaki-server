package fuzs.puzzleslib.impl.init;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.util.v1.ARGB;
import fuzs.puzzleslib.api.util.v1.HSV;
import fuzs.puzzleslib.impl.core.Freezable;
import fuzs.puzzleslib.impl.item.CreativeModeTabHelper;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.CreativeModeTab.Builder;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import org.apache.commons.lang3.StringUtils;

public abstract class RegistryManagerImpl implements RegistryManager, Freezable {
   protected final String modId;
   protected Set<ModLoader> allowedModLoaders = EnumSet.allOf(ModLoader.class);

   protected RegistryManagerImpl(String modId) {
      this.modId = modId;
      Preconditions.checkArgument(StringUtils.isNotEmpty(modId), "mod id is invalid");
   }

   @Override
   public ResourceLocation makeKey(String path) {
      Preconditions.checkArgument(StringUtils.isNotEmpty(path), "path is invalid");
      return ResourceLocation.fromNamespaceAndPath(this.modId, path);
   }

   public RegistryManager whenOn(ModLoader... allowedModLoaders) {
      Preconditions.checkState(allowedModLoaders.length > 0, "mod loaders is empty");
      this.allowedModLoaders = EnumSet.copyOf(Arrays.asList(allowedModLoaders));
      return this;
   }

   @Override
   public final <T> Reference<T> register(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
      this.isWritableOrThrow();
      return this.register(registryKey, path, supplier, false);
   }

   public final <T> Reference<T> register(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier, boolean skipRegistration) {
      Objects.requireNonNull(registryKey, "registry key is null");
      Preconditions.checkArgument(StringUtils.isNotEmpty(path), "path is invalid");
      Objects.requireNonNull(supplier, "supplier is null");
      Reference<T> holder;
      if (!this.allowedModLoaders.contains(ModLoaderEnvironment.INSTANCE.getModLoader())) {
         holder = this.registerLazily(registryKey, path);
      } else {
         holder = this.getHolderReference(registryKey, path, supplier, skipRegistration);
      }

      this.allowedModLoaders = EnumSet.allOf(ModLoader.class);
      Objects.requireNonNull(holder, "holder is null");
      return holder;
   }

   protected abstract <T> Reference<T> getHolderReference(ResourceKey<? extends Registry<? super T>> var1, String var2, Supplier<T> var3, boolean var4);

   @Override
   public Reference<Item> registerLegacySpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeHolder, int backgroundColor) {
      return this.registerLegacySpawnEggItem(entityTypeHolder, backgroundColor, this.generateSpawnEggHighlightColor(backgroundColor));
   }

   private int generateSpawnEggHighlightColor(int backgroundColor) {
      int hsv = HSV.rgbToHsv(ARGB.redFloat(backgroundColor), ARGB.greenFloat(backgroundColor), ARGB.blueFloat(backgroundColor));
      float saturation = Math.min(1.0F, HSV.saturationFloat(hsv) * 1.2F);
      float value = Math.max(0.0F, HSV.valueFloat(hsv) * 0.75F);
      return Mth.hsvToRgb(HSV.hueFloat(hsv), saturation, value);
   }

   @Override
   public Reference<CreativeModeTab> registerCreativeModeTab(
      String path, Supplier<ItemStack> iconSupplier, UnaryOperator<DisplayItemsGenerator> displayItems, boolean hasSearchBar
   ) {
      return this.register(Registries.CREATIVE_MODE_TAB, path, () -> {
         Builder builder = this.getCreativeModeTabBuilder(hasSearchBar);
         builder.title(CreativeModeTabHelper.getTitle(this.makeKey(path)));
         builder.icon(iconSupplier);
         builder.displayItems((parameters, output) -> {
            Collection<ItemStack> tabContents = ItemStackLinkedSet.createTypeAndComponentsSet();
            displayItems.apply(CreativeModeTabHelper.getDisplayItems(this.modId)).accept(parameters, (itemStack, tabVisibility) -> {
               if (!tabContents.contains(itemStack) || tabVisibility == TabVisibility.SEARCH_TAB_ONLY) {
                  tabContents.add(itemStack);
                  output.accept(itemStack, tabVisibility);
               }
            });
         });
         return builder.build();
      });
   }

   protected abstract Builder getCreativeModeTabBuilder(boolean var1);

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object whenOn(ModLoader[] param1) {
      return this.whenOn(var1);
   }
}
