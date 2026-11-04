package net.satisfy.herbalbrews.core.blocks.entity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.herbalbrews.client.gui.handler.TeaKettleGuiHandler;
import net.satisfy.herbalbrews.core.blocks.TeaKettleBlock;
import net.satisfy.herbalbrews.core.recipe.TeaKettleRecipe;
import net.satisfy.herbalbrews.core.registry.EntityTypeRegistry;
import net.satisfy.herbalbrews.core.registry.RecipeTypeRegistry;
import net.satisfy.herbalbrews.core.registry.TagsRegistry;
import net.satisfy.herbalbrews.core.world.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeaKettleBlockEntity extends BlockEntity implements ImplementedInventory, MenuProvider {
   private final NonNullList<ItemStack> inventory = NonNullList.withSize(8, ItemStack.EMPTY);
   private static final int MAX_CAPACITY = 8;
   private static final int MAX_HEAT_LEVEL = 100;
   private static final int HEAT_CONSUMPTION_THRESHOLD = 70;
   private static final int HEAT_PER_ITEM = 35;
   private int cookingTime;
   private int requiredDuration;
   public static final int OUTPUT_SLOT = 0;
   private static final int INGREDIENTS_AREA = 5;
   private static final int WATER_SLOT = 6;
   private static final int HEATING_SLOT = 7;
   private boolean isBeingBurned;
   protected float experience;
   private int waterLevel;
   private int heatLevel;
   private int heatDecreaseCounter = 0;
   private static final int HEAT_DECREASE_INTERVAL = 200;
   private final ContainerData delegate;
   public boolean doEffect;

   public TeaKettleBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.TEA_KETTLE_BLOCK_ENTITY.get(), pos, state);
      this.isBeingBurned = false;
      this.heatLevel = 0;
      this.delegate = new ContainerData() {
         public int get(int index) {
            return switch (index) {
               case 0 -> TeaKettleBlockEntity.this.cookingTime;
               case 1 -> TeaKettleBlockEntity.this.isBeingBurned ? 1 : 0;
               case 2 -> TeaKettleBlockEntity.this.waterLevel;
               case 3 -> TeaKettleBlockEntity.this.heatLevel;
               case 4 -> TeaKettleBlockEntity.this.requiredDuration;
               default -> 0;
            };
         }

         public void set(int index, int value) {
            switch (index) {
               case 0:
                  TeaKettleBlockEntity.this.cookingTime = value;
                  break;
               case 1:
                  TeaKettleBlockEntity.this.isBeingBurned = value != 0;
                  TeaKettleBlockEntity.this.heatLevel = TeaKettleBlockEntity.this.isBeingBurned ? 30 : 0;
                  break;
               case 2:
                  TeaKettleBlockEntity.this.waterLevel = value;
                  break;
               case 3:
                  TeaKettleBlockEntity.this.heatLevel = value;
                  break;
               case 4:
                  TeaKettleBlockEntity.this.requiredDuration = value;
            }
         }

         public int getCount() {
            return 5;
         }
      };
   }

   protected void loadAdditional(CompoundTag nbt, Provider provider) {
      super.loadAdditional(nbt, provider);
      ContainerHelper.loadAllItems(nbt, this.inventory, provider);
      this.cookingTime = nbt.getInt("CookingTime");
      this.isBeingBurned = nbt.getBoolean("IsBeingBurned");
      this.waterLevel = nbt.getInt("WaterLevel");
      this.heatLevel = nbt.getInt("HeatLevel");
      this.experience = nbt.getFloat("Experience");
      this.requiredDuration = nbt.getInt("RequiredDuration");
   }

   protected void saveAdditional(CompoundTag nbt, Provider provider) {
      super.saveAdditional(nbt, provider);
      ContainerHelper.saveAllItems(nbt, this.inventory, provider);
      nbt.putInt("CookingTime", this.cookingTime);
      nbt.putBoolean("IsBeingBurned", this.isBeingBurned);
      nbt.putInt("WaterLevel", this.waterLevel);
      nbt.putInt("HeatLevel", this.heatLevel);
      nbt.putFloat("Experience", this.experience);
      nbt.putInt("RequiredDuration", this.requiredDuration);
   }

   public boolean isBeingBurned() {
      if (this.getLevel() == null) {
         throw new NullPointerException("Null world invoked");
      }

      BlockState belowState = this.getLevel().getBlockState(this.getBlockPos().below());
      Optional<Named<Block>> optionalList = BuiltInRegistries.BLOCK.getTag(TagsRegistry.ALLOWS_COOKING);
      Named<Block> entryList = optionalList.orElse(null);
      return entryList == null ? false : entryList.contains(belowState.getBlock().builtInRegistryHolder());
   }

   private boolean canCraft(TeaKettleRecipe recipe) {
      if (recipe == null || recipe.getResultItem().isEmpty()) {
         return false;
      } else if (this.getItem(0).isEmpty()) {
         return this.waterLevel >= recipe.getRequiredWater() && this.heatLevel >= recipe.getRequiredHeat();
      } else {
         ItemStack recipeOutput = recipe.getResultItem();
         ItemStack outputSlotStack = this.getItem(0);
         int outputSlotCount = outputSlotStack.getCount();
         if (!ItemStack.isSameItem(outputSlotStack, recipeOutput)) {
            return false;
         } else if (outputSlotCount < this.getMaxStackSize() && outputSlotCount < outputSlotStack.getMaxStackSize()) {
            return this.waterLevel >= recipe.getRequiredWater() && this.heatLevel >= recipe.getRequiredHeat();
         } else {
            return this.waterLevel >= recipe.getRequiredWater() && this.heatLevel >= recipe.getRequiredHeat()
               ? outputSlotCount < recipeOutput.getMaxStackSize()
               : false;
         }
      }
   }

   private void craft(TeaKettleRecipe recipe) {
      if (this.canCraft(recipe)) {
         NonNullList<ItemStack> ingredients = NonNullList.create();

         for (int i = 1; i <= 5; i++) {
            ingredients.add(this.getItem(i));
         }

         for (Ingredient ingredient : recipe.getIngredients()) {
            boolean ingredientConsumed = false;

            for (int i = 0; i < ingredients.size(); i++) {
               ItemStack inputStack = (ItemStack)ingredients.get(i);
               if (!inputStack.isEmpty() && ingredient.test(inputStack)) {
                  ItemStack remainderStack = this.getRemainderItem(inputStack);
                  inputStack.shrink(1);
                  if (inputStack.isEmpty()) {
                     this.setItem(i + 1, remainderStack);
                  }

                  ingredientConsumed = true;
                  ingredients.set(i, inputStack);
                  break;
               }
            }

            if (!ingredientConsumed) {
               return;
            }
         }

         ItemStack recipeOutput = recipe.assemble();
         if (recipe.getEffect() != null && recipe.getEffectDuration() > 0) {
            PotionContents potionContents = ((PotionContents)recipeOutput.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY))
               .withEffectAdded(
                  new MobEffectInstance(
                     BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)BuiltInRegistries.MOB_EFFECT.get(recipe.getEffect())), recipe.getEffectDuration()
                  )
               );
            recipeOutput.set(DataComponents.POTION_CONTENTS, potionContents);
         }

         ItemStack outputSlotStack = this.getItem(0);
         this.popExp(recipe.getExperience());
         if (outputSlotStack.isEmpty()) {
            this.setItem(0, recipeOutput);
         } else if (outputSlotStack.is(recipeOutput.getItem())) {
            outputSlotStack.grow(recipeOutput.getCount());
         }

         this.waterLevel = this.waterLevel - recipe.getRequiredWater();
         if (this.waterLevel < 0) {
            this.waterLevel = 0;
         }

         this.requiredDuration = recipe.getRequiredDuration();
         this.cookingTime = 0;
      }
   }

   private ItemStack getRemainderItem(ItemStack stack) {
      return stack.getItem().hasCraftingRemainingItem() ? new ItemStack(Objects.requireNonNull(stack.getItem().getCraftingRemainingItem())) : ItemStack.EMPTY;
   }

   public void consumeHeatItem() {
      ItemStack heatingItem = (ItemStack)this.inventory.get(7);
      if (!heatingItem.isEmpty() && heatingItem.is(TagsRegistry.HEAT_ITEMS)) {
         heatingItem.shrink(1);
         this.inventory.set(7, heatingItem);
         this.doEffect = true;
         this.setChanged();
         if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
         }
      }
   }

   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = super.getUpdateTag(provider);
      tag.putBoolean("DoEffect", this.doEffect);
      return tag;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void tick(Level world, BlockPos pos, BlockState state) {
      if (!world.isClientSide()) {
         boolean previousBurned = this.isBeingBurned;
         boolean currentBurned = this.isBeingBurned();
         this.isBeingBurned = currentBurned;
         if (!previousBurned && currentBurned) {
            world.setBlock(pos, (BlockState)state.setValue(TeaKettleBlock.LIT, true), 3);
            this.heatLevel = Math.max(this.heatLevel, 30);
         } else if (previousBurned && !currentBurned) {
            world.setBlock(pos, (BlockState)state.setValue(TeaKettleBlock.LIT, false), 3);
            this.heatLevel = 0;
         }

         ItemStack heatingItem = this.getItem(7);
         if (this.heatLevel < 70 && !heatingItem.isEmpty() && heatingItem.is(TagsRegistry.HEAT_ITEMS)) {
            this.heatLevel = Math.min(this.heatLevel + 35, 100);
            this.consumeHeatItem();
            this.setChanged();
         }

         this.heatDecreaseCounter++;
         int decreaseInterval = this.isBeingBurned ? 200 : 33;
         if (this.heatDecreaseCounter >= decreaseInterval) {
            this.heatDecreaseCounter = 0;
            if (this.heatLevel > (this.isBeingBurned ? 30 : 0)) {
               this.heatLevel = Math.max(this.heatLevel - 1, this.isBeingBurned ? 30 : 0);
               this.setChanged();
            }
         }

         RecipeManager recipeManager = world.getRecipeManager();
         List<RecipeHolder<TeaKettleRecipe>> recipes = recipeManager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.TEA_KETTLE_RECIPE_TYPE.get());
         Optional<TeaKettleRecipe> recipe = Optional.ofNullable(this.getRecipe(recipes, this.inventory));
         boolean canCraft = recipe.isPresent() && this.canCraft(recipe.get());
         if (canCraft) {
            if (this.requiredDuration <= 0) {
               this.requiredDuration = recipe.get().getRequiredDuration();
               this.cookingTime = 0;
            } else {
               this.cookingTime++;
               if (this.cookingTime >= this.requiredDuration) {
                  this.cookingTime = 0;
                  this.craft(recipe.get());
               }
            }

            world.setBlock(pos, (BlockState)((BlockState)state.setValue(TeaKettleBlock.COOKING, true)).setValue(TeaKettleBlock.LIT, this.isBeingBurned), 3);
         } else {
            this.cookingTime = 0;
            world.setBlock(pos, (BlockState)state.setValue(TeaKettleBlock.COOKING, false), 3);
         }

         if (!this.getItem(6).isEmpty()) {
            ItemStack waterItem = this.getItem(6);
            if (waterItem.is(TagsRegistry.SMALL_WATER_FILL)) {
               this.waterLevel = Math.min(this.waterLevel + 25, 100);
               ItemStack remainderStack = this.getRemainderItem(waterItem);
               waterItem.shrink(1);
               this.setItem(6, remainderStack.isEmpty() ? ItemStack.EMPTY : remainderStack);
               this.setChanged();
            } else if (waterItem.is(TagsRegistry.LARGE_WATER_FILL)) {
               this.waterLevel = Math.min(this.waterLevel + 50, 100);
               ItemStack remainderStack = this.getRemainderItem(waterItem);
               waterItem.shrink(1);
               this.setItem(6, remainderStack.isEmpty() ? ItemStack.EMPTY : remainderStack);
               this.setChanged();
            }
         }

         this.delegate.set(3, this.heatLevel);
         this.delegate.set(4, this.requiredDuration);
      }
   }

   @Override
   public NonNullList<ItemStack> getItems() {
      return this.inventory;
   }

   public int getWaterLevel() {
      return this.waterLevel;
   }

   public int getHeatLevel() {
      return this.heatLevel;
   }

   @Override
   public boolean stillValid(Player player) {
      if (this.level == null) {
         return false;
      } else {
         return this.level.getBlockEntity(this.worldPosition) != this
            ? false
            : player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
      }
   }

   public void dropExperience(ServerLevel world, Vec3 pos) {
      ExperienceOrb.award(world, pos, (int)this.experience);
   }

   @NotNull
   public Component getDisplayName() {
      return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
   }

   @Nullable
   public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
      return new TeaKettleGuiHandler(syncId, inv, this, this.delegate);
   }

   @Override
   public int @NotNull [] getSlotsForFace(Direction side) {
      return side == Direction.DOWN ? new int[]{0, 6} : new int[]{1, 2, 3, 4, 5, 6, 7};
   }

   @Override
   public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
      if (direction == Direction.DOWN) {
         return false;
      } else if (stack.is(TagsRegistry.CONTAINER_ITEMS)) {
         return index == 5;
      } else if (stack.is(TagsRegistry.HEAT_ITEMS)) {
         return index == 7;
      } else {
         return !stack.is(TagsRegistry.SMALL_WATER_FILL) && !stack.is(TagsRegistry.LARGE_WATER_FILL) ? index >= 1 && index <= 4 : index == 6;
      }
   }

   @Override
   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return index == 0 || index == 6;
   }

   private TeaKettleRecipe getRecipe(List<RecipeHolder<TeaKettleRecipe>> recipes, NonNullList<ItemStack> inventory) {
      label34:
      for (RecipeHolder<TeaKettleRecipe> recipeHolder : recipes) {
         TeaKettleRecipe recipe = (TeaKettleRecipe)recipeHolder.value();

         for (Ingredient ingredient : recipe.getIngredients()) {
            boolean ingredientFound = false;

            for (int slotIndex = 1; slotIndex < inventory.size(); slotIndex++) {
               ItemStack slotItem = (ItemStack)inventory.get(slotIndex);
               if (ingredient.test(slotItem)) {
                  ingredientFound = true;
                  break;
               }
            }

            if (!ingredientFound) {
               continue label34;
            }
         }

         return recipe;
      }

      return null;
   }

   private void popExp(float experience) {
      if (this.level instanceof ServerLevel serverLevel) {
         this.createExperience(serverLevel, new Vec3(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ()), 1, experience);
      }
   }

   private void createExperience(ServerLevel arg, Vec3 arg2, int j, float g) {
      int i = Mth.floor(j * g);
      float f = Mth.frac(j * g);
      if (f != 0.0F && Math.random() < f) {
         i++;
      }

      ExperienceOrb.award(arg, arg2, i);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
