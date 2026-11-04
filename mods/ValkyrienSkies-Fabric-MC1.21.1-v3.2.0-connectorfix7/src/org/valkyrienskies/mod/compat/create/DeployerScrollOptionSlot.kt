package org.valkyrienskies.mod.compat.create

import com.simibubi.create.content.contraptions.DirectionalExtenderScrollOptionSlot
import java.util.function.BiPredicate
import net.minecraft.class_1936
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_243
import net.minecraft.class_2680
import net.minecraft.class_2741
import net.minecraft.class_2769

public class DeployerScrollOptionSlot(allowedDirections: BiPredicate<class_2680, class_2350>) : DirectionalExtenderScrollOptionSlot(allowedDirections) {
   public open fun getLocalOffset(level: class_1936, pos: class_2338, state: class_2680): class_243 {
      val var10000: class_243 = super.getLocalOffset(level, pos, state)
         .method_1019(class_243.method_24954((state.method_11654(class_2741.field_12525 as class_2769) as class_2350).method_10163()).method_1021(-0.25))
         return var10000
   }
}
