package dev.terminalmc.clientsort.client.gui;

import dev.terminalmc.clientsort.ClientSort;
import dev.terminalmc.clientsort.client.config.ClassPolicy;
import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.config.Operation;
import dev.terminalmc.clientsort.client.config.Vec2i;
import dev.terminalmc.clientsort.client.gui.screen.edit.ContainerEditorScreen;
import dev.terminalmc.clientsort.client.gui.screen.edit.PlayerEditorScreen;
import dev.terminalmc.clientsort.client.gui.screen.edit.SelectorScreen;
import dev.terminalmc.clientsort.client.gui.widget.MatchTransferButton;
import dev.terminalmc.clientsort.client.gui.widget.SortButton;
import dev.terminalmc.clientsort.client.gui.widget.StackFillButton;
import dev.terminalmc.clientsort.client.gui.widget.TransferButton;
import dev.terminalmc.clientsort.client.gui.widget.TriggerButton;
import dev.terminalmc.clientsort.client.inventory.Scope;
import dev.terminalmc.clientsort.client.inventory.helper.ContainerScreenHelper;
import dev.terminalmc.clientsort.client.util.KeybindManager;
import dev.terminalmc.clientsort.client.util.PolicyManager;
import dev.terminalmc.clientsort.mixin.client.accessor.AbstractContainerScreenAccessor;
import dev.terminalmc.clientsort.mixin.client.accessor.ScreenAccessor;
import dev.terminalmc.clientsort.util.Localization;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class TriggerButtonManager {
   private static final int BUTTON_SPACING = 1;
   @Nullable
   private static AbstractContainerScreen<?> screen;
   private static final LinkedHashSet<TriggerButton> containerButtons = new LinkedHashSet<>();
   private static final LinkedHashSet<TriggerButton> visibleContainerButtons = new LinkedHashSet<>();
   private static final LinkedHashSet<TriggerButton> playerButtons = new LinkedHashSet<>();
   private static final LinkedHashSet<TriggerButton> visiblePlayerButtons = new LinkedHashSet<>();

   private TriggerButtonManager() {
   }

   @Nullable
   public static AbstractContainerScreen<?> getScreen() {
      return screen;
   }

   public static LinkedList<TriggerButton> getContainerButtons() {
      return new LinkedList<>(containerButtons);
   }

   public static LinkedList<TriggerButton> getPlayerButtons() {
      return new LinkedList<>(playerButtons);
   }

   @Nullable
   public static Slot getContainerRefSlot(Operation op) {
      return getRefSlot(containerButtons, op);
   }

   @Nullable
   public static Slot getPlayerRefSlot(Operation op) {
      return getRefSlot(playerButtons, op);
   }

   @Nullable
   private static Slot getRefSlot(LinkedHashSet<TriggerButton> buttons, Operation op) {
      Class<? extends TriggerButton> clazz = switch (op) {
         case SORT -> SortButton.class;
         case STACK_FILL -> StackFillButton.class;
         case MATCH_TRANSFER -> MatchTransferButton.class;
         case TRANSFER -> TransferButton.class;
      };

      for (TriggerButton button : buttons) {
         if (button.getClass().equals(clazz)) {
            return button.referenceSlot;
         }
      }

      return null;
   }

   public static void afterScreenInit(Screen initScreen) {
      if (initScreen instanceof AbstractContainerScreen<?> acs) {
         if (Minecraft.getInstance().player != null && !Minecraft.getInstance().player.isSpectator()) {
            String initScreenClass = initScreen.getClass().getName();

            for (String screenClass : Config.options().screenClassBlacklist) {
               if (screenClass.equalsIgnoreCase(initScreenClass)) {
                  return;
               }
            }

            containerButtons.clear();
            visibleContainerButtons.clear();
            playerButtons.clear();
            visiblePlayerButtons.clear();
            screen = acs;
            boolean enabled = Config.options().showButtons;
            boolean isEditorC = false;
            boolean isEditorP = false;
            Screen currentScreen = Minecraft.getInstance().screen;
            if (currentScreen instanceof SelectorScreen) {
               isEditorC = true;
               isEditorP = true;
            } else if (currentScreen instanceof ContainerEditorScreen) {
               isEditorC = true;
            } else if (currentScreen instanceof PlayerEditorScreen) {
               isEditorP = true;
            }

            boolean left = Config.options().anchorButtonsLeft;
            boolean justifyLeft = Config.options().justifyButtonsTopLeft;
            List<Operation> ops = List.of(
               Config.options().firstButtonOp, Config.options().secondButtonOp, Config.options().thirdButtonOp, Config.options().fourthButtonOp
            );
            if (!justifyLeft) {
               ops = ops.reversed();
            }

            Slot refSlotC = getReferenceSlot(acs, false, left);
            if (refSlotC != null) {
               boolean isEditor = isEditorC;
               ops.forEach(op -> generate(acs, refSlotC, left, justifyLeft, false, isEditor, enabled, op));
            }

            Slot refSlotP = getReferenceSlot(acs, true, left);
            if (refSlotP != null) {
               boolean isEditor = isEditorP;
               ops.forEach(op -> generate(acs, refSlotP, left, justifyLeft, true, isEditor, enabled, op));
            }
         }
      }
   }

   private static void generate(
      AbstractContainerScreen<?> screen,
      Slot referenceSlot,
      boolean referenceLeft,
      boolean justifyTopLeft,
      boolean isPlayerInv,
      boolean isEditor,
      boolean enabled,
      Operation op
   ) {
      switch (op) {
         case SORT:
            generateSimpleButton(
               screen,
               referenceSlot,
               referenceLeft,
               justifyTopLeft,
               isPlayerInv,
               isEditor,
               enabled,
               ClassPolicy::canSort,
               ClassPolicy::showSortButton,
               ClassPolicy::autoSort,
               SortButton::new,
               Localization.localized("key", "op.sort")
            );
            break;
         case STACK_FILL:
            generateDirectionalButton(
               screen,
               referenceSlot,
               referenceLeft,
               justifyTopLeft,
               isPlayerInv,
               isEditor,
               enabled,
               ClassPolicy::canStackFill,
               ClassPolicy::showStackFillButton,
               ClassPolicy::autoStackFill,
               StackFillButton::new,
               Localization.localized("key", "op.stackFill")
            );
            break;
         case MATCH_TRANSFER:
            generateDirectionalButton(
               screen,
               referenceSlot,
               referenceLeft,
               justifyTopLeft,
               isPlayerInv,
               isEditor,
               enabled,
               ClassPolicy::canMatchTransfer,
               ClassPolicy::showMatchTransferButton,
               ClassPolicy::autoMatchTransfer,
               MatchTransferButton::new,
               Localization.localized("key", "op.matchTransfer")
            );
            break;
         case TRANSFER:
            generateDirectionalButton(
               screen,
               referenceSlot,
               referenceLeft,
               justifyTopLeft,
               isPlayerInv,
               isEditor,
               enabled,
               ClassPolicy::canTransfer,
               ClassPolicy::showTransferButton,
               ClassPolicy::autoTransfer,
               TransferButton::new,
               Localization.localized("key", "op.transfer")
            );
      }
   }

   private static void generateSimpleButton(
      AbstractContainerScreen<?> screen,
      Slot referenceSlot,
      boolean referenceLeft,
      boolean justifyTopLeft,
      boolean isPlayerInv,
      boolean isEditor,
      boolean enabled,
      Function<ClassPolicy, Boolean> opCheck,
      Function<ClassPolicy, Boolean> buttonCheck,
      Function<ClassPolicy, Boolean> autoCheck,
      TriggerButtonManager.TriggerButtonCreator creator,
      Component name
   ) {
      boolean autoPress = false;
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         if (!(screen instanceof InventoryScreen) || isPlayerInv) {
            Container container = (Container)(isPlayerInv ? player.getInventory() : getContainer(player));
            Object object = ClientSort.getObj(container, screen.getMenu());
            if (object != null) {
               Component invTitle = isPlayerInv ? ((AbstractContainerScreenAccessor)screen).clientsort$getPlayerInventoryTitle() : screen.getTitle();
               ClassPolicy policy = PolicyManager.getPolicy(object.getClass(), invTitle.getString());
               boolean create = isEditor || policy == null || opCheck.apply(policy);
               boolean add = enabled && (isEditor || policy != null && buttonCheck.apply(policy));
               if (create) {
                  Vec2i offset = policy != null ? policy.getButtonOffset() : Config.options().layoutOffset;
                  if (policy != null && autoCheck.apply(policy)) {
                     autoPress = true;
                  }

                  TriggerButton button = creator.create(
                     screen,
                     container,
                     referenceSlot,
                     referenceLeft,
                     isPlayerInv,
                     policy,
                     ClassPolicy.getKey(object.getClass().getName(), null),
                     getShiftedOffset(offset, isPlayerInv, justifyTopLeft),
                     name
                  );
                  BiConsumer<SequencedCollection<TriggerButton>, TriggerButton> adder = justifyTopLeft ? Collection::add : SequencedCollection::addFirst;
                  adder.accept(isPlayerInv ? playerButtons : containerButtons, button);
                  if (add) {
                     adder.accept(isPlayerInv ? visiblePlayerButtons : visibleContainerButtons, button);
                     ((ScreenAccessor)screen).clientsort$addRenderableWidget(button);
                  }

                  if (autoPress) {
                     dev.terminalmc.clientsort.client.ClientSort.taskManager
                        .schedule(
                           isPlayerInv ? Config.options().autoOpDelayPlayer : Config.options().autoOpDelayContainer,
                           () -> {
                              if (Minecraft.getInstance().screen == screen
                                 && !KeybindManager.isDown(KeybindManager.CANCEL_AUTO_KEY)
                                 && (isPlayerInv ? playerButtons : containerButtons).contains(button)) {
                                 button.onPress();
                              }
                           }
                        );
                  }
               }
            }
         }
      }
   }

   private static void generateDirectionalButton(
      AbstractContainerScreen<?> screen,
      Slot referenceSlot,
      boolean referenceLeft,
      boolean justifyTopLeft,
      boolean isPlayerInv,
      boolean isEditor,
      boolean enabled,
      Function<ClassPolicy, Boolean> opCheck,
      Function<ClassPolicy, Boolean> buttonCheck,
      Function<ClassPolicy, Boolean> autoCheck,
      TriggerButtonManager.TriggerButtonCreator creator,
      Component name
   ) {
      boolean autoPress = false;
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         if (!(screen instanceof InventoryScreen) || isPlayerInv) {
            Container container = (Container)(isPlayerInv ? player.getInventory() : getContainer(player));
            Object object = ClientSort.getObj(container, screen.getMenu());
            if (object != null) {
               Component invTitle = isPlayerInv ? ((AbstractContainerScreenAccessor)screen).clientsort$getPlayerInventoryTitle() : screen.getTitle();
               ClassPolicy policy = PolicyManager.getPolicy(object.getClass(), invTitle.getString());
               boolean create = isEditor || policy == null || opCheck.apply(policy);
               boolean add = enabled && (isEditor || policy != null && buttonCheck.apply(policy));
               if (create) {
                  Vec2i offset = policy != null ? policy.getButtonOffset() : Config.options().layoutOffset;
                  if (policy != null && autoCheck.apply(policy) && !policy.autoOpOther()) {
                     autoPress = true;
                  }

                  Container dstContainer = (Container)(isPlayerInv ? getContainer(player) : player.getInventory());
                  if (dstContainer != null) {
                     Object dstObject = ClientSort.getObj(dstContainer, screen.getMenu());
                     if (dstObject == null) {
                        return;
                     }

                     Component dstInvTitle = isPlayerInv ? screen.getTitle() : ((AbstractContainerScreenAccessor)screen).clientsort$getPlayerInventoryTitle();
                     ClassPolicy dstPolicy = PolicyManager.getPolicy(dstObject.getClass(), dstInvTitle.getString());
                     create = isEditor || dstPolicy == null || opCheck.apply(dstPolicy);
                     add = add && (isEditor || dstPolicy != null && buttonCheck.apply(dstPolicy));
                     if (!create) {
                        return;
                     }

                     if (dstPolicy != null && autoCheck.apply(dstPolicy) && dstPolicy.autoOpOther()) {
                        autoPress = true;
                     }
                  }

                  TriggerButton button = creator.create(
                     screen,
                     container,
                     referenceSlot,
                     referenceLeft,
                     isPlayerInv,
                     policy,
                     ClassPolicy.getKey(object.getClass().getName(), null),
                     getShiftedOffset(offset, isPlayerInv, justifyTopLeft),
                     name
                  );
                  BiConsumer<SequencedCollection<TriggerButton>, TriggerButton> adder = justifyTopLeft ? Collection::add : SequencedCollection::addFirst;
                  adder.accept(isPlayerInv ? playerButtons : containerButtons, button);
                  if (add) {
                     adder.accept(isPlayerInv ? visiblePlayerButtons : visibleContainerButtons, button);
                     ((ScreenAccessor)screen).clientsort$addRenderableWidget(button);
                  }

                  if (autoPress) {
                     dev.terminalmc.clientsort.client.ClientSort.taskManager
                        .schedule(
                           isPlayerInv ? Config.options().autoOpDelayPlayer : Config.options().autoOpDelayContainer,
                           () -> {
                              if (Minecraft.getInstance().screen == screen
                                 && !KeybindManager.isDown(KeybindManager.CANCEL_AUTO_KEY)
                                 && (isPlayerInv ? playerButtons : containerButtons).contains(button)) {
                                 button.onPress();
                              }
                           }
                        );
                  }
               }
            }
         }
      }
   }

   @Nullable
   public static Container getContainer(Player player) {
      class ScoredContainer {
         public final Container container;
         public int score;

         public ScoredContainer(Container container, int score) {
            this.container = container;
            this.score = score;
         }
      }

      Map<Container, ScoredContainer> map = new HashMap<>();

      for (Slot slot : player.containerMenu.slots) {
         if (slot.container != null) {
            if (slot.container == player.getInventory() || slot.container instanceof Inventory) {
               break;
            }

            ScoredContainer scoredContainer = map.get(slot.container);
            if (scoredContainer == null) {
               map.put(slot.container, new ScoredContainer(slot.container, 1));
            } else {
               scoredContainer.score++;
            }
         }
      }

      return map.isEmpty() ? null : map.values().stream().max(Comparator.comparingInt(s -> s.score)).get().container;
   }

   @Nullable
   private static Slot getReferenceSlot(AbstractContainerScreen<?> screen, boolean isPlayerInv, boolean anchorButtonsLeft) {
      ContainerScreenHelper<?> helper = ContainerScreenHelper.of(screen);
      Slot bestSlot = null;
      double bestScore = 0.0;
      Scope scope = isPlayerInv ? Scope.PLAYER_INV : Scope.CONTAINER_INV;

      for (Slot slot : helper.getLargestSlotGroup(scope)) {
         double xFactor = (double)Math.clamp(slot.x, 0, screen.width) / screen.width;
         double yFactor = (double)Math.clamp(slot.y, 0, screen.height) / screen.height;
         double x;
         if (anchorButtonsLeft) {
            x = 1.0 - xFactor;
         } else {
            x = xFactor;
         }

         double y = 1.0 - yFactor;
         double score = x * 0.8 + y * 0.2;
         if (score > bestScore) {
            bestSlot = slot;
            bestScore = score;
         }
      }

      return bestSlot;
   }

   public static Vec2i getShiftedOffset(Vec2i offset, boolean isPlayerInv, boolean justifyTopLeft) {
      int index = (isPlayerInv ? visiblePlayerButtons : visibleContainerButtons).size();
      boolean horizontal = Config.options().buttonsHorizontal;
      int shiftX = horizontal ? 1 : 0;
      int shiftY = horizontal ? 0 : 1;
      int x;
      int y;
      if (justifyTopLeft) {
         x = offset.x() + shiftX * 14 * index;
         y = offset.y() + shiftY * 14 * index;
      } else {
         x = offset.x() - shiftX * 14 * index;
         y = offset.y() - shiftY * 14 * index;
      }

      return new Vec2i(x, y);
   }

   @FunctionalInterface
   public interface TriggerButtonCreator {
      TriggerButton create(
         AbstractContainerScreen<?> var1,
         Container var2,
         Slot var3,
         boolean var4,
         boolean var5,
         @Nullable ClassPolicy var6,
         String var7,
         Vec2i var8,
         Component var9
      );
   }
}
