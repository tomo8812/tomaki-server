package net.satisfy.brewery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.block.BrewingstationBlock;
import net.satisfy.brewery.core.block.entity.BrewstationBlockEntity;
import net.satisfy.brewery.core.block.property.Liquid;
import net.satisfy.brewery.core.registry.BlockStateRegistry;
import net.satisfy.brewery.platform.PlatformHelper;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.render.ClientUtil;
import org.joml.Quaternionf;

public class BrewingstationRenderer implements BlockEntityRenderer<BrewstationBlockEntity> {
   private static final float ITEM_SCALE = 0.32F;
   private static final float RING_RADIUS = 0.19F;
   private static final float CENTER_X = 0.5F;
   private static final float CENTER_Z = 0.53F;
   private static final float TILT = (float)Math.toRadians(14.0);
   private static final float ITEM_THICKNESS = 0.012F;

   public BrewingstationRenderer(Context ctx) {
   }

   public void render(
      BrewstationBlockEntity entity, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay
   ) {
      if (entity.hasLevel() && entity.getBlockState().getBlock() instanceof BrewingstationBlock) {
         this.updateLiquid(entity);
         BrewingstationLiquid.render(entity, partialTicks, matrixStack, bufferSource, combinedLight, combinedOverlay);
         BrewingstationLid.render(entity, partialTicks, matrixStack, bufferSource, combinedLight, combinedOverlay);
         List<ItemStack> ingredients = new ArrayList<>();

         for (ItemStack stack : entity.getIngredient()) {
            if (!stack.isEmpty()) {
               ingredients.add(stack);
            }
         }

         if (!ingredients.isEmpty()) {
            float surface = this.surfaceHeight(entity.getBlockState());
            float time = PlatformHelper.animationsEnabled() ? (float)entity.getLevel().getGameTime() + partialTicks : 0.0F;
            boolean floating = surface > 0.125F;
            Random random = new Random(entity.getBlockPos().hashCode());
            int count = ingredients.size();

            for (int index = 0; index < count; index++) {
               ItemStack stack = ingredients.get(index);
               float baseAngle = (float)((Math.PI * 2) * index / count);
               float drift = floating ? time * 0.012F : 0.0F;
               float ringAngle = baseAngle + drift;
               float radius = count == 1 ? 0.0F : 0.19F;
               float bob = floating ? (float)Math.sin(time * 0.08F + index * 2.1F) * 0.008F : 0.0F;
               float spin = baseAngle * 1.7F + (floating ? time * 0.01F * (index % 2 == 0 ? 1 : -1) : 0.0F);
               matrixStack.pushPose();
               matrixStack.translate(0.5 + Math.cos(ringAngle) * radius, surface + bob, 0.53F + Math.sin(ringAngle) * radius);
               float tiltX = floating ? (float)Math.sin(time * 0.08F + index * 2.1F + 0.8F) * TILT : 0.0F;
               float tiltZ = floating ? (float)Math.sin(time * 0.065F + index * 1.3F) * TILT : 0.0F;
               matrixStack.mulPose(new Quaternionf().rotateX(tiltX).rotateZ(tiltZ).rotateY(spin).rotateX((float)Math.toRadians(90.0)));
               matrixStack.scale(0.32F, 0.32F, 0.32F);
               int layers = Math.min(stack.getCount() / 16 + 1, 4);

               for (int layer = 0; layer < layers; layer++) {
                  matrixStack.pushPose();
                  matrixStack.translate((random.nextFloat() - 0.5F) * 0.12F, (random.nextFloat() - 0.5F) * 0.12F, -layer * 0.012F / 0.32F);
                  ClientUtil.renderItem(stack, matrixStack, bufferSource, entity);
                  matrixStack.popPose();
               }

               matrixStack.popPose();
            }
         }
      }
   }

   private void updateLiquid(BrewstationBlockEntity entity) {
      Level level = entity.getLevel();
      BlockState state = entity.getBlockState();
      if (state.hasProperty(BlockStateRegistry.LIQUID)) {
         Liquid liquid = (Liquid)state.getValue(BlockStateRegistry.LIQUID);
         if (liquid != Liquid.EMPTY && liquid != Liquid.DRAINED) {
            int color = entity.getLiquidColor(BiomeColors.getAverageWaterColor(level, entity.getBlockPos()));
            boolean active = entity.isBrewingClient() || liquid == Liquid.OVERFLOWING;
            long now = level.getGameTime();
            if (active && now != entity.getClientParticleTick()) {
               entity.setClientParticleTick(now);
               RandomSource random = level.getRandom();
               boolean overflowing = liquid == Liquid.OVERFLOWING;
               if (overflowing || !(random.nextFloat() > 0.25F)) {
                  BlockPos pos = entity.getBlockPos();
                  int bubbles = overflowing ? 4 : 1;

                  for (int i = 0; i < bubbles; i++) {
                     double x = pos.getX() + 0.15 + random.nextDouble() * 0.7;
                     double z = pos.getZ() + 0.15 + random.nextDouble() * 0.7;
                     double y = pos.getY() + this.surfaceHeight(state) + 0.02;
                     level.addParticle(
                        ColorParticleOption.create((ParticleType)FoundationParticles.COLORED_SOUP_BUBBLE.get(), ARGB32.opaque(color)),
                        x,
                        y,
                        z,
                        0.0,
                        overflowing ? 0.03 : 0.01,
                        0.0
                     );
                  }
               }
            }
         }
      }
   }

   private float surfaceHeight(BlockState state) {
      return state.hasProperty(BlockStateRegistry.LIQUID) ? BrewingstationLiquid.surfaceHeight((Liquid)state.getValue(BlockStateRegistry.LIQUID)) : 0.125F;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((BrewstationBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
