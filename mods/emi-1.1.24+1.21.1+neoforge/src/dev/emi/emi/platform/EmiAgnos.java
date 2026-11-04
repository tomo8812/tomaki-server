package dev.emi.emi.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.registry.EmiPluginContainer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.material.Fluid;

public abstract class EmiAgnos {
   public static EmiAgnos delegate;

   public static boolean isForge() {
      return delegate.isForgeAgnos();
   }

   protected abstract boolean isForgeAgnos();

   public static String getModName(String namespace) {
      return delegate.getModNameAgnos(namespace);
   }

   protected abstract String getModNameAgnos(String var1);

   public static Path getConfigDirectory() {
      return delegate.getConfigDirectoryAgnos();
   }

   protected abstract Path getConfigDirectoryAgnos();

   public static boolean isDevelopmentEnvironment() {
      return delegate.isDevelopmentEnvironmentAgnos();
   }

   protected abstract boolean isDevelopmentEnvironmentAgnos();

   public static boolean isModLoaded(String id) {
      return delegate.isModLoadedAgnos(id);
   }

   protected abstract boolean isModLoadedAgnos(String var1);

   public static List<String> getAllModNames() {
      return delegate.getAllModNamesAgnos();
   }

   protected abstract List<String> getAllModNamesAgnos();

   public static List<String> getAllModAuthors() {
      return delegate.getAllModAuthorsAgnos();
   }

   protected abstract List<String> getAllModAuthorsAgnos();

   public static List<String> getModsWithPlugins() {
      return delegate.getModsWithPluginsAgnos();
   }

   protected abstract List<String> getModsWithPluginsAgnos();

   public static List<EmiPluginContainer> getPlugins() {
      return delegate.getPluginsAgnos();
   }

   protected abstract List<EmiPluginContainer> getPluginsAgnos();

   public static void addBrewingRecipes(EmiRegistry registry) {
      delegate.addBrewingRecipesAgnos(registry);
   }

   protected abstract void addBrewingRecipesAgnos(EmiRegistry var1);

   public static List<ClientTooltipComponent> getItemTooltip(ItemStack stack) {
      return delegate.getItemTooltipAgnos(stack);
   }

   protected abstract List<ClientTooltipComponent> getItemTooltipAgnos(ItemStack var1);

   public static Component getFluidName(Fluid fluid, DataComponentPatch componentChanges) {
      return delegate.getFluidNameAgnos(fluid, componentChanges);
   }

   protected abstract Component getFluidNameAgnos(Fluid var1, DataComponentPatch var2);

   public static List<Component> getFluidTooltip(Fluid fluid, DataComponentPatch componentChanges) {
      return delegate.getFluidTooltipAgnos(fluid, componentChanges);
   }

   protected abstract List<Component> getFluidTooltipAgnos(Fluid var1, DataComponentPatch var2);

   public static boolean isFloatyFluid(FluidEmiStack stack) {
      return delegate.isFloatyFluidAgnos(stack);
   }

   protected abstract boolean isFloatyFluidAgnos(FluidEmiStack var1);

   public static void renderFluid(FluidEmiStack stack, PoseStack matrices, int x, int y, float delta) {
      renderFluid(stack, matrices, x, y, delta, 0, 0, 16, 16);
   }

   public static void renderFluid(FluidEmiStack stack, PoseStack matrices, int x, int y, float delta, int xOff, int yOff, int width, int height) {
      delegate.renderFluidAgnos(stack, matrices, x, y, delta, xOff, yOff, width, height);
   }

   protected abstract void renderFluidAgnos(FluidEmiStack var1, PoseStack var2, int var3, int var4, float var5, int var6, int var7, int var8, int var9);

   public static EmiStack createFluidStack(Object object) {
      return delegate.createFluidStackAgnos(object);
   }

   protected abstract EmiStack createFluidStackAgnos(Object var1);

   public static boolean canBatch(ItemStack stack) {
      return delegate.canBatchAgnos(stack);
   }

   protected abstract boolean canBatchAgnos(ItemStack var1);

   public static Map<Item, Integer> getFuelMap() {
      return delegate.getFuelMapAgnos();
   }

   protected abstract Map<Item, Integer> getFuelMapAgnos();

   public static BakedModel getBakedTagModel(ResourceLocation id) {
      return delegate.getBakedTagModelAgnos(id);
   }

   protected abstract BakedModel getBakedTagModelAgnos(ResourceLocation var1);

   public static boolean isEnchantable(ItemStack stack, Enchantment enchantment) {
      return delegate.isEnchantableAgnos(stack, enchantment);
   }

   protected abstract boolean isEnchantableAgnos(ItemStack var1, Enchantment var2);

   static {
      try {
         Class.forName("dev.emi.emi.platform.fabric.EmiAgnosFabric");
      } catch (Throwable var3) {
      }

      try {
         Class.forName("dev.emi.emi.platform.forge.EmiAgnosForge");
      } catch (Throwable var2) {
      }

      try {
         Class.forName("dev.emi.emi.platform.neoforge.EmiAgnosNeoForge");
      } catch (Throwable var1) {
      }
   }
}
