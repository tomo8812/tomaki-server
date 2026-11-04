package net.satisfy.candlelight.core.block.entity;

import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.satisfy.candlelight.core.block.TableSetBlock;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;
import net.satisfy.candlelight.core.util.DinnerGuest;
import net.satisfy.candlelight.platform.PlatformHelper;
import net.satisfy.foundation.storage.StorageBlockEntity;

public class TableSetBlockEntity extends StorageBlockEntity {
   private static final String GLASS_DRINK_KEY = "EffectStack";
   private static final String WINE_GLASS_DRINK_KEY = "WineGlassDrink";
   private static final String HOST_KEY = "Host";
   private static final int GUEST_TIMEOUT = 600;
   private static final double EAT_DISTANCE_SQR = 4.0;
   private static final float GUEST_SPEED = 0.6F;
   private ItemStack glassDrink = ItemStack.EMPTY;
   private UUID guest;
   private UUID host;
   private int guestTicks;
   private ItemStack wineGlassDrink = ItemStack.EMPTY;
   private static final String SERVED_AT_KEY = "ServedAt";
   private long servedAt = -1L;

   public TableSetBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.TABLE_SET_BLOCK_ENTITY.get(), pos, state, 1);
   }

   public TableSetBlockEntity(BlockPos pos, BlockState state, int size) {
      super((BlockEntityType)EntityTypeRegistry.TABLE_SET_BLOCK_ENTITY.get(), pos, state, size);
   }

   public void setStack(int slot, ItemStack stack) {
      if (!stack.isEmpty() && ((ItemStack)this.getInventory().get(slot)).isEmpty() && this.level != null) {
         this.servedAt = this.level.getGameTime();
      }

      super.setStack(slot, stack);
   }

   public long getServedAt() {
      return this.servedAt;
   }

   public void setHost(UUID host) {
      this.host = host;
      this.setChanged();
   }

   public ItemStack getDrink(boolean wineGlass) {
      return wineGlass ? this.wineGlassDrink : this.glassDrink;
   }

   public void setDrink(boolean wineGlass, ItemStack stack) {
      if (wineGlass) {
         this.wineGlassDrink = stack;
      } else {
         this.glassDrink = stack;
      }

      this.setChanged();
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.glassDrink = ItemStack.parseOptional(provider, tag.getCompound("EffectStack"));
      this.wineGlassDrink = ItemStack.parseOptional(provider, tag.getCompound("WineGlassDrink"));
      this.servedAt = tag.contains("ServedAt") ? tag.getLong("ServedAt") : -1L;
      this.host = tag.hasUUID("Host") ? tag.getUUID("Host") : null;
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      if (!this.glassDrink.isEmpty()) {
         tag.put("EffectStack", this.glassDrink.save(provider, new CompoundTag()));
      }

      if (this.servedAt >= 0L) {
         tag.putLong("ServedAt", this.servedAt);
      }

      if (this.host != null) {
         tag.putUUID("Host", this.host);
      }

      if (!this.wineGlassDrink.isEmpty()) {
         tag.put("WineGlassDrink", this.wineGlassDrink.save(provider, new CompoundTag()));
      }
   }

   public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
      ItemStack dish = this.getInventory().isEmpty() ? ItemStack.EMPTY : (ItemStack)this.getInventory().get(0);
      if (dish.isEmpty() || (Boolean)state.getValue(TableSetBlock.CLOCHE)) {
         this.guest = null;
      } else if (this.guest != null) {
         this.tickGuest(level, pos);
      } else if (PlatformHelper.enableDinnerGuests()
         && level.getGameTime() % PlatformHelper.dinnerGuestInterval() == 0L
         && isDinnerTime(level)
         && !(level.random.nextFloat() >= PlatformHelper.dinnerGuestChance())) {
         Vec3 center = Vec3.atCenterOf(pos);
         level.getEntitiesOfClass(
               Villager.class,
               new AABB(pos).inflate(PlatformHelper.dinnerGuestRange()),
               villager -> !villager.isBaby()
                  && !villager.isSleeping()
                  && villager.getTradingPlayer() == null
                  && (PlatformHelper.nitwitsEat() || villager.getVillagerData().getProfession() != VillagerProfession.NITWIT)
            )
            .stream()
            .min(Comparator.comparingDouble(villager -> villager.distanceToSqr(center)))
            .ifPresent(villager -> {
               this.guest = villager.getUUID();
               this.guestTicks = 0;
            });
      }
   }

   private void tickGuest(ServerLevel level, BlockPos pos) {
      if (level.getEntity(this.guest) instanceof Villager villager && villager.isAlive() && !villager.isSleeping() && ++this.guestTicks <= 600) {
         Vec3 center = Vec3.atCenterOf(pos);
         if (villager.distanceToSqr(center) > 4.0) {
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 0.6F, 1));
         } else {
            this.guest = null;
            ItemStack dish = this.removeStack(0);
            villager.getLookControl().setLookAt(center);
            level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F);
            level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, dish), center.x, center.y + 0.2, center.z, 8, 0.15, 0.1, 0.15, 0.05);
            if (villager.getVillagerData().getProfession() != VillagerProfession.NITWIT) {
               ((DinnerGuest)villager).candlelight$setDinnerDiscountUntil(level.getGameTime() + PlatformHelper.dinnerDiscountTicks());
               ((DinnerGuest)villager).candlelight$setDinnerHost(this.host);
               level.sendParticles(ParticleTypes.HAPPY_VILLAGER, villager.getX(), villager.getEyeY() + 0.3, villager.getZ(), 6, 0.3, 0.2, 0.3, 0.0);
            }
         }
      } else {
         this.guest = null;
      }
   }

   private static boolean isDinnerTime(Level level) {
      long time = level.getDayTime() % 24000L;
      return time >= PlatformHelper.dinnerStart() && time < PlatformHelper.dinnerEnd();
   }
}
