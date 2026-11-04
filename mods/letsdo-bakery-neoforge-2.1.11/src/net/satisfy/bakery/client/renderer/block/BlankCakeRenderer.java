package net.satisfy.bakery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.bakery.Bakery;
import net.satisfy.bakery.core.block.BlankCakeBlock;
import net.satisfy.bakery.core.block.entity.BlankCakeBlockEntity;
import net.satisfy.bakery.core.recipe.BlankCakeStage;
import net.satisfy.bakery.core.recipe.CakeAnimation;
import net.satisfy.bakery.core.registry.ObjectRegistry;

public class BlankCakeRenderer implements BlockEntityRenderer<BlankCakeBlockEntity> {
   private static final float PIXEL = 0.0625F;
   private static final float SHELL_FLAT = 0.6F;
   private static final float SHEET_MIN = 3.0F;
   private static final float SHEET_MAX = 13.0F;
   private static final float FILLING_MIN = 4.0F;
   private static final float FILLING_MAX = 12.0F;
   private static final int ROLL_SLICES = 5;
   private static final float SHELL_GROW_TICKS = 10.0F;
   private static final int VERTEX_STRIDE = 8;

   public void render(BlankCakeBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
      BlockState state = entity.getBlockState();
      if (entity.getLevel() != null) {
         if (!(Boolean)state.getValue(BlankCakeBlock.ANIMATING)) {
            renderResting(entity, state, partialTick, poseStack, buffers, light, overlay);
         } else {
            CakeAnimation animation = entity.getAnimation();
            int duration = entity.getAnimationDuration();
            float progress = entity.getProgress(entity.getLevel().getGameTime(), partialTick);
            BlockState shown = (BlockState)state.setValue(BlankCakeBlock.ANIMATING, false);
            switch (animation) {
               case SPLIT:
                  this.renderSplit(shown, progress, poseStack, buffers, light);
                  break;
               case ROLL_UP:
                  this.renderRollUp(entity, partialTick, poseStack, buffers, light);
                  break;
               case SLICE:
                  this.renderSlices(progress, poseStack, buffers, light);
                  break;
               case FLATTEN:
               case KNEAD:
                  renderPress(entity, shown, progress, partialTick, poseStack, buffers, light, overlay);
                  break;
               case SPREAD:
                  renderSpread(entity, shown, progress, poseStack, buffers, light, overlay);
                  break;
               default:
                  renderShrink(shown, progress, poseStack, buffers, light, overlay);
            }
         }
      }
   }

   private static void renderShrink(BlockState state, float progress, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
      float bounce = Mth.sin(Math.min(progress / 0.3F, 1.0F) * (float) Math.PI) * 0.12F;
      float horizontal = Math.max(0.0F, CakeAnimation.shrinkScale(progress) * (1.0F + bounce));
      float vertical = CakeAnimation.SHRINK.verticalScale(progress, CakeAnimation.SHRINK.duration());
      poseStack.pushPose();
      poseStack.translate(0.5, 0.0, 0.5);
      poseStack.mulPose(Axis.YP.rotationDegrees(progress * progress * 90.0F));
      poseStack.scale(horizontal, vertical, horizontal);
      poseStack.translate(-0.5, 0.0, -0.5);
      Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, buffers, light, overlay);
      poseStack.popPose();
   }

   private static void renderPress(
      BlankCakeBlockEntity entity, BlockState state, float progress, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay
   ) {
      float vertical = entity.heightScale(entity.getLevel().getGameTime(), partialTick);
      float horizontal = 1.0F + Mth.sin(progress * (float) Math.PI) * 0.06F;
      poseStack.pushPose();
      poseStack.translate(0.5, 0.0, 0.5);
      poseStack.scale(horizontal, vertical, horizontal);
      poseStack.translate(-0.5, 0.0, -0.5);
      Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, buffers, light, overlay);
      poseStack.popPose();
   }

   private static void renderSpread(
      BlankCakeBlockEntity entity, BlockState state, float progress, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay
   ) {
      Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, buffers, light, overlay);
      BlockState result = entity.getPendingState();
      if (result != null) {
         boolean pieces = state.getValue(BlankCakeBlock.STAGE) != BlankCakeStage.CAKE;
         BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(result);
         RandomSource random = RandomSource.create(42L);
         List<BakedQuad> quads = new ArrayList<>(model.getQuads(result, null, random));

         for (Direction direction : Direction.values()) {
            quads.addAll(model.getQuads(result, direction, random));
         }

         float top = 0.0F;

         for (BakedQuad quad : quads) {
            int[] data = quad.getVertices();

            for (int vertex = 0; vertex < 4; vertex++) {
               top = Math.max(top, Float.intBitsToFloat(data[vertex * 8 + 1]));
            }
         }

         float maxReach = (pieces ? 3.6F : 9.9F) + top * 16.0F;
         float radius = CakeAnimation.easeOutCubic(Math.min(1.0F, progress / 0.8F)) * maxReach;
         VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());
         Pose pose = poseStack.last();

         for (BakedQuad quad : quads) {
            drawSpreadQuad(pose, consumer, quad, radius, top, pieces, light);
         }
      }
   }

   private static void drawSpreadQuad(Pose pose, VertexConsumer consumer, BakedQuad quad, float radius, float top, boolean pieces, int light) {
      int[] data = quad.getVertices();
      float[][] position = new float[4][3];
      float[][] uv = new float[4][2];

      for (int vertex = 0; vertex < 4; vertex++) {
         int offset = vertex * 8;
         position[vertex][0] = Float.intBitsToFloat(data[offset]);
         position[vertex][1] = Float.intBitsToFloat(data[offset + 1]);
         position[vertex][2] = Float.intBitsToFloat(data[offset + 2]);
         uv[vertex][0] = Float.intBitsToFloat(data[offset + 4]);
         uv[vertex][1] = Float.intBitsToFloat(data[offset + 5]);
      }

      Direction direction = quad.getDirection();
      float nx = direction.getStepX();
      float ny = direction.getStepY();
      float nz = direction.getStepZ();

      float shade = switch (direction) {
         case UP -> 1.0F;
         case DOWN -> 0.5F;
         case NORTH, SOUTH -> 0.8F;
         default -> 0.6F;
      };
      int columns = Math.max(1, Math.round(distance(position[0], position[3]) * 16.0F));
      int rows = Math.max(1, Math.round(distance(position[0], position[1]) * 16.0F));

      for (int column = 0; column < columns; column++) {
         for (int row = 0; row < rows; row++) {
            float s0 = (float)column / columns;
            float s1 = (float)(column + 1) / columns;
            float t0 = (float)row / rows;
            float t1 = (float)(row + 1) / rows;
            float[] center = lerpQuad(position, (s0 + s1) / 2.0F, (t0 + t1) / 2.0F);
            float centerX = pieces ? (center[0] < 0.5F ? 4.5F : 11.5F) : 8.0F;
            float centerZ = pieces ? (center[2] < 0.5F ? 4.5F : 11.5F) : 8.0F;
            float reach = (float)Math.hypot(center[0] * 16.0F - centerX, center[2] * 16.0F - centerZ) + (top - center[1]) * 16.0F;
            if (!(reach > radius)) {
               float[][] corners = new float[][]{{s0, t0}, {s0, t1}, {s1, t1}, {s1, t0}};

               for (float[] corner : corners) {
                  float[] point = lerpQuad(position, corner[0], corner[1]);
                  float[] texture = lerpQuad(uv, corner[0], corner[1]);
                  consumer.addVertex(pose, point[0] + nx * 0.0015F, point[1] + ny * 0.0015F, point[2] + nz * 0.0015F)
                     .setColor(shade, shade, shade, 1.0F)
                     .setUv(texture[0], texture[1])
                     .setLight(light)
                     .setNormal(pose, nx, ny, nz);
               }
            }
         }
      }
   }

   private static float[] lerpQuad(float[][] corners, float s, float t) {
      float[] result = new float[corners[0].length];

      for (int axis = 0; axis < result.length; axis++) {
         float near = Mth.lerp(s, corners[0][axis], corners[3][axis]);
         float far = Mth.lerp(s, corners[1][axis], corners[2][axis]);
         result[axis] = Mth.lerp(t, near, far);
      }

      return result;
   }

   private static float distance(float[] a, float[] b) {
      return (float)Math.sqrt((a[0] - b[0]) * (a[0] - b[0]) + (a[1] - b[1]) * (a[1] - b[1]) + (a[2] - b[2]) * (a[2] - b[2]));
   }

   private void renderSplit(BlockState state, float progress, PoseStack poseStack, MultiBufferSource buffers, int light) {
      float height = state.getValue(BlankCakeBlock.STAGE) == BlankCakeStage.DOUGH ? 6.0F : 1.0F;
      float spread = CakeAnimation.easeOutBack(progress);
      float hop = Mth.sin(progress * (float) Math.PI) * 1.5F;
      TextureAtlasSprite top = sprite("block/blank_cake_top");
      TextureAtlasSprite side = sprite("block/blank_cake_side");
      VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());

      for (int quarter = 0; quarter < 4; quarter++) {
         poseStack.pushPose();
         poseStack.translate(0.5, 0.0, 0.5);
         poseStack.mulPose(Axis.YP.rotationDegrees(quarter * 90.0F));
         poseStack.translate(-0.5, 0.0, -0.5);
         float x0 = 3.0F - spread;
         float z0 = 3.0F - spread;
         drawBox(poseStack.last(), consumer, top, side, x0, hop, z0, x0 + 5.0F, hop + height, z0 + 5.0F, light);
         poseStack.popPose();
      }
   }

   private static void renderResting(
      BlankCakeBlockEntity entity, BlockState state, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay
   ) {
      BlankCakeStage stage = (BlankCakeStage)state.getValue(BlankCakeBlock.STAGE);
      if (stage == BlankCakeStage.SHELLS) {
         float grow = Mth.clamp(((float)entity.getLevel().getGameTime() + partialTick - (float)entity.getFillTime()) / 10.0F, 0.0F, 1.0F);

         for (int slot = 0; slot < 4; slot++) {
            ItemStack shell = (ItemStack)entity.getShells().get(slot);
            if (!shell.isEmpty()) {
               float size = shell.is((Item)ObjectRegistry.CORNET_SHELL.get()) ? 0.6F : Mth.lerp(CakeAnimation.easeOutBack(grow), 0.6F, 1.0F);
               poseStack.pushPose();
               poseStack.translate(0.5, 0.01 + slot * 0.004, 0.5);
               poseStack.mulPose(Axis.YP.rotationDegrees(slot * 45.0F + 15.0F));
               poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
               poseStack.scale(0.5F * size, 0.5F * size, 0.5F);
               Minecraft.getInstance()
                  .getItemRenderer()
                  .renderStatic(shell, ItemDisplayContext.FIXED, light, overlay, poseStack, buffers, entity.getLevel(), slot);
               poseStack.popPose();
            }
         }
      } else if (stage == BlankCakeStage.SHEET && entity.getFilling() != null) {
         drawFilling(poseStack.last(), buffers, 4.0F, 12.0F, 1.02F, entity.getFilling().color(), light);
      }
   }

   private void renderRollUp(BlankCakeBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
      float rolled = entity.pressProgress(entity.getLevel().getGameTime(), partialTick);
      float edge = 3.0F + Math.round(10.0F * rolled);
      float size = 1.0F + Math.round(2.0F * rolled);
      TextureAtlasSprite top = sprite("block/blank_cake_top");
      TextureAtlasSprite side = sprite("block/blank_cake_side");
      VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());
      Pose pose = poseStack.last();
      if (edge < 13.0F) {
         drawBox(pose, consumer, top, side, 3.0F, 0.0F, edge, 13.0F, 1.0F, 13.0F, light);
         if (entity.getFilling() != null && edge < 12.0F) {
            drawFilling(pose, buffers, Math.max(edge, 4.0F), 12.0F, 1.02F, entity.getFilling().color(), light);
         }
      }

      drawBox(pose, consumer, top, side, 3.0F, 0.0F, Math.max(3.0F, edge - size), 13.0F, size, edge, light);
   }

   private void renderSlices(float progress, PoseStack poseStack, MultiBufferSource buffers, int light) {
      float spread = CakeAnimation.easeOutBack(progress);
      float hop = Math.round(Mth.sin(progress * (float) Math.PI) * 2.0F);
      TextureAtlasSprite top = sprite("block/blank_cake_top");
      TextureAtlasSprite side = sprite("block/blank_cake_side");
      VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());

      for (int slice = 0; slice < 5; slice++) {
         float x0 = 3.0F + slice * 2.0F + Math.round((slice - 2) * spread);
         drawBox(poseStack.last(), consumer, top, side, x0, hop, 10.0F, x0 + 2.0F, hop + 3.0F, 13.0F, light);
      }
   }

   private static void drawFilling(Pose pose, MultiBufferSource buffers, float z0, float z1, float y, int color, int light) {
      TextureAtlasSprite sprite = sprite("block/jam_roll_filling");
      VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());
      float r = (color >> 16 & 0xFF) / 255.0F;
      float g = (color >> 8 & 0xFF) / 255.0F;
      float b = (color & 0xFF) / 255.0F;
      float size = 8.0F;
      float[][] corners = new float[][]{{4.0F, z0}, {4.0F, z1}, {12.0F, z1}, {12.0F, z0}};

      for (float[] corner : corners) {
         consumer.addVertex(pose, corner[0] * 0.0625F, y * 0.0625F, corner[1] * 0.0625F)
            .setColor(r, g, b, 1.0F)
            .setUv(sprite.getU((corner[0] - 4.0F) / size), sprite.getV((corner[1] - 4.0F) / size))
            .setLight(light)
            .setNormal(pose, 0.0F, 1.0F, 0.0F);
      }
   }

   private static TextureAtlasSprite sprite(String path) {
      return (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(Bakery.identifier(path));
   }

   private static void drawBox(
      Pose pose,
      VertexConsumer consumer,
      TextureAtlasSprite top,
      TextureAtlasSprite side,
      float x0,
      float y0,
      float z0,
      float x1,
      float y1,
      float z1,
      int light
   ) {
      float sv0 = Math.max(0.0F, 16.0F - (y1 - y0));
      float sv1 = 16.0F;
      float u0 = wrap(x0);
      float u1 = u0 + (x1 - x0);
      float w0 = wrap(z0);
      float w1 = w0 + (z1 - z0);
      x0 *= 0.0625F;
      y0 *= 0.0625F;
      z0 *= 0.0625F;
      x1 *= 0.0625F;
      y1 *= 0.0625F;
      z1 *= 0.0625F;
      quad(pose, consumer, top, light, 0.0F, 1.0F, 0.0F, u0, w0, u1, w1, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0);
      quad(pose, consumer, top, light, 0.0F, -1.0F, 0.0F, u0, w0, u1, w1, x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1);
      quad(pose, consumer, side, light, 0.0F, 0.0F, -1.0F, u0, sv0, u1, sv1, x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0);
      quad(pose, consumer, side, light, 0.0F, 0.0F, 1.0F, u0, sv0, u1, sv1, x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1);
      quad(pose, consumer, side, light, -1.0F, 0.0F, 0.0F, w0, sv0, w1, sv1, x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1);
      quad(pose, consumer, side, light, 1.0F, 0.0F, 0.0F, w0, sv0, w1, sv1, x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0);
   }

   private static float wrap(float pixel) {
      return Math.max(0.0F, Math.min(pixel, 16.0F));
   }

   private static void quad(
      Pose pose,
      VertexConsumer consumer,
      TextureAtlasSprite sprite,
      int light,
      float nx,
      float ny,
      float nz,
      float u0,
      float v0,
      float u1,
      float v1,
      float ax,
      float ay,
      float az,
      float bx,
      float by,
      float bz,
      float cx,
      float cy,
      float cz,
      float dx,
      float dy,
      float dz
   ) {
      vertex(pose, consumer, sprite, light, nx, ny, nz, ax, ay, az, u0, v0);
      vertex(pose, consumer, sprite, light, nx, ny, nz, bx, by, bz, u0, v1);
      vertex(pose, consumer, sprite, light, nx, ny, nz, cx, cy, cz, u1, v1);
      vertex(pose, consumer, sprite, light, nx, ny, nz, dx, dy, dz, u1, v0);
   }

   private static void vertex(
      Pose pose, VertexConsumer consumer, TextureAtlasSprite sprite, int light, float nx, float ny, float nz, float x, float y, float z, float u, float v
   ) {
      consumer.addVertex(pose, x, y, z)
         .setColor(1.0F, 1.0F, 1.0F, 1.0F)
         .setUv(sprite.getU(u / 16.0F), sprite.getV(v / 16.0F))
         .setLight(light)
         .setNormal(pose, nx, ny, nz);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((BlankCakeBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
