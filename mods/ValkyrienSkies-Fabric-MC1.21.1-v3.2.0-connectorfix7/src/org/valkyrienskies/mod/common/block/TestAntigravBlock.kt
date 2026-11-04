package org.valkyrienskies.mod.common.block

import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2343
import net.minecraft.class_2498
import net.minecraft.class_2586
import net.minecraft.class_2680
import net.minecraft.class_4970.class_2251
import org.valkyrienskies.mod.common.blockentity.TestAntigravBlockEntity

public object TestAntigravBlock : class_2248(class_2251.method_9637().method_9629(1.0F, 120.0F).method_9626(class_2498.field_11544)), class_2343 {
   fun method_10123(blockPos: class_2338, blockState: class_2680): class_2586? {
      TestAntigravBlockEntity(blockPos, blockState) as class_2586
   }
}
