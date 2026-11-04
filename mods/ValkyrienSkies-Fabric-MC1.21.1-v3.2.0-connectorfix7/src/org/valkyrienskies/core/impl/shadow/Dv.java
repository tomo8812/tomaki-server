package org.valkyrienskies.core.impl.shadow;

import javax.annotation.Nullable;
import org.joml.primitives.AABBi;
import org.valkyrienskies.core.util.datastructures.IBlockPosSet;

public interface Dv extends IBlockPosSet {
   @Nullable
   AABBi a();
}
