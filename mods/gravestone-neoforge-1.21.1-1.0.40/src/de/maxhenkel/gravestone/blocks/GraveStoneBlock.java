package de.maxhenkel.gravestone.blocks;

import com.google.common.collect.ImmutableList;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.gravestone.corelib.block.IItemBlock;
import de.maxhenkel.gravestone.corelib.death.Death;
import de.maxhenkel.gravestone.entity.GhostPlayerEntity;
import de.maxhenkel.gravestone.items.ObituaryItem;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GraveStoneBlock extends Block implements EntityBlock, IItemBlock, SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final VoxelShape BASE1 = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
   private static final VoxelShape BASE2 = Block.box(1.0, 1.0, 1.0, 15.0, 2.0, 15.0);
   private static final DirectionalVoxelShape SHAPE = new DirectionalVoxelShape.Builder()
      .direction(
         Direction.NORTH,
         BASE1,
         BASE2,
         Block.box(1.0, 2.0, 1.0, 15.0, 12.0, 2.0),
         Block.box(2.0, 12.0, 1.0, 14.0, 14.0, 2.0),
         Block.box(3.0, 14.0, 1.0, 13.0, 15.0, 2.0)
      )
      .direction(
         Direction.SOUTH,
         BASE1,
         BASE2,
         Block.box(1.0, 2.0, 14.0, 15.0, 12.0, 15.0),
         Block.box(2.0, 12.0, 14.0, 14.0, 14.0, 15.0),
         Block.box(3.0, 14.0, 14.0, 13.0, 15.0, 15.0)
      )
      .direction(
         Direction.EAST,
         BASE1,
         BASE2,
         Block.box(14.0, 2.0, 1.0, 15.0, 12.0, 15.0),
         Block.box(14.0, 12.0, 2.0, 15.0, 14.0, 14.0),
         Block.box(14.0, 14.0, 3.0, 15.0, 15.0, 13.0)
      )
      .direction(
         Direction.WEST,
         BASE1,
         BASE2,
         Block.box(1.0, 2.0, 1.0, 2.0, 12.0, 15.0),
         Block.box(1.0, 12.0, 2.0, 2.0, 14.0, 14.0),
         Block.box(1.0, 14.0, 3.0, 2.0, 15.0, 13.0)
      )
      .build();

   public GraveStoneBlock() {
      super(Properties.of().mapColor(MapColor.DIRT).strength(0.3F, Float.MAX_VALUE));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false)
      );
   }

   @Override
   public Item toItem() {
      return new BlockItem(this, new net.minecraft.world.item.Item.Properties());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
      builder.add(new Property[]{WATERLOGGED});
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
      }

      return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
   }

   public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      if (stack.has(DataComponents.CUSTOM_NAME) && world.getBlockEntity(pos) instanceof GraveStoneTileEntity grave) {
         grave.setCustomName(stack.getHoverName());
      }

      super.setPlacedBy(world, pos, state, placer, stack);
   }

   public void wasExploded(Level world, BlockPos pos, Explosion explosion) {
   }

   public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()))
         .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
   }

   public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
      return false;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
      if (!(level.getBlockEntity(pos) instanceof GraveStoneTileEntity grave)) {
         return InteractionResult.FAIL;
      } else {
         Component name = grave.getGraveName();
         if (name == null) {
            return InteractionResult.FAIL;
         }

         if (level.isClientSide) {
            Component time = GraveUtils.getDate(grave.getDeath().getTimestamp());
            if (time == null) {
               player.sendSystemMessage(name);
            } else {
               player.sendSystemMessage(Component.translatable("message.gravestone.died", new Object[]{name, time}));
            }
         }

         return InteractionResult.SUCCESS;
      }
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         BlockEntity tileentity = world.getBlockEntity(pos);
         if (tileentity instanceof GraveStoneTileEntity) {
            this.dropItems(world, pos, ((GraveStoneTileEntity)tileentity).getDeath().getAllItems());
         }

         super.onRemove(state, world, pos, newState, isMoving);
      }
   }

   public void dropItems(Level world, BlockPos pos, NonNullList<ItemStack> items) {
      for (ItemStack item : items) {
         Containers.dropItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item);
      }
   }

   public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      if (!GraveUtils.canBreakGrave(world, player, pos)) {
         return false;
      }

      if (!world.isClientSide && world.getBlockEntity(pos) instanceof GraveStoneTileEntity grave) {
         this.removeObituary(player, grave);
         this.spawnGhost(world, pos, grave);
         if (!grave.getDeath().getId().equals(GraveUtils.EMPTY_UUID) && (Boolean)Main.SERVER_CONFIG.breakPickup.get()) {
            this.sortItems(world, pos, player, grave);
         }
      }

      return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
   }

   protected void spawnGhost(Level world, BlockPos pos, GraveStoneTileEntity grave) {
      if ((Boolean)Main.SERVER_CONFIG.spawnGhost.get()) {
         if (world.isEmptyBlock(pos.above())) {
            UUID uuid = grave.getDeath().getPlayerUUID();
            if (!uuid.equals(GraveUtils.EMPTY_UUID)) {
               GhostPlayerEntity ghost = new GhostPlayerEntity(
                  world, uuid, Component.literal(grave.getDeath().getPlayerName()), grave.getDeath().getEquipment(), grave.getDeath().getModel()
               );
               ghost.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
               world.addFreshEntity(ghost);
            }
         }
      }
   }

   protected void removeObituary(Player p, GraveStoneTileEntity grave) {
      if ((Boolean)Main.SERVER_CONFIG.removeObituary.get()) {
         if (p instanceof ServerPlayer player) {
            Inventory inv = player.getInventory();

            for (NonNullList<ItemStack> i : ImmutableList.of(player.getInventory().items, player.getInventory().armor, player.getInventory().offhand)) {
               for (ItemStack stack : i) {
                  if (stack.getItem().equals(Main.OBITUARY.get())) {
                     Death death = ((ObituaryItem)Main.OBITUARY.get()).fromStack(player, stack);
                     if (death != null && !grave.getDeath().getId().equals(GraveUtils.EMPTY_UUID) && grave.getDeath().getId().equals(death.getId())) {
                        inv.removeItem(stack);
                     }
                  }
               }
            }
         }
      }
   }

   public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
      super.entityInside(state, world, pos, entity);
      if (entity instanceof ServerPlayer && entity.isAlive() && (Boolean)Main.SERVER_CONFIG.sneakPickup.get()) {
         ServerPlayer player = (ServerPlayer)entity;
         if (player.isShiftKeyDown() && !player.getAbilities().instabuild && GraveUtils.canBreakGrave(world, player, pos)) {
            if (world.getBlockEntity(pos) instanceof GraveStoneTileEntity grave) {
               if (!grave.getDeath().getId().equals(GraveUtils.EMPTY_UUID)) {
                  this.removeObituary(player, grave);
                  this.spawnGhost(world, pos, grave);
                  this.sortItems(world, pos, player, grave);
                  world.destroyBlock(pos, true);
               }
            }
         }
      }
   }

   protected void sortItems(Level world, BlockPos pos, Player player, GraveStoneTileEntity grave) {
      Death death = grave.getDeath();
      this.dropItems(world, pos, this.fillPlayerInventory(player, death));
      world.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
      grave.setChanged();
   }

   public NonNullList<ItemStack> fillPlayerInventory(Player player, Death death) {
      NonNullList<ItemStack> additionalItems = NonNullList.create();
      this.fillInventory(additionalItems, death.getMainInventory(), player.getInventory().items);
      this.fillInventory(additionalItems, death.getArmorInventory(), player.getInventory().armor);
      this.fillInventory(additionalItems, death.getOffHandInventory(), player.getInventory().offhand);
      additionalItems.addAll(death.getAdditionalItems());
      NonNullList<ItemStack> restItems = NonNullList.create();

      for (ItemStack stack : additionalItems) {
         if (!player.getInventory().add(stack)) {
            restItems.add(stack);
         }
      }

      death.getAdditionalItems().clear();
      return restItems;
   }

   public void fillInventory(List<ItemStack> additionalItems, NonNullList<ItemStack> inventory, NonNullList<ItemStack> playerInv) {
      for (int i = 0; i < inventory.size(); i++) {
         ItemStack stack = (ItemStack)inventory.get(i);
         if (!stack.isEmpty()) {
            ItemStack playerStack = (ItemStack)playerInv.get(i);
            if (!playerStack.isEmpty()) {
               additionalItems.add(playerStack);
            }

            inventory.set(i, ItemStack.EMPTY);
            playerInv.set(i, stack);
         }
      }
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return SHAPE.get((Direction)state.getValue(FACING));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new GraveStoneTileEntity(pos, state);
   }
}
