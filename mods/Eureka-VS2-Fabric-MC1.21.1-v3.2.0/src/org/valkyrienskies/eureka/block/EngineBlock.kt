package org.valkyrienskies.eureka.block

import com.mojang.serialization.MapCodec
import net.minecraft.class_1269
import net.minecraft.class_1297
import net.minecraft.class_1542
import net.minecraft.class_1657
import net.minecraft.class_1750
import net.minecraft.class_1922
import net.minecraft.class_1937
import net.minecraft.class_2237
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2394
import net.minecraft.class_2398
import net.minecraft.class_2464
import net.minecraft.class_2470
import net.minecraft.class_2498
import net.minecraft.class_2586
import net.minecraft.class_2591
import net.minecraft.class_2680
import net.minecraft.class_2741
import net.minecraft.class_2769
import net.minecraft.class_3417
import net.minecraft.class_3419
import net.minecraft.class_3620
import net.minecraft.class_3908
import net.minecraft.class_3965
import net.minecraft.class_5558
import net.minecraft.class_5819
import net.minecraft.class_2350.class_2351
import net.minecraft.class_2689.class_2690
import net.minecraft.class_4970.class_2251
import org.valkyrienskies.eureka.EurekaProperties
import org.valkyrienskies.eureka.blockentity.EngineBlockEntity

public class EngineBlock : class_2237(
      class_2251.method_9637()
         .method_31710(class_3620.field_16023)
         .method_29292()
         .method_9632(3.5F)
         .method_9626(class_2498.field_11544)
         .method_9631(
            { state: class_2680 ->
               if ((state.method_11654(EurekaProperties.INSTANCE.HEAT as class_2769) as java.lang.Number).intValue() > 0)
                  (state.method_11654(EurekaProperties.INSTANCE.HEAT as class_2769) as java.lang.Number).intValue() + 9
                  else
                  0
               }
         )
   ) {
   fun method_10123(blockPos: class_2338, state: class_2680): class_2586 {
      EngineBlockEntity(blockPos, state) as class_2586
   }

   fun method_55766(state: class_2680, level: class_1937, pos: class_2338, player: class_1657, blockHitResult: class_3965): class_1269 {
      if (level.field_9236) {
         class_1269.field_5812
      } else {
         val var10000: class_2586 = level.method_8321(pos)
         player.method_17355((var10000 as EngineBlockEntity) as class_3908)
         class_1269.field_21466
      }
   }

   fun method_9515(builder: class_2690<class_2248, class_2680>) {
      builder.method_11667(arrayOf(class_2741.field_12481)).method_11667(arrayOf(EurekaProperties.INSTANCE.HEAT))
   }

   fun method_9605(ctx: class_1750): class_2680? {
      this.method_9564().method_11657(class_2741.field_12481 as class_2769, ctx.method_8042().method_10153() as java.lang.Comparable) as class_2680
   }

   fun method_9598(state: class_2680, rotation: class_2470): class_2680? {
      val var10001: class_2769 = class_2741.field_12481 as class_2769
      val var10003: java.lang.Comparable = state.method_11654(class_2741.field_12481 as class_2769)
      val var10000: Any = state.method_11657(var10001, rotation.method_10503(var10003 as class_2350) as java.lang.Comparable)
      var10000 as class_2680
   }

   fun method_9604(blockState: class_2680): class_2464 {
      class_2464.field_11458
   }

   fun method_9575(state: class_2680, level: class_1922, pos: class_2338): Float {
      val var10000: Int = state.method_11654(EurekaProperties.INSTANCE.HEAT as class_2769) as Int
      if ((var10000 ?: 0) > 0) 1.0F else super.method_9575(state, level, pos)
   }

   fun method_9496(state: class_2680, level: class_1937, pos: class_2338, randomSource: class_5819) {
      val heat: Int = state.method_11654(EurekaProperties.INSTANCE.HEAT as class_2769) as Int
      if (heat == null || heat != 0) {
         val d: Double = pos.method_10263() + 0.5
         val e: Double = pos.method_10264()
         val f: Double = pos.method_10260() + 0.5
         val var10000: Double = randomSource.method_43058()
         if (var10000 < 0.04 * heat.intValue()) {
            level.method_8486(d, e, f, class_3417.field_15006, class_3419.field_15245, 1.0F, 1.0F, false)
         }

         if (!(randomSource.method_43058() > 0.2 * heat.intValue())) {
            val direction: class_2350 = state.method_11654(class_2741.field_12481 as class_2769) as class_2350
            val axis: class_2351 = direction.method_10166()
            val h: Double = randomSource.method_43058() * 0.6 - 0.3
            val i: Double = if (axis === class_2351.field_11048) direction.method_10148() * 0.52 else h
            val j: Double = randomSource.method_43058() * 4.0 / 16.0
            val k: Double = if (axis === class_2351.field_11051) direction.method_10165() * 0.52 else h
            level.method_8406(class_2398.field_11251 as class_2394, d + i, e + j, f + k, 0.0, 0.0, 0.0)
            level.method_8406(class_2398.field_11240 as class_2394, d + i, e + j, f + k, 0.0, 0.0, 0.0)
         }
      }
   }

   fun <T extends class_2586> method_31645(level: class_1937, state: class_2680, type: class_2591<T>): class_5558<T> {
      { level: class_1937, pos: class_2338, state: class_2680, blockEntity: class_2586 ->
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

   fun method_9576(level: class_1937, pos: class_2338, state: class_2680, player: class_1657): class_2680? {
      if (!level.field_9236) {
         val var10000: class_2586 = level.method_8321(pos)
         val blockEntity: EngineBlockEntity = var10000 as EngineBlockEntity
         if (!(var10000 as EngineBlockEntity).fuel.method_7960()) {
            level.method_8649(
               class_1542(level, (double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), blockEntity.fuel) as class_1297
            )
         }
      }

      super.method_9576(level, pos, state, player)
   }

   fun method_53969(): MapCodec<EngineBlock> {
      CODEC
   }

   @JvmStatic
   fun {
      val var10000: MapCodec = class_2237.method_54094({ it: class_2251 ->
         EngineBlock()
      })
      CODEC = var10000
   }

   public companion object {
      public final val CODEC: MapCodec<EngineBlock>
   }
}
