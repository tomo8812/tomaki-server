package org.millenaire.block;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class XunaanHiveBlock extends BeehiveBlock {
   public XunaanHiveBlock(Properties properties) {
      super(properties);
   }

   public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
      player.awardStat(Stats.BLOCK_MINED.get(this));
      player.causeFoodExhaustion(0.005F);
      dropResources(state, level, pos, blockEntity, player, tool);
      if (!level.isClientSide && blockEntity instanceof BeehiveBlockEntity beehive && player instanceof ServerPlayer serverPlayer) {
         CriteriaTriggers.BEE_NEST_DESTROYED.trigger(serverPlayer, state, tool, beehive.getOccupantCount());
      }
   }
}
