package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.serialization.Codec;
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
import org.jetbrains.annotations.NotNull;

public class MincerRecipe implements Recipe<RecipeInput> {
   private final String recipe_type;
   private final Ingredient input;
   private final ItemStack output;
   private final float experience;

   public MincerRecipe(String type, Ingredient input, ItemStack output, float experience) {
      this.recipe_type = type;
      this.input = input;
      this.output = output;
      this.experience = experience;
   }

   public float getExperience() {
      return this.experience;
   }

   public Ingredient getInput() {
      return this.input;
   }

   public ItemStack getOutput() {
      return this.output.copy();
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonNullList = NonNullList.create();
      nonNullList.add(this.input);
      return nonNullList;
   }

   public String getRecipeType() {
      return this.recipe_type;
   }

   public boolean matches(RecipeInput recipeInput, Level level) {
      return this.input.test(recipeInput.getItem(0));
   }

   @NotNull
   public ItemStack assemble(RecipeInput recipeInput, Provider provider) {
      return this.output.copy();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   @NotNull
   public ItemStack getResultItem(Provider provider) {
      return this.output.copy();
   }

   @NotNull
   public ResourceLocation getId() {
      return RecipeTypeRegistry.MINCER_RECIPE_TYPE.getId();
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeTypeRegistry.MINCER_RECIPE_SERIALIZER.get();
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)RecipeTypeRegistry.MINCER_RECIPE_TYPE.get();
   }

   public boolean isSpecial() {
      return true;
   }

   public static class Serializer implements RecipeSerializer<MincerRecipe> {
      public static final MapCodec<MincerRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.STRING.fieldOf("recipe_type").forGetter(MincerRecipe::getRecipeType),
               Ingredient.CODEC.fieldOf("ingredient").forGetter(MincerRecipe::getInput),
               ItemStack.CODEC.fieldOf("result").forGetter(MincerRecipe::getOutput),
               Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(MincerRecipe::getExperience)
            )
            .apply(instance, MincerRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, MincerRecipe> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.fromCodec(Codec.STRING),
         MincerRecipe::getRecipeType,
         Ingredient.CONTENTS_STREAM_CODEC,
         MincerRecipe::getInput,
         ItemStack.STREAM_CODEC,
         MincerRecipe::getOutput,
         ByteBufCodecs.FLOAT,
         MincerRecipe::getExperience,
         MincerRecipe::new
      );

      @NotNull
      public MapCodec<MincerRecipe> codec() {
         return CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, MincerRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
