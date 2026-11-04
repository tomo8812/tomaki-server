package net.satisfy.bakery.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
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
import net.satisfy.bakery.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

public final class BlankCakeInteractionRecipe implements Recipe<BlankCakeInteractionInput> {
   private final BlankCakeStage stage;
   private final Ingredient ingredient;
   private final BlankCakeInteractionRecipe.Result result;
   private final int priority;
   private final float experience;

   public BlankCakeInteractionRecipe(BlankCakeStage stage, Ingredient ingredient, BlankCakeInteractionRecipe.Result result, int priority, float experience) {
      this.experience = experience;
      this.stage = stage;
      this.ingredient = ingredient;
      this.result = result;
      this.priority = priority;
   }

   public BlankCakeStage stage() {
      return this.stage;
   }

   public Ingredient ingredient() {
      return this.ingredient;
   }

   public BlankCakeInteractionRecipe.Result result() {
      return this.result;
   }

   public float experience() {
      return this.experience;
   }

   public int priority() {
      return this.priority;
   }

   public boolean matchesStage(BlankCakeStage currentStage) {
      return this.stage == currentStage;
   }

   public boolean matches(BlankCakeInteractionInput input, Level level) {
      return this.ingredient.test(input.stack());
   }

   @NotNull
   public ItemStack assemble(BlankCakeInteractionInput input, Provider registries) {
      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   @NotNull
   public ItemStack getResultItem(Provider registries) {
      return ItemStack.EMPTY;
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeTypeRegistry.BLANK_CAKE_INTERACTION_SERIALIZER.get();
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)RecipeTypeRegistry.BLANK_CAKE_INTERACTION_TYPE.get();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   public ItemStack assemble(RecipeInput param1, Provider nullx) {
      return this.assemble((BlankCakeInteractionInput)var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public boolean matches(RecipeInput param1, Level nullx) {
      return this.matches((BlankCakeInteractionInput)var1, nullx);
   }

   public record Result(
      ResourceLocation setBlock,
      BlankCakeStage setStage,
      ResourceLocation giveItem,
      ResourceLocation sound,
      boolean consumeOne,
      boolean particles,
      int cooldownTicks,
      CakeAnimation animation
   ) {
      public static final Codec<BlankCakeInteractionRecipe.Result> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               ResourceLocation.CODEC.optionalFieldOf("set_block").forGetter(r -> Optional.ofNullable(r.setBlock)),
               Codec.STRING.xmap(BlankCakeStage::valueOf, Enum::name).optionalFieldOf("set_stage").forGetter(r -> Optional.ofNullable(r.setStage)),
               ResourceLocation.CODEC.optionalFieldOf("give_item").forGetter(r -> Optional.ofNullable(r.giveItem)),
               ResourceLocation.CODEC.optionalFieldOf("sound").forGetter(r -> Optional.ofNullable(r.sound)),
               Codec.BOOL.optionalFieldOf("consume_one", false).forGetter(BlankCakeInteractionRecipe.Result::consumeOne),
               Codec.BOOL.optionalFieldOf("particles", true).forGetter(BlankCakeInteractionRecipe.Result::particles),
               Codec.INT.optionalFieldOf("cooldown_ticks", 0).forGetter(BlankCakeInteractionRecipe.Result::cooldownTicks),
               CakeAnimation.CODEC.optionalFieldOf("animation", CakeAnimation.SPREAD).forGetter(BlankCakeInteractionRecipe.Result::animation)
            )
            .apply(
               instance,
               (setBlock, setStage, giveItem, sound, consumeOne, particles, cooldownTicks, animation) -> new BlankCakeInteractionRecipe.Result(
                  (ResourceLocation)setBlock.orElse(null),
                  (BlankCakeStage)setStage.orElse(null),
                  (ResourceLocation)giveItem.orElse(null),
                  (ResourceLocation)sound.orElse(null),
                  consumeOne,
                  particles,
                  cooldownTicks,
                  animation
               )
            )
      );
   }

   private static final class ResultStream {
      private static final StreamCodec<RegistryFriendlyByteBuf, BlankCakeInteractionRecipe.Result> CODEC = new StreamCodec<RegistryFriendlyByteBuf, BlankCakeInteractionRecipe.Result>() {
         @NotNull
         public BlankCakeInteractionRecipe.Result decode(RegistryFriendlyByteBuf buffer) {
            ResourceLocation setBlock = buffer.readBoolean() ? (ResourceLocation)ResourceLocation.STREAM_CODEC.decode(buffer) : null;
            BlankCakeStage setStage = buffer.readBoolean() ? BlankCakeStage.valueOf(buffer.readUtf()) : null;
            ResourceLocation giveItem = buffer.readBoolean() ? (ResourceLocation)ResourceLocation.STREAM_CODEC.decode(buffer) : null;
            ResourceLocation sound = buffer.readBoolean() ? (ResourceLocation)ResourceLocation.STREAM_CODEC.decode(buffer) : null;
            boolean consumeOne = (Boolean)ByteBufCodecs.BOOL.decode(buffer);
            boolean particles = (Boolean)ByteBufCodecs.BOOL.decode(buffer);
            int cooldownTicks = (Integer)ByteBufCodecs.INT.decode(buffer);
            CakeAnimation animation = (CakeAnimation)CakeAnimation.STREAM_CODEC.decode(buffer);
            return new BlankCakeInteractionRecipe.Result(setBlock, setStage, giveItem, sound, consumeOne, particles, cooldownTicks, animation);
         }

         public void encode(RegistryFriendlyByteBuf buffer, BlankCakeInteractionRecipe.Result value) {
            buffer.writeBoolean(value.setBlock() != null);
            if (value.setBlock() != null) {
               ResourceLocation.STREAM_CODEC.encode(buffer, value.setBlock());
            }

            buffer.writeBoolean(value.setStage() != null);
            if (value.setStage() != null) {
               buffer.writeUtf(value.setStage().name());
            }

            buffer.writeBoolean(value.giveItem() != null);
            if (value.giveItem() != null) {
               ResourceLocation.STREAM_CODEC.encode(buffer, value.giveItem());
            }

            buffer.writeBoolean(value.sound() != null);
            if (value.sound() != null) {
               ResourceLocation.STREAM_CODEC.encode(buffer, value.sound());
            }

            ByteBufCodecs.BOOL.encode(buffer, value.consumeOne());
            ByteBufCodecs.BOOL.encode(buffer, value.particles());
            ByteBufCodecs.INT.encode(buffer, value.cooldownTicks());
            CakeAnimation.STREAM_CODEC.encode(buffer, value.animation());
         }

         // $VF: synthetic method
         // $VF: bridge method
         @NotNull
         public Object decode(Object param1) {
            return this.decode((RegistryFriendlyByteBuf)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public void encode(Object param1, Object nullx) {
            this.encode((RegistryFriendlyByteBuf)var1, (BlankCakeInteractionRecipe.Result)nullx);
         }
      };
   }

   public static final class Serializer implements RecipeSerializer<BlankCakeInteractionRecipe> {
      public static final MapCodec<BlankCakeInteractionRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.STRING.xmap(BlankCakeStage::valueOf, Enum::name).fieldOf("stage").forGetter(BlankCakeInteractionRecipe::stage),
               Ingredient.CODEC.fieldOf("ingredient").forGetter(BlankCakeInteractionRecipe::ingredient),
               BlankCakeInteractionRecipe.Result.CODEC.fieldOf("result").forGetter(BlankCakeInteractionRecipe::result),
               Codec.INT.optionalFieldOf("priority", 1000).forGetter(BlankCakeInteractionRecipe::priority),
               Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(BlankCakeInteractionRecipe::experience)
            )
            .apply(instance, BlankCakeInteractionRecipe::new)
      );
      private static final StreamCodec<RegistryFriendlyByteBuf, BlankCakeInteractionRecipe> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, BlankCakeInteractionRecipe>() {
         @NotNull
         public BlankCakeInteractionRecipe decode(RegistryFriendlyByteBuf buffer) {
            BlankCakeStage stage = BlankCakeStage.valueOf(buffer.readUtf());
            Ingredient ingredient = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            BlankCakeInteractionRecipe.Result result = (BlankCakeInteractionRecipe.Result)BlankCakeInteractionRecipe.ResultStream.CODEC.decode(buffer);
            int priority = buffer.readVarInt();
            float experience = buffer.readFloat();
            return new BlankCakeInteractionRecipe(stage, ingredient, result, priority, experience);
         }

         public void encode(RegistryFriendlyByteBuf buffer, BlankCakeInteractionRecipe value) {
            buffer.writeUtf(value.stage().name());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, value.ingredient());
            BlankCakeInteractionRecipe.ResultStream.CODEC.encode(buffer, value.result());
            buffer.writeVarInt(value.priority());
            buffer.writeFloat(value.experience());
         }

         // $VF: synthetic method
         // $VF: bridge method
         @NotNull
         public Object decode(Object param1) {
            return this.decode((RegistryFriendlyByteBuf)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public void encode(Object param1, Object nullx) {
            this.encode((RegistryFriendlyByteBuf)var1, (BlankCakeInteractionRecipe)nullx);
         }
      };

      @NotNull
      public MapCodec<BlankCakeInteractionRecipe> codec() {
         return CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, BlankCakeInteractionRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
