package com.archiangel.quadhotbar;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class QuadHotbarPages {
   public static final int PAGE_SIZE = 36;
   public static final int MAX_PAGES = 100;
   public static final int MAX_HOTBAR_ROWS = 8;
   private static final String TAG = "QuadHotbarPages";
   private static final ReferenceQueue<Player> STALE_PLAYERS = new ReferenceQueue<>();
   private static final Map<QuadHotbarPages.IdentityWeakReference, QuadHotbarPages.State> STATES = new HashMap<>();

   private QuadHotbarPages() {
   }

   public static int pageStep(int rows) {
      return switch (rows) {
         case 3, 6 -> 3;
         default -> 1;
         case 5 -> 5;
         case 7 -> 7;
         case 8 -> 2;
      };
   }

   public static int maximumInventoryPages(int rows) {
      int step = pageStep(rows);
      return 100 - 100 % step;
   }

   public static int alignInventoryPages(int requested, int rows) {
      int step = pageStep(rows);
      int clamped = Mth.clamp(requested, 1, 100);
      int roundedUp = (clamped + step - 1) / step * step;
      return roundedUp <= 100 ? roundedUp : maximumInventoryPages(rows);
   }

   public static QuadHotbarPages.PageSettings limitSettings(int requestedPages, int requestedRows, int maximumPages, int maximumRows) {
      int pageCap = Mth.clamp(maximumPages, 1, 100);
      int rows = Mth.clamp(requestedRows, 2, Mth.clamp(maximumRows, 2, 8));

      while (pageStep(rows) > pageCap) {
         rows--;
      }

      int step = pageStep(rows);
      int largestCompletePageCount = pageCap / step * step;
      int pages = Math.min(alignInventoryPages(Mth.clamp(requestedPages, 1, pageCap), rows), largestCompletePageCount);
      return new QuadHotbarPages.PageSettings(pages, rows);
   }

   public static synchronized QuadHotbarPages.State state(Player player) {
      expungeStalePlayers();
      QuadHotbarPages.IdentityWeakReference lookup = new QuadHotbarPages.IdentityWeakReference(player, null);
      QuadHotbarPages.State state = STATES.get(lookup);
      if (state == null) {
         state = new QuadHotbarPages.State();
         STATES.put(new QuadHotbarPages.IdentityWeakReference(player, STALE_PLAYERS), state);
      }

      return state;
   }

   public static ItemStack visibleStack(Player player, int row, int column) {
      QuadHotbarPages.State state = state(player);
      int globalRow = state.hotbarPage * state.hotbarRows + row;
      if (globalRow >= state.pageCount * 4) {
         return ItemStack.EMPTY;
      }

      int page = globalRow / 4;
      int slot = state.inventoryRowOrder.inventoryRow(globalRow % 4) * 9 + column;
      return page == state.activePage ? (ItemStack)player.getInventory().items.get(slot) : state.pages.get(page).get(slot);
   }

   public static int pageForRow(QuadHotbarPages.State state, int row) {
      return (state.hotbarPage * state.hotbarRows + row) / 4;
   }

   public static int slotForRow(QuadHotbarPages.State state, int row, int column) {
      return state.inventoryRowOrder.inventoryRow((state.hotbarPage * state.hotbarRows + row) % 4) * 9 + column;
   }

   public static int orderedRow(QuadHotbarPages.State state, int page, int slot) {
      return page * 4 + state.inventoryRowOrder.hotbarRow(slot / 9);
   }

   public static int hotbarPageCount(QuadHotbarPages.State state) {
      return maximumHotbarPages(state);
   }

   public static int maximumHotbarPages(QuadHotbarPages.State state) {
      return Math.max(1, state.pageCount * 4 / state.hotbarRows);
   }

   public static int visibleRows(QuadHotbarPages.State state) {
      return Math.min(state.hotbarRows, state.pageCount * 4 - state.hotbarPage * state.hotbarRows);
   }

   public static void switchActivePage(Player player, int page) {
      QuadHotbarPages.State state = state(player);
      page = Mth.clamp(page, 0, state.pageCount - 1);
      if (state.activePage != page) {
         Inventory inventory = player.getInventory();
         List<ItemStack> old = state.pages.get(state.activePage);
         List<ItemStack> next = state.pages.get(page);

         for (int slot = 0; slot < 36; slot++) {
            old.set(slot, (ItemStack)inventory.items.get(slot));
            inventory.items.set(slot, next.get(slot));
            next.set(slot, ItemStack.EMPTY);
         }

         state.activePage = page;
         inventory.setChanged();
      }
   }

   public static List<ItemStack> snapshot(Player player, int page) {
      QuadHotbarPages.State state = state(player);
      List<ItemStack> source = (List<ItemStack>)(page == state.activePage ? player.getInventory().items : state.pages.get(page));
      List<ItemStack> snapshot = new ArrayList<>(36);

      for (ItemStack stack : source) {
         snapshot.add(stack.copy());
      }

      return snapshot;
   }

   public static ItemStack getPageItem(Player player, int page, int slot) {
      QuadHotbarPages.State state = state(player);
      return (ItemStack)(page == state.activePage ? player.getInventory().items : state.pages.get(page)).get(slot);
   }

   public static void setPageItem(Player player, int page, int slot, ItemStack stack) {
      QuadHotbarPages.State state = state(player);
      (page == state.activePage ? player.getInventory().items : state.pages.get(page)).set(slot, stack);
      player.getInventory().setChanged();
   }

   public static boolean addPickup(Player player, ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      }

      QuadHotbarPages.State state = state(player);
      Inventory inventory = player.getInventory();
      int before = stack.getCount();
      if (!stack.isDamaged()) {
         if (inventory.selected >= 0 && inventory.selected < 36) {
            mergePickup((ItemStack)inventory.items.get(inventory.selected), stack, inventory);
         }

         mergePickup(inventory.getItem(40), stack, inventory);

         for (ItemStack existing : inventory.items) {
            mergePickup(existing, stack, inventory);
         }

         for (int distance = 1; distance < state.pageCount && !stack.isEmpty(); distance++) {
            int lower = state.activePage - distance;
            int upper = state.activePage + distance;
            if (lower >= 0) {
               mergePage(state.pages.get(lower), stack, inventory);
            }

            if (upper < state.pageCount) {
               mergePage(state.pages.get(upper), stack, inventory);
            }
         }
      }

      fillEmptySlots(inventory.items, stack, inventory);

      for (int distance = 1; distance < state.pageCount && !stack.isEmpty(); distance++) {
         int lower = state.activePage - distance;
         int upper = state.activePage + distance;
         if (lower >= 0) {
            fillEmptySlots(state.pages.get(lower), stack, inventory);
         }

         if (upper < state.pageCount) {
            fillEmptySlots(state.pages.get(upper), stack, inventory);
         }
      }

      if (stack.getCount() < before) {
         inventory.setChanged();
         return true;
      } else {
         return false;
      }
   }

   private static void mergePage(List<ItemStack> page, ItemStack incoming, Inventory inventory) {
      for (ItemStack existing : page) {
         mergePickup(existing, incoming, inventory);
         if (incoming.isEmpty()) {
            return;
         }
      }
   }

   private static void mergePickup(ItemStack existing, ItemStack incoming, Inventory inventory) {
      if (!existing.isEmpty() && !incoming.isEmpty() && existing.isStackable() && ItemStack.isSameItemSameComponents(existing, incoming)) {
         int room = inventory.getMaxStackSize(existing) - existing.getCount();
         int moved = Math.min(room, incoming.getCount());
         if (moved > 0) {
            existing.grow(moved);
            existing.setPopTime(5);
            incoming.shrink(moved);
         }
      }
   }

   private static void fillEmptySlots(List<ItemStack> page, ItemStack incoming, Inventory inventory) {
      for (int slot = 0; slot < 36 && !incoming.isEmpty(); slot++) {
         if (page.get(slot).isEmpty()) {
            int moved = Math.min(incoming.getCount(), inventory.getMaxStackSize(incoming));
            if (moved > 0) {
               ItemStack placed = incoming.copyWithCount(moved);
               placed.setPopTime(5);
               page.set(slot, placed);
               incoming.shrink(moved);
            }
         }
      }
   }

   public static void dropStored(Player player) {
      QuadHotbarPages.State state = state(player);

      for (int page = 0; page < 100; page++) {
         if (page != state.activePage) {
            List<ItemStack> items = state.pages.get(page);

            for (int slot = 0; slot < 36; slot++) {
               ItemStack stack = items.get(slot);
               if (!stack.isEmpty()) {
                  if (!EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                     ItemEntity dropped = player.drop(stack, true, false);
                     if (dropped != null && QuadHotbarServerConfig.deathPageCompatibility()) {
                        dropped.getPersistentData().putInt("QuadHotbarPageSlot", page * 36 + slot);
                     }
                  }

                  items.set(slot, ItemStack.EMPTY);
               }
            }
         }
      }
   }

   public static void receiveSnapshot(
      Player player,
      int pageCount,
      int hotbarRows,
      int activePage,
      int hotbarPage,
      int selectedSlot,
      boolean bottomToTop,
      List<ItemStack> firstItems,
      List<ItemStack> secondItems,
      List<ItemStack> middleItems
   ) {
      QuadHotbarPages.State state = state(player);
      state.pageCount = Mth.clamp(pageCount, 1, 100);
      state.hotbarRows = Mth.clamp(hotbarRows, 2, state.pageCount >= 2 ? 8 : 4);
      state.activePage = Mth.clamp(activePage, 0, state.pageCount - 1);
      state.hotbarPage = Mth.clamp(hotbarPage, 0, hotbarPageCount(state) - 1);
      state.inventoryRowOrder = bottomToTop ? QuadHotbarLayout.InventoryOrder.BOTTOM_TO_TOP : QuadHotbarLayout.InventoryOrder.TOP_TO_BOTTOM;
      int firstPage = state.hotbarPage * state.hotbarRows / 4;
      int lastPage = (state.hotbarPage * state.hotbarRows + visibleRows(state) - 1) / 4;
      copyInto((List<ItemStack>)(firstPage == state.activePage ? player.getInventory().items : state.pages.get(firstPage)), firstItems);
      if (lastPage != firstPage) {
         copyInto((List<ItemStack>)(lastPage == state.activePage ? player.getInventory().items : state.pages.get(lastPage)), secondItems);
      }

      if (lastPage - firstPage == 2) {
         int middlePage = firstPage + 1;
         copyInto((List<ItemStack>)(middlePage == state.activePage ? player.getInventory().items : state.pages.get(middlePage)), middleItems);
      }

      Collections.fill(state.pages.get(state.activePage), ItemStack.EMPTY);
      player.getInventory().selected = Mth.clamp(selectedSlot, 0, 35);
   }

   private static void copyInto(List<ItemStack> target, List<ItemStack> source) {
      for (int slot = 0; slot < 36; slot++) {
         target.set(slot, slot < source.size() ? source.get(slot).copy() : ItemStack.EMPTY);
      }
   }

   public static void save(Player player, CompoundTag playerTag) {
      QuadHotbarPages.State state = state(player);
      CompoundTag tag = new CompoundTag();
      tag.putInt("ActivePage", state.activePage);
      tag.putInt("HotbarPage", state.hotbarPage);
      tag.putInt("HotbarRows", state.hotbarRows);
      tag.putInt("PageCount", state.pageCount);
      tag.putBoolean("BottomToTop", state.inventoryRowOrder == QuadHotbarLayout.InventoryOrder.BOTTOM_TO_TOP);
      ListTag pages = new ListTag();

      for (int page = 0; page < 100; page++) {
         if (page != state.activePage) {
            ListTag items = new ListTag();

            for (int slot = 0; slot < 36; slot++) {
               ItemStack stack = state.pages.get(page).get(slot);
               if (!stack.isEmpty()) {
                  CompoundTag itemTag = new CompoundTag();
                  itemTag.putByte("Slot", (byte)slot);
                  items.add(stack.save(player.registryAccess(), itemTag));
               }
            }

            if (!items.isEmpty()) {
               CompoundTag pageTag = new CompoundTag();
               pageTag.putInt("Page", page);
               pageTag.put("Items", items);
               pages.add(pageTag);
            }
         }
      }

      tag.put("StoredPages", pages);
      playerTag.put("QuadHotbarPages", tag);
   }

   public static void load(Player player, CompoundTag playerTag) {
      QuadHotbarPages.State state = new QuadHotbarPages.State();
      putState(player, state);
      if (playerTag.contains("QuadHotbarPages", 10)) {
         CompoundTag tag = playerTag.getCompound("QuadHotbarPages");
         state.pageCount = Mth.clamp(tag.getInt("PageCount"), 1, 100);
         state.activePage = Mth.clamp(tag.getInt("ActivePage"), 0, 99);
         state.hotbarRows = Mth.clamp(tag.getInt("HotbarRows"), 2, state.pageCount >= 2 ? 8 : 4);
         state.hotbarPage = Mth.clamp(tag.getInt("HotbarPage"), 0, hotbarPageCount(state) - 1);
         state.inventoryRowOrder = tag.getBoolean("BottomToTop")
            ? QuadHotbarLayout.InventoryOrder.BOTTOM_TO_TOP
            : QuadHotbarLayout.InventoryOrder.TOP_TO_BOTTOM;
         ListTag pages = tag.getList("StoredPages", 10);

         for (int i = 0; i < pages.size(); i++) {
            CompoundTag pageTag = pages.getCompound(i);
            int page = pageTag.getInt("Page");
            if (page >= 0 && page < 100 && page != state.activePage) {
               ListTag items = pageTag.getList("Items", 10);

               for (int j = 0; j < items.size(); j++) {
                  CompoundTag itemTag = items.getCompound(j);
                  int slot = itemTag.getByte("Slot") & 255;
                  if (slot < 36) {
                     state.pages.get(page).set(slot, ItemStack.parse(player.registryAccess(), itemTag).orElse(ItemStack.EMPTY));
                  }
               }
            }
         }
      }
   }

   public static synchronized void clear(Player player) {
      expungeStalePlayers();
      STATES.remove(new QuadHotbarPages.IdentityWeakReference(player, null));
   }

   private static synchronized void putState(Player player, QuadHotbarPages.State state) {
      expungeStalePlayers();
      STATES.put(new QuadHotbarPages.IdentityWeakReference(player, STALE_PLAYERS), state);
   }

   private static void expungeStalePlayers() {
      Reference<? extends Player> stale;
      while ((stale = STALE_PLAYERS.poll()) != null) {
         STATES.remove(stale);
      }
   }

   private static final class IdentityWeakReference extends WeakReference<Player> {
      private final int identityHash;

      private IdentityWeakReference(Player player, ReferenceQueue<Player> queue) {
         super(player, queue);
         this.identityHash = System.identityHashCode(player);
      }

      @Override
      public int hashCode() {
         return this.identityHash;
      }

      @Override
      public boolean equals(Object object) {
         return this == object ? true : object instanceof QuadHotbarPages.IdentityWeakReference other && this.get() != null && this.get() == other.get();
      }
   }

   public record PageSettings(int pages, int rows) {
   }

   public static final class State {
      public int activePage;
      public int hotbarPage;
      public int hotbarRows = 2;
      public int pageCount = 1;
      public QuadHotbarLayout.InventoryOrder inventoryRowOrder = QuadHotbarLayout.InventoryOrder.TOP_TO_BOTTOM;
      private final List<List<ItemStack>> pages = new ArrayList<>(100);

      private State() {
         for (int page = 0; page < 100; page++) {
            List<ItemStack> items = new ArrayList<>(36);

            for (int slot = 0; slot < 36; slot++) {
               items.add(ItemStack.EMPTY);
            }

            this.pages.add(items);
         }
      }
   }
}
