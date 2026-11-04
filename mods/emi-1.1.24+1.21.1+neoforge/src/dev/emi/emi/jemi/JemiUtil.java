package dev.emi.emi.jemi;

import com.google.common.collect.Sets;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.platform.EmiAgnos;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class JemiUtil {
   public static EmiIngredient getIngredient(List<ITypedIngredient<?>> ingredients) {
      return ingredients.isEmpty() ? EmiStack.EMPTY : EmiIngredient.of(ingredients.stream().map(JemiUtil::getStack).filter(i -> !i.isEmpty()).toList());
   }

   public static EmiStack getStack(Object ingredient) {
      Optional<IIngredientType> optional = JemiPlugin.runtime.getIngredientManager().getIngredientTypeChecked(ingredient);
      return optional.isPresent() ? getStack(optional.get(), ingredient) : EmiStack.EMPTY;
   }

   public static EmiStack getStack(ITypedIngredient<?> ingredient) {
      return getStack(ingredient.getType(), ingredient.getIngredient());
   }

   public static EmiStack getStack(IIngredientType<?> type, Object ingredient) {
      if (type == VanillaTypes.ITEM_STACK) {
         return EmiStack.of((ItemStack)ingredient);
      }

      if (type == getFluidType()) {
         return EmiAgnos.createFluidStack(ingredient);
      }

      IIngredientManager im = JemiPlugin.runtime.getIngredientManager();
      IIngredientHelper helper = im.getIngredientHelper(type);
      return helper.isValidIngredient(ingredient)
         ? new JemiStack<>((IIngredientType<Object>)type, helper, im.getIngredientRenderer(type), ingredient)
         : EmiStack.EMPTY;
   }

   public static Optional<ITypedIngredient<?>> getTyped(EmiStack stack) {
      if (stack.isEmpty()) {
         return Optional.empty();
      } else if (stack.getKey() instanceof Fluid f) {
         return JemiPlugin.runtime
            .getIngredientManager()
            .createTypedIngredient(
               getFluidType(),
               getFluidHelper().create(f.builtInRegistryHolder(), stack.getAmount() == 0L ? 1000L : stack.getAmount(), stack.getComponentChanges())
            );
      } else {
         return stack instanceof JemiStack js
            ? JemiPlugin.runtime
               .getIngredientManager()
               .getIngredientTypeChecked(js.ingredient)
               .map(t -> JemiPlugin.runtime.getIngredientManager().createTypedIngredient(t, js.ingredient))
               .orElse(Optional.empty())
            : JemiPlugin.runtime.getIngredientManager().createTypedIngredient(VanillaTypes.ITEM_STACK, stack.getItemStack());
      }
   }

   public static IPlatformFluidHelper getFluidHelper() {
      return JemiPlugin.runtime.getJeiHelpers().getPlatformFluidHelper();
   }

   public static IIngredientType getFluidType() {
      return getFluidHelper().getFluidIngredientType();
   }

   public static Set<String> getHandledMods() {
      Set<String> set = Sets.newHashSet();

      for (String mod : EmiAgnos.getModsWithPlugins()) {
         set.add(mod);
      }

      return set;
   }
}
