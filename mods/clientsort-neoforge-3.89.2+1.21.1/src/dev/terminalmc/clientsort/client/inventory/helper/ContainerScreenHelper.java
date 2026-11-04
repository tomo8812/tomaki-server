package dev.terminalmc.clientsort.client.inventory.helper;

import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.inventory.Scope;
import dev.terminalmc.clientsort.util.inject.ISlot;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.ticks.ContainerSingleItem;

public class ContainerScreenHelper<T extends AbstractContainerScreen<?>> {
   protected final T screen;

   protected ContainerScreenHelper(T screen) {
      this.screen = screen;
   }

   public static <T extends AbstractContainerScreen<?>> ContainerScreenHelper<T> of(T screen) {
      return screen instanceof CreativeModeInventoryScreen creativeScreen
         ? new CreativeContainerScreenHelper<>(creativeScreen)
         : new ContainerScreenHelper<>(screen);
   }

   public static boolean isHotbarSlot(Slot slot) {
      return ((ISlot)slot).clientsort$getIndexInContainer() < 9;
   }

   public static boolean isExtraSlot(Slot slot) {
      return ((ISlot)slot).clientsort$getIndexInContainer() > 35;
   }

   public Scope getScope(Slot slot) {
      if (slot.isFake()) {
         return Scope.INVALID;
      }

      return switch (slot.container) {
         case null -> Scope.INVALID;
         case ContainerSingleItem ignored -> Scope.INVALID;
         case Inventory ignored -> {
            boolean mergeWithHotbar = false;
            if (isExtraSlot(slot)) {
               switch (Config.options().extraSlotScope) {
                  case HOTBAR:
                     mergeWithHotbar = true;
                     break;
                  case EXTRA:
                     yield Scope.PLAYER_INV_EXTRA;
                  case NONE:
                     yield Scope.INVALID;
               }
            }

            if (mergeWithHotbar || isHotbarSlot(slot)) {
               switch (Config.options().hotbarScope) {
                  case HOTBAR:
                     yield Scope.PLAYER_INV_HOTBAR;
                  case NONE:
                     yield Scope.INVALID;
               }
            }

            yield Scope.PLAYER_INV;
         }
         default -> Scope.CONTAINER_INV;
      };
   }

   public List<Slot> getLargestSlotGroup(Scope scope) {
      List<Slot> slots = new ArrayList<>();

      for (List<Slot> group : this.getSlotGroups(scope)) {
         if (group.size() > slots.size()) {
            slots = group;
         }
      }

      return slots;
   }

   public List<Slot> getGroupForSlot(Slot slot, Scope scope) {
      if (scope != Scope.PLAYER_INV && scope != Scope.PLAYER_INV_HOTBAR) {
         for (List<Slot> group : this.getSlotGroups(scope)) {
            if (group.contains(slot)) {
               return group;
            }
         }
      } else {
         List<Slot> slots = this.getAllSlots(slot.container, scope);
         if (slots.contains(slot)) {
            return slots;
         }
      }

      return new ArrayList<>();
   }

   public List<List<Slot>> getSlotGroups(Scope scope) {
      List<List<Slot>> slotGroups = new ArrayList<>();
      List<Slot> currentGroup = new ArrayList<>();
      int lastIdx = Integer.MAX_VALUE;
      Container lastContainer = null;

      for (Slot slot : this.screen.getMenu().slots) {
         if (slot.container != null && this.getScope(slot).equals(scope)) {
            int slotIdx = ((ISlot)slot).clientsort$getIndexInContainer();
            if (slotIdx <= lastIdx || slot.container != lastContainer) {
               if (!currentGroup.isEmpty()) {
                  slotGroups.add(currentGroup);
               }

               currentGroup = new ArrayList<>();
            }

            currentGroup.add(slot);
            lastIdx = slotIdx;
            lastContainer = slot.container;
         }
      }

      if (!currentGroup.isEmpty()) {
         slotGroups.add(currentGroup);
      }

      return slotGroups;
   }

   public List<Slot> getAllSlots(Container container, Scope scope) {
      List<Slot> slots = new ArrayList<>();

      for (Slot slot : this.screen.getMenu().slots) {
         if (slot.container != null && this.getScope(slot).equals(scope) && slot.container == container) {
            slots.add(slot);
         }
      }

      return slots;
   }

   public void translateSlotIds(int[] slotMapping) {
   }
}
