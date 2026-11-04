package de.maxhenkel.gravestone.entity;

import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GhostPlayerEntity extends Monster {
   private static final EntityDataAccessor<Optional<UUID>> PLAYER_UUID = SynchedEntityData.defineId(
      GhostPlayerEntity.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Byte> PLAYER_MODEL = SynchedEntityData.defineId(GhostPlayerEntity.class, EntityDataSerializers.BYTE);

   public GhostPlayerEntity(EntityType type, Level world) {
      super(type, world);
   }

   public GhostPlayerEntity(Level world, UUID playerUUID, Component name, NonNullList<ItemStack> equipment, byte model) {
      this((EntityType)Main.GHOST.get(), world);
      this.setPlayerUUID(playerUUID);
      this.setCustomName(name);
      this.setModel(model);
      Arrays.fill(this.armorDropChances, 0.0F);
      Arrays.fill(this.handDropChances, 0.0F);

      for (int i = 0; i < EquipmentSlot.values().length; i++) {
         this.setItemSlot(EquipmentSlot.values()[i], (ItemStack)equipment.get(i));
      }
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(PLAYER_UUID, Optional.empty());
      builder.define(PLAYER_MODEL, (byte)0);
   }

   public static AttributeSupplier getGhostAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ARMOR, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.23F)
         .add(Attributes.FOLLOW_RANGE, 35.0)
         .build();
   }

   public boolean shouldShowName() {
      return false;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      if ((Boolean)Main.SERVER_CONFIG.friendlyGhost.get()) {
         this.targetSelector
            .addGoal(
               10,
               new NearestAttackableTargetGoal(
                  this,
                  LivingEntity.class,
                  10,
                  false,
                  true,
                  entityLiving -> entityLiving != null
                     && !entityLiving.isInvisible()
                     && (entityLiving instanceof Monster || entityLiving instanceof Slime)
                     && !(entityLiving instanceof Creeper)
                     && !(entityLiving instanceof GhostPlayerEntity)
               )
            );
      } else {
         this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, Player.class, true));
      }
   }

   public boolean isInvertedHealAndHarm() {
      return true;
   }

   public void setPlayerUUID(UUID uuid) {
      this.getEntityData().set(PLAYER_UUID, Optional.of(uuid));
      if (uuid.toString().equals("af3bd5f4-8634-4700-8281-e4cc851be180")) {
         this.setOverpowered();
      }
   }

   private void setOverpowered() {
      this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(35.0);
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4);
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(20.0);
   }

   public void setCustomName(@Nullable Component name) {
      super.setCustomName(name);
      if (name != null && name.getString().equals("henkelmax")) {
         this.setOverpowered();
      }
   }

   public UUID getPlayerUUID() {
      return ((Optional)this.getEntityData().get(PLAYER_UUID)).orElse(GraveUtils.EMPTY_UUID);
   }

   public void setModel(byte model) {
      this.entityData.set(PLAYER_MODEL, model);
   }

   public byte getModel() {
      return (Byte)this.entityData.get(PLAYER_MODEL);
   }

   public boolean isWearing(PlayerModelPart part) {
      return (this.getModel() & part.getMask()) == part.getMask();
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      ((Optional)this.getEntityData().get(PLAYER_UUID)).ifPresent(uuid -> compound.putUUID("PlayerUUID", uuid));
      compound.putByte("Model", this.getModel());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.contains("player_uuid")) {
         String uuidStr = compound.getString("player_uuid");

         try {
            UUID uuid = UUID.fromString(uuidStr);
            this.setPlayerUUID(uuid);
         } catch (Exception var4) {
         }
      } else if (compound.contains("PlayerUUID")) {
         this.setPlayerUUID(compound.getUUID("PlayerUUID"));
      }

      this.setModel(compound.getByte("Model"));
   }

   public boolean doHurtTarget(Entity entity) {
      return !entity.getName().getString().equals("henkelmax") && !entity.getUUID().toString().equals("af3bd5f4-8634-4700-8281-e4cc851be180")
         ? super.doHurtTarget(entity)
         : true;
   }
}
