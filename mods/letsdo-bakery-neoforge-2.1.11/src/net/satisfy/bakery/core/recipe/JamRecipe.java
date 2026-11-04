package net.satisfy.bakery.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
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
import net.satisfy.bakery.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class JamRecipe implements Recipe<JamRecipe.Input> {
   private final NonNullList<Ingredient> ingredients;
   private final ItemStack result;
   private final int color;
   private final int cookingTime;
   private final float experience;

   public JamRecipe(List<Ingredient> ingredients, ItemStack result, int color, int cookingTime, float experience) {
      this.experience = experience;
      this.ingredients = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));
      this.result = result;
      this.color = color;
      this.cookingTime = cookingTime;
   }

   public boolean matches(JamRecipe.Input input, Level level) {
      return input.size() == this.ingredients.size() && this.accepts(input.items());
   }

   public boolean accepts(List<ItemStack> items) {
      return items.size() <= this.ingredients.size() && this.assign(items, 0, new boolean[this.ingredients.size()]);
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

   public ItemStack getResult() {
      return this.result;
   }

   public int getColor() {
      return this.color;
   }

   public float getExperience() {
      return this.experience;
   }

   public int getCookingTime() {
      return this.cookingTime;
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return this.ingredients;
   }

   @NotNull
   public ItemStack assemble(JamRecipe.Input input, Provider provider) {
      return this.result.copy();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   @NotNull
   public ItemStack getResultItem(Provider provider) {
      return this.result;
   }

   public boolean isSpecial() {
      return true;
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeTypeRegistry.JAM_COOKING_SERIALIZER.get();
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)RecipeTypeRegistry.JAM_COOKING_TYPE.get();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   public ItemStack assemble(RecipeInput param1, Provider nullx) {
      return this.assemble((JamRecipe.Input)var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public boolean matches(RecipeInput param1, Level nullx) {
      return this.matches((JamRecipe.Input)var1, nullx);
   }

   public static final class HexColor {
      public static final Codec<Integer> CODEC = Codec.STRING.comapFlatMap(value -> {
         String hex = value.startsWith("#") ? value.substring(1) : value;

         try {
            return DataResult.success(Integer.parseInt(hex, 16) & 16777215);
         } catch (NumberFormatException exception) {
            return DataResult.error(() -> "Invalid colour: " + value);
         }
      }, color -> String.format("#%06X", color & 16777215));

      private HexColor() {
      }
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

   public static class Serializer implements RecipeSerializer<JamRecipe> {
      private static final Codec<List<Ingredient>> INGREDIENTS_CODEC = Ingredient.CODEC_NONEMPTY
         .listOf()
         .flatXmap(
            list -> !list.isEmpty() && list.size() <= 4 ? DataResult.success(list) : DataResult.error(() -> "Jam needs 1 to 4 ingredients"),
            DataResult::success
         );
      public static final MapCodec<JamRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
               ItemStack.STRICT_CODEC.fieldOf("result").forGetter(JamRecipe::getResult),
               JamRecipe.HexColor.CODEC.optionalFieldOf("color", 13120060).forGetter(JamRecipe::getColor),
               Codec.intRange(20, 72000).optionalFieldOf("cooking_time", 600).forGetter(JamRecipe::getCookingTime),
               Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(JamRecipe::getExperience)
            )
            .apply(instance, JamRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, JamRecipe> STREAM_CODEC = StreamCodec.composite(
         Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
         recipe -> recipe.ingredients,
         ItemStack.STREAM_CODEC,
         JamRecipe::getResult,
         ByteBufCodecs.INT,
         JamRecipe::getColor,
         ByteBufCodecs.VAR_INT,
         JamRecipe::getCookingTime,
         ByteBufCodecs.FLOAT,
         JamRecipe::getExperience,
         JamRecipe::new
      );

      @NotNull
      public MapCodec<JamRecipe> codec() {
         return CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, JamRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
