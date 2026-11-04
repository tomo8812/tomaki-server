package net.satisfy.candlelight.client.overlay;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.candlelight.core.block.TableSetBlock;
import net.satisfy.candlelight.core.block.entity.TableSetBlockEntity;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import net.satisfy.candlelight.core.util.Wearables;
import net.satisfy.candlelight.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.foundation.overlay.InfoSection.Row;
import org.jetbrains.annotations.Nullable;

public class TableSetInfoProvider implements BlockInfoProvider {
   public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
      if (PlatformHelper.showTableSetInfo() && state.getBlock() instanceof TableSetBlock && level.getBlockEntity(pos) instanceof TableSetBlockEntity tableSet) {
         Player player = Minecraft.getInstance().player;
         if (player == null) {
            return List.of();
         }

         List<InfoSection> sections = new ArrayList<>();
         ItemStack dish = tableSet.getInventory().isEmpty() ? ItemStack.EMPTY : (ItemStack)tableSet.getInventory().get(0);
         if (dish.isEmpty()) {
            sections.add(InfoSection.title(Component.translatable("hud.candlelight.table_set.no_dish").withStyle(ChatFormatting.GOLD)));
         } else {
            Component note = TableSetBlock.isServedDish(dish)
               ? Component.translatable("hud.candlelight.table_set.served_dish").withStyle(ChatFormatting.GREEN)
               : Component.translatable("hud.candlelight.table_set.plain_food").withStyle(ChatFormatting.GRAY);
            sections.add(InfoSection.rows(Component.translatable("hud.candlelight.table_set.dish"), List.of(Row.item(dish, note))));
         }

         int courses = TableSetBlock.countCourses(level, pos, state, player);
         List<Row> rows = new ArrayList<>();
         rows.add(row(((Item)ObjectRegistry.NAPKIN.get()).getDefaultInstance(), state, TableSetBlock.NAPKIN, "napkin"));
         rows.add(row(((Item)ObjectRegistry.GLASS.get()).getDefaultInstance(), state, TableSetBlock.GLASS, "glass"));
         rows.add(row(((Item)ObjectRegistry.WINE_GLASS.get()).getDefaultInstance(), state, TableSetBlock.WINE_GLASS, "wine_glass"));
         rows.add(Row.item(((Item)ObjectRegistry.GLASS.get()).getDefaultInstance(), status(TableSetBlock.hasDrink(state), "drink")));
         rows.add(Row.item(Items.CANDLE.getDefaultInstance(), status(TableSetBlock.hasLight(level, pos), "light")));
         rows.add(Row.item(((Item)ObjectRegistry.DRESS.get()).getDefaultInstance(), status(Wearables.isElegantlyDressed(player), "outfit")));
         sections.add(InfoSection.rows(Component.translatable("hud.candlelight.table_set.courses", new Object[]{TableSetBlock.rating(courses)}), rows));
         if ((Boolean)state.getValue(TableSetBlock.CLOCHE)) {
            sections.add(InfoSection.title(Component.translatable("hud.candlelight.table_set.cloche").withStyle(ChatFormatting.GOLD)));
            sections.add(InfoSection.title(Component.translatable("hud.candlelight.table_set.cloche_hint").withStyle(ChatFormatting.GRAY)));
         }

         return sections;
      } else {
         return List.of();
      }
   }

   private static Row row(ItemStack icon, BlockState state, BooleanProperty property, String key) {
      return Row.item(icon, status((Boolean)state.getValue(property), key));
   }

   private static Component status(boolean done, String key) {
      Component name = Component.translatable("hud.candlelight.table_set." + key);
      return done ? Component.literal("✔ ").append(name).withStyle(ChatFormatting.GREEN) : Component.literal("✘ ").append(name).withStyle(ChatFormatting.GRAY);
   }
}
