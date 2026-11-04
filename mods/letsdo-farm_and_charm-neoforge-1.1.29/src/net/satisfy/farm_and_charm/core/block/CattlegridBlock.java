package net.satisfy.farm_and_charm.core.block;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;

public class CattlegridBlock extends Block {
   private static final VoxelShape FLOOR_COLLISION = Shapes.block();
   private static final VoxelShape MOB_WALLS = Shapes.or(
      Block.box(0.0, 16.0, 0.0, 16.0, 28.0, 2.0),
      new VoxelShape[]{Block.box(0.0, 16.0, 14.0, 16.0, 28.0, 16.0), Block.box(0.0, 16.0, 0.0, 2.0, 28.0, 16.0), Block.box(14.0, 16.0, 0.0, 16.0, 28.0, 16.0)}
   );
   private static final VoxelShape MOB_COLLISION = Shapes.or(FLOOR_COLLISION, MOB_WALLS);

   public CattlegridBlock(Properties properties) {
      super(properties);
   }

   public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
      if (!entity.getType().is(TagRegistry.CAN_WALK_OVER_CATTLEGRID)) {
         if (entity instanceof Player player) {
            if (!player.isCreative()) {
               player.setSprinting(false);
               player.makeStuckInBlock(state, new Vec3(0.98, 1.0, 0.98));
            }
         } else if (entity instanceof Cat) {
            Vec3 movement = entity.getDeltaMovement();
            entity.setDeltaMovement(movement.x * 1.03, movement.y, movement.z * 1.03);
         } else if (!(entity instanceof Mob)) {
            if (entity instanceof LivingEntity livingEntity) {
               livingEntity.makeStuckInBlock(state, new Vec3(0.95, 1.0, 0.95));
            }
         }
      }
   }

   protected boolean isPathfindable(BlockState state, PathComputationType type) {
      return false;
   }

   @NotNull
   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      if (context instanceof EntityCollisionContext entityContext) {
         Entity entity = entityContext.getEntity();
         return entity instanceof Mob && !entity.getType().is(TagRegistry.CAN_WALK_OVER_CATTLEGRID) ? MOB_COLLISION : FLOOR_COLLISION;
      } else {
         return MOB_COLLISION;
      }
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      int earthyColor = 16767334;
      int goldColor = 16766720;
      if (Screen.hasShiftDown()) {
         tooltip.add(Component.translatable("tooltip.farm_and_charm.cattlegrid.info_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthyColor))));
      } else {
         Component shiftKey = Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(goldColor)));
         tooltip.add(
            Component.translatable("tooltip.farm_and_charm.tooltip_information.hold", new Object[]{shiftKey})
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthyColor)))
         );
      }
   }
}
