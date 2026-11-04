package net.satisfy.candlelight.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class TypewriterModel<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("candlelight", "typewriter_iron"), "main"
   );
   private final ModelPart typewriter;
   private final ModelPart keyboard;
   private final ModelPart space;
   private final ModelPart enter;

   public TypewriterModel(ModelPart root) {
      this.typewriter = root.getChild("typewriter");
      this.keyboard = this.typewriter.getChild("keyboard");
      this.space = this.keyboard.getChild("space");
      this.enter = this.keyboard.getChild("enter");
      ModelPart button_1 = this.keyboard.getChild("button_1");
      ModelPart button_2 = this.keyboard.getChild("button_2");
      ModelPart button_3 = this.keyboard.getChild("button_3");
      ModelPart button_4 = this.keyboard.getChild("button_4");
      ModelPart button_5 = this.keyboard.getChild("button_5");
      ModelPart button_6 = this.keyboard.getChild("button_6");
      ModelPart button_7 = this.keyboard.getChild("button_7");
      ModelPart body = this.typewriter.getChild("body");
      ModelPart roller = this.typewriter.getChild("roller");
      ModelPart paper_written = this.typewriter.getChild("paper_written");
      ModelPart paper = this.typewriter.getChild("paper");
   }

   public static LayerDefinition getTexturedModelData() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition typewriter = partdefinition.addOrReplaceChild("typewriter", CubeListBuilder.create(), PartPose.offset(3.0F, 22.0F, -6.0F));
      PartDefinition keyboard = typewriter.addOrReplaceChild("keyboard", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition space = keyboard.addOrReplaceChild(
         "space",
         CubeListBuilder.create().texOffs(1, 30).addBox(-6.0F, -1.0F, -1.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition enter = keyboard.addOrReplaceChild(
         "enter",
         CubeListBuilder.create().texOffs(8, 38).addBox(-3.0F, -1.0F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(3.0F, 0.0F, 2.0F)
      );
      PartDefinition button_1 = keyboard.addOrReplaceChild(
         "button_1",
         CubeListBuilder.create().texOffs(8, 38).addBox(-9.0F, -1.0F, 1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition button_2 = keyboard.addOrReplaceChild(
         "button_2",
         CubeListBuilder.create().texOffs(0, 38).addBox(0.5F, -2.0F, 3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition button_3 = keyboard.addOrReplaceChild(
         "button_3",
         CubeListBuilder.create().texOffs(0, 38).addBox(-2.5F, -2.0F, 3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition button_4 = keyboard.addOrReplaceChild(
         "button_4",
         CubeListBuilder.create().texOffs(0, 38).addBox(-8.5F, -2.0F, 3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition button_5 = keyboard.addOrReplaceChild(
         "button_5",
         CubeListBuilder.create().texOffs(8, 38).addBox(-6.0F, -1.0F, 1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition button_6 = keyboard.addOrReplaceChild(
         "button_6",
         CubeListBuilder.create().texOffs(8, 38).addBox(-3.0F, -1.0F, 1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition button_7 = keyboard.addOrReplaceChild(
         "button_7",
         CubeListBuilder.create().texOffs(0, 38).addBox(-5.5F, -2.0F, 3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition body = typewriter.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(16, 37)
            .addBox(-11.0F, -9.0F, 10.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-13.0F, -2.0F, -1.0F, 14.0F, 2.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(0, 16)
            .addBox(-13.0F, -7.0F, 5.0F, 14.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 32)
            .addBox(-13.0F, -11.0F, 10.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 32)
            .addBox(-1.0F, -11.0F, 10.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(20, 29)
            .addBox(-11.0F, -10.5F, 12.0F, 10.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(3.0F, 2.0F, 0.0F)
      );
      PartDefinition roller = typewriter.addOrReplaceChild(
         "roller",
         CubeListBuilder.create().texOffs(8, 38).addBox(-4.0F, 6.0F, -3.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-3.0F, -12.0F, 10.0F)
      );
      PartDefinition paper_written = typewriter.addOrReplaceChild("paper_written", CubeListBuilder.create(), PartPose.offset(-3.0F, -12.0F, 6.0F));
      PartDefinition paper_r1 = paper_written.addOrReplaceChild(
         "paper_r1",
         CubeListBuilder.create().texOffs(20, 39).addBox(-4.0F, -2.5F, 0.5F, 8.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 3.5F, 5.0F, -0.3927F, 0.0F, 0.0F)
      );
      PartDefinition paper = typewriter.addOrReplaceChild("paper", CubeListBuilder.create(), PartPose.offset(-3.0F, -12.0F, 6.0F));
      PartDefinition paper_r2 = paper.addOrReplaceChild(
         "paper_r2",
         CubeListBuilder.create().texOffs(20, 31).addBox(-4.0F, -2.5F, 0.5F, 8.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 3.5F, 5.0F, -0.3927F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public List<ModelPart> getKeys() {
      return this.keyboard.getAllParts().filter(p -> p != this.space && p != this.enter).toList();
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
      this.typewriter.render(poseStack, vertexConsumer, i, j, k);
   }
}
