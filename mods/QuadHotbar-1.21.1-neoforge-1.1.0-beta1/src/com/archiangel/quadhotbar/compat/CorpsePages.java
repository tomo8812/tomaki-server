package com.archiangel.quadhotbar.compat;

import com.archiangel.quadhotbar.QuadHotbarServerConfig;
import com.mojang.logging.LogUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class CorpsePages {
   private CorpsePages() {
   }

   public static boolean transfer(AbstractContainerMenu menu, ServerPlayer player) {
      if (player.containerMenu == menu && menu.stillValid(player) && QuadHotbarServerConfig.deathPageCompatibility()) {
         try {
            if (!(Boolean)menu.getClass().getMethod("isEditable").invoke(menu)) {
               return false;
            }

            Object corpse = menu.getClass().getMethod("getCorpse").invoke(menu);
            Object death = corpse.getClass().getMethod("getDeath").invoke(corpse);
            boolean main = menu.getClass().getName().endsWith(".CorpseInventoryContainer");
            Method openAdditional = main
               ? Class.forName("de.maxhenkel.corpse.gui.Guis").getMethod("openAdditionalItems", ServerPlayer.class, menu.getClass())
               : null;
            NonNullList<ItemStack> remainder = DeathPageRecovery.restore(player, death, main);
            if (remainder == null) {
               return false;
            }

            menu.broadcastFullState();
            if (main && !remainder.isEmpty()) {
               openAdditional.invoke(null, player, menu);
            }

            return true;
         } catch (ReflectiveOperationException exception) {
            LogUtils.getLogger().warn("Could not adapt Corpse take-all; using its native transfer", exception);
            return false;
         }
      } else {
         return false;
      }
   }

   public static CorpsePages.View find(AbstractContainerMenu menu) {
      if (QuadHotbarServerConfig.deathPageCompatibility() && menu.getClass().getName().startsWith("de.maxhenkel.corpse.gui.")) {
         try {
            Object corpse = menu.getClass().getMethod("getCorpse").invoke(menu);
            Object death = corpse.getClass().getMethod("getDeath").invoke(corpse);
            if (death instanceof DeathPageAccess access) {
               List<ItemStack> items = (List<ItemStack>)death.getClass().getMethod("getAdditionalItems").invoke(death);
               int[] positions = access.quadhotbar$pageSlots();
               int pages = 0;

               for (int i = 0; i < Math.min(items.size(), positions.length); i++) {
                  if (!items.get(i).isEmpty() && positions[i] >= 0) {
                     pages = Math.max(pages, positions[i] / 36 + 1);
                  }
               }

               if (pages == 0) {
                  return null;
               }

               List<ItemStack> main = (List<ItemStack>)death.getClass().getMethod("getMainInventory").invoke(death);
               int active = access.quadhotbar$activePage();
               if (active >= 0) {
                  pages = Math.max(pages, active + 1);
               }

               pages = Math.min(pages, 100);
               boolean editable = (Boolean)menu.getClass().getMethod("isEditable").invoke(menu);
               return new CorpsePages.View(new CorpsePages.DeathContainer(items, positions, main, active, pages * 36, menu), pages, editable);
            } else {
               return null;
            }
         } catch (ReflectiveOperationException exception) {
            LogUtils.getLogger().warn("Could not adapt this Corpse inventory; its native item GUI remains available", exception);
            return null;
         }
      } else {
         return null;
      }
   }

   private static final class DeathContainer implements Container {
      private final List<ItemStack> items;
      private final int[] indices;
      private final ItemStack[] identities;
      private final AbstractContainerMenu parent;
      private final List<ItemStack> main;
      private final int active;

      DeathContainer(List<ItemStack> items, int[] positions, List<ItemStack> main, int active, int size, AbstractContainerMenu parent) {
         this.items = items;
         this.parent = parent;
         this.main = main;
         this.active = active;
         this.indices = new int[size];
         Arrays.fill(this.indices, -1);
         this.identities = new ItemStack[size];

         for (int i = 0; i < Math.min(items.size(), positions.length); i++) {
            int address = positions[i];
            if (address >= 0 && address < size && this.indices[address] < 0) {
               this.indices[address] = i;
               this.identities[address] = items.get(i);
            }
         }
      }

      public int getContainerSize() {
         return this.indices.length;
      }

      public boolean isEmpty() {
         for (int i = 0; i < this.indices.length; i++) {
            if (!this.getItem(i).isEmpty()) {
               return false;
            }
         }

         return true;
      }

      public ItemStack getItem(int slot) {
         if (this.isMainSlot(slot)) {
            return this.main.get(slot % 36);
         }

         int index = slot >= 0 && slot < this.indices.length ? this.indices[slot] : -1;
         return index >= 0 && index < this.items.size() && this.items.get(index) == this.identities[slot] ? this.items.get(index) : ItemStack.EMPTY;
      }

      public ItemStack removeItem(int slot, int amount) {
         return this.getItem(slot).split(amount);
      }

      public ItemStack removeItemNoUpdate(int slot) {
         ItemStack stack = this.getItem(slot);
         this.setItem(slot, ItemStack.EMPTY);
         return stack;
      }

      public void setItem(int slot, ItemStack stack) {
         if (this.isMainSlot(slot)) {
            this.main.set(slot % 36, stack);
         } else {
            if (slot >= 0
               && slot < this.indices.length
               && this.indices[slot] >= 0
               && this.indices[slot] < this.items.size()
               && this.items.get(this.indices[slot]) == this.identities[slot]) {
               this.items.set(this.indices[slot], stack);
               this.identities[slot] = stack;
            }
         }
      }

      public void setChanged() {
      }

      private boolean isMainSlot(int slot) {
         return slot >= 0
            && slot < this.indices.length
            && this.indices[slot] < 0
            && this.active >= 0
            && slot / 36 == this.active
            && slot % 36 < this.main.size();
      }

      public boolean stillValid(Player player) {
         return player.containerMenu == this.parent && this.parent.stillValid(player);
      }

      public void clearContent() {
         for (int i = 0; i < this.indices.length; i++) {
            this.setItem(i, ItemStack.EMPTY);
         }
      }
   }

   public record View(Container storage, int pages, boolean editable) {
   }
}
