package com.archiangel.quadhotbar.mixin;

import com.archiangel.quadhotbar.QuadHotbarPages;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
   private static final String QUADHOTBAR_SELECTED_SLOT_TAG = "QuadHotbarSelectedSlot";

   @Shadow
   public abstract Inventory getInventory();

   @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
   private void quadhotbar$saveSelectedSlot(CompoundTag compound, CallbackInfo ci) {
      compound.putInt("QuadHotbarSelectedSlot", Mth.clamp(this.getInventory().selected, 0, 35));
      QuadHotbarPages.save((Player)this, compound);
   }

   @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
   private void quadhotbar$loadSelectedSlot(CompoundTag compound, CallbackInfo ci) {
      QuadHotbarPages.load((Player)this, compound);
      if (compound.contains("QuadHotbarSelectedSlot")) {
         this.getInventory().selected = Mth.clamp(compound.getInt("QuadHotbarSelectedSlot"), 0, 35);
      }
   }
}
