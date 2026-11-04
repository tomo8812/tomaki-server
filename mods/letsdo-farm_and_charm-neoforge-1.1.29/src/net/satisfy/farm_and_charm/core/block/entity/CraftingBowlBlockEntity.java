package net.satisfy.farm_and_charm.core.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.recipe.CraftingBowlRecipe;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.StoredExperience;
import net.satisfy.foundation.registry.FoundationParticles;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftingBowlBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer, BlockEntityTicker<CraftingBowlBlockEntity> {
   private NonNullList<ItemStack> stacks = NonNullList.withSize(5, ItemStack.EMPTY);
   private float whiskAngle;
   private float whiskAnglePrev;
   private float whiskSpeed;
   private float stirred;
   private final StoredExperience experience = new StoredExperience();
   private long lastStir;
   @Nullable
   private Boolean hasRecipe;
   public static final float WHISK_MAX_SPEED = 1.2F;
   private static final int DOUGH_COLOR = -1986719;
   private static final float WHISK_DECAY = 0.93F;
   private static final float STIR_RATE = 1.25F;
   private static final int SYNC_INTERVAL = 10;
   private static final int TAKE_OUT_DELAY = 8;

   public CraftingBowlBlockEntity(BlockPos position, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.CRAFTING_BOWL_BLOCK_ENTITY.get(), position, state);
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      if (!this.tryLoadLootTable(tag)) {
         this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      }

      ContainerHelper.loadAllItems(tag, this.stacks, provider);
      this.hasRecipe = null;
      this.whiskSpeed = tag.getFloat("WhiskSpeed");
      this.stirred = tag.getFloat("Stirred");
      this.experience.load(tag);
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      if (!this.trySaveLootTable(tag)) {
         ContainerHelper.saveAllItems(tag, this.stacks, provider);
      }

      tag.putFloat("WhiskSpeed", this.whiskSpeed);
      tag.putFloat("Stirred", this.stirred);
      this.experience.save(tag);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      return this.saveWithoutMetadata(provider);
   }

   public int getContainerSize() {
      return this.stacks.size();
   }

   public boolean isEmpty() {
      for (ItemStack itemstack : this.stacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @NotNull
   public Component getDefaultName() {
      return Component.literal("crafting_bowl");
   }

   @NotNull
   public AbstractContainerMenu createMenu(int id, Inventory inventory) {
      return ChestMenu.threeRows(id, inventory);
   }

   public int getMaxStackSize() {
      return 64;
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return index >= 0 && index < 4;
   }

   @NotNull
   public NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   protected void setItems(NonNullList<ItemStack> stacks) {
      this.stacks = stacks;
      this.hasRecipe = null;
   }

   public boolean canAddItem() {
      for (int i = 0; i < 4; i++) {
         if (this.getItem(i).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public void addItemStack(ItemStack stack) {
      for (int j = 0; j < 4; j++) {
         if (this.getItem(j).isEmpty()) {
            ItemStack one = stack.copy();
            one.setCount(1);
            this.setItem(j, one);
            this.setChanged();
            return;
         }
      }
   }

   public int @NotNull [] getSlotsForFace(Direction side) {
      return IntStream.range(0, this.getContainerSize()).toArray();
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
      return this.canPlaceItem(index, stack);
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return true;
   }

   private ItemStack getRemainderItem(ItemStack stack) {
      return stack.getItem().hasCraftingRemainingItem() ? new ItemStack(Objects.requireNonNull(stack.getItem().getCraftingRemainingItem())) : ItemStack.EMPTY;
   }

   public float getStirred() {
      return this.stirred;
   }

   public boolean isFinished() {
      return !this.getItem(4).isEmpty();
   }

   public void markStirred(Level level) {
      this.lastStir = level.getGameTime();
   }

   public boolean canTakeOut(Level level) {
      return level.getGameTime() - this.lastStir > 8L;
   }

   public void resetStirring() {
      this.stirred = 0.0F;
   }

   public Optional<CraftingBowlRecipe> findRecipe(Level level) {
      if (!this.getItem(4).isEmpty()) {
         return Optional.empty();
      }

      List<RecipeHolder<CraftingBowlRecipe>> all = level.getRecipeManager().getAllRecipesFor((RecipeType)RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get());
      return Optional.ofNullable(this.matchExact(all));
   }

   public float getDoughFill() {
      if (this.isFinished()) {
         return 1.0F;
      } else {
         return this.stirred > 0.0F && this.hasRecipe() ? Math.min(1.0F, this.stirred / 200.0F) : 0.0F;
      }
   }

   public void splashDough(ServerLevel server, BlockPos pos, int amount) {
      float fill = this.getDoughFill();
      if (!(fill <= 0.0F) && amount > 0) {
         double y = pos.getY() + 0.06 + fill * 0.375;
         server.sendParticles(
            ColorParticleOption.create((ParticleType)FoundationParticles.DYE_SPLASH.get(), -1986719),
            pos.getX() + 0.5,
            y,
            pos.getZ() + 0.5,
            amount,
            0.12,
            0.02,
            0.12,
            0.05 + this.whiskSpeed * 0.08
         );
      }
   }

   public boolean hasRecipe() {
      if (this.hasRecipe == null) {
         this.hasRecipe = this.level != null && this.numberOfIngredientsInBowl() > 0 && this.findRecipe(this.level).isPresent();
      }

      return this.hasRecipe;
   }

   private int numberOfIngredientsInBowl() {
      int num = 0;
      if (!this.getItem(0).isEmpty()) {
         num++;
      }

      if (!this.getItem(1).isEmpty()) {
         num++;
      }

      if (!this.getItem(2).isEmpty()) {
         num++;
      }

      if (!this.getItem(3).isEmpty()) {
         num++;
      }

      return num;
   }

   private CraftingBowlRecipe matchExact(List<RecipeHolder<CraftingBowlRecipe>> recipes) {
      int present = this.numberOfIngredientsInBowl();

      for (RecipeHolder<CraftingBowlRecipe> holder : recipes) {
         CraftingBowlRecipe r = (CraftingBowlRecipe)holder.value();
         int needed = 0;

         for (Ingredient ing : r.getIngredients()) {
            if (!ing.isEmpty()) {
               needed++;
            }
         }

         if (present == needed) {
            boolean[] used = new boolean[4];
            boolean ok = true;

            for (Ingredient ing : r.getIngredients()) {
               if (!ing.isEmpty()) {
                  boolean matched = false;

                  for (int i = 0; i < 4; i++) {
                     ItemStack item = this.getItem(i);
                     if (!used[i] && !item.isEmpty() && ing.test(item)) {
                        used[i] = true;
                        matched = true;
                        break;
                     }
                  }

                  if (!matched) {
                     ok = false;
                     break;
                  }
               }
            }

            if (ok) {
               return r;
            }
         }
      }

      return null;
   }

   public void setItem(int slot, ItemStack stack) {
      super.setItem(slot, stack);
      this.hasRecipe = null;
      this.setChanged();
   }

   @NotNull
   public ItemStack removeItem(int slot, int amount) {
      this.hasRecipe = null;
      return super.removeItem(slot, amount);
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int slot) {
      this.hasRecipe = null;
      return super.removeItemNoUpdate(slot);
   }

   public void setChanged() {
      super.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
      }
   }

   public float getInterpolatedWhiskAngle(float partial) {
      float a0 = this.whiskAnglePrev;
      float a1 = this.whiskAngle;
      float da = a1 - a0;
      float tau = (float) (Math.PI * 2);
      if (da > Math.PI) {
         da -= tau;
      }

      if (da < -Math.PI) {
         da += tau;
      }

      return a0 + da * partial;
   }

   public void addWhiskImpulse(float v) {
      this.whiskSpeed = Math.min(1.2F, this.whiskSpeed + v);
      this.setChanged();
   }

   public float getWhiskSpeed() {
      return this.whiskSpeed;
   }

   public void tick(Level level, BlockPos pos, BlockState state, CraftingBowlBlockEntity be) {
      this.whiskAnglePrev = this.whiskAngle;
      this.whiskSpeed *= 0.93F;
      if (this.whiskSpeed < 0.005F) {
         this.whiskSpeed = 0.0F;
      }

      this.whiskAngle = this.whiskAngle + this.whiskSpeed;
      float tau = (float) (Math.PI * 2);
      if (this.whiskAngle > tau) {
         this.whiskAngle -= tau;
      }

      if (this.whiskAngle < 0.0F) {
         this.whiskAngle += tau;
      }

      if (!(this.whiskSpeed <= 0.0F)) {
         if (!this.isFinished() && this.hasRecipe()) {
            this.stirred = Math.min(200.0F, this.stirred + this.whiskSpeed / 1.2F * 1.25F);
         }

         if (level instanceof ServerLevel server) {
            if (this.whiskSpeed > 0.3F && level.getGameTime() % 4L == 0L) {
               this.sprayIngredients(server, pos, Math.round(this.whiskSpeed * 2.0F));
               this.splashDough(server, pos, Math.round(this.whiskSpeed * 2.0F));
            }

            if (this.stirred >= 200.0F && !this.isFinished()) {
               this.finish(level, pos);
            } else if (level.getGameTime() % 10L == 0L) {
               this.setChanged();
            }
         }
      }
   }

   private void finish(Level level, BlockPos pos) {
      Optional<CraftingBowlRecipe> recipe = this.findRecipe(level);
      if (!recipe.isEmpty()) {
         for (int i = 0; i < 4; i++) {
            this.setItem(i, this.getRemainderItem(this.getItem(i)));
         }

         level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 0.7F);
         level.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.5F, 1.2F);
         ItemStack resultItem = recipe.get().getResultItem(level.registryAccess()).copy();
         resultItem.setCount(recipe.get().getOutputCount());
         this.stirred = 0.0F;
         this.setItem(4, resultItem);
         if (level instanceof ServerLevel serverLevel) {
            this.experience.add(recipe.get().getExperience());
            this.experience.award(serverLevel, Vec3.atCenterOf(pos));
         }
      }
   }

   private void sprayIngredients(ServerLevel server, BlockPos pos, int amount) {
      List<ItemStack> present = new ArrayList<>();

      for (int i = 0; i < 4; i++) {
         if (!this.getItem(i).isEmpty()) {
            present.add(this.getItem(i));
         }
      }

      if (!present.isEmpty() && amount > 0) {
         ItemStack stack = present.get(server.random.nextInt(present.size()));
         server.sendParticles(
            new ItemParticleOption(ParticleTypes.ITEM, stack),
            pos.getX() + 0.5,
            pos.getY() + 0.4,
            pos.getZ() + 0.5,
            amount,
            0.12,
            0.02,
            0.12,
            0.05 + this.whiskSpeed * 0.08
         );
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void tick(Level param1, BlockPos nullx, BlockState nullxx, BlockEntity nullxxx) {
      this.tick(var1, nullx, nullxx, (CraftingBowlBlockEntity)nullxxx);
   }
}
