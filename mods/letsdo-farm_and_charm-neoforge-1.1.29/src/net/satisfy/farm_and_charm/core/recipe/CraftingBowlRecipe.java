package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import net.satisfy.foundation.util.StreamCodecUtil;
import org.jetbrains.annotations.NotNull;

public class CraftingBowlRecipe implements Recipe<RecipeInput> {
   private final NonNullList<Ingredient> inputs;
   private final ItemStack output;
   private final int outputCount;
   private final float experience;

   public CraftingBowlRecipe(NonNullList<Ingredient> inputs, ItemStack output, float experience) {
      this.inputs = inputs;
      this.output = output;
      this.outputCount = output.getCount();
      this.experience = experience;
   }

   public float getExperience() {
      return this.experience;
   }

   public int getOutputCount() {
      return this.outputCount;
   }

   public boolean matches(RecipeInput inventory, Level world) {
      int nonEmptySlots = 0;

      for (int i = 0; i < inventory.size(); i++) {
         if (!inventory.getItem(i).isEmpty()) {
            nonEmptySlots++;
         }
      }

      return nonEmptySlots >= 1 && nonEmptySlots <= this.inputs.size() && GeneralUtil.matchesRecipe(inventory, this.inputs, 0, 3);
   }

   @NotNull
   public ItemStack assemble(RecipeInput recipeInput, Provider provider) {
      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   @NotNull
   public ItemStack getResultItem(Provider provider) {
      ItemStack result = this.output.copy();
      result.setCount(this.outputCount);
      return result;
   }

   public ItemStack getResultItem() {
      return this.getResultItem(null);
   }

   @NotNull
   public ResourceLocation getId() {
      return RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.getId();
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_SERIALIZER.get();
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get();
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return this.inputs;
   }

   public boolean isSpecial() {
      return true;
   }

   public static class Serializer implements RecipeSerializer<CraftingBowlRecipe> {
      public static final MapCodec<CraftingBowlRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC_NONEMPTY
                  .listOf()
                  .fieldOf("ingredients")
                  .flatXmap(
                     list -> {
                        Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                        return ingredients.length == 0
                           ? DataResult.error(() -> "No ingredients for shapeless recipe")
                           : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                     },
                     DataResult::success
                  )
                  .forGetter(CraftingBowlRecipe::getIngredients),
               ItemStack.CODEC.fieldOf("result").forGetter(CraftingBowlRecipe::getResultItem),
               Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(CraftingBowlRecipe::getExperience)
            )
            .apply(instance, CraftingBowlRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, CraftingBowlRecipe> STREAM_CODEC = StreamCodec.composite(
         StreamCodecUtil.nonNullList(Ingredient.CONTENTS_STREAM_CODEC, Ingredient.EMPTY),
         CraftingBowlRecipe::getIngredients,
         ItemStack.STREAM_CODEC,
         CraftingBowlRecipe::getResultItem,
         ByteBufCodecs.FLOAT,
         CraftingBowlRecipe::getExperience,
         CraftingBowlRecipe::new
      );

      @NotNull
      public MapCodec<CraftingBowlRecipe> codec() {
         return CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, CraftingBowlRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
