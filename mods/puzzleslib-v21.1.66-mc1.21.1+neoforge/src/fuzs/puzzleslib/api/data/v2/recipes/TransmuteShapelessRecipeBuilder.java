package fuzs.puzzleslib.api.data.v2.recipes;

import fuzs.puzzleslib.impl.item.CustomTransmuteRecipe;
import fuzs.puzzleslib.impl.item.TransmuteShapelessRecipe;
import java.util.Objects;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class TransmuteShapelessRecipeBuilder extends ShapelessRecipeBuilder {
   private final RecipeSerializer<?> recipeSerializer;
   private Ingredient input;

   public TransmuteShapelessRecipeBuilder(RecipeSerializer<?> recipeSerializer, RecipeCategory recipeCategory, ItemStack result) {
      super(recipeCategory, result.getItem(), result.getCount());
      this.recipeSerializer = recipeSerializer;
   }

   public static TransmuteShapelessRecipeBuilder shapeless(RecipeSerializer<?> recipeSerializer, RecipeCategory category, ItemLike result) {
      return shapeless(recipeSerializer, category, result, 1);
   }

   public static TransmuteShapelessRecipeBuilder shapeless(RecipeSerializer<?> recipeSerializer, RecipeCategory category, ItemLike result, int count) {
      return new TransmuteShapelessRecipeBuilder(recipeSerializer, category, result.asItem().getDefaultInstance().copyWithCount(count));
   }

   public static RecipeSerializer<?> getRecipeSerializer(String modId) {
      return CustomTransmuteRecipe.getModSerializer(modId, "crafting_transmute_shapeless");
   }

   public TransmuteShapelessRecipeBuilder requires(TagKey<Item> tag) {
      super.requires(tag);
      return this;
   }

   public TransmuteShapelessRecipeBuilder requires(ItemLike item) {
      super.requires(item);
      return this;
   }

   public TransmuteShapelessRecipeBuilder requires(ItemLike item, int quantity) {
      super.requires(item, quantity);
      return this;
   }

   public TransmuteShapelessRecipeBuilder requires(Ingredient ingredient) {
      super.requires(ingredient);
      return this;
   }

   public TransmuteShapelessRecipeBuilder requires(Ingredient ingredient, int quantity) {
      super.requires(ingredient, quantity);
      return this;
   }

   public TransmuteShapelessRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
      super.unlockedBy(criterionName, criterionTrigger);
      return this;
   }

   public TransmuteShapelessRecipeBuilder group(@Nullable String groupName) {
      super.group(groupName);
      return this;
   }

   public TransmuteShapelessRecipeBuilder input(ItemLike input) {
      return this.input(Ingredient.of(new ItemLike[]{input}));
   }

   public TransmuteShapelessRecipeBuilder input(Ingredient input) {
      Objects.requireNonNull(input, "input is null");
      this.input = input;
      return this;
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
      Objects.requireNonNull(this.input, "input is null");
      super.save(
         TransformingRecipeOutput.transformed(recipeOutput, recipe -> new TransmuteShapelessRecipe(this.recipeSerializer, (ShapelessRecipe)recipe, this.input)),
         resourceLocation
      );
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapelessRecipeBuilder group(@Nullable String param1) {
      return this.group(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapelessRecipeBuilder unlockedBy(String param1, Criterion nullx) {
      return this.unlockedBy(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapelessRecipeBuilder requires(Ingredient param1, int nullx) {
      return this.requires(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapelessRecipeBuilder requires(Ingredient param1) {
      return this.requires(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapelessRecipeBuilder requires(ItemLike param1, int nullx) {
      return this.requires(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapelessRecipeBuilder requires(ItemLike param1) {
      return this.requires(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ShapelessRecipeBuilder requires(TagKey param1) {
      return this.requires(var1);
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
