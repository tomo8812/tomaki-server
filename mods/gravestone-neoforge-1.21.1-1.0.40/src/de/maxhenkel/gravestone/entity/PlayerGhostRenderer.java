package de.maxhenkel.gravestone.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.gravestone.corelib.client.PlayerSkins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;

public class PlayerGhostRenderer extends LivingEntityRenderer<GhostPlayerEntity, PlayerModel<GhostPlayerEntity>> {
   private PlayerModel<GhostPlayerEntity> playerModel;
   private PlayerModel<GhostPlayerEntity> playerModelSmallArms;

   public PlayerGhostRenderer(Context renderer) {
      super(renderer, null, 0.5F);
      this.playerModel = new PlayerModel(renderer.bakeLayer(ModelLayers.PLAYER), false);
      this.playerModelSmallArms = new PlayerModel(renderer.bakeLayer(ModelLayers.PLAYER_SLIM), true);
      this.model = this.playerModel;
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            new HumanoidModel(renderer.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(renderer.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            Minecraft.getInstance().getModelManager()
         )
      );
      this.addLayer(new CustomHeadLayer(this, renderer.getModelSet(), 1.0F, 1.0F, 1.0F, Minecraft.getInstance().gameRenderer.itemInHandRenderer));
      this.addLayer(new ElytraLayer(this, renderer.getModelSet()));
      this.addLayer(new ItemInHandLayer(this, Minecraft.getInstance().gameRenderer.itemInHandRenderer));
   }

   protected void scale(GhostPlayerEntity ghost, PoseStack matrixStack, float partialTickTime) {
      float scale = 0.9375F;
      matrixStack.scale(scale, scale, scale);
   }

   public ResourceLocation getTextureLocation(GhostPlayerEntity entity) {
      return PlayerSkins.getSkin(entity.getPlayerUUID(), entity.getName().getString());
   }

   public void render(GhostPlayerEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
      matrixStack.pushPose();
      if (PlayerSkins.isSlim(entity.getPlayerUUID())) {
         this.model = this.playerModelSmallArms;
      } else {
         this.model = this.playerModel;
      }

      this.setModelVisibilities(entity);
      super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
      matrixStack.popPose();
   }

   private void setModelVisibilities(GhostPlayerEntity playerEntity) {
      ((PlayerModel)this.model).hat.visible = playerEntity.isWearing(PlayerModelPart.HAT);
      ((PlayerModel)this.model).jacket.visible = playerEntity.isWearing(PlayerModelPart.JACKET);
      ((PlayerModel)this.model).leftPants.visible = playerEntity.isWearing(PlayerModelPart.LEFT_PANTS_LEG);
      ((PlayerModel)this.model).rightPants.visible = playerEntity.isWearing(PlayerModelPart.RIGHT_PANTS_LEG);
      ((PlayerModel)this.model).leftSleeve.visible = playerEntity.isWearing(PlayerModelPart.LEFT_SLEEVE);
      ((PlayerModel)this.model).rightSleeve.visible = playerEntity.isWearing(PlayerModelPart.RIGHT_SLEEVE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected void scale(LivingEntity param1, PoseStack nullx, float nullxx) {
      this.scale((GhostPlayerEntity)var1, nullx, nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(LivingEntity param1, float nullx, float nullxx, PoseStack nullxxx, MultiBufferSource nullxxxx, int nullxxxxx) {
      this.render((GhostPlayerEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ResourceLocation getTextureLocation(Entity param1) {
      return this.getTextureLocation((GhostPlayerEntity)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(Entity param1, float nullx, float nullxx, PoseStack nullxxx, MultiBufferSource nullxxxx, int nullxxxxx) {
      this.render((GhostPlayerEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
