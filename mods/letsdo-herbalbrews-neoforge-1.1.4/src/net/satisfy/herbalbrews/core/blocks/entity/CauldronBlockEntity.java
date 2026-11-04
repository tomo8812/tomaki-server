package net.satisfy.herbalbrews.core.blocks.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.herbalbrews.client.gui.handler.CauldronGuiHandler;
import net.satisfy.herbalbrews.core.registry.EntityTypeRegistry;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;
import net.satisfy.herbalbrews.core.world.ImplementedInventory;
import net.satisfy.herbalbrews.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CauldronBlockEntity extends BlockEntity implements ImplementedInventory, BlockEntityTicker<CauldronBlockEntity>, MenuProvider {
   public static final int CAPACITY = 5;
   private static final int[] SLOTS_FOR_SIDE = new int[]{0, 4};
   private static final int[] SLOTS_FOR_UP = new int[]{1, 2};
   private static final int[] SLOTS_FOR_DOWN = new int[]{3};
   private NonNullList<ItemStack> inventory;
   private int brewingTime = 0;
   private int totalBrewingTime;
   private final ContainerData propertyDelegate = new ContainerData() {
      public int get(int index) {
         return switch (index) {
            case 0 -> CauldronBlockEntity.this.brewingTime;
            case 1 -> CauldronBlockEntity.this.totalBrewingTime;
            default -> 0;
         };
      }

      public void set(int index, int value) {
         switch (index) {
            case 0:
               CauldronBlockEntity.this.brewingTime = value;
               break;
            case 1:
               CauldronBlockEntity.this.totalBrewingTime = value;
         }
      }

      public int getCount() {
         return 2;
      }
   };
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !CauldronBlockEntity.class.desiredAssertionStatus();

   public CauldronBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.CAULDRON_BLOCK_ENTITY.get(), pos, state);
      this.inventory = NonNullList.withSize(5, ItemStack.EMPTY);
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compoundTag, this.inventory, provider);
      this.brewingTime = compoundTag.getInt("BrewingTime");
      this.totalBrewingTime = compoundTag.getInt("TotalBrewingTime");
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      ContainerHelper.saveAllItems(compoundTag, this.inventory, provider);
      compoundTag.putInt("BrewingTime", this.brewingTime);
      compoundTag.putInt("TotalBrewingTime", this.totalBrewingTime);
   }

   public void tick(Level world, BlockPos pos, BlockState state, CauldronBlockEntity blockEntity) {
      if (!world.isClientSide) {
         boolean dirty = false;
         if (this.canCraft()) {
            this.brewingTime++;
            if (this.brewingTime >= this.totalBrewingTime) {
               this.brewingTime = 0;
               this.craft(world);
               dirty = true;
            }
         } else {
            this.brewingTime = 0;
         }

         if (dirty) {
            this.setChanged();
         }
      }
   }

   private boolean canCraft() {
      for (int i = 0; i < 3; i++) {
         ItemStack stack = this.getItem(i);
         if (stack.isEmpty() || !(stack.getItem() instanceof PotionItem)) {
            return false;
         }
      }

      ItemStack catalyst = this.getItem(4);
      if (!catalyst.isEmpty() && catalyst.getItem() == ObjectRegistry.HERBAL_INFUSION.get()) {
         ItemStack output = this.getItem(3);
         return output.isEmpty();
      } else {
         return false;
      }
   }

   private void craft(Level world) {
      List<MobEffectInstance> combinedEffects = new ArrayList<>();

      for (int i = 0; i < 3; i++) {
         ItemStack potionStack = this.getItem(i);
         List<MobEffectInstance> potionEffects = StreamSupport.<MobEffectInstance>stream(
               ((PotionContents)potionStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).getAllEffects().spliterator(), false
            )
            .toList();
         combinedEffects.addAll(potionEffects);
      }

      Map<Holder<MobEffect>, MobEffectInstance> uniqueEffectsMap = new HashMap<>();

      for (MobEffectInstance effectInstance : combinedEffects) {
         Holder<MobEffect> effect = effectInstance.getEffect();
         if (!uniqueEffectsMap.containsKey(effect) || effectInstance.getAmplifier() > uniqueEffectsMap.get(effect).getAmplifier()) {
            uniqueEffectsMap.put(effect, new MobEffectInstance(effect, effectInstance.getDuration(), effectInstance.getAmplifier()));
         }
      }

      ItemStack outputPotion = new ItemStack((ItemLike)ObjectRegistry.FLASK.get());
      PotionContents potionContents = (PotionContents)outputPotion.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
      RandomSource random = world != null ? world.getRandom() : RandomSource.create();
      int randomTexture = random.nextInt(6) + 1;
      outputPotion.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(randomTexture));

      for (MobEffectInstance effectInstance : uniqueEffectsMap.values()) {
         potionContents = potionContents.withEffectAdded(effectInstance);
      }

      outputPotion.set(DataComponents.POTION_CONTENTS, potionContents);
      this.setItem(3, outputPotion);

      for (int i = 0; i < 3; i++) {
         this.removeItem(i, 1);
      }

      this.removeItem(4, 1);
   }

   @Override
   public int @NotNull [] getSlotsForFace(Direction side) {
      if (side.equals(Direction.UP)) {
         return SLOTS_FOR_UP;
      } else {
         return side.equals(Direction.DOWN) ? SLOTS_FOR_DOWN : SLOTS_FOR_SIDE;
      }
   }

   @Override
   public NonNullList<ItemStack> getItems() {
      return this.inventory;
   }

   @Override
   public void setItem(int slot, ItemStack stack) {
      ItemStack stackInSlot = (ItemStack)this.inventory.get(slot);
      boolean dirty = !stack.isEmpty() && ItemStack.isSameItem(stack, stackInSlot) && ItemStack.matches(stack, stackInSlot);
      this.inventory.set(slot, stack);
      if (stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }

      if (slot < 3 && !dirty) {
         this.totalBrewingTime = PlatformHelper.getBrewingDuration();
         this.brewingTime = 0;
         this.setChanged();
      }

      if (slot == 4 && !dirty) {
         this.setChanged();
      }
   }

   @Override
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
      return new CauldronGuiHandler(syncId, inv, this, this.propertyDelegate);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void tick(Level param1, BlockPos nullx, BlockState nullxx, BlockEntity nullxxx) {
      this.tick(var1, nullx, nullxx, (CauldronBlockEntity)nullxxx);
   }
}
