package de.maxhenkel.gravestone.tileentity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.corelib.client.PlayerSkins;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import java.util.UUID;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GravestoneRenderer implements BlockEntityRenderer<GraveStoneTileEntity> {
   private Context renderer;

   public GravestoneRenderer(Context renderer) {
      this.renderer = renderer;
   }

   public void render(GraveStoneTileEntity grave, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
      Component name = grave.getGraveName();
      if (name != null) {
         Direction direction = (Direction)grave.getBlockState().getValue(GraveStoneBlock.FACING);
         matrixStack.pushPose();
         matrixStack.translate(0.5, 1.0, 0.5);
         matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
         matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F + direction.toYRot()));
         Font font = this.renderer.getFont();
         int textWidth = font.width(name.getString());
         double textScale = Math.min(0.8 / textWidth, 0.02);
         matrixStack.translate(0.0, 0.3, 0.37);
         matrixStack.scale((float)textScale, (float)textScale, (float)textScale);
         float left = -textWidth / 2;
         font.drawInBatch(
            name.getString(), left, 0.0F, Main.CLIENT_CONFIG.graveTextColor, false, matrixStack.last().pose(), buffer, DisplayMode.NORMAL, 0, combinedLight
         );
         matrixStack.popPose();
         BlockState state = grave.getLevel().getBlockState(grave.getBlockPos().below());
         boolean render = state.isRedstoneConductor(grave.getLevel(), grave.getBlockPos());
         UUID playerUUID = grave.getDeath().getPlayerUUID();
         if (playerUUID != null && !playerUUID.equals(GraveUtils.EMPTY_UUID) && (Boolean)Main.CLIENT_CONFIG.renderSkull.get() && render) {
            this.renderSkull(playerUUID, name.getString(), direction, matrixStack, buffer, combinedLight);
         }
      }
   }

   public void renderSkull(UUID uuid, String name, Direction rotation, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight) {
      SkullModel model = new SkullModel(this.renderer.bakeLayer(ModelLayers.PLAYER_HEAD));
      ResourceLocation resourcelocation = PlayerSkins.getSkin(uuid, name);
      matrixStack.pushPose();
      matrixStack.translate(0.5, 0.0, 0.5);
      matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - rotation.toYRot()));
      matrixStack.mulPose(Axis.YP.rotationDegrees(-26.0F));
      matrixStack.translate(0.0, -0.14, 0.18);
      matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
      matrixStack.mulPose(Axis.XP.rotationDegrees(-61.0F));
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      model.renderToBuffer(matrixStack, buffer.getBuffer(model.renderType(resourcelocation)), combinedLight, OverlayTexture.NO_OVERLAY);
      matrixStack.popPose();
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((GraveStoneTileEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
