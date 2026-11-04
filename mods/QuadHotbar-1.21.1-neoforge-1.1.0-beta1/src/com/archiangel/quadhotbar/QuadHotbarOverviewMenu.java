package com.archiangel.quadhotbar;

import com.archiangel.quadhotbar.mixin.FurnaceMenuAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class QuadHotbarOverviewMenu extends AbstractContainerMenu {
   public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "quadhotbar");
   public static final DeferredHolder<MenuType<?>, MenuType<QuadHotbarOverviewMenu>> TYPE = MENUS.register(
      "page_overview", () -> IMenuTypeExtension.create(QuadHotbarOverviewMenu::fromNetwork)
   );
   public final int pageCount;
   public int activePage;
   public final boolean standalone;
   public final int columns;
   public final int viewportRows;
   public final int pageSlots;
   private final AbstractContainerMenu inventoryMenu;
   public final boolean external;
   public final boolean deathInventory;
   public boolean returningToInventory;

   private static QuadHotbarOverviewMenu fromNetwork(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
      return new QuadHotbarOverviewMenu(id, inventory, data.readVarInt(), data.readVarInt(), data.readBoolean(), data.readVarInt(), data.readVarInt());
   }

   public QuadHotbarOverviewMenu(int id, Inventory inventory, int pages, int active, boolean standalone, int columns, int height) {
      this(id, inventory, pages, active, standalone, columns, height, inventory.player.inventoryMenu, null, false);
   }

   public static QuadHotbarOverviewMenu external(
      int id, Inventory inventory, int pages, int active, int height, AbstractContainerMenu parent, Container storage, boolean death
   ) {
      return new QuadHotbarOverviewMenu(id, inventory, pages, active, false, 9, height, parent, storage, death);
   }

   public static Container playerPages(Player player, int pages) {
      return new QuadHotbarOverviewMenu.PageInventory(player, pages * 36);
   }

   private QuadHotbarOverviewMenu(
      int id,
      Inventory inventory,
      int pages,
      int active,
      boolean standalone,
      int columns,
      int height,
      AbstractContainerMenu cursorMenu,
      Container externalStorage,
      boolean death
   ) {
      super((MenuType)TYPE.get(), id);
      this.external = externalStorage != null;
      this.deathInventory = death;
      this.pageCount = Math.max(1, Math.min(100, pages));
      this.activePage = Math.max(0, Math.min(this.pageCount - 1, active));
      this.standalone = standalone;
      this.columns = QuadHotbarOverviewLayout.normalizeWidth(columns);
      this.viewportRows = Math.max(1, Math.min(50, height));
      this.pageSlots = this.pageCount * 36;
      this.inventoryMenu = cursorMenu;
      Container storage = this.external ? externalStorage : new QuadHotbarOverviewMenu.PageInventory(inventory.player, this.pageSlots);

      for (int index = 0; index < this.pageSlots; index++) {
         this.addSlot(new QuadHotbarOverviewMenu.OverviewSlot(storage, index, death));
      }

      if (!standalone && !this.external) {
         for (Slot slot : this.inventoryMenu.slots) {
            this.addSlot(new QuadHotbarOverviewMenu.InventoryAliasSlot(slot));
         }
      }
   }

   public int canonicalSlot(int index) {
      int vanilla = index - this.pageSlots;
      return index >= this.pageSlots && index < this.slots.size() && vanilla >= 9 && vanilla < 45
         ? this.activePage * 36 + (vanilla < 36 ? vanilla : vanilla - 36)
         : index;
   }

   public ItemStack getCarried() {
      return this.inventoryMenu.getCarried();
   }

   public void setCarried(ItemStack stack) {
      this.inventoryMenu.setCarried(stack);
   }

   public void clicked(int index, int button, ClickType type, Player player) {
      if (this.stillValid(player)) {
         if (index < this.slots.size() && (index >= 0 || index == -999 || index == -1)) {
            if (index >= this.pageSlots && type != ClickType.QUICK_CRAFT) {
               this.inventoryMenu.clicked(index - this.pageSlots, button, type, player);
            } else {
               int canonical = this.canonicalSlot(index);
               if (this.deathInventory || type != ClickType.SWAP || button < 0 || button >= 9 || canonical != this.activePage * 36 + button) {
                  super.clicked(canonical, button, type, player);
               }
            }
         }
      }
   }

   public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
      return slot.index < this.pageSlots;
   }

   public ItemStack quickMoveStack(Player player, int index) {
      index = this.canonicalSlot(index);
      if (index >= 0 && index < this.pageSlots) {
         Slot source = (Slot)this.slots.get(index);
         ItemStack stack = source.getItem();
         if (stack.isEmpty()) {
            return ItemStack.EMPTY;
         }

         ItemStack before = stack.copy();
         if (this.external) {
            if (this.deathInventory) {
               QuadHotbarPages.addPickup(player, stack);
            } else {
               int furnaceTarget = -1;
               if (this.inventoryMenu instanceof AbstractFurnaceMenu) {
                  FurnaceMenuAccessor furnace = (FurnaceMenuAccessor)this.inventoryMenu;
                  furnaceTarget = furnace.quadhotbar$canSmelt(stack) ? 0 : (furnace.quadhotbar$isFuel(stack) ? 1 : -1);
               }

               for (int pass = 0; pass < 2 && !stack.isEmpty(); pass++) {
                  for (Slot destination : this.inventoryMenu.slots) {
                     if (stack.isEmpty()) {
                        break;
                     }

                     if (destination.container != player.getInventory()
                        && destination.mayPlace(stack)
                        && (!(this.inventoryMenu instanceof AbstractFurnaceMenu) || destination.index == furnaceTarget)) {
                        ItemStack existing = destination.getItem();
                        if (pass == 0 && !existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, stack)) {
                           int moved = Math.min(stack.getCount(), destination.getMaxStackSize(stack) - existing.getCount());
                           if (moved > 0) {
                              existing.grow(moved);
                              stack.shrink(moved);
                              destination.setChanged();
                           }
                        } else if (pass == 1 && existing.isEmpty()) {
                           destination.setByPlayer(stack.split(Math.min(stack.getCount(), destination.getMaxStackSize(stack))));
                        }
                     }
                  }
               }
            }

            if (before.getCount() == stack.getCount()) {
               return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
               source.setByPlayer(ItemStack.EMPTY);
            } else {
               source.setChanged();
            }

            source.onTake(player, stack);
            return before;
         } else {
            for (int pass = 0; pass < 2 && !stack.isEmpty(); pass++) {
               for (int target = 0; target < this.pageSlots && !stack.isEmpty(); target++) {
                  if (target != index) {
                     Slot destination = (Slot)this.slots.get(target);
                     ItemStack existing = destination.getItem();
                     if (destination.mayPlace(stack)) {
                        if (pass == 0 && !existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, stack)) {
                           int moved = Math.min(stack.getCount(), destination.getMaxStackSize(stack) - existing.getCount());
                           if (moved > 0) {
                              existing.grow(moved);
                              stack.shrink(moved);
                              destination.setChanged();
                           }
                        } else if (pass == 1 && existing.isEmpty()) {
                           destination.setByPlayer(stack.split(Math.min(stack.getCount(), destination.getMaxStackSize(stack))));
                        }
                     }
                  }
               }
            }

            if (stack.getCount() == before.getCount()) {
               return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
               source.setByPlayer(ItemStack.EMPTY);
            } else {
               source.setChanged();
            }

            source.onTake(player, stack);
            return before;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean stillValid(Player player) {
      if (!player.isAlive()) {
         return false;
      } else if (!player.level().isClientSide && !QuadHotbarServerConfig.permitsCurrentPages(player)) {
         return false;
      } else {
         return this.external
            ? player.level().isClientSide
               || player.containerMenu == this.inventoryMenu
                  && this.inventoryMenu.stillValid(player)
                  && QuadHotbarServerConfig.forPlayer(player).allowContainerOverview()
                  && (
                     this.deathInventory
                        || QuadHotbarPages.state(player).pageCount == this.pageCount && QuadHotbarPages.state(player).activePage == this.activePage
                  )
            : player.level().isClientSide
               || QuadHotbarServerConfig.forPlayer(player).allowPageOverview()
                  && QuadHotbarPages.state(player).pageCount == this.pageCount
                  && QuadHotbarPages.state(player).activePage == this.activePage;
      }
   }

   public void removed(Player player) {
      if (!this.external) {
         super.removed(player);
         if (!this.returningToInventory) {
            this.inventoryMenu.removed(player);
         }

         if (player instanceof ServerPlayer server) {
            player.inventoryMenu.broadcastFullState();
            QuadHotbarNetwork.sendPageSync(server);
         }
      }
   }

   private static final class InventoryAliasSlot extends Slot {
      private final Slot target;

      private InventoryAliasSlot(Slot target) {
         super(target.container, target.getSlotIndex(), -10000, -10000);
         this.target = target;
      }

      public boolean isActive() {
         return false;
      }

      public ItemStack getItem() {
         return this.target.getItem();
      }

      public void set(ItemStack stack) {
         this.target.set(stack);
      }

      public void setByPlayer(ItemStack stack, ItemStack previous) {
         this.target.setByPlayer(stack, previous);
      }

      public void setChanged() {
         this.target.setChanged();
      }

      public boolean mayPlace(ItemStack stack) {
         return this.target.mayPlace(stack);
      }

      public boolean mayPickup(Player player) {
         return this.target.mayPickup(player);
      }

      public int getMaxStackSize() {
         return this.target.getMaxStackSize();
      }

      public int getMaxStackSize(ItemStack stack) {
         return this.target.getMaxStackSize(stack);
      }

      public void onTake(Player player, ItemStack stack) {
         this.target.onTake(player, stack);
      }
   }

   public static final class OverviewSlot extends Slot {
      public boolean visible = true;
      private final boolean readOnlyStorage;

      private OverviewSlot(Container container, int index, boolean readOnlyStorage) {
         super(container, index, -10000, -10000);
         this.readOnlyStorage = readOnlyStorage;
      }

      public boolean mayPlace(ItemStack stack) {
         return !this.readOnlyStorage;
      }

      public boolean isActive() {
         return this.visible;
      }
   }

   private static final class PageInventory implements Container {
      private final Player player;
      private final int size;

      private PageInventory(Player player, int size) {
         this.player = player;
         this.size = size;
      }

      public int getContainerSize() {
         return this.size;
      }

      public boolean isEmpty() {
         for (int i = 0; i < this.size; i++) {
            if (!this.getItem(i).isEmpty()) {
               return false;
            }
         }

         return true;
      }

      public ItemStack getItem(int index) {
         return QuadHotbarPages.getPageItem(this.player, index / 36, index % 36);
      }

      public ItemStack removeItem(int index, int amount) {
         ItemStack result = this.getItem(index).split(amount);
         if (!result.isEmpty()) {
            this.setChanged();
         }

         return result;
      }

      public ItemStack removeItemNoUpdate(int index) {
         ItemStack stack = this.getItem(index);
         this.setItem(index, ItemStack.EMPTY);
         return stack;
      }

      public void setItem(int index, ItemStack stack) {
         QuadHotbarPages.setPageItem(this.player, index / 36, index % 36, stack);
      }

      public void setChanged() {
         this.player.getInventory().setChanged();
      }

      public boolean stillValid(Player player) {
         return this.player == player && player.isAlive();
      }

      public void clearContent() {
         for (int i = 0; i < this.size; i++) {
            this.setItem(i, ItemStack.EMPTY);
         }
      }
   }
}
