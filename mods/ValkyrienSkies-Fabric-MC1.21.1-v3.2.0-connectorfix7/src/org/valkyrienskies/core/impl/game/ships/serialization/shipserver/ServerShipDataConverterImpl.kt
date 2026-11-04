package org.valkyrienskies.core.impl.game.ships.serialization.shipserver

import javax.inject.Inject
import org.valkyrienskies.core.impl.bodies.properties.BodyKinematicsFactory
import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.ShipInertiaConverter
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV4
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.ShipTransformConverter
import org.valkyrienskies.core.impl.shadow.Cy
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class ServerShipDataConverterImpl @Inject  public constructor(inertiaConverter: ShipInertiaConverter,
      transformConverter: ShipTransformConverter,
      attachmentHolderFactory: Cy
   ) :
   ServerShipDataConverter {
   private final val inertiaConverter: ShipInertiaConverter
   private final val transformConverter: ShipTransformConverter
   private final val attachmentHolderFactory: Cy
   private final val transformVelocityFactory: BodyKinematicsFactory

   init {
      this.inertiaConverter = inertiaConverter
      this.transformConverter = transformConverter
      this.attachmentHolderFactory = attachmentHolderFactory
      this.transformVelocityFactory = BodyKinematicsFactory.INSTANCE
   }

   public override fun convertToDto(model: ShipData): ServerShipDataV4 {
      val var10000: ServerShipDataV4 = ServerShipDataV4
      val var10002: Long = model.id
      var var10003: java.lang.String = model.slug
      if (var10003 == null) {
         var10003 = ""
      }

      var10000./* $VF: Unable to resugar constructor */<init>(
         var10002,
         var10003,
         model.chunkClaim,
         model.chunkClaimDimension,
         model.kinematics.velocity,
         model.kinematics.angularVelocity,
         this.inertiaConverter.convertToDto(model.inertiaData),
         this.transformConverter.convertToDto(model.getTransform()),
         this.transformConverter.convertToDto(model.prevTickTransform),
         model.worldAABB,
         model.shipAABB,
         model.activeChunksSet,
         model.isStatic,
         model.attachmentHolder.a(),
         model.portalCoolDown
      )
      return var10000
   }

   public override fun convertToModel(dto: ServerShipDataV4): ShipData {
      return ShipData(
         dto.id,
         if (dto.name == "") null else dto.name,
         dto.chunkClaim,
         dto.chunkClaimDimension,
         this.inertiaConverter.convertToModel(dto.inertiaData),
         this.transformVelocityFactory.create(dto.velocity, dto.omega, this.transformConverter.convertToModel(dto.transform)),
         dto.worldAABB,
         dto.shipAABB,
         dto.activeChunks,
         dto.isStatic,
         null,
         this.attachmentHolderFactory.a(dto.persistentAttachedData),
         dto.portalCoolDown,
         1024,
         null
      )
   }
}
