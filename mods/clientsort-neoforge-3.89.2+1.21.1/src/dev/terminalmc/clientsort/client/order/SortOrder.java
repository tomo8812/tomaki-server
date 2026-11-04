package dev.terminalmc.clientsort.client.order;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.config.Config;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class SortOrder {
   public final String name;
   public static final Map<String, SortOrder> SORT_ORDERS = new HashMap<>();
   public static final SortOrder NONE = register(new SortOrder("none") {});
   public static final SortOrder ALPHABET = register(new SortOrder("alphabet") {
      @Override
      public int[] sort(int[] slotIds, ItemStack[] stacks, SortContext context) {
         String[] strings = new String[slotIds.length];

         for (int i = 0; i < slotIds.length; i++) {
            ItemStack stack = stacks[i];
            strings[i] = stack.isEmpty() ? "" : stack.getHoverName().getString();
         }

         IntArrays.quickSort(slotIds, (a, b) -> {
            int cmp = SortOrder.comparePresetPriority(stacks[a], stacks[b]);
            if (cmp != 0) {
               return cmp;
            }

            if (strings[a].isEmpty()) {
               return strings[b].isEmpty() ? 0 : 1;
            }

            if (strings[b].isEmpty()) {
               return -1;
            }

            cmp = strings[a].compareToIgnoreCase(strings[b]);
            return cmp != 0 ? cmp : StackComparison.compareEqualItems(stacks[a], stacks[b], context);
         });
         return slotIds;
      }
   });
   public static final SortOrder CREATIVE = register(new SortOrder("creative") {
      @Override
      public int[] sort(int[] slotIds, ItemStack[] stacks, SortContext context) {
         int[] sortValues = new int[slotIds.length];
         if (Config.options().optimizeCreativeSorting && ClientSort.searchOrderUpdated) {
            Lock readLock = CreativeSearchOrder.getReadLock();
            readLock.lock();

            try {
               for (int i = 0; i < stacks.length; i++) {
                  sortValues[i] = CreativeSearchOrder.getPositionUnsafe(stacks[i]);
               }
            } finally {
               readLock.unlock();
            }
         } else {
            Collection<ItemStack> creativeStacks = CreativeModeTabs.searchTab().getSearchTabDisplayItems();
            Object2IntMap<StackMatcher> lookup = new Object2IntOpenHashMap(stacks.length);

            for (int i = 0; i < stacks.length; i++) {
               ItemStack stack = stacks[i];
               sortValues[i] = lookup.computeIfAbsent(StackMatcher.of(stack), matcher -> {
                  int j = 0;
                  int jPlain = Integer.MAX_VALUE;
                  StackMatcher plainMatcher = StackMatcher.plain(stack);

                  for (ItemStack creativeStack : creativeStacks) {
                     if (matcher.equals(creativeStack)) {
                        return j;
                     }

                     if (plainMatcher.equals(creativeStack)) {
                        jPlain = j;
                     }

                     j++;
                  }

                  return jPlain;
               });
            }

            if (Config.options().optimizeCreativeSorting) {
               ClientSort.searchOrderUpdated = true;
               CreativeSearchOrder.tryRefreshStackPositionMap();
            }
         }

         SortOrder.sortByValues(slotIds, sortValues, stacks, context);
         return slotIds;
      }
   });
   public static final SortOrder QUANTITY = register(
      new SortOrder("quantity") {
         @Override
         public int[] sort(int[] slotIds, ItemStack[] stacks, SortContext context) {
            HashMap<Item, Integer> itemTotalAmountMap = new HashMap<>();

            for (ItemStack stack : stacks) {
               if (!stack.isEmpty()) {
                  itemTotalAmountMap.merge(stack.getItem(), stack.getCount(), Integer::sum);
               }
            }

            IntArrays.quickSort(
               slotIds,
               (a, b) -> {
                  int cmp = SortOrder.comparePresetPriority(stacks[a], stacks[b]);
                  if (cmp != 0) {
                     return cmp;
                  } else {
                     ItemStack stackA = stacks[a];
                     ItemStack stackB = stacks[b];
                     if (stackA.isEmpty()) {
                        return stackB.isEmpty() ? 0 : 1;
                     } else if (stackB.isEmpty()) {
                        return -1;
                     } else {
                        Integer amountA = itemTotalAmountMap.get(stackA.getItem());
                        Integer amountB = itemTotalAmountMap.get(stackB.getItem());
                        cmp = Integer.compare(amountB, amountA);
                        if (cmp != 0) {
                           return cmp;
                        } else {
                           return ItemStack.isSameItemSameComponents(stackA, stackB)
                              ? StackComparison.compareEqualItems(stackA, stackB, context)
                              : StackComparison.compareEqualItems(stackA.copyWithCount(1), stackB.copyWithCount(1), context);
                        }
                     }
                  }
               }
            );
            return slotIds;
         }
      }
   );
   public static final SortOrder RAW_ID = register(new SortOrder("rawId") {
      @Override
      public int[] sort(int[] slotIds, ItemStack[] stacks, SortContext context) {
         int[] values = Arrays.stream(stacks).mapToInt(stack -> stack.isEmpty() ? Integer.MAX_VALUE : BuiltInRegistries.ITEM.getId(stack.getItem())).toArray();
         SortOrder.sortByValues(slotIds, values, stacks, context);
         return slotIds;
      }
   });

   protected SortOrder(String name) {
      this.name = name;
   }

   public int[] sort(int[] slotIds, ItemStack[] stacks, SortContext context) {
      return slotIds;
   }

   public static <T extends SortOrder> T register(T sortOrder) {
      SORT_ORDERS.put(sortOrder.name, sortOrder);
      return sortOrder;
   }

   private static void sortByValues(int[] sortIds, int[] values, ItemStack[] stacks, SortContext context) {
      IntArrays.quickSort(sortIds, (a, b) -> {
         int cmp = comparePresetPriority(stacks[a], stacks[b]);
         if (cmp != 0) {
            return cmp;
         }

         cmp = Integer.compare(values[a], values[b]);
         return cmp != 0 ? cmp : StackComparison.compareEqualItems(stacks[a], stacks[b], context);
      });
   }

   private static int comparePresetPriority(ItemStack stackA, ItemStack stackB) {
      if (stackA.isEmpty()) {
         return stackB.isEmpty() ? 0 : 1;
      }

      if (stackB.isEmpty()) {
         return -1;
      }

      Item a = stackA.getItem();
      Item b = stackB.getItem();
      if (Config.options().useStartOverrides) {
         Integer idxA = Config.options().startOverrideMap.get(a);
         Integer idxB = Config.options().startOverrideMap.get(b);
         if (idxA != null || idxB != null) {
            if (idxA != null && idxB != null) {
               return Integer.compare(idxA, idxB);
            } else {
               return idxA != null ? -1 : 1;
            }
         }
      }

      if (Config.options().useEndOverrides) {
         Integer idxA = Config.options().endOverrideMap.get(a);
         Integer idxB = Config.options().endOverrideMap.get(b);
         if (idxA != null || idxB != null) {
            if (idxA != null && idxB != null) {
               return Integer.compare(idxA, idxB);
            }

            return idxA != null ? 1 : -1;
         }
      }

      return 0;
   }
}
