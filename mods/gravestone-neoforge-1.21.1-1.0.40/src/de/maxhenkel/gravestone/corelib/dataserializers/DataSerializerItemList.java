package de.maxhenkel.gravestone.corelib.dataserializers;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.ItemStack;

public class DataSerializerItemList {
   private static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<ItemStack>> CODEC = new StreamCodec<RegistryFriendlyByteBuf, NonNullList<ItemStack>>() {
      public void encode(RegistryFriendlyByteBuf buf, NonNullList<ItemStack> itemStacks) {
         buf.writeInt(itemStacks.size());

         for (ItemStack itemStack : itemStacks) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemStack);
         }
      }

      public NonNullList<ItemStack> decode(RegistryFriendlyByteBuf itemStacks) {
         int length = itemStacks.readInt();
         NonNullList<ItemStack> list = NonNullList.withSize(length, ItemStack.EMPTY);

         for (int i = 0; i < list.size(); i++) {
            list.set(i, (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(itemStacks));
         }

         return list;
      }

      // $VF: synthetic method
      // $VF: bridge method
      public Object decode(Object param1) {
         return this.decode((RegistryFriendlyByteBuf)var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public void encode(Object param1, Object nullx) {
         this.encode((RegistryFriendlyByteBuf)var1, (NonNullList<ItemStack>)nullx);
      }
   };

   public static EntityDataSerializer<NonNullList<ItemStack>> create() {
      return new EntityDataSerializer<NonNullList<ItemStack>>() {
         public StreamCodec<? super RegistryFriendlyByteBuf, NonNullList<ItemStack>> codec() {
            return DataSerializerItemList.CODEC;
         }

         public NonNullList<ItemStack> copy(NonNullList<ItemStack> itemStacks) {
            NonNullList<ItemStack> list = NonNullList.withSize(itemStacks.size(), ItemStack.EMPTY);

            for (int i = 0; i < itemStacks.size(); i++) {
               list.set(i, ((ItemStack)itemStacks.get(i)).copy());
            }

            return list;
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object copy(Object param1) {
            return this.copy((NonNullList<ItemStack>)var1);
         }
      };
   }
}
