package dev.emi.emi.jemi.impl;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.config.FluidUnit;
import dev.emi.emi.jemi.JemiStack;
import dev.emi.emi.jemi.JemiUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class JemiIngredientAcceptor implements IIngredientAcceptor<JemiIngredientAcceptor> {
   public static final Pattern FLUID_END = Pattern.compile("(^|\\s)([\\d,]+)\\s*mB$");
   public final RecipeIngredientRole role;
   public final List<EmiStack> stacks = Lists.newArrayList();

   public JemiIngredientAcceptor(RecipeIngredientRole role) {
      this.role = role;
   }

   public void coerceStacks(IRecipeSlotTooltipCallback tooltipCallback, Map<IIngredientType<?>, JemiRecipeSlot.IngredientRenderer<?>> renderers) {
      if (tooltipCallback != null || renderers != null) {
         Iterator var3 = this.stacks.iterator();

         while (true) {
            EmiStack stack;
            List<Component> base;
            while (true) {
               if (!var3.hasNext()) {
                  return;
               }

               stack = (EmiStack)var3.next();
               ITypedIngredient typed = JemiUtil.getTyped(stack).orElse(null);
               if (typed != null && (stack instanceof JemiStack || stack.getKey() instanceof Fluid)) {
                  base = Lists.newArrayList();
                  if (renderers != null && renderers.containsKey(typed.getType())) {
                     base.addAll(renderers.get(typed.getType()).renderer().getTooltip(typed.getIngredient(), TooltipFlag.NORMAL));
                  }

                  if (base != null && !base.isEmpty()) {
                     break;
                  }

                  if (tooltipCallback != null) {
                     base.add(stack.getName());
                     base.add(EmiPort.literal(""));
                     break;
                  }
               }
            }

            if (tooltipCallback != null) {
               JemiRecipeSlot jsr = new JemiRecipeSlot(this.role, stack);
               tooltipCallback.onTooltip(jsr, base);
            }

            for (int i = 0; i < 2 && i < base.size(); i++) {
               Component t = base.get(i);
               if (t != null) {
                  Matcher m = FLUID_END.matcher(t.getString());
                  if (m.find()) {
                     long amount = Long.parseLong(m.group(2).replace(",", ""));
                     if (amount != stack.getAmount()) {
                        stack.setAmount(amount);
                     }
                  }
               }
            }
         }
      }
   }

   public EmiIngredient build() {
      return EmiIngredient.of(this.stacks);
   }

   private void addStack(EmiStack stack) {
      if (!stack.isEmpty()) {
         this.stacks.add(stack);
      }
   }

   public <I> JemiIngredientAcceptor addIngredients(IIngredientType<I> ingredientType, List<@Nullable I> ingredients) {
      for (I i : ingredients) {
         this.addIngredient(ingredientType, i);
      }

      return this;
   }

   public <I> JemiIngredientAcceptor addIngredient(IIngredientType<I> ingredientType, I ingredient) {
      this.addStack(JemiUtil.getStack(ingredientType, ingredient));
      return this;
   }

   public JemiIngredientAcceptor addIngredientsUnsafe(List<?> ingredients) {
      for (Object o : ingredients) {
         this.addStack(JemiUtil.getStack(o));
      }

      return this;
   }

   public JemiIngredientAcceptor addFluidStack(Fluid fluid) {
      return this.addFluidStack(fluid, FluidUnit.BUCKET);
   }

   public JemiIngredientAcceptor addFluidStack(Fluid fluid, long amount) {
      this.addStack(EmiStack.of(fluid, amount));
      return this;
   }

   public JemiIngredientAcceptor addFluidStack(Fluid fluid, long amount, DataComponentPatch componentChanges) {
      this.addStack(EmiStack.of(fluid, componentChanges, amount));
      return this;
   }

   public JemiIngredientAcceptor addTypedIngredients(List<ITypedIngredient<?>> ingredients) {
      for (ITypedIngredient<?> i : ingredients) {
         this.addIngredient(i.getType(), i.getIngredient());
      }

      return this;
   }

   public JemiIngredientAcceptor addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> ingredients) {
      for (Optional<ITypedIngredient<?>> opt : ingredients) {
         if (opt.isPresent()) {
            ITypedIngredient<?> i = opt.get();
            this.addIngredient(i.getType(), i.getIngredient());
         }
      }

      return this;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addFluidStack(Fluid param1, long nullx, DataComponentPatch nullxx) {
      return this.addFluidStack(var1, nullx, nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addFluidStack(Fluid param1, long nullx) {
      return this.addFluidStack(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addFluidStack(Fluid param1) {
      return this.addFluidStack(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addOptionalTypedIngredients(List param1) {
      return this.addOptionalTypedIngredients(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addTypedIngredients(List param1) {
      return this.addTypedIngredients(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addIngredientsUnsafe(List param1) {
      return this.addIngredientsUnsafe(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addIngredient(IIngredientType param1, Object nullx) {
      return this.addIngredient(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientAcceptor addIngredients(IIngredientType param1, List/* @Nullable */ nullx) {
      return this.addIngredients(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addFluidStack(Fluid param1, long nullx, DataComponentPatch nullxx) {
      return this.addFluidStack(var1, nullx, nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addFluidStack(Fluid param1, long nullx) {
      return this.addFluidStack(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addFluidStack(Fluid param1) {
      return this.addFluidStack(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addOptionalTypedIngredients(List param1) {
      return this.addOptionalTypedIngredients(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addTypedIngredients(List param1) {
      return this.addTypedIngredients(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addIngredientsUnsafe(List param1) {
      return this.addIngredientsUnsafe(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addIngredient(IIngredientType param1, Object nullx) {
      return this.addIngredient(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public IIngredientConsumer addIngredients(IIngredientType param1, List/* @Nullable */ nullx) {
      return this.addIngredients(var1, nullx);
   }
}
