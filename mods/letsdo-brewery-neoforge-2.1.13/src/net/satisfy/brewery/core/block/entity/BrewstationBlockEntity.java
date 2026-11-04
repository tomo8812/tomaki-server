package net.satisfy.brewery.core.block.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.block.BrewWhistleBlock;
import net.satisfy.brewery.core.block.property.BrewMaterial;
import net.satisfy.brewery.core.block.property.Heat;
import net.satisfy.brewery.core.block.property.Liquid;
import net.satisfy.brewery.core.entity.BeerElementalEntity;
import net.satisfy.brewery.core.event.brew_event.BrewEvent;
import net.satisfy.brewery.core.event.brew_event.BrewEvents;
import net.satisfy.brewery.core.event.brew_event.BrewHelper;
import net.satisfy.brewery.core.item.DrinkBlockItem;
import net.satisfy.brewery.core.recipe.BrewingRecipe;
import net.satisfy.brewery.core.registry.BlockStateRegistry;
import net.satisfy.brewery.core.registry.EntityTypeRegistry;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.brewery.core.registry.RecipeTypeRegistry;
import net.satisfy.brewery.core.registry.SoundEventRegistry;
import net.satisfy.brewery.platform.PlatformHelper;
import net.satisfy.foundation.util.ImplementedInventory;
import net.satisfy.foundation.util.LibUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BrewstationBlockEntity extends BlockEntity implements ImplementedInventory, BlockEntityTicker<BrewstationBlockEntity> {
   @NotNull
   private Set<BlockPos> components = new HashSet<>(4);
   private static final int SOUND_DURATION = 60;
   private static final int WATER_PHASE = 200;
   private static final int FADE_TIME = 200;
   private static final int WHISKEY_COLOR = 13666874;
   private static final int BEER_COLOR = 15251747;
   private long brewStart = -1L;
   private float lidAngle = 15.0F;
   private float liquidHeight = -1.0F;
   private boolean started;
   private boolean brewWhiskey;
   private int clientTintStep = -1;
   private long clientParticleTick = -1L;
   private int soundTime;
   private int brewTime;
   private int timeToNextEvent = Integer.MIN_VALUE;
   private final Set<BrewEvent> runningEvents = new HashSet<>();
   private int solved;
   private int totalEvents;
   private int eventQuota = -1;
   private int overflowStarted;
   private int overflowSolved;
   private NonNullList<ItemStack> ingredients;
   private ItemStack beer = ItemStack.EMPTY;
   private final SoundEvent spawnEntitySound = (SoundEvent)SoundEventRegistry.BREWSTATION_PROCESS_FAILED.get();
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !BrewstationBlockEntity.class.desiredAssertionStatus();

   public BrewstationBlockEntity(BlockPos blockPos, BlockState blockState) {
      super((BlockEntityType)EntityTypeRegistry.BREWINGSTATION_BLOCK_ENTITY.get(), blockPos, blockState);
      this.ingredients = NonNullList.withSize(3, ItemStack.EMPTY);
   }

   public void setComponents(BlockPos... components) {
      if (components.length >= 4) {
         this.components.addAll(Arrays.asList(components));
      }
   }

   public ItemInteractionResult addIngredient(ItemStack itemStack) {
      for (int i = 0; i < 3; i++) {
         ItemStack stack = (ItemStack)this.ingredients.get(i);
         if (stack.isEmpty()) {
            this.setItem(i, itemStack.split(1));
            return ItemInteractionResult.SUCCESS;
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   @Nullable
   public ItemStack getBeer() {
      if (this.beer.isEmpty()) {
         return null;
      }

      ItemStack beerStack = this.beer.copy();
      beerStack.setCount(1);
      this.beer.shrink(1);
      if (this.beer.isEmpty() && this.level != null) {
         this.level.setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue(BlockStateRegistry.LIQUID, Liquid.EMPTY));
      }

      this.setChanged();
      return beerStack;
   }

   @Nullable
   public ItemStack peekBeer() {
      if (this.beer.isEmpty()) {
         return null;
      }

      ItemStack beerStack = this.beer.copy();
      beerStack.setCount(1);
      return beerStack;
   }

   @Nullable
   public ItemStack removeIngredient() {
      for (int i = 0; i < 3; i++) {
         ItemStack itemStack = (ItemStack)this.ingredients.get(i);
         if (!itemStack.isEmpty()) {
            this.ingredients.set(i, ItemStack.EMPTY);
            return itemStack;
         }
      }

      return null;
   }

   public void tick(Level level, BlockPos blockPos, BlockState blockState, BrewstationBlockEntity blockEntity) {
      if (!level.isClientSide) {
         BlockPos whistlePos = BrewHelper.getBlock((Block)ObjectRegistry.BREW_WHISTLE.get(), this.components, level);
         if (whistlePos != null && (Boolean)level.getBlockState(whistlePos).getValue(BlockStateRegistry.WHISTLE)) {
            BrewWhistleBlock.playSounds(level, whistlePos);
         }

         if (whistlePos != null && level instanceof ServerLevel serverLevel && level.getGameTime() % 30L == 0L) {
            BlockPos timerPos = BrewHelper.getBlock((Block)ObjectRegistry.BREW_TIMER.get(), this.components, level);
            if (timerPos != null && (Boolean)level.getBlockState(timerPos).getValue(BlockStateRegistry.TIME)) {
               BrewWhistleBlock.puff(serverLevel, whistlePos);
            }
         }

         if (this.beer.isEmpty()) {
            RecipeHolder<BrewingRecipe> active = this.findActiveRecipe(level);
            if (active == null) {
               this.endBrewing();
            } else if (this.brewStart >= 0L || this.started) {
               if (this.brewStart < 0L) {
                  this.brewStart = level.getGameTime() - this.brewTime;
                  this.brewWhiskey = DrinkBlockItem.isBottled(((BrewingRecipe)active.value()).getResultItem(level.registryAccess()));
                  this.setChanged();
               }

               BrewMaterial material = (BrewMaterial)this.getBlockState().getValue(BlockStateRegistry.MATERIAL);
               boolean isNetherite = material == BrewMaterial.NETHERITE;
               boolean eventsEnabled = PlatformHelper.isBrewEventsEnabled();
               if (!isNetherite && eventsEnabled) {
                  if (this.eventQuota < 0) {
                     this.eventQuota = this.computeEventQuota();
                  }
               } else {
                  if (!this.runningEvents.isEmpty()) {
                     BrewHelper.finishEvents(this);
                     this.runningEvents.clear();
                  }

                  this.totalEvents = 0;
                  this.timeToNextEvent = Integer.MIN_VALUE;
                  this.eventQuota = 0;
               }

               if (this.soundTime >= 60) {
                  level.playSound(null, blockPos, (SoundEvent)SoundEventRegistry.BREWSTATION_AMBIENT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                  this.soundTime = 0;
               }

               this.soundTime++;
               if (!isNetherite && eventsEnabled) {
                  if (this.timeToNextEvent == Integer.MIN_VALUE) {
                     this.setTimeToEvent();
                  }

                  BrewHelper.checkRunningEvents(this);
                  int timeLeft = maxBrewTime() - this.brewTime;
                  if (this.brewTime >= maxBrewTime()) {
                     RegistryAccess access = level.registryAccess();
                     this.brew(active.value(), access);
                  } else if (timeLeft >= minTimeForEvent()
                     && this.timeToNextEvent <= 0
                     && this.totalEvents < this.eventQuota
                     && this.runningEvents.size() < BrewEvents.BREW_EVENTS.size()) {
                     BrewEvent event = BrewHelper.getRdmEvent(this);
                     if (event != null) {
                        ResourceLocation eventId = BrewEvents.getId(event);
                        if (eventId != null) {
                           if (eventId.equals(BrewEvents.KETTLE_EVENT)) {
                              this.overflowStarted++;
                           }

                           event.start(this.components, level);
                           this.runningEvents.add(event);
                           this.totalEvents++;
                        }
                     }

                     this.setTimeToEvent();
                  }

                  this.brewTime++;
                  this.timeToNextEvent--;
               } else if (this.brewTime >= maxBrewTime()) {
                  RegistryAccess access = level.registryAccess();
                  this.brew(active.value(), access);
               } else {
                  this.brewTime++;
               }
            }
         }
      }
   }

   private static int maxBrewTime() {
      return PlatformHelper.getBrewTime() * 20;
   }

   private static int minTimeForEvent() {
      return PlatformHelper.getMinBrewEventInterval() * 20;
   }

   private void setTimeToEvent() {
      if (this.level != null) {
         this.timeToNextEvent = getRandomHighNumber(
            this.level.getRandom(), minTimeForEvent(), Math.max(minTimeForEvent(), PlatformHelper.getMaxBrewEventInterval() * 20)
         );
      }
   }

   public static int getRandomHighNumber(RandomSource rnd, int lowerBound, int upperBound) {
      int range = upperBound - lowerBound + 1;
      return upperBound - (int)(Math.pow(rnd.nextDouble(), 1.5) * range);
   }

   private boolean canBrew(@Nullable Recipe<?> recipe) {
      if (recipe != null && this.level != null) {
         BlockState blockState = this.level.getBlockState(this.getBlockPos());
         return recipe instanceof BrewingRecipe brewingRecipe
            && ((BrewMaterial)blockState.getValue(BlockStateRegistry.MATERIAL)).getLevel() >= brewingRecipe.getMaterial().getLevel()
            && blockState.getValue(BlockStateRegistry.LIQUID) != Liquid.EMPTY
            && this.level
                  .getBlockState(BrewHelper.getBlock((Block)ObjectRegistry.BREW_OVEN.get(), this.components, this.level))
                  .getValue(BlockStateRegistry.HEAT)
               != Heat.OFF;
      } else {
         return false;
      }
   }

   private void brew(Recipe<?> recipe, RegistryAccess access) {
      ItemStack resultStack = recipe.getResultItem(access);
      if (resultStack.getItem() instanceof DrinkBlockItem drinkItem) {
         assert this.level != null;
         BrewMaterial material = (BrewMaterial)this.level.getBlockState(this.getBlockPos()).getValue(BlockStateRegistry.MATERIAL);
         int solvedEvents = this.solved;
         int totalBrewEvents = this.totalEvents;
         int quality;
         int count;
         if (material == BrewMaterial.NETHERITE) {
            quality = 3;
            count = 3;
         } else if (!PlatformHelper.isBrewEventsEnabled()) {
            quality = -1;
            count = 2;
         } else {
            int failedEvents = Math.max(0, totalBrewEvents - solvedEvents);
            if (solvedEvents <= 0) {
               quality = 0;
            } else if (solvedEvents >= 4 && failedEvents == 0) {
               quality = 3;
            } else if (solvedEvents >= 3 && failedEvents <= 1) {
               quality = 2;
            } else {
               quality = 1;
            }

            count = solvedEvents == 0 ? 1 : solvedEvents + 1;
         }

         if (quality >= 0) {
            DrinkBlockItem.addQuality(resultStack, quality);
         }

         drinkItem.addCount(resultStack, count);
      }

      this.beer = resultStack;
      this.spawnElementals();
      this.endBrewing();
      if (this.level != null) {
         BlockState blockState = this.level.getBlockState(this.getBlockPos());
         this.level.setBlockAndUpdate(this.getBlockPos(), (BlockState)blockState.setValue(BlockStateRegistry.LIQUID, Liquid.BEER));
         BlockPos ovenPos = BrewHelper.getBlock((Block)ObjectRegistry.BREW_OVEN.get(), this.components, this.level);
         BlockState ovenState = this.level.getBlockState(ovenPos);
         this.level.setBlockAndUpdate(ovenPos, (BlockState)ovenState.setValue(BlockStateRegistry.HEAT, Heat.OFF));
         BlockPos timerPos = BrewHelper.getBlock((Block)ObjectRegistry.BREW_TIMER.get(), this.components, this.level);
         BlockState timerState = this.level.getBlockState(timerPos);
         this.level.setBlockAndUpdate(timerPos, (BlockState)timerState.setValue(BlockStateRegistry.TIME, false));
      }

      for (Ingredient ingredient : recipe.getIngredients()) {
         for (int i = 0; i < 3; i++) {
            ItemStack itemStack = (ItemStack)this.ingredients.get(i);
            if (ingredient.test(itemStack)) {
               this.removeItem(i, 1);
               break;
            }
         }
      }
   }

   private void spawnElementals() {
      if (this.level != null) {
         BlockState state = this.level.getBlockState(this.getBlockPos());
         BrewMaterial material = (BrewMaterial)state.getValue(BlockStateRegistry.MATERIAL);
         boolean overflowUnresolved = this.overflowStarted > this.overflowSolved;
         int failed = Math.max(0, this.totalEvents - this.solved);
         boolean highFailRate = this.totalEvents > 0 && failed * 2 > this.totalEvents;
         int count;
         if (highFailRate) {
            count = this.level.getRandom().nextInt(2, 4);
         } else if (overflowUnresolved) {
            count = material == BrewMaterial.COPPER ? 1 : 2;
         } else {
            count = 0;
         }

         if (count > 0 && PlatformHelper.isBeerElementalsEnabled()) {
            BlockPos base = BrewHelper.getBlock((Block)ObjectRegistry.BREW_OVEN.get(), this.components, this.level);
            if (base != null) {
               for (int n = 0; n < count; n++) {
                  BeerElementalEntity e = new BeerElementalEntity((EntityType<? extends Monster>)EntityTypeRegistry.BEER_ELEMENTAL.get(), this.level);
                  double ox = this.level.getRandom().nextInt(-1, 2) + 0.5;
                  double oz = this.level.getRandom().nextInt(-1, 2) + 0.5;
                  e.setPos(base.getX() + ox, base.getY() + 1, base.getZ() + oz);
                  e.setHealth(20.0F);
                  this.level.addFreshEntity(e);
                  this.level.playSound(null, e.blockPosition(), this.spawnEntitySound, SoundSource.BLOCKS, 1.0F, 1.0F);
               }

               this.overflowStarted = 0;
               this.overflowSolved = 0;
            }
         }
      }
   }

   public void onEventFinished(BrewEvent event, boolean success) {
      ResourceLocation id = BrewEvents.getId(event);
      if (id != null) {
         if (id.equals(BrewEvents.KETTLE_EVENT) && success) {
            this.overflowSolved++;
         }
      }
   }

   private int computeEventQuota() {
      BrewMaterial material = (BrewMaterial)this.getBlockState().getValue(BlockStateRegistry.MATERIAL);
      assert this.level != null;
      RandomSource rnd = this.level.getRandom();

      return switch (material) {
         case WOOD -> rnd.nextInt(8, 13);
         case COPPER -> rnd.nextInt(4, 7);
         case NETHERITE -> rnd.nextInt(1, 3);
      };
   }

   public boolean tryStart() {
      if (this.level != null && this.beer.isEmpty() && this.brewStart < 0L && !this.started && this.findActiveRecipe(this.level) != null) {
         this.started = true;
         this.setChanged();
         return true;
      } else {
         return false;
      }
   }

   public boolean isStarted() {
      return this.started;
   }

   public void endBrewing() {
      this.started = false;
      if (this.brewStart >= 0L) {
         this.brewStart = -1L;
         this.setChanged();
      }

      BrewHelper.finishEvents(this);
      this.brewTime = 0;
      this.solved = 0;
      this.totalEvents = 0;
      this.soundTime = 60;
      this.timeToNextEvent = Integer.MIN_VALUE;
      this.eventQuota = -1;
      this.overflowStarted = 0;
      this.overflowSolved = 0;
   }

   public boolean isPartOf(BlockPos blockPos) {
      return this.components.contains(blockPos);
   }

   public void saveAdditional(CompoundTag compoundTag, Provider provider) {
      if (!this.components.isEmpty()) {
         LibUtil.putBlockPoses(compoundTag, this.components);
      }

      ContainerHelper.saveAllItems(compoundTag, this.ingredients, provider);
      if (!this.beer.isEmpty()) {
         compoundTag.put("beer", this.beer.save(provider, new CompoundTag()));
      }

      compoundTag.putLong("brewStart", this.brewStart);
      compoundTag.putBoolean("brewWhiskey", this.brewWhiskey);
      compoundTag.putInt("solved", this.solved);
      compoundTag.putBoolean("started", this.started);
      compoundTag.putInt("brewTime", this.brewTime);
      compoundTag.putInt("totalEvents", this.totalEvents);
      compoundTag.putInt("timeToNextEvent", this.timeToNextEvent);
      compoundTag.putInt("eventQuota", this.eventQuota);
      compoundTag.putInt("overflowStarted", this.overflowStarted);
      compoundTag.putInt("overflowSolved", this.overflowSolved);
      BrewHelper.saveAdditional(this, compoundTag);
   }

   public void loadAdditional(CompoundTag compoundTag, Provider provider) {
      this.components = LibUtil.readBlockPoses(compoundTag);
      this.ingredients = NonNullList.withSize(3, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compoundTag, this.ingredients, provider);
      this.beer = compoundTag.contains("beer") ? ItemStack.parseOptional(provider, compoundTag.getCompound("beer")) : ItemStack.EMPTY;
      this.brewStart = compoundTag.contains("brewStart") ? compoundTag.getLong("brewStart") : -1L;
      this.brewWhiskey = compoundTag.getBoolean("brewWhiskey");
      this.solved = compoundTag.getInt("solved");
      this.started = compoundTag.getBoolean("started");
      this.brewTime = compoundTag.getInt("brewTime");
      this.totalEvents = compoundTag.getInt("totalEvents");
      this.timeToNextEvent = compoundTag.getInt("timeToNextEvent");
      this.eventQuota = compoundTag.getInt("eventQuota");
      this.overflowStarted = compoundTag.getInt("overflowStarted");
      this.overflowSolved = compoundTag.getInt("overflowSolved");
      BrewHelper.load(this, compoundTag);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag compoundTag = new CompoundTag();
      this.saveAdditional(compoundTag, provider);
      return compoundTag;
   }

   public void setChanged() {
      super.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
      }
   }

   public int getLiquidColor(int waterColor) {
      if (!this.beer.isEmpty()) {
         return DrinkBlockItem.isBottled(this.beer) ? 13666874 : 15251747;
      }

      if (this.brewStart >= 0L && this.level != null) {
         long elapsed = this.level.getGameTime() - this.brewStart - 200L;
         if (elapsed <= 0L) {
            return waterColor;
         }

         float progress = Math.min(1.0F, (float)elapsed / 200.0F);
         return mix(waterColor, this.brewWhiskey ? 13666874 : 15251747, progress);
      } else {
         return waterColor;
      }
   }

   private static int mix(int from, int to, float t) {
      int r = Math.round((from >> 16 & 0xFF) * (1.0F - t) + (to >> 16 & 0xFF) * t);
      int g = Math.round((from >> 8 & 0xFF) * (1.0F - t) + (to >> 8 & 0xFF) * t);
      int b = Math.round((from & 0xFF) * (1.0F - t) + (to & 0xFF) * t);
      return r << 16 | g << 8 | b;
   }

   public float updateLiquidHeight(float target, float speed) {
      this.liquidHeight = this.liquidHeight < 0.0F ? target : this.liquidHeight + (target - this.liquidHeight) * speed;
      return this.liquidHeight;
   }

   public float updateLid(float target, float speed) {
      this.lidAngle = this.lidAngle + (target - this.lidAngle) * speed;
      return this.lidAngle;
   }

   public float getBrewProgress() {
      return this.brewStart >= 0L && this.level != null
         ? Math.min(1.0F, Math.max(0.0F, (float)(this.level.getGameTime() - this.brewStart) / maxBrewTime()))
         : -1.0F;
   }

   public boolean isBrewingClient() {
      return this.brewStart >= 0L;
   }

   public int getClientTintStep() {
      return this.clientTintStep;
   }

   public void setClientTintStep(int step) {
      this.clientTintStep = step;
   }

   public long getClientParticleTick() {
      return this.clientParticleTick;
   }

   public void setClientParticleTick(long tick) {
      this.clientParticleTick = tick;
   }

   public void growSolved() {
      this.solved++;
   }

   public Set<BrewEvent> getRunningEvents() {
      return this.runningEvents;
   }

   @NotNull
   public Set<BlockPos> getComponents() {
      return this.components;
   }

   public List<ItemStack> getIngredient() {
      return this.ingredients;
   }

   public NonNullList<ItemStack> getItems() {
      return this.ingredients;
   }

   public boolean stillValid(Player player) {
      return this.level.getBlockEntity(this.worldPosition) != this
         ? false
         : player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
   }

   @Nullable
   private RecipeHolder<BrewingRecipe> findActiveRecipe(Level level) {
      for (RecipeHolder<BrewingRecipe> holder : level.getRecipeManager().getAllRecipesFor((RecipeType)RecipeTypeRegistry.BREWING_RECIPE_TYPE.get())) {
         BrewingRecipe r = (BrewingRecipe)holder.value();
         if (this.canBrew(r) && this.ingredientsMatch(r)) {
            return holder;
         }
      }

      return null;
   }

   private boolean ingredientsMatch(BrewingRecipe recipe) {
      List<Ingredient> req = recipe.getIngredients();
      boolean[] used = new boolean[this.ingredients.size()];
      int matched = 0;

      for (Ingredient ing : req) {
         boolean found = false;

         for (int i = 0; i < this.ingredients.size(); i++) {
            if (!used[i] && ing.test((ItemStack)this.ingredients.get(i))) {
               used[i] = true;
               found = true;
               matched++;
               break;
            }
         }

         if (!found) {
            return false;
         }
      }

      return matched == req.size();
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void tick(Level param1, BlockPos nullx, BlockState nullxx, BlockEntity nullxxx) {
      this.tick(var1, nullx, nullxx, (BrewstationBlockEntity)nullxxx);
   }
}
