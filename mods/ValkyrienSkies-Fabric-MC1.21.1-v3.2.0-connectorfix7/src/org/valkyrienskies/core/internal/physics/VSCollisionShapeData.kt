package org.valkyrienskies.core.internal.physics

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes([@JsonSubTypes.Type(value = VSVoxelCollisionShapeData::class, name = "voxel"), @JsonSubTypes.Type(value = VSSphereCollisionShapeData::class, name = "sphere"), @JsonSubTypes.Type(value = VSWheelCollisionShapeData::class, name = "wheel"), @JsonSubTypes.Type(value = VSBoxCollisionShapeData::class, name = "box"), @JsonSubTypes.Type(value = VSCapsuleCollisionShapeData::class, name = "capsule")])
public interface VSCollisionShapeData {
   public val shapeType: VSCollisionShapes
}
