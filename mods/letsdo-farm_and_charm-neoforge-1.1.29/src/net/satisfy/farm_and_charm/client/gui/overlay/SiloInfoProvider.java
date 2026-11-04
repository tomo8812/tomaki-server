package net.satisfy.farm_and_charm.client.gui.overlay;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.SiloBlock;
import net.satisfy.farm_and_charm.core.block.entity.SiloBlockEntity;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

public class SiloInfoProvider implements BlockInfoProvider {
   public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
      if (InfoTooltips.isHidden(PlatformHelper.showSiloInfo())) {
         return List.of();
      } else if (!(
         level.getBlockEntity(pos) instanceof SiloBlockEntity silo && level.getBlockEntity(silo.getController()) instanceof SiloBlockEntity controller
      )) {
         return List.of();
      } else {
         if (!isHatch(pos, state, controller)) {
            return List.of();
         }

         int capacity = controller.getCapacity();
         List<ItemStack> drying = collect(controller, 0, capacity);
         List<ItemStack> dried = collect(controller, 81, 81 + capacity);
         int used = drying.stream().mapToInt(ItemStack::getCount).sum();
         List<InfoSection> sections = new ArrayList<>();
         sections.add(InfoSection.title(Component.translatable("hud.farm_and_charm.silo_filled", new Object[]{used, capacity}).withStyle(ChatFormatting.GRAY)));
         if (!drying.isEmpty()) {
            sections.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.silo_drying"), drying, 6).withDecorations());
         }

         if (!dried.isEmpty()) {
            sections.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.silo_dried").withStyle(ChatFormatting.GREEN), dried, 6).withDecorations());
            if (!(Boolean)state.getValue(SiloBlock.OPEN)) {
               sections.add(InfoSection.title(Component.translatable("hud.farm_and_charm.silo_open_hint").withStyle(ChatFormatting.GRAY)));
            }
         }

         return sections;
      }
   }

   private static boolean isHatch(BlockPos pos, BlockState state, SiloBlockEntity controller) {
      BlockPos origin = controller.getBlockPos();
      if (pos.getY() != origin.getY()) {
         return false;
      }

      int last = controller.getWidth() - 1;

      return switch ((Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
         case NORTH -> pos.getZ() == origin.getZ();
         case SOUTH -> pos.getZ() == origin.getZ() + last;
         case WEST -> pos.getX() == origin.getX();
         case EAST -> pos.getX() == origin.getX() + last;
         default -> false;
      };
   }

   private static List<ItemStack> collect(SiloBlockEntity silo, int from, int to) {
      List<ItemStack> result = new ArrayList<>();

      for (int slot = from; slot < to; slot++) {
         ItemStack stack = silo.getItem(slot);
         if (!stack.isEmpty()) {
            result.stream()
               .filter(existing -> ItemStack.isSameItemSameComponents(existing, stack))
               .findFirst()
               .ifPresentOrElse(existing -> existing.grow(stack.getCount()), () -> result.add(stack.copy()));
         }
      }

      return result;
   }
}
