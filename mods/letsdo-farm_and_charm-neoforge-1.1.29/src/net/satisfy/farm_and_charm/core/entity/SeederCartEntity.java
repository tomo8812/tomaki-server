package net.satisfy.farm_and_charm.core.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.FertilizedFarmlandBlock;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;

public class SeederCartEntity extends AbstractCartEntity implements MenuProvider {
   private static final int SEED_FLIGHT_TICKS = 16;
   private static final EntityDataAccessor<Integer> DATA_SOW_EFFECT_TICKS = SynchedEntityData.defineId(SeederCartEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<BlockPos> DATA_SOW_EFFECT_POS = SynchedEntityData.defineId(SeederCartEntity.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<ItemStack> DATA_SOW_ITEM = SynchedEntityData.defineId(SeederCartEntity.class, EntityDataSerializers.ITEM_STACK);
   private final SimpleContainer inventory;
   private final List<SeederCartEntity.PendingSow> pendingSows = new ArrayList<>();
   private final Set<BlockPos> reservedPositions = new HashSet<>();
   private int clientPrevSowEffectTicks;

   public SeederCartEntity(EntityType<? extends AbstractCartEntity> entityType, Level level) {
      super(entityType, level);
      this.inventory = new SimpleContainer(5) {
         public boolean canPlaceItem(int slot, ItemStack stack) {
            return SeederCartEntity.isSeedItem(stack);
         }
      };
   }

   private static boolean isSeedItem(ItemStack stack) {
      return !stack.isEmpty() && stack.is(TagRegistry.SEEDS) && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CropBlock;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_SOW_EFFECT_TICKS, 0);
      builder.define(DATA_SOW_EFFECT_POS, BlockPos.ZERO);
      builder.define(DATA_SOW_ITEM, ItemStack.EMPTY);
   }

   public int getSowEffectTicks() {
      return (Integer)this.entityData.get(DATA_SOW_EFFECT_TICKS);
   }

   @NotNull
   @Override
   public InteractionResult interact(Player player, InteractionHand hand) {
      if (!player.isShiftKeyDown() || !player.getItemInHand(hand).isEmpty() || this.getPulling() != player) {
         return super.interact(player, hand);
      }

      if (this.level().isClientSide) {
         return InteractionResult.SUCCESS;
      }

      player.openMenu(this);
      return InteractionResult.CONSUME;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         int effectTicks = (Integer)this.entityData.get(DATA_SOW_EFFECT_TICKS);
         if (effectTicks > 0) {
            this.entityData.set(DATA_SOW_EFFECT_TICKS, effectTicks - 1);
         }

         this.tickPendingSows();
         if (this.tickCount % 4 == 0) {
            this.suckInNearbySeeds();
         }
      } else {
         int effectTicks = (Integer)this.entityData.get(DATA_SOW_EFFECT_TICKS);
         if (effectTicks > 0 && this.clientPrevSowEffectTicks <= 0) {
            this.spawnSeedFlightParticle(effectTicks);
         } else if (effectTicks <= 0 && this.clientPrevSowEffectTicks > 0) {
            this.spawnSeedLandingEffect((BlockPos)this.entityData.get(DATA_SOW_EFFECT_POS));
         }

         this.clientPrevSowEffectTicks = effectTicks;
      }
   }

   private void tickPendingSows() {
      if (!this.pendingSows.isEmpty()) {
         Iterator<SeederCartEntity.PendingSow> iterator = this.pendingSows.iterator();

         while (iterator.hasNext()) {
            SeederCartEntity.PendingSow sow = iterator.next();
            sow.ticks--;
            if (sow.ticks <= 0) {
               this.level().setBlock(sow.pos, sow.state, 3);
               this.reservedPositions.remove(sow.pos);
               iterator.remove();
            }
         }
      }
   }

   private void suckInNearbySeeds() {
      for (ItemEntity itemEntity : this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.6, 0.4, 0.6))) {
         if (itemEntity.isAlive() && isSeedItem(itemEntity.getItem())) {
            HopperBlockEntity.addItem(this.inventory, itemEntity);
         }
      }
   }

   private void spawnSeedFlightParticle(int travelTicks) {
      ItemStack itemStack = (ItemStack)this.entityData.get(DATA_SOW_ITEM);
      if (!itemStack.isEmpty()) {
         BlockPos targetPos = (BlockPos)this.entityData.get(DATA_SOW_EFFECT_POS);
         double startX = this.getX();
         double startY = this.getY() + 0.7;
         double startZ = this.getZ();
         double endX = targetPos.getX() + 0.5;
         double endY = targetPos.getY() + 0.2;
         double endZ = targetPos.getZ() + 0.5;
         double ticks = Math.max(travelTicks, 1);
         double velocityX = (endX - startX) / ticks;
         double velocityZ = (endZ - startZ) / ticks;
         double gravity = 0.04;
         double velocityY = (endY - startY) / ticks + gravity * ticks * 0.5;
         ItemParticleOption particleOption = new ItemParticleOption(ParticleTypes.ITEM, itemStack);
         this.level().addParticle(particleOption, startX, startY, startZ, velocityX, velocityY, velocityZ);
         this.level()
            .addParticle(
               particleOption,
               startX,
               startY,
               startZ,
               velocityX + (this.random.nextDouble() - 0.5) * 0.03,
               velocityY + (this.random.nextDouble() - 0.5) * 0.03,
               velocityZ + (this.random.nextDouble() - 0.5) * 0.03
            );
         float yawRad = this.getYRot() * (float) (Math.PI / 180.0);
         float backX = Mth.sin(yawRad);
         float backZ = -Mth.cos(yawRad);

         for (int i = 0; i < 3; i++) {
            double dustX = this.getX() + backX * 1.3 + (this.random.nextDouble() - 0.5) * 0.4;
            double dustY = this.getY() + 0.35;
            double dustZ = this.getZ() + backZ * 1.3 + (this.random.nextDouble() - 0.5) * 0.4;
            this.level().addParticle(ParticleTypes.CRIT, dustX, dustY, dustZ, backX * -0.02, -0.02, backZ * -0.02);
         }
      }
   }

   private void spawnSeedLandingEffect(BlockPos cropPos) {
      BlockPos farmlandPos = cropPos.below();
      BlockState farmlandState = this.level().getBlockState(farmlandPos);
      this.level().addDestroyBlockEffect(farmlandPos, farmlandState);

      for (int i = 0; i < 5; i++) {
         double offsetX = cropPos.getX() + 0.5 + (this.random.nextDouble() - 0.5) * 0.8;
         double offsetY = cropPos.getY() + 0.1 + this.random.nextDouble() * 0.5;
         double offsetZ = cropPos.getZ() + 0.5 + (this.random.nextDouble() - 0.5) * 0.8;
         this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, offsetX, offsetY, offsetZ, 0.0, 0.05, 0.0);
      }
   }

   @Override
   public void pulledPostTick() {
      double prevX = this.getX();
      double prevZ = this.getZ();
      super.pulledPostTick();
      if (!this.level().isClientSide) {
         if (this.getPulling() instanceof Player) {
            if (!(Math.abs(this.getX() - prevX) < 1.0E-4) || !(Math.abs(this.getZ() - prevZ) < 1.0E-4)) {
               this.handleSowServer();
            }
         }
      }
   }

   private void triggerSowEffect(BlockPos blockPos, ItemStack itemStack) {
      if (!this.level().isClientSide) {
         this.entityData.set(DATA_SOW_EFFECT_POS, blockPos);
         this.entityData.set(DATA_SOW_ITEM, itemStack.copyWithCount(1));
         this.entityData.set(DATA_SOW_EFFECT_TICKS, 16);
      }
   }

   private void handleSowServer() {
      BlockPos groundPos = this.getOnPos();
      Direction sidePos = Direction.fromYRot(this.getYRot()).getClockWise();
      BlockPos[] positions = new BlockPos[]{groundPos, groundPos.relative(sidePos)};

      for (BlockPos blockPos : positions) {
         BlockState blockState = this.level().getBlockState(blockPos);
         boolean isFarmland = blockState.is(Blocks.FARMLAND) || blockState.is((Block)ObjectRegistry.FERTILIZED_FARM_BLOCK.get());
         if (isFarmland) {
            BlockPos abovePos = blockPos.above();
            if (!this.reservedPositions.contains(abovePos)) {
               BlockState aboveState = this.level().getBlockState(abovePos);
               if (aboveState.isAir()) {
                  int seedSlot = this.findSeedSlot(blockState);
                  if (seedSlot >= 0) {
                     ItemStack seedStack = this.inventory.getItem(seedSlot);
                     BlockItem blockItem = (BlockItem)seedStack.getItem();
                     BlockState cropState = blockItem.getBlock().defaultBlockState();
                     ItemStack particleItem = seedStack.copyWithCount(1);
                     seedStack.shrink(1);
                     this.reservedPositions.add(abovePos);
                     this.pendingSows.add(new SeederCartEntity.PendingSow(abovePos, cropState, 16));
                     this.triggerSowEffect(abovePos, particleItem);
                  }
               }
            }
         }
      }
   }

   private int findSeedSlot(BlockState soilState) {
      for (int slot = 0; slot < this.inventory.getContainerSize(); slot++) {
         ItemStack stack = this.inventory.getItem(slot);
         if (isSeedItem(stack) && FertilizedFarmlandBlock.canPlantOn(soilState, stack)) {
            return slot;
         }
      }

      return -1;
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      NonNullList<ItemStack> items = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compoundTag, items, this.registryAccess());

      for (int slotIndex = 0; slotIndex < items.size(); slotIndex++) {
         this.inventory.setItem(slotIndex, (ItemStack)items.get(slotIndex));
      }
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      NonNullList<ItemStack> items = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);

      for (int slotIndex = 0; slotIndex < items.size(); slotIndex++) {
         items.set(slotIndex, this.inventory.getItem(slotIndex));
      }

      ContainerHelper.saveAllItems(compoundTag, items, this.registryAccess());
   }

   public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
      return new HopperMenu(syncId, playerInventory, this.inventory);
   }

   public void remove(RemovalReason removalReason) {
      if (!this.level().isClientSide && removalReason == RemovalReason.KILLED) {
         Containers.dropContents(this.level(), this.blockPosition(), this.inventory);
      }

      super.remove(removalReason);
   }

   @Override
   protected ItemStack getCartItemStack() {
      return new ItemStack((ItemLike)ObjectRegistry.SEEDER.get());
   }

   @NotNull
   public Component getDisplayName() {
      return ((Item)ObjectRegistry.SEEDER.get()).getDefaultInstance().getHoverName();
   }

   @Override
   public ItemStack getPickResult() {
      return new ItemStack((ItemLike)ObjectRegistry.SEEDER.get());
   }

   private static final class PendingSow {
      final BlockPos pos;
      final BlockState state;
      int ticks;

      PendingSow(BlockPos pos, BlockState state, int ticks) {
         this.pos = pos;
         this.state = state;
         this.ticks = ticks;
      }
   }
}
