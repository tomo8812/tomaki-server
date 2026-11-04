package net.satisfy.farm_and_charm.core.block.entity;

import com.mojang.datafixers.util.Pair;
import dev.architectury.registry.fuel.FuelRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.client.gui.handler.StoveGuiHandler;
import net.satisfy.farm_and_charm.core.block.StoveBlock;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.StoredExperience;
import net.satisfy.foundation.food.IngredientEffectCarrier;
import net.satisfy.foundation.food.IngredientEffects;
import net.satisfy.foundation.food.PlaceableIngredientEffectFoodItem;
import net.satisfy.foundation.menu.ExperienceSource;
import net.satisfy.foundation.recipe.RecipeUnlockManager;
import net.satisfy.foundation.util.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoveBlockEntity extends BlockEntity implements BlockEntityTicker<StoveBlockEntity>, ImplementedInventory, MenuProvider, ExperienceSource {
   public static final int TOTAL_COOKING_TIME = 240;
   public static final int GRILL_SLOTS = 4;
   protected static final int[] INGREDIENT_SLOTS = new int[]{1, 2, 3};
   protected int burnTime;
   protected int burnTimeTotal;
   protected int cookTime;
   protected int cookTimeTotal;
   private UUID ownerUuid;
   private boolean manuallyExtinguished;
   private final ContainerData propertyDelegate = new ContainerData() {
      public int get(int index) {
         return switch (index) {
            case 0 -> StoveBlockEntity.this.burnTime;
            case 1 -> StoveBlockEntity.this.burnTimeTotal;
            case 2 -> StoveBlockEntity.this.cookTime;
            case 3 -> StoveBlockEntity.this.cookTimeTotal;
            default -> 0;
         };
      }

      public void set(int index, int value) {
         switch (index) {
            case 0:
               StoveBlockEntity.this.burnTime = value;
               break;
            case 1:
               StoveBlockEntity.this.burnTimeTotal = value;
               break;
            case 2:
               StoveBlockEntity.this.cookTime = value;
               break;
            case 3:
               StoveBlockEntity.this.cookTimeTotal = value;
         }
      }

      public int getCount() {
         return 4;
      }
   };
   private final StoredExperience experience = new StoredExperience();
   private NonNullList<ItemStack> inventory;
   private final NonNullList<ItemStack> grillItems = NonNullList.withSize(4, ItemStack.EMPTY);
   private final int[] grillProgress = new int[4];
   private final int[] grillTotal = new int[4];
   private static final int FLIP_COOLDOWN_TICKS = 40;
   private static final int SIZZLE_CHANCE = 100;
   private final long[] grillFlipStart = new long[4];
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !StoveBlockEntity.class.desiredAssertionStatus();

   public StoveBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.STOVE_BLOCK_ENTITY.get(), pos, state);
      this.inventory = NonNullList.withSize(5, ItemStack.EMPTY);
   }

   public int[] getIngredientSlots() {
      return INGREDIENT_SLOTS;
   }

   public int getOutputSlot() {
      return 0;
   }

   public void dropExperience(ServerLevel world, Vec3 pos) {
      this.experience.award(world, pos);
   }

   public int @NotNull [] getSlotsForFace(Direction side) {
      if (side.equals(Direction.UP)) {
         return INGREDIENT_SLOTS;
      } else {
         return side.equals(Direction.DOWN) ? new int[]{0} : new int[]{4};
      }
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      this.inventory = NonNullList.withSize(5, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compoundTag, this.inventory, provider);
      this.burnTime = compoundTag.getShort("BurnTime");
      this.cookTime = compoundTag.getShort("CookTime");
      this.cookTimeTotal = compoundTag.getShort("CookTimeTotal");
      this.burnTimeTotal = this.getTotalBurnTime(this.getItem(4));
      this.experience.load(compoundTag);
      this.manuallyExtinguished = compoundTag.getBoolean("ManuallyExtinguished");
      if (compoundTag.hasUUID("Owner")) {
         this.ownerUuid = compoundTag.getUUID("Owner");
      }

      CompoundTag grill = compoundTag.getCompound("Grill");

      for (int slot = 0; slot < 4; slot++) {
         this.grillItems.set(slot, ItemStack.EMPTY);
      }

      ContainerHelper.loadAllItems(grill, this.grillItems, provider);
      int[] progress = grill.getIntArray("Progress");
      int[] total = grill.getIntArray("Total");
      long[] flip = grill.getLongArray("Flip");

      for (int slot = 0; slot < 4; slot++) {
         this.grillProgress[slot] = slot < progress.length ? progress[slot] : 0;
         this.grillTotal[slot] = slot < total.length ? total[slot] : 0;
         this.grillFlipStart[slot] = slot < flip.length ? flip[slot] : 0L;
      }
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      compoundTag.putShort("BurnTime", (short)this.burnTime);
      compoundTag.putShort("CookTime", (short)this.cookTime);
      compoundTag.putShort("CookTimeTotal", (short)this.cookTimeTotal);
      this.experience.save(compoundTag);
      CompoundTag grill = new CompoundTag();
      ContainerHelper.saveAllItems(grill, this.grillItems, true, provider);
      grill.putIntArray("Progress", this.grillProgress);
      grill.putIntArray("Total", this.grillTotal);
      grill.putLongArray("Flip", this.grillFlipStart);
      compoundTag.put("Grill", grill);
      compoundTag.putBoolean("ManuallyExtinguished", this.manuallyExtinguished);
      if (this.ownerUuid != null) {
         compoundTag.putUUID("Owner", this.ownerUuid);
      }

      ContainerHelper.saveAllItems(compoundTag, this.inventory, provider);
   }

   protected boolean isBurning() {
      return this.burnTime > 0;
   }

   public boolean canIgnite() {
      return !this.isBurning() && this.getTotalBurnTime(this.getItem(4)) > 0;
   }

   public boolean ignite() {
      if (!this.canIgnite()) {
         return false;
      }

      this.burnTime = this.getTotalBurnTime(this.getItem(4));
      this.burnTimeTotal = this.burnTime;
      this.manuallyExtinguished = false;
      ItemStack fuelStack = this.getItem(4);
      if (fuelStack.getItem().hasCraftingRemainingItem()) {
         this.setItem(4, new ItemStack(Objects.requireNonNull(fuelStack.getItem().getCraftingRemainingItem())));
      } else if (fuelStack.getCount() > 1) {
         this.removeItem(4, 1);
      } else if (fuelStack.getCount() == 1) {
         this.setItem(4, ItemStack.EMPTY);
      }

      if (this.level != null) {
         BlockState blockState = this.getBlockState();
         if (!(Boolean)blockState.getValue(StoveBlock.LIT)) {
            this.level.setBlock(this.worldPosition, (BlockState)blockState.setValue(StoveBlock.LIT, true), 3);
         }
      }

      this.setChanged();
      return true;
   }

   public boolean canExtinguish() {
      return this.isBurning();
   }

   public boolean extinguish() {
      if (!this.canExtinguish()) {
         return false;
      }

      this.burnTime = 0;
      this.burnTimeTotal = 0;
      this.manuallyExtinguished = true;
      if (this.level != null) {
         BlockState blockState = this.getBlockState();
         if ((Boolean)blockState.getValue(StoveBlock.LIT)) {
            this.level.setBlock(this.worldPosition, (BlockState)blockState.setValue(StoveBlock.LIT, false), 3);
         }
      }

      this.setChanged();
      return true;
   }

   public boolean isGrillFree() {
      return this.level != null && this.level.getBlockState(this.worldPosition.above()).isAir();
   }

   public static int grillSlotAt(Vec3 location, BlockPos pos) {
      double x = location.x - pos.getX();
      double z = location.z - pos.getZ();
      return (x >= 0.5 ? 1 : 0) + (z >= 0.5 ? 2 : 0);
   }

   public static Vec3 grillSlotCenter(int slot) {
      return new Vec3(slot % 2 == 0 ? 0.28 : 0.72, 1.0, slot < 2 ? 0.28 : 0.72);
   }

   public ItemStack getGrillItem(int slot) {
      return (ItemStack)this.grillItems.get(slot);
   }

   public int getGrillProgress(int slot) {
      return this.grillProgress[slot];
   }

   public int getGrillTotal(int slot) {
      return this.grillTotal[slot];
   }

   public boolean isGrillDone(int slot) {
      return !((ItemStack)this.grillItems.get(slot)).isEmpty() && this.grillTotal[slot] <= 0;
   }

   public long getGrillFlipStart(int slot) {
      return this.grillFlipStart[slot];
   }

   public boolean hasGrillItems() {
      for (ItemStack stack : this.grillItems) {
         if (!stack.isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public boolean isLit() {
      return (Boolean)this.getBlockState().getValue(StoveBlock.LIT);
   }

   public static Optional<RecipeHolder<CampfireCookingRecipe>> findGrillRecipe(Level level, ItemStack stack) {
      return stack.isEmpty() ? Optional.empty() : level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SingleRecipeInput(stack), level);
   }

   public boolean placeOnGrill(int slot, ItemStack stack) {
      if (this.level != null && this.isGrillFree() && ((ItemStack)this.grillItems.get(slot)).isEmpty()) {
         Optional<RecipeHolder<CampfireCookingRecipe>> recipe = findGrillRecipe(this.level, stack);
         if (recipe.isEmpty()) {
            return false;
         }

         this.grillItems.set(slot, stack.copyWithCount(1));
         this.grillProgress[slot] = 0;
         this.grillTotal[slot] = ((CampfireCookingRecipe)recipe.get().value()).getCookingTime();
         this.grillFlipStart[slot] = 0L;
         this.setChanged();
         return true;
      } else {
         return false;
      }
   }

   public ItemStack takeFromGrill(int slot) {
      ItemStack stack = (ItemStack)this.grillItems.get(slot);
      this.grillItems.set(slot, ItemStack.EMPTY);
      this.grillProgress[slot] = 0;
      this.grillTotal[slot] = 0;
      this.setChanged();
      return stack;
   }

   public boolean flipGrillItem(int slot) {
      if (this.level != null && !((ItemStack)this.grillItems.get(slot)).isEmpty() && !this.isGrillDone(slot)) {
         long time = this.level.getGameTime();
         if (this.grillFlipStart[slot] != 0L && time - this.grillFlipStart[slot] < 40L) {
            return false;
         }

         this.grillFlipStart[slot] = time;
         this.setChanged();
         return true;
      } else {
         return false;
      }
   }

   private void popGrillItem(ServerLevel level, int slot, ItemStack stack) {
      Vec3 center = grillSlotCenter(slot);
      ItemEntity item = new ItemEntity(
         level, this.worldPosition.getX() + center.x, this.worldPosition.getY() + 1.05, this.worldPosition.getZ() + center.z, stack
      );
      item.setDeltaMovement(level.random.triangle(0.0, 0.06), 0.25, level.random.triangle(0.0, 0.06));
      level.addFreshEntity(item);
   }

   public void dropGrillItems(ServerLevel level) {
      for (int slot = 0; slot < 4; slot++) {
         if (!((ItemStack)this.grillItems.get(slot)).isEmpty()) {
            this.popGrillItem(level, slot, this.takeFromGrill(slot));
         }
      }
   }

   private void tickGrill(ServerLevel level) {
      if (this.hasGrillItems()) {
         if (!this.isGrillFree()) {
            this.dropGrillItems(level);
         } else if (this.isBurning()) {
            boolean cooking = false;

            for (int slot = 0; slot < 4; slot++) {
               ItemStack stack = (ItemStack)this.grillItems.get(slot);
               if (!stack.isEmpty() && !this.isGrillDone(slot)) {
                  cooking = true;
                  if (++this.grillProgress[slot] >= this.grillTotal[slot]) {
                     Optional<RecipeHolder<CampfireCookingRecipe>> recipe = findGrillRecipe(level, stack);
                     ItemStack result = recipe.<ItemStack>map(
                           holder -> ((CampfireCookingRecipe)holder.value()).assemble(new SingleRecipeInput(stack), level.registryAccess())
                        )
                        .orElse(stack);
                     this.grillItems.set(slot, result);
                     this.grillProgress[slot] = 0;
                     this.grillTotal[slot] = 0;
                     this.setChanged();
                     level.playSound(null, this.worldPosition, SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 0.6F, 1.4F + level.random.nextFloat() * 0.2F);
                     if (recipe.isPresent()) {
                        this.experience.add(((CampfireCookingRecipe)recipe.get().value()).getExperience());
                        this.experience.award(level, Vec3.atCenterOf(this.worldPosition).add(0.0, 0.6, 0.0));
                     }
                  }
               }
            }

            if (cooking && level.random.nextInt(100) == 0) {
               level.playSound(null, this.worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.15F, 1.8F + level.random.nextFloat() * 0.3F);
            }

            if (level.getGameTime() % 20L == 0L) {
               this.setChanged();
            }
         }
      }
   }

   public void tick(Level world, BlockPos pos, BlockState state, StoveBlockEntity blockEntity) {
      if (!world.isClientSide) {
         boolean initialBurningState = blockEntity.isBurning();
         boolean dirty = false;
         if (initialBurningState) {
            this.burnTime--;
         }

         assert this.level != null;
         this.tickGrill((ServerLevel)world);
         RecipeManager recipeManager = world.getRecipeManager();
         List<RecipeHolder<StoveRecipe>> recipes = recipeManager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.STOVE_RECIPE_TYPE.get());
         Optional<StoveRecipe> recipe = Optional.ofNullable(this.getRecipe(recipes, this.inventory));
         RegistryAccess access = this.level.registryAccess();
         if (recipe.isPresent() && recipe.get().requiresLearning()) {
            ServerPlayer owner = Objects.requireNonNull(world.getServer()).getPlayerList().getPlayer(this.ownerUuid);
            if (owner == null || RecipeUnlockManager.isRecipeLocked(owner, BuiltInRegistries.RECIPE_TYPE.getKey(recipe.get().getType()))) {
               this.cookTime = 0;
               if (!this.isBurning() && (Boolean)state.getValue(StoveBlock.LIT)) {
                  world.setBlock(pos, (BlockState)state.setValue(StoveBlock.LIT, false), 3);
                  this.setChanged();
               }

               return;
            }
         }

         if (!this.isBurning() && !this.manuallyExtinguished) {
            boolean shouldKeepBurning = (Boolean)state.getValue(StoveBlock.LIT) && this.getTotalBurnTime(this.getItem(4)) > 0;
            boolean shouldStartCooking = (recipe.isPresent() && this.canCraft(recipe.get(), access) || this.hasGrillItems() && this.isGrillFree())
               && this.getTotalBurnTime(this.getItem(4)) > 0;
            if (shouldKeepBurning || shouldStartCooking) {
               this.burnTime = this.burnTimeTotal = this.getTotalBurnTime(this.getItem(4));
               if (this.burnTime > 0) {
                  dirty = true;
                  ItemStack fuelStack = this.getItem(4);
                  if (fuelStack.getItem().hasCraftingRemainingItem()) {
                     this.setItem(4, new ItemStack(Objects.requireNonNull(fuelStack.getItem().getCraftingRemainingItem())));
                  } else if (fuelStack.getCount() > 1) {
                     this.removeItem(4, 1);
                  } else if (fuelStack.getCount() == 1) {
                     this.setItem(4, ItemStack.EMPTY);
                  }
               }
            }
         }

         if (this.isBurning() && recipe.isPresent() && this.canCraft(recipe.get(), access)) {
            this.cookTime++;
            if (this.cookTime == this.cookTimeTotal) {
               this.cookTime = 0;
               this.craft(recipe.get(), access);
               dirty = true;
            }
         } else if (recipe.isPresent() && !this.canCraft(recipe.get(), access)) {
            this.cookTime = 0;
         }

         if ((Boolean)state.getValue(StoveBlock.LIT) != this.isBurning()) {
            world.setBlock(pos, (BlockState)state.setValue(StoveBlock.LIT, this.isBurning()), 3);
            dirty = true;
         }

         if (dirty) {
            this.setChanged();
         }
      }
   }

   protected boolean canCraft(StoveRecipe recipe, RegistryAccess access) {
      if (recipe == null || recipe.getResultItem(access).isEmpty()) {
         return false;
      } else if (!this.matchesInventory(recipe, this.inventory)) {
         return false;
      } else {
         ItemStack expected = this.generateOutputItem(recipe, access);
         ItemStack output = this.getItem(0);
         if (output.isEmpty()) {
            return true;
         } else {
            return !ItemStack.isSameItemSameComponents(output, expected) ? false : output.getCount() + expected.getCount() <= output.getMaxStackSize();
         }
      }
   }

   private StoveRecipe getRecipe(List<RecipeHolder<StoveRecipe>> recipes, NonNullList<ItemStack> inventory) {
      for (RecipeHolder<StoveRecipe> holder : recipes) {
         StoveRecipe recipe = (StoveRecipe)holder.value();
         if (this.matchesInventory(recipe, inventory)) {
            return recipe;
         }
      }

      return null;
   }

   private boolean matchesInventory(StoveRecipe recipe, NonNullList<ItemStack> inventory) {
      NonNullList<ItemStack> inventoryCopy = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);

      for (int slot : INGREDIENT_SLOTS) {
         inventoryCopy.set(slot, ((ItemStack)inventory.get(slot)).copy());
      }

      List<Integer> plannedSlots = this.getPlannedIngredientSlots(recipe, inventoryCopy);
      if (plannedSlots == null) {
         return false;
      }

      int occupiedIngredientSlots = 0;

      for (int slot : INGREDIENT_SLOTS) {
         if (!((ItemStack)inventory.get(slot)).isEmpty()) {
            occupiedIngredientSlots++;
         }
      }

      return occupiedIngredientSlots == recipe.getIngredients().size();
   }

   protected void craft(StoveRecipe recipe, RegistryAccess access) {
      if (recipe != null && this.canCraft(recipe, access)) {
         List<Integer> plannedSlots = this.getPlannedIngredientSlots(recipe, this.inventory);
         if (plannedSlots != null) {
            ItemStack recipeOutput = this.generateOutputItem(recipe, access);
            ItemStack outputSlotStack = this.getItem(0);
            this.experience.add(recipe.getExperience());
            if (outputSlotStack.isEmpty()) {
               this.setItem(0, recipeOutput);
            } else if (ItemStack.isSameItemSameComponents(outputSlotStack, recipeOutput)) {
               outputSlotStack.grow(recipeOutput.getCount());
            }

            List<Ingredient> ingredients = recipe.getIngredients();

            for (int ingredientIndex = 0; ingredientIndex < ingredients.size(); ingredientIndex++) {
               int slot = plannedSlots.get(ingredientIndex);
               ItemStack stackInSlot = this.getItem(slot);
               if (!stackInSlot.isEmpty()) {
                  ItemStack remainderStack = this.getRemainderItem(stackInSlot);
                  stackInSlot.shrink(1);
                  if (!remainderStack.isEmpty()) {
                     if (stackInSlot.isEmpty()) {
                        this.setItem(slot, remainderStack);
                     } else if (!this.tryInsertRemainder(remainderStack) && this.level != null) {
                        Block.popResource(this.level, this.worldPosition, remainderStack);
                     }
                  }
               }
            }

            this.setChanged();
         }
      }
   }

   private List<Integer> getPlannedIngredientSlots(StoveRecipe recipe, NonNullList<ItemStack> inventory) {
      NonNullList<ItemStack> inventoryCopy = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);

      for (int slot : INGREDIENT_SLOTS) {
         inventoryCopy.set(slot, ((ItemStack)inventory.get(slot)).copy());
      }

      List<Integer> plannedSlots = new ArrayList<>();
      List<Ingredient> ingredients = recipe.getIngredients();
      List<Integer> ingredientOrder = new ArrayList<>();

      for (int ingredientIndex = 0; ingredientIndex < ingredients.size(); ingredientIndex++) {
         ingredientOrder.add(ingredientIndex);
      }

      ingredientOrder.sort((leftIndex, rightIndex) -> {
         int leftMatches = this.countMatchingSlots(ingredients.get(leftIndex), inventoryCopy);
         int rightMatches = this.countMatchingSlots(ingredients.get(rightIndex), inventoryCopy);
         return Integer.compare(leftMatches, rightMatches);
      });
      Integer[] slotAssignments = new Integer[ingredients.size()];

      for (int orderedIndex = 0; orderedIndex < ingredientOrder.size(); orderedIndex++) {
         int ingredientIndex = ingredientOrder.get(orderedIndex);
         Ingredient ingredient = ingredients.get(ingredientIndex);
         int selectedSlot = this.findBestMatchingSlot(ingredient, inventoryCopy);
         if (selectedSlot == -1) {
            return null;
         }

         ((ItemStack)inventoryCopy.get(selectedSlot)).shrink(1);
         slotAssignments[ingredientIndex] = selectedSlot;
      }

      for (Integer slotAssignment : slotAssignments) {
         if (slotAssignment == null) {
            return null;
         }

         plannedSlots.add(slotAssignment);
      }

      return plannedSlots;
   }

   private int findBestMatchingSlot(Ingredient ingredient, NonNullList<ItemStack> inventory) {
      int selectedSlot = -1;
      int smallestStackCount = Integer.MAX_VALUE;

      for (int slot : INGREDIENT_SLOTS) {
         ItemStack stack = (ItemStack)inventory.get(slot);
         if (!stack.isEmpty() && ingredient.test(stack) && stack.getCount() < smallestStackCount) {
            smallestStackCount = stack.getCount();
            selectedSlot = slot;
         }
      }

      return selectedSlot;
   }

   private int countMatchingSlots(Ingredient ingredient, NonNullList<ItemStack> inventory) {
      int matchingSlots = 0;

      for (int slot : INGREDIENT_SLOTS) {
         ItemStack stack = (ItemStack)inventory.get(slot);
         if (!stack.isEmpty() && ingredient.test(stack)) {
            matchingSlots++;
         }
      }

      return matchingSlots;
   }

   private boolean tryInsertRemainder(ItemStack remainderStack) {
      if (remainderStack.isEmpty()) {
         return true;
      }

      for (int slot : INGREDIENT_SLOTS) {
         ItemStack existingStack = this.getItem(slot);
         if (existingStack.isEmpty()) {
            this.setItem(slot, remainderStack);
            return true;
         }

         if (ItemStack.isSameItemSameComponents(existingStack, remainderStack) && existingStack.getCount() < existingStack.getMaxStackSize()) {
            int transferableAmount = Math.min(remainderStack.getCount(), existingStack.getMaxStackSize() - existingStack.getCount());
            if (transferableAmount > 0) {
               existingStack.grow(transferableAmount);
               remainderStack.shrink(transferableAmount);
               if (remainderStack.isEmpty()) {
                  this.setChanged();
                  return true;
               }
            }
         }
      }

      ItemStack fuelSlotStack = this.getItem(4);
      if (fuelSlotStack.isEmpty()) {
         this.setItem(4, remainderStack);
         return true;
      }

      if (ItemStack.isSameItemSameComponents(fuelSlotStack, remainderStack) && fuelSlotStack.getCount() < fuelSlotStack.getMaxStackSize()) {
         int transferableAmount = Math.min(remainderStack.getCount(), fuelSlotStack.getMaxStackSize() - fuelSlotStack.getCount());
         if (transferableAmount > 0) {
            fuelSlotStack.grow(transferableAmount);
            remainderStack.shrink(transferableAmount);
            this.setChanged();
            return remainderStack.isEmpty();
         }
      }

      return remainderStack.isEmpty();
   }

   private ItemStack generateOutputItem(StoveRecipe recipe, RegistryAccess access) {
      ItemStack outputStack = recipe.getResultItem(access).copy();
      if (outputStack.getItem() instanceof IngredientEffectCarrier || outputStack.getItem() instanceof PlaceableIngredientEffectFoodItem) {
         List<ItemStack> stacks = new ArrayList<>();

         for (int slot : INGREDIENT_SLOTS) {
            ItemStack stack = this.getItem(slot);
            if (!stack.isEmpty()) {
               stacks.add(stack);
            }
         }

         for (MobEffectInstance instance : IngredientEffects.collectMergedSortedEffects(stacks)) {
            IngredientEffects.addEffect(outputStack, new Pair(instance, 1.0F));
         }
      }

      return outputStack;
   }

   protected int getTotalBurnTime(ItemStack fuel) {
      return fuel.isEmpty() ? 0 : FuelRegistry.get(fuel);
   }

   private ItemStack getRemainderItem(ItemStack stack) {
      return stack.getItem().hasCraftingRemainingItem() ? new ItemStack(Objects.requireNonNull(stack.getItem().getCraftingRemainingItem())) : ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getItems() {
      return this.inventory;
   }

   public void setItem(int slot, ItemStack stack) {
      ItemStack stackInSlot = (ItemStack)this.inventory.get(slot);
      boolean dirty = !stack.isEmpty() && ItemStack.isSameItem(stack, stackInSlot) && ItemStack.matches(stack, stackInSlot);
      this.inventory.set(slot, stack);
      if (stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }

      boolean isIngredientSlot = false;

      for (int ingredientSlot : INGREDIENT_SLOTS) {
         if (slot == ingredientSlot) {
            isIngredientSlot = true;
            break;
         }
      }

      if (slot == 4 && !dirty) {
         this.manuallyExtinguished = false;
      }

      if (isIngredientSlot && !dirty) {
         this.cookTimeTotal = 240;
         this.cookTime = 0;
         this.setChanged();
      }
   }

   public boolean stillValid(Player player) {
      assert this.level != null;
      return this.level.getBlockEntity(this.worldPosition) != this
         ? false
         : player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
   }

   @NotNull
   public Component getDisplayName() {
      return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
   }

   @Nullable
   public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
      return new StoveGuiHandler(syncId, inv, this, this.propertyDelegate);
   }

   public void setChanged() {
      super.setChanged();
      if (this.level != null) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
      }
   }

   public void setOwner(UUID uuid) {
      this.ownerUuid = uuid;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag compoundTag = new CompoundTag();
      this.saveAdditional(compoundTag, provider);
      return compoundTag;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void tick(Level param1, BlockPos nullx, BlockState nullxx, BlockEntity nullxxx) {
      this.tick(var1, nullx, nullxx, (StoveBlockEntity)nullxxx);
   }
}
