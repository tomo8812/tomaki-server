package net.satisfy.brewery.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.entity.BeerElementalAttackEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BeerElementalAttackRenderer extends EntityRenderer<BeerElementalAttackEntity> {
   private static final ResourceLocation TEXTURE_LOCATION = Brewery.identifier("textures/particle/beer_elemental_attack.png");
   private static final float SCALE = 0.4F;
   private static final Vec2[] UVS = new Vec2[]{new Vec2(1.0F, 1.0F), new Vec2(0.0F, 1.0F), new Vec2(0.0F, 0.0F), new Vec2(1.0F, 0.0F)};
   private static final Vector3f[] VERTS = new Vector3f[]{
      new Vector3f(0.5F, -0.5F, 0.0F), new Vector3f(-0.5F, -0.5F, 0.0F), new Vector3f(-0.5F, 0.5F, 0.0F), new Vector3f(0.5F, 0.5F, 0.0F)
   };

   public BeerElementalAttackRenderer(Context context) {
      super(context);
   }

   public void render(BeerElementalAttackEntity entity, float f, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
      if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)) {
         poseStack.pushPose();
         poseStack.scale(0.4F, 0.4F, 0.4F);
         poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
         VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucentCull(TEXTURE_LOCATION));
         renderQuad(poseStack.last(), vertexConsumer, combinedLight);
         poseStack.popPose();
         super.render(entity, f, partialTick, poseStack, buffer, combinedLight);
      }
   }

   private static void renderQuad(Pose pose, VertexConsumer vertexBuilder, int combinedLight) {
      Matrix4f poseMatrix = pose.pose();

      for (int i = 0; i < 4; i++) {
         Vector3f localPos = VERTS[i];
         Vec2 quadUvs = UVS[i];
         Vector4f pos = new Vector4f(localPos.x(), localPos.y() + 0.5F, localPos.z(), 1.0F);
         pos.mul(poseMatrix);
         vertexBuilder.addVertex(pos.x(), pos.y(), pos.z(), 1, quadUvs.x, quadUvs.y, OverlayTexture.NO_OVERLAY, combinedLight, 1.0F, 1.0F, 1.0F);
      }
   }

   protected int getBlockLightLevel(BeerElementalAttackEntity entity, BlockPos pos) {
      return 15;
   }

   @NotNull
   public ResourceLocation getTextureLocation(BeerElementalAttackEntity entity) {
      return TEXTURE_LOCATION;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   public ResourceLocation getTextureLocation(Entity param1) {
      return this.getTextureLocation((BeerElementalAttackEntity)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(Entity param1, float nullx, float nullxx, PoseStack nullxxx, MultiBufferSource nullxxxx, int nullxxxxx) {
      this.render((BeerElementalAttackEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected int getBlockLightLevel(Entity param1, BlockPos nullx) {
      return this.getBlockLightLevel((BeerElementalAttackEntity)var1, nullx);
   }
}
