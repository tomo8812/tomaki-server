package dev.terminalmc.clientsort.client.order;

import java.awt.Color;
import java.util.Iterator;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.item.component.DyedItemColor;

public class StackComparison {
   public static int compareEqualItems(ItemStack a, ItemStack b, SortContext context) {
      int cmp = Integer.compare(b.getCount(), a.getCount());
      return cmp != 0 ? cmp : compareEqualItems2(a, b, context);
   }

   private static int compareEqualItems2(ItemStack a, ItemStack b, SortContext context) {
      if (hasCustomHoverName(a)) {
         return !hasCustomHoverName(b) ? -1 : compareEqualItems3(a, b, context);
      } else {
         return hasCustomHoverName(b) ? 1 : compareEqualItems3(a, b, context);
      }
   }

   private static boolean hasCustomHoverName(ItemStack itemStack) {
      return itemStack.get(DataComponents.CUSTOM_NAME) != null;
   }

   private static int compareEqualItems3(ItemStack a, ItemStack b, SortContext context) {
      Iterator<Component> tooltipsA = a.getTooltipLines(TooltipContext.of(context.level), null, Default.NORMAL).iterator();
      Iterator<Component> tooltipsB = b.getTooltipLines(TooltipContext.of(context.level), null, Default.NORMAL).iterator();

      while (tooltipsA.hasNext()) {
         if (!tooltipsB.hasNext()) {
            return 1;
         }

         int cmp = tooltipsA.next().getString().compareToIgnoreCase(tooltipsB.next().getString());
         if (cmp != 0) {
            return cmp;
         }
      }

      return tooltipsB.hasNext() ? -1 : compareEqualItems4(a, b, context);
   }

   private static int compareEqualItems4(ItemStack a, ItemStack b, SortContext context) {
      Item item = a.getItem();
      if (item.getDefaultInstance().is(ItemTags.DYEABLE)) {
         int colorA = DyedItemColor.getOrDefault(a, -6265536);
         int colorB = DyedItemColor.getOrDefault(b, -6265536);
         float[] hsbA = Color.RGBtoHSB(colorA >> 16 & 0xFF, colorA >> 8 & 0xFF, colorA & 0xFF, null);
         float[] hsbB = Color.RGBtoHSB(colorB >> 16 & 0xFF, colorB >> 8 & 0xFF, colorB & 0xFF, null);
         int cmp = Float.compare(hsbA[0], hsbB[0]);
         if (cmp != 0) {
            return cmp;
         }

         cmp = Float.compare(hsbA[1], hsbB[1]);
         if (cmp != 0) {
            return cmp;
         }

         cmp = Float.compare(hsbA[2], hsbB[2]);
         if (cmp != 0) {
            return cmp;
         }
      }

      return compareEqualItems5(a, b, context);
   }

   private static int compareEqualItems5(ItemStack a, ItemStack b, SortContext context) {
      return Integer.compare(a.getDamageValue(), b.getDamageValue());
   }
}
