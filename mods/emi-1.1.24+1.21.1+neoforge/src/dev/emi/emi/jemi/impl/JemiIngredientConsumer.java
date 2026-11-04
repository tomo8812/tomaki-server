package dev.emi.emi.jemi.impl;

import com.google.common.collect.Lists;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.config.FluidUnit;
import dev.emi.emi.jemi.JemiUtil;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class JemiIngredientConsumer implements IIngredientConsumer {
   public List<EmiIngredient> stacks = Lists.newArrayList();

   public <I> IIngredientConsumer addIngredients(IIngredientType<I> ingredientType, List<@Nullable I> ingredients) {
      for (I i : ingredients) {
         this.addIngredient(ingredientType, i);
      }

      return this;
   }

   public <I> IIngredientConsumer addIngredient(IIngredientType<I> ingredientType, I ingredient) {
      this.stacks.add(JemiUtil.getStack(ingredientType, ingredient));
      return this;
   }

   public IIngredientConsumer addIngredientsUnsafe(List<?> ingredients) {
      for (Object o : ingredients) {
         this.stacks.add(JemiUtil.getStack(o));
      }

      return this;
   }

   public IIngredientConsumer addTypedIngredients(List<ITypedIngredient<?>> ingredients) {
      for (ITypedIngredient i : ingredients) {
         this.addIngredient(i.getType(), i.getIngredient());
      }

      return this;
   }

   public IIngredientConsumer addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> ingredients) {
      for (Optional<ITypedIngredient<?>> i : ingredients) {
         if (i.isPresent()) {
            ITypedIngredient t = i.get();
            this.addIngredient(t.getType(), t.getIngredient());
         }
      }

      return this;
   }

   public IIngredientConsumer addFluidStack(Fluid fluid) {
      return this.addFluidStack(fluid, FluidUnit.BUCKET);
   }

   public IIngredientConsumer addFluidStack(Fluid fluid, long amount) {
      return this.addFluidStack(fluid, amount, DataComponentPatch.EMPTY);
   }

   public IIngredientConsumer addFluidStack(Fluid fluid, long amount, DataComponentPatch component) {
      this.stacks.add(EmiStack.of(fluid, component, amount));
      return this;
   }
}
