package net.satisfy.bakery.core.block;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.bakery.Bakery;
import net.satisfy.bakery.core.block.entity.BakerStationBlockEntity;
import net.satisfy.bakery.core.block.entity.BlankCakeBlockEntity;
import net.satisfy.bakery.core.recipe.BlankCakeInteractionInput;
import net.satisfy.bakery.core.recipe.BlankCakeInteractionRecipe;
import net.satisfy.bakery.core.recipe.BlankCakeStage;
import net.satisfy.bakery.core.recipe.CakeAnimation;
import net.satisfy.bakery.core.recipe.Filling;
import net.satisfy.bakery.core.registry.DataComponentRegistry;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import net.satisfy.bakery.core.registry.RecipeTypeRegistry;
import net.satisfy.bakery.core.registry.SoundEventRegistry;
import net.satisfy.bakery.platform.PlatformHelper;
import net.satisfy.foundation.registry.FoundationParticles;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlankCakeBlock extends Block implements EntityBlock {
   private static final TagKey<Item> KNIVES = TagKey.create(Registries.ITEM, Bakery.identifier("knives"));
   private static final int ROLL_SLICES = 5;
   public static final EnumProperty<BlankCakeStage> STAGE = EnumProperty.create("stage", BlankCakeStage.class);
   public static final BooleanProperty ANIMATING = BooleanProperty.create("animating");
   private static final VoxelShape CAKE_SHAPE = Shapes.box(0.0625, 0.0, 0.0625, 0.9375, 0.5, 0.9375);
   private static final VoxelShape BASE_SHAPE = Shapes.empty();
   private static final VoxelShape DOUGH_SHAPE = Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 0.375, 0.8125);
   private static final VoxelShape SHEET_SHAPE = Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 0.8125);
   private static final VoxelShape ROLL_SHAPE = Shapes.box(0.1875, 0.0, 0.625, 0.8125, 0.1875, 0.8125);
   private static final VoxelShape SHELLS_SHAPE = Shapes.box(0.3125, 0.0, 0.3125, 0.6875, 0.0625, 0.6875);
   private static final VoxelShape FLAT_SHAPE = Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 0.8125);
   private static final VoxelShape CUPCAKE_SHAPE = Shapes.or(
      BASE_SHAPE,
      new VoxelShape[]{
         Shapes.box(0.125, 0.0, 0.125, 0.4375, 0.375, 0.4375),
         Shapes.box(0.125, 0.0, 0.5625, 0.4375, 0.375, 0.875),
         Shapes.box(0.5625, 0.0, 0.125, 0.875, 0.375, 0.4375),
         Shapes.box(0.5625, 0.0, 0.5625, 0.875, 0.375, 0.875)
      }
   );
   private static final VoxelShape COOKIE_SHAPE = Shapes.or(
      BASE_SHAPE,
      new VoxelShape[]{
         Shapes.box(0.125, 0.0, 0.125, 0.4375, 0.0625, 0.4375),
         Shapes.box(0.125, 0.0, 0.5625, 0.4375, 0.0625, 0.875),
         Shapes.box(0.5625, 0.0, 0.125, 0.875, 0.0625, 0.4375),
         Shapes.box(0.5625, 0.0, 0.5625, 0.875, 0.0625, 0.875)
      }
   );

   public BlankCakeBlock(Properties settings) {
      super(settings);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(STAGE, BlankCakeStage.CAKE)).setValue(ANIMATING, false)
      );
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      VoxelShape shape = switch ((BlankCakeStage)state.getValue(STAGE)) {
         case CAKE -> CAKE_SHAPE;
         case DOUGH -> DOUGH_SHAPE;
         case CUPCAKE -> CUPCAKE_SHAPE;
         case FLAT -> FLAT_SHAPE;
         case COOKIE -> COOKIE_SHAPE;
         case SHEET -> SHEET_SHAPE;
         case ROLL -> ROLL_SHAPE;
         case SHELLS -> SHELLS_SHAPE;
      };
      if (!((Boolean)state.getValue(ANIMATING) && world instanceof Level level && world.getBlockEntity(pos) instanceof BlankCakeBlockEntity cake)) {
         return shape;
      } else {
         if (cake.getAnimation() == CakeAnimation.ROLL_UP) {
            float rolled = cake.pressProgress(level.getGameTime(), 0.0F);
            int edge = 3 + Math.round(10.0F * rolled);
            int size = 1 + Math.round(2.0F * rolled);
            return Shapes.box(0.1875, 0.0, Math.max(3, edge - size) / 16.0, 0.8125, (edge < 13 ? Math.max(1, size) : size) / 16.0, 0.8125);
         }

         float scale = Math.min(cake.heightScale(level.getGameTime(), 0.0F), 1.0F);
         if (scale >= 1.0F) {
            return shape;
         }

         AABB bounds = shape.bounds();
         return Shapes.box(bounds.minX, 0.0, bounds.minZ, bounds.maxX, Math.max(bounds.maxY * scale, 0.0625), bounds.maxZ);
      }
   }

   public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
      super.neighborChanged(state, world, pos, block, fromPos, isMoving);
      if (!world.isClientSide && !this.canSurvive(state, world, pos)) {
         world.destroyBlock(pos, true);
      }
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      return !world.isEmptyBlock(pos.below());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{STAGE, ANIMATING});
   }

   protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
      if (!state.is(newState.getBlock()) && world.getBlockEntity(pos) instanceof BlankCakeBlockEntity cake) {
         cake.takeShells().forEach(shell -> Block.popResource(world, pos, shell));
         cake.takeOutput().forEach(slice -> Block.popResource(world, pos, slice));
      }

      super.onRemove(state, world, pos, newState, moved);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new BlankCakeBlockEntity(pos, state);
   }

   protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
      if ((Boolean)state.getValue(ANIMATING) && world.getBlockEntity(pos) instanceof BlankCakeBlockEntity cake) {
         CakeAnimation animation = cake.getAnimation();
         if (animation.isPressing()) {
            if (cake.isPressedFlat()) {
               finish(world, pos, cake.getPendingState() != null ? cake.getPendingState() : (BlockState)state.setValue(ANIMATING, false), false);
            }
         } else if (animation == CakeAnimation.SLICE && world.getGameTime() - cake.getAnimationStart() >= cake.getAnimationDuration()) {
            List<ItemStack> slices = cake.takeOutput();
            world.removeBlock(pos, false);
            slices.forEach(slice -> Block.popResource(world, pos, slice));
            world.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 0.6F, 1.3F);
         } else {
            long elapsed = world.getGameTime() - cake.getAnimationStart();
            int duration = cake.getAnimationDuration();
            if (elapsed < duration) {
               if (animation == CakeAnimation.SPREAD) {
                  spread(world, pos, state, cake, (float)elapsed / duration);
               }

               world.scheduleTick(pos, this, (int)Math.min(4L, duration - elapsed));
            } else {
               BlockState result = cake.getPendingState();
               boolean newBlock = animation == CakeAnimation.SPREAD || animation == CakeAnimation.SHRINK;
               boolean bonus = cake.hasPerfectBonus();
               finish(world, pos, result != null ? result : (BlockState)state.setValue(ANIMATING, false), newBlock);
               if (bonus && result != null) {
                  popPerfectBonus(world, pos, result);
               }
            }
         }
      }
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
      BlankCakeStage stage = (BlankCakeStage)state.getValue(STAGE);
      if (stage == BlankCakeStage.SHELLS) {
         if (!world.isClientSide && world.getBlockEntity(pos) instanceof BlankCakeBlockEntity cake) {
            cake.takeShells().forEach(shell -> {
               if (!player.getInventory().add(shell)) {
                  player.drop(shell, false);
               }
            });
            world.removeBlock(pos, false);
            world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 1.4F);
         }

         return InteractionResult.sidedSuccess(world.isClientSide);
      } else {
         if (stage == BlankCakeStage.SHEET) {
            return this.rollUp(state, world, pos, player);
         }

         if (stage != BlankCakeStage.DOUGH || !PlatformHelper.isKneadingEnabled() || !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
            return InteractionResult.PASS;
         }

         if (world.isClientSide) {
            return InteractionResult.SUCCESS;
         }

         if (world.getBlockEntity(pos) instanceof BlankCakeBlockEntity cake) {
            if (!PlatformHelper.isBakerStationAnimationEnabled()) {
               knead((ServerLevel)world, pos, player);
               finish(world, pos, (BlockState)state.setValue(STAGE, BlankCakeStage.FLAT), false);
               return InteractionResult.SUCCESS;
            }

            if (!(Boolean)state.getValue(ANIMATING)) {
               world.setBlock(pos, (BlockState)state.setValue(ANIMATING, true), 3);
               cake.start(CakeAnimation.KNEAD, world.getGameTime(), (BlockState)state.setValue(STAGE, BlankCakeStage.FLAT), player.getUUID());
            } else {
               if (cake.getAnimation() != CakeAnimation.KNEAD || cake.isPressedFlat()) {
                  return InteractionResult.CONSUME;
               }

               if (cake.isTooFast(world.getGameTime())) {
                  squish((ServerLevel)world, pos, cake, player);
                  return InteractionResult.SUCCESS;
               }
            }

            this.press((ServerLevel)world, pos, cake);
            knead((ServerLevel)world, pos, player);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.CONSUME;
         }
      }
   }

   private InteractionResult rollUp(BlockState state, Level world, BlockPos pos, Player player) {
      if (!player.getMainHandItem().isEmpty()) {
         return InteractionResult.PASS;
      } else if (world.isClientSide) {
         return InteractionResult.SUCCESS;
      } else if (!(world.getBlockEntity(pos) instanceof BlankCakeBlockEntity cake && cake.getFilling() != null)) {
         return InteractionResult.CONSUME;
      } else {
         if (!PlatformHelper.isBakerStationAnimationEnabled()) {
            finish(world, pos, (BlockState)state.setValue(STAGE, BlankCakeStage.ROLL), false);
            return InteractionResult.SUCCESS;
         }

         if (!(Boolean)state.getValue(ANIMATING)) {
            world.setBlock(pos, (BlockState)state.setValue(ANIMATING, true), 3);
            cake.start(CakeAnimation.ROLL_UP, world.getGameTime(), (BlockState)state.setValue(STAGE, BlankCakeStage.ROLL), player.getUUID());
         } else if (cake.getAnimation() != CakeAnimation.ROLL_UP || cake.isPressedFlat() || !cake.canPress(world.getGameTime())) {
            return InteractionResult.CONSUME;
         }

         this.press((ServerLevel)world, pos, cake);
         player.swing(InteractionHand.MAIN_HAND, true);
         world.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.6F, 1.2F);
         return InteractionResult.SUCCESS;
      }
   }

   private ItemInteractionResult fillOrSlice(ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, BlankCakeBlockEntity cake) {
      BlankCakeStage stage = (BlankCakeStage)state.getValue(STAGE);
      ServerLevel server = (ServerLevel)world;
      Optional<Filling> filling = Filling.of(world, itemStack);
      boolean perfect = itemStack.has((DataComponentType)DataComponentRegistry.PERFECT_JAM.get());
      if (stage == BlankCakeStage.SHELLS) {
         if (itemStack.is((Item)ObjectRegistry.CORNET_SHELL.get())) {
            if (cake.addShell(itemStack)) {
               itemStack.consume(1, player);
               world.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 1.3F);
               return ItemInteractionResult.SUCCESS;
            } else {
               return ItemInteractionResult.CONSUME;
            }
         } else if (filling.isPresent()) {
            ItemStack cornet = new ItemStack((ItemLike)ObjectRegistry.CORNET.get());
            cornet.set((DataComponentType)DataComponentRegistry.FILLING.get(), filling.get());
            if (cake.fillShells(cornet) == 0) {
               return ItemInteractionResult.CONSUME;
            }

            useFilling(itemStack, player);
            splash(server, pos, filling.get(), 0.15);
            world.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.7F, 1.2F);
            if (perfect) {
               popPerfectBonus(server, pos, cornet);
            }

            return ItemInteractionResult.SUCCESS;
         } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }
      } else {
         if (stage == BlankCakeStage.SHEET && cake.getFilling() == null && filling.isPresent()) {
            cake.setFilling(filling.get());
            cake.setPerfectBonus(perfect);
            useFilling(itemStack, player);
            splash(server, pos, filling.get(), 0.1);
            world.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
            return ItemInteractionResult.SUCCESS;
         }

         if (stage == BlankCakeStage.ROLL && itemStack.is(KNIVES) && cake.getFilling() != null) {
            ItemStack roll = new ItemStack((ItemLike)ObjectRegistry.JAM_ROLL.get());
            roll.set((DataComponentType)DataComponentRegistry.FILLING.get(), cake.getFilling());
            List<ItemStack> slices = new ArrayList<>();

            for (int slice = 0; slice < 5 + (cake.hasPerfectBonus() ? 1 : 0); slice++) {
               slices.add(roll.copy());
            }

            world.playSound(null, pos, (SoundEvent)SoundEventRegistry.CAKE_CUT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            server.sendParticles(
               new BlockParticleOption(ParticleTypes.BLOCK, (BlockState)state.setValue(ANIMATING, false)),
               pos.getX() + 0.5,
               pos.getY() + 0.15,
               pos.getZ() + 0.72,
               12,
               0.2,
               0.05,
               0.06,
               0.1
            );
            if (!PlatformHelper.isBakerStationAnimationEnabled()) {
               world.removeBlock(pos, false);
               slices.forEach(slice -> Block.popResource(world, pos, slice));
               return ItemInteractionResult.SUCCESS;
            }

            boolean bonus = cake.hasPerfectBonus();
            world.setBlock(pos, (BlockState)state.setValue(ANIMATING, true), 3);
            cake.start(CakeAnimation.SLICE, world.getGameTime(), state);
            cake.setOutput(slices);
            world.scheduleTick(pos, this, cake.getAnimationDuration());
            if (bonus) {
               splash(server, pos, cake.getFilling(), 0.3);
               world.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.8F, 1.3F);
            }

            return ItemInteractionResult.SUCCESS;
         } else {
            return null;
         }
      }
   }

   private static void useFilling(ItemStack stack, Player player) {
      ItemStack remainder = stack.getItem().hasCraftingRemainingItem() ? new ItemStack(stack.getItem().getCraftingRemainingItem()) : ItemStack.EMPTY;
      stack.consume(1, player);
      if (!remainder.isEmpty() && !player.getAbilities().instabuild && !player.getInventory().add(remainder)) {
         player.drop(remainder, false);
      }
   }

   private static void splash(ServerLevel world, BlockPos pos, Filling filling, double height) {
      world.sendParticles(
         ColorParticleOption.create((ParticleType)FoundationParticles.DYE_SPLASH.get(), 0xFF000000 | filling.color()),
         pos.getX() + 0.5,
         pos.getY() + height,
         pos.getZ() + 0.5,
         3,
         0.2,
         0.02,
         0.2,
         0.05
      );
   }

   private void press(ServerLevel world, BlockPos pos, BlankCakeBlockEntity cake) {
      cake.press(world.getGameTime());
      if (cake.isPressedFlat()) {
         world.scheduleTick(pos, this, 6);
      }
   }

   private static void squish(ServerLevel world, BlockPos pos, BlankCakeBlockEntity cake, Player player) {
      cake.squish(world.getGameTime());
      player.swing(InteractionHand.MAIN_HAND, true);
      world.playSound(null, pos, SoundEvents.SLIME_SQUISH_SMALL, SoundSource.BLOCKS, 0.7F, 0.6F + world.random.nextFloat() * 0.2F);
   }

   private static void knead(ServerLevel world, BlockPos pos, Player player) {
      player.swing(InteractionHand.MAIN_HAND, true);
      world.sendParticles(
         new ItemParticleOption(ParticleTypes.ITEM, new ItemStack((ItemLike)ObjectRegistry.SWEET_DOUGH.get())),
         pos.getX() + 0.5,
         pos.getY() + 0.3,
         pos.getZ() + 0.5,
         5,
         0.2,
         0.05,
         0.2,
         0.05
      );
      world.playSound(null, pos, SoundEvents.HONEY_BLOCK_STEP, SoundSource.BLOCKS, 0.6F, 0.8F + world.random.nextFloat() * 0.3F);
   }

   private static void spread(ServerLevel world, BlockPos pos, BlockState state, BlankCakeBlockEntity cake, float progress) {
      BlockState result = cake.getPendingState();
      if (result != null) {
         boolean pieces = state.getValue(STAGE) != BlankCakeStage.CAKE;
         double radius = (pieces ? 0.12 : 0.4) * Math.min(1.0, progress * 1.4);
         double height = pos.getY() + state.getShape(world, pos).max(Axis.Y) + 0.02;
         BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, result);

         for (int index = 0; index < 4; index++) {
            double angle = world.random.nextDouble() * Math.PI * 2.0;
            double centerX = pieces ? (index % 2 == 0 ? 0.28 : 0.72) : 0.5;
            double centerZ = pieces ? (index < 2 ? 0.28 : 0.72) : 0.5;
            world.sendParticles(
               particle, pos.getX() + centerX + Math.cos(angle) * radius, height, pos.getZ() + centerZ + Math.sin(angle) * radius, 1, 0.0, 0.0, 0.0, 0.0
            );
         }

         world.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.5F, 0.9F + progress * 0.5F);
      }
   }

   private static void playResultSound(Level world, BlockPos pos, BlankCakeInteractionRecipe.Result result) {
      if (result.sound() != null) {
         BuiltInRegistries.SOUND_EVENT
            .getOptional(result.sound())
            .ifPresent(soundEvent -> world.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F));
      }
   }

   private static void popPerfectBonus(ServerLevel world, BlockPos pos, BlockState result) {
      popPerfectBonus(world, pos, servingOf(result));
   }

   private static void popPerfectBonus(ServerLevel world, BlockPos pos, ItemStack bonus) {
      if (!bonus.isEmpty()) {
         Block.popResource(world, pos, bonus);
         world.sendParticles(ParticleTypes.WAX_ON, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8, 0.25, 0.15, 0.25, 0.5);
         world.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.8F, 1.3F);
      }
   }

   private static ItemStack servingOf(BlockState state) {
      if (state.getBlock() instanceof PieBlock pie) {
         return new ItemStack((ItemLike)pie.Slice.get());
      } else {
         ItemStack cupcake = CupcakeBlock.serving(state);
         return cupcake.isEmpty() ? CookieBlock.serving(state) : cupcake;
      }
   }

   private static void finish(Level world, BlockPos pos, BlockState result, boolean particles) {
      world.setBlock(pos, result, 3);
      if (particles) {
         world.levelEvent(2001, pos, Block.getId(result));
      }
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (world.isClientSide) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      if (itemStack.is(BakerStationBlock.FLOUR)) {
         BakerStationBlock.dustWithFlour((ServerLevel)world, pos, itemStack);
         return ItemInteractionResult.SUCCESS;
      }

      BlankCakeStage stage = BlankCakeStage.fromState(state);
      if (!(Boolean)state.getValue(ANIMATING) && world.getBlockEntity(pos) instanceof BlankCakeBlockEntity filled) {
         ItemInteractionResult handled = this.fillOrSlice(itemStack, state, world, pos, player, filled);
         if (handled != null) {
            return handled;
         }
      }

      BlankCakeInteractionInput input = new BlankCakeInteractionInput(itemStack);
      BlankCakeInteractionRecipe recipe = world.getRecipeManager()
         .getAllRecipesFor((RecipeType)RecipeTypeRegistry.BLANK_CAKE_INTERACTION_TYPE.get())
         .stream()
         .<BlankCakeInteractionRecipe>map(RecipeHolder::value)
         .filter(currentRecipe -> currentRecipe.matchesStage(stage))
         .filter(currentRecipe -> currentRecipe.matches(input, world))
         .min(Comparator.comparingInt(BlankCakeInteractionRecipe::priority))
         .orElse(null);
      if (recipe == null) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      BlankCakeInteractionRecipe.Result result = recipe.result();
      if ((Boolean)state.getValue(ANIMATING)) {
         if (world.getBlockEntity(pos) instanceof BlankCakeBlockEntity cake
            && result.animation().isPressing()
            && cake.getAnimation() == result.animation()
            && !cake.isPressedFlat()) {
            if (cake.isTooFast(world.getGameTime())) {
               squish((ServerLevel)world, pos, cake, player);
            } else {
               this.press((ServerLevel)world, pos, cake);
               playResultSound(world, pos, result);
            }

            return ItemInteractionResult.SUCCESS;
         } else {
            return ItemInteractionResult.CONSUME;
         }
      } else {
         BlockState newState;
         if (result.setBlock() != null) {
            Block output = (Block)BuiltInRegistries.BLOCK.getOptional(result.setBlock()).orElse(null);
            if (output == null) {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            newState = output.defaultBlockState();
         } else {
            if (result.setStage() == null) {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            newState = (BlockState)state.setValue(STAGE, result.setStage());
         }

         if (result.cooldownTicks() > 0) {
            player.getCooldowns().addCooldown(itemStack.getItem(), result.cooldownTicks());
         }

         boolean perfect = result.setBlock() != null && itemStack.has((DataComponentType)DataComponentRegistry.PERFECT_JAM.get());
         CakeAnimation animation = PlatformHelper.isBakerStationAnimationEnabled() ? result.animation() : CakeAnimation.NONE;
         if (result.animation() == CakeAnimation.SPLIT && result.particles()) {
            ((ServerLevel)world)
               .sendParticles(
                  new BlockParticleOption(ParticleTypes.BLOCK, (BlockState)state.setValue(ANIMATING, false)),
                  pos.getX() + 0.5,
                  pos.getY() + state.getShape(world, pos).max(Axis.Y),
                  pos.getZ() + 0.5,
                  12,
                  0.2,
                  0.05,
                  0.2,
                  0.1
               );
         }

         if (animation != CakeAnimation.NONE && world.getBlockEntity(pos) instanceof BlankCakeBlockEntity cake) {
            world.setBlock(pos, (BlockState)state.setValue(ANIMATING, true), 3);
            cake.start(animation, world.getGameTime(), newState);
            cake.setPerfectBonus(perfect);
            if (animation.isPressing()) {
               this.press((ServerLevel)world, pos, cake);
            } else {
               world.scheduleTick(pos, this, animation == CakeAnimation.SPREAD ? 4 : cake.getAnimationDuration());
            }
         } else {
            finish(world, pos, newState, result.particles() && result.animation() != CakeAnimation.SPLIT);
            if (perfect) {
               popPerfectBonus((ServerLevel)world, pos, newState);
            }
         }

         if (recipe.experience() > 0.0F && world.getBlockEntity(pos.below()) instanceof BakerStationBlockEntity station) {
            station.awardExperience((ServerLevel)world, recipe.experience(), Vec3.atCenterOf(pos));
         }

         playResultSound(world, pos, result);
         if (result.giveItem() != null) {
            ItemStack giveStack = BuiltInRegistries.ITEM.getOptional(result.giveItem()).<ItemStack>map(ItemStack::new).orElse(ItemStack.EMPTY);
            if (!giveStack.isEmpty() && !player.getInventory().add(giveStack)) {
               world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), giveStack));
            }
         }

         if (result.consumeOne() && !player.isCreative()) {
            itemStack.shrink(1);
         }

         return ItemInteractionResult.sidedSuccess(false);
      }
   }
}
