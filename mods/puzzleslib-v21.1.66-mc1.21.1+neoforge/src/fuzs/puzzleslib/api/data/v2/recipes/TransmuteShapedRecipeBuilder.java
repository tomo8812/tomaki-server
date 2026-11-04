package fuzs.puzzleslib.api.data.v2.recipes;

import fuzs.puzzleslib.impl.item.CustomTransmuteRecipe;
import fuzs.puzzleslib.impl.item.TransmuteShapedRecipe;
import java.util.Objects;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class TransmuteShapedRecipeBuilder extends ShapedRecipeBuilder {
   private final RecipeSerializer<?> recipeSerializer;
   private Ingredient input;

   public TransmuteShapedRecipeBuilder(RecipeSerializer<?> recipeSerializer, RecipeCategory recipeCategory, ItemStack result) {
      super(recipeCategory, result.getItem(), result.getCount());
      this.recipeSerializer = recipeSerializer;
   }

   public static TransmuteShapedRecipeBuilder shaped(RecipeSerializer<?> recipeSerializer, RecipeCategory category, ItemLike result) {
      return shaped(recipeSerializer, category, result, 1);
   }

   public static TransmuteShapedRecipeBuilder shaped(RecipeSerializer<?> recipeSerializer, RecipeCategory category, ItemLike result, int count) {
      return new TransmuteShapedRecipeBuilder(recipeSerializer, category, result.asItem().getDefaultInstance().copyWithCount(count));
   }

   public static RecipeSerializer<?> getRecipeSerializer(String modId) {
      return CustomTransmuteRecipe.getModSerializer(modId, "crafting_transmute_shaped");
   }

   public TransmuteShapedRecipeBuilder define(Character symbol, TagKey<Item> tag) {
      super.define(symbol, tag);
      return this;
   }

   public TransmuteShapedRecipeBuilder define(Character symbol, ItemLike item) {
      super.define(symbol, item);
      return this;
   }

   public TransmuteShapedRecipeBuilder define(Character symbol, Ingredient ingredient) {
      super.define(symbol, ingredient);
      return this;
   }

   public TransmuteShapedRecipeBuilder pattern(String pattern) {
      super.pattern(pattern);
      return this;
   }

   public TransmuteShapedRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
      super.unlockedBy(criterionName, criterionTrigger);
      return this;
   }

   public TransmuteShapedRecipeBuilder group(@Nullable String groupName) {
      super.group(groupName);
      return this;
   }

   public TransmuteShapedRecipeBuilder showNotification(boolean bl) {
      super.showNotification(bl);
      return this;
   }

   public TransmuteShapedRecipeBuilder input(ItemLike input) {
      return this.input(Ingredient.of(new ItemLike[]{input}));
   }

   public TransmuteShapedRecipeBuilder input(Ingredient input) {
      Objects.requireNonNull(input, "input is null");
      this.input = input;
      return this;
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
      Objects.requireNonNull(this.input, "input is null");
      super.save(
         TransformingRecipeOutput.transformed(recipeOutput, recipe -> new TransmuteShapedRecipe(this.recipeSerializer, (ShapedRecipe)recipe, this.input)),
         resourceLocation
      );
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapedRecipeBuilder showNotification(boolean param1) {
      return this.showNotification(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapedRecipeBuilder group(@Nullable String param1) {
      return this.group(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapedRecipeBuilder unlockedBy(String param1, Criterion nullx) {
      return this.unlockedBy(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapedRecipeBuilder pattern(String param1) {
      return this.pattern(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapedRecipeBuilder define(Character param1, Ingredient nullx) {
      return this.define(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapedRecipeBuilder define(Character param1, ItemLike nullx) {
      return this.define(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapedRecipeBuilder define(Character param1, TagKey nullx) {
      return this.define(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public RecipeBuilder group(@Nullable String param1) {
      return this.group(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public RecipeBuilder unlockedBy(String param1, Criterion nullx) {
      return this.unlockedBy(var1, nullx);
   }
}
