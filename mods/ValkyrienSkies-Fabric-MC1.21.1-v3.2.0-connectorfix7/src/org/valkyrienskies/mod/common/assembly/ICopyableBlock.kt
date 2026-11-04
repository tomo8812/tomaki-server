package org.valkyrienskies.mod.common.assembly

import java.util.function.Supplier
import net.minecraft.class_1792
import net.minecraft.class_2338
import net.minecraft.class_2487
import net.minecraft.class_2586
import net.minecraft.class_2680
import net.minecraft.class_3218
import org.jetbrains.annotations.ApiStatus
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip

@ApiStatus.Experimental
public interface ICopyableBlock {
   public abstract fun onCopy(
      level: class_3218,
      pos: class_2338,
      state: class_2680,
      be: class_2586?,
      shipsBeingCopied: List<ServerShip>,
      centerPositions: Map<Long, Vector3dc>
   ): class_2487? {
   }

   public abstract fun onPaste(
      level: class_3218,
      pos: class_2338,
      state: class_2680,
      oldShipIdToNewId: Map<Long, Long>,
      centerPositions: Map<Long, Pair<Vector3dc, Vector3dc>>,
      tag: class_2487?
   ): class_2487? {
   }

   public open fun pasteSurvivalCost(data: Supplier<class_2487>?): Map<class_1792, Int>? {
      return null
   }
}
