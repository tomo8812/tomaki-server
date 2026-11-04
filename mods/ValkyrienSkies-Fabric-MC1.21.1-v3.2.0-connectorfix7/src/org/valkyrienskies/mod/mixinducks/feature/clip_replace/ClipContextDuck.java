package org.valkyrienskies.mod.mixinducks.feature.clip_replace;

import org.valkyrienskies.mod.common.world.RayCastExtraParameter;

public interface ClipContextDuck {
   void setRayCastExtraParameter(RayCastExtraParameter var1);

   RayCastExtraParameter getRayCastExtraParameter();

   void reset();
}
