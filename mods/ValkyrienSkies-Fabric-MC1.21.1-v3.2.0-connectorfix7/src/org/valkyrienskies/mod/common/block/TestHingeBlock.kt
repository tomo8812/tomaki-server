package org.valkyrienskies.mod.common.block

import com.mojang.serialization.MapCodec
import java.util.Optional
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt
import net.minecraft.class_1269
import net.minecraft.class_1657
import net.minecraft.class_1750
import net.minecraft.class_1922
import net.minecraft.class_1937
import net.minecraft.class_2246
import net.minecraft.class_2248
import net.minecraft.class_2318
import net.minecraft.class_2338
import net.minecraft.class_2343
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_2487
import net.minecraft.class_2498
import net.minecraft.class_2586
import net.minecraft.class_2591
import net.minecraft.class_265
import net.minecraft.class_2680
import net.minecraft.class_2769
import net.minecraft.class_3218
import net.minecraft.class_3726
import net.minecraft.class_3965
import net.minecraft.class_5558
import net.minecraft.class_2689.class_2690
import net.minecraft.class_4970.class_2251
import org.joml.AxisAngle4d
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.internal.joints.VSJointPose
import org.valkyrienskies.core.internal.joints.VSRevoluteJoint
import org.valkyrienskies.core.internal.ships.VsiShip
import org.valkyrienskies.core.internal.world.VsiServerShipWorld
import org.valkyrienskies.mod.api.VsApi
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.assembly.ICopyableBlock
import org.valkyrienskies.mod.common.blockentity.TestHingeBlockEntity
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils
import org.valkyrienskies.mod.common.util.GameToPhysicsAdapter
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.util.NbtUtilKt

@SourceDebugExtension(["SMAP\nTestHingeBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TestHingeBlock.kt\norg/valkyrienskies/mod/common/block/TestHingeBlock\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,252:1\n1#2:253\n*E\n"])
public object TestHingeBlock : class_2318(class_2251.method_9637().method_9629(10.0F, 1200.0F).method_9626(class_2498.field_11533)), class_2343, ICopyableBlock {
   private final val EAST_AABB: class_265 = class_2318.method_9541(0.0, 0.0, 0.0, 8.0, 16.0, 16.0)
   private final val WEST_AABB: class_265 = class_2318.method_9541(8.0, 0.0, 0.0, 16.0, 16.0, 16.0)
   private final val SOUTH_AABB: class_265 = class_2318.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 8.0)
   private final val NORTH_AABB: class_265 = class_2318.method_9541(0.0, 0.0, 8.0, 16.0, 16.0, 16.0)
   private final val UP_AABB: class_265 = class_2318.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
   private final val DOWN_AABB: class_265 = class_2318.method_9541(0.0, 8.0, 0.0, 16.0, 16.0, 16.0)
   private final val CODEC: MapCodec<TestHingeBlock>

   fun method_9515(builder: class_2690<class_2248, class_2680>) {
      builder.method_11667(arrayOf(class_2318.field_10927))
   }

   fun method_9605(ctx: class_1750): class_2680 {
      val var10000: Any = this.method_9564().method_11657(class_2318.field_10927 as class_2769, ctx.method_7715().method_10153() as java.lang.Comparable)
      var10000 as class_2680
   }

   /** @deprecated */
   @Deprecated(message = "Deprecated in Java")
   fun method_9530(state: class_2680, level: class_1922?, pos: class_2338?, context: class_3726?): class_265 {
      val var10000: class_2350 = state.method_11654(class_2318.field_10927 as class_2769) as class_2350
var var6: class_265
      when (if (var10000 == null) -1 else TestHingeBlock.WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
         1 -> var6 = DOWN_AABB
         2 -> var6 = NORTH_AABB
         3 -> var6 = SOUTH_AABB
         4 -> var6 = WEST_AABB
         5 -> var6 = EAST_AABB
         6 -> var6 = UP_AABB
         else -> {
            val var5: class_265 = UP_AABB
            var6 = var5
         }
      }

      var6
   }

   /** @deprecated */
   @Deprecated(message = "Deprecated in Java")
   fun method_55766(state: class_2680, level: class_1937, pos: class_2338, player: class_1657, blockHitResult: class_3965): class_1269 {
      if (level.field_9236) {
         class_1269.field_5812
      } else {
         val blockEntity: Optional = level.method_35230(pos, ValkyrienSkiesMod.INSTANCE.TEST_HINGE_BLOCK_ENTITY_TYPE)
         if (!blockEntity.isPresent()) {
            class_1269.field_21466
         } else if ((blockEntity.get() as TestHingeBlockEntity).otherHingePos != null) {
            class_1269.field_21466
         } else {
            val shipThisIsIn: Ship = VSGameUtilsKt.getShipManagingPos(level, pos)
val var10000: VsiServerShipWorld = shipObjectWorld
val var10001: class_2338 = pos.method_10069(0, 1, 0)
val ship: ServerShip = var10000.createNewShipAtBlock(VectorConversionsMCKt.toJOML(var10001 as class_2382), false, 1.0, dimensionId)
EntityShipCollisionUtils.markShipAsRecentlySpawned$default(ship.getId(), (level as class_3218).method_8510(), 0L, 4, null)
val shipCenterPos: class_2338 = class_2338(
   MathKt.roundToInt(ship.getTransform().positionInShip.x() - 0.5),
   MathKt.roundToInt(ship.getTransform().positionInShip.y() - 0.5),
   MathKt.roundToInt(ship.getTransform().positionInShip.z() - 0.5)
)
val var25: class_2350 = state.method_11654(class_2318.field_10927 as class_2769) as class_2350
var var26: Quaterniond
            when (if (var25 == null) -1 else TestHingeBlock.WhenMappings.$EnumSwitchMapping$0[var25.ordinal()]) {
               1 -> var26 = Quaterniond(AxisAngle4d(Math.PI, Vector3d(1.0, 0.0, 0.0)))
               2 -> var26 = Quaterniond(AxisAngle4d(Math.PI, Vector3d(0.0, 1.0, 0.0)))
                     .mul(Quaterniond(AxisAngle4d(Math.PI / 2, Vector3d(1.0, 0.0, 0.0))))
                     .normalize()
                  3 -> var26 = Quaterniond(AxisAngle4d(Math.PI / 2, Vector3d(1.0, 0.0, 0.0))).normalize()
               4 -> var26 = Quaterniond(AxisAngle4d(Math.PI * 3.0 / 2.0, Vector3d(0.0, 1.0, 0.0)))
                     .mul(Quaterniond(AxisAngle4d(Math.PI / 2, Vector3d(1.0, 0.0, 0.0))))
                     .normalize()
                  5 -> var26 = Quaterniond(AxisAngle4d(Math.PI / 2, Vector3d(0.0, 1.0, 0.0)))
                     .mul(Quaterniond(AxisAngle4d(Math.PI / 2, Vector3d(1.0, 0.0, 0.0))))
                     .normalize()
                  6 -> var26 = Quaterniond()
               else -> throw NoWhenBranchMatchedException()
            }

            val var27: Vector3d = var26.transform(Vector3d(0.0, 0.5 + 0.0, 0.0))
            val attachmentOffset0: Vector3dc = var27
            val var28: Vector3d = var26.transform(Vector3d(0.0, -0.5, 0.0))
            val attachmentOffset1: Vector3dc = var28
            val var29: Vector3d = Vector3d((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5)
               .add(attachmentOffset0)
               val attachmentLocalPos0: Vector3dc = var29
            val var30: Vector3d = Vector3d(
                  (double)shipCenterPos.method_10263() + 0.5, (double)shipCenterPos.method_10264() + 0.5, (double)shipCenterPos.method_10260() + 0.5
               )
               .add(attachmentOffset1)
               val attachmentLocalPos1: Vector3dc = var30
            if (shipThisIsIn != null) {
               val shipId0: Vector3d = shipThisIsIn.transform.shipToWorld.transformPosition(attachmentLocalPos0, Vector3d())
               shipId0.sub(shipThisIsIn.transform.shipToWorldRotation.transform(attachmentOffset1, Vector3d()))
               val var31: VsApi = vsApi
               val var34: Vector3dc = shipThisIsIn.velocity
               val var10002: Vector3dc = shipThisIsIn.angularVelocity
               val shipId1: BodyKinematics = var31.newBodyKinematics(
                  var34,
                  var10002,
                  shipId0,
                  shipThisIsIn.transform.shipToWorldRotation,
                  ship.getTransform().shipToWorldScaling,
                  ship.getTransform().positionInShip
               )
               (ship as VsiShip).unsafeSetKinematics(shipId1)
            } else {
               val var21: Vector3d = Vector3d(attachmentLocalPos0)
               var21.sub(attachmentOffset1)
               val var23: BodyKinematics = vsApi
                  .newBodyKinematics(
                     ship.getVelocity(),
                     ship.getAngularVelocity(),
                     var21,
                     ship.getTransform().shipToWorldRotation,
                     ship.getTransform().shipToWorldScaling,
                     ship.getTransform().positionInShip
                  )
                  (ship as VsiShip).unsafeSetKinematics(var23)
            }

            level.method_8501(shipCenterPos, class_2246.field_10085.method_9564())
            (blockEntity.get() as TestHingeBlockEntity).otherHingePos = shipCenterPos
            val var22: java.lang.Long = if (shipThisIsIn != null) shipThisIsIn.id else null
            val var24: Long = ship.getId()
            val hingeOrientation: Quaterniond = var26.mul(Quaterniond(AxisAngle4d(Math.toRadians(90.0), 0.0, 0.0, 1.0)), Quaterniond()).normalize()
            val var32: TestHingeBlockEntity = blockEntity.get() as TestHingeBlockEntity
            var32.hingeConstraint = VSRevoluteJoint(
               var22,
               VSJointPose(attachmentLocalPos0, hingeOrientation),
               var24,
               VSJointPose(attachmentLocalPos1, hingeOrientation),
               null,
               0.0,
               null,
               null,
               null,
               null,
               true,
               992,
               null
            )
            val var33: GameToPhysicsAdapter = ValkyrienSkiesMod.getOrCreateGTPA(dimensionId)
            val var35: VSRevoluteJoint = (blockEntity.get() as TestHingeBlockEntity).hingeConstraint
            var33.addJoint(var35, 4, { t: Int ->
               (`$blockEntity`.get() as TestHingeBlockEntity).constraintId = t
            })
            class_1269.field_21466
         }
      }
   }

   fun method_9536(blockState: class_2680, level: class_1937, blockPos: class_2338, blockState2: class_2680, bl: Boolean) {
      if (level is class_3218) {
         val `$this$onRemove_u24lambda_u242`: TestHingeBlock = this
         val var8: class_2586 = (level as class_3218).method_8321(blockPos)
         val var10000: TestHingeBlockEntity = var8 as? TestHingeBlockEntity
         if ((var8 as? TestHingeBlockEntity) != null) {
            val var10: GameToPhysicsAdapter = ValkyrienSkiesMod.getOrCreateGTPA(dimensionId)
            val var10001: Int = var10000.constraintId
            if (var10001 != null) {
               var10.removeJoint(var10001)
            }
         }
      }

      super.method_9536(blockState, level, blockPos, blockState2, bl)
   }

   public open fun newBlockEntity(blockPos: class_2338, blockState: class_2680): TestHingeBlockEntity {
      return TestHingeBlockEntity(blockPos, blockState)
   }

   fun method_53969(): MapCodec<TestHingeBlock> {
      CODEC
   }

   public override fun onCopy(
      level: class_3218,
      pos: class_2338,
      state: class_2680,
      be: class_2586?,
      shipsBeingCopied: List<ServerShip>,
      centerPositions: Map<Long, Vector3dc>
   ): class_2487? {
      return null
   }

   public override fun onPaste(
      level: class_3218,
      pos: class_2338,
      state: class_2680,
      oldShipIdToNewId: Map<Long, Long>,
      centerPositions: Map<Long, Pair<Vector3dc, Vector3dc>>,
      tag: class_2487?
   ): class_2487? {
      if (tag == null) {
         return null
      } else {
         var var10002: Any = centerPositions.get(tag.method_10537("shipId0"))
         var old: Vector3dc = (var10002 as Pair).component1() as Vector3dc
         var var11: Vector3dc = (var10002 as Pair).component2() as Vector3dc
         var var10000: Vector3d = NbtUtilKt.getVector3d(tag, "pos0")
         var var14: Vector3d = var10000.sub(old).add(var11)
         NbtUtilKt.putVector3d(tag, "pos0", var14)
         var10002 = centerPositions.get(tag.method_10537("shipId1"))
         old = (var10002 as Pair).component1() as Vector3dc
         var11 = (var10002 as Pair).component2() as Vector3dc
         var10000 = NbtUtilKt.getVector3d(tag, "pos1")
         var14 = var10000.sub(old).add(var11)
         NbtUtilKt.putVector3d(tag, "pos1", var14)
         var10002 = oldShipIdToNewId.get(tag.method_10537("shipId0"))
         tag.method_10544("shipId0", (var10002 as java.lang.Number).longValue())
         var10002 = oldShipIdToNewId.get(tag.method_10537("shipId1"))
         tag.method_10544("shipId1", (var10002 as java.lang.Number).longValue())
         return tag
      }
   }

   fun <T extends class_2586> method_31645(level: class_1937, blockState: class_2680, type: class_2591<T>): class_5558<T> {
      { var1: class_1937, var2: class_2338, var3: class_2680, blockEntity: class_2586 ->
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
         //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
         //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(ReferencePipeline.java:288)
         //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1716)
         //   at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:570)
         //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:560)
         //   at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:635)
         //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(AbstractPipeline.java:291)
         //   at java.base/java.util.stream.ReferencePipeline.toArray(ReferencePipeline.java:652)
         //   at java.base/java.util.stream.ReferencePipeline.toArray(ReferencePipeline.java:658)
         //   at java.base/java.util.stream.ReferencePipeline.toList(ReferencePipeline.java:663)
         //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
         //   at org.vineflower.kotlin.expr.KExitExprent.toJava(KExitExprent.java:41)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun method_10123(blockPos: class_2338, blockState: class_2680): class_2586 {
      this.newBlockEntity(blockPos, blockState) as class_2586
   }

   @JvmStatic
   fun {
      val var10000: MapCodec = class_2318.method_54094({ it: class_2251 ->
         INSTANCE
      })
      CODEC = var10000
      INSTANCE.method_9590(
         (INSTANCE.field_10647.method_11664() as class_2680).method_11657(class_2318.field_10927 as class_2769, class_2350.field_11036 as java.lang.Comparable) as class_2680
      )
   }

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class WhenMappings {
      @JvmStatic
      @JvmSynthetic
      public int[] $EnumSwitchMapping$0;

      @JvmStatic
      fun {
         val var0: IntArray = IntArray(class_2350.values().length)

         try {
            var0[class_2350.field_11033.ordinal()] = 1
         } catch (var7: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11043.ordinal()] = 2
         } catch (var6: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11035.ordinal()] = 3
         } catch (var5: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11039.ordinal()] = 4
         } catch (var4: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11034.ordinal()] = 5
         } catch (var3: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11036.ordinal()] = 6
         } catch (var2: NoSuchFieldError) {
         }

         $EnumSwitchMapping$0 = var0
      }
   }
}
