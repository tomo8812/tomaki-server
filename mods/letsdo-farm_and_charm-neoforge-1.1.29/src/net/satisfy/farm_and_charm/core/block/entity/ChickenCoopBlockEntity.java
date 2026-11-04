package net.satisfy.farm_and_charm.core.block.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

public class ChickenCoopBlockEntity extends BlockEntity {
   private static final String KEY_CHICKENS = "Chickens";
   private static final String KEY_EGGS = "EggCount";
   private static final String KEY_COOP_TIME = "CoopTime";
   private static final String KEY_UUID = "UUID";
   private static final String KEY_UUID_MOST = "UUIDMost";
   private static final String KEY_UUID_LEAST = "UUIDLeast";
   private static final String KEY_LEASH = "leash";
   private static final String KEY_CAPTURED_UUID = "CapturedUUID";
   private final List<CompoundTag> storedChickens = new ArrayList<>();
   private int eggCount = 0;

   public ChickenCoopBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.CHICKEN_COOP_BLOCK_ENTITY.get(), pos, state);
   }

   public static void tick(Level level, BlockPos pos, ChickenCoopBlockEntity coop) {
      if (!level.isClientSide) {
         Iterator<CompoundTag> iterator = coop.storedChickens.iterator();

         while (iterator.hasNext()) {
            CompoundTag chickenTag = iterator.next();
            int ticks = chickenTag.getInt("CoopTime") - 1;
            if (ticks <= 0) {
               chickenTag.remove("CoopTime");
               if (EntityType.CHICKEN.create(level) instanceof Chicken spawned) {
                  int coopCooldown = 1200 * (4 + level.getRandom().nextInt(2));
                  CompoundTag toLoad = chickenTag.copy();
                  toLoad.remove("UUID");
                  toLoad.remove("UUIDMost");
                  toLoad.remove("UUIDLeast");
                  toLoad.remove("CapturedUUID");
                  spawned.load(toLoad);
                  spawned.setHealth(spawned.getMaxHealth());
                  spawned.setInvisible(false);
                  spawned.setNoAi(false);
                  spawned.setSilent(false);
                  if (spawned instanceof ChickenCoopAccess coopChicken) {
                     coopChicken.farmAndCharm$setCoopCooldown(coopCooldown);
                  }

                  BlockPos spawnPos = findSafeSpawnPosition(level, pos);
                  spawned.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, spawned.getYRot(), spawned.getXRot());
                  level.addFreshEntity(spawned);
                  iterator.remove();
                  coop.setChanged();
                  level.sendBlockUpdated(pos, coop.getBlockState(), coop.getBlockState(), 3);
               }
            } else {
               chickenTag.putInt("CoopTime", ticks);
            }
         }
      }
   }

   private static BlockPos findSafeSpawnPosition(Level level, BlockPos center) {
      for (Direction dir : Plane.HORIZONTAL) {
         BlockPos offset = center.relative(dir);
         if (level.getBlockState(offset).isAir() && level.getBlockState(offset.above()).isAir()) {
            return offset;
         }
      }

      return center.above();
   }

   public boolean hasSpaceForChicken() {
      return this.storedChickens.size() < PlatformHelper.getChickenCoopMaxChickens();
   }

   public void addChicken(Chicken chicken) {
      if (this.level != null && !this.level.isClientSide) {
         if (this.hasSpaceForChicken()) {
            chicken.removeAllEffects();
            CompoundTag tag = new CompoundTag();
            chicken.save(tag);
            UUID u = chicken.getUUID();
            tag.putUUID("CapturedUUID", u);
            tag.remove("leash");
            tag.remove("UUID");
            tag.remove("UUIDMost");
            tag.remove("UUIDLeast");
            tag.putInt("CoopTime", 200 + chicken.getRandom().nextInt(200));
            this.storedChickens.add(tag);
            boolean hadLeash = chicken.getLeashHolder() != null;
            if (hadLeash) {
               chicken.dropLeash(true, false);
               chicken.spawnAtLocation(Items.LEAD);
            }

            chicken.stopRiding();
            chicken.ejectPassengers();
            chicken.setNoAi(true);
            chicken.setSilent(true);
            chicken.setInvisible(true);
            chicken.discard();
            if (!hadLeash) {
               this.addEgg();
            }

            this.setChanged();
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         }
      }
   }

   public void releaseAllChickens() {
      if (this.level != null && !this.level.isClientSide) {
         for (CompoundTag tag : new ArrayList<>(this.storedChickens)) {
            tag.remove("CoopTime");
            if (EntityType.CHICKEN.create(this.level) instanceof Chicken spawned) {
               int coopCooldown = 1200 * (4 + this.level.getRandom().nextInt(2));
               CompoundTag toLoad = tag.copy();
               toLoad.remove("UUID");
               toLoad.remove("UUIDMost");
               toLoad.remove("UUIDLeast");
               toLoad.remove("CapturedUUID");
               spawned.load(toLoad);
               spawned.setHealth(spawned.getMaxHealth());
               spawned.setInvisible(false);
               spawned.setNoAi(false);
               spawned.setSilent(false);
               if (spawned instanceof ChickenCoopAccess coopChicken) {
                  coopChicken.farmAndCharm$setCoopCooldown(coopCooldown);
               }

               BlockPos spawnPos = findSafeSpawnPosition(this.level, this.worldPosition);
               spawned.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, spawned.getYRot(), spawned.getXRot());
               this.level.addFreshEntity(spawned);
            }
         }

         this.storedChickens.clear();
         this.setChanged();
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public boolean containsChicken(Chicken chicken) {
      UUID u = chicken.getUUID();

      for (CompoundTag tag : this.storedChickens) {
         if (tag.hasUUID("CapturedUUID") && tag.getUUID("CapturedUUID").equals(u)) {
            return true;
         }
      }

      return false;
   }

   public void addEgg() {
      if (this.eggCount < PlatformHelper.getChickenCoopMaxEggs()) {
         this.eggCount++;
         this.setChanged();
      }
   }

   public int getEggCount() {
      return this.eggCount;
   }

   public void clearEggs() {
      this.eggCount = 0;
      this.setChanged();
   }

   public List<CompoundTag> getStoredChickens() {
      return this.storedChickens;
   }

   public void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      ListTag chickenList = new ListTag();

      for (CompoundTag chickenTag : this.storedChickens) {
         chickenList.add(chickenTag.copy());
      }

      tag.put("Chickens", chickenList);
      tag.putInt("EggCount", this.eggCount);
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.storedChickens.clear();
      ListTag chickenList = tag.getList("Chickens", 10);

      for (int i = 0; i < chickenList.size(); i++) {
         CompoundTag chickenTag = chickenList.getCompound(i);
         this.storedChickens.add(chickenTag);
      }

      this.eggCount = tag.getInt("EggCount");
   }
}
