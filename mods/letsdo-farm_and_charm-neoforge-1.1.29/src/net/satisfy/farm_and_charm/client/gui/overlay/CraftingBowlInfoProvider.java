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
import net.satisfy.farm_and_charm.core.block.entity.CraftingBowlBlockEntity;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

public class CraftingBowlInfoProvider implements BlockInfoProvider {
   public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
      if (InfoTooltips.isHidden(PlatformHelper.showCraftingBowlInfo())) {
         return List.of();
      }

      if (level.getBlockEntity(pos) instanceof CraftingBowlBlockEntity bowl) {
         ArrayList var10 = new ArrayList();
         ItemStack result = bowl.getItem(4);
         if (!result.isEmpty()) {
            var10.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.result").withStyle(ChatFormatting.GREEN), List.of(result), 6));
            var10.add(InfoSection.title(Component.translatable("hud.farm_and_charm.take_hint").withStyle(ChatFormatting.GRAY)));
            return var10;
         }

         List<ItemStack> ingredients = new ArrayList<>();

         for (int slot = 0; slot < 4; slot++) {
            if (!bowl.getItem(slot).isEmpty()) {
               ingredients.add(bowl.getItem(slot));
            }
         }

         if (ingredients.isEmpty()) {
            return var10;
         }

         var10.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.in_bowl"), ingredients, 6).withDecorations());
         bowl.findRecipe(level).ifPresent(recipe -> {
            ItemStack output = recipe.getResultItem(level.registryAccess()).copyWithCount(recipe.getOutputCount());
            var10.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.result").withStyle(ChatFormatting.GREEN), List.of(output), 6));
            int stirred = (int)bowl.getStirred();
            var10.add(InfoSection.title(Component.translatable("hud.farm_and_charm.stirred", new Object[]{stirred, 200}).withStyle(ChatFormatting.GRAY)));
            var10.add(InfoSection.title(Component.translatable("hud.farm_and_charm.stir_hint").withStyle(ChatFormatting.GRAY)));
         });
         return var10;
      } else {
         return List.of();
      }
   }
}
