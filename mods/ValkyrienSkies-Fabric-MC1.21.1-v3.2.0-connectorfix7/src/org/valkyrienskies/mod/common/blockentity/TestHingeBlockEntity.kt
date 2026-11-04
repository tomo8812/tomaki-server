package org.valkyrienskies.mod.common.blockentity

import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2487
import net.minecraft.class_2586
import net.minecraft.class_2680
import net.minecraft.class_3218
import net.minecraft.class_7225.class_7874
import org.joml.Quaterniond
import org.joml.Vector3dc
import org.valkyrienskies.core.internal.joints.VSJointPose
import org.valkyrienskies.core.internal.joints.VSRevoluteJoint
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.util.GameToPhysicsAdapter
import org.valkyrienskies.mod.util.NbtUtilKt

public class TestHingeBlockEntity(blockPos: class_2338, blockState: class_2680) : class_2586(
      ValkyrienSkiesMod.INSTANCE.TEST_HINGE_BLOCK_ENTITY_TYPE, blockPos, blockState
   ) {
   public final var otherHingePos: class_2338?
   public final var constraintId: Int?
   public final var hingeConstraint: VSRevoluteJoint?
   private final var makeConstraint: Boolean

   fun method_11007(tag: class_2487, provider: class_7874) {
      super.method_11007(tag, provider)
      if (this.hingeConstraint != null) {
         val h: VSRevoluteJoint = this.hingeConstraint
         var var10002: java.lang.Long = this.hingeConstraint.shipId0
         tag.method_10544("shipId0", var10002 ?: -1L)
         var10002 = h.shipId1
         tag.method_10544("shipId1", var10002 ?: -1L)
         NbtUtilKt.putVector3d(tag, "pos0", h.pose0.pos)
         NbtUtilKt.putVector3d(tag, "pos1", h.pose1.pos)
         NbtUtilKt.putQuatd(tag, "rot0", h.pose0.rot)
         NbtUtilKt.putQuatd(tag, "rot1", h.pose1.rot)
      }
   }

   fun method_11014(tag: class_2487, provider: class_7874) {
      super.method_11014(tag, provider)
      val var10001: VSRevoluteJoint = VSRevoluteJoint
      val var10003: java.lang.Long = tag.method_10537("shipId0")
      val var10004: VSJointPose = VSJointPose
      var var10006: Vector3dc = NbtUtilKt.getVector3d(tag, "pos0")
      if (var10006 != null) {
         var10006 = var10006
         val var10007: Quaterniond = NbtUtilKt.getQuatd(tag, "rot0")
         if (var10007 != null) {
            var10004./* $VF: Unable to resugar constructor */<init>(var10006, var10007)
            val var10005: java.lang.Long = tag.method_10537("shipId1")
            val var4: VSJointPose = VSJointPose
            var var10008: Vector3dc = NbtUtilKt.getVector3d(tag, "pos1")
            if (var10008 != null) {
               var10008 = var10008
               val var10009: Quaterniond = NbtUtilKt.getQuatd(tag, "rot1")
               if (var10009 != null) {
                  var4./* $VF: Unable to resugar constructor */<init>(var10008, var10009)
                  var10001./* $VF: Unable to resugar constructor */<init>(
                     var10003, var10004, var10005, var4, null, 0.0, null, null, null, null, true, 992, null
                  )
                  this.hingeConstraint = var10001
                  this.makeConstraint = true
               }
            }
         }
      }
   }

   public fun tick() {
      if (this.makeConstraint) {
         if (this.field_11863 is class_3218) {
            this.makeConstraint = false
            val var10000: class_1937 = this.field_11863
            val var2: GameToPhysicsAdapter = ValkyrienSkiesMod.getOrCreateGTPA(dimensionId)
            val var10001: VSRevoluteJoint = this.hingeConstraint
            var2.addJoint(var10001, 4, { it: Int ->
               `this$0`.constraintId = it
            })
         }
      }
   }
}
