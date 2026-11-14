package org.millenaire.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BancoBlock extends Block {
   public static final EnumProperty<BancoProgress> PROGRESS = EnumProperty.create("progress", BancoProgress.class);

   public BancoBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(PROGRESS, BancoProgress.WET));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PROGRESS});
   }

   public boolean isRandomlyTicking(BlockState state) {
      return true;
   }

   protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      BancoProgress next = switch ((BancoProgress)state.getValue(PROGRESS)) {
         case WET -> BancoProgress.DRYING1;
         case DRYING1 -> BancoProgress.DRYING2;
         case DRYING2 -> BancoProgress.DRY;
         case DRY -> null;
      };
      if (next != null) {
         level.setBlock(pos, (BlockState)state.setValue(PROGRESS, next), 2);
      }
   }
}
