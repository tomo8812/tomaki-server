package com.archiangel.quadhotbar.mixin;

import com.archiangel.quadhotbar.QuadHotbarOverviewMenu;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
   @Shadow
   public ServerPlayer player;

   @Inject(method = "handlePlaceRecipe", at = @At("TAIL"))
   private void quadhotbar$vanillaRecipeInOverview(ServerboundPlaceRecipePacket packet, CallbackInfo ci) {
      if (!this.player.isSpectator()
         && this.player.containerMenu instanceof QuadHotbarOverviewMenu menu
         && !menu.standalone
         && menu.containerId == packet.getContainerId()
         && menu.stillValid(this.player)) {
         this.player.server.getRecipeManager().byKey(packet.getRecipe()).ifPresent(recipe -> {
            this.player.inventoryMenu.handlePlacement(packet.isShiftDown(), recipe, this.player);
            menu.broadcastFullState();
         });
      }
   }

   @Redirect(method = "handleSetCarriedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getSelectionSize()I"))
   private int quadhotbar$allowFullInventorySelection() {
      return 36;
   }
}
