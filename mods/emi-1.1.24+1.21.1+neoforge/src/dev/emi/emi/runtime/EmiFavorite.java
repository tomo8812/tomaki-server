package dev.emi.emi.runtime;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.config.HelpLevel;
import dev.emi.emi.registry.EmiRecipeFiller;
import dev.emi.emi.screen.MicroTextRenderer;
import dev.emi.emi.screen.StackBatcher;
import dev.emi.emi.screen.tooltip.RecipeTooltipComponent;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class EmiFavorite implements EmiIngredient, StackBatcher.Batchable {
   protected final EmiIngredient stack;
   @Nullable
   protected final EmiRecipe recipe;

   public EmiFavorite(EmiIngredient stack, @Nullable EmiRecipe recipe) {
      this.stack = stack;
      this.recipe = recipe;
   }

   public EmiIngredient getStack() {
      return this.stack;
   }

   @Override
   public EmiIngredient copy() {
      return new EmiFavorite(this.stack, this.recipe);
   }

   @Override
   public long getAmount() {
      return this.stack.getAmount();
   }

   @Override
   public EmiIngredient setAmount(long amount) {
      return this;
   }

   @Override
   public float getChance() {
      return 1.0F;
   }

   @Override
   public EmiIngredient setChance(float chance) {
      return this;
   }

   public EmiRecipe getRecipe() {
      return this.recipe;
   }

   @Override
   public List<EmiStack> getEmiStacks() {
      return this.stack.getEmiStacks();
   }

   @Override
   public void render(GuiGraphics raw, int x, int y, float delta, int flags) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      if (this.recipe != null) {
         flags |= 2;
      }

      this.stack.render(context.raw(), x, y, delta, flags);
      if ((flags & 4) > 0 && this.recipe != null) {
         EmiRenderHelper.renderRecipeFavorite(this.stack, context, x, y);
      }
   }

   @Override
   public List<ClientTooltipComponent> getTooltip() {
      List<ClientTooltipComponent> list = Lists.newArrayList();
      list.addAll(this.stack.getTooltip());
      if (this.recipe != null) {
         list.add(new RecipeTooltipComponent(this.recipe, true));
      }

      return list;
   }

   public boolean strictEquals(EmiIngredient other) {
      List<EmiStack> as = this.getEmiStacks();
      List<EmiStack> bs = other.getEmiStacks();
      if (as.size() != bs.size()) {
         return false;
      }

      for (int i = 0; i < as.size(); i++) {
         if (!as.get(i).isEqual(bs.get(i), EmiPort.compareStrict())) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof EmiIngredient ingredient && EmiIngredient.areEqual(this, ingredient);
   }

   @Override
   public boolean isSideLit() {
      return this.stack instanceof StackBatcher.Batchable b && b.isSideLit();
   }

   @Override
   public boolean isUnbatchable() {
      return !(this.stack instanceof StackBatcher.Batchable b && !b.isUnbatchable());
   }

   @Override
   public void setUnbatchable() {
      if (this.stack instanceof StackBatcher.Batchable b) {
         b.setUnbatchable();
      }
   }

   @Override
   public void renderForBatch(MultiBufferSource vcp, GuiGraphics raw, int x, int y, int z, float delta) {
      if (this.stack instanceof StackBatcher.Batchable b) {
         b.renderForBatch(vcp, raw, x, y, z, delta);
      }
   }

   public static class Craftable extends EmiFavorite {
      public Craftable(EmiRecipe recipe) {
         super(recipe.getOutputs().isEmpty() ? EmiStack.EMPTY : recipe.getOutputs().get(0), recipe);
      }

      @Override
      public void render(GuiGraphics raw, int x, int y, float delta, int flags) {
         super.render(raw, x, y, delta, flags & -5);
      }
   }

   public static class Synthetic extends EmiFavorite {
      public final long batches;
      public final long amount;
      public final int state;
      public final long total;

      public Synthetic(EmiRecipe recipe, long batches, long amount, long total, int state) {
         super(recipe.getOutputs().get(0), recipe);
         this.batches = batches;
         this.amount = amount;
         this.total = total;
         this.state = state;
      }

      public Synthetic(EmiIngredient ingredient, long needed, long total) {
         super(ingredient, null);
         this.batches = needed;
         this.amount = needed;
         this.total = total;
         this.state = -1;
      }

      @Override
      public void render(GuiGraphics raw, int x, int y, float delta, int flags) {
         EmiDrawContext context = EmiDrawContext.wrap(raw);
         int color = 9525504;
         if (this.state == 1) {
            color = 7930001;
         } else if (this.state == 2) {
            color = 37262;
         } else if (this.state == -1) {
            color = 9507584;
         }

         this.stack.render(context.raw(), x, y, delta, flags & -3);
         MicroTextRenderer.render(context, this.amount, this.stack.getEmiStacks().get(0) instanceof FluidEmiStack, 18, x + 17, y + 17, color);
      }

      @Override
      public List<ClientTooltipComponent> getTooltip() {
         List<ClientTooltipComponent> list = Lists.newArrayList();
         list.addAll(super.getTooltip());
         long diff = this.total - this.amount;
         list.add(
            EmiTooltipComponents.of(
               EmiPort.translatable("tooltip.emi.synfav.remaining", EmiRenderHelper.getAmountText(this.stack, this.amount)).withStyle(ChatFormatting.GRAY)
            )
         );
         list.add(
            EmiTooltipComponents.of(
               EmiPort.translatable(
                     "tooltip.emi.synfav.obtained", EmiRenderHelper.getAmountText(this.stack, diff), EmiRenderHelper.getAmountText(this.stack, this.total)
                  )
                  .withStyle(ChatFormatting.GRAY)
            )
         );
         if (this.batches != this.amount) {
            list.add(EmiTooltipComponents.of(EmiPort.translatable("tooltip.emi.synfav.batches_remaining", this.batches).withStyle(ChatFormatting.GRAY)));
         }

         if (this.state == -1) {
            return list;
         }

         Component craftKey = null;
         if (EmiConfig.helpLevel.has(HelpLevel.NORMAL) && EmiRecipeFiller.getFirstValidHandler(this.recipe, EmiApi.getHandledScreen()) != null) {
            if (EmiConfig.craftAllToInventory.isBound()) {
               craftKey = EmiConfig.craftAllToInventory.getBindText();
            } else if (EmiConfig.craftAll.isBound()) {
               craftKey = EmiConfig.craftAll.getBindText();
            }
         }

         if (this.state == 0) {
            list.add(ClientTooltipComponent.create(EmiPort.translatable("tooltip.emi.synfav.uncraftable").getVisualOrderText()));
         } else if (this.state == 1) {
            list.add(ClientTooltipComponent.create(EmiPort.translatable("tooltip.emi.synfav.partially_craftable").getVisualOrderText()));
            if (craftKey != null) {
               list.add(ClientTooltipComponent.create(EmiPort.translatable("tooltip.emi.synfav.craft_some", craftKey).getVisualOrderText()));
            }
         } else if (this.state == 2) {
            list.add(ClientTooltipComponent.create(EmiPort.translatable("tooltip.emi.synfav.fully_craftable", this.batches).getVisualOrderText()));
            if (craftKey != null) {
               list.add(ClientTooltipComponent.create(EmiPort.translatable("tooltip.emi.synfav.craft_all", craftKey, this.batches).getVisualOrderText()));
            }
         }

         return list;
      }

      @Override
      public boolean isUnbatchable() {
         return true;
      }
   }
}
