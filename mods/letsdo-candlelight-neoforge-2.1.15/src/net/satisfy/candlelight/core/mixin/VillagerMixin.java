package net.satisfy.candlelight.core.mixin;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.satisfy.candlelight.core.util.DinnerGuest;
import net.satisfy.candlelight.platform.PlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin implements DinnerGuest {
   @Unique
   private static final String candlelight$DISCOUNT_KEY = "CandlelightDinnerDiscountUntil";
   @Unique
   private static final String candlelight$HOST_KEY = "CandlelightDinnerHost";
   @Unique
   private long candlelight$dinnerDiscountUntil;
   @Unique
   private UUID candlelight$dinnerHost;

   @Override
   public long candlelight$getDinnerDiscountUntil() {
      return this.candlelight$dinnerDiscountUntil;
   }

   @Override
   public void candlelight$setDinnerDiscountUntil(long gameTime) {
      this.candlelight$dinnerDiscountUntil = gameTime;
   }

   @Override
   public void candlelight$setDinnerHost(UUID host) {
      this.candlelight$dinnerHost = host;
   }

   @Inject(method = "updateSpecialPrices(Lnet/minecraft/world/entity/player/Player;)V", at = @At("TAIL"))
   private void candlelight$applyDinnerDiscount(Player player, CallbackInfo ci) {
      Villager villager = (Villager)this;
      if (villager.level().getGameTime() < this.candlelight$dinnerDiscountUntil) {
         if (!PlatformHelper.discountOnlyHost() || player.getUUID().equals(this.candlelight$dinnerHost)) {
            for (MerchantOffer offer : villager.getOffers()) {
               offer.addToSpecialPriceDiff(-Math.round(offer.getBaseCostA().getCount() * PlatformHelper.dinnerDiscountPercent() / 100.0F));
            }
         }
      }
   }

   @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
   private void candlelight$saveDinnerDiscount(CompoundTag tag, CallbackInfo ci) {
      if (this.candlelight$dinnerDiscountUntil > 0L) {
         tag.putLong("CandlelightDinnerDiscountUntil", this.candlelight$dinnerDiscountUntil);
         if (this.candlelight$dinnerHost != null) {
            tag.putUUID("CandlelightDinnerHost", this.candlelight$dinnerHost);
         }
      }
   }

   @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
   private void candlelight$loadDinnerDiscount(CompoundTag tag, CallbackInfo ci) {
      this.candlelight$dinnerDiscountUntil = tag.getLong("CandlelightDinnerDiscountUntil");
      this.candlelight$dinnerHost = tag.hasUUID("CandlelightDinnerHost") ? tag.getUUID("CandlelightDinnerHost") : null;
   }
}
