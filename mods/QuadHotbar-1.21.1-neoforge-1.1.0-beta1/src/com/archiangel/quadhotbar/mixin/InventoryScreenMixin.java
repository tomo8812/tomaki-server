package com.archiangel.quadhotbar.mixin;

import com.archiangel.quadhotbar.client.QuadHotbarClientEvents;
import com.archiangel.quadhotbar.client.QuadHotbarControlButton;
import com.archiangel.quadhotbar.client.QuadHotbarInventoryPageUi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends Screen {
   @Unique
   private Button quadhotbar$previous;
   @Unique
   private Button quadhotbar$next;
   @Unique
   private Button quadhotbar$overview;

   protected InventoryScreenMixin(Component title) {
      super(title);
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void quadhotbar$addPageControls(CallbackInfo ci) {
      InventoryScreen screen = (InventoryScreen)this;
      int y = screen.getGuiTop() + 138;
      this.quadhotbar$previous = (Button)this.addRenderableWidget(
         new QuadHotbarControlButton(screen.getGuiLeft() - 23, y, Component.literal("<"), button -> QuadHotbarClientEvents.requestInventoryPage(-1), false)
      );
      this.quadhotbar$next = (Button)this.addRenderableWidget(
         new QuadHotbarControlButton(screen.getGuiLeft() + 179, y, Component.literal(">"), button -> QuadHotbarClientEvents.requestInventoryPage(1), false)
      );
      this.quadhotbar$overview = (Button)this.addRenderableWidget(
         new QuadHotbarControlButton(
            screen.getGuiLeft() + 179, y - 24, Component.translatable("quadhotbar.overview.title"), button -> QuadHotbarInventoryPageUi.openOverview(), true
         )
      );
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void quadhotbar$updatePageButtons(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      if (this.quadhotbar$previous != null) {
         boolean show = QuadHotbarInventoryPageUi.showButtons(this.minecraft);
         this.quadhotbar$previous.visible = show;
         this.quadhotbar$next.visible = show;
         this.quadhotbar$overview.visible = QuadHotbarInventoryPageUi.showOverviewButton(this.minecraft);
         InventoryScreen screen = (InventoryScreen)this;
         this.quadhotbar$previous.setX(screen.getGuiLeft() - 23);
         this.quadhotbar$next.setX(screen.getGuiLeft() + 179);
         this.quadhotbar$overview.setX(screen.getGuiLeft() + 179);
      }
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void quadhotbar$renderPageLabel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      InventoryScreen screen = (InventoryScreen)this;
      QuadHotbarInventoryPageUi.renderPageLabel(graphics, this.minecraft, this.font, screen.getGuiLeft() + 88, screen.getGuiTop() - 13);
   }

   @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
   private void quadhotbar$turnInventoryPageWithHotbarBinds(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      if (!(this.getFocused() instanceof EditBox) && QuadHotbarInventoryPageUi.handlePageKey(this.minecraft, keyCode, scanCode)) {
         cir.setReturnValue(true);
      }
   }
}
