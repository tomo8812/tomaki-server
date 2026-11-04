package dev.terminalmc.clientsort.client.order;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.mixin.client.accessor.CreativeModeTabsAccessor;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

public class CreativeSearchOrder {
   private static final Object2IntMap<StackMatcher> stackPositionMap = new Object2IntOpenHashMap();
   private static final ReadWriteLock stackPositionMapLock = new ReentrantReadWriteLock();

   public static Lock getReadLock() {
      return stackPositionMapLock.readLock();
   }

   public static int getPositionUnsafe(ItemStack stack) {
      int pos = stackPositionMap.getOrDefault(StackMatcher.of(stack), Integer.MAX_VALUE);
      if (pos == Integer.MAX_VALUE) {
         pos = stackPositionMap.getOrDefault(StackMatcher.plain(stack), Integer.MAX_VALUE);
      }

      return pos;
   }

   public static void tryRefreshStackPositionMap() {
      if (ClientSort.emiReloading) {
         ClientSort.updateBlockedByEmi = true;
         ClientSort.LOG.info("Search order update blocked by EMI reload, waiting...");
      } else {
         refreshStackPositionMap();
      }
   }

   private static void refreshStackPositionMap() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && mc.player != null) {
         FeatureFlagSet enabledFeatures = mc.level.enabledFeatures();

         Collection<ItemStack> displayStacks;
         try {
            CreativeModeTabs.tryRebuildTabContents(enabledFeatures, true, mc.level.registryAccess());
            displayStacks = CreativeModeTabs.searchTab().getDisplayItems().stream().<ItemStack>map(ItemStack::copy).toList();
         } finally {
            CreativeModeTabsAccessor.clientsort$setCachedParameters(null);
         }

         Thread.ofVirtual().name("clientsort-creative-sort-builder").start(() -> {
            Lock writeLock = stackPositionMapLock.writeLock();

            try {
               writeLock.lock();
               stackPositionMap.clear();
               int i = 0;

               for (ItemStack stack : displayStacks) {
                  stackPositionMap.put(StackMatcher.of(stack), i);
                  i++;
               }
            } finally {
               writeLock.unlock();
            }
         });
      }
   }

   static {
      stackPositionMap.defaultReturnValue(Integer.MAX_VALUE);
   }
}
