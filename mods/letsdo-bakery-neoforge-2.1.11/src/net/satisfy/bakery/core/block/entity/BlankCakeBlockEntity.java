package net.satisfy.bakery.core.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.bakery.core.recipe.CakeAnimation;
import net.satisfy.bakery.core.recipe.Filling;
import net.satisfy.bakery.core.registry.EntityTypeRegistry;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.Nullable;

public class BlankCakeBlockEntity extends BlockEntity {
   public static final int MAX_SHELLS = 4;
   private CakeAnimation animation = CakeAnimation.NONE;
   private long animationStart;
   private int animationDuration;
   private int presses;
   private int pressesNeeded;
   private int pressesBase;
   private boolean perfectBonus;
   @Nullable
   private Filling filling;
   private final NonNullList<ItemStack> shells = NonNullList.withSize(4, ItemStack.EMPTY);
   private final List<ItemStack> output = new ArrayList<>();
   private long fillTime = -100L;
   @Nullable
   private BlockState pendingState;
   @Nullable
   private UUID worker;

   public BlankCakeBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.BLANK_CAKE_BLOCK_ENTITY.get(), pos, state);
   }

   public CakeAnimation getAnimation() {
      return this.animation;
   }

   public long getAnimationStart() {
      return this.animationStart;
   }

   public int getAnimationDuration() {
      return Math.max(1, this.animationDuration);
   }

   public float getProgress(long gameTime, float partialTick) {
      return Math.min(1.0F, Math.max(0.0F, ((float)(gameTime - this.animationStart) + partialTick) / this.getAnimationDuration()));
   }

   public int getPresses() {
      return this.presses;
   }

   public int getPressesNeeded() {
      return this.pressesNeeded;
   }

   public boolean canPress(long gameTime) {
      return this.animation.isPressing() && this.presses < this.pressesNeeded && !this.isTooFast(gameTime);
   }

   public boolean isTooFast(long gameTime) {
      return this.presses > 0 && gameTime - this.animationStart < 6L;
   }

   public void squish(long gameTime) {
      if (this.pressesNeeded < this.pressesBase * 2) {
         this.pressesNeeded++;
      }

      this.animationStart = gameTime;
      this.sync();
   }

   public boolean isPressedFlat() {
      return this.animation.isPressing() && this.presses >= this.pressesNeeded;
   }

   public void press(long gameTime) {
      this.presses++;
      this.animationStart = gameTime;
      this.sync();
   }

   public float pressProgress(long gameTime, float partialTick) {
      float needed = Math.max(1, this.pressesNeeded);
      float before = Math.max(0, this.presses - 1) / needed;
      float after = this.presses / needed;
      return Mth.lerp(CakeAnimation.easeOutCubic(this.getProgress(gameTime, partialTick)), before, after);
   }

   @Nullable
   public Filling getFilling() {
      return this.filling;
   }

   public void setFilling(@Nullable Filling filling) {
      this.filling = filling;
      this.sync();
   }

   public NonNullList<ItemStack> getShells() {
      return this.shells;
   }

   public boolean addShell(ItemStack shell) {
      for (int slot = 0; slot < 4; slot++) {
         if (((ItemStack)this.shells.get(slot)).isEmpty()) {
            this.shells.set(slot, shell.copyWithCount(1));
            this.sync();
            return true;
         }
      }

      return false;
   }

   public int fillShells(ItemStack filled) {
      int count = 0;

      for (int slot = 0; slot < 4; slot++) {
         if (((ItemStack)this.shells.get(slot)).is((Item)ObjectRegistry.CORNET_SHELL.get())) {
            this.shells.set(slot, filled.copyWithCount(1));
            count++;
         }
      }

      if (count > 0) {
         this.fillTime = this.level != null ? this.level.getGameTime() : 0L;
         this.sync();
      }

      return count;
   }

   public long getFillTime() {
      return this.fillTime;
   }

   public List<ItemStack> takeShells() {
      List<ItemStack> taken = this.shells.stream().filter(stack -> !stack.isEmpty()).<ItemStack>map(ItemStack::copy).toList();

      for (int slot = 0; slot < 4; slot++) {
         this.shells.set(slot, ItemStack.EMPTY);
      }

      this.sync();
      return taken;
   }

   public void setOutput(List<ItemStack> stacks) {
      this.output.clear();
      this.output.addAll(stacks);
      this.setChanged();
   }

   public List<ItemStack> takeOutput() {
      List<ItemStack> taken = List.copyOf(this.output);
      this.output.clear();
      return taken;
   }

   public float heightScale(long gameTime, float partialTick) {
      float progress = this.getProgress(gameTime, partialTick);
      if (this.animation == CakeAnimation.ROLL_UP) {
         return 1.0F;
      }

      if (!this.animation.isPressing()) {
         return this.animation.verticalScale(progress, this.getAnimationDuration());
      }

      float needed = Math.max(1, this.pressesNeeded);
      float before = Math.max(0, this.presses - 1) / needed;
      float after = this.presses / needed;
      float pressed = Mth.lerp(CakeAnimation.easeOutCubic(progress), before, after);
      float squash = this.presses > 0 ? Mth.sin(progress * (float) Math.PI) * 0.08F : 0.0F;
      return Math.max(Mth.lerp(pressed, 1.0F, 0.16666667F) * (1.0F - squash), 0.02F);
   }

   public void setPerfectBonus(boolean perfectBonus) {
      this.perfectBonus = perfectBonus;
      this.setChanged();
   }

   public boolean hasPerfectBonus() {
      return this.perfectBonus;
   }

   @Nullable
   public BlockState getPendingState() {
      return this.pendingState;
   }

   @Nullable
   public UUID getWorker() {
      return this.worker;
   }

   public void start(CakeAnimation newAnimation, long gameTime, BlockState result) {
      this.start(newAnimation, gameTime, result, null);
   }

   public void start(CakeAnimation newAnimation, long gameTime, BlockState result, @Nullable UUID newWorker) {
      this.animation = newAnimation;
      this.animationDuration = newAnimation.configuredDuration();
      this.presses = 0;
      this.pressesNeeded = newAnimation.isPressing() ? newAnimation.configuredPresses() : 0;
      this.pressesBase = this.pressesNeeded;
      this.perfectBonus = false;
      this.worker = newWorker;
      this.animationStart = gameTime;
      this.pendingState = result;
      this.sync();
   }

   private void sync() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.animation = CakeAnimation.byName(tag.getString("Animation"));
      this.animationStart = tag.getLong("AnimationStart");
      this.animationDuration = tag.contains("AnimationDuration") ? tag.getInt("AnimationDuration") : this.animation.duration();
      this.presses = tag.getInt("Presses");
      this.pressesNeeded = tag.getInt("PressesNeeded");
      this.pressesBase = tag.contains("PressesBase") ? tag.getInt("PressesBase") : this.pressesNeeded;
      this.perfectBonus = tag.getBoolean("PerfectBonus");
      this.fillTime = tag.contains("FillTime") ? tag.getLong("FillTime") : -100L;
      this.filling = tag.contains("Filling") ? (Filling)Filling.CODEC.parse(NbtOps.INSTANCE, tag.get("Filling")).result().orElse(null) : null;

      for (int slot = 0; slot < 4; slot++) {
         this.shells.set(slot, ItemStack.EMPTY);
      }

      ListTag shellList = tag.getList("Shells", 10);

      for (int slot = 0; slot < Math.min(4, shellList.size()); slot++) {
         this.shells.set(slot, ItemStack.parseOptional(provider, shellList.getCompound(slot)));
      }

      this.output.clear();

      for (Tag entry : tag.getList("Output", 10)) {
         ItemStack.parse(provider, entry).ifPresent(this.output::add);
      }

      this.worker = tag.hasUUID("Worker") ? tag.getUUID("Worker") : null;
      this.pendingState = tag.contains("PendingState")
         ? NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), tag.getCompound("PendingState"))
         : null;
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      tag.putString("Animation", this.animation.getSerializedName());
      tag.putLong("AnimationStart", this.animationStart);
      tag.putInt("AnimationDuration", this.animationDuration);
      tag.putInt("Presses", this.presses);
      tag.putInt("PressesNeeded", this.pressesNeeded);
      tag.putInt("PressesBase", this.pressesBase);
      tag.putBoolean("PerfectBonus", this.perfectBonus);
      tag.putLong("FillTime", this.fillTime);
      if (this.filling != null) {
         Filling.CODEC.encodeStart(NbtOps.INSTANCE, this.filling).result().ifPresent(encoded -> tag.put("Filling", encoded));
      }

      ListTag shellList = new ListTag();
      this.shells.forEach(stack -> shellList.add(stack.saveOptional(provider)));
      tag.put("Shells", shellList);
      ListTag outputList = new ListTag();
      this.output.forEach(stack -> outputList.add(stack.save(provider)));
      tag.put("Output", outputList);
      if (this.worker != null) {
         tag.putUUID("Worker", this.worker);
      }

      if (this.pendingState != null) {
         tag.put("PendingState", NbtUtils.writeBlockState(this.pendingState));
      }
   }

   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = super.getUpdateTag(provider);
      this.saveAdditional(tag, provider);
      return tag;
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
