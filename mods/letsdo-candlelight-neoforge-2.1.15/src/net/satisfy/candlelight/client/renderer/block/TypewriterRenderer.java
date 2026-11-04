package net.satisfy.candlelight.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.candlelight.Candlelight;
import net.satisfy.candlelight.client.model.TypewriterModel;
import net.satisfy.candlelight.core.block.TypewriterBlock;
import net.satisfy.candlelight.core.block.entity.TypewriterEntity;
import net.satisfy.candlelight.core.registry.ObjectRegistry;

public class TypewriterRenderer implements BlockEntityRenderer<TypewriterEntity> {
   private static final ResourceLocation IRON_TEXTURE = Candlelight.identifier("textures/entity/typewriter_iron.png");
   private static final ResourceLocation GOLD_TEXTURE = Candlelight.identifier("textures/entity/typewriter_gold.png");
   private final ModelPart typewriter;
   private final ModelPart space;
   private final ModelPart enter;
   private final ModelPart roller;
   private final ModelPart paper;
   private final ModelPart paperWritten;
   private final List<ModelPart> keyParts;

   public TypewriterRenderer(Context context) {
      ModelPart root = context.bakeLayer(TypewriterModel.LAYER_LOCATION);
      this.typewriter = root.getChild("typewriter");
      ModelPart keyboard = this.typewriter.getChild("keyboard");

      ModelPart sp;
      try {
         sp = keyboard.getChild("space");
      } catch (Exception e) {
         sp = keyboard.getChild("spacebar");
      }

      this.space = sp;

      ModelPart en;
      try {
         en = keyboard.getChild("enter");
      } catch (Exception e) {
         en = keyboard.getChild("return");
      }

      this.enter = en;

      ModelPart rl;
      try {
         rl = this.typewriter.getChild("roller");
      } catch (Exception e) {
         rl = this.typewriter.getChild("carriage");
      }

      this.roller = rl;

      ModelPart pp;
      try {
         pp = this.typewriter.getChild("paper");
      } catch (Exception e) {
         pp = this.typewriter.getChild("sheet");
      }

      this.paper = pp;

      ModelPart pw;
      try {
         pw = this.typewriter.getChild("paper_written");
      } catch (Exception e) {
         pw = this.typewriter.getChild("sheet_written");
      }

      this.paperWritten = pw;
      List<ModelPart> keys = new ArrayList<>();

      for (int i = 1; i <= 7; i++) {
         try {
            keys.add(keyboard.getChild("button_" + i));
         } catch (Exception var13) {
         }
      }

      if (keys.isEmpty()) {
         for (int i = 0; i < 8; i++) {
            try {
               keys.add(keyboard.getChild("key" + i));
            } catch (Exception var12) {
            }
         }
      }

      this.keyParts = keys;
   }

   public void render(TypewriterEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
      Level level = be.getLevel();
      if (level != null) {
         BlockState state = level.getBlockState(be.getBlockPos());
         if (state.getBlock() instanceof TypewriterBlock) {
            poseStack.pushPose();
            Direction facing = (Direction)state.getValue(TypewriterBlock.FACING);

            float rotY = switch (facing) {
               case EAST -> 270.0F;
               case SOUTH -> 0.0F;
               case WEST -> 90.0F;
               default -> 180.0F;
            };
            float offsetX;
            float offsetZ;
            switch (facing) {
               case EAST:
                  offsetX = -0.5F;
                  offsetZ = -0.5F;
                  break;
               case SOUTH:
                  offsetX = 0.5F;
                  offsetZ = -0.5F;
                  break;
               case WEST:
                  offsetX = 0.5F;
                  offsetZ = 0.5F;
                  break;
               default:
                  offsetX = -0.5F;
                  offsetZ = 0.5F;
            }

            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
            poseStack.translate(offsetX, -1.5F, offsetZ);
            float shake = be.getShake();
            poseStack.translate(shake * 0.006F, Math.abs(shake) * 0.004F, 0.0F);
            VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(this.getTexture(state)));
            float rx = 0.15F + be.getCarriage() * 4.5F;
            float sy = be.getSpacePress() * 0.6F;
            float ey = be.getEnterPress() * 0.6F;
            float feed = be.getLineFeed();
            this.space.y += sy;
            this.enter.y += ey;
            float[] ky = new float[this.keyParts.size()];

            for (int i = 0; i < ky.length; i++) {
               ky[i] = i < 7 ? be.getKeyPress(i) * 0.7F : 0.0F;
               ModelPart var23 = this.keyParts.get(i);
               var23.y = var23.y + ky[i];
            }

            this.roller.x += rx;
            this.roller.xRot -= feed * 0.5F;
            this.paper.x += rx;
            this.paperWritten.x += rx;
            this.paper.y -= feed * 0.4F;
            int full = (Integer)state.getValue(TypewriterBlock.FULL);
            this.paper.visible = full == 1;
            this.paperWritten.visible = full == 2;
            this.typewriter.render(poseStack, vc, light, overlay);
            this.space.y -= sy;
            this.enter.y -= ey;

            for (int i = 0; i < ky.length; i++) {
               ModelPart var24 = this.keyParts.get(i);
               var24.y = var24.y - ky[i];
            }

            this.roller.x -= rx;
            this.roller.xRot += feed * 0.5F;
            this.paper.x -= rx;
            this.paperWritten.x -= rx;
            this.paper.y += feed * 0.4F;
            poseStack.popPose();
         }
      }
   }

   private ResourceLocation getTexture(BlockState state) {
      if (state.is((Block)ObjectRegistry.TYPEWRITER_IRON.get())) {
         return IRON_TEXTURE;
      } else {
         return state.is((Block)ObjectRegistry.TYPEWRITER_GOLD.get()) ? GOLD_TEXTURE : IRON_TEXTURE;
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((TypewriterEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
