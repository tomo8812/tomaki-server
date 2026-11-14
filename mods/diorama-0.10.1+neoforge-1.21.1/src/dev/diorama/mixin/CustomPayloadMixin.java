package dev.diorama.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.diorama.XaeroChannels;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.protocol.common.custom.CustomPacketPayload$1")
public abstract class CustomPayloadMixin {
   @WrapOperation(
      method = "decode",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/StreamCodec;decode(Ljava/lang/Object;)Ljava/lang/Object;")
   )
   private Object diorama$peek(StreamCodec<Object, Object> codec, Object input, Operation<Object> original, @Local ResourceLocation channel) {
      XaeroChannels.Message message = XaeroChannels.peek(channel, (ByteBuf)input);
      Object payload = original.call(new Object[]{codec, input});
      if (message != null) {
         XaeroChannels.peeked(payload, message);
      }

      return payload;
   }

   @Inject(
      method = "encode(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void diorama$write(FriendlyByteBuf output, CustomPacketPayload value, CallbackInfo ci) {
      if (value instanceof XaeroChannels.Message message) {
         output.writeResourceLocation(message.type().id());
         XaeroChannels.write(output, message);
         ci.cancel();
      }
   }
}
