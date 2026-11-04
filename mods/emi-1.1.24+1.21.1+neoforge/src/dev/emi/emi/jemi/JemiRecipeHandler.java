package dev.emi.emi.jemi;

import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.RecipeFillButtonWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.jemi.impl.JemiRecipeLayoutBuilder;
import dev.emi.emi.jemi.impl.JemiRecipeSlot;
import dev.emi.emi.jemi.impl.JemiRecipeSlotsView;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiLog;
import dev.emi.emi.screen.EmiScreenManager;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferError.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public class JemiRecipeHandler<T extends AbstractContainerMenu, R> implements EmiRecipeHandler<T> {
   private final RecipeType<R> type;
   public IRecipeTransferHandler<T, R> handler;

   public JemiRecipeHandler(IRecipeTransferHandler<T, R> handler) {
      this.handler = handler;
      this.type = handler.getRecipeType();
   }

   @Override
   public boolean alwaysDisplaySupport(EmiRecipe recipe) {
      return this.type != null;
   }

   @Override
   public EmiPlayerInventory getInventory(AbstractContainerScreen<T> screen) {
      return new EmiPlayerInventory(List.of());
   }

   @Override
   public boolean supportsRecipe(EmiRecipe recipe) {
      return (this.type == null || this.getRawRecipe(recipe) != null) && recipe.supportsRecipeTree();
   }

   @Override
   public boolean canCraft(EmiRecipe recipe, EmiCraftContext<T> context) {
      IRecipeTransferError err = this.jeiCraft(recipe, context, false, null);
      return err == null || err.getType().allowsTransfer;
   }

   @Override
   public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context) {
      IRecipeTransferError err = this.jeiCraft(recipe, context, true, null);
      if (err == null || err.getType().allowsTransfer) {
         Minecraft.getInstance().setScreen(context.getScreen());
      }

      return err == null || err.getType().allowsTransfer;
   }

   @Override
   public void render(EmiRecipe recipe, EmiCraftContext<T> context, List<Widget> widgets, GuiGraphics raw) {
      EmiDrawContext draw = EmiDrawContext.wrap(raw);
      R rawRecipe = this.getRawRecipe(recipe);
      JemiRecipeSlotsView view = createSlotsView(recipe, rawRecipe, this.type, widgets);
      IRecipeTransferError err = this.jeiCraft(recipe, context, false, view);
      if (err != null) {
         if (err.getType() == Type.COSMETIC) {
            for (Widget widget : widgets) {
               if (widget instanceof RecipeFillButtonWidget) {
                  Bounds b = widget.getBounds();
                  draw.fill(b.left(), b.top(), b.width(), b.height(), err.getButtonHighlightColor());
               }
            }
         }

         if (view != null) {
            view.getSlotViews().forEach(v -> {
               if (v instanceof JemiRecipeSlot jrs) {
                  jrs.highlight = 0;
               }
            });
            draw.push();
            draw.matrices().translate(-100000.0F, -100000.0F, -100000.0F);
            draw.matrices().scale(0.0F, 0.0F, 0.0F);
            err.showError(raw, EmiScreenManager.lastMouseX, EmiScreenManager.lastMouseY, view, 0, 0);
            draw.pop();
            view.getSlotViews().forEach(v -> {
               if (v instanceof JemiRecipeSlot jrs && jrs.highlight != 0 && !jrs.isEmpty()) {
                  draw.fill(jrs.x, jrs.y, 18, 18, jrs.highlight);
               }
            });
         }
      }
   }

   private IRecipeTransferError jeiCraft(EmiRecipe recipe, EmiCraftContext<T> context, boolean craft, JemiRecipeSlotsView view) {
      try {
         Minecraft client = Minecraft.getInstance();
         R rawRecipe = this.getRawRecipe(recipe);
         if (view == null) {
            view = createSlotsView(recipe, rawRecipe, this.type, List.of());
         }

         return view == null
            ? () -> Type.INTERNAL
            : this.handler
               .transferRecipe(context.getScreenHandler(), rawRecipe != null ? rawRecipe : recipe, view, client.player, context.getAmount() > 1, craft);
      } catch (Exception e) {
         EmiLog.error("Error executing JEI craft", e);
         return () -> Type.INTERNAL;
      }
   }

   public static <R> JemiRecipeSlotsView createSlotsView(EmiRecipe recipe, R rawRecipe, RecipeType<R> type, List<Widget> widgets) {
      JemiRecipeLayoutBuilder builder = null;
      if (rawRecipe == null && type != null) {
         return null;
      }

      if (builder == null) {
         List<SlotWidget> slotWidgets = widgets.stream().filter(w -> w instanceof SlotWidget).map(w -> (SlotWidget)w).toList();
         builder = new JemiRecipeLayoutBuilder();
         addIngredients(builder, slotWidgets, recipe.getOutputs(), RecipeIngredientRole.OUTPUT);
         int blankedSlots = 0;
         if (recipe instanceof EmiCraftingRecipe ecr) {
            if (ecr.shapeless) {
               int inputSize = recipe.getInputs().size();
               if (inputSize == 1) {
                  addBlankIngredients(builder, slotWidgets, 4, RecipeIngredientRole.INPUT);
                  blankedSlots += 4;
                  addIngredients(builder, slotWidgets, recipe.getInputs(), RecipeIngredientRole.INPUT);
               } else if (inputSize < 5) {
                  int wrap = 0;

                  for (EmiIngredient i : recipe.getInputs()) {
                     addIngredients(builder, slotWidgets, List.of(i), RecipeIngredientRole.INPUT);
                     if (++wrap >= 2) {
                        wrap = 0;
                        addBlankIngredients(builder, slotWidgets, 1, RecipeIngredientRole.INPUT);
                        blankedSlots++;
                     }
                  }
               } else {
                  addIngredients(builder, slotWidgets, recipe.getInputs(), RecipeIngredientRole.INPUT);
               }
            } else {
               if (ecr.canFit(1, 3)) {
                  addBlankIngredients(builder, slotWidgets, 1, RecipeIngredientRole.INPUT);
                  blankedSlots++;
               } else if (ecr.canFit(3, 1) || ecr.canFit(3, 2) && !ecr.canFit(2, 2)) {
                  addBlankIngredients(builder, slotWidgets, 3, RecipeIngredientRole.INPUT);
                  blankedSlots += 3;
               }

               addIngredients(
                  builder, slotWidgets, recipe.getInputs().subList(0, Math.max(9, recipe.getInputs().size()) - blankedSlots), RecipeIngredientRole.INPUT
               );
            }
         } else {
            addIngredients(builder, slotWidgets, recipe.getInputs(), RecipeIngredientRole.INPUT);
         }

         if (recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING) {
            for (int i = recipe.getInputs().size() + blankedSlots; i < 9; i++) {
               addIngredients(builder, slotWidgets, List.of(EmiStack.EMPTY), RecipeIngredientRole.INPUT);
            }
         }

         addIngredients(builder, slotWidgets, recipe.getCatalysts(), RecipeIngredientRole.CATALYST);
      }

      return new JemiRecipeSlotsView(builder.slots.stream().map(JemiRecipeSlot::new).toList());
   }

   private R getRawRecipe(EmiRecipe recipe) {
      try {
         Minecraft client = Minecraft.getInstance();
         RecipeManager manager = client.level.getRecipeManager();
         if (this.type != null && this.type.getRecipeClass() != null) {
            if (recipe instanceof JemiRecipe jr && jr.recipe != null && this.type.getRecipeClass().isAssignableFrom(jr.recipe.getClass())) {
               return (R)this.type.getRecipeClass().cast(jr.recipe);
            }

            if (manager != null) {
               Optional<? extends RecipeHolder<?>> opt = manager.byKey(recipe.getId());
               if (opt.isPresent()) {
                  RecipeHolder<?> r = (RecipeHolder<?>)opt.get();
                  if (this.type.getRecipeClass().isAssignableFrom(r.getClass())) {
                     return (R)this.type.getRecipeClass().cast(r);
                  }
               }
            }
         }

         if (manager != null) {
            Optional<? extends RecipeHolder<?>> opt = manager.byKey(recipe.getId());
            if (opt.isPresent()) {
               return (R)opt.get();
            }
         }
      } catch (Exception var6) {
      }

      return null;
   }

   private static void addBlankIngredients(JemiRecipeLayoutBuilder builder, List<SlotWidget> widgets, int amount, RecipeIngredientRole role) {
      for (int i = 0; i < amount; i++) {
         addIngredients(builder, widgets, List.of(EmiStack.EMPTY), RecipeIngredientRole.INPUT);
      }
   }

   private static void addIngredients(
      JemiRecipeLayoutBuilder builder, List<SlotWidget> widgets, List<? extends EmiIngredient> stacks, RecipeIngredientRole role
   ) {
      for (EmiIngredient ing : stacks) {
         int x = 0;
         int y = 0;

         for (SlotWidget w : widgets) {
            if (w.getStack() == ing) {
               x = w.getBounds().x();
               y = w.getBounds().y();
            }
         }

         IIngredientAcceptor acceptor = builder.addSlot(role, x, y);

         for (EmiStack stack : ing.getEmiStacks()) {
            Optional<ITypedIngredient<?>> opt = JemiUtil.getTyped(stack);
            if (opt.isPresent()) {
               ITypedIngredient<?> typed = opt.get();
               acceptor.addIngredient(typed.getType(), typed.getIngredient());
            }
         }
      }
   }
}
