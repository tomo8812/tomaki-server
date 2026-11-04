package dev.terminalmc.clientsort.mixin.client;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.config.ClassPolicy;
import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.gui.screen.edit.EditorScreen;
import dev.terminalmc.clientsort.client.gui.screen.edit.SelectorScreen;
import dev.terminalmc.clientsort.client.inventory.helper.ContainerScreenHelper;
import dev.terminalmc.clientsort.client.inventory.operator.SingleUseOperator;
import dev.terminalmc.clientsort.client.order.SortOrder;
import dev.terminalmc.clientsort.client.util.KeybindManager;
import dev.terminalmc.clientsort.client.util.PolicyManager;
import dev.terminalmc.clientsort.mixin.client.accessor.AbstractContainerScreenAccessor;
import dev.terminalmc.clientsort.util.inject.ISlot;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {
   @Shadow
   @Final
   protected AbstractContainerMenu menu;
   @Shadow
   protected Slot hoveredSlot;
   @Shadow
   private ItemStack draggingItem;

   protected AbstractContainerScreenMixin(Component title) {
      super(title);
   }

   @Shadow
   public abstract T getMenu();

   @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
   private void beforeSlotClicked(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
      if (slotId < 0 && ClientSort.operatingClient) {
         ci.cancel();
      }
   }

   @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
   private void beforeMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      Supplier<Boolean> op = this.clientsort$getOperation(keyMapping -> keyMapping.matchesMouse(button));
      if (op != null && op.get()) {
         cir.setReturnValue(true);
      }
   }

   @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
   private void beforeKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      Supplier<Boolean> op = this.clientsort$getOperation(keyMapping -> keyMapping.matches(keyCode, scanCode));
      if (op != null && op.get()) {
         cir.setReturnValue(true);
      }
   }

   @Unique
   @Nullable
   private Supplier<Boolean> clientsort$getOperation(Function<KeyMapping, Boolean> inputMatcher) {
      boolean isEditKey = inputMatcher.apply(KeybindManager.EDIT_KEY);
      if (this.hoveredSlot == null) {
         return isEditKey ? this::clientsort$openEditor : null;
      }

      Options options = this.minecraft.options;
      if (!inputMatcher.apply(options.keyPickItem)
         || !this.minecraft.player.hasInfiniteMaterials()
         || !this.hoveredSlot.hasItem() && this.draggingItem.isEmpty() && this.menu.getCarried().isEmpty()) {
         if (inputMatcher.apply(options.keyDrop) && this.hoveredSlot.hasItem()) {
            return null;
         }

         if (inputMatcher.apply(options.keySwapOffhand)) {
            return null;
         }

         for (int i = 0; i < 9; i++) {
            if (inputMatcher.apply(options.keyHotbarSlots[i])) {
               return null;
            }
         }

         if (isEditKey) {
            return this::clientsort$openEditor;
         } else if (inputMatcher.apply(KeybindManager.SORT_KEY)) {
            return this::clientsort$sort;
         } else if (inputMatcher.apply(KeybindManager.STACK_FILL_KEY)) {
            return this::clientsort$fillStacks;
         } else if (inputMatcher.apply(KeybindManager.MATCH_TRANSFER_KEY)) {
            return this::clientsort$transferMatching;
         } else {
            return inputMatcher.apply(KeybindManager.TRANSFER_KEY) ? this::clientsort$transfer : null;
         }
      } else {
         return null;
      }
   }

   @Unique
   private boolean clientsort$openEditor() {
      Minecraft.getInstance().setScreen(new SelectorScreen((AbstractContainerScreen<?>)this));
      return true;
   }

   @Unique
   private boolean clientsort$sort() {
      if (this.hoveredSlot == null) {
         return false;
      }

      SortOrder sortOrder = Config.options().sortOrder;
      if (Screen.hasShiftDown()) {
         sortOrder = Config.options().shiftSortOrder;
      } else if (Screen.hasControlDown()) {
         sortOrder = Config.options().ctrlSortOrder;
      } else if (Screen.hasAltDown()) {
         sortOrder = Config.options().altSortOrder;
      }

      return SingleUseOperator.sort((AbstractContainerScreen<?>)this, this.hoveredSlot, false, sortOrder);
   }

   @Unique
   private boolean clientsort$fillStacks() {
      return this.hoveredSlot == null ? false : SingleUseOperator.fillStacks((AbstractContainerScreen<?>)this, this.hoveredSlot, false);
   }

   @Unique
   private boolean clientsort$transferMatching() {
      return this.hoveredSlot == null ? false : SingleUseOperator.transferMatching((AbstractContainerScreen<?>)this, this.hoveredSlot, false);
   }

   @Unique
   private boolean clientsort$transfer() {
      return this.hoveredSlot == null ? false : SingleUseOperator.transfer((AbstractContainerScreen<?>)this, this.hoveredSlot, false);
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void afterRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      if (this.equals(Minecraft.getInstance().screen)) {
         if (ClientSort.overlayMessage != null) {
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 1000.0F);
            ClientSort.overlayMessage.renderWidget(graphics, mouseX, mouseY, partialTick);
            graphics.pose().popPose();
         }

         if (dev.terminalmc.clientsort.ClientSort.debug()) {
            ContainerScreenHelper<?> helper = ContainerScreenHelper.of((T)((AbstractContainerScreen)this));
            float scale = 0.7F;
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 1000.0F);
            graphics.pose().scale(scale, scale, 0.0F);

            for (Slot slot : this.menu.slots) {
               int slotId = ((ISlot)slot).clientsort$getIndexInMenu();
               int slotIdx = ((ISlot)slot).clientsort$getIndexInContainer();
               if (!(Minecraft.getInstance().screen instanceof EditorScreen)) {
                  Object object = dev.terminalmc.clientsort.ClientSort.getObj(slot, this.getMenu());
                  if (object == null) {
                     continue;
                  }

                  boolean isPlayerInv = slot.container instanceof Inventory;
                  Component invTitle = isPlayerInv ? ((AbstractContainerScreenAccessor)this).clientsort$getPlayerInventoryTitle() : this.getTitle();
                  ClassPolicy policy = PolicyManager.getPolicy(object.getClass(), invTitle.getString());
                  if (policy != null && policy.ignoredSlots().contains(slotIdx)) {
                     graphics.drawString(
                        Minecraft.getInstance().font,
                        "❌",
                        (int)((((AbstractContainerScreenAccessor)this).clientsort$getLeftPos() + slot.x) / scale),
                        (int)((((AbstractContainerScreenAccessor)this).clientsort$getTopPos() + slot.y) / scale),
                        -65536
                     );
                  }
               }

               graphics.drawString(
                  Minecraft.getInstance().font,
                  String.valueOf(hasShiftDown() ? slotIdx : slotId),
                  (int)((((AbstractContainerScreenAccessor)this).clientsort$getLeftPos() + slot.x) / scale),
                  (int)((((AbstractContainerScreenAccessor)this).clientsort$getTopPos() + slot.y + 12) / scale),
                  -1
               );
               graphics.drawString(
                  Minecraft.getInstance().font,
                  String.valueOf(helper.getScope(slot).ordinal()),
                  (int)((((AbstractContainerScreenAccessor)this).clientsort$getLeftPos() + slot.x + 12) / scale),
                  (int)((((AbstractContainerScreenAccessor)this).clientsort$getTopPos() + slot.y + 12) / scale),
                  -1
               );
            }

            graphics.pose().popPose();
         }
      }
   }
}
