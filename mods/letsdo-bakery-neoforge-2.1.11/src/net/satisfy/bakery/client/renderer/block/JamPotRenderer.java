package net.satisfy.bakery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.bakery.Bakery;
import net.satisfy.bakery.core.block.SmallCookingPotBlock;
import net.satisfy.bakery.core.block.entity.SmallCookingPotBlockEntity;
import net.satisfy.bakery.platform.PlatformHelper;
import net.satisfy.farm_and_charm.client.model.CraftingBowlModel;
import org.joml.Quaternionf;

public class JamPotRenderer implements BlockEntityRenderer<SmallCookingPotBlockEntity> {
   private static final ResourceLocation WHISK_TEXTURE = ResourceLocation.fromNamespaceAndPath("farm_and_charm", "textures/entity/crafting_bowl.png");
   private static final ResourceLocation JAM_TEXTURE = Bakery.identifier("block/jam_surface");
   private static final float INNER_MIN = 0.3125F;
   private static final float INNER_MAX = 0.6875F;
   private static final float FLOOR = 0.125F;
   private static final float FULL = 0.28125F;
   private static final float DUMP_TICKS = 16.0F;
   private static final float DUMP_TILT = 70.0F;
   private static final float DUMP_LIFT = 0.25F;
   private static final float WOBBLE_ANGLE = 3.0F;
   private static final float WOBBLE_SPEED = 0.9F;
   private static final float SIMMER_ANGLE = 1.5F;
   private static final float SIMMER_SPEED = 0.15F;
   private static final float SIMMER_HOP = 0.006F;
   private static final float WHISK_SWAY = 4.0F;
   private static final float WHISK_TILT = -8.0F;
   private static final float WHISK_DROP = 0.125F;
   private static final float WHISK_PIVOT = 1.25F;
   private final ModelPart whisk;

   public JamPotRenderer(Context context) {
      this.whisk = context.bakeLayer(CraftingBowlModel.LAYER_LOCATION).getChild("swing");
   }

   public void render(SmallCookingPotBlockEntity pot, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
      Level level = pot.getLevel();
      if (level != null) {
         float time = (float)level.getGameTime() + partialTick;
         BlockState state = pot.getBlockState();
         BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
         List<BakedQuad> support = new ArrayList<>();
         List<BakedQuad> body = new ArrayList<>();
         splitQuads(model, state, support, body);
         VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());
         drawQuads(poseStack, consumer, support, light, overlay);
         poseStack.pushPose();
         float whiskAngle = pot.getWhiskAngle(partialTick);
         move(pot, time, whiskAngle, poseStack);
         drawQuads(poseStack, consumer, body, light, overlay);
         if (pot.isBurnt()) {
            drawSurface(poseStack, buffers, light, 0.28125F, pot.getDisplayColor());
         } else if (!pot.isCooking() && !pot.isBottling()) {
            if (!pot.getIngredients().isEmpty()) {
               drawFruit(pot, poseStack, buffers, light, overlay, level, 0.125F, 1.0F, 0.0F);
            }
         } else {
            float progress = pot.getCookingTime() <= 0 ? 1.0F : Mth.clamp((float)pot.getTimer() / pot.getCookingTime(), 0.0F, 1.0F);
            float surface = pot.isBottling() ? Mth.lerp(Math.min(1.0F, pot.getJarsLeft() / 3.0F), 0.15625F, 0.28125F) : 0.28125F;
            drawSurface(poseStack, buffers, light, surface, pot.getDisplayColor());
            if (pot.isCooking() && progress < 0.6F) {
               drawFruit(pot, poseStack, buffers, light, overlay, level, surface, 1.0F - progress / 0.6F, time);
            }
         }

         this.drawWhisk(poseStack, buffers, light, overlay, whiskAngle, (Direction)pot.getBlockState().getValue(SmallCookingPotBlock.FACING));
         poseStack.popPose();
      }
   }

   private static void splitQuads(BakedModel model, BlockState state, List<BakedQuad> support, List<BakedQuad> body) {
      RandomSource random = RandomSource.create(42L);
      List<Direction> sides = new ArrayList<>(List.of(Direction.values()));
      sides.add(null);

      for (Direction side : sides) {
         for (BakedQuad quad : model.getQuads(state, side, random)) {
            (isSupport(quad) ? support : body).add(quad);
         }
      }
   }

   private static boolean isSupport(BakedQuad quad) {
      int[] vertices = quad.getVertices();
      int stride = vertices.length / 4;

      for (int i = 0; i < 4; i++) {
         if (Float.intBitsToFloat(vertices[i * stride + 1]) > 1.0E-4F) {
            return false;
         }
      }

      return true;
   }

   private static void drawQuads(PoseStack poseStack, VertexConsumer consumer, List<BakedQuad> quads, int light, int overlay) {
      for (BakedQuad quad : quads) {
         consumer.putBulkData(poseStack.last(), quad, 1.0F, 1.0F, 1.0F, 1.0F, light, overlay);
      }
   }

   private static void move(SmallCookingPotBlockEntity pot, float time, float whiskAngle, PoseStack poseStack) {
      if (PlatformHelper.animationsEnabled()) {
         poseStack.translate(0.5F, 0.0F, 0.5F);
         float sinceDump = time - (float)pot.getLastDump();
         if (sinceDump >= 0.0F && sinceDump < 16.0F) {
            float tilt = Mth.sin((float) Math.PI * sinceDump / 16.0F);
            Direction side = ((Direction)pot.getBlockState().getValue(SmallCookingPotBlock.FACING)).getClockWise();
            poseStack.translate(0.0F, tilt * 0.25F, 0.0F);
            poseStack.mulPose(new Quaternionf().rotateAxis(tilt * 70.0F * (float) (Math.PI / 180.0), side.getStepZ(), 0.0F, -side.getStepX()));
         } else if ((Boolean)pot.getBlockState().getValue(SmallCookingPotBlock.LIT)) {
            if (pot.isStirDue()) {
               poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(time * 0.9F) * 3.0F));
            } else if (pot.isCooking()) {
               poseStack.translate(0.0F, Math.max(0.0F, Mth.sin(time * 0.15F * 2.0F)) * 0.006F, 0.0F);
               poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(time * 0.15F) * 1.5F));
            }
         }

         float sway = pot.getWhiskSpeed() / 1.2F * 4.0F;
         if (sway > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.cos(whiskAngle) * sway));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(whiskAngle) * sway));
         }

         poseStack.translate(-0.5F, 0.0F, -0.5F);
      }
   }

   private static void drawSurface(PoseStack poseStack, MultiBufferSource buffers, int light, float y, int color) {
      TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(JAM_TEXTURE);
      VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());
      Pose pose = poseStack.last();
      float r = (color >> 16 & 0xFF) / 255.0F;
      float g = (color >> 8 & 0xFF) / 255.0F;
      float b = (color & 0xFF) / 255.0F;
      float u0 = sprite.getU(0.3125F);
      float u1 = sprite.getU(0.6875F);
      float v0 = sprite.getV(0.3125F);
      float v1 = sprite.getV(0.6875F);
      vertex(pose, consumer, 0.3125F, y, 0.3125F, u0, v0, r, g, b, light);
      vertex(pose, consumer, 0.3125F, y, 0.6875F, u0, v1, r, g, b, light);
      vertex(pose, consumer, 0.6875F, y, 0.6875F, u1, v1, r, g, b, light);
      vertex(pose, consumer, 0.6875F, y, 0.3125F, u1, v0, r, g, b, light);
   }

   private static void vertex(Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, float r, float g, float b, int light) {
      consumer.addVertex(pose, x, y, z).setColor(r, g, b, 1.0F).setUv(u, v).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
   }

   private static void drawFruit(
      SmallCookingPotBlockEntity pot, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, Level level, float y, float size, float time
   ) {
      List<ItemStack> items = pot.getIngredients();

      for (int index = 0; index < items.size(); index++) {
         float angle = index * (360.0F / items.size()) + time * 0.6F;
         poseStack.pushPose();
         poseStack.translate(0.5, y + 0.01, 0.5);
         poseStack.mulPose(Axis.YP.rotationDegrees(angle));
         poseStack.translate(0.09, 0.0, 0.0);
         poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
         float scale = 0.22F * Math.max(0.2F, size);
         poseStack.scale(scale, scale, scale);
         Minecraft.getInstance().getItemRenderer().renderStatic(items.get(index), ItemDisplayContext.FIXED, light, overlay, poseStack, buffers, level, index);
         poseStack.popPose();
      }
   }

   private void drawWhisk(PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, float angle, Direction facing) {
      poseStack.pushPose();
      poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
      poseStack.translate(0.5F, -1.375F, -0.5F);
      poseStack.mulPose(Axis.YP.rotation(angle));
      poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
      poseStack.translate(0.0F, 1.25F, 0.0F);
      poseStack.mulPose(Axis.ZP.rotationDegrees(-8.0F));
      poseStack.translate(0.0F, -1.25F, 0.0F);
      this.whisk.render(poseStack, buffers.getBuffer(RenderType.entityCutoutNoCull(WHISK_TEXTURE)), light, overlay);
      poseStack.popPose();
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((SmallCookingPotBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
