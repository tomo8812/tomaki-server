package net.satisfy.herbalbrews.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;
import net.satisfy.herbalbrews.core.util.HerbalBrewsIdentifier;

public class TopHatModel<T extends Entity> extends EntityModel<T> implements HatModel {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(HerbalBrewsIdentifier.identifier("top_hat"), "main");
   private final ModelPart topHat;

   public TopHatModel(ModelPart root) {
      this.topHat = root.getChild("topHat");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition topHat = partdefinition.addOrReplaceChild(
         "topHat",
         CubeListBuilder.create()
            .texOffs(-14, 16)
            .addBox(-7.0F, -6.0F, -7.0F, 14.0F, 0.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-4.0F, -14.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 48, 48);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
      poseStack.pushPose();
      poseStack.scale(1.05F, 1.05F, 1.05F);
      this.topHat.render(poseStack, vertexConsumer, i, j, k);
      poseStack.popPose();
   }

   public void setupAnim(T entity, float f, float g, float h, float i, float j) {
   }

   @Override
   public void copyHead(ModelPart model) {
      this.topHat.copyFrom(model);
   }
}
