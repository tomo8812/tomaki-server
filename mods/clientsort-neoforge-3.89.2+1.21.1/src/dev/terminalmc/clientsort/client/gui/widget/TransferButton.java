package dev.terminalmc.clientsort.client.gui.widget;

import dev.terminalmc.clientsort.client.config.ClassPolicy;
import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.config.Operation;
import dev.terminalmc.clientsort.client.config.Policy;
import dev.terminalmc.clientsort.client.config.Vec2i;
import dev.terminalmc.clientsort.client.inventory.operator.SingleUseOperator;
import java.util.Collection;
import java.util.TreeSet;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class TransferButton extends TriggerButton {
   private static final WidgetSprites SPRITES_UP = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("clientsort", "widget/transfer_up"),
      ResourceLocation.fromNamespaceAndPath("clientsort", "widget/transfer_up_disabled"),
      ResourceLocation.fromNamespaceAndPath("clientsort", "widget/transfer_up_highlighted")
   );
   private static final WidgetSprites SPRITES_DOWN = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("clientsort", "widget/transfer_down"),
      ResourceLocation.fromNamespaceAndPath("clientsort", "widget/transfer_down_disabled"),
      ResourceLocation.fromNamespaceAndPath("clientsort", "widget/transfer_down_highlighted")
   );

   public TransferButton(
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
         isPlayerInv ? SPRITES_UP : SPRITES_DOWN,
         name,
         policy == null ? null : policy.getKey(),
         lowestPolicyKey,
         offset,
         policy == null ? true : policy.offsetFromSlot(),
         policy == null || policy.canTransfer(),
         policy != null && policy.showTransferButton(),
         button -> SingleUseOperator.transfer(screen, referenceSlot, false)
      );
   }

   @Override
   public boolean getPolicyStatus(ClassPolicy policy) {
      return policy.showTransferButton();
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
         Config.options()
            .classPolicies
            .put(
               key,
               ClassPolicy.create(
                  key,
                  offset,
                  offsetFromSlot,
                  Policy.KEYBIND,
                  Policy.KEYBIND,
                  Policy.KEYBIND,
                  this.operationAllowed ? (this.active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND) : Policy.NONE,
                  autoOp,
                  autoOpOther,
                  new TreeSet<>(slots)
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
                  policy.sortPolicy(),
                  policy.stackFillPolicy(),
                  policy.matchTransferPolicy(),
                  this.operationAllowed ? (this.active ? Policy.KEYBIND_BUTTON : Policy.KEYBIND) : Policy.NONE,
                  autoOp,
                  autoOpOther,
                  new TreeSet<>(slots)
               )
            );
      }

      Config.save();
   }
}
