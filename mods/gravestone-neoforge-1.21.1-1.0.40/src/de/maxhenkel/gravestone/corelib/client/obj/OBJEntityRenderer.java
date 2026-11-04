package de.maxhenkel.gravestone.corelib.client.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class OBJEntityRenderer<T extends Entity> extends EntityRenderer<T> {
   protected OBJEntityRenderer(Context renderManager) {
      super(renderManager);
   }

   public abstract List<OBJModelInstance<T>> getModels(T var1);

   public ResourceLocation getTextureLocation(T entity) {
      return null;
   }

   public void render(T entity, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
      this.renderModels(entity, yaw, partialTicks, matrixStack, buffer, packedLight);
      super.render(entity, yaw, partialTicks, matrixStack, buffer, packedLight);
   }

   protected void renderModels(T entity, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
      List<OBJModelInstance<T>> models = this.getModels(entity);
      matrixStack.pushPose();
      this.setupYaw(entity, yaw, matrixStack);
      this.setupPitch(entity, partialTicks, matrixStack);

      for (OBJModelInstance<T> model : models) {
         matrixStack.pushPose();
         matrixStack.translate(model.getOptions().getOffset().x, model.getOptions().getOffset().y, model.getOptions().getOffset().z);
         if (model.getOptions().getRotation() != null) {
            model.getOptions().getRotation().applyRotation(matrixStack);
         }

         if (model.getOptions().getOnRender() != null) {
            model.getOptions().getOnRender().onRender(entity, matrixStack, partialTicks);
         }

         model.getModel().render(model.getOptions().getTexture(), matrixStack, buffer, packedLight);
         matrixStack.popPose();
      }

      matrixStack.popPose();
   }

   protected void setupYaw(T entity, float yaw, PoseStack matrixStack) {
      matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
   }

   protected void setupPitch(T entity, float partialTicks, PoseStack matrixStack) {
      float pitch = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks;
      matrixStack.mulPose(Axis.XN.rotationDegrees(pitch));
   }
}
