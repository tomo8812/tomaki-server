package net.satisfy.brewery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.block.BrewTimerBlock;
import net.satisfy.brewery.core.block.entity.BrewTimerBlockEntity;
import net.satisfy.brewery.core.block.entity.BrewstationBlockEntity;
import net.satisfy.brewery.core.block.property.BrewMaterial;
import net.satisfy.brewery.core.registry.BlockStateRegistry;
import net.satisfy.brewery.platform.PlatformHelper;

public class BrewTimerRenderer implements BlockEntityRenderer<BrewTimerBlockEntity> {
   private static final float PX = 0.0625F;
   private static final float PRESS_TICKS = 6.0F;

   public BrewTimerRenderer(Context context) {
   }

   public void render(BrewTimerBlockEntity timer, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
      if (timer.getLevel() != null) {
         BlockState state = timer.getBlockState();
         float time = PlatformHelper.animationsEnabled() ? (float)timer.getLevel().getGameTime() + partialTicks : 0.0F;
         BrewMaterial material = (BrewMaterial)state.getValue(BlockStateRegistry.MATERIAL);
         VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
         poseStack.pushPose();
         poseStack.translate(0.5F, 0.0F, 0.5F);
         poseStack.mulPose(Axis.YP.rotationDegrees(-yRotation((Direction)state.getValue(BrewTimerBlock.FACING))));
         poseStack.translate(-0.5F, 0.0F, -0.5F);
         this.renderButton(poseStack, consumer, sprite(material, "button"), time, timer.getPressTime(), light, overlay);
         this.renderNeedle(poseStack, consumer, sprite(material, "tachometer"), needleTarget(timer, state, time), timer, light, overlay);
         poseStack.popPose();
      }
   }

   private static float needleTarget(BrewTimerBlockEntity timer, BlockState state, float time) {
      if ((Boolean)state.getValue(BrewTimerBlock.TIME)) {
         return 55.0F + Mth.sin(time * 1.9F) * 20.0F;
      }

      BrewstationBlockEntity station = timer.findStation();
      float progress = station == null ? -1.0F : station.getBrewProgress();
      return progress < 0.0F ? -60.0F : -60.0F + progress * 120.0F + Mth.sin(time * 0.8F) * 1.5F;
   }

   private void renderNeedle(
      PoseStack poseStack, VertexConsumer consumer, TextureAtlasSprite sprite, float target, BrewTimerBlockEntity timer, int light, int overlay
   ) {
      float angle = timer.updateNeedle(target, 0.12F);
      float u = sprite.getU(0.15625F);
      float v = sprite.getV(0.15625F);
      poseStack.pushPose();
      poseStack.translate(0.3125F, 0.675F, 1.00125F);
      poseStack.mulPose(Axis.ZP.rotationDegrees(-angle));
      float half = 0.03125F;
      float length = 0.125F;
      quad(poseStack, consumer, -half, 0.0F, half, length, 0.0F, u, v, u, v, 0.0F, 0.0F, 1.0F, -5236712, light, overlay);
      poseStack.popPose();
   }

   private void renderButton(PoseStack poseStack, VertexConsumer consumer, TextureAtlasSprite sprite, float time, long pressTime, int light, int overlay) {
      float progress = (time - (float)pressTime) / 6.0F;
      float depth = progress >= 0.0F && progress < 1.0F ? Mth.sin(progress * (float) Math.PI) * 0.8F * 0.0625F : 0.0F;
      float x0 = 0.6875F;
      float x1 = 0.8125F;
      float y0 = 0.6875F;
      float y1 = 0.8125F;
      float z0 = 0.9375F - depth;
      float z1 = 1.0F - depth;
      float u0 = sprite.getU(0.0F);
      float u1 = sprite.getU(0.125F);
      float v0 = sprite.getV(0.0F);
      float v1 = sprite.getV(0.125F);
      float eu0 = sprite.getU(0.1875F);
      float eu1 = sprite.getU(0.25F);
      float wu0 = sprite.getU(0.0F);
      float wu1 = sprite.getU(0.0625F);
      float cu0 = sprite.getU(0.1875F);
      float cu1 = sprite.getU(0.3125F);
      float cv1 = sprite.getV(0.0625F);
      quad(poseStack, consumer, x0, y0, x1, y1, z1, u0, v1, u1, v0, 0.0F, 0.0F, 1.0F, -1, light, overlay);
      side(poseStack, consumer, x1, y0, y1, z0, z1, eu0, v1, eu1, v0, 1, light, overlay);
      side(poseStack, consumer, x0, y0, y1, z0, z1, wu0, v1, wu1, v0, -1, light, overlay);
      cap(poseStack, consumer, x0, x1, y1, z0, z1, cu1, cv1, cu0, v0, 1, light, overlay);
      cap(poseStack, consumer, x0, x1, y0, z0, z1, cu0, v0, cu1, cv1, -1, light, overlay);
   }

   private static void quad(
      PoseStack poseStack,
      VertexConsumer consumer,
      float x0,
      float y0,
      float x1,
      float y1,
      float z,
      float u0,
      float v0,
      float u1,
      float v1,
      float nx,
      float ny,
      float nz,
      int color,
      int light,
      int overlay
   ) {
      Pose pose = poseStack.last();
      vertex(consumer, pose, x0, y0, z, u0, v0, nx, ny, nz, color, light, overlay);
      vertex(consumer, pose, x1, y0, z, u1, v0, nx, ny, nz, color, light, overlay);
      vertex(consumer, pose, x1, y1, z, u1, v1, nx, ny, nz, color, light, overlay);
      vertex(consumer, pose, x0, y1, z, u0, v1, nx, ny, nz, color, light, overlay);
   }

   private static void side(
      PoseStack poseStack,
      VertexConsumer consumer,
      float x,
      float y0,
      float y1,
      float z0,
      float z1,
      float u0,
      float v0,
      float u1,
      float v1,
      int dir,
      int light,
      int overlay
   ) {
      Pose pose = poseStack.last();
      float za = dir > 0 ? z1 : z0;
      float zb = dir > 0 ? z0 : z1;
      vertex(consumer, pose, x, y0, za, u0, v0, dir, 0.0F, 0.0F, -1, light, overlay);
      vertex(consumer, pose, x, y0, zb, u1, v0, dir, 0.0F, 0.0F, -1, light, overlay);
      vertex(consumer, pose, x, y1, zb, u1, v1, dir, 0.0F, 0.0F, -1, light, overlay);
      vertex(consumer, pose, x, y1, za, u0, v1, dir, 0.0F, 0.0F, -1, light, overlay);
   }

   private static void cap(
      PoseStack poseStack,
      VertexConsumer consumer,
      float x0,
      float x1,
      float y,
      float z0,
      float z1,
      float u0,
      float v0,
      float u1,
      float v1,
      int dir,
      int light,
      int overlay
   ) {
      Pose pose = poseStack.last();
      float za = dir > 0 ? z1 : z0;
      float zb = dir > 0 ? z0 : z1;
      vertex(consumer, pose, x0, y, za, u0, v0, 0.0F, dir, 0.0F, -1, light, overlay);
      vertex(consumer, pose, x1, y, za, u1, v0, 0.0F, dir, 0.0F, -1, light, overlay);
      vertex(consumer, pose, x1, y, zb, u1, v1, 0.0F, dir, 0.0F, -1, light, overlay);
      vertex(consumer, pose, x0, y, zb, u0, v1, 0.0F, dir, 0.0F, -1, light, overlay);
   }

   private static void vertex(
      VertexConsumer consumer, Pose pose, float x, float y, float z, float u, float v, float nx, float ny, float nz, int color, int light, int overlay
   ) {
      consumer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
   }

   private static TextureAtlasSprite sprite(BrewMaterial material, String part) {
      String folder = material.getSerializedName();
      String prefix = material == BrewMaterial.WOOD ? "wooden" : folder;
      ResourceLocation id = Brewery.identifier("block/brewing_station/" + folder + "/" + prefix + "_brewing_station_" + part);
      return (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(id);
   }

   private static float yRotation(Direction facing) {
      return switch (facing) {
         case EAST -> 90.0F;
         case SOUTH -> 180.0F;
         case WEST -> 270.0F;
         default -> 0.0F;
      };
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((BrewTimerBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
