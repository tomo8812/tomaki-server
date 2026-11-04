package net.satisfy.farm_and_charm.core.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.satisfy.farm_and_charm.FarmAndCharm;

public record UpdateCartPullingPacket(int pullingId, int cartId) implements CustomPacketPayload {
   public static final Type<UpdateCartPullingPacket> TYPE = new Type(FarmAndCharm.identifier("update_cart_pulling"));
   public static final StreamCodec<FriendlyByteBuf, UpdateCartPullingPacket> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, UpdateCartPullingPacket>() {
      public UpdateCartPullingPacket decode(FriendlyByteBuf buf) {
         return new UpdateCartPullingPacket(buf.readVarInt(), buf.readVarInt());
      }

      public void encode(FriendlyByteBuf buf, UpdateCartPullingPacket packet) {
         buf.writeVarInt(packet.pullingId());
         buf.writeVarInt(packet.cartId());
      }

      // $VF: synthetic method
      // $VF: bridge method
      public Object decode(Object param1) {
         return this.decode((FriendlyByteBuf)var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public void encode(Object param1, Object nullx) {
         this.encode((FriendlyByteBuf)var1, (UpdateCartPullingPacket)nullx);
      }
   };

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
