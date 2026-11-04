package org.embeddedt.modernfix.duck;

import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;

public interface IExtendedModelBakery {
   void mfix$tick();

   void mfix$finishLoading();

   UnbakedModel mfix$loadUnbakedModelDynamic(ModelResourceLocation var1);

   UnbakedModel mfix$getMissingModel();

   ReentrantLock mfix$getLock();
}
