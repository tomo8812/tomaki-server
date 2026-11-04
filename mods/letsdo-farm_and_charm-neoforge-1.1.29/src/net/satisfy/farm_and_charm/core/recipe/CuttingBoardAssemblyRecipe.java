package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class CuttingBoardAssemblyRecipe implements Recipe<CuttingBoardAssemblyRecipe.Input> {
   public static final int MAX_ORDERED_ITEMS = 3;
   private final NonNullList<Ingredient> ingredients;
   private final Optional<Ingredient> finisher;
   private final ItemStack result;
   private final boolean ordered;

   public CuttingBoardAssemblyRecipe(List<Ingredient> ingredients, Optional<Ingredient> finisher, ItemStack result, boolean ordered) {
      this.ingredients = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));
      this.finisher = finisher;
      this.result = result;
      this.ordered = ordered;
   }

   public boolean matches(CuttingBoardAssemblyRecipe.Input input, Level level) {
      return input.size() == this.totalSize() && this.accepts(input.items());
   }

   public boolean accepts(List<ItemStack> items) {
      int count = this.ingredients.size();
      if (items.size() > this.totalSize()) {
         return false;
      }

      if (this.ordered) {
         for (int i = 0; i < items.size(); i++) {
            Ingredient expected = i < count ? (Ingredient)this.ingredients.get(i) : this.finisher.orElseThrow();
            if (!expected.test(items.get(i))) {
               return false;
            }
         }

         return true;
      } else {
         return this.finisher.isPresent() && items.size() == count + 1
            ? this.finisher.get().test(items.getLast()) && this.assign(items.subList(0, count), 0, new boolean[count])
            : this.assign(items, 0, new boolean[count]);
      }
   }

   private int totalSize() {
      return this.ingredients.size() + (this.finisher.isPresent() ? 1 : 0);
   }

   private boolean assign(List<ItemStack> items, int index, boolean[] used) {
      if (index == items.size()) {
         return true;
      }

      for (int i = 0; i < this.ingredients.size(); i++) {
         if (!used[i] && ((Ingredient)this.ingredients.get(i)).test(items.get(index))) {
            used[i] = true;
            if (this.assign(items, index + 1, used)) {
               return true;
            }

            used[i] = false;
         }
      }

      return false;
   }

   @NotNull
   public ItemStack assemble(CuttingBoardAssemblyRecipe.Input input, Provider provider) {
      return this.result.copy();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   @NotNull
   public ItemStack getResultItem(Provider provider) {
      return this.result;
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      if (this.finisher.isEmpty()) {
         return this.ingredients;
      }

      NonNullList<Ingredient> all = NonNullList.create();
      all.addAll(this.ingredients);
      all.add(this.finisher.get());
      return all;
   }

   public boolean isSpecial() {
      return true;
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_SERIALIZER.get();
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get();
   }

   public List<Ingredient> getBaseIngredients() {
      return this.ingredients;
   }

   public Optional<Ingredient> getFinisher() {
      return this.finisher;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public boolean isOrdered() {
      return this.ordered;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   public ItemStack assemble(RecipeInput param1, Provider nullx) {
      return this.assemble((CuttingBoardAssemblyRecipe.Input)var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public boolean matches(RecipeInput param1, Level nullx) {
      return this.matches((CuttingBoardAssemblyRecipe.Input)var1, nullx);
   }

   public record Input(List<ItemStack> items) implements RecipeInput {
      @NotNull
      public ItemStack getItem(int index) {
         return this.items.get(index);
      }

      public int size() {
         return this.items.size();
      }
   }

   public static class Serializer implements RecipeSerializer<CuttingBoardAssemblyRecipe> {
      private static final Codec<List<Ingredient>> INGREDIENTS_CODEC = Ingredient.CODEC_NONEMPTY
         .listOf()
         .flatXmap(
            list -> list.size() >= 2 && list.size() <= 5 ? DataResult.success(list) : DataResult.error(() -> "Cutting board assembly needs 2 to 5 ingredients"),
            DataResult::success
         );
      public static final MapCodec<CuttingBoardAssemblyRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                  INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(CuttingBoardAssemblyRecipe::getBaseIngredients),
                  Ingredient.CODEC_NONEMPTY.optionalFieldOf("finisher").forGetter(CuttingBoardAssemblyRecipe::getFinisher),
                  ItemStack.STRICT_CODEC.fieldOf("result").forGetter(CuttingBoardAssemblyRecipe::getResult),
                  Codec.BOOL.optionalFieldOf("ordered", false).forGetter(CuttingBoardAssemblyRecipe::isOrdered)
               )
               .apply(instance, CuttingBoardAssemblyRecipe::new)
         )
         .validate(
            recipe -> {
               if (recipe.totalSize() > 5) {
                  return DataResult.error(() -> "Cutting board assembly holds at most 5 items including the finisher");
               } else {
                  return recipe.isOrdered() && recipe.totalSize() > 3
                     ? DataResult.error(() -> "Ordered cutting board assembly holds at most 3 items including the finisher")
                     : DataResult.success(recipe);
               }
            }
         );
      public static final StreamCodec<RegistryFriendlyByteBuf, CuttingBoardAssemblyRecipe> STREAM_CODEC = StreamCodec.composite(
         Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
         CuttingBoardAssemblyRecipe::getBaseIngredients,
         ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC),
         CuttingBoardAssemblyRecipe::getFinisher,
         ItemStack.STREAM_CODEC,
         CuttingBoardAssemblyRecipe::getResult,
         ByteBufCodecs.BOOL,
         CuttingBoardAssemblyRecipe::isOrdered,
         CuttingBoardAssemblyRecipe::new
      );

      @NotNull
      public MapCodec<CuttingBoardAssemblyRecipe> codec() {
         return CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, CuttingBoardAssemblyRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
