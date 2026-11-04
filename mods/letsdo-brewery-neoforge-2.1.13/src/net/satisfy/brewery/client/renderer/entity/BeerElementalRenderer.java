package net.satisfy.brewery.client.renderer.entity;

import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.client.model.BeerElementalModel;
import net.satisfy.brewery.core.entity.BeerElementalEntity;
import org.jetbrains.annotations.NotNull;

public class BeerElementalRenderer extends MobRenderer<BeerElementalEntity, BeerElementalModel<BeerElementalEntity>> {
   private static final ResourceLocation TEXTURE = Brewery.identifier("textures/entity/beer_elemental.png");

   public BeerElementalRenderer(Context context) {
      super(context, new BeerElementalModel(context.bakeLayer(BeerElementalModel.BEER_ELEMENTAL_MODEL_LAYER)), 0.7F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(BeerElementalEntity entity) {
      return TEXTURE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected float getShadowRadius(LivingEntity param1) {
      return super.getShadowRadius((Mob)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected boolean shouldShowName(LivingEntity param1) {
      return super.shouldShowName((Mob)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   public ResourceLocation getTextureLocation(Entity param1) {
      return this.getTextureLocation((BeerElementalEntity)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected float getShadowRadius(Entity param1) {
      return super.getShadowRadius((Mob)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected boolean shouldShowName(Entity param1) {
      return super.shouldShowName((Mob)var1);
   }
}
