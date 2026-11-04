package net.satisfy.herbalbrews.core.registry;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.item.Item;
import net.satisfy.herbalbrews.client.model.HatModel;
import net.satisfy.herbalbrews.client.model.TopHatModel;
import net.satisfy.herbalbrews.client.model.WitchHatModel;

public class ArmorRegistry {
   private static final Map<Item, HatModel> models = new HashMap<>();
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !ArmorRegistry.class.desiredAssertionStatus();

   public static Model getHatModel(Item item, ModelPart baseHead) {
      EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
      HatModel model = models.computeIfAbsent(item, key -> {
         if (key == ObjectRegistry.TOP_HAT.get()) {
            return new TopHatModel(modelSet.bakeLayer(TopHatModel.LAYER_LOCATION));
         } else {
            return key == ObjectRegistry.WITCH_HAT.get() ? new WitchHatModel(modelSet.bakeLayer(WitchHatModel.LAYER_LOCATION)) : null;
         }
      });
      assert model != null;
      model.copyHead(baseHead);
      return (Model)model;
   }
}
