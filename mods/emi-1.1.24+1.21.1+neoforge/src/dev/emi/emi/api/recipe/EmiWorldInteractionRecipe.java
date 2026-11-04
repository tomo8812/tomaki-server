package dev.emi.emi.api.recipe;

import com.google.common.collect.Lists;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class EmiWorldInteractionRecipe implements EmiRecipe {
   private final ResourceLocation id;
   private final List<EmiWorldInteractionRecipe.WorldIngredient> left;
   private final List<EmiWorldInteractionRecipe.WorldIngredient> right;
   private final List<EmiWorldInteractionRecipe.WorldIngredient> outputIngredients;
   private final List<EmiIngredient> inputs;
   private final List<EmiIngredient> catalysts;
   private final List<EmiStack> outputs;
   private final boolean supportsRecipeTree;
   private final int slotHeight;
   private int width = 125;
   private int height;
   private int totalSize;
   private int leftSize;
   private int rightSize;
   private int outputSize;
   private int leftHeight;
   private int rightHeight;
   private int outputHeight;

   protected EmiWorldInteractionRecipe(EmiWorldInteractionRecipe.Builder builder) {
      this.id = builder.id;
      this.left = builder.left;
      this.right = builder.right;
      this.inputs = Stream.concat(this.left.stream(), this.right.stream()).filter(i -> !i.catalyst).map(i -> i.stack).toList();
      this.catalysts = Stream.concat(this.left.stream(), this.right.stream()).filter(i -> i.catalyst).map(i -> i.stack).toList();
      this.outputIngredients = builder.output;
      this.outputs = builder.output.stream().map(i -> (EmiStack)i.stack).toList();
      this.supportsRecipeTree = builder.supportsRecipeTree;

      for (EmiIngredient catalyst : this.catalysts) {
         for (EmiStack stack : catalyst.getEmiStacks()) {
            if (stack.getRemainder().isEmpty()) {
               stack.setRemainder(stack.copy());
            }
         }
      }

      this.totalSize = this.left.size() + this.right.size() + this.outputs.size();
      if (this.totalSize > 5) {
         int[] portions = new int[]{this.left.size(), this.right.size(), this.outputs.size()};
         int[] sizes = new int[]{1, 1, 1};

         for (int i = 0; i < 2; i++) {
            int largest = portions[0];
            int li = 0;

            for (int j = 1; j < 3; j++) {
               if (portions[j] >= largest) {
                  largest = portions[j];
                  li = j;
               }
            }

            sizes[li]++;
            portions[li] = portions[li] * 2 / 3;
         }

         this.leftSize = sizes[0];
         this.rightSize = sizes[1];
         this.outputSize = sizes[2];
      } else {
         this.leftSize = this.left.size();
         this.rightSize = this.right.size();
         this.outputSize = this.outputs.size();
      }

      this.leftHeight = (this.left.size() - 1) / this.leftSize + 1;
      this.rightHeight = (this.right.size() - 1) / this.rightSize + 1;
      this.outputHeight = (this.outputs.size() - 1) / this.outputSize + 1;
      this.slotHeight = Math.max(this.leftHeight, Math.max(this.rightHeight, this.outputHeight));
      this.height = this.slotHeight * 18;
      if (this.totalSize > 4) {
         this.width = 134;
      }
   }

   public static EmiWorldInteractionRecipe.Builder builder() {
      return new EmiWorldInteractionRecipe.Builder();
   }

   @Override
   public EmiRecipeCategory getCategory() {
      return VanillaEmiRecipeCategories.WORLD_INTERACTION;
   }

   @Nullable
   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public List<EmiIngredient> getInputs() {
      return this.inputs;
   }

   @Override
   public List<EmiIngredient> getCatalysts() {
      return this.catalysts;
   }

   @Override
   public List<EmiStack> getOutputs() {
      return this.outputs;
   }

   @Override
   public boolean supportsRecipeTree() {
      return this.supportsRecipeTree;
   }

   @Override
   public int getDisplayWidth() {
      return this.width;
   }

   @Override
   public int getDisplayHeight() {
      return this.height;
   }

   @Override
   public void addWidgets(WidgetHolder widgets) {
      int lr = this.leftSize * 18;
      int ol = this.width - this.outputSize * 18;
      int rl = (lr + ol) / 2 - this.rightSize * 9 - 4;
      int rr = rl + this.rightSize * 18;
      widgets.addTexture(EmiTexture.PLUS, (lr + rl) / 2 - EmiTexture.PLUS.width / 2, -6 + this.slotHeight * 9);
      widgets.addTexture(EmiTexture.EMPTY_ARROW, (rr + ol) / 2 - EmiTexture.EMPTY_ARROW.width / 2, -8 + this.slotHeight * 9);
      int yo = (this.slotHeight - this.leftHeight) * 9;

      for (int i = 0; i < this.left.size(); i++) {
         EmiWorldInteractionRecipe.WorldIngredient wi = this.left.get(i);
         widgets.add(wi.mutator.apply(new SlotWidget(wi.stack, i % this.leftSize * 18, yo + i / this.leftSize * 18)));
      }

      yo = (this.slotHeight - this.rightHeight) * 9;

      for (int i = 0; i < this.right.size(); i++) {
         EmiWorldInteractionRecipe.WorldIngredient wi = this.right.get(i);
         widgets.add(wi.mutator.apply(new SlotWidget(wi.stack, rl + i % this.rightSize * 18, yo + i / this.rightSize * 18).catalyst(wi.catalyst)));
      }

      yo = (this.slotHeight - this.outputHeight) * 9;

      for (int i = 0; i < this.outputIngredients.size(); i++) {
         EmiWorldInteractionRecipe.WorldIngredient wi = this.outputIngredients.get(i);
         widgets.add(wi.mutator.apply(new SlotWidget(wi.stack, ol + i % this.outputSize * 18, yo + i / this.outputSize * 18)).recipeContext(this));
      }
   }

   public static class Builder {
      private final List<EmiWorldInteractionRecipe.WorldIngredient> left = Lists.newArrayList();
      private final List<EmiWorldInteractionRecipe.WorldIngredient> right = Lists.newArrayList();
      private final List<EmiWorldInteractionRecipe.WorldIngredient> output = Lists.newArrayList();
      private boolean supportsRecipeTree = true;
      private ResourceLocation id = null;

      private Builder() {
      }

      public EmiWorldInteractionRecipe build() {
         if (this.left.isEmpty()) {
            throw new IllegalStateException("Cannot create a world interaction recipe without a left input");
         } else if (this.right.isEmpty()) {
            throw new IllegalStateException("Cannot create a world interaction recipe without a right input");
         } else if (this.output.isEmpty()) {
            throw new IllegalStateException("Cannot create a world interaction recipe without an output");
         } else {
            return new EmiWorldInteractionRecipe(this);
         }
      }

      public EmiWorldInteractionRecipe.Builder id(ResourceLocation id) {
         this.id = id;
         return this;
      }

      public EmiWorldInteractionRecipe.Builder leftInput(EmiIngredient stack) {
         this.left.add(new EmiWorldInteractionRecipe.WorldIngredient(stack, false, s -> s));
         return this;
      }

      public EmiWorldInteractionRecipe.Builder leftInput(EmiIngredient stack, Function<SlotWidget, SlotWidget> mutator) {
         this.left.add(new EmiWorldInteractionRecipe.WorldIngredient(stack, false, mutator));
         return this;
      }

      public EmiWorldInteractionRecipe.Builder rightInput(EmiIngredient stack, boolean catalyst) {
         this.right.add(new EmiWorldInteractionRecipe.WorldIngredient(stack, catalyst, s -> s));
         return this;
      }

      public EmiWorldInteractionRecipe.Builder rightInput(EmiIngredient stack, boolean catalyst, Function<SlotWidget, SlotWidget> mutator) {
         this.right.add(new EmiWorldInteractionRecipe.WorldIngredient(stack, catalyst, mutator));
         return this;
      }

      public EmiWorldInteractionRecipe.Builder output(EmiStack stack) {
         this.output.add(new EmiWorldInteractionRecipe.WorldIngredient(stack, false, s -> s));
         return this;
      }

      public EmiWorldInteractionRecipe.Builder output(EmiStack stack, Function<SlotWidget, SlotWidget> mutator) {
         this.output.add(new EmiWorldInteractionRecipe.WorldIngredient(stack, false, mutator));
         return this;
      }

      public EmiWorldInteractionRecipe.Builder supportsRecipeTree(boolean supportsRecipeTree) {
         this.supportsRecipeTree = supportsRecipeTree;
         return this;
      }
   }

   private record WorldIngredient(EmiIngredient stack, boolean catalyst, Function<SlotWidget, SlotWidget> mutator) {
   }
}
