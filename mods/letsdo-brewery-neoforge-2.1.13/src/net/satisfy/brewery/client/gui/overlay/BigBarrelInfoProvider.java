package net.satisfy.brewery.client.gui.overlay;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.brewery.core.block.BigBarrelBlock;
import net.satisfy.brewery.core.block.entity.BigBarrelBlockEntity;
import net.satisfy.brewery.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.foundation.overlay.InfoSection.Row;
import org.jetbrains.annotations.Nullable;

public class BigBarrelInfoProvider implements BlockInfoProvider {
   private static final ResourceLocation DUNGAREES = ResourceLocation.fromNamespaceAndPath("farm_and_charm", "dungarees");

   public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
      if (state.getBlock() instanceof BigBarrelBlock && !isHidden()) {
         if (level.getBlockEntity(BigBarrelBlock.mainPos(state, pos)) instanceof BigBarrelBlockEntity barrel && !barrel.isEmpty()) {
            List<Row> rows = new ArrayList<>();

            for (int slot = 0; slot < 9; slot++) {
               ItemStack drink = barrel.getDrink(slot);
               if (!drink.isEmpty()) {
                  Component status = barrel.getQuality(slot) >= 4
                     ? Component.translatable("hud.brewery.barrel_aged").withStyle(ChatFormatting.GOLD)
                     : Component.translatable("hud.brewery.barrel_aging", new Object[]{barrel.getAgingPercent(slot)}).withStyle(ChatFormatting.YELLOW);
                  Component name = drink.getHoverName().copy().append(drink.getCount() > 1 ? " ×" + drink.getCount() : "");
                  rows.add(Row.item(drink, name.copy().append(" ").append(status)));
               }
            }

            return List.of(
               InfoSection.rows(Component.translatable("hud.brewery.in_barrel"), rows),
               InfoSection.title(Component.translatable("hud.brewery.barrel_hint").withStyle(ChatFormatting.GRAY))
            );
         } else {
            return List.of();
         }
      } else {
         return List.of();
      }
   }

   private static boolean isHidden() {
      if (!PlatformHelper.showBigBarrelInfo()) {
         return true;
      }

      if (!PlatformHelper.infoTooltipsNeedDungarees()) {
         return false;
      }

      Player player = Minecraft.getInstance().player;
      return player == null || !BuiltInRegistries.ITEM.getKey(player.getItemBySlot(EquipmentSlot.LEGS).getItem()).equals(DUNGAREES);
   }
}
