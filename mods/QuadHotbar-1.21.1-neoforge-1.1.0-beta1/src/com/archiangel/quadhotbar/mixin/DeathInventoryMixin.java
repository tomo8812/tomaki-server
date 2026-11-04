package com.archiangel.quadhotbar.mixin;

import com.archiangel.quadhotbar.QuadHotbarPages;
import com.archiangel.quadhotbar.compat.DeathPageAccess;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.stream.IntStream;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {"de.maxhenkel.corpse.corelib.death.Death", "de.maxhenkel.gravestone.corelib.death.Death"}, remap = false)
public abstract class DeathInventoryMixin implements DeathPageAccess {
   @Unique
   private int[] quadhotbar$positions = new int[0];
   @Unique
   private int quadhotbar$activePage = -1;
   @Unique
   private final IdentityHashMap<ItemStack, Integer> quadhotbar$remaining = new IdentityHashMap<>();
   @Unique
   private final IdentityHashMap<ItemStack, Integer> quadhotbar$dropped = new IdentityHashMap<>();

   @Shadow
   public abstract NonNullList<ItemStack> getAdditionalItems();

   @Inject(method = "fromPlayer", at = @At("RETURN"))
   private static void quadhotbar$captureActivePage(Player player, CallbackInfoReturnable<Object> cir) {
      if (cir.getReturnValue() instanceof DeathPageAccess death) {
         death.quadhotbar$setActivePage(QuadHotbarPages.state(player).activePage);
      }
   }

   @Inject(method = "processDrops", at = @At("HEAD"))
   private void quadhotbar$captureAddresses(Collection<ItemEntity> drops, CallbackInfo ci) {
      this.quadhotbar$dropped.clear();
      this.quadhotbar$remaining.clear();

      for (ItemEntity entity : drops) {
         CompoundTag tag = entity.getPersistentData();
         if (tag.contains("QuadHotbarPageSlot")) {
            int address = tag.getInt("QuadHotbarPageSlot");
            if (address >= 0 && address < 3600) {
               this.quadhotbar$dropped.put(entity.getItem(), address);
            }
         }
      }
   }

   @Inject(method = "processDrops", at = @At("TAIL"))
   private void quadhotbar$mapCanonicalItems(Collection<ItemEntity> drops, CallbackInfo ci) {
      NonNullList<ItemStack> items = this.getAdditionalItems();
      this.quadhotbar$positions = new int[items.size()];
      Arrays.fill(this.quadhotbar$positions, -1);

      for (int i = 0; i < items.size(); i++) {
         Integer address = this.quadhotbar$dropped.get(items.get(i));
         if (address != null) {
            this.quadhotbar$positions[i] = address;
            this.quadhotbar$remaining.put((ItemStack)items.get(i), address);
         }
      }

      this.quadhotbar$dropped.clear();
   }

   @Inject(method = "toNBT(Lnet/minecraft/core/HolderLookup$Provider;Z)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"))
   private void quadhotbar$saveAddresses(Provider registries, boolean items, CallbackInfoReturnable<CompoundTag> cir) {
      if (this.quadhotbar$activePage >= 0) {
         ((CompoundTag)cir.getReturnValue()).putInt("QuadHotbarActivePage", this.quadhotbar$activePage);
      }

      if (items && this.quadhotbar$positions.length > 0) {
         int[] addresses = this.quadhotbar$pageSlots();
         ((CompoundTag)cir.getReturnValue())
            .putIntArray(
               "QuadHotbarPageSlots",
               IntStream.range(0, this.getAdditionalItems().size())
                  .filter(i -> !((ItemStack)this.getAdditionalItems().get(i)).isEmpty())
                  .map(i -> addresses[i])
                  .toArray()
            );
      }
   }

   @Inject(method = "fromNBT", at = @At("RETURN"))
   private static void quadhotbar$loadAddresses(Provider registries, CompoundTag tag, CallbackInfoReturnable<Object> cir) {
      if (cir.getReturnValue() instanceof DeathPageAccess death) {
         if (tag.contains("QuadHotbarActivePage")) {
            death.quadhotbar$setActivePage(tag.getInt("QuadHotbarActivePage"));
         }

         int[] addresses = tag.getIntArray("QuadHotbarPageSlots");
         death.quadhotbar$setPageSlots(Arrays.copyOf(addresses, Math.min(addresses.length, 3600)));
      }
   }

   @Override
   public int[] quadhotbar$pageSlots() {
      int[] positions = new int[this.getAdditionalItems().size()];

      for (int i = 0; i < positions.length; i++) {
         positions[i] = this.quadhotbar$remaining.getOrDefault(this.getAdditionalItems().get(i), -1);
      }

      return positions;
   }

   @Override
   public void quadhotbar$setPageSlots(int[] slots) {
      this.quadhotbar$positions = slots;
      this.quadhotbar$remaining.clear();

      for (int i = 0; i < Math.min(slots.length, this.getAdditionalItems().size()); i++) {
         if (slots[i] >= 0 && slots[i] < 3600) {
            this.quadhotbar$remaining.put((ItemStack)this.getAdditionalItems().get(i), slots[i]);
         }
      }
   }

   @Override
   public int quadhotbar$activePage() {
      return this.quadhotbar$activePage;
   }

   @Override
   public void quadhotbar$setActivePage(int page) {
      this.quadhotbar$activePage = page >= 0 && page < 100 ? page : -1;
   }
}
