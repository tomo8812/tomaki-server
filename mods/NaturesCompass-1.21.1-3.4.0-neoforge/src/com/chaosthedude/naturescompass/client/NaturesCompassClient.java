package com.chaosthedude.naturescompass.client;

import com.chaosthedude.naturescompass.NaturesCompass;
import com.chaosthedude.naturescompass.items.NaturesCompassItem;
import com.chaosthedude.naturescompass.util.CompassState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = "naturescompass", bus = Bus.MOD, value = Dist.CLIENT)
public class NaturesCompassClient {
   @SubscribeEvent
   public static void clientInit(FMLClientSetupEvent event) {
      event.enqueueWork(
         () -> ItemProperties.register(
            NaturesCompass.naturesCompass, ResourceLocation.fromNamespaceAndPath("naturescompass", "angle"), new ClampedItemPropertyFunction() {
               @OnlyIn(Dist.CLIENT)
               private double rotation;
               @OnlyIn(Dist.CLIENT)
               private double rota;
               @OnlyIn(Dist.CLIENT)
               private long lastUpdateTick;

               @OnlyIn(Dist.CLIENT)
               public float unclampedCall(ItemStack stack, ClientLevel world, LivingEntity entityLiving, int seed) {
                  if (entityLiving == null && !stack.isFramed()) {
                     return 0.0F;
                  }

                  boolean entityExists = entityLiving != null;
                  Entity entity = (Entity)(entityExists ? entityLiving : stack.getFrame());
                  if (world == null && entity.level() instanceof ClientLevel) {
                     world = (ClientLevel)entity.level();
                  }

                  double rotation = entityExists ? entity.getYRot() : this.getFrameRotation((ItemFrame)entity);
                  rotation %= 360.0;
                  double adjusted = Math.PI - ((rotation - 90.0) * (float) (Math.PI / 180.0) - this.getAngle(world, entity, stack));
                  if (entityExists) {
                     adjusted = this.wobble(world, adjusted);
                  }

                  float f = (float)(adjusted / (Math.PI * 2));
                  return Mth.positiveModulo(f, 1.0F);
               }

               @OnlyIn(Dist.CLIENT)
               private double wobble(ClientLevel world, double amount) {
                  if (world.getGameTime() != this.lastUpdateTick) {
                     this.lastUpdateTick = world.getGameTime();
                     double d0 = amount - this.rotation;
                     d0 = Mth.positiveModulo(d0 + Math.PI, Math.PI * 2) - Math.PI;
                     d0 = Mth.clamp(d0, -1.0, 1.0);
                     this.rota += d0 * 0.1;
                     this.rota *= 0.8;
                     this.rotation = this.rotation + this.rota;
                  }

                  return this.rotation;
               }

               @OnlyIn(Dist.CLIENT)
               private double getFrameRotation(ItemFrame itemFrame) {
                  Direction direction = itemFrame.getDirection();
                  int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
                  return Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + itemFrame.getRotation() * 45 + i);
               }

               @OnlyIn(Dist.CLIENT)
               private double getAngle(ClientLevel world, Entity entity, ItemStack stack) {
                  if (stack.getItem() == NaturesCompass.naturesCompass) {
                     NaturesCompassItem compassItem = (NaturesCompassItem)stack.getItem();
                     BlockPos pos;
                     if (compassItem.getState(stack) == CompassState.FOUND) {
                        pos = new BlockPos(compassItem.getFoundBiomeX(stack), 0, compassItem.getFoundBiomeZ(stack));
                     } else {
                        pos = world.getSharedSpawnPos();
                     }

                     return Math.atan2(pos.getZ() - entity.position().z(), pos.getX() - entity.position().x());
                  } else {
                     return 0.0;
                  }
               }
            }
         )
      );
   }

   @SubscribeEvent
   public static void registerOverlay(RegisterGuiLayersEvent event) {
      event.registerAbove(
         VanillaGuiLayers.BOSS_OVERLAY, ResourceLocation.fromNamespaceAndPath("naturescompass", "natures_compass"), new NaturesCompassOverlay()
      );
   }
}
