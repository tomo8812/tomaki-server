package org.valkyrienskies.mod.common.config

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import java.util.ArrayList
import java.util.HashMap
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.jvm.internal.Ref.IntRef
import kotlin.math.MathKt
import kotlin.reflect.KProperty
import net.minecraft.class_1922
import net.minecraft.class_2246
import net.minecraft.class_2338
import net.minecraft.class_2586
import net.minecraft.class_265
import net.minecraft.class_2680
import net.minecraft.class_2960
import net.minecraft.class_3300
import net.minecraft.class_3610
import net.minecraft.class_3611
import net.minecraft.class_3612
import net.minecraft.class_3695
import net.minecraft.class_4309
import net.minecraft.class_7923
import org.apache.logging.log4j.Logger
import org.joml.Vector3d
import org.joml.primitives.AABBi
import org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape
import org.valkyrienskies.core.api.physics.blockstates.CollisionPoint
import org.valkyrienskies.core.api.physics.blockstates.LiquidState
import org.valkyrienskies.core.api.physics.blockstates.SolidBlockShape
import org.valkyrienskies.core.api.physics.blockstates.SolidState
import org.valkyrienskies.core.internal.physics.blockstates.VsiBlockState
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType
import org.valkyrienskies.mod.api_impl.events.RegisterBlockStateEventImpl
import org.valkyrienskies.mod.common.BlockStateInfoProvider
import org.valkyrienskies.mod.mixin.accessors.world.level.block.SlabBlockAccessor
import org.valkyrienskies.mod.mixin.accessors.world.level.block.StairBlockAccessor
import org.valkyrienskies.mod.util.ClassLogger
import org.valkyrienskies.mod.util.LoggingKt

@SourceDebugExtension(["SMAP\nMassDatapackResolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MassDatapackResolver.kt\norg/valkyrienskies/mod/common/config/MassDatapackResolver\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,444:1\n13402#2:445\n13403#2:449\n1872#3,3:446\n1863#3,2:450\n*S KotlinDebug\n*F\n+ 1 MassDatapackResolver.kt\norg/valkyrienskies/mod/common/config/MassDatapackResolver\n*L\n201#1:445\n201#1:449\n206#1:446,3\n368#1:450,2\n*E\n"])
public object MassDatapackResolver : BlockStateInfoProvider {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties = arrayOf(
      Reflection.property1(
         PropertyReference1Impl(MassDatapackResolver.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
      )
   );
   private final val map: HashMap<class_2960, VSBlockStateInfo> = HashMap()
   private final val mcBlockStateToVs: MutableMap<class_2680, VsiBlockState> = HashMap() as java.util.Map
   public final val blockStateData: Collection<VsiBlockState> = mcBlockStateToVs.values()

   public final var registeredBlocks: Boolean
      private set

   private final val logger: Logger by LoggingKt.logger().provideDelegate_j8jxBIw/* $VF was: provideDelegate-j8jxBIw */(INSTANCE, $$delegatedProperties[0])
      private final get() {
         return ClassLogger.getValue_impl/* $VF was: getValue-impl */(logger$delegate, this, $$delegatedProperties[0])
      }


   public final val loader: org.valkyrienskies.mod.common.config.MassDatapackResolver.VSMassDataLoader
      public final get() {
         return MassDatapackResolver.VSMassDataLoader()
      }


   public open val priority: Int
      public open get() {
         return 100
      }


   public override fun getBlockStateMass(blockState: class_2680): Double? {
      val var10000: VSBlockStateInfo = map.get(class_7923.field_41175.method_10221(blockState.method_26204()))
      return if (var10000 != null) var10000.mass else null
   }

   public override fun getBlockStateType(blockState: class_2680): VsiBlockType? {
      val var10000: VsiBlockState = mcBlockStateToVs.get(blockState)
      return if (var10000 == null) null else vsCore.blockTypes.getType(var10000)
   }

   public fun decideDefaultPriority(resourceLocation: class_2960): Int {
      return if (resourceLocation.method_12836().equals("valkyrienskies")) 50 else (if (resourceLocation.method_12836().equals("custom")) 1000 else 100)
   }

   @JvmStatic
   public fun generateStairCollisionShapes(stairShapes: Array<class_265>): Map<class_265, SolidBlockShape> {
      val testPoints: java.util.List = CollectionsKt.listOf(
         arrayOf(
            CollisionPoint(0.25F, 0.25F, 0.25F, 0.25F),
            CollisionPoint(0.25F, 0.25F, 0.75F, 0.25F),
            CollisionPoint(0.25F, 0.75F, 0.25F, 0.25F),
            CollisionPoint(0.25F, 0.75F, 0.75F, 0.25F),
            CollisionPoint(0.75F, 0.25F, 0.25F, 0.25F),
            CollisionPoint(0.75F, 0.25F, 0.75F, 0.25F),
            CollisionPoint(0.75F, 0.75F, 0.25F, 0.25F),
            CollisionPoint(0.75F, 0.75F, 0.75F, 0.25F)
         )
      )
      val var24: java.util.List = CollectionsKt.listOf(
         arrayOf(
            AABBi(0, 0, 0, 7, 7, 7),
            AABBi(0, 0, 8, 7, 7, 15),
            AABBi(0, 8, 0, 7, 15, 7),
            AABBi(0, 8, 8, 7, 15, 15),
            AABBi(8, 0, 0, 15, 7, 7),
            AABBi(8, 0, 8, 15, 7, 15),
            AABBi(8, 8, 0, 15, 15, 7),
            AABBi(8, 8, 8, 15, 15, 15)
         )
      )
      val var25: java.util.Map = HashMap()

      for (`element$iv` in stairShapes) {
         val stairShape: class_265 = (class_265)`element$iv`
         val points: java.util.List = ArrayList()
         val positiveBoxes: java.util.List = ArrayList()
         val negativeBoxes: java.util.List = ArrayList()
         val collisionShape: java.lang.Iterable = testPoints
         var `index$iv`: Int = 0

         for (`item$iv` in collisionShape) {
            val var19: Int = `index$iv`++
            if (var19 < 0) {
               CollectionsKt.throwIndexOverflow()
            }

            val testPoint: CollisionPoint = `item$iv` as CollisionPoint
            val added: BooleanRef = BooleanRef()
            stairShape.method_1089(
               { minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double ->
                  // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
                  // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.exps.Exprent.toJava(int)" because the return value of "org.vineflower.kotlin.expr.KExitExprent.getValue()" is null
                  //   at org.vineflower.kotlin.expr.KExitExprent.toJava(KExitExprent.java:41)
                  //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
                  //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
               }
            )
            if (added.element) {
               positiveBoxes.add(var24.get(var19))
            } else {
               negativeBoxes.add(var24.get(var19))
            }
         }

         var25.put(
            stairShape,
            vsCore
               .newSolidStateBoxesShapeBuilder()
               .addCollisionPoints(points)
               .addPositiveBoxes(vsCore.solidShapeUtils.mergeBoxes(positiveBoxes))
               .addNegativeBoxes(vsCore.solidShapeUtils.mergeBoxes(negativeBoxes))
               .build()
         )
      }

      return var25
   }

   @JvmStatic
   public fun generateShapeFromVoxel(voxelShape: class_265): BoxesBlockShape? {
      val posBoxes: ArrayList = ArrayList()
      val failed: BooleanRef = BooleanRef()
      val maxBoxesToTest: IntRef = IntRef()
      maxBoxesToTest.element = 20
      voxelShape.method_1089(
         { minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double ->
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
            //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
         }
      )
      val var10000: BoxesBlockShape
      if (!failed.element) {
         var var4: BoxesBlockShape
         try {
            var4 = vsCore.solidShapeUtils.generateShapeFromBoxes(posBoxes)
         } catch (var6: IllegalArgumentException) {
            System.out.println((Object)("WTF ERROR WHILE PROCESSING $voxelShape"))
            var4 = null
         }

         var10000 = var4
      } else {
         var10000 = null
      }

      return var10000
   }

   public fun registerAllBlockStates(blockStates: Iterable<class_2680>) {
      val var23: BoxesBlockShape = vsCore
         .newSolidStateBoxesShapeBuilder()
         .addCollisionPoints(
            CollectionsKt.listOf(
               arrayOf(
                  CollisionPoint(0.25F, 0.25F, 0.25F, 0.25F),
                  CollisionPoint(0.25F, 0.25F, 0.75F, 0.25F),
                  CollisionPoint(0.25F, 0.75F, 0.25F, 0.25F),
                  CollisionPoint(0.25F, 0.75F, 0.75F, 0.25F),
                  CollisionPoint(0.75F, 0.25F, 0.25F, 0.25F),
                  CollisionPoint(0.75F, 0.25F, 0.75F, 0.25F),
                  CollisionPoint(0.75F, 0.75F, 0.25F, 0.25F),
                  CollisionPoint(0.75F, 0.75F, 0.75F, 0.25F)
               )
            )
         )
         .addPositiveBox(AABBi(0, 0, 0, 15, 15, 15))
         .build()
         val dummyBlockGetter: <unrepresentable> = object : class_1922 {
         fun method_31605(): Int {
            255
         }

         fun method_31607(): Int {
            0
         }

         fun method_8321(blockPos: class_2338): class_2586 {
            null
         }

         fun method_8320(blockPos: class_2338): class_2680 {
            val var10000: class_2680 = class_2246.field_10243.method_9564()
            var10000
         }

         fun method_8316(blockPos: class_2338): class_3610 {
            val var10000: class_3610 = class_3612.field_15906.method_15785()
            var10000
         }
      }
      var var10000: SolidBlockShape = StairBlockAccessor.getTopShapes()
      var10000 = var10000 as Array<Any>
      val var10001: Array<class_265> = StairBlockAccessor.getBottomShapes()
      val voxelShapeToCollisionShapeMap: java.util.Map = generateStairCollisionShapes(
         ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(var10000, var10001), SlabBlockAccessor.getBottomAABB()), SlabBlockAccessor.getTopAABB()) as Array<class_265>
      )
      val generatedCollisionShapesMap: HashMap = HashMap()
      val liquidMaterialToDensityMap: HashMap = MapsKt.hashMapOf(
         arrayOf(
            TuplesKt.to(class_3612.field_15910, Pair(1000.0, 0.3)),
            TuplesKt.to(class_3612.field_15908, Pair(10000.0, 1.0)),
            TuplesKt.to(class_3612.field_15909, Pair(1000.0, 0.3)),
            TuplesKt.to(class_3612.field_15907, Pair(10000.0, 1.0))
         )
      )
      val var24: HashMap = HashMap()

      for (`element$iv` in blockStates) {
         val blockState: class_2680 = `element$iv` as class_2680
         val var25: VsiBlockState
         if ((`element$iv` as class_2680).method_26215()) {
            var25 = vsCore.blockTypes.airState
         } else {
            val var29: VsiBlockState
            if (!blockState.method_51176()
               && (blockState.method_26227().method_15769() || !blockState.method_26220(dummyBlockGetter, class_2338.field_10980).method_1110())) {
               val var26: class_265
               if (blockState.method_51367()) {
                  var26 = blockState.method_26218(dummyBlockGetter, class_2338.field_10980)
               } else {
                  var26 = blockState.method_26220(dummyBlockGetter, class_2338.field_10980)
               }

               if (voxelShapeToCollisionShapeMap.containsKey(var26)) {
                  var10000 = (SolidBlockShape)voxelShapeToCollisionShapeMap.get(var26)
                  var10000 = var10000
               } else if (generatedCollisionShapesMap.containsKey(var26)) {
                  if (generatedCollisionShapesMap.get(var26) != null) {
                     var10000 = (SolidBlockShape)generatedCollisionShapesMap.get(var26)
                     var10000 = var10000
                  } else {
                     var10000 = var23
                  }
               } else {
                  val vsBlockStateInfo: BoxesBlockShape = generateShapeFromVoxel(var26)
                  generatedCollisionShapesMap.put(var26, vsBlockStateInfo)
                  var10000 = vsBlockStateInfo
                  if (vsBlockStateInfo == null) {
                     var10000 = var23
                  }

                  var10000 = var10000
               }

               var collisionShape: SolidBlockShape
               var var27: VSBlockStateInfo
               run label102@{
                  collisionShape = var10000
                  var27 = map.get(class_7923.field_41175.method_10221(blockState.method_26204()))
                  if (var27 != null) {
                     val var34: java.lang.Boolean = var27.noCollisionOverride
                     if (var34 != null) {
                        var35 = var34
                        return@label102
                     }
                  }

                  var35 = false
               }

               if (var35) {
                  var10000 = vsCore.solidShapeUtils.generateShapeFromBoxes(ArrayList<>())
                  collisionShape = var10000
               }

               val solidState: SolidState = vsCore
                  .newSolidStateBuilder()
                  .shape(collisionShape)
                  .elasticity(if (var27 != null) var27.elasticity else VSGameConfig.SERVER.defaultBlockElasticity)
                  .friction(if (var27 != null) var27.friction else VSGameConfig.SERVER.defaultBlockFriction)
                  .hardness(VSGameConfig.SERVER.defaultBlockHardness)
                  .build()
                  val var37: LiquidState
               if (!blockState.method_26227().method_15769()) {
                  val var10002: class_3610 = blockState.method_26227()
                  var37 = registerAllBlockStates$getFluidState$default(var24, liquidMaterialToDensityMap, var10002, null, false, 16, null)
               } else {
                  var37 = null
               }

               var29 = VsiBlockState(solidState, var37)
            } else {
               val var10005: class_3610 = blockState.method_26227()
               var29 = VsiBlockState(
                  null,
                  registerAllBlockStates$getFluidState(
                     var24, liquidMaterialToDensityMap, var10005, map.get(class_7923.field_41175.method_10221(blockState.method_26204())), true
                  )
               )
            }

            var25 = var29
         }

         mcBlockStateToVs.put(blockState, var25)
      }

      this.runRegisterBlockStateEvent()
      registeredBlocks = true
   }

   private fun runRegisterBlockStateEvent() {
      val event: RegisterBlockStateEventImpl = RegisterBlockStateEventImpl()
      api.registerBlockStateEvent.emit(event)
      MapsKt.putAll(mcBlockStateToVs, event.toRegister)
   }

   @JvmStatic
   fun `registerAllBlockStates$getFluidState`(
      fluidStateToBlockTypeMap: HashMap<class_3610, LiquidState>,
      liquidMaterialToDensityMap: HashMap<class_3611, Pair<java.lang.Double, java.lang.Double>>,
      fluidState: class_3610,
      blockStateInfo: VSBlockStateInfo,
      isLiquid: Boolean
   ): LiquidState {
      val cached: LiquidState = fluidStateToBlockTypeMap.get(fluidState) as LiquidState
      if (cached != null) {
         cached
      } else {
         val fluidBox: AABBi = AABBi(
            0, 0, 0, 15, if (fluidState.method_15771()) 15 else RangesKt.coerceIn(MathKt.roundToInt((double)fluidState.method_20785() * 16.0) - 1, 0, 15), 15
         )
         val var16: LiquidState
         if (liquidMaterialToDensityMap.containsKey(fluidState.method_15772())) {
            val var10000: Any = liquidMaterialToDensityMap.get(fluidState.method_15772())
            var16 = vsCore
               .newLiquidStateBuilder()
               .boxShape(fluidBox)
               .density(((var10000 as Pair).component1() as java.lang.Number).doubleValue())
               .dragCoefficient(((var10000 as Pair).component2() as java.lang.Number).doubleValue())
               .velocity(Vector3d())
               .build()
            } else if (isLiquid) {
            val var14: Double = if (blockStateInfo != null) blockStateInfo.mass else VSGameConfig.SERVER.defaultBlockMass
            val dragCoefficient: Double = if (blockStateInfo != null) blockStateInfo.friction else VSGameConfig.SERVER.defaultBlockFriction
            val newFluidBlockState: LiquidState = vsCore
               .newLiquidStateBuilder()
               .boxShape(fluidBox)
               .density(var14)
               .dragCoefficient(dragCoefficient)
               .velocity(Vector3d())
               .build()
               liquidMaterialToDensityMap.put(fluidState.method_15772(), Pair(var14, dragCoefficient))
            var16 = newFluidBlockState
         } else {
            val var17: LiquidState.Builder = vsCore.newLiquidStateBuilder().boxShape(fluidBox).density(VSGameConfig.SERVER.defaultBlockMass)
            val var10001: Any = liquidMaterialToDensityMap.get(class_3612.field_15910)
            var16 = var17.dragCoefficient(((var10001 as Pair).getSecond() as java.lang.Number).doubleValue()).velocity(Vector3d()).build()
         }

         var16
      }
   }

   @JvmStatic
   @JvmSynthetic
   fun `registerAllBlockStates$getFluidState$default`(
      var0: HashMap, var1: HashMap, var2: class_3610, var3: VSBlockStateInfo, var4: Boolean, var5: Int, var6: Any
   ): LiquidState {
      if ((var5 and 16) != 0) {
         var4 = false
      }

      registerAllBlockStates$getFluidState(var0, var1, var2, var3, var4)
   }

   @SourceDebugExtension(["SMAP\nMassDatapackResolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MassDatapackResolver.kt\norg/valkyrienskies/mod/common/config/MassDatapackResolver$VSMassDataLoader\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,444:1\n216#2:445\n217#2:448\n1863#3,2:446\n1863#3:449\n1863#3,2:450\n1864#3:452\n*S KotlinDebug\n*F\n+ 1 MassDatapackResolver.kt\norg/valkyrienskies/mod/common/config/MassDatapackResolver$VSMassDataLoader\n*L\n85#1:445\n85#1:448\n88#1:446,2\n102#1:449\n112#1:450,2\n102#1:452\n*E\n"])
   public class VSMassDataLoader : class_4309(Gson(), "vs_mass") {
      private final val tags: MutableList<VSBlockStateInfo> = ArrayList() as java.util.List

      protected open fun apply(objects: MutableMap<class_2960, JsonElement>?, resourceManager: class_3300?, profiler: class_3695?) {
         MassDatapackResolver.map.clear()
         this.tags.clear()
         if (objects != null) {
            for (`element$iv` in objects.entrySet()) {
               val location: class_2960 = `element$iv`.getKey() as class_2960
               val element: JsonElement = `element$iv`.getValue() as JsonElement

               try {
                  if (!element.isJsonArray()) {
                     if (!element.isJsonObject()) {
                        throw IllegalArgumentException()
                     }

                     this.parse(element, location)
                  } else {
                     val var10000: JsonArray = element.getAsJsonArray()

                     for (`element$ivx` in var10000 as java.lang.Iterable) {
                        this.parse(`element$ivx` as JsonElement, location)
                     }
                  }
               } catch (var18: Exception) {
                  MassDatapackResolver.INSTANCE.logger.error(var18)
               }
            }
         }
      }

      private fun addToBeAddedTags(tag: VSBlockStateInfo) {
         this.tags.add(tag)
      }

      private fun add(info: VSBlockStateInfo) {
         if (MassDatapackResolver.map.containsKey(info.id)) {
            val var10000: Any = MassDatapackResolver.map.get(info.id)
            if ((var10000 as VSBlockStateInfo).priority < info.priority) {
               MassDatapackResolver.map.put(info.id, info)
            }
         } else {
            MassDatapackResolver.map.put(info.id, info)
         }
      }

      private fun parse(element: JsonElement, origin: class_2960) {
         var var10000: JsonElement = element.getAsJsonObject().get("tag")
         val tag: java.lang.String = if (var10000 != null) var10000.getAsString() else null
         var10000 = element.getAsJsonObject().get("mass")
         if (var10000 != null) {
            val weight: Double = var10000.getAsDouble()
            var10000 = element.getAsJsonObject().get("friction")
            val friction: Double = if (var10000 != null) var10000.getAsDouble() else VSGameConfig.SERVER.defaultBlockFriction
            var10000 = element.getAsJsonObject().get("elasticity")
            val elasticity: Double = if (var10000 != null) var10000.getAsDouble() else VSGameConfig.SERVER.defaultBlockElasticity
            var10000 = element.getAsJsonObject().get("priority")
            val priority: Int = if (var10000 != null) var10000.getAsInt() else MassDatapackResolver.INSTANCE.decideDefaultPriority(origin)
            var10000 = element.getAsJsonObject().get("no_collision")
            val overrideNoCollision: java.lang.Boolean = if (var10000 != null) var10000.getAsBoolean() else null
            if (tag != null) {
               val var10003: class_2960 = class_2960.method_60654(tag)
               this.addToBeAddedTags(VSBlockStateInfo(var10003, priority, weight, friction, elasticity, null, overrideNoCollision))
            } else {
               var10000 = element.getAsJsonObject().get("block")
               if (var10000 == null) {
                  throw IllegalArgumentException("No block or tag in file $origin")
               }

               val var19: java.lang.String = var10000.getAsString()
               if (var19 == null) {
                  throw IllegalArgumentException("No block or tag in file $origin")
               }

               val var20: class_2960 = class_2960.method_60654(var19)
               this.add(VSBlockStateInfo(var20, priority, weight, friction, elasticity, null, overrideNoCollision))
            }
         } else {
            throw IllegalArgumentException("No mass in file $origin")
         }
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      fun method_18788(`object`: Any, resourceManager: class_3300, profilerFiller: class_3695) {
         this.apply(`object` as MutableMap<class_2960, JsonElement>, resourceManager, profilerFiller)
      }
   }
}
