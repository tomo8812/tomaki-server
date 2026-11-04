package net.satisfy.herbalbrews.core.recipe;

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
import net.satisfy.herbalbrews.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class CauldronRecipe implements Recipe<RecipeInput> {
   private final NonNullList<Ingredient> inputs;
   private final ItemStack output;

   public CauldronRecipe(NonNullList<Ingredient> inputs, ItemStack output) {
      this.inputs = inputs;
      this.output = output;
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return this.inputs;
   }

   public boolean matches(RecipeInput recipeInput, Level level) {
      return false;
   }

   public ItemStack assemble(RecipeInput recipeInput, Provider provider) {
      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return false;
   }

   public ItemStack getResultItem(Provider provider) {
      return this.output.copy();
   }

   @NotNull
   public ResourceLocation getId() {
      return RecipeTypeRegistry.CAULDRON_RECIPE_TYPE.getId();
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeTypeRegistry.CAULDRON_RECIPE_SERIALIZER.get();
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)RecipeTypeRegistry.CAULDRON_RECIPE_TYPE.get();
   }

   public boolean isSpecial() {
      return true;
   }

   public static class Serializer implements RecipeSerializer<CauldronRecipe> {
      public static final StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> STREAM_CODEC = StreamCodec.of(
         CauldronRecipe.Serializer::toNetwork, CauldronRecipe.Serializer::fromNetwork
      );
      public static final MapCodec<CauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC_NONEMPTY
                  .listOf()
                  .fieldOf("ingredients")
                  .flatXmap(
                     list -> {
                        Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                        if (ingredients.length == 0) {
                           return DataResult.error(() -> "No ingredients for Cauldron recipe");
                        } else {
                           return ingredients.length > 3
                              ? DataResult.error(() -> "Too many ingredients for Cauldron recipe")
                              : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                        }
                     },
                     DataResult::success
                  )
                  .forGetter(CauldronRecipe::getIngredients),
               ItemStack.STRICT_CODEC.fieldOf("result").forGetter(cauldronRecipe -> cauldronRecipe.output)
            )
            .apply(instance, CauldronRecipe::new)
      );

      @NotNull
      public static CauldronRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
         int i = registryFriendlyByteBuf.readVarInt();
         NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
         nonNullList.replaceAll(ingredient -> (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(registryFriendlyByteBuf));
         ItemStack itemStack = (ItemStack)ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
         return new CauldronRecipe(nonNullList, itemStack);
      }

      public static void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, CauldronRecipe recipe) {
         registryFriendlyByteBuf.writeVarInt(recipe.getIngredients().size());

         for (Ingredient ingredient : recipe.getIngredients()) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(registryFriendlyByteBuf, ingredient);
         }

         ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.output);
      }

      public MapCodec<CauldronRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
