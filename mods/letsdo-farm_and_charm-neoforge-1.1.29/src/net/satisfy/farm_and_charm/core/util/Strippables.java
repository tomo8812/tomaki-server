package net.satisfy.farm_and_charm.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.core.item.CleaverItem;

public final class Strippables extends AxeItem {
   public static final int CHOPS = 2;

   private Strippables(Tier tier, Properties properties) {
      super(tier, properties);
   }

   public static boolean isAxe(ItemStack stack) {
      return stack.is(ItemTags.AXES) && !(stack.getItem() instanceof CleaverItem);
   }

   public static List<Strippables.Entry> all() {
      List<Strippables.Entry> entries = new ArrayList<>();

      for (Map.Entry<Block, Block> entry : STRIPPABLES.entrySet()) {
         ItemStack log = new ItemStack((ItemLike)entry.getKey());
         ItemStack stripped = new ItemStack((ItemLike)entry.getValue());
         if (!log.isEmpty() && !stripped.isEmpty() && !ItemStack.isSameItem(log, stripped)) {
            entries.add(new Strippables.Entry(log, stripped));
         }
      }

      return entries;
   }

   public static Optional<ItemStack> strip(ItemStack stack) {
      if (stack.getItem() instanceof BlockItem blockItem) {
         Block stripped = (Block)STRIPPABLES.get(blockItem.getBlock());
         if (stripped != null && stripped.asItem() != blockItem) {
            ItemStack result = new ItemStack(stripped);
            return result.isEmpty() ? Optional.empty() : Optional.of(result);
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public record Entry(ItemStack log, ItemStack stripped) {
   }
}
