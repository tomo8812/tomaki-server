package com.archiangel.quadhotbar.mixin;

import com.archiangel.quadhotbar.client.QuadHotbarClientEvents;
import com.archiangel.quadhotbar.client.QuadHotbarControlButton;
import com.archiangel.quadhotbar.client.QuadHotbarInventoryPageUi;
import com.archiangel.quadhotbar.client.QuadHotbarOverviewScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.CreativeTabsScreenPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends Screen {
   @Shadow
   private CreativeTabsScreenPage currentPage;
   @Shadow
   private EditBox searchBox;
   @Unique
   private CreativeTabsScreenPage quadhotbar$lastCreativePage;
   @Unique
   private Button quadhotbar$previous;
   @Unique
   private Button quadhotbar$next;
   @Unique
   private Button quadhotbar$overview;

   protected CreativeInventoryScreenMixin(Component title) {
      super(title);
   }

   @Inject(method = "hasClickedOutside", at = @At("HEAD"), cancellable = true)
   private void quadhotbar$safeOverviewOverlap(double x, double y, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
      if (this.minecraft.screen instanceof QuadHotbarOverviewScreen overview && overview.isBaseScreen(this) && overview.containsPanel(x, y)) {
         cir.setReturnValue(false);
      }
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void quadhotbar$addPageControls(CallbackInfo ci) {
      CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen)this;
      int y = screen.getGuiTop() + 109;
      this.quadhotbar$previous = (Button)this.addRenderableWidget(
         new QuadHotbarControlButton(screen.getGuiLeft() - 23, y, Component.literal("<"), button -> QuadHotbarClientEvents.requestInventoryPage(-1), false)
      );
      this.quadhotbar$next = (Button)this.addRenderableWidget(
         new QuadHotbarControlButton(
            screen.getGuiLeft() + screen.getXSize() + 3, y, Component.literal(">"), button -> QuadHotbarClientEvents.requestInventoryPage(1), false
         )
      );
      this.quadhotbar$overview = (Button)this.addRenderableWidget(
         new QuadHotbarControlButton(
            screen.getGuiLeft() + screen.getXSize() + 3,
            y - 24,
            Component.translatable("quadhotbar.overview.title"),
            button -> QuadHotbarInventoryPageUi.openOverview(),
            true
         )
      );
      this.quadhotbar$lastCreativePage = this.currentPage;
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void quadhotbar$updatePageControls(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      if (this.quadhotbar$lastCreativePage != null && this.currentPage != this.quadhotbar$lastCreativePage) {
         QuadHotbarClientEvents.hideInventoryPageLabel();
      }

      this.quadhotbar$lastCreativePage = this.currentPage;
      if (this.quadhotbar$previous != null) {
         boolean show = QuadHotbarInventoryPageUi.showButtons(this.minecraft);
         this.quadhotbar$previous.visible = show;
         this.quadhotbar$next.visible = show;
         this.quadhotbar$overview.visible = QuadHotbarInventoryPageUi.showOverviewButton(this.minecraft);
      }
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void quadhotbar$renderInventoryPageLabel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen)this;
      QuadHotbarInventoryPageUi.renderPageLabel(graphics, this.minecraft, this.font, screen.getGuiLeft() + screen.getXSize() / 2, screen.getGuiTop() - 62);
   }

   @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
   private void quadhotbar$turnInventoryPageWithHotbarBinds(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      if ((this.searchBox == null || !this.searchBox.isFocused() || !this.searchBox.isVisible())
         && QuadHotbarInventoryPageUi.handlePageKey(this.minecraft, keyCode, scanCode)) {
         cir.setReturnValue(true);
      }
   }
}
