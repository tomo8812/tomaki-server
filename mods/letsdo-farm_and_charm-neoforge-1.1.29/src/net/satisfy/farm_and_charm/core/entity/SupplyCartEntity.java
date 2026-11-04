package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class SupplyCartEntity extends AbstractCartEntity implements MenuProvider {
   private static final EntityDataAccessor<Boolean> DATA_OPEN = SynchedEntityData.defineId(SupplyCartEntity.class, EntityDataSerializers.BOOLEAN);
   private final SimpleContainer inventory = new SimpleContainer(27);
   private int openTicks;

   public SupplyCartEntity(EntityType<? extends SupplyCartEntity> entityType, Level level) {
      super(entityType, level);
   }

   @NotNull
   @Override
   public InteractionResult interact(Player player, InteractionHand hand) {
      return player.isSecondaryUseActive() ? this.openInventory(player, hand) : this.primaryAction(player, hand);
   }

   private InteractionResult primaryAction(Player player, InteractionHand hand) {
      if (!player.getItemInHand(hand).isEmpty()) {
         return InteractionResult.PASS;
      } else {
         Entity pullingEntity = this.getPulling();
         if (pullingEntity == null || pullingEntity instanceof Player) {
            return super.onPrimaryUse(player, hand);
         } else if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
         } else if (!this.getPassengers().isEmpty()) {
            return InteractionResult.PASS;
         } else if (player.isPassenger()) {
            return InteractionResult.PASS;
         } else {
            return player.startRiding(this, true) ? InteractionResult.CONSUME : InteractionResult.PASS;
         }
      }
   }

   private InteractionResult openInventory(Player player, InteractionHand hand) {
      if (!player.getItemInHand(hand).isEmpty()) {
         return InteractionResult.PASS;
      }

      if (this.level().isClientSide) {
         return InteractionResult.SUCCESS;
      }

      player.openMenu(this);
      this.entityData.set(DATA_OPEN, true);
      this.openTicks = 10;
      return InteractionResult.CONSUME;
   }

   @NotNull
   public Vec3 getPassengerRidingPosition(Entity passenger) {
      float yawRadians = this.getYRot() * (float) (Math.PI / 180.0);
      double forwardX = -Mth.sin(yawRadians);
      double forwardZ = Mth.cos(yawRadians);
      double seatBack = -0.85;
      double seatUp = 0.7;
      double seatSide = 0.0;
      double sideX = -forwardZ;
      double sideZ = forwardX;
      return new Vec3(this.getX() - forwardX * seatBack + sideX * seatSide, this.getY() + seatUp, this.getZ() - forwardZ * seatBack + sideZ * seatSide);
   }

   public void positionRider(Entity passenger, MoveFunction moveFunction) {
      super.positionRider(passenger, moveFunction);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_OPEN, false);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      NonNullList<ItemStack> items = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compoundTag, items, this.registryAccess());

      for (int slotIndex = 0; slotIndex < items.size(); slotIndex++) {
         this.inventory.setItem(slotIndex, (ItemStack)items.get(slotIndex));
      }

      this.openTicks = compoundTag.getInt("OpenTicks");
      this.entityData.set(DATA_OPEN, this.openTicks > 0);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      NonNullList<ItemStack> items = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);

      for (int slotIndex = 0; slotIndex < items.size(); slotIndex++) {
         items.set(slotIndex, this.inventory.getItem(slotIndex));
      }

      ContainerHelper.saveAllItems(compoundTag, items, this.registryAccess());
      compoundTag.putInt("OpenTicks", this.openTicks);
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide && this.openTicks > 0) {
         this.openTicks--;
         if (this.openTicks == 0) {
            this.entityData.set(DATA_OPEN, false);
         }
      }
   }

   @NotNull
   public Component getDisplayName() {
      return ((Item)ObjectRegistry.SUPPLY_CART.get()).getDefaultInstance().getHoverName();
   }

   public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
      return ChestMenu.threeRows(syncId, playerInventory, this.inventory);
   }

   public void remove(RemovalReason removalReason) {
      if (!this.level().isClientSide && removalReason == RemovalReason.KILLED) {
         Containers.dropContents(this.level(), this.blockPosition(), this.inventory);
      }

      super.remove(removalReason);
   }

   @Override
   protected ItemStack getCartItemStack() {
      return new ItemStack((ItemLike)ObjectRegistry.SUPPLY_CART.get());
   }
}
