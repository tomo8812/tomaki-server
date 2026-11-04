package org.embeddedt.modernfix.common.mixin.bugfix.entity_pose_stack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Pre;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
@ClientOnlyMixin
public class LivingEntityRendererMixin {
   @Redirect(
      method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
      at = @At(value = "INVOKE", target = "Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;", ordinal = 0)
   )
   private Event fireCheckingPoseStack(IEventBus instance, Event event) {
      PoseStack stack = ((RenderLivingEvent)event).getPoseStack();
      int size = ((PoseStackAccessor)stack).getPoseStack().size();
      instance.post(event);
      if (((Pre)event).isCanceled()) {
         while (((PoseStackAccessor)stack).getPoseStack().size() > size) {
            stack.popPose();
         }
      }

      return event;
   }
}
