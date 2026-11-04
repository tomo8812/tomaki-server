package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.module.SimpleModule;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics;
import org.valkyrienskies.core.api.bodies.properties.BodyTransform;
import org.valkyrienskies.core.api.ships.properties.ChunkClaim;
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.bodies.properties.BodyKinematicsImpl;
import org.valkyrienskies.core.impl.bodies.properties.BodyTransformImpl;
import org.valkyrienskies.core.impl.chunk_tracking.ShipActiveChunksSet;
import org.valkyrienskies.core.impl.game.ChunkClaimImpl;

public final class GR extends SimpleModule {
   public GR() {
      SimpleModule var1;
      Intrinsics.checkNotNullExpressionValue((var1 = this).addAbstractTypeMapping(IShipActiveChunksSet.class, ShipActiveChunksSet.class), "");
      Intrinsics.checkNotNullExpressionValue((var1 = this).addAbstractTypeMapping(ChunkClaim.class, ChunkClaimImpl.class), "");
      Intrinsics.checkNotNullExpressionValue((var1 = this).addAbstractTypeMapping(ShipTransform.class, BodyTransformImpl.class), "");
      Intrinsics.checkNotNullExpressionValue((var1 = this).addAbstractTypeMapping(BodyTransform.class, BodyTransformImpl.class), "");
      Intrinsics.checkNotNullExpressionValue((var1 = this).addAbstractTypeMapping(BodyKinematics.class, BodyKinematicsImpl.class), "");
   }
}
