package net.satisfy.herbalbrews.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.herbalbrews.core.blocks.CompletionistBannerBlock;
import net.satisfy.herbalbrews.core.blocks.CompletionistWallBannerBlock;
import net.satisfy.herbalbrews.core.blocks.entity.CompletionistBannerEntity;

public class CompletionistBannerRenderer implements BlockEntityRenderer<CompletionistBannerEntity> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("herbalbrews", "banner"), "main");
   public static final String FLAG = "flag";
   private static final String POLE = "pole";
   private static final String BAR = "bar";
   private final ModelPart flag;
   private final ModelPart pole;
   private final ModelPart bar;

   public CompletionistBannerRenderer(Context context) {
      ModelPart modelPart = context.bakeLayer(LAYER_LOCATION);
      this.flag = modelPart.getChild("flag");
      this.pole = modelPart.getChild("pole");
      this.bar = modelPart.getChild("bar");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshDefinition = new MeshDefinition();
      PartDefinition partDefinition = meshDefinition.getRoot();
      partDefinition.addOrReplaceChild(
         "flag",
         CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, 0.0F, -1.0F, 20.0F, 40.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -44.0F, -1.0F, -0.0349F, 0.0F, 0.0F)
      );
      partDefinition.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(44, 0).addBox(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F), PartPose.ZERO);
      partDefinition.addOrReplaceChild("bar", CubeListBuilder.create().texOffs(0, 42).addBox(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F), PartPose.ZERO);
      return LayerDefinition.create(meshDefinition, 64, 64);
   }

   public static void renderBanner(
      PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, ModelPart modelPart, CompletionistBannerEntity banner
   ) {
      ResourceLocation location = ((CompletionistBannerBlock)banner.getBlockState().getBlock()).getRenderTexture();
      VertexConsumer vc = multiBufferSource.getBuffer(RenderType.entitySolid(location));
      modelPart.render(poseStack, vc, i, j);
   }

   public void render(CompletionistBannerEntity banner, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
      float scale = 0.66F;
      boolean inInventory = banner.getLevel() == null;
      poseStack.pushPose();
      long time;
      if (inInventory) {
         time = 0L;
         poseStack.translate(0.5, 0.5, 0.5);
         this.pole.visible = true;
      } else {
         time = banner.getLevel().getGameTime();
         BlockState blockState = banner.getBlockState();
         if (!(blockState.getBlock() instanceof CompletionistWallBannerBlock)) {
            poseStack.translate(0.5, 0.5, 0.5);
            float rotation = -(Integer)blockState.getValue(CompletionistBannerBlock.ROTATION) * 360 / 16.0F;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            this.pole.visible = true;
         } else {
            poseStack.translate(0.5, -0.16666667F, 0.5);
            float rotation = -((Direction)blockState.getValue(CompletionistWallBannerBlock.FACING)).toYRot() + 180.0F;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            poseStack.translate(0.0, -0.3125, -0.4375);
            this.pole.visible = false;
         }
      }

      poseStack.pushPose();
      poseStack.scale(scale, -scale, -scale);
      VertexConsumer vertexConsumer = ModelBakery.BANNER_BASE.buffer(multiBufferSource, RenderType::entitySolid);
      this.pole.render(poseStack, vertexConsumer, i, j);
      this.bar.render(poseStack, vertexConsumer, i, j);
      BlockPos blockPos = banner.getBlockPos();
      float k = ((float)Math.floorMod(blockPos.getX() * 7L + blockPos.getY() * 9L + blockPos.getZ() * 13L + time, 100L) + f) / 100.0F;
      this.flag.xRot = (-0.0125F + 0.01F * Mth.cos((float) (Math.PI * 2) * k)) * (float) Math.PI;
      this.flag.y = -32.0F;
      renderBanner(poseStack, multiBufferSource, i, j, this.flag, banner);
      poseStack.popPose();
      poseStack.popPose();
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((CompletionistBannerEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
