package org.valkyrienskies.core.impl.game.ships.serialization.shipserver

import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV0Updater
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV0UpdaterImpl
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV1Updater
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV1UpdaterImpl
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV2Updater
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV2UpdaterImpl
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV3Updater
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV3UpdaterImpl
import org.valkyrienskies.core.impl.shadow.dP
import org.valkyrienskies.core.impl.shadow.dW

@dW
public interface ServerShipDataModule {
   @dP
   public abstract fun converter(impl: ServerShipDataConverterImpl): ServerShipDataConverter {
   }

   @dP
   public abstract fun v0(impl: ServerShipDataV0UpdaterImpl): ServerShipDataV0Updater {
   }

   @dP
   public abstract fun v1(impl: ServerShipDataV1UpdaterImpl): ServerShipDataV1Updater {
   }

   @dP
   public abstract fun v2(impl: ServerShipDataV2UpdaterImpl): ServerShipDataV2Updater {
   }

   @dP
   public abstract fun v3(impl: ServerShipDataV3UpdaterImpl): ServerShipDataV3Updater {
   }
}
