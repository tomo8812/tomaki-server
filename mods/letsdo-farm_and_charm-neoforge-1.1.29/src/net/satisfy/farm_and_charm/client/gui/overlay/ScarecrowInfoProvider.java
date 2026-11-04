package net.satisfy.farm_and_charm.client.gui.overlay;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.ScarecrowBlock;
import net.satisfy.farm_and_charm.core.block.ScarecrowMode;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

public class ScarecrowInfoProvider implements BlockInfoProvider {
   public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
      if (InfoTooltips.isHidden(PlatformHelper.showScarecrowInfo())) {
         return List.of();
      } else {
         return !(state.getBlock() instanceof ScarecrowBlock)
            ? List.of()
            : List.of(
               InfoSection.title(Component.translatable(((ScarecrowMode)state.getValue(ScarecrowBlock.MODE)).getTranslationKey())),
               InfoSection.title(Component.translatable("hud.farm_and_charm.scarecrow_hint").withStyle(ChatFormatting.GRAY))
            );
      }
   }
}
