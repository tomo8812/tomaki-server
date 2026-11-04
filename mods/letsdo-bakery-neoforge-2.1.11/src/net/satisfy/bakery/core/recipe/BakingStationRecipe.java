package net.satisfy.bakery.core.recipe;

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
import net.satisfy.bakery.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

public class BakingStationRecipe implements Recipe<RecipeInput> {
   private final NonNullList<Ingredient> inputs;
   private final ItemStack output;

   public BakingStationRecipe(NonNullList<Ingredient> inputs, ItemStack output) {
      this.inputs = inputs;
      this.output = output;
   }

   public boolean matches(RecipeInput recipeInput, Level level) {
      return GeneralUtil.matchesRecipe(recipeInput, this.inputs, 1, 3);
   }

   public ItemStack assemble(RecipeInput recipeInput, Provider provider) {
      return this.getResultItem(provider);
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   public ItemStack getResultItem(Provider provider) {
      return this.output.copy();
   }

   @NotNull
   public ResourceLocation getId() {
      return RecipeTypeRegistry.BAKING_STATION_RECIPE_TYPE.getId();
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeTypeRegistry.BAKING_STATION_RECIPE_SERIALIZER.get();
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)RecipeTypeRegistry.BAKING_STATION_RECIPE_TYPE.get();
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return this.inputs;
   }

   public boolean isSpecial() {
      return true;
   }

   public static class Serializer implements RecipeSerializer<BakingStationRecipe> {
      public static final StreamCodec<RegistryFriendlyByteBuf, BakingStationRecipe> STREAM_CODEC = StreamCodec.of(
         BakingStationRecipe.Serializer::toNetwork, BakingStationRecipe.Serializer::fromNetwork
      );
      private static final MapCodec<BakingStationRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC_NONEMPTY
                  .listOf()
                  .fieldOf("ingredients")
                  .flatXmap(
                     list -> {
                        Ingredient[] ingredients = list.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                        if (ingredients.length == 0) {
                           return DataResult.error(() -> "No ingredients for Baking Station recipe");
                        } else {
                           return ingredients.length > 3
                              ? DataResult.error(() -> "Too many ingredients for Baking Station recipe")
                              : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                        }
                     },
                     DataResult::success
                  )
                  .forGetter(bakingStationRecipe -> bakingStationRecipe.inputs),
               ItemStack.STRICT_CODEC.fieldOf("result").forGetter(bakingStationRecipe -> bakingStationRecipe.output)
            )
            .apply(instance, BakingStationRecipe::new)
      );

      @NotNull
      public static BakingStationRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         int i = buf.readVarInt();
         NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
         nonNullList.replaceAll(ingredient -> (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
         ItemStack itemStack = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
         return new BakingStationRecipe(nonNullList, itemStack);
      }

      public static void toNetwork(RegistryFriendlyByteBuf buf, BakingStationRecipe recipe) {
         buf.writeVarInt(recipe.inputs.size());

         for (Ingredient ingredient : recipe.inputs) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
         }

         ItemStack.STREAM_CODEC.encode(buf, recipe.output);
      }

      public MapCodec<BakingStationRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, BakingStationRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
