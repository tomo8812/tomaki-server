package org.valkyrienskies.mod.mixinducks.world.level.lighting;

import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_4076;

public interface ThreadedLevelLightEngineDuck {
   void vsDirectUpdateSectionStatus(class_4076 var1, boolean var2);

   void vsDirectSetLightEnabled(class_1923 var1, boolean var2);

   void vsDirectPropagateLightSources(class_1923 var1);

   void vsDirectCheckBlock(class_2338 var1);

   int vsDirectRunLightUpdates();
}
