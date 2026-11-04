package net.satisfy.candlelight.core.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.candlelight.core.item.WineGlassItem;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
   @Unique
   private boolean candlelight$dabbing;
   @Unique
   private boolean candlelight$raising;
   @Unique
   private float candlelight$drink;

   @Inject(
      method = "renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
      at = @At("HEAD")
   )
   private void candlelight$startDab(
      AbstractClientPlayer player,
      float partialTicks,
      float pitch,
      InteractionHand hand,
      float swingProgress,
      ItemStack stack,
      float equippedProgress,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int light,
      CallbackInfo ci
   ) {
      this.candlelight$raising = player.isUsingItem() && player.getUsedItemHand() == hand && WineGlassItem.isBorrowed(stack);
      if (this.candlelight$raising) {
         HumanoidArm side = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
         float direction = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;
         float ticks = player.getTicksUsingItem() + partialTicks;
         float raise = Mth.clamp(ticks / 8.0F, 0.0F, 1.0F);
         raise = raise * raise * (3.0F - 2.0F * raise);
         this.candlelight$drink = WineGlassItem.isFilled(stack) ? Mth.clamp((ticks - 8.0F - 8.0F) / 10.0F, 0.0F, 1.0F) : 0.0F;
         float clink = ticks > 8.0F && ticks < 12.0F ? Mth.sin((ticks - 8.0F) / 4.0F * (float) Math.PI) * 0.03F : 0.0F;
         poseStack.pushPose();
         poseStack.translate(
            -0.12F * direction * raise - 0.2F * direction * this.candlelight$drink,
            0.16F * raise + 0.1F * this.candlelight$drink + clink,
            -0.08F * raise + 0.08F * this.candlelight$drink
         );
      } else {
         this.candlelight$dabbing = player.isUsingItem() && player.getUsedItemHand() == hand && stack.is((Item)ObjectRegistry.NAPKIN.get());
         if (this.candlelight$dabbing) {
            HumanoidArm side = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
            float direction = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;
            float time = player.tickCount + partialTicks;
            float dab = Mth.sin(time * 0.6F) * 0.04F;
            poseStack.pushPose();
            poseStack.translate(-0.28F * direction, 0.22F + dab, 0.0F);
         }
      }
   }

   @Inject(
      method = "renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
      )
   )
   private void candlelight$tiltNapkin(
      AbstractClientPlayer player,
      float partialTicks,
      float pitch,
      InteractionHand hand,
      float swingProgress,
      ItemStack stack,
      float equippedProgress,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int light,
      CallbackInfo ci
   ) {
      if (this.candlelight$raising) {
         HumanoidArm side = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
         float direction = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;
         poseStack.mulPose(Axis.ZP.rotationDegrees(55.0F * this.candlelight$drink * direction));
         poseStack.mulPose(Axis.XP.rotationDegrees(25.0F * this.candlelight$drink));
      } else if (this.candlelight$dabbing) {
         HumanoidArm side = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
         float direction = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;
         poseStack.mulPose(Axis.YP.rotationDegrees(-25.0F * direction));
         poseStack.mulPose(Axis.ZP.rotationDegrees(25.0F * direction));
      }
   }

   @Inject(
      method = "renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
      at = @At("TAIL")
   )
   private void candlelight$endDab(
      AbstractClientPlayer player,
      float partialTicks,
      float pitch,
      InteractionHand hand,
      float swingProgress,
      ItemStack stack,
      float equippedProgress,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int light,
      CallbackInfo ci
   ) {
      if (this.candlelight$dabbing || this.candlelight$raising) {
         poseStack.popPose();
         this.candlelight$dabbing = false;
         this.candlelight$raising = false;
      }
   }
}
