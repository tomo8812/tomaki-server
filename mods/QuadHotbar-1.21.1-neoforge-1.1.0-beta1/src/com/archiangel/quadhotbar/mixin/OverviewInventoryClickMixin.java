package com.archiangel.quadhotbar.mixin;

import com.archiangel.quadhotbar.QuadHotbarOverviewMenu;
import com.archiangel.quadhotbar.QuadHotbarOverviewNetwork;
import com.archiangel.quadhotbar.client.QuadHotbarOverviewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class OverviewInventoryClickMixin {
   @Unique
   private boolean quadhotbar$routing;

   @Shadow
   public abstract void handleInventoryMouseClick(int var1, int var2, int var3, ClickType var4, Player var5);

   @Shadow
   public abstract void handlePlaceRecipe(int var1, RecipeHolder<?> var2, boolean var3);

   @Inject(method = "handlePlaceRecipe", at = @At("HEAD"), cancellable = true)
   private void quadhotbar$routeRecipe(int id, RecipeHolder<?> recipe, boolean shift, CallbackInfo ci) {
      Minecraft mc = Minecraft.getInstance();
      if (id == 0
         && mc.player != null
         && mc.player.containerMenu instanceof QuadHotbarOverviewMenu menu
         && !menu.standalone
         && mc.screen instanceof QuadHotbarOverviewScreen screen
         && screen.isDelegatingBaseInput()) {
         this.handlePlaceRecipe(menu.containerId, recipe, shift);
         ci.cancel();
      }
   }

   @Inject(method = "handleInventoryMouseClick", at = @At("HEAD"), cancellable = true)
   private void quadhotbar$routeVanillaClick(int id, int slot, int button, ClickType type, Player player, CallbackInfo ci) {
      if (player.containerMenu instanceof QuadHotbarOverviewMenu menu) {
         if (!this.quadhotbar$routing
            && id == player.inventoryMenu.containerId
            && Minecraft.getInstance().screen instanceof QuadHotbarOverviewScreen screen
            && screen.isDelegatingBaseInput()) {
            this.quadhotbar$routing = true;

            try {
               this.handleInventoryMouseClick(menu.containerId, slot < 0 ? slot : menu.pageSlots + slot, button, type, player);
            } finally {
               this.quadhotbar$routing = false;
            }

            ci.cancel();
         } else {
            if (player.hasInfiniteMaterials()) {
               QuadHotbarOverviewNetwork.syncCreativeCursor();
            }
         }
      }
   }
}
