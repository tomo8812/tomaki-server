package net.satisfy.farm_and_charm.client.gui.overlay;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.entity.MincerBlockEntity;
import net.satisfy.farm_and_charm.core.recipe.MincerRecipe;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

public class MincerInfoProvider implements BlockInfoProvider {
   public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
      if (InfoTooltips.isHidden(PlatformHelper.showMincerInfo())) {
         return List.of();
      }

      if (level.getBlockEntity(pos) instanceof MincerBlockEntity mincer) {
         ItemStack input = mincer.getItem(0);
         if (input.isEmpty()) {
            return List.of();
         }

         List<InfoSection> sections = new ArrayList<>();
         sections.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.mincer_input"), List.of(input), 6).withDecorations());
         MincerRecipe recipe = mincer.findRecipe(level);
         if (recipe != null) {
            sections.add(
               InfoSection.icons(
                  Component.translatable("hud.farm_and_charm.result").withStyle(ChatFormatting.GREEN), List.of(recipe.getResultItem(level.registryAccess())), 6
               )
            );
         }

         int cranked = (int)mincer.getCranked();
         sections.add(InfoSection.title(Component.translatable("hud.farm_and_charm.mincer_cranked", new Object[]{cranked, 20}).withStyle(ChatFormatting.GRAY)));
         sections.add(InfoSection.title(Component.translatable("hud.farm_and_charm.mincer_hint").withStyle(ChatFormatting.GRAY)));
         return sections;
      } else {
         return List.of();
      }
   }
}
