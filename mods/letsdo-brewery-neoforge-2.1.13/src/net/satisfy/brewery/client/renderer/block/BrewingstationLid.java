package net.satisfy.brewery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.block.entity.BrewstationBlockEntity;
import net.satisfy.brewery.core.block.property.BrewMaterial;
import net.satisfy.brewery.core.block.property.Liquid;
import net.satisfy.brewery.core.registry.BlockStateRegistry;
import net.satisfy.brewery.platform.PlatformHelper;

public final class BrewingstationLid {
   public static final float OPEN = 15.0F;
   private static final float BREWING = 55.0F;
   private static final float DRAINED = 35.0F;
   private static final float OVERFLOWING = 87.0F;
   private static final float PX = 0.0625F;

   private BrewingstationLid() {
   }

   public static void render(BrewstationBlockEntity station, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
      BlockState state = station.getBlockState();
      if (station.getLevel() != null && state.hasProperty(BlockStateRegistry.LIQUID) && state.hasProperty(BlockStateRegistry.MATERIAL)) {
         float time = PlatformHelper.animationsEnabled() ? (float)station.getLevel().getGameTime() + partialTicks : 0.0F;
         Liquid liquid = (Liquid)state.getValue(BlockStateRegistry.LIQUID);
         float angle;
         if (liquid == Liquid.OVERFLOWING) {
            angle = station.updateLid(87.0F - Math.abs(Mth.sin(time * 0.9F)) * 9.0F, 0.35F);
         } else if (liquid == Liquid.DRAINED) {
            angle = station.updateLid(35.0F, 0.06F);
         } else if (station.isBrewingClient()) {
            angle = station.updateLid(55.0F + Mth.sin(time * 0.12F) * 1.5F, 0.05F);
         } else {
            angle = station.updateLid(15.0F, 0.05F);
         }

         TextureAtlasSprite sprite = sprite((BrewMaterial)state.getValue(BlockStateRegistry.MATERIAL));
         VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
         poseStack.pushPose();
         poseStack.translate(0.5F, 0.0F, 0.5F);
         poseStack.mulPose(Axis.YP.rotationDegrees(-yRotation((Direction)state.getValue(HorizontalDirectionalBlock.FACING))));
         poseStack.translate(-0.5F, 0.0F, -0.5F);
         poseStack.translate(0.03125F, 1.0F, 0.0F);
         poseStack.mulPose(Axis.ZP.rotationDegrees(-angle));
         poseStack.translate(-0.03125F, -1.0F, 0.0F);
         cuboid(poseStack, consumer, sprite, 0.0F, 16.0F, 1.0F, 1.0F, 30.0F, 16.0F, light, overlay);
         poseStack.popPose();
      }
   }

   private static void cuboid(
      PoseStack poseStack,
      VertexConsumer consumer,
      TextureAtlasSprite sprite,
      float x0,
      float y0,
      float z0,
      float x1,
      float y1,
      float z1,
      int light,
      int overlay
   ) {
      Pose pose = poseStack.last();
      float ax = x0 * 0.0625F;
      float ay = y0 * 0.0625F;
      float az = z0 * 0.0625F;
      float bx = x1 * 0.0625F;
      float by = y1 * 0.0625F;
      float bz = z1 * 0.0625F;
      face(
         pose,
         consumer,
         sprite,
         new float[][]{{ax, by, az}, {ax, ay, az}, {ax, ay, bz}, {ax, by, bz}},
         0.0F,
         2.0F,
         15.0F,
         16.0F,
         180,
         -1.0F,
         0.0F,
         0.0F,
         light,
         overlay
      );
      face(
         pose,
         consumer,
         sprite,
         new float[][]{{bx, by, bz}, {bx, ay, bz}, {bx, ay, az}, {bx, by, az}},
         15.0F,
         2.0F,
         0.0F,
         16.0F,
         180,
         1.0F,
         0.0F,
         0.0F,
         light,
         overlay
      );
      face(
         pose,
         consumer,
         sprite,
         new float[][]{{bx, by, az}, {bx, ay, az}, {ax, ay, az}, {ax, by, az}},
         15.0F,
         2.0F,
         14.0F,
         16.0F,
         180,
         0.0F,
         0.0F,
         -1.0F,
         light,
         overlay
      );
      face(
         pose,
         consumer,
         sprite,
         new float[][]{{ax, by, bz}, {ax, ay, bz}, {bx, ay, bz}, {bx, by, bz}},
         1.0F,
         2.0F,
         0.0F,
         16.0F,
         180,
         0.0F,
         0.0F,
         1.0F,
         light,
         overlay
      );
      face(
         pose,
         consumer,
         sprite,
         new float[][]{{ax, by, az}, {ax, by, bz}, {bx, by, bz}, {bx, by, az}},
         0.0F,
         16.0F,
         15.0F,
         15.0F,
         270,
         0.0F,
         1.0F,
         0.0F,
         light,
         overlay
      );
      face(
         pose,
         consumer,
         sprite,
         new float[][]{{ax, ay, bz}, {ax, ay, az}, {bx, ay, az}, {bx, ay, bz}},
         1.0F,
         2.0F,
         15.0F,
         3.0F,
         270,
         0.0F,
         -1.0F,
         0.0F,
         light,
         overlay
      );
   }

   private static void face(
      Pose pose,
      VertexConsumer consumer,
      TextureAtlasSprite sprite,
      float[][] corners,
      float u0,
      float v0,
      float u1,
      float v1,
      int rotation,
      float nx,
      float ny,
      float nz,
      int light,
      int overlay
   ) {
      float[][] uv = new float[][]{{u0, v0}, {u0, v1}, {u1, v1}, {u1, v0}};
      int shift = rotation / 90;

      for (int i = 0; i < 4; i++) {
         float[] corner = uv[(i + shift) % 4];
         consumer.addVertex(pose, corners[i][0], corners[i][1], corners[i][2])
            .setColor(-1)
            .setUv(sprite.getU(corner[0] / 16.0F), sprite.getV(corner[1] / 16.0F))
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(pose, nx, ny, nz);
      }
   }

   private static TextureAtlasSprite sprite(BrewMaterial material) {
      String folder = material.getSerializedName();
      String prefix = material == BrewMaterial.WOOD ? "wooden" : folder;
      return (TextureAtlasSprite)Minecraft.getInstance()
         .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
         .apply(Brewery.identifier("block/brewing_station/" + folder + "/" + prefix + "_brewing_station_top_1"));
   }

   private static float yRotation(Direction facing) {
      return switch (facing) {
         case EAST -> 90.0F;
         case SOUTH -> 180.0F;
         case WEST -> 270.0F;
         default -> 0.0F;
      };
   }
}
