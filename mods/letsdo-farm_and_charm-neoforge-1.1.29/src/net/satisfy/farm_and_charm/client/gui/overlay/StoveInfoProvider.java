package net.satisfy.farm_and_charm.client.gui.overlay;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.entity.StoveBlockEntity;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.foundation.overlay.InfoSection.Row;
import org.jetbrains.annotations.Nullable;

public class StoveInfoProvider implements BlockInfoProvider {
   public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
      if (hit != null && hit.getDirection() == Direction.UP && !InfoTooltips.isHidden(true)) {
         if (level.getBlockEntity(pos) instanceof StoveBlockEntity stove && stove.isGrillFree()) {
            int slot = StoveBlockEntity.grillSlotAt(hit.getLocation(), pos);
            ItemStack stack = stove.getGrillItem(slot);
            if (stack.isEmpty()) {
               return List.of();
            }

            boolean done = stove.isGrillDone(slot);
            Component status;
            if (done) {
               status = Component.translatable("hud.farm_and_charm.grill_done").withStyle(ChatFormatting.GREEN);
            } else if (!stove.isLit()) {
               status = Component.translatable("hud.farm_and_charm.grill_cold").withStyle(ChatFormatting.GOLD);
            } else {
               int seconds = Mth.positiveCeilDiv(Math.max(0, stove.getGrillTotal(slot) - stove.getGrillProgress(slot)), 20);
               status = Component.translatable("hud.farm_and_charm.grill_time", new Object[]{seconds}).withStyle(ChatFormatting.YELLOW);
            }

            return List.of(
               InfoSection.rows(
                  Component.translatable("hud.farm_and_charm.grill"), List.of(Row.item(stack, stack.getHoverName().copy().append(" ").append(status)))
               ),
               InfoSection.title(
                  Component.translatable(done ? "hud.farm_and_charm.grill_hint_done" : "hud.farm_and_charm.grill_hint").withStyle(ChatFormatting.GRAY)
               )
            );
         } else {
            return List.of();
         }
      } else {
         return List.of();
      }
   }
}
