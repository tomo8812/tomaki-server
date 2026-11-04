package org.valkyrienskies.eureka.blockentity.renderer

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
import java.util.LinkedHashMap
import java.util.function.Function
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1087
import net.minecraft.class_1920
import net.minecraft.class_1921
import net.minecraft.class_1937
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2586
import net.minecraft.class_2680
import net.minecraft.class_2688
import net.minecraft.class_2754
import net.minecraft.class_310
import net.minecraft.class_4587
import net.minecraft.class_4597
import net.minecraft.class_5819
import net.minecraft.class_778
import org.valkyrienskies.eureka.block.IWoodType
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.block.WoodType

@SourceDebugExtension(["SMAP\nWheelModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WheelModel.kt\norg/valkyrienskies/eureka/blockentity/renderer/WheelModels\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,77:1\n1863#2,2:78\n1279#2,2:80\n1293#2,4:82\n*S KotlinDebug\n*F\n+ 1 WheelModel.kt\norg/valkyrienskies/eureka/blockentity/renderer/WheelModels\n*L\n64#1:78,2\n29#1:80,2\n29#1:82,4\n*E\n"])
public object WheelModels {
   private final val property: class_2754<WoodType> = class_2754.method_11850("wood", WoodType.class)
   private final val random: class_5819 = class_5819.method_43047()

   private final val models: Map<WoodType, org.valkyrienskies.eureka.blockentity.renderer.WheelModels.WheelModel> by LazyKt.lazy({ 
      val var10000: java.util.Collection = property.method_11898()
      val `$this$associateWith$iv`: java.lang.Iterable = var10000
      val `result$iv`: LinkedHashMap = LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(var10000, 10)), 16))

      for (`element$iv$iv` in `$this$associateWith$iv`) {
         val var12: java.util.Map = `result$iv`
         val it: WoodType = `element$iv$iv` as WoodType
         var12.put(`element$iv$iv`, WheelModels.WheelModel(it))
      }

      `result$iv` as java.util.Map
   })
      private final get() {
         return models$delegate.getValue() as MutableMap<WoodType, WheelModels.WheelModel>
      }


   private final val mc: class_310
      private final get() {
         return class_310.method_1551()
      }


   public fun render(matrixStack: class_4587, blockEntity: class_2586, buffer: class_4597, combinedLight: Int, combinedOverlay: Int) {
      val var10000: class_1937 = blockEntity.method_10997()
      if (var10000 != null) {
         val blockState: class_2680 = blockEntity.method_11010()
         val var10: class_2248 = blockState.method_26204()
         val woodType: IWoodType = (var10 as ShipHelmBlock).woodType
         matrixStack.method_22903()
         matrixStack.method_22904(-0.5, -0.625, -0.25)
         val blockPos: class_2338 = blockEntity.method_11016()
         val var11: class_778 = this.mc.method_1541().method_3350()
         val var10001: class_1920 = var10000 as class_1920
         val var10002: Any = this.models.get(woodType)
         var11.method_3373(
            var10001,
            (var10002 as WheelModels.WheelModel).model,
            blockState,
            blockPos,
            matrixStack,
            buffer.getBuffer(class_1921.method_23581()),
            true,
            random,
            blockState.method_26190(blockPos),
            combinedOverlay
         )
         matrixStack.method_22909()
      }
   }

   public fun setModelGetter(getter: Function<IWoodType, class_1087>) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Anonymous class does not have Class Kotlin metadata
      //   at org.vineflower.kotlin.KotlinWriter.writeClassDefinition(KotlinWriter.java:742)
      //   at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:309)
      //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:178)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:770)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:736)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ldc "getter"
      // 03: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 06: aload 0
      // 07: invokespecial org/valkyrienskies/eureka/blockentity/renderer/WheelModels.getModels ()Ljava/util/Map;
      // 0a: invokeinterface java/util/Map.values ()Ljava/util/Collection; 1
      // 0f: checkcast java/lang/Iterable
      // 12: astore 2
      // 13: bipush 0
      // 14: istore 3
      // 15: aload 2
      // 16: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 1b: astore 4
      // 1d: aload 4
      // 1f: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 24: ifeq 4e
      // 27: aload 4
      // 29: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 2e: astore 5
      // 30: aload 5
      // 32: checkcast org/valkyrienskies/eureka/blockentity/renderer/WheelModels$WheelModel
      // 35: astore 6
      // 37: bipush 0
      // 38: istore 7
      // 3a: aload 6
      // 3c: new org/valkyrienskies/eureka/blockentity/renderer/WheelModels$setModelGetter$1$1
      // 3f: dup
      // 40: aload 1
      // 41: invokespecial org/valkyrienskies/eureka/blockentity/renderer/WheelModels$setModelGetter$1$1.<init> (Ljava/lang/Object;)V
      // 44: checkcast kotlin/jvm/functions/Function1
      // 47: invokevirtual org/valkyrienskies/eureka/blockentity/renderer/WheelModels$WheelModel.setGetter (Lkotlin/jvm/functions/Function1;)V
      // 4a: nop
      // 4b: goto 1d
      // 4e: nop
      // 4f: return
   }

   public class WheelModel(type: WoodType) : class_2688<WheelModels, WheelModels.WheelModel> {
      public final var getter: (IWoodType) -> class_1087

      public final val model: class_1087
         public final get() {
            return this.model$delegate.getValue() as class_1087
         }


      init {
         val var10001: WheelModels = WheelModels.INSTANCE
         val var2: Reference2ObjectArrayMap = Reference2ObjectArrayMap()
         (var2 as java.util.Map).put(WheelModels.property, type)
         super(var10001, var2, null)
         this.getter = <unrepresentable>.INSTANCE
         this.model$delegate = LazyKt.lazy({ 
            `this$0`.getter(`$type`) as class_1087
         })
      }
   }
}
