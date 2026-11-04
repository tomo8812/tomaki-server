package dev.emi.emi.api.recipe;

import com.google.common.collect.Lists;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public abstract class BasicEmiRecipe implements EmiRecipe {
   protected List<EmiIngredient> inputs = Lists.newArrayList();
   protected List<EmiIngredient> catalysts = Lists.newArrayList();
   protected List<EmiStack> outputs = Lists.newArrayList();
   protected EmiRecipeCategory category;
   protected ResourceLocation id;
   protected int width;
   protected int height;

   public BasicEmiRecipe(EmiRecipeCategory category, ResourceLocation id, int width, int height) {
      this.category = category;
      this.id = id;
      this.width = width;
      this.height = height;
   }

   @Override
   public EmiRecipeCategory getCategory() {
      return this.category;
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
   public int getDisplayWidth() {
      return this.width;
   }

   @Override
   public int getDisplayHeight() {
      return this.height;
   }
}
