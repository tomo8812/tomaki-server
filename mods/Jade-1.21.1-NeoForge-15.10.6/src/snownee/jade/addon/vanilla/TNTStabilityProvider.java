package snownee.jade.addon.vanilla;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum TNTStabilityProvider implements IBlockComponentProvider {
   INSTANCE;

   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
      BlockState state = accessor.getBlockState();
      if ((Boolean)state.getValue(TntBlock.UNSTABLE)) {
         tooltip.add(IThemeHelper.get().danger(Component.translatable("jade.tnt.unstable")));
      }
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_TNT_STABILITY;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void appendTooltip(ITooltip param1, Accessor nullx, IPluginConfig nullxx) {
      this.appendTooltip(var1, (BlockAccessor)nullx, nullxx);
   }

   // $VF: synthetic method
   private static TNTStabilityProvider[] $values() {
      return new TNTStabilityProvider[]{INSTANCE};
   }
}
