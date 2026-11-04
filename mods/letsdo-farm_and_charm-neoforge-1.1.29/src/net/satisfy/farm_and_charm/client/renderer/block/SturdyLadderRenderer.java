package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.SturdyLadderBlock;
import net.satisfy.farm_and_charm.core.block.entity.SturdyLadderBlockEntity;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.joml.Quaternionf;

public class SturdyLadderRenderer implements BlockEntityRenderer<SturdyLadderBlockEntity> {
   private static final float TILT_ANGLE = 1.2F;
   private static final float JITTER = 0.008F;
   private static final float RAMP_TICKS = 4.0F;

   public SturdyLadderRenderer(Context context) {
   }

   public void render(SturdyLadderBlockEntity ladder, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
      Level level = ladder.getLevel();
      BlockState state = ladder.getBlockState();
      if (level != null && (Boolean)state.getValue(SturdyLadderBlock.SHAKING)) {
         double time = (float)level.getGameTime() + partialTick;
         float ramp = !PlatformHelper.animationsEnabled() ? 0.0F : Math.min(1.0F, (float)(time - ladder.getShakeStart()) / 4.0F);
         Direction facing = (Direction)state.getValue(LadderBlock.FACING);
         Direction wall = facing.getOpposite();
         float tilt = (0.6F + Math.abs((float)Math.sin(time * 1.1))) * 1.2F * ramp;
         float jitter = (float)Math.sin(time * 2.3) * 0.008F * ramp;
         pose.pushPose();
         pose.translate(0.5 + wall.getStepX() * 0.5 + facing.getStepZ() * jitter, 0.0, 0.5 + wall.getStepZ() * 0.5 - facing.getStepX() * jitter);
         pose.mulPose(new Quaternionf().rotationAxis(tilt * (float) (Math.PI / 180.0), facing.getStepZ(), 0.0F, -facing.getStepX()));
         pose.translate(-0.5 - wall.getStepX() * 0.5, 0.0, -0.5 - wall.getStepZ() * 0.5);
         Minecraft.getInstance()
            .getBlockRenderer()
            .renderSingleBlock((BlockState)state.setValue(SturdyLadderBlock.SHAKING, false), pose, buffers, light, overlay);
         pose.popPose();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((SturdyLadderBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
