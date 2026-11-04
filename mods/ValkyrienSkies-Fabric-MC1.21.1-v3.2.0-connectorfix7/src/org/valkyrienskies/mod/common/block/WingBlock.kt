package org.valkyrienskies.mod.common.block

import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2680
import org.valkyrienskies.core.api.ships.Wing

public interface WingBlock {
   public abstract fun getWing(level: class_1937?, pos: class_2338?, blockState: class_2680): Wing? {
   }
}
