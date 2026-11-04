package dev.terminalmc.clientsort.client.inventory.operator;

import dev.terminalmc.clientsort.ClientSort;
import dev.terminalmc.clientsort.client.compat.itemlocks.ItemLocksCompat;
import dev.terminalmc.clientsort.client.compat.lockedinslots.LockedInSlotsCompat;
import dev.terminalmc.clientsort.client.compat.stacktnc.StackTncCompat;
import dev.terminalmc.clientsort.client.config.ClassPolicy;
import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.config.Operation;
import dev.terminalmc.clientsort.client.gui.TriggerButtonManager;
import dev.terminalmc.clientsort.client.interaction.InteractionManager;
import dev.terminalmc.clientsort.client.inventory.Scope;
import dev.terminalmc.clientsort.client.inventory.helper.ContainerScreenHelper;
import dev.terminalmc.clientsort.client.inventory.operator.client.ClientCreativeOperator;
import dev.terminalmc.clientsort.client.inventory.operator.client.ClientSurvivalOperator;
import dev.terminalmc.clientsort.client.inventory.operator.server.ServerOperator;
import dev.terminalmc.clientsort.client.order.SortOrder;
import dev.terminalmc.clientsort.client.platform.services.ClientPlatformServices;
import dev.terminalmc.clientsort.client.util.PolicyManager;
import dev.terminalmc.clientsort.mixin.client.accessor.AbstractContainerScreenAccessor;
import dev.terminalmc.clientsort.util.SlotLogUtil;
import dev.terminalmc.clientsort.util.inject.ISlot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class SingleUseOperator {
   protected final AbstractContainerScreen<?> screen;
   protected final ContainerScreenHelper<? extends AbstractContainerScreen<?>> screenHelper;
   protected final Operation op;
   protected final Slot originSlot;
   protected final Slot[] originScopeSlots;
   protected final ItemStack[] originScopeStacks;
   protected final Slot[] otherScopeSlots;
   protected final ItemStack[] otherScopeStacks;

   protected SingleUseOperator(AbstractContainerScreen<?> screen, Slot originSlot, Operation op) {
      this.screen = screen;
      this.screenHelper = ContainerScreenHelper.of(screen);
      this.originSlot = originSlot;
      this.op = op;
      Scope originScope = this.screenHelper.getScope(originSlot);
      this.originScopeSlots = this.collectSlots(originSlot, originScope);
      if (ClientSort.debug()) {
         dev.terminalmc.clientsort.client.ClientSort.LOG
            .info("Discovered {} slots in origin scope ({} - {}):", this.originScopeSlots.length, originScope.ordinal(), originScope.name());
         dev.terminalmc.clientsort.client.ClientSort.LOG.info(SlotLogUtil.listSlotIds(List.of(this.originScopeSlots)));
      }

      this.originScopeStacks = new ItemStack[this.originScopeSlots.length];

      for (int i = 0; i < this.originScopeSlots.length; i++) {
         this.originScopeStacks[i] = this.originScopeSlots[i].getItem().copy();
      }

      LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
      Slot otherSlot = originSlot.container == player.getInventory() ? TriggerButtonManager.getContainerRefSlot(op) : TriggerButtonManager.getPlayerRefSlot(op);
      if (otherSlot != null && op.isDirectional()) {
         Scope otherScope = this.screenHelper.getScope(otherSlot);
         this.otherScopeSlots = this.collectSlots(otherSlot, otherScope);
         if (ClientSort.debug()) {
            dev.terminalmc.clientsort.client.ClientSort.LOG
               .info("Discovered {} slots in other scope ({} - {}):", this.otherScopeSlots.length, otherScope.ordinal(), otherScope.name());
            dev.terminalmc.clientsort.client.ClientSort.LOG.info(SlotLogUtil.listSlotIds(List.of(this.otherScopeSlots)));
         }

         this.otherScopeStacks = new ItemStack[this.otherScopeSlots.length];

         for (int i = 0; i < this.otherScopeSlots.length; i++) {
            this.otherScopeStacks[i] = this.otherScopeSlots[i].getItem().copy();
         }
      } else {
         this.otherScopeSlots = new Slot[0];
         this.otherScopeStacks = new ItemStack[0];
      }
   }

   private Slot[] collectSlots(Slot refSlot, Scope scope) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (scope != Scope.INVALID && player != null) {
         ItemStack testItem = Items.LIGHT.getDefaultInstance();
         ArrayList<Slot> collectedSlots = new ArrayList<>();

         for (Slot slot : this.screenHelper.getGroupForSlot(refSlot, scope)) {
            int slotIdx = ((ISlot)slot).clientsort$getIndexInContainer();
            if (slot.mayPlace(testItem)
               && (slot.hasItem() ? slot.mayPickup(player) : slot.container.canPlaceItem(slotIdx, testItem))
               && !ItemLocksCompat.isLocked(slot)
               && !StackTncCompat.isLocked(slot)
               && !LockedInSlotsCompat.isLocked(slot)) {
               Object object = ClientSort.getObj(slot, this.screen.getMenu());
               if (object != null) {
                  boolean isPlayerInv = slot.container instanceof Inventory;
                  Component invTitle = isPlayerInv
                     ? ((AbstractContainerScreenAccessor)this.screen).clientsort$getPlayerInventoryTitle()
                     : this.screen.getTitle();
                  ClassPolicy policy = PolicyManager.getPolicy(object.getClass(), invTitle.getString());
                  if (policy == null || !policy.ignoredSlots().contains(slotIdx)) {
                     collectedSlots.add(slot);
                  }
               }
            }
         }

         return collectedSlots.toArray(new Slot[0]);
      } else {
         return new Slot[0];
      }
   }

   protected static void startClientOp() {
      dev.terminalmc.clientsort.client.ClientSort.operatingClient = true;
   }

   protected static void endClientOp() {
      InteractionManager.push(() -> {
         Runnable nextOp = dev.terminalmc.clientsort.client.ClientSort.clientOpQueue.poll();
         if (nextOp != null) {
            Minecraft.getInstance().execute(nextOp);
         } else {
            dev.terminalmc.clientsort.client.ClientSort.operatingClient = false;
         }

         return InteractionManager.TICK_WAITER;
      });
   }

   public static boolean sort(AbstractContainerScreen<?> screen, Slot originSlot, boolean onlyClient, SortOrder sortOrder) {
      return sortOrder.equals(SortOrder.NONE) ? false : operate(screen, originSlot, onlyClient, Operation.SORT, op -> op.sort(sortOrder));
   }

   public static boolean fillStacks(AbstractContainerScreen<?> screen, Slot originSlot, boolean onlyClient) {
      return operate(screen, originSlot, onlyClient, Operation.STACK_FILL, SingleUseOperator::fillStacks);
   }

   public static boolean transferMatching(AbstractContainerScreen<?> screen, Slot originSlot, boolean onlyClient) {
      return operate(screen, originSlot, onlyClient, Operation.MATCH_TRANSFER, SingleUseOperator::matchTransfer);
   }

   public static boolean transfer(AbstractContainerScreen<?> screen, Slot originSlot, boolean onlyClient) {
      return operate(screen, originSlot, onlyClient, Operation.TRANSFER, SingleUseOperator::transfer);
   }

   private static boolean operate(
      AbstractContainerScreen<?> screen, Slot originSlot, boolean onlyClient, Operation operation, Consumer<SingleUseOperator> wrapper
   ) {
      if (Minecraft.getInstance().player.isSpectator()) {
         return false;
      }

      boolean isPlayerInv = originSlot.container instanceof Inventory;
      Component invTitle = isPlayerInv ? ((AbstractContainerScreenAccessor)screen).clientsort$getPlayerInventoryTitle() : screen.getTitle();
      Object object = ClientSort.getObj(originSlot, screen.getMenu());
      if (object == null) {
         return false;
      }

      ClassPolicy policy;
      label77: {
         policy = PolicyManager.getPolicy(object.getClass(), invTitle.getString());
         if (policy != null) {
            switch (operation) {
               case SORT:
                  if (!policy.canSort()) {
                     break label77;
                  }
                  break;
               case STACK_FILL:
                  if (!policy.canStackFill()) {
                     break label77;
                  }
                  break;
               case MATCH_TRANSFER:
                  if (!policy.canMatchTransfer()) {
                     break label77;
                  }
                  break;
               case TRANSFER:
                  if (!policy.canTransfer()) {
                     break label77;
                  }
                  break;
               default:
                  throw new MatchException(null, null);
            }
         }

         if (!onlyClient && Config.options().useServerAcceleration && ClientPlatformServices.getInstance().canSendToServer(operation.type)) {
            if (ClientSort.debug()) {
               dev.terminalmc.clientsort.client.ClientSort.LOG.info("Preparing server operator for {}", operation.name());
            }

            wrapper.accept(new ServerOperator(screen, originSlot, operation));
            return true;
         }

         Runnable op = () -> {
            if (ClientSort.debug()) {
               dev.terminalmc.clientsort.client.ClientSort.LOG.info("Preparing server operator for {}", operation.name());
            }

            wrapper.accept(getClientOperator(screen, originSlot, operation));
            endClientOp();
         };
         if (dev.terminalmc.clientsort.client.ClientSort.operatingClient) {
            if (dev.terminalmc.clientsort.client.ClientSort.clientOpQueue.offer(op)) {
               if (ClientSort.debug()) {
                  dev.terminalmc.clientsort.client.ClientSort.LOG.warn("Client operation added to queue: another operation is in progress!");
               }

               return true;
            }

            if (ClientSort.debug()) {
               dev.terminalmc.clientsort.client.ClientSort.LOG.warn("Client operation rejected: another operation is in progress and the queue is full!");
            }

            return false;
         }

         startClientOp();
         op.run();
         return true;
      }

      if (ClientSort.debug()) {
         dev.terminalmc.clientsort.client.ClientSort.LOG.warn("Operation {} is disallowed by policy for class {}!", operation.name(), policy.getClass());
      }

      return false;
   }

   private static SingleUseOperator getClientOperator(AbstractContainerScreen<?> screen, Slot originSlot, Operation operation) {
      if (Objects.requireNonNull(Minecraft.getInstance().player).isCreative() && screen instanceof CreativeModeInventoryScreen) {
         if (ClientSort.debug()) {
            dev.terminalmc.clientsort.client.ClientSort.LOG.info("Preparing client-creative operator for {}", operation.name());
         }

         return new ClientCreativeOperator(screen, originSlot, operation);
      } else {
         if (ClientSort.debug()) {
            dev.terminalmc.clientsort.client.ClientSort.LOG.info("Preparing client-survival operator for {}", operation.name());
         }

         return new ClientSurvivalOperator(screen, originSlot, operation);
      }
   }

   protected abstract void sort(SortOrder var1);

   protected abstract void fillStacks();

   protected abstract void matchTransfer();

   protected abstract void transfer();

   protected static Slot[] collectMatchingSlots(Slot[] originSlots, ItemStack[] otherStacks, boolean alwaysMatchByType, Set<Item> typeMatchItems) {
      List<Slot> slots = new ArrayList<>();

      for (Slot slot : originSlots) {
         if (containsMatchingStack(otherStacks, slot.getItem(), alwaysMatchByType, typeMatchItems)) {
            slots.add(slot);
         }
      }

      return slots.toArray(new Slot[0]);
   }

   protected static boolean containsMatchingStack(ItemStack[] stacks, ItemStack stack, boolean alwaysMatchByType, Set<Item> typeMatchItems) {
      for (ItemStack s : stacks) {
         if (stacksMatch(s, stack, alwaysMatchByType, typeMatchItems)) {
            return true;
         }
      }

      return false;
   }

   protected static boolean stacksMatch(ItemStack a, ItemStack b, boolean alwaysMatchByType, Set<Item> typeMatchItems) {
      return ItemStack.isSameItemSameComponents(a, b) || ItemStack.isSameItem(a, b) && (alwaysMatchByType || typeMatchItems.contains(a.getItem()));
   }
}
