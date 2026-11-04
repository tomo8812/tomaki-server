package net.satisfy.farm_and_charm.core.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Position;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.MincerBlock;
import net.satisfy.farm_and_charm.core.recipe.MincerRecipe;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.StoredExperience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MincerBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer, BlockEntityTicker<MincerBlockEntity> {
   public final int SLOT_COUNT = 2;
   public final int INPUT_SLOT = 0;
   public final int OUTPUT_SLOT = 1;
   private NonNullList<ItemStack> stacks = NonNullList.withSize(2, ItemStack.EMPTY);
   public static final float CRANK_MAX_SPEED = 0.5F;
   private float crankAngle;
   private float crankAnglePrev;
   private float crankSpeed;
   private float crankTargetSpeed;
   private int crankTicks;
   private float cranked;
   private final StoredExperience experience = new StoredExperience();
   private static final int CRANK_TICKS_PER_CLICK = 10;
   private static final int SYNC_INTERVAL = 10;

   public MincerBlockEntity(BlockPos position, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.MINCER_BLOCK_ENTITY.get(), position, state);
   }

   public static void spawnItem(Level world, ItemStack stack, int speed, Direction side, Position pos) {
      double d = pos.x();
      double e = pos.y();
      double f = pos.z();
      if (side.getAxis() == Axis.Y) {
         e -= 0.125;
      } else {
         e -= 0.15625;
      }

      ItemEntity itemEntity = new ItemEntity(world, d, e, f, stack);
      double g = world.random.nextDouble() * 0.1 + 0.2;
      itemEntity.setDeltaMovement(
         world.random.triangle(side.getStepX() * g, 0.0172275 * speed),
         world.random.triangle(0.2, 0.0172275 * speed),
         world.random.triangle(side.getStepZ() * g, 0.0172275 * speed)
      );
      world.addFreshEntity(itemEntity);
   }

   public void loadAdditional(CompoundTag compound, Provider provider) {
      super.loadAdditional(compound, provider);
      if (!this.tryLoadLootTable(compound)) {
         this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      }

      ContainerHelper.loadAllItems(compound, this.stacks, provider);
      this.crankSpeed = compound.getFloat("CrankSpeed");
      this.crankTargetSpeed = compound.getFloat("CrankTargetSpeed");
      this.crankTicks = compound.getInt("CrankTicks");
      this.cranked = compound.getFloat("Cranked");
      this.experience.load(compound);
   }

   public void saveAdditional(CompoundTag compound, Provider provider) {
      super.saveAdditional(compound, provider);
      if (!this.trySaveLootTable(compound)) {
         ContainerHelper.saveAllItems(compound, this.stacks, provider);
      }

      compound.putFloat("CrankSpeed", this.crankSpeed);
      compound.putFloat("CrankTargetSpeed", this.crankTargetSpeed);
      compound.putInt("CrankTicks", this.crankTicks);
      compound.putFloat("Cranked", this.cranked);
      this.experience.save(compound);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      return this.saveWithoutMetadata(provider);
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
      return Component.literal("mincer");
   }

   @NotNull
   public AbstractContainerMenu createMenu(int id, Inventory inventory) {
      return ChestMenu.threeRows(id, inventory);
   }

   private void dropItemsInOutputSlot(Level level, BlockPos pos, BlockState state, MincerBlockEntity mincer) {
      Direction direction = ((Direction)state.getValue(MincerBlock.FACING)).getClockWise();
      if (!level.isClientSide() && !((ItemStack)this.stacks.get(1)).isEmpty()) {
         ItemStack droppedStack = (ItemStack)mincer.stacks.get(1);
         this.stacks.set(1, ItemStack.EMPTY);
         Vec3 vec3d = Vec3.atCenterOf(pos);
         Vec3 vec3d2 = vec3d.relative(direction, 0.7);
         ((ServerLevel)level).sendParticles(ParticleTypes.SPIT, vec3d2.x(), vec3d2.y(), vec3d2.z(), 3, 0.2, 0.1, 0.0, 0.1);
         spawnItem(level, droppedStack, 6, direction, vec3d2);
         this.setChanged();
      }
   }

   public int getMaxStackSize() {
      return 64;
   }

   public int getContainerSize() {
      return this.stacks.size();
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      ItemStack inputSlotItemStack = (ItemStack)this.stacks.get(0);
      return index == 0 && inputSlotItemStack.isEmpty()
         || stack.is(inputSlotItemStack.getItem()) && inputSlotItemStack.getCount() < inputSlotItemStack.getMaxStackSize();
   }

   @NotNull
   public ItemStack getItem(int index) {
      return (ItemStack)this.getItems().get(index);
   }

   @NotNull
   public NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   protected void setItems(NonNullList<ItemStack> stacks) {
      this.stacks = stacks;
   }

   public int @NotNull [] getSlotsForFace(Direction side) {
      return IntStream.range(0, this.getContainerSize()).toArray();
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
      return this.canPlaceItem(index, stack);
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return direction == Direction.UP && this.canPlaceItem(index, stack);
   }

   private MincerRecipe getRecipe(List<RecipeHolder<MincerRecipe>> recipes, NonNullList<ItemStack> inventory) {
      for (RecipeHolder<MincerRecipe> recipeHolder : recipes) {
         MincerRecipe recipe = (MincerRecipe)recipeHolder.value();
         boolean ok = true;

         for (Ingredient ingredient : recipe.getIngredients()) {
            boolean found = false;

            for (ItemStack slotItem : inventory) {
               if (ingredient.test(slotItem)) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               ok = false;
               break;
            }
         }

         if (ok) {
            return recipe;
         }
      }

      return null;
   }

   @Nullable
   public MincerRecipe findRecipe(Level level) {
      return this.getRecipe(level.getRecipeManager().getAllRecipesFor((RecipeType)RecipeTypeRegistry.MINCER_RECIPE_TYPE.get()), this.stacks);
   }

   public void setItem(int slot, ItemStack stack) {
      super.setItem(slot, stack);
      this.setChanged();
   }

   public void setChanged() {
      super.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
      }
   }

   public float getInterpolatedCrankAngle(float partial) {
      float a0 = this.crankAnglePrev;
      float a1 = this.crankAngle;
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

   public int getCrankTicks() {
      return this.crankTicks;
   }

   public float getCranked() {
      return this.cranked;
   }

   public void crank() {
      this.crankTicks = 10;
      this.setChanged();
   }

   public void addCrankImpulse(float v) {
      this.crankTargetSpeed = Math.min(0.5F, this.crankTargetSpeed + v);
   }

   public float getCrankSpeed() {
      return this.crankSpeed;
   }

   public void tick(Level level, BlockPos pos, BlockState state, MincerBlockEntity mincer) {
      this.dropItemsInOutputSlot(level, pos, state, mincer);
      this.crankAnglePrev = this.crankAngle;
      this.crankTargetSpeed = this.crankTicks > 0 ? 0.5F : 0.0F;
      float k = 0.22F;
      this.crankSpeed = this.crankSpeed + (this.crankTargetSpeed - this.crankSpeed) * k;
      if (this.crankTicks == 0) {
         this.crankSpeed *= 0.96F;
      }

      if (this.crankSpeed < 0.001F) {
         this.crankSpeed = 0.0F;
      }

      this.crankAngle = this.crankAngle + this.crankSpeed;
      float tau = (float) (Math.PI * 2);
      if (this.crankAngle > tau) {
         this.crankAngle -= tau;
      }

      if (this.crankAngle < 0.0F) {
         this.crankAngle += tau;
      }

      if (this.crankTicks > 0) {
         this.crankTicks--;
      }

      if (!(this.crankSpeed <= 0.0F)) {
         if (!((ItemStack)this.stacks.get(0)).isEmpty()) {
            this.cranked = Math.min(20.0F, this.cranked + this.crankSpeed / 0.5F);
         }

         if (!level.isClientSide) {
            if (this.cranked >= 20.0F) {
               this.cranked = 0.0F;
               MincerRecipe recipe = this.getRecipe(
                  level.getRecipeManager().getAllRecipesFor((RecipeType)RecipeTypeRegistry.MINCER_RECIPE_TYPE.get()), this.stacks
               );
               if (recipe != null) {
                  level.playSound(null, pos, grindSound(recipe.getRecipeType()), SoundSource.BLOCKS, 0.7F, 0.8F + level.random.nextFloat() * 0.2F);
                  level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 0.7F);
                  ItemStack inputStack = (ItemStack)this.stacks.get(0);
                  inputStack.shrink(1);
                  mincer.setItem(0, inputStack.isEmpty() ? ItemStack.EMPTY : inputStack);
                  mincer.setItem(1, recipe.getResultItem(level.registryAccess()));
                  this.experience.add(recipe.getExperience());
                  this.experience.award((ServerLevel)level, Vec3.atCenterOf(pos));
               } else {
                  this.setChanged();
               }
            } else {
               if (this.crankTicks > 0 && level.getGameTime() % 6L == 0L && !((ItemStack)this.stacks.get(0)).isEmpty()) {
                  MincerRecipe grinding = this.getRecipe(
                     level.getRecipeManager().getAllRecipesFor((RecipeType)RecipeTypeRegistry.MINCER_RECIPE_TYPE.get()), this.stacks
                  );
                  if (grinding != null) {
                     level.playSound(null, pos, grindSound(grinding.getRecipeType()), SoundSource.BLOCKS, 0.3F, 0.7F + level.random.nextFloat() * 0.3F);
                  }
               }

               if (level.getGameTime() % 10L == 0L) {
                  this.setChanged();
               }
            }
         }
      }
   }

   public static SoundEvent grindSound(String recipeType) {
      return switch (recipeType) {
         case "MEAT" -> SoundEvents.SLIME_SQUISH_SMALL;
         case "STONE" -> SoundEvents.GRAVEL_HIT;
         case "METAL" -> SoundEvents.GRINDSTONE_USE;
         default -> SoundEvents.WOOD_HIT;
      };
   }

   public boolean hasValidRecipe(Level level, ItemStack stack) {
      if (level != null && !level.isClientSide && !stack.isEmpty()) {
         RecipeManager recipeManager = level.getRecipeManager();
         List<RecipeHolder<MincerRecipe>> recipes = recipeManager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.MINCER_RECIPE_TYPE.get());
         NonNullList<ItemStack> testInventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
         testInventory.set(0, stack.copy());
         return this.getRecipe(recipes, testInventory) != null;
      } else {
         return false;
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
      this.tick(var1, nullx, nullxx, (MincerBlockEntity)nullxxx);
   }
}
