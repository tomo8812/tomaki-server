package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
import org.jetbrains.annotations.NotNull;

public class StoveRecipe implements Recipe<RecipeInput> {
   protected final NonNullList<Ingredient> inputs;
   protected final ItemStack output;
   protected final float experience;
   private final boolean requiresLearning;

   public StoveRecipe(NonNullList<Ingredient> inputs, ItemStack output, float experience, boolean requiresLearning) {
      this.inputs = inputs;
      this.output = output;
      this.experience = experience;
      this.requiresLearning = requiresLearning;
   }

   public boolean matches(RecipeInput recipeInput, Level level) {
      return GeneralUtil.matchesRecipe(recipeInput, this.inputs, 1, 3);
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
      return this.output.copy();
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return this.inputs;
   }

   @NotNull
   public ResourceLocation getId() {
      return RecipeTypeRegistry.STOVE_RECIPE_TYPE.getId();
   }

   public float getExperience() {
      return this.experience;
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeTypeRegistry.STOVE_RECIPE_SERIALIZER.get();
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)RecipeTypeRegistry.STOVE_RECIPE_TYPE.get();
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean requiresLearning() {
      return this.requiresLearning;
   }

   public static class Serializer implements RecipeSerializer<StoveRecipe> {
      public static final MapCodec<StoveRecipe> CODEC = RecordCodecBuilder.mapCodec(
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
                  .forGetter(StoveRecipe::getIngredients),
               ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
               Codec.FLOAT.fieldOf("experience").forGetter(StoveRecipe::getExperience),
               Codec.BOOL.fieldOf("requiresLearning").forGetter(StoveRecipe::requiresLearning)
            )
            .apply(instance, StoveRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, StoveRecipe> STREAM_CODEC = StreamCodec.of(
         StoveRecipe.Serializer::toNetwork, StoveRecipe.Serializer::fromNetwork
      );

      @NotNull
      public static StoveRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
         int i = registryFriendlyByteBuf.readVarInt();
         NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
         nonNullList.replaceAll(ingredient -> (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(registryFriendlyByteBuf));
         ItemStack itemStack = (ItemStack)ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
         float experience = registryFriendlyByteBuf.readFloat();
         boolean requiresLearning = registryFriendlyByteBuf.readBoolean();
         return new StoveRecipe(nonNullList, itemStack, experience, requiresLearning);
      }

      public static void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, StoveRecipe recipe) {
         registryFriendlyByteBuf.writeVarInt(recipe.getIngredients().size());

         for (Ingredient ingredient : recipe.getIngredients()) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(registryFriendlyByteBuf, ingredient);
         }

         ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.output);
         registryFriendlyByteBuf.writeFloat(recipe.experience);
         registryFriendlyByteBuf.writeBoolean(recipe.requiresLearning);
      }

      @NotNull
      public MapCodec<StoveRecipe> codec() {
         return CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, StoveRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
