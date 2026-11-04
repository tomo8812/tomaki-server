package org.valkyrienskies.mod.mixin.client.multiplayer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1297;
import net.minecraft.class_2604;
import net.minecraft.class_2678;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.IShipObjectWorldClientCreator;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

@Mixin(class_634.class)
public class MixinClientPacketListener {
   @Shadow
   private class_638 field_3699;

   @Inject(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V",
         shift = Shift.AFTER
      ),
      method = "handleLogin"
   )
   private void beforeHandleLogin(class_2678 packet, CallbackInfo ci) {
      ((IShipObjectWorldClientCreator)class_310.method_1551()).createShipObjectWorldClient();
   }

   @Inject(
      method = "handleAddEntity",
      at = @At(
         value = "INVOKE",
         shift = Shift.AFTER,
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V"
      ),
      cancellable = true
   )
   private void handleShipMountingEntity(class_2604 packet, CallbackInfo ci) {
      if (packet.method_11169().equals(ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE)) {
         ci.cancel();
         double d = packet.method_11175();
         double e = packet.method_11174();
         double f = packet.method_11176();
         class_1297 entity = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.method_5883(this.field_3699);
         int i = packet.method_11167();
         entity.method_43391(d, e, f);
         entity.method_24203(d, e, f);
         entity.method_36457(packet.method_11171() * 360.0F / 256.0F);
         entity.method_36456(packet.method_11168() * 360.0F / 256.0F);
         entity.method_5838(i);
         entity.method_5826(packet.method_11164());
         this.field_3699.method_53875(entity);
      }
   }

   @WrapOperation(method = "handleTeleportEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;lerpTo(DDDFFI)V"))
   private void teleportingWithNoStep(class_1297 instance, double x, double y, double z, float yRot, float xRot, int lerpSteps, Operation<Void> lerpTo) {
      if (VSGameUtilsKt.getShipObjectManagingPos(instance.method_37908(), instance.method_23317(), instance.method_23318(), instance.method_23321()) != null) {
         instance.method_5814(x, y, z);
         lerpTo.call(new Object[]{instance, x, y, z, yRot, xRot, 1});
      } else {
         lerpTo.call(new Object[]{instance, x, y, z, yRot, xRot, lerpSteps});
      }
   }
}
