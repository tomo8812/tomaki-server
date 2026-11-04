package org.embeddedt.modernfix.common.mixin.perf.smart_ingredient_sync;

import java.util.Optional;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.item.crafting.Ingredient.Value;
import org.embeddedt.modernfix.neoforge.packet.SmartIngredientSyncPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/world/item/crafting/Ingredient$1")
public abstract class IngredientMixin {
   @Inject(
      method = "encode(Lnet/minecraft/network/RegistryFriendlyByteBuf;Lnet/minecraft/world/item/crafting/Ingredient;)V",
      at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/ItemStack;LIST_STREAM_CODEC:Lnet/minecraft/network/codec/StreamCodec;"),
      cancellable = true
   )
   private void checkForVanillaTagIngredient(RegistryFriendlyByteBuf buf, Ingredient ingredient, CallbackInfo ci) {
      if (SmartIngredientSyncPayload.CLIENT_HAS_SMART_INGREDIENT_SYNC.get() && !ingredient.isCustom()) {
         Value[] values = ingredient.getValues();
         if (values.length == 1 && values[0] instanceof TagValue tagValue) {
            Optional<Named<Item>> optionalHolderSet = BuiltInRegistries.ITEM.getTag(tagValue.tag());
            if (optionalHolderSet.isEmpty()) {
               return;
            }

            ci.cancel();
            buf.writeVarInt(-2);
            buf.writeResourceLocation(tagValue.tag().location());
         }
      }
   }

   @Inject(
      method = "decode(Lnet/minecraft/network/RegistryFriendlyByteBuf;)Lnet/minecraft/world/item/crafting/Ingredient;",
      at = @At("HEAD"),
      cancellable = true,
      remap = false
   )
   private void decodeSmartIngredient(RegistryFriendlyByteBuf buf, CallbackInfoReturnable<Ingredient> cir) {
      int readerIndex = buf.readerIndex();
      int sizeId = buf.readVarInt();
      if (sizeId == -2) {
         TagKey<Item> tagKey = TagKey.create(Registries.ITEM, buf.readResourceLocation());
         cir.setReturnValue(Ingredient.of(tagKey));
      } else {
         buf.readerIndex(readerIndex);
      }
   }
}
