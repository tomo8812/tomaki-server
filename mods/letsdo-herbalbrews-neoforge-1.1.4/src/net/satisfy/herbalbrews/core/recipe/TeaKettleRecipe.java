package net.satisfy.herbalbrews.core.recipe;

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
import net.satisfy.herbalbrews.core.registry.RecipeTypeRegistry;
import net.satisfy.herbalbrews.core.util.HerbalBrewsUtil;
import org.jetbrains.annotations.NotNull;

public class TeaKettleRecipe implements Recipe<RecipeInput> {
   private final NonNullList<Ingredient> inputs;
   private final ItemStack output;
   private final ResourceLocation effect;
   private final int effectDuration;
   private final int requiredWater;
   private final int requiredHeat;
   private final int requiredDuration;
   private final float experience;

   public TeaKettleRecipe(
      NonNullList<Ingredient> inputs,
      ItemStack output,
      ResourceLocation effect,
      int effectDuration,
      int requiredWater,
      int requiredHeat,
      int requiredDuration,
      float experience
   ) {
      this.inputs = inputs;
      this.output = output;
      this.effect = effect;
      this.effectDuration = effectDuration;
      this.requiredWater = requiredWater;
      this.requiredHeat = requiredHeat;
      this.requiredDuration = requiredDuration;
      this.experience = experience;
   }

   public boolean matches(RecipeInput recipeInput, Level level) {
      return HerbalBrewsUtil.matchesRecipe(recipeInput, this.inputs, 0, 5);
   }

   public ItemStack assemble(RecipeInput recipeInput, Provider provider) {
      return this.output.copy();
   }

   public ItemStack assemble() {
      return this.assemble(null, null);
   }

   public boolean canCraftInDimensions(int width, int height) {
      return false;
   }

   public ItemStack getResultItem(Provider provider) {
      return this.output;
   }

   public ItemStack getResultItem() {
      return this.getResultItem(null);
   }

   public ResourceLocation getEffect() {
      return this.effect;
   }

   public int getEffectDuration() {
      return this.effectDuration;
   }

   public int getRequiredWater() {
      return this.requiredWater;
   }

   public int getRequiredHeat() {
      return this.requiredHeat;
   }

   public int getRequiredDuration() {
      return this.requiredDuration;
   }

   public float getExperience() {
      return this.experience;
   }

   @NotNull
   public ResourceLocation getId() {
      return RecipeTypeRegistry.TEA_KETTLE_RECIPE_TYPE.getId();
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeTypeRegistry.TEA_KETTLE_RECIPE_SERIALIZER.get();
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)RecipeTypeRegistry.TEA_KETTLE_RECIPE_TYPE.get();
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return this.inputs;
   }

   public boolean isSpecial() {
      return true;
   }

   public static class Serializer implements RecipeSerializer<TeaKettleRecipe> {
      public static final StreamCodec<RegistryFriendlyByteBuf, TeaKettleRecipe> STREAM_CODEC = StreamCodec.of(
         TeaKettleRecipe.Serializer::toNetwork, TeaKettleRecipe.Serializer::fromNetwork
      );
      public static final MapCodec<TeaKettleRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC_NONEMPTY
                  .listOf()
                  .fieldOf("ingredients")
                  .flatXmap(
                     list -> {
                        Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                        if (ingredients.length == 0) {
                           return DataResult.error(() -> "No ingredients for Tea Kettle recipe");
                        } else {
                           return ingredients.length > 6
                              ? DataResult.error(() -> "Too many ingredients for Tea Kettle recipe")
                              : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                        }
                     },
                     DataResult::success
                  )
                  .forGetter(TeaKettleRecipe::getIngredients),
               ItemStack.STRICT_CODEC.fieldOf("result").forGetter(teaKettleRecipe -> teaKettleRecipe.output),
               ResourceLocation.CODEC.fieldOf("effect").forGetter(TeaKettleRecipe::getEffect),
               Codec.INT.fieldOf("effect_duration").forGetter(TeaKettleRecipe::getEffectDuration),
               Codec.INT.fieldOf("fluid_amount").forGetter(TeaKettleRecipe::getRequiredWater),
               Codec.INT.fieldOf("heat_amount").forGetter(TeaKettleRecipe::getRequiredHeat),
               Codec.INT.fieldOf("crafting_duration").forGetter(TeaKettleRecipe::getRequiredDuration),
               Codec.FLOAT.fieldOf("experience").forGetter(TeaKettleRecipe::getExperience)
            )
            .apply(instance, TeaKettleRecipe::new)
      );

      @NotNull
      public static TeaKettleRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
         int i = registryFriendlyByteBuf.readVarInt();
         NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
         nonNullList.replaceAll(ingredient -> (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(registryFriendlyByteBuf));
         ItemStack itemStack = (ItemStack)ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
         ResourceLocation effect = null;
         int effectDuration = 0;
         boolean hasEffect = registryFriendlyByteBuf.readBoolean();
         if (hasEffect) {
            effect = registryFriendlyByteBuf.readResourceLocation();
            effectDuration = registryFriendlyByteBuf.readInt();
         }

         int requiredWater = registryFriendlyByteBuf.readInt();
         int requiredHeat = registryFriendlyByteBuf.readInt();
         int requiredDuration = registryFriendlyByteBuf.readInt();
         float experience = registryFriendlyByteBuf.readFloat();
         return new TeaKettleRecipe(nonNullList, itemStack, effect, effectDuration, requiredWater, requiredHeat, requiredDuration, experience);
      }

      public static void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, TeaKettleRecipe recipe) {
         registryFriendlyByteBuf.writeVarInt(recipe.getIngredients().size());

         for (Ingredient ingredient : recipe.getIngredients()) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(registryFriendlyByteBuf, ingredient);
         }

         ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.output);
         if (recipe.effect != null) {
            registryFriendlyByteBuf.writeBoolean(true);
            registryFriendlyByteBuf.writeResourceLocation(
               recipe.effect.toString().contains(":")
                  ? ResourceLocation.parse(recipe.effect.toString().split(":")[1])
                  : ResourceLocation.withDefaultNamespace("unknown")
            );
            registryFriendlyByteBuf.writeInt(recipe.effectDuration);
         } else {
            registryFriendlyByteBuf.writeBoolean(false);
         }

         registryFriendlyByteBuf.writeInt(recipe.requiredWater);
         registryFriendlyByteBuf.writeInt(recipe.requiredHeat);
         registryFriendlyByteBuf.writeInt(recipe.requiredDuration);
         registryFriendlyByteBuf.writeFloat(recipe.experience);
      }

      public MapCodec<TeaKettleRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, TeaKettleRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }

   public static class Type implements RecipeType<TeaKettleRecipe> {
      private Type() {
      }
   }
}
