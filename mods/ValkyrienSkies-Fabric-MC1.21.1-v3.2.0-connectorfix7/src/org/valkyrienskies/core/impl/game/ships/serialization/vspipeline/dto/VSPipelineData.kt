package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonSubTypes([@JsonSubTypes.Type(VSPipelineDataV1::class), @JsonSubTypes.Type(VSPipelineDataV2::class), @JsonSubTypes.Type(VSPipelineDataV3::class), @JsonSubTypes.Type(VSPipelineDataV4::class), @JsonSubTypes.Type(VSPipelineDataV5::class)])
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
public sealed interface VSPipelineData
