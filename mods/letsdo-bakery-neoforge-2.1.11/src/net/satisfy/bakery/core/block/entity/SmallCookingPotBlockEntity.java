package net.satisfy.bakery.core.block.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.bakery.core.block.SmallCookingPotBlock;
import net.satisfy.bakery.core.recipe.JamConsistency;
import net.satisfy.bakery.core.recipe.JamRecipe;
import net.satisfy.bakery.core.registry.DataComponentRegistry;
import net.satisfy.bakery.core.registry.EntityTypeRegistry;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import net.satisfy.bakery.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.farm_and_charm.core.util.StoredExperience;
import net.satisfy.foundation.registry.FoundationParticles;
import org.jetbrains.annotations.Nullable;

public class SmallCookingPotBlockEntity extends BlockEntity {
   public static final int MAX_INGREDIENTS = 4;
   public static final int STIR_INTERVAL = 160;
   public static final int STIR_WINDOW = 80;
   public static final int PERFECT_WINDOW = 200;
   public static final float WHISK_MAX_SPEED = 1.2F;
   public static final float RUNNY_FROM = 0.6F;
   private static final int MISSED_STIRS_FOR_CARAMEL = 2;
   public static final int CARAMEL_WINDOW = 300;
   private final List<ItemStack> ingredients = new ArrayList<>();
   @Nullable
   private ResourceLocation recipeId;
   private ItemStack result = ItemStack.EMPTY;
   private int color = 16777215;
   private int cookingTime;
   private int timer;
   private boolean stirDue;
   private int stirDeadline;
   private int missedStirs;
   private long lastStir = -100L;
   private float whiskSpeed;
   private long lastDump = -100L;
   private float whiskAngle;
   private float previousWhiskAngle;
   @Nullable
   private JamConsistency consistency;
   private int jarsLeft;
   private float recipeExperience;
   private final StoredExperience experience = new StoredExperience();
   private boolean burnt;
   private String cook = "";

   public SmallCookingPotBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.SMALL_COOKING_POT_BLOCK_ENTITY.get(), pos, state);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, SmallCookingPotBlockEntity pot) {
      BlockState below = level.getBlockState(pos.below());
      boolean heated = below.is(TagRegistry.ALLOWS_COOKING) || below.is(BlockTags.CAMPFIRES) && (Boolean)below.getValue(CampfireBlock.LIT);
      if ((Boolean)state.getValue(SmallCookingPotBlock.LIT) != heated) {
         level.setBlock(pos, (BlockState)state.setValue(SmallCookingPotBlock.LIT, heated), 3);
      }

      if (heated && pot.isCooking()) {
         pot.stepCooking(level);
      }

      pot.spinWhisk(level);
   }

   public static void clientTick(Level level, BlockPos pos, BlockState state, SmallCookingPotBlockEntity pot) {
      pot.previousWhiskAngle = pot.whiskAngle;
      pot.whiskAngle = pot.whiskAngle + pot.whiskSpeed;
      pot.whiskSpeed = pot.decay(pot.whiskSpeed);
   }

   private float decay(float speed) {
      float next = speed * Mth.lerp(this.getViscosity(), 0.96F, 0.7F);
      return next < 0.005F ? 0.0F : next;
   }

   public float getViscosity() {
      if (this.burnt) {
         return 1.0F;
      }

      if (this.consistency != null) {
         return switch (this.consistency) {
            case RUNNY -> 0.1F;
            case PERFECT -> 0.4F;
            case CARAMELIZED -> 0.8F;
         };
      } else if (this.recipeId == null || this.cookingTime <= 0) {
         return 0.0F;
      } else {
         return this.timer <= this.cookingTime ? 0.4F * this.timer / this.cookingTime : Math.min(0.9F, 0.4F + 0.5F * (this.timer - this.cookingTime) / 500.0F);
      }
   }

   private void spinWhisk(Level level) {
      if (!(this.whiskSpeed <= 0.0F)) {
         if (level instanceof ServerLevel serverLevel && (this.isCooking() || this.isBottling()) && this.whiskSpeed > 0.3F && level.getGameTime() % 4L == 0L) {
            int amount = Math.round(this.whiskSpeed * 2.0F);
            double x = this.worldPosition.getX() + 0.5;
            double y = this.worldPosition.getY() + 0.3;
            double z = this.worldPosition.getZ() + 0.5;
            serverLevel.sendParticles(
               ColorParticleOption.create((ParticleType)FoundationParticles.DYE_SPLASH.get(), 0xFF000000 | this.getDisplayColor()),
               x,
               y,
               z,
               amount,
               0.12,
               0.02,
               0.12,
               0.05 + this.whiskSpeed * 0.1
            );
            serverLevel.sendParticles(
               ColorParticleOption.create((ParticleType)FoundationParticles.COLORED_SOUP_BUBBLE.get(), 0xFF000000 | this.getDisplayColor()),
               x,
               y,
               z,
               1,
               0.12,
               0.0,
               0.12,
               0.0
            );
         }

         this.whiskSpeed = this.decay(this.whiskSpeed);
      }
   }

   private void stepCooking(Level level) {
      this.timer++;
      if (this.stirDue && this.timer > this.stirDeadline) {
         this.stirDue = false;
         this.missedStirs++;
         level.playSound(null, this.worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 0.7F);
         if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
               ParticleTypes.SMOKE, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.45, this.worldPosition.getZ() + 0.5, 6, 0.1, 0.05, 0.1, 0.01
            );
         }

         this.sync();
      }

      if (!this.stirDue && this.timer < this.cookingTime && this.timer % 160 == 0) {
         this.stirDue = true;
         this.stirDeadline = this.timer + 80;
         level.playSound(null, this.worldPosition, SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, SoundSource.BLOCKS, 0.9F, 0.8F);
         this.sync();
      }

      if (this.timer > this.cookingTime + 200 + 300) {
         this.burnt = true;
         this.stirDue = false;
         level.playSound(null, this.worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 0.5F);
         if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
               ParticleTypes.LARGE_SMOKE,
               this.worldPosition.getX() + 0.5,
               this.worldPosition.getY() + 0.45,
               this.worldPosition.getZ() + 0.5,
               10,
               0.12,
               0.05,
               0.12,
               0.02
            );
         }

         this.sync();
      } else {
         if (this.timer == this.cookingTime) {
            level.playSound(null, this.worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5F, 1.2F);
            this.sync();
         } else if (this.timer % 20 == 0) {
            this.sync();
         }
      }
   }

   public boolean addIngredient(ItemStack stack, Player player) {
      if (this.level != null && this.recipeId == null && this.consistency == null && this.ingredients.size() < 4) {
         List<ItemStack> candidate = new ArrayList<>(this.ingredients);
         candidate.add(stack.copyWithCount(1));
         if (this.recipes().stream().noneMatch(recipe -> ((JamRecipe)recipe.value()).accepts(candidate))) {
            return false;
         }

         ItemStack added = stack.copyWithCount(1);
         this.ingredients.add(added.copy());
         ItemStack remainder = stack.getItem().hasCraftingRemainingItem() ? new ItemStack(stack.getItem().getCraftingRemainingItem()) : ItemStack.EMPTY;
         stack.consume(1, player);
         if (!remainder.isEmpty() && !player.getAbilities().instabuild && !player.getInventory().add(remainder)) {
            player.drop(remainder, false);
         }

         this.level.playSound(null, this.worldPosition, SoundEvents.SLIME_SQUISH_SMALL, SoundSource.BLOCKS, 0.5F, 1.3F + this.level.random.nextFloat() * 0.2F);
         if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
               new ItemParticleOption(ParticleTypes.ITEM, added),
               this.worldPosition.getX() + 0.5,
               this.worldPosition.getY() + 0.3,
               this.worldPosition.getZ() + 0.5,
               4,
               0.08,
               0.02,
               0.08,
               0.04
            );
         }

         JamRecipe.Input input = new JamRecipe.Input(List.copyOf(this.ingredients));
         this.level.getRecipeManager().getRecipeFor((RecipeType)RecipeTypeRegistry.JAM_COOKING_TYPE.get(), input, this.level).ifPresent(holder -> {
            this.recipeId = holder.id();
            this.result = ((JamRecipe)holder.value()).getResult().copy();
            this.color = ((JamRecipe)holder.value()).getColor();
            this.cookingTime = ((JamRecipe)holder.value()).getCookingTime();
            this.recipeExperience = ((JamRecipe)holder.value()).getExperience();
            this.timer = 0;
            this.missedStirs = 0;
            this.stirDue = false;
            this.cook = player.getGameProfile().getName();
         });
         this.sync();
         return true;
      } else {
         return false;
      }
   }

   public boolean dump(@Nullable Player player) {
      if (this.level != null && !this.isEmpty()) {
         if (player != null && this.recipeId == null && this.consistency == null) {
            for (ItemStack ingredient : this.ingredients) {
               if (!player.getInventory().add(ingredient.copy())) {
                  player.drop(ingredient.copy(), false);
               }
            }
         } else if (this.level instanceof ServerLevel serverLevel) {
            Direction side = ((Direction)this.getBlockState().getValue(SmallCookingPotBlock.FACING)).getClockWise();
            double x = this.worldPosition.getX() + 0.5 + side.getStepX() * 0.35;
            double y = this.worldPosition.getY() + 0.3;
            double z = this.worldPosition.getZ() + 0.5 + side.getStepZ() * 0.35;
            serverLevel.sendParticles(
               ColorParticleOption.create((ParticleType)FoundationParticles.DYE_SPLASH.get(), 0xFF000000 | this.getDisplayColor()),
               x,
               y,
               z,
               4,
               0.08,
               0.05,
               0.08,
               0.08
            );
            if (this.burnt) {
               serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 8, 0.1, 0.05, 0.1, 0.02);
            }
         }

         this.reset();
         this.lastDump = this.level.getGameTime();
         this.level.playSound(null, this.worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.6F, 0.8F);
         this.sync();
         return true;
      } else {
         return false;
      }
   }

   public boolean isEmpty() {
      return this.ingredients.isEmpty() && this.recipeId == null && this.consistency == null && !this.burnt;
   }

   public long getLastDump() {
      return this.lastDump;
   }

   public boolean stir() {
      if (this.level == null) {
         return false;
      }

      float viscosity = this.getViscosity();
      float limit = this.burnt ? 0.15F : Mth.lerp(viscosity, 1.2F, 0.3F);
      float impulse = this.burnt ? 0.15F : Mth.lerp(viscosity, 0.18F, 0.05F);
      this.whiskSpeed = Math.min(limit, this.whiskSpeed + impulse);
      if (this.isCooking()) {
         this.lastStir = this.level.getGameTime();
         this.stirDue = false;
      }

      if ((this.isCooking() || this.isBottling()) && this.level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            ColorParticleOption.create((ParticleType)FoundationParticles.DYE_SPLASH.get(), 0xFF000000 | this.getDisplayColor()),
            this.worldPosition.getX() + 0.5,
            this.worldPosition.getY() + 0.3,
            this.worldPosition.getZ() + 0.5,
            1,
            0.12,
            0.02,
            0.12,
            0.06
         );
      }

      this.level
         .playSound(
            null,
            this.worldPosition,
            this.burnt ? SoundEvents.MUD_STEP : (SoundEvent)SoundEventRegistry.CRAFTING_BOWL_STIRRING.get(),
            SoundSource.BLOCKS,
            0.5F + this.whiskSpeed * 0.3F,
            Math.max(0.5F, 0.9F + this.whiskSpeed * 0.8F - viscosity * 0.4F)
         );
      this.sync();
      return true;
   }

   public boolean canBottle() {
      return this.consistency != null ? this.jarsLeft > 0 : this.isCooking() && this.timer >= this.cookingTime * 0.6F;
   }

   public ItemStack bottle() {
      if (this.level != null && this.canBottle()) {
         if (this.consistency == null) {
            this.consistency = this.currentConsistency();

            int bonus = switch (this.consistency) {
               case RUNNY -> 1;
               case PERFECT -> 0;
               case CARAMELIZED -> -1;
            };
            this.jarsLeft = Math.max(1, this.result.getCount() + bonus);
            if (this.level instanceof ServerLevel serverLevel) {
               this.experience.add(this.recipeExperience);
               this.experience.award(serverLevel, Vec3.atCenterOf(this.worldPosition));
            }

            this.recipeExperience = 0.0F;
            this.ingredients.clear();
            this.stirDue = false;
         }

         ItemStack jam = this.jarOf(1);
         if (this.level instanceof ServerLevel serverLevel) {
            int color = 0xFF000000 | this.getDisplayColor();
            double x = this.worldPosition.getX() + 0.5;
            double y = this.worldPosition.getY() + 0.35;
            double z = this.worldPosition.getZ() + 0.5;
            serverLevel.sendParticles(ColorParticleOption.create((ParticleType)FoundationParticles.DYE_SPLASH.get(), color), x, y, z, 2, 0.1, 0.02, 0.1, 0.06);
            if (this.consistency == JamConsistency.PERFECT) {
               serverLevel.sendParticles(
                  ColorParticleOption.create((ParticleType)FoundationParticles.COLORED_SOUP_BUBBLE.get(), color), x, y, z, 3, 0.12, 0.02, 0.12, 0.0
               );
               this.level.playSound(null, this.worldPosition, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.8F, 1.4F);
            }
         }

         this.jarsLeft--;
         this.level.playSound(null, this.worldPosition, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 0.9F);
         if (this.jarsLeft <= 0) {
            this.reset();
         }

         this.sync();
         return jam;
      } else {
         return ItemStack.EMPTY;
      }
   }

   private ItemStack jarOf(int count) {
      ItemStack jam = this.result.copyWithCount(count);
      if (this.consistency == JamConsistency.PERFECT) {
         jam.set((DataComponentType)DataComponentRegistry.PERFECT_JAM.get(), this.cook);
         jam.set(DataComponents.ITEM_NAME, Component.translatable("item.bakery.perfect_jam", new Object[]{this.result.getHoverName()}));
      }

      return jam;
   }

   public JamConsistency currentConsistency() {
      if (this.consistency != null) {
         return this.consistency;
      } else if (this.timer < this.cookingTime) {
         return JamConsistency.RUNNY;
      } else {
         return this.timer <= this.cookingTime + 200 && this.missedStirs < 2 ? JamConsistency.PERFECT : JamConsistency.CARAMELIZED;
      }
   }

   private void reset() {
      this.ingredients.clear();
      this.recipeId = null;
      this.result = ItemStack.EMPTY;
      this.color = 16777215;
      this.cookingTime = 0;
      this.timer = 0;
      this.stirDue = false;
      this.missedStirs = 0;
      this.consistency = null;
      this.jarsLeft = 0;
      this.burnt = false;
      this.cook = "";
   }

   public List<ItemStack> getNextIngredients() {
      List<ItemStack> next = new ArrayList<>();
      if (this.level != null && this.recipeId == null && this.consistency == null) {
         for (RecipeHolder<JamRecipe> holder : this.recipes()) {
            JamRecipe recipe = (JamRecipe)holder.value();
            if (recipe.accepts(this.ingredients)) {
               recipe.getIngredients().forEach(ingredient -> {
                  ItemStack[] choices = ingredient.getItems();
                  if (choices.length != 0) {
                     List<ItemStack> candidate = new ArrayList<>(this.ingredients);
                     candidate.add(choices[0]);
                     if (recipe.accepts(candidate) && next.stream().noneMatch(known -> ItemStack.isSameItem(known, choices[0]))) {
                        next.add(choices[0]);
                     }
                  }
               });
            }
         }

         return next;
      } else {
         return next;
      }
   }

   private List<RecipeHolder<JamRecipe>> recipes() {
      return this.level == null ? List.of() : this.level.getRecipeManager().getAllRecipesFor((RecipeType)RecipeTypeRegistry.JAM_COOKING_TYPE.get());
   }

   public List<ItemStack> getIngredients() {
      return Collections.unmodifiableList(this.ingredients);
   }

   public List<ItemStack> getDrops() {
      List<ItemStack> drops = new ArrayList<>(this.ingredients);
      if (this.consistency != null && this.jarsLeft > 0) {
         drops.add(this.jarOf(this.jarsLeft));
      }

      return drops;
   }

   public boolean isCooking() {
      return this.recipeId != null && this.consistency == null && !this.burnt;
   }

   public boolean isBurnt() {
      return this.burnt;
   }

   public boolean isBottling() {
      return this.consistency != null && this.jarsLeft > 0;
   }

   public float getStirUrgency() {
      return this.stirDue ? Mth.clamp(1.0F - (this.stirDeadline - this.timer) / 80.0F, 0.0F, 1.0F) : 0.0F;
   }

   public boolean isStirDue() {
      return this.stirDue;
   }

   public boolean isReady() {
      return this.isCooking() && this.timer >= this.cookingTime;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public int getDisplayColor() {
      if (this.burnt) {
         return 1708560;
      }

      float progress = this.cookingTime <= 0 ? 1.0F : Mth.clamp((float)this.timer / this.cookingTime, 0.0F, 1.0F);
      float r = (this.color >> 16 & 0xFF) / 255.0F;
      float g = (this.color >> 8 & 0xFF) / 255.0F;
      float b = (this.color & 0xFF) / 255.0F;
      float lighten = 0.3F * (1.0F - progress);
      r = Mth.lerp(lighten, r, 1.0F);
      g = Mth.lerp(lighten, g, 1.0F);
      b = Mth.lerp(lighten, b, 1.0F);
      if (this.isCooking() && this.timer > this.cookingTime) {
         float caramel = Mth.clamp((this.timer - this.cookingTime) / 500.0F, 0.0F, 1.0F) * 0.75F;
         r = Mth.lerp(caramel, r, 0.42F);
         g = Mth.lerp(caramel, g, 0.22F);
         b = Mth.lerp(caramel, b, 0.1F);
      }

      return (int)(r * 255.0F) << 16 | (int)(g * 255.0F) << 8 | (int)(b * 255.0F);
   }

   public int getColor() {
      return this.color;
   }

   public int getTimer() {
      return this.timer;
   }

   public int getCookingTime() {
      return this.cookingTime;
   }

   public float getWhiskSpeed() {
      return this.whiskSpeed;
   }

   public float getWhiskAngle(float partialTick) {
      return Mth.lerp(partialTick, this.previousWhiskAngle, this.whiskAngle);
   }

   public long getLastStir() {
      return this.lastStir;
   }

   public int getJarsLeft() {
      return this.jarsLeft;
   }

   @Nullable
   public JamConsistency getConsistency() {
      return this.consistency;
   }

   public static boolean isJar(ItemStack stack) {
      return stack.is(((Block)ObjectRegistry.JAR.get()).asItem());
   }

   private void sync() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.ingredients.clear();

      for (Tag entry : tag.getList("Ingredients", 10)) {
         ItemStack.parse(provider, entry).ifPresent(this.ingredients::add);
      }

      this.recipeId = tag.contains("Recipe") ? ResourceLocation.tryParse(tag.getString("Recipe")) : null;
      this.result = tag.contains("Result") ? ItemStack.parseOptional(provider, tag.getCompound("Result")) : ItemStack.EMPTY;
      this.color = tag.contains("Color") ? tag.getInt("Color") : 16777215;
      this.cookingTime = tag.getInt("CookingTime");
      this.timer = tag.getInt("Timer");
      this.stirDue = tag.getBoolean("StirDue");
      this.stirDeadline = tag.getInt("StirDeadline");
      this.missedStirs = tag.getInt("MissedStirs");
      this.lastStir = tag.contains("LastStir") ? tag.getLong("LastStir") : -100L;
      this.whiskSpeed = tag.getFloat("WhiskSpeed");
      this.lastDump = tag.contains("LastDump") ? tag.getLong("LastDump") : -100L;
      this.consistency = tag.contains("Consistency") ? SmallCookingPotBlockEntity.JamConsistencyLookup.byName(tag.getString("Consistency")) : null;
      this.jarsLeft = tag.getInt("JarsLeft");
      this.burnt = tag.getBoolean("Burnt");
      this.cook = tag.getString("Cook");
      this.recipeExperience = tag.getFloat("RecipeExperience");
      this.experience.load(tag);
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      ListTag list = new ListTag();
      this.ingredients.forEach(stack -> list.add(stack.save(provider)));
      tag.put("Ingredients", list);
      if (this.recipeId != null) {
         tag.putString("Recipe", this.recipeId.toString());
      }

      if (!this.result.isEmpty()) {
         tag.put("Result", this.result.save(provider));
      }

      tag.putInt("Color", this.color);
      tag.putInt("CookingTime", this.cookingTime);
      tag.putInt("Timer", this.timer);
      tag.putBoolean("StirDue", this.stirDue);
      tag.putInt("StirDeadline", this.stirDeadline);
      tag.putInt("MissedStirs", this.missedStirs);
      tag.putLong("LastStir", this.lastStir);
      tag.putFloat("WhiskSpeed", this.whiskSpeed);
      tag.putLong("LastDump", this.lastDump);
      if (this.consistency != null) {
         tag.putString("Consistency", this.consistency.getSerializedName());
      }

      tag.putInt("JarsLeft", this.jarsLeft);
      tag.putBoolean("Burnt", this.burnt);
      tag.putString("Cook", this.cook);
      tag.putFloat("RecipeExperience", this.recipeExperience);
      this.experience.save(tag);
   }

   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = super.getUpdateTag(provider);
      this.saveAdditional(tag, provider);
      return tag;
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   private static final class JamConsistencyLookup {
      @Nullable
      private static JamConsistency byName(String name) {
         for (JamConsistency value : JamConsistency.values()) {
            if (value.getSerializedName().equals(name)) {
               return value;
            }
         }

         return null;
      }
   }
}
