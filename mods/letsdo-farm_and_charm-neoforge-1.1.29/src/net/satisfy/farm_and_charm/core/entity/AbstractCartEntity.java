package net.satisfy.farm_and_charm.core.entity;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.util.CartWorldData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCartEntity extends Entity {
   private static final EntityDataAccessor<Float> DATA_WHEEL_ROTATION = SynchedEntityData.defineId(AbstractCartEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_SPEED = SynchedEntityData.defineId(AbstractCartEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_HURT_TIME = SynchedEntityData.defineId(AbstractCartEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_HURT_DIR = SynchedEntityData.defineId(AbstractCartEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(AbstractCartEntity.class, EntityDataSerializers.FLOAT);
   private float prevWheelRotation;
   private float wheelRotation;
   private int lerpSteps;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private double lerpYaw;
   private double lerpPitch;
   private int pullingId = -1;
   private UUID pullingUuid;
   protected double spacing = 1.7;
   protected Entity pulling;

   protected AbstractCartEntity(EntityType<? extends AbstractCartEntity> entityType, Level level) {
      super(entityType, level);
      this.blocksBuilding = true;
   }

   public float maxUpStep() {
      return 1.2F;
   }

   @NotNull
   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(3.0, 3.0, 3.0);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_WHEEL_ROTATION, 0.0F);
      builder.define(DATA_SPEED, 0.0F);
      builder.define(DATA_HURT_TIME, 0);
      builder.define(DATA_HURT_DIR, 1);
      builder.define(DATA_DAMAGE, 0.0F);
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (compound.hasUUID("PullingUUID")) {
         this.pullingUuid = compound.getUUID("PullingUUID");
      } else {
         this.pullingUuid = null;
      }

      if (compound.contains("CartSpeed")) {
         this.entityData.set(DATA_SPEED, compound.getFloat("CartSpeed"));
      }

      if (compound.contains("CartDamage")) {
         this.entityData.set(DATA_DAMAGE, compound.getFloat("CartDamage"));
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      if (this.pulling != null && this.pullingUuid != null) {
         compound.putUUID("PullingUUID", this.pullingUuid);
      }

      compound.putFloat("CartSpeed", (Float)this.entityData.get(DATA_SPEED));
      compound.putFloat("CartDamage", (Float)this.entityData.get(DATA_DAMAGE));
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return null;
   }

   protected boolean isPlayerDrivingPulledByHorse() {
      return this.pulling instanceof AbstractHorse && this.getCartDriver() != null;
   }

   public boolean canBeCollidedWith() {
      return this.isAlive();
   }

   public boolean isPushable() {
      return true;
   }

   protected boolean canAddPassenger(Entity passenger) {
      if (!(passenger instanceof Player)) {
         return false;
      } else {
         return !(this.pulling instanceof AbstractHorse) ? false : this.getPassengers().isEmpty();
      }
   }

   public boolean isPickable() {
      return true;
   }

   public ItemStack getPickResult() {
      return this.getCartItemStack().copy();
   }

   @NotNull
   public InteractionResult interact(Player player, InteractionHand hand) {
      return this.onPrimaryUse(player, hand);
   }

   protected InteractionResult onSecondaryUse(Player player, InteractionHand hand) {
      return InteractionResult.PASS;
   }

   protected InteractionResult onPrimaryUse(Player player, InteractionHand hand) {
      if (!player.getItemInHand(hand).isEmpty()) {
         return InteractionResult.PASS;
      }

      if (!this.level().isClientSide) {
         if (this.pulling == player) {
            this.setPulling(null);
         } else if (this.pulling == null) {
            this.setPulling(player);
         }

         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public void tick() {
      this.prevWheelRotation = this.wheelRotation;
      this.wheelRotation = (Float)this.entityData.get(DATA_WHEEL_ROTATION);
      int hurtTime = (Integer)this.entityData.get(DATA_HURT_TIME);
      if (hurtTime > 0) {
         this.entityData.set(DATA_HURT_TIME, hurtTime - 1);
      }

      float damage = (Float)this.entityData.get(DATA_DAMAGE);
      if (damage > 0.0F) {
         this.entityData.set(DATA_DAMAGE, Math.max(damage - 1.0F, 0.0F));
      }

      if (!this.isNoGravity()) {
         this.setDeltaMovement(0.0, this.getDeltaMovement().y - 0.08, 0.0);
      }

      if (this.isVehicle()) {
         Vec3 current = this.getDeltaMovement();
         this.setDeltaMovement(0.0, current.y, 0.0);
      }

      super.tick();
      this.tickLerp();
      if (this.level().isClientSide) {
         this.clientResolvePullingIfPossible();
      }

      if (this.pulling == null) {
         this.setXRot(25.0F);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.attemptReattach();
      }

      for (Entity entity : this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this))) {
         this.push(entity);
      }

      if (!this.level().isClientSide && this.getY() < this.level().getMinBuildHeight() - 64) {
         this.destroyCart(null);
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      if (!this.isRemoved() && !this.isInvulnerableTo(source)) {
         if (!this.level().isClientSide) {
            this.entityData.set(DATA_HURT_DIR, -(Integer)this.entityData.get(DATA_HURT_DIR));
            this.entityData.set(DATA_HURT_TIME, 10);
            float newDamage = (Float)this.entityData.get(DATA_DAMAGE) + amount * 10.0F;
            this.entityData.set(DATA_DAMAGE, newDamage);
            boolean instantBreak = false;
            Entity direct = source.getDirectEntity();
            Entity attacker = source.getEntity();
            if (attacker instanceof Player player && player.getAbilities().instabuild) {
               instantBreak = true;
            }

            if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
               instantBreak = true;
            }

            if (instantBreak || newDamage > 40.0F) {
               if (attacker instanceof Player player && player.getAbilities().instabuild) {
                  this.remove(RemovalReason.KILLED);
               } else {
                  this.destroyCart(source);
               }

               return true;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected void destroyCart(@Nullable DamageSource source) {
      this.setPulling(null);
      if (this.isVehicle()) {
         this.ejectPassengers();
      }

      this.remove(RemovalReason.KILLED);
      if (!this.level().isClientSide) {
         this.spawnAtLocation(this.getCartItemStack().copy());
      }
   }

   @Nullable
   public Player getCartDriver() {
      if (this.getPassengers().isEmpty()) {
         return null;
      }

      Entity passenger = (Entity)this.getPassengers().get(0);
      return passenger instanceof Player player ? player : null;
   }

   public float getWheelRotation(float partialTick) {
      return Mth.lerp(partialTick, this.prevWheelRotation, this.wheelRotation);
   }

   protected float getWheelRadius() {
      return 0.5F;
   }

   public float getCartSpeed() {
      return (Float)this.entityData.get(DATA_SPEED);
   }

   public void setCartSpeed(float speed) {
      this.entityData.set(DATA_SPEED, speed);
   }

   public int getHurtTime() {
      return (Integer)this.entityData.get(DATA_HURT_TIME);
   }

   public int getHurtDir() {
      return (Integer)this.entityData.get(DATA_HURT_DIR);
   }

   public float getDamage() {
      return (Float)this.entityData.get(DATA_DAMAGE);
   }

   public void pulledPostTick() {
      if (this.pulling != null) {
         Vec3 targetVec = this.getRelativeTargetVec();
         this.handleRotation(targetVec);

         while (this.getYRot() - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
         }

         while (this.getYRot() - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
         }

         if (this.pulling.onGround()) {
            targetVec = new Vec3(targetVec.x, 0.0, targetVec.z);
         }

         double targetVecLength = targetVec.length();
         double r = 0.2;
         double relativeSpacing = Math.max(this.spacing + 0.5 * this.pulling.getBbWidth(), 1.0);
         double diff = targetVecLength - relativeSpacing;
         Vec3 move;
         if (Math.abs(diff) < r) {
            move = this.getDeltaMovement();
         } else {
            move = this.getDeltaMovement().add(targetVec.subtract(targetVec.normalize().scale(relativeSpacing + r * Math.signum(diff))));
         }

         this.setOnGround(true);
         this.move(MoverType.SELF, move);
         if (this.isAlive()) {
            if (!this.level().isClientSide) {
               targetVec = this.getRelativeTargetVec();
               if (targetVec.length() > relativeSpacing + 1.0) {
                  this.setPulling(null);
                  return;
               }

               this.updateWheelRotationFromMovement();
            }
         }
      }
   }

   public boolean shouldStopPulledTick() {
      if (this.isAlive() && this.getPulling() != null && this.getPulling().isAlive() && !this.getPulling().isPassenger()) {
         if (!this.level().isClientSide && this.shouldRemovePulling()) {
            this.setPulling(null);
            return true;
         } else {
            return false;
         }
      } else {
         if (this.pulling instanceof Player) {
            this.setPulling(null);
         } else {
            this.pulling = null;
         }

         return true;
      }
   }

   protected boolean shouldRemovePulling() {
      if (this.horizontalCollision && this.pulling != null) {
         Vec3 start = new Vec3(this.getX(), this.getY() + this.getBbHeight(), this.getZ());
         Vec3 end = new Vec3(this.pulling.getX(), this.pulling.getY() + this.pulling.getBbHeight() / 2.0F, this.pulling.getZ());
         BlockHitResult result = this.level().clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, this));
         if (result.getType() == Type.BLOCK) {
            this.setPulling(null);
            return true;
         }
      }

      return false;
   }

   protected Vec3 getRelativeTargetVec() {
      double x = this.pulling.getX() - this.getX();
      double y = this.pulling.getY() - this.getY();
      double z = this.pulling.getZ() - this.getZ();
      float yaw = (float)Math.toRadians(this.pulling.getYRot());
      float nx = -Mth.sin(yaw);
      float nz = Mth.cos(yaw);
      double r = 0.2;
      return new Vec3(x + nx * r, y, z + nz * r);
   }

   protected void handleRotation(Vec3 target) {
      this.setYRot(Mth.wrapDegrees((float)Math.toDegrees(-Mth.atan2(target.x, target.z))));
      this.setXRot(Mth.wrapDegrees((float)Math.toDegrees(-Mth.atan2(target.y, Mth.sqrt((float)(target.x * target.x + target.z * target.z))))));
   }

   private void attemptReattach() {
      if (this.level().isClientSide) {
         if (this.pullingId != -1) {
            Entity entity = this.level().getEntity(this.pullingId);
            if (entity != null && entity.isAlive()) {
               this.setPulling(entity);
            }
         }
      } else {
         if (this.pullingUuid != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.pullingUuid);
            if (entity != null && entity.isAlive()) {
               this.setPulling(entity);
            }
         }
      }
   }

   private void tickLerp() {
      if (this.lerpSteps > 0) {
         double dx = (this.lerpX - this.getX()) / this.lerpSteps;
         double dy = (this.lerpY - this.getY()) / this.lerpSteps;
         double dz = (this.lerpZ - this.getZ()) / this.lerpSteps;
         this.setYRot((float)(this.getYRot() + Mth.wrapDegrees(this.lerpYaw - this.getYRot()) / this.lerpSteps));
         this.setXRot((float)(this.getXRot() + (this.lerpPitch - this.getXRot()) / this.lerpSteps));
         this.lerpSteps--;
         this.setOnGround(true);
         this.move(MoverType.SELF, new Vec3(dx, dy, dz));
         this.setRot(this.getYRot(), this.getXRot());
      }
   }

   public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements) {
      this.lerpX = x;
      this.lerpY = y;
      this.lerpZ = z;
      this.lerpYaw = yaw;
      this.lerpPitch = pitch;
      this.lerpSteps = posRotationIncrements;
   }

   public Entity getPulling() {
      return this.pulling;
   }

   public void setPulling(Entity entity) {
      if (this.level().isClientSide) {
         if (entity == null) {
            this.pulling = null;
            this.pullingId = -1;
            this.pullingUuid = null;
         } else {
            this.pulling = entity;
            this.pullingId = entity.getId();
            this.pullingUuid = entity.getUUID();
         }
      } else {
         CartWorldData worldData = CartWorldData.get(this.level());
         if (this.pulling != null) {
            worldData.removePullingByCart(this);
         }

         if (entity == null) {
            this.pulling = null;
            this.pullingUuid = null;
            this.setCartSpeed(0.0F);
            PacketHandler.sendCartPullingSync(this, -1);
         } else if (!worldData.canAttach(entity, this)) {
            this.pulling = null;
            this.pullingUuid = null;
            this.setCartSpeed(0.0F);
            PacketHandler.sendCartPullingSync(this, -1);
         } else {
            this.pulling = entity;
            this.pullingUuid = entity.getUUID();
            worldData.addPulling(this);
            PacketHandler.sendCartPullingSync(this, entity.getId());
         }
      }
   }

   protected void clientResolvePullingIfPossible() {
      if (this.level().isClientSide) {
         if (this.pulling == null) {
            if (this.pullingId >= 0) {
               Entity entity = this.level().getEntity(this.pullingId);
               if (entity != null && entity.isAlive()) {
                  this.pulling = entity;
               }
            }
         }
      }
   }

   private void updateWheelRotationFromMovement() {
      double deltaX;
      double deltaZ;
      if (this.pulling instanceof AbstractHorse) {
         Vec3 pullerDelta = this.pulling.getDeltaMovement();
         float pullerYawRad = this.pulling.getYRot() * (float) (Math.PI / 180.0);
         float pullerForwardX = -Mth.sin(pullerYawRad);
         float pullerForwardZ = Mth.cos(pullerYawRad);
         double forwardSpeed = pullerDelta.x * pullerForwardX + pullerDelta.z * pullerForwardZ;
         float cartYawRad = this.getYRot() * (float) (Math.PI / 180.0);
         deltaX = -Mth.sin(cartYawRad) * forwardSpeed;
         deltaZ = Mth.cos(cartYawRad) * forwardSpeed;
      } else {
         deltaX = this.getX() - this.xOld;
         deltaZ = this.getZ() - this.zOld;
      }

      float distance = Mth.sqrt((float)(deltaX * deltaX + deltaZ * deltaZ));
      if (!(distance < 1.0E-5F) && !(distance > 2.0F)) {
         float clampedDistance = Math.min(distance, 0.35F);
         float cartYawRadians = (float)Math.toRadians(this.getYRot());
         float forwardX = -Mth.sin(cartYawRadians);
         float forwardZ = Mth.cos(cartYawRadians);
         float forwardDot = (float)(deltaX * forwardX + deltaZ * forwardZ);
         float direction = forwardDot >= 0.0F ? -1.0F : 1.0F;
         float wheelRadius = this.getWheelRadius();
         float deltaRotation = direction * (clampedDistance / wheelRadius);
         this.wheelRotation += deltaRotation;
         this.entityData.set(DATA_WHEEL_ROTATION, this.wheelRotation);
      }
   }

   protected abstract ItemStack getCartItemStack();
}
