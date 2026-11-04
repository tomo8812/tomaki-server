package net.satisfy.farm_and_charm.core.block.entity;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.client.gui.handler.CookingPotGuiHandler;
import net.satisfy.farm_and_charm.core.block.CookingPotBlock;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.farm_and_charm.core.util.StoredExperience;
import net.satisfy.foundation.food.IngredientEffectCarrier;
import net.satisfy.foundation.food.IngredientEffects;
import net.satisfy.foundation.menu.ExperienceSource;
import net.satisfy.foundation.recipe.RecipeUnlockManager;
import net.satisfy.foundation.util.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CookingPotBlockEntity
   extends BlockEntity
   implements BlockEntityTicker<CookingPotBlockEntity>,
   ImplementedInventory,
   MenuProvider,
   ExperienceSource,
   Container {
   private static final int FIRST_INGREDIENT_SLOT = 0;
   private static final int LAST_INGREDIENT_SLOT = 5;
   private static final int CONTAINER_SLOT = 6;
   private static final int OUTPUT_SLOT = 7;
   private static final int MAX_CAPACITY = 8;
   private static final int MAX_COOKING_TIME = 900;
   private final NonNullList<ItemStack> inventory = NonNullList.withSize(8, ItemStack.EMPTY);
   private int cookingTime;
   private final StoredExperience experience = new StoredExperience();
   private boolean isBeingBurned;
   private UUID ownerUuid;
   private final ContainerData delegate = new ContainerData() {
      public int get(int index) {
         return switch (index) {
            case 0 -> CookingPotBlockEntity.this.cookingTime;
            case 1 -> CookingPotBlockEntity.this.isBeingBurned ? 1 : 0;
            default -> 0;
         };
      }

      public void set(int index, int value) {
         switch (index) {
            case 0:
               CookingPotBlockEntity.this.cookingTime = value;
               break;
            case 1:
               CookingPotBlockEntity.this.isBeingBurned = value != 0;
         }
      }

      public int getCount() {
         return 2;
      }
   };

   public CookingPotBlockEntity(BlockPos pos, BlockState state) {
      this((BlockEntityType<?>)EntityTypeRegistry.COOKING_POT_BLOCK_ENTITY.get(), pos, state);
   }

   protected CookingPotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   protected BooleanProperty getLitProperty() {
      return CookingPotBlock.LIT;
   }

   protected BooleanProperty getCookingProperty() {
      return CookingPotBlock.COOKING;
   }

   public static int getMaxCookingTime() {
      return 900;
   }

   public int @NotNull [] getSlotsForFace(Direction side) {
      return switch (side) {
         case UP -> new int[]{0, 1, 2, 3, 4, 5};
         case DOWN -> new int[]{7};
         default -> new int[]{6};
      };
   }

   public void dropExperience(ServerLevel level, Vec3 pos) {
      this.experience.award(level, pos);
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      NonNullList<ItemStack> loaded = NonNullList.withSize(8, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(tag, loaded, provider);

      for (int i = 0; i < 8; i++) {
         this.inventory.set(i, (ItemStack)loaded.get(i));
      }

      this.cookingTime = tag.getInt("CookingTime");
      this.experience.load(tag);
      if (tag.hasUUID("OwnerUUID")) {
         this.ownerUuid = tag.getUUID("OwnerUUID");
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      ContainerHelper.saveAllItems(tag, this.inventory, provider);
      tag.putInt("CookingTime", this.cookingTime);
      this.experience.save(tag);
      if (this.ownerUuid != null) {
         tag.putUUID("OwnerUUID", this.ownerUuid);
      }
   }

   public boolean isBeingBurned() {
      if (this.level == null) {
         throw new IllegalStateException("Null world not allowed");
      }

      BlockState belowState = this.level.getBlockState(this.worldPosition.below());
      return belowState.is(TagRegistry.ALLOWS_COOKING);
   }

   private boolean canCraft(Recipe<?> recipe, RegistryAccess access) {
      if (recipe == null || recipe.getResultItem(access).isEmpty()) {
         return false;
      }

      if (recipe instanceof CookingPotRecipe cookingRecipe) {
         if (cookingRecipe.isContainerRequired()) {
            ItemStack containerSlotStack = this.getItem(6);
            if (!containerSlotStack.is(cookingRecipe.getContainerItem().getItem())) {
               return false;
            }
         }

         ItemStack outputSlotStack = this.getItem(7);
         ItemStack expected = this.generateOutputItem(recipe, access);
         return outputSlotStack.isEmpty()
            || ItemStack.isSameItemSameComponents(outputSlotStack, expected) && outputSlotStack.getCount() < outputSlotStack.getMaxStackSize();
      } else {
         return false;
      }
   }

   protected int getExtraOutputCount(ItemStack output) {
      return 0;
   }

   @Nullable
   protected Player getOwner() {
      return this.ownerUuid != null && this.level != null && this.level.getServer() != null
         ? this.level.getServer().getPlayerList().getPlayer(this.ownerUuid)
         : null;
   }

   private void craft(Recipe<?> recipe, RegistryAccess access) {
      if (this.canCraft(recipe, access)) {
         ItemStack recipeOutput = this.generateOutputItem(recipe, access);
         ItemStack outputSlotStack = this.getItem(7);
         int room = recipeOutput.getMaxStackSize() - outputSlotStack.getCount() - recipeOutput.getCount();
         recipeOutput.grow(Math.max(0, Math.min(this.getExtraOutputCount(recipeOutput), room)));
         if (outputSlotStack.isEmpty()) {
            this.setItem(7, recipeOutput);
         } else {
            outputSlotStack.grow(recipeOutput.getCount());
         }

         if (recipe instanceof CookingPotRecipe cookingPotRecipe) {
            this.experience.add(cookingPotRecipe.getExperience());
         }

         recipe.getIngredients()
            .forEach(
               ingredient -> {
                  for (int slot = 0; slot <= 5; slot++) {
                     ItemStack stack = this.getItem(slot);
                     if (!stack.isEmpty() && ingredient.test(stack)) {
                        ItemStack remainderStack = stack.getItem().hasCraftingRemainingItem()
                           ? new ItemStack(Objects.requireNonNull(stack.getItem().getCraftingRemainingItem()))
                           : ItemStack.EMPTY;
                        stack.shrink(1);
                        if (!remainderStack.isEmpty()) {
                           if (stack.isEmpty()) {
                              this.setItem(slot, remainderStack);
                           } else {
                              boolean insertedx = this.tryInsertRemainder(remainderStack);
                              if (!insertedx && this.level != null) {
                                 Block.popResource(this.level, this.worldPosition, remainderStack);
                              }
                           }
                        }
                        break;
                     }
                  }
               }
            );
         if (recipe instanceof CookingPotRecipe cookingRecipe && cookingRecipe.isContainerRequired()) {
            ItemStack containerSlotStack = this.getItem(6);
            if (!containerSlotStack.isEmpty()) {
               ItemStack containerRemainder = containerSlotStack.getItem().hasCraftingRemainingItem()
                  ? new ItemStack(Objects.requireNonNull(containerSlotStack.getItem().getCraftingRemainingItem()))
                  : ItemStack.EMPTY;
               containerSlotStack.shrink(1);
               if (!containerRemainder.isEmpty()) {
                  if (containerSlotStack.isEmpty()) {
                     this.setItem(6, containerRemainder);
                  } else {
                     boolean inserted = this.tryInsertRemainder(containerRemainder);
                     if (!inserted && this.level != null) {
                        Block.popResource(this.level, this.worldPosition, containerRemainder);
                     }
                  }
               }
            }
         }

         this.setChanged();
      }
   }

   private boolean tryInsertRemainder(ItemStack remainderStack) {
      if (remainderStack.isEmpty()) {
         return true;
      }

      for (int slot = 0; slot <= 5; slot++) {
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

      ItemStack containerSlotStack = this.getItem(6);
      if (containerSlotStack.isEmpty()) {
         this.setItem(6, remainderStack);
         return true;
      }

      if (ItemStack.isSameItemSameComponents(containerSlotStack, remainderStack) && containerSlotStack.getCount() < containerSlotStack.getMaxStackSize()) {
         int transferableAmount = Math.min(remainderStack.getCount(), containerSlotStack.getMaxStackSize() - containerSlotStack.getCount());
         if (transferableAmount > 0) {
            containerSlotStack.grow(transferableAmount);
            remainderStack.shrink(transferableAmount);
            this.setChanged();
            return remainderStack.isEmpty();
         }
      }

      return remainderStack.isEmpty();
   }

   private ItemStack generateOutputItem(Recipe<?> recipe, RegistryAccess access) {
      ItemStack outputStack = recipe.getResultItem(access).copy();
      if (outputStack.getItem() instanceof IngredientEffectCarrier) {
         for (MobEffectInstance inst : IngredientEffects.collectMergedSortedEffects(this, 0, 5)) {
            IngredientEffects.addEffect(outputStack, new Pair(inst, 1.0F));
         }
      }

      return outputStack;
   }

   public boolean hasOutputItem() {
      return !this.getItem(7).isEmpty();
   }

   public void tick(Level world, BlockPos pos, BlockState state, CookingPotBlockEntity blockEntity) {
      if (!world.isClientSide()) {
         boolean wasBeingBurned = this.isBeingBurned;
         this.isBeingBurned = this.isBeingBurned();
         if (wasBeingBurned != this.isBeingBurned || (Boolean)state.getValue(this.getLitProperty()) != this.isBeingBurned) {
            world.setBlock(pos, (BlockState)state.setValue(this.getLitProperty(), this.isBeingBurned), 3);
         }

         if (this.isBeingBurned) {
            RecipeManager recipeManager = world.getRecipeManager();
            List<RecipeHolder<CookingPotRecipe>> recipes = recipeManager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get());
            Optional<CookingPotRecipe> recipe = Optional.ofNullable(this.getRecipe(recipes, this.inventory));
            if (recipe.isPresent() && recipe.get() instanceof CookingPotRecipe cookingRecipe && cookingRecipe.requiresLearning()) {
               ServerPlayer owner = Objects.requireNonNull(world.getServer()).getPlayerList().getPlayer(this.ownerUuid);
               if (owner == null || RecipeUnlockManager.isRecipeLocked(owner, BuiltInRegistries.RECIPE_TYPE.getKey(recipe.get().getType()))) {
                  this.cookingTime = 0;
                  if ((Boolean)state.getValue(this.getCookingProperty())) {
                     world.setBlock(pos, (BlockState)state.setValue(this.getCookingProperty(), false), 3);
                  }

                  return;
               }
            }

            if (this.level == null) {
               throw new IllegalStateException("Null world not allowed");
            }

            RegistryAccess access = this.level.registryAccess();
            if (recipe.isPresent() && this.canCraft(recipe.get(), access)) {
               if (++this.cookingTime >= 900) {
                  this.cookingTime = 0;
                  this.craft(recipe.get(), access);
               }

               if (!(Boolean)state.getValue(this.getCookingProperty())) {
                  world.setBlock(pos, (BlockState)state.setValue(this.getCookingProperty(), true), 3);
               }
            } else {
               this.cookingTime = 0;
               if ((Boolean)state.getValue(this.getCookingProperty())) {
                  world.setBlock(pos, (BlockState)state.setValue(this.getCookingProperty(), false), 3);
               }
            }
         }
      }
   }

   public NonNullList<ItemStack> getItems() {
      return this.inventory;
   }

   public void setItem(int slot, ItemStack stack) {
      this.inventory.set(slot, stack);
      this.setChanged();
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this
         ? player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0
         : false;
   }

   @NotNull
   public Component getDisplayName() {
      return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
   }

   private CookingPotRecipe getRecipe(List<RecipeHolder<CookingPotRecipe>> recipes, NonNullList<ItemStack> inventory) {
      for (RecipeHolder<CookingPotRecipe> recipeHolder : recipes) {
         CookingPotRecipe recipe = (CookingPotRecipe)recipeHolder.value();
         if (this.matchesInventory(recipe, inventory)) {
            return recipe;
         }
      }

      return null;
   }

   private boolean matchesInventory(CookingPotRecipe recipe, NonNullList<ItemStack> inventory) {
      List<Ingredient> ingredients = recipe.getIngredients();
      NonNullList<ItemStack> invCopy = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);

      for (int i = 0; i <= 5; i++) {
         invCopy.set(i, ((ItemStack)inventory.get(i)).copy());
      }

      for (Ingredient ingredient : ingredients) {
         boolean matched = false;

         for (int i = 0; i <= 5; i++) {
            ItemStack stack = (ItemStack)invCopy.get(i);
            if (!stack.isEmpty() && ingredient.test(stack)) {
               stack.shrink(1);
               matched = true;
               break;
            }
         }

         if (!matched) {
            return false;
         }
      }

      label41:
      for (int i = 0; i <= 5; i++) {
         ItemStack remaining = (ItemStack)invCopy.get(i);
         if (!remaining.isEmpty()) {
            for (Ingredient ingredient : ingredients) {
               if (ingredient.test(remaining)) {
                  continue label41;
               }
            }

            return false;
         }
      }

      return true;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
      this.ownerUuid = player.getUUID();
      return new CookingPotGuiHandler(syncId, inv, this, this.delegate);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void tick(Level param1, BlockPos nullx, BlockState nullxx, BlockEntity nullxxx) {
      this.tick(var1, nullx, nullxx, (CookingPotBlockEntity)nullxxx);
   }
}
