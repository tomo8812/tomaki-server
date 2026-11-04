package net.satisfy.candlelight.core.block;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.candlelight.Candlelight;
import net.satisfy.candlelight.core.block.entity.TableSetBlockEntity;
import net.satisfy.candlelight.core.item.NapkinItem;
import net.satisfy.candlelight.core.item.WineGlassItem;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;
import net.satisfy.candlelight.core.registry.MobEffectRegistry;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import net.satisfy.candlelight.core.registry.StorageTypeRegistry;
import net.satisfy.candlelight.core.util.DinnerMenu;
import net.satisfy.candlelight.core.util.Wearables;
import net.satisfy.candlelight.platform.PlatformHelper;
import net.satisfy.foundation.block.LampBlock;
import net.satisfy.foundation.food.IngredientEffects;
import net.satisfy.foundation.overlay.BlockNotice;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.storage.StorageBlock;
import net.satisfy.foundation.storage.StorageBlockEntity;
import org.jetbrains.annotations.NotNull;

public class TableSetBlock extends StorageBlock {
   public static final EnumProperty<TableSetBlock.PlateType> PLATE_TYPE = EnumProperty.create("plate_type", TableSetBlock.PlateType.class);
   public static final BooleanProperty WINE_GLASS = BooleanProperty.create("wine_glass");
   public static final BooleanProperty GLASS = BooleanProperty.create("glass");
   public static final BooleanProperty CLOCHE = BooleanProperty.create("cloche");
   public static final BooleanProperty NAPKIN = BooleanProperty.create("napkin");
   public static final BooleanProperty GLASS_DRINK = BooleanProperty.create("glass_drink");
   public static final BooleanProperty WINE_GLASS_DRINK = BooleanProperty.create("wine_glass_drink");
   private static final TagKey<Item> GLASS_DRINKS = TagKey.create(Registries.ITEM, Candlelight.identifier("glass_drinks"));
   private static final TagKey<Item> WINE_GLASS_DRINKS = TagKey.create(Registries.ITEM, Candlelight.identifier("wine_glass_drinks"));
   private static final TagKey<Block> TABLE_LIGHTS = TagKey.create(Registries.BLOCK, Candlelight.identifier("table_lights"));
   private static final TagKey<Item> SERVED_DISHES = TagKey.create(Registries.ITEM, Candlelight.identifier("served_dishes"));
   public static final int MAX_COURSES = 7;
   private static final VoxelShape GLASS_SHAPE = Block.box(0.0, 0.0, 12.0, 4.0, 8.0, 16.0);
   private static final VoxelShape WINE_GLASS_SHAPE = Block.box(4.0, 0.0, 12.0, 8.0, 14.0, 16.0);
   private static final VoxelShape NAPKIN_SHAPE = Block.box(1.0, 0.0, 3.0, 3.0, 1.0, 11.0);
   public static final int MAX_STARS = 5;

   public BlockEntityType<?> blockEntityType() {
      return (BlockEntityType<?>)EntityTypeRegistry.TABLE_SET_BLOCK_ENTITY.get();
   }

   public TableSetBlock(Properties settings) {
      super(settings);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)super.defaultBlockState().setValue(WINE_GLASS, false))
                        .setValue(GLASS, false))
                     .setValue(CLOCHE, false))
                  .setValue(NAPKIN, false))
               .setValue(GLASS_DRINK, false))
            .setValue(WINE_GLASS_DRINK, false)
      );
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return level.isClientSide() ? null : (tickLevel, pos, tickState, blockEntity) -> {
         if (blockEntity instanceof TableSetBlockEntity tableSet && tickLevel instanceof ServerLevel serverLevel) {
            tableSet.serverTick(serverLevel, pos, tickState);
         }
      };
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if (PlatformHelper.enableDishSteam() && !(Boolean)state.getValue(CLOCHE) && !(random.nextFloat() > 0.35F)) {
         if (level.getBlockEntity(pos) instanceof TableSetBlockEntity tableSet
            && !((ItemStack)tableSet.getInventory().get(0)).isEmpty()
            && tableSet.getServedAt() >= 0L) {
            long age = level.getGameTime() - tableSet.getServedAt();
            if (age >= 0L && age <= PlatformHelper.dishSteamTicks() && !(random.nextFloat() > 1.0F - (float)age / PlatformHelper.dishSteamTicks())) {
               Vec3 plate = this.rotateShape((Direction)state.getValue(FACING), this.makePlateShape()).bounds().getCenter();
               double x = pos.getX() + plate.x + (random.nextDouble() - 0.5) * 0.25;
               double z = pos.getZ() + plate.z + (random.nextDouble() - 0.5) * 0.25;
               level.addParticle((ParticleOptions)FoundationParticles.SOUP_STEAM.get(), x, pos.getY() + 0.2, z, 0.0, 0.02 + random.nextDouble() * 0.01, 0.0);
            }
         }
      }
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TableSetBlockEntity(pos, state, this.size());
   }

   private static HashMap<Item, BooleanProperty> itemHashMap() {
      return (HashMap<Item, BooleanProperty>)Util.make(new HashMap(), map -> {
         map.put((Item)ObjectRegistry.WINE_GLASS.get(), WINE_GLASS);
         map.put((Item)ObjectRegistry.GLASS.get(), GLASS);
         map.put((Item)ObjectRegistry.CLOCHE.get(), CLOCHE);
         map.put((Item)ObjectRegistry.NAPKIN.get(), NAPKIN);
      });
   }

   private static Item getItemFromProperty(BooleanProperty property) {
      return itemHashMap().entrySet().stream().filter(entry -> entry.getValue().equals(property)).map(Entry::getKey).findFirst().orElse(null);
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      ItemStack stack = player.getItemInHand(hand);
      if (NapkinItem.isBorrowed(stack) || WineGlassItem.isBorrowed(stack)) {
         return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
      }

      if (stack.isEmpty() && hand == InteractionHand.MAIN_HAND && (Boolean)state.getValue(NAPKIN) && this.hitsNapkin(state, pos, hit)) {
         if (!world.isClientSide()) {
            world.setBlockAndUpdate(pos, (BlockState)state.setValue(NAPKIN, false));
            player.setItemInHand(hand, NapkinItem.borrowedFrom(pos, world.getGameTime()));
         }

         return ItemInteractionResult.sidedSuccess(world.isClientSide());
      } else if (stack.isEmpty()
         && hand == InteractionHand.MAIN_HAND
         && (Boolean)state.getValue(WINE_GLASS)
         && this.hitsPart(state, pos, hit, WINE_GLASS_SHAPE)) {
         if (!world.isClientSide()) {
            boolean filled = (Boolean)state.getValue(WINE_GLASS_DRINK);
            world.setBlockAndUpdate(pos, (BlockState)((BlockState)state.setValue(WINE_GLASS, false)).setValue(WINE_GLASS_DRINK, false));
            player.setItemInHand(hand, WineGlassItem.borrowedFrom(pos, world.getGameTime(), filled));
            world.playSound(null, pos, SoundEvents.GLASS_STEP, SoundSource.BLOCKS, 0.5F, 1.8F);
         }

         return ItemInteractionResult.sidedSuccess(world.isClientSide());
      } else if (stack.isEmpty() && hasDrink(state)) {
         if (!world.isClientSide() && world.getBlockEntity(pos) instanceof TableSetBlockEntity tableSet) {
            boolean wineGlass = !(Boolean)state.getValue(GLASS_DRINK);
            world.setBlockAndUpdate(pos, (BlockState)state.setValue(wineGlass ? WINE_GLASS_DRINK : GLASS_DRINK, false));
            drink(world, pos, player, tableSet.getDrink(wineGlass));
            tableSet.setDrink(wineGlass, ItemStack.EMPTY);
         }

         return ItemInteractionResult.sidedSuccess(world.isClientSide());
      } else {
         boolean fitsWineGlass = PlatformHelper.enableTableDrinks()
            && stack.is(WINE_GLASS_DRINKS)
            && (Boolean)state.getValue(WINE_GLASS)
            && !(Boolean)state.getValue(WINE_GLASS_DRINK);
         boolean fitsGlass = PlatformHelper.enableTableDrinks()
            && stack.is(GLASS_DRINKS)
            && (Boolean)state.getValue(GLASS)
            && !(Boolean)state.getValue(GLASS_DRINK);
         if (!fitsWineGlass && !fitsGlass) {
            HashMap<Item, BooleanProperty> items = itemHashMap();
            if (player.isShiftKeyDown() && (Boolean)state.getValue(CLOCHE)) {
               if (!world.isClientSide()) {
                  world.setBlockAndUpdate(pos, (BlockState)state.setValue(CLOCHE, false));
                  ItemStack clocheItem = new ItemStack(getItemFromProperty(CLOCHE));
                  if (!player.getInventory().add(clocheItem)) {
                     player.drop(clocheItem, false);
                  }
               }

               return ItemInteractionResult.sidedSuccess(world.isClientSide());
            } else {
               Item item = stack.getItem();
               if (!items.containsKey(item)) {
                  ItemInteractionResult result = super.useItemOn(itemStack, state, world, pos, player, hand, hit);
                  if (!world.isClientSide() && result.consumesAction() && world.getBlockEntity(pos) instanceof TableSetBlockEntity tableSet) {
                     tableSet.setHost(player.getUUID());
                  }

                  return result;
               } else {
                  BooleanProperty property = items.get(item);
                  if ((Boolean)state.getValue(property)) {
                     return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                  }

                  if (!world.isClientSide()) {
                     world.setBlockAndUpdate(pos, (BlockState)state.setValue(property, true));
                     if (!player.isCreative()) {
                        stack.shrink(1);
                     }
                  }

                  return ItemInteractionResult.sidedSuccess(world.isClientSide());
               }
            }
         } else {
            if (!world.isClientSide() && world.getBlockEntity(pos) instanceof TableSetBlockEntity tableSet) {
               tableSet.setDrink(fitsWineGlass, stack.copyWithCount(1));
               world.setBlockAndUpdate(pos, (BlockState)state.setValue(fitsWineGlass ? WINE_GLASS_DRINK : GLASS_DRINK, true));
               world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               if (!player.isCreative()) {
                  stack.shrink(1);
               }
            }

            return ItemInteractionResult.sidedSuccess(world.isClientSide());
         }
      }
   }

   public void remove(Level world, BlockPos blockPos, Player player, StorageBlockEntity storageBlockEntity, int i) {
      BlockState state = world.getBlockState(blockPos);
      if (!(Boolean)state.getValue(CLOCHE) && !world.isClientSide()) {
         ItemStack itemStack = storageBlockEntity.removeStack(i);
         world.playSound(null, blockPos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0F, 1.0F);
         FoodProperties foodComponent = (FoodProperties)itemStack.get(DataComponents.FOOD);
         if (foodComponent != null) {
            float bonus = Wearables.isElegantlyDressed(player) ? (float)PlatformHelper.elegantFoodBonus() : (float)PlatformHelper.tableSetFoodBonus();
            player.getFoodData().eat(Math.round(foodComponent.nutrition() * bonus), foodComponent.saturation() * bonus);
            foodComponent.effects().forEach(possibleEffect -> {
               if (world.random.nextFloat() < possibleEffect.probability()) {
                  player.addEffect(new MobEffectInstance(possibleEffect.effect()));
               }
            });
         }

         for (Pair<MobEffectInstance, Float> effect : IngredientEffects.getEffects(itemStack)) {
            if (effect.getFirst() != null && world.random.nextFloat() < (Float)effect.getSecond()) {
               player.addEffect(new MobEffectInstance((MobEffectInstance)effect.getFirst()));
            }
         }

         if (isServedDish(itemStack)) {
            serve(world, blockPos, state, player, itemStack);
         }

         world.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
      }
   }

   public static String rating(int courses) {
      int stars = Math.round((courses - 1) * 5.0F / 6.0F);
      stars = Math.max(0, Math.min(5, stars));
      return "★".repeat(stars) + "☆".repeat(5 - stars);
   }

   public static int countCourses(Level level, BlockPos pos, BlockState state, Player player) {
      int courses = 1;
      if (hasLight(level, pos)) {
         courses++;
      }

      if ((Boolean)state.getValue(NAPKIN)) {
         courses++;
      }

      if ((Boolean)state.getValue(GLASS)) {
         courses++;
      }

      if ((Boolean)state.getValue(WINE_GLASS)) {
         courses++;
      }

      if (hasDrink(state)) {
         courses++;
      }

      if (Wearables.isElegantlyDressed(player)) {
         courses++;
      }

      return courses;
   }

   public static boolean hasLight(Level level, BlockPos pos) {
      if (!PlatformHelper.enableTableLights()) {
         return false;
      }

      int radius = PlatformHelper.tableLightRadius();

      for (BlockPos neighbor : BlockPos.betweenClosed(pos.offset(-radius, 0, -radius), pos.offset(radius, 0, radius))) {
         BlockState neighborState = level.getBlockState(neighbor);
         if (neighborState.is(TABLE_LIGHTS) && isLit(neighborState)) {
            return true;
         }
      }

      return false;
   }

   private static boolean isLit(BlockState state) {
      if (state.hasProperty(BlockStateProperties.LIT)) {
         return (Boolean)state.getValue(BlockStateProperties.LIT);
      } else {
         return state.hasProperty(LampBlock.LUMINANCE) ? (Boolean)state.getValue(LampBlock.LUMINANCE) : true;
      }
   }

   private static void drink(Level level, BlockPos pos, Player player, ItemStack drink) {
      level.playSound(null, pos, SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
      if (!drink.isEmpty()) {
         ItemStack remainder = drink.getItem().finishUsingItem(drink.copy(), level, player);
         if (!remainder.isEmpty() && !ItemStack.isSameItem(remainder, drink) && !player.getInventory().add(remainder)) {
            player.drop(remainder, false);
         }
      }
   }

   public static boolean hasDrink(BlockState state) {
      return (Boolean)state.getValue(GLASS_DRINK) || (Boolean)state.getValue(WINE_GLASS_DRINK);
   }

   public static boolean isServedDish(ItemStack stack) {
      return stack.is(SERVED_DISHES);
   }

   private static void serve(Level world, BlockPos pos, BlockState state, Player player, ItemStack dish) {
      Level level = world;
      int courses = countCourses(world, pos, state, player);
      Holder<MobEffect> wellServed = MobEffectRegistry.holder(MobEffectRegistry.WELL_SERVED);
      MobEffectInstance current = player.getEffect(wellServed);
      int duration = courses * PlatformHelper.wellServedTicksPerCourse() + (current != null ? current.getDuration() : 0);
      player.addEffect(new MobEffectInstance(wellServed, Math.min(duration, PlatformHelper.maxWellServedTicks()), 0, false, false));
      if (world instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 4 + courses * 2, 0.3, 0.15, 0.3, 0.0);
      }

      DinnerMenu.Result menu = PlatformHelper.enableDinnerMenu() ? DinnerMenu.eat(player, dish) : null;
      List<Component> lines = new ArrayList<>();
      lines.add(Component.literal(rating(courses)).withStyle(ChatFormatting.GOLD));
      if (menu != null && menu.complete()) {
         MobEffectInstance served = player.getEffect(wellServed);
         int bonus = PlatformHelper.menuBonusTicks() + (served != null ? served.getDuration() : 0);
         player.addEffect(new MobEffectInstance(wellServed, Math.min(bonus, PlatformHelper.maxMenuWellServedTicks()), 0, false, false));
         player.addEffect(new MobEffectInstance(MobEffectRegistry.holder(MobEffectRegistry.REFRESHED), PlatformHelper.menuBonusTicks(), 0, false, false));
         level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6F, 1.4F);
         lines.add(Component.translatable("message.candlelight.table_set.menu_complete").withStyle(ChatFormatting.GREEN));
      } else if (menu != null && menu.step() > 0) {
         lines.add(
            Component.translatable(
                  "message.candlelight.table_set.menu_course",
                  new Object[]{Component.translatable("hud.candlelight.table_set.course." + menu.course().name().toLowerCase()), menu.step(), 3}
               )
               .withStyle(ChatFormatting.GRAY)
         );
      }

      if (player instanceof ServerPlayer serverPlayer) {
         BlockNotice.send(serverPlayer, pos, Component.translatable("message.candlelight.table_set.served"), lines);
      }
   }

   @NotNull
   public List<ItemStack> getDrops(BlockState blockState, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      List<ItemStack> list = super.getDrops(blockState, builder);
      TableSetBlock.PlateType type = (TableSetBlock.PlateType)blockState.getValue(PLATE_TYPE);
      switch (type) {
         case PLATE:
            list.add(new ItemStack((ItemLike)ObjectRegistry.PLATE.get()));
            break;
         case BOWL:
            list.add(new ItemStack((ItemLike)ObjectRegistry.BOWL.get()));
      }

      for (BooleanProperty property : itemHashMap().values()) {
         if ((Boolean)blockState.getValue(property)) {
            Item item = getItemFromProperty(property);
            if (item != null) {
               list.add(new ItemStack(item));
            }
         }
      }

      return list;
   }

   public int size() {
      return 1;
   }

   public ResourceLocation type() {
      return StorageTypeRegistry.TABLE_SET;
   }

   public Direction[] unAllowedDirections() {
      return new Direction[0];
   }

   public boolean canInsertStack(ItemStack stack) {
      return stack.has(DataComponents.FOOD);
   }

   public int getSection(Float x, Float y) {
      return 0;
   }

   public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(world, pos)) {
         world.destroyBlock(pos, true);
      }
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      VoxelShape shape = world.getBlockState(pos.below()).getBlockSupportShape(world, pos.below());
      return Block.isFaceFull(shape, Direction.UP);
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
      if (!state.canSurvive(world, pos)) {
         world.scheduleTick(pos, this, 1);
      }

      return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      tooltip.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{PLATE_TYPE});
      builder.add(new Property[]{WINE_GLASS, GLASS, CLOCHE, NAPKIN, GLASS_DRINK, WINE_GLASS_DRINK});
   }

   private VoxelShape makeBowlShape() {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.0, 0.125, 0.3125, 0.1875, 0.75), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.8125, 0.0, 0.125, 0.875, 0.1875, 0.75), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.3125, 0.0, 0.125, 0.8125, 0.1875, 0.1875), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.3125, 0.0, 0.6875, 0.8125, 0.1875, 0.75), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.3125, 0.0, 0.1875, 0.8125, 0.0625, 0.6875), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.875, 0.1875, 0.3125, 1.0, 0.1875, 0.5625), BooleanOp.OR);
      return Shapes.joinUnoptimized(shape, Shapes.box(0.125, 0.1875, 0.3125, 0.25, 0.1875, 0.5625), BooleanOp.OR);
   }

   private VoxelShape makePlateShape() {
      VoxelShape shape = Shapes.empty();
      return Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.0, 0.0625, 0.9375, 0.0625, 0.75), BooleanOp.OR);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      Direction direction = (Direction)state.getValue(FACING);
      TableSetBlock.PlateType type = (TableSetBlock.PlateType)state.getValue(PLATE_TYPE);
      VoxelShape shape = type.equals(TableSetBlock.PlateType.BOWL) ? this.makeBowlShape() : this.makePlateShape();
      if ((Boolean)state.getValue(GLASS)) {
         shape = Shapes.or(shape, GLASS_SHAPE);
      }

      if ((Boolean)state.getValue(WINE_GLASS)) {
         shape = Shapes.or(shape, WINE_GLASS_SHAPE);
      }

      if ((Boolean)state.getValue(NAPKIN)) {
         shape = Shapes.or(shape, NAPKIN_SHAPE);
      }

      return this.rotateShape(direction, shape);
   }

   private boolean hitsNapkin(BlockState state, BlockPos pos, BlockHitResult hit) {
      return this.hitsPart(state, pos, hit, NAPKIN_SHAPE);
   }

   private boolean hitsPart(BlockState state, BlockPos pos, BlockHitResult hit, VoxelShape part) {
      Vec3 local = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
      return this.rotateShape((Direction)state.getValue(FACING), part).bounds().inflate(0.06).contains(local);
   }

   private VoxelShape rotateShape(Direction direction, VoxelShape shape) {
      if (direction == Direction.NORTH) {
         return shape;
      }

      VoxelShape[] rotatedShapes = new VoxelShape[]{shape};

      for (int i = 0; i < (direction.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4; i++) {
         rotatedShapes[0] = this.rotateShapeClockwise(rotatedShapes[0]);
      }

      return rotatedShapes[0];
   }

   private VoxelShape rotateShapeClockwise(VoxelShape shape) {
      VoxelShape[] result = new VoxelShape[]{Shapes.empty()};
      shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
         double newMinX = 1.0 - maxZ;
         double newMaxX = 1.0 - minZ;
         result[0] = Shapes.or(result[0], Shapes.box(newMinX, minY, minX, newMaxX, maxY, maxX));
      });
      return result[0];
   }

   public enum PlateType implements StringRepresentable {
      PLATE("plate"),
      BOWL("bowl");

      private final String name;

      PlateType(String name) {
         this.name = name;
      }

      @NotNull
      public String getSerializedName() {
         return this.name;
      }

      // $VF: synthetic method
      private static TableSetBlock.PlateType[] $values() {
         return new TableSetBlock.PlateType[]{PLATE, BOWL};
      }
   }
}
