package net.satisfy.candlelight.core.block.entity;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.candlelight.core.block.TypewriterBlock;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;
import net.satisfy.foundation.registry.FoundationParticles;
import org.jetbrains.annotations.Nullable;

public class TypewriterEntity extends BlockEntity {
   public static final String PAPER_KEY = "paper";
   ItemStack paper = ItemStack.EMPTY;
   private static final RandomSource RANDOM = RandomSource.create();
   private final long[] keyTimes = new long[7];
   private long spaceTime;
   private long enterTime;
   private long lineFeedTime;
   private long shakeTime;
   private float carriageFrom;
   private float carriageTo;
   private long carriageTime;
   private long carriageDuration = 90L;
   private int mashColumn;
   public static final int KEY_COUNT = 7;
   private static final long PRESS_MS = 140L;
   private static final long ADVANCE_MS = 90L;
   private static final long RETURN_MS = 380L;
   private static final long SHAKE_MS = 120L;
   private static final int INK_COLOR = -15001052;
   public static final int EVENT_MASH = 1;
   public static final int MASH_LINE_LENGTH = 24;
   public static final int EVENT_MASH_RETURN = 25;

   public TypewriterEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.TYPE_WRITER_BLOCK_ENTITY.get(), pos, state);
   }

   public int advanceMashColumn(int steps) {
      this.mashColumn += steps;
      if (this.mashColumn >= 24) {
         this.mashColumn = 0;
         return -1;
      } else {
         return this.mashColumn;
      }
   }

   public ItemStack getPaper() {
      return this.paper;
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      if (!this.paper.isEmpty()) {
         this.writePaper(compoundTag, this.paper, provider);
      }
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      this.paper = this.readPaper(compoundTag, provider);
   }

   public void writePaper(CompoundTag nbt, ItemStack stack, Provider provider) {
      if (stack != null && !stack.isEmpty()) {
         CompoundTag tag = new CompoundTag();
         stack.save(provider, tag);
         nbt.put("paper", tag);
      }
   }

   public ItemStack readPaper(CompoundTag nbt, Provider provider) {
      if (nbt.contains("paper")) {
         CompoundTag tag = nbt.getCompound("paper");
         if (!tag.isEmpty()) {
            return ItemStack.parseOptional(provider, tag);
         }
      }

      return ItemStack.EMPTY;
   }

   @Nullable
   public Packet<ClientGamePacketListener> toUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag toInitialChunkDataNbt(Provider provider) {
      return this.saveWithoutMetadata(provider);
   }

   public void addPaper(ItemStack itemStack) {
      this.paper = itemStack;
      this.setChanged();
   }

   public void removePaper() {
      this.paper = ItemStack.EMPTY;
      this.setChanged();
   }

   public void triggerSpace() {
      long now = Util.getMillis();
      this.spaceTime = now;
      this.shakeTime = now;
   }

   public void triggerEnter() {
      long now = Util.getMillis();
      this.enterTime = now;
      this.lineFeedTime = now;
      this.shakeTime = now;
   }

   public void triggerKeyBounce() {
      this.triggerKey(RANDOM.nextInt(7));
   }

   public void triggerKey(int index) {
      long now = Util.getMillis();
      this.keyTimes[Math.floorMod(index, 7)] = now;
      this.shakeTime = now;
      this.spawnInk();
   }

   private void spawnInk() {
      if (this.level != null && this.level.isClientSide && !(RANDOM.nextFloat() > 0.18F)) {
         Direction facing = this.getBlockState().hasProperty(TypewriterBlock.FACING)
            ? (Direction)this.getBlockState().getValue(TypewriterBlock.FACING)
            : Direction.NORTH;
         double x = this.worldPosition.getX() + 0.5 - facing.getStepX() * 0.05 + (RANDOM.nextDouble() - 0.5) * 0.2;
         double z = this.worldPosition.getZ() + 0.5 - facing.getStepZ() * 0.05 + (RANDOM.nextDouble() - 0.5) * 0.2;
         double y = this.worldPosition.getY() + 0.5;
         this.level
            .addParticle(
               ColorParticleOption.create((ParticleType)FoundationParticles.DYE_SPLASH.get(), -15001052),
               x,
               y,
               z,
               (RANDOM.nextDouble() - 0.5) * 0.04,
               0.03,
               (RANDOM.nextDouble() - 0.5) * 0.04
            );
      }
   }

   public void setLineProgress(float v) {
      float target = Math.max(0.0F, Math.min(1.0F, v));
      if (target != this.carriageTo) {
         this.carriageFrom = this.getCarriage();
         this.carriageTo = target;
         this.carriageTime = Util.getMillis();
         this.carriageDuration = target < this.carriageFrom ? 380L : 90L;
      }
   }

   public float getLineProgress() {
      return this.carriageTo;
   }

   public void snapRoller() {
      this.lineFeedTime = Util.getMillis();
      this.setLineProgress(0.0F);
   }

   public float getCarriage() {
      float t = Math.min(1.0F, (float)(Util.getMillis() - this.carriageTime) / (float)this.carriageDuration);
      float eased = this.carriageDuration == 380L ? 1.0F - (float)Math.pow(1.0F - t, 3.0) : t * (2.0F - t);
      float pos = this.carriageFrom + (this.carriageTo - this.carriageFrom) * eased;
      if (this.carriageDuration == 380L && t > 0.85F) {
         pos -= (float)Math.sin((t - 0.85F) / 0.15F * Math.PI) * 0.02F;
      }

      return pos;
   }

   public float getKeyPress(int index) {
      return pressCurve(this.keyTimes[index]);
   }

   public float getSpacePress() {
      return pressCurve(this.spaceTime);
   }

   public float getEnterPress() {
      return pressCurve(this.enterTime);
   }

   public float getLineFeed() {
      long dt = Util.getMillis() - this.lineFeedTime;
      return dt >= 0L && dt <= 380L ? (float)Math.sin((float)dt / 380.0F * Math.PI) : 0.0F;
   }

   public float getShake() {
      long dt = Util.getMillis() - this.shakeTime;
      if (dt >= 0L && dt <= 120L) {
         float t = (float)dt / 120.0F;
         return (float)Math.sin(t * Math.PI * 3.0) * (1.0F - t);
      } else {
         return 0.0F;
      }
   }

   private static float pressCurve(long time) {
      long dt = Util.getMillis() - time;
      if (dt >= 0L && dt <= 140L) {
         float t = (float)dt / 140.0F;
         if (t < 0.25F) {
            return t / 0.25F;
         }

         float r = (t - 0.25F) / 0.75F;
         return (1.0F - r) * (1.0F - r) * (1.0F + 0.6F * (float)Math.sin(r * Math.PI));
      } else {
         return 0.0F;
      }
   }

   public boolean triggerEvent(int id, int param) {
      if (id >= 1 && id <= 25) {
         if (this.level != null && this.level.isClientSide) {
            for (int i = 0; i < 7; i++) {
               if ((param & 1 << i) != 0) {
                  this.triggerKey(i);
               }
            }

            if ((param & 128) != 0) {
               this.triggerSpace();
            }

            if (id == 25) {
               this.triggerEnter();
               this.snapRoller();
            } else {
               this.setLineProgress((id - 1) / 24.0F);
            }
         }

         return true;
      } else {
         return super.triggerEvent(id, param);
      }
   }
}
