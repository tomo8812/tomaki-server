package org.valkyrienskies.mod.util;

import java.util.List;
import java.util.Map;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;

public interface StructureTemplateFillFromVoxelSet {
   void vs$fillFromVoxelSet(
      @NotNull class_1937 var1,
      @NotNull Iterable<class_2338> var2,
      @NotNull List<ServerShip> var3,
      @NotNull Map<Long, Vector3d> var4,
      @NotNull class_2338 var5,
      @NotNull class_2338 var6
   );
}
