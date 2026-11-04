package dev.terminalmc.clientsort.client.gui.widget;

import dev.terminalmc.clientsort.client.config.ClassPolicy;
import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.config.Operation;
import dev.terminalmc.clientsort.client.config.Vec2i;
import dev.terminalmc.clientsort.client.gui.screen.edit.ContainerEditorScreen;
import dev.terminalmc.clientsort.client.gui.screen.edit.EditorScreen;
import dev.terminalmc.clientsort.client.gui.screen.edit.PlayerEditorScreen;
import dev.terminalmc.clientsort.mixin.client.accessor.AbstractContainerScreenAccessor;
import dev.terminalmc.clientsort.util.Localization;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TriggerButton extends Button {
   public static final int WIDTH = 13;
   public static final int HEIGHT = 13;
   public static final int HALF_WIDTH = 6;
   public static final int HALF_HEIGHT = 6;
   private final AbstractContainerScreen<?> screen;
   public final Container container;
   public final Slot referenceSlot;
   public final boolean referenceLeft;
   public final boolean isPlayerInv;
   private final WidgetSprites sprites;
   private final Component name;
   @Nullable
   public final String activePolicyKey;
   public final String lowestPolicyKey;
   public Vec2i offset;
   public boolean offsetFromSlot;
   public boolean operationAllowed;

   protected TriggerButton(
      AbstractContainerScreen<?> screen,
      Container container,
      Slot referenceSlot,
      boolean referenceLeft,
      boolean isPlayerInv,
      WidgetSprites sprites,
      Component name,
      @Nullable String activePolicyKey,
      String lowestPolicyKey,
      Vec2i offset,
      boolean offsetFromSlot,
      boolean operationAllowed,
      boolean active,
      OnPress onPress
   ) {
      super(
         ((AbstractContainerScreenAccessor)screen).clientsort$getLeftPos() + ((AbstractContainerScreenAccessor)screen).clientsort$getImageWidth() + offset.x(),
         ((AbstractContainerScreenAccessor)screen).clientsort$getTopPos() + referenceSlot.y + offset.y(),
         13,
         13,
         CommonComponents.EMPTY,
         onPress,
         DEFAULT_NARRATION
      );
      this.screen = screen;
      this.container = container;
      this.referenceSlot = referenceSlot;
      this.referenceLeft = referenceLeft;
      this.isPlayerInv = isPlayerInv;
      this.sprites = sprites;
      this.offset = offset;
      this.offsetFromSlot = offsetFromSlot;
      this.activePolicyKey = activePolicyKey;
      this.lowestPolicyKey = lowestPolicyKey;
      this.operationAllowed = operationAllowed;
      this.active = active;
      this.name = name;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      if (this.isMouseOver(mouseX, mouseY)) {
         boolean rightClick = mouseButton == 1;
         if (Minecraft.getInstance().screen instanceof EditorScreen) {
            if (rightClick) {
               if (Screen.hasShiftDown()) {
                  this.operationAllowed = !this.operationAllowed;
               } else {
                  this.active = !this.active;
               }
            }

            return true;
         }

         if (rightClick) {
            this.openEditScreen();
            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
   }

   public void openEditScreen() {
      Minecraft.getInstance().setScreen(this.isPlayerInv ? new PlayerEditorScreen(this.screen, this) : new ContainerEditorScreen(this.screen, this));
   }

   public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      AbstractContainerScreenAccessor acs = (AbstractContainerScreenAccessor)this.screen;
      int newX = Math.clamp(this.getAnchorSideX(acs) + this.offset.x(), 0, this.screen.width - 13);
      int newY = Math.clamp(acs.clientsort$getTopPos() + Math.max(0, this.referenceSlot.y) + this.offset.y(), 0, this.screen.height - 13);
      this.setX(newX);
      this.setY(newY);
      ResourceLocation texture = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
      graphics.blitSprite(texture, this.getX(), this.getY(), 0, this.width, this.height);
      if (!this.operationAllowed) {
         graphics.hLine(this.getX(), this.getX() + this.width - 1, this.getY() + this.height / 2, -65536);
      }

      if (this.isMouseOver(mouseX, mouseY)) {
         if (this.getTooltip() == null) {
            if (Minecraft.getInstance().screen instanceof EditorScreen) {
               Component visibilityStatus = Localization.localized("editor", this.active ? "enabled" : "disabled")
                  .withStyle(this.active ? ChatFormatting.GREEN : ChatFormatting.RED);
               Component operationStatus = Localization.localized("editor", this.operationAllowed ? "enabled" : "disabled")
                  .withStyle(this.operationAllowed ? ChatFormatting.GREEN : ChatFormatting.RED);
               this.setTooltip(
                  Tooltip.create(
                     Component.empty()
                        .append(this.name)
                        .append("\n")
                        .append(Localization.localized("editor", "visibility", visibilityStatus))
                        .append("\n")
                        .append(Localization.localized("editor", "operation", operationStatus))
                  )
               );
            } else if (Config.options().showButtonTooltips) {
               this.setTooltip(Tooltip.create(this.name));
            }
         }
      } else {
         this.setTooltip(null);
      }
   }

   protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
      if (Minecraft.getInstance().screen instanceof EditorScreen) {
         AbstractContainerScreenAccessor acs = (AbstractContainerScreenAccessor)this.screen;
         int newX = Math.clamp((int)mouseX - 6, 0, this.screen.width - 13);
         int newY = Math.clamp((int)mouseY - 6, 0, this.screen.height - 13);
         this.offset = new Vec2i(newX - this.getAnchorSideX(acs), newY - (acs.clientsort$getTopPos() + Math.clamp(this.referenceSlot.y, 0, this.screen.height)));
      } else {
         super.onDrag(mouseX, mouseY, dragX, dragY);
      }
   }

   public ComponentPath nextFocusPath(@NotNull FocusNavigationEvent event) {
      return null;
   }

   public void setFocused(boolean focused) {
      if (!focused) {
         super.setFocused(false);
      }
   }

   public abstract boolean getPolicyStatus(ClassPolicy var1);

   public abstract void savePolicy(@Nullable Vec2i var1, boolean var2, @Nullable Operation var3, boolean var4, Collection<Integer> var5);

   private int getAnchorSideX(AbstractContainerScreenAccessor acs) {
      int anchorSideX;
      if (this.offsetFromSlot) {
         anchorSideX = acs.clientsort$getLeftPos() + this.referenceSlot.x;
         if (this.referenceLeft) {
            anchorSideX -= 13;
         } else {
            anchorSideX += 16;
         }
      } else {
         anchorSideX = acs.clientsort$getLeftPos();
         if (!this.referenceLeft) {
            anchorSideX += acs.clientsort$getImageWidth();
         }
      }

      return anchorSideX;
   }
}
