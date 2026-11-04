package dev.terminalmc.clientsort.client.gui.widget;

import dev.terminalmc.clientsort.client.config.ClassPolicy;
import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.config.Operation;
import dev.terminalmc.clientsort.client.config.Policy;
import dev.terminalmc.clientsort.client.config.Vec2i;
import dev.terminalmc.clientsort.client.inventory.operator.SingleUseOperator;
import dev.terminalmc.clientsort.client.order.SortOrder;
import java.util.Collection;
import java.util.TreeSet;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class SortButton extends TriggerButton {
   private static final WidgetSprites SPRITES = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("clientsort", "widget/sort"),
      ResourceLocation.fromNamespaceAndPath("clientsort", "widget/sort_disabled"),
      ResourceLocation.fromNamespaceAndPath("clientsort", "widget/sort_highlighted")
   );

   public SortButton(
      AbstractContainerScreen<?> screen,
      Container container,
      Slot referenceSlot,
      boolean referenceLeft,
      boolean isPlayerInv,
      @Nullable ClassPolicy policy,
      String lowestPolicyKey,
      Vec2i offset,
      Component name
   ) {
      super(
         screen,
         container,
         referenceSlot,
         referenceLeft,
         isPlayerInv,
         SPRITES,
         name,
         policy == null ? null : policy.getKey(),
         lowestPolicyKey,
         offset,
         policy == null ? true : policy.offsetFromSlot(),
         policy == null || policy.canSort(),
         policy != null && policy.showSortButton(),
         button -> {
            SortOrder sortOrder = Config.options().sortOrder;
            if (Screen.hasShiftDown()) {
               sortOrder = Config.options().shiftSortOrder;
            } else if (Screen.hasControlDown()) {
               sortOrder = Config.options().ctrlSortOrder;
            } else if (Screen.hasAltDown()) {
               sortOrder = Config.options().altSortOrder;
            }

            SingleUseOperator.sort(screen, referenceSlot, false, sortOrder);
         }
      );
   }

   @Override
   public boolean getPolicyStatus(ClassPolicy policy) {
      return policy.showSortButton();
   }

   @Override
   public void savePolicy(@Nullable Vec2i offset, boolean offsetFromSlot, @Nullable Operation autoOp, boolean autoOpOther, Collection<Integer> slots) {
      String key = this.activePolicyKey != null ? this.activePolicyKey : this.lowestPolicyKey;
      ClassPolicy policy = Config.options().classPolicies.get(key);
      if (policy == null) {
         key = this.lowestPolicyKey;
         policy = Config.options().classPolicies.get(key);
      }

      if (policy == null) {
         Policy var10005 = this.operationAllowed ? (this.active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND) : Policy.NONE;
         Config.options()
            .classPolicies
            .put(
               key,
               ClassPolicy.create(
                  key, offset, offsetFromSlot, var10005, Policy.KEYBIND, Policy.KEYBIND, Policy.KEYBIND, autoOp, autoOpOther, new TreeSet<>(slots)
               )
            );
      } else {
         Config.options()
            .classPolicies
            .put(
               key,
               ClassPolicy.create(
                  key,
                  offset,
                  offsetFromSlot,
                  this.operationAllowed ? (this.active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND) : Policy.NONE,
                  policy.stackFillPolicy(),
                  policy.matchTransferPolicy(),
                  policy.transferPolicy(),
                  autoOp,
                  autoOpOther,
                  new TreeSet<>(slots)
               )
            );
      }

      Config.save();
   }
}
