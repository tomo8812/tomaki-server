package net.satisfy.farm_and_charm.client.util;

import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;

public final class CartWheelUtil {
   private static final Int2ObjectOpenHashMap<CartWheelUtil.PullSoundInstance> ACTIVE_SOUNDS = new Int2ObjectOpenHashMap();
   private static final Int2FloatOpenHashMap MOVE_ACCUMULATOR = new Int2FloatOpenHashMap();

   private CartWheelUtil() {
   }

   public static List<CartWheelUtil.CartWheel> createDefaultWheels(AbstractCartEntity cart) {
      return Arrays.asList(new CartWheelUtil.CartWheel(cart, 0.9F), new CartWheelUtil.CartWheel(cart, -0.9F));
   }

   public static void tickCart(AbstractCartEntity cart, List<CartWheelUtil.CartWheel> wheels, SoundEvent pullSound) {
      if (cart.level().isClientSide) {
         tickPullSound(cart, pullSound);

         for (CartWheelUtil.CartWheel wheel : wheels) {
            wheel.tickClient();
         }
      }
   }

   public static void stopCart(AbstractCartEntity cart) {
      CartWheelUtil.PullSoundInstance sound = (CartWheelUtil.PullSoundInstance)ACTIVE_SOUNDS.get(cart.getId());
      if (sound != null) {
         sound.requestFadeOut();
      }

      MOVE_ACCUMULATOR.remove(cart.getId());
   }

   private static void tickPullSound(AbstractCartEntity cart, SoundEvent soundEvent) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null && cart.isAlive()) {
         CartWheelUtil.PullSoundInstance existing = (CartWheelUtil.PullSoundInstance)ACTIVE_SOUNDS.get(cart.getId());
         if (cart.getPulling() == null) {
            if (existing != null) {
               existing.requestFadeOut();
            }

            MOVE_ACCUMULATOR.remove(cart.getId());
         } else {
            double dx = cart.getX() - cart.xOld;
            double dz = cart.getZ() - cart.zOld;
            float step = Mth.sqrt((float)(dx * dx + dz * dz));
            if (step < 1.0E-4F) {
               if (existing != null) {
                  existing.requestFadeOut();
               }
            } else {
               float moved = MOVE_ACCUMULATOR.get(cart.getId());
               moved += step;
               MOVE_ACCUMULATOR.put(cart.getId(), moved);
               if (moved < 1.0F) {
                  if (existing != null) {
                     existing.requestFadeOut();
                  }
               } else {
                  if (existing != null && !existing.isStopped()) {
                     existing.ensureActive();
                  } else {
                     CartWheelUtil.PullSoundInstance sound = new CartWheelUtil.PullSoundInstance(cart, soundEvent);
                     ACTIVE_SOUNDS.put(cart.getId(), sound);
                     minecraft.getSoundManager().play(sound);
                  }
               }
            }
         }
      } else {
         stopCart(cart);
      }
   }

   private static void spawnWheelParticles(AbstractCartEntity cart, double wheelX, double wheelZ, double velocityX, double velocityZ, float distanceTravelled) {
      if (!(distanceTravelled < 0.02F)) {
         double groundY = cart.getBoundingBox().minY - 0.05;
         BlockPos blockPos = BlockPos.containing(wheelX, groundY, wheelZ);
         BlockState blockState = cart.level().getBlockState(blockPos);
         if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
            int count = Mth.clamp((int)(distanceTravelled * 70.0F), 1, 5);
            double particleY = cart.getBoundingBox().minY + 0.02;

            for (int i = 0; i < count; i++) {
               double px = wheelX + (cart.getRandom().nextDouble() - 0.5) * 0.08;
               double pz = wheelZ + (cart.getRandom().nextDouble() - 0.5) * 0.08;
               double vx = velocityX * 0.12;
               double vz = velocityZ * 0.12;
               double vy = 0.02 + cart.getRandom().nextDouble() * 0.03;
               cart.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), px, particleY, pz, vx, vy, vz);
            }
         }
      }
   }

   public static void stopCartById(int cartId) {
      CartWheelUtil.PullSoundInstance sound = (CartWheelUtil.PullSoundInstance)ACTIVE_SOUNDS.get(cartId);
      if (sound != null) {
         sound.requestFadeOut();
         ACTIVE_SOUNDS.remove(cartId);
      }

      MOVE_ACCUMULATOR.remove(cartId);
   }

   public static final class CartWheel {
      private final AbstractCartEntity cart;
      private final float offsetX;
      private final float offsetZ;
      private final float circumference;
      private float rotation;
      private float rotationIncrement;
      private double x;
      private double z;
      private double prevX;
      private double prevZ;

      public CartWheel(AbstractCartEntity cart, float offsetX) {
         this(cart, offsetX, 0.0F, (float) (Math.PI * 5.0 / 4.0));
      }

      public CartWheel(AbstractCartEntity cart, float offsetX, float offsetZ, float circumference) {
         this.cart = cart;
         this.offsetX = offsetX;
         this.offsetZ = offsetZ;
         this.circumference = circumference;
         this.x = this.prevX = cart.getX();
         this.z = this.prevZ = cart.getZ();
      }

      public void tickClient() {
         this.rotation = this.rotation + this.rotationIncrement;
         this.prevX = this.x;
         this.prevZ = this.z;
         float yaw = (float)Math.toRadians(this.cart.getYRot());
         float forwardX = -Mth.sin(yaw);
         float forwardZ = Mth.cos(yaw);
         this.x = this.cart.getX() + forwardX * this.offsetZ - forwardZ * this.offsetX;
         this.z = this.cart.getZ() + forwardZ * this.offsetZ + forwardX * this.offsetX;
         double dx = this.x - this.prevX;
         double dz = this.z - this.prevZ;
         float distance = Mth.sqrt((float)(dx * dx + dz * dz));
         if (distance > 0.02F) {
            CartWheelUtil.spawnWheelParticles(this.cart, this.x, this.z, dx, dz, distance);
         }

         float forward = Mth.sign((float)(dx * forwardX + dz * forwardZ));
         this.rotationIncrement = forward * distance * this.circumference * 0.2F;
      }

      public float getRotation() {
         return this.rotation;
      }
   }

   private static final class PullSoundInstance extends AbstractTickableSoundInstance {
      private static final int FADE_TICKS = 15;
      private final AbstractCartEntity cart;
      private boolean fadingOut;
      private int fadeTicksLeft;

      private PullSoundInstance(AbstractCartEntity cart, SoundEvent sound) {
         super(sound, SoundSource.NEUTRAL, cart.getRandom());
         this.cart = cart;
         this.looping = true;
         this.delay = 0;
         this.volume = 0.2F;
         this.pitch = 1.4F;
         this.fadingOut = false;
         this.fadeTicksLeft = 0;
         this.x = cart.getX();
         this.y = cart.getY();
         this.z = cart.getZ();
      }

      public void tick() {
         if (!this.cart.isAlive()) {
            this.requestFadeOut();
         }

         this.x = this.cart.getX();
         this.y = this.cart.getY();
         this.z = this.cart.getZ();
         if (this.fadingOut) {
            if (this.fadeTicksLeft <= 0) {
               this.volume = 0.0F;
               this.stop();
            } else {
               float t = this.fadeTicksLeft / 15.0F;
               this.volume = 0.2F * t;
               this.fadeTicksLeft--;
            }
         } else if (this.cart.getPulling() == null) {
            this.requestFadeOut();
         } else {
            double dx = this.cart.getX() - this.cart.xOld;
            double dz = this.cart.getZ() - this.cart.zOld;
            float speed = Mth.sqrt((float)(dx * dx + dz * dz));
            if (speed < 1.0E-4F) {
               this.requestFadeOut();
            } else {
               float pitchVariation = 0.1F + this.cart.getRandom().nextFloat() * 0.4F;
               this.pitch = 1.4F + pitchVariation;
               this.volume = 0.2F;
            }
         }
      }

      private void ensureActive() {
         this.fadingOut = false;
         this.fadeTicksLeft = 0;
      }

      private void requestFadeOut() {
         if (!this.fadingOut && !this.isStopped()) {
            this.fadingOut = true;
            this.fadeTicksLeft = 15;
         }
      }
   }
}
