package org.valkyrienskies.core.impl.game.ships.serialization

import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.ShipInertiaModule
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.ServerShipDataModule
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.ShipTransformModule
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.VSPipelineChainUpdaterModule
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.VSPipelineDataModule
import org.valkyrienskies.core.impl.shadow.dW

@dW(a = [VSPipelineDataModule::class, VSPipelineChainUpdaterModule::class, ServerShipDataModule::class, ShipInertiaModule::class, ShipTransformModule::class])
public class ShipSerializationModule
