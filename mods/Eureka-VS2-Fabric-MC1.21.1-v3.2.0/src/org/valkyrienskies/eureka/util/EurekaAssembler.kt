package org.valkyrienskies.eureka.util

import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashMap
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_124
import net.minecraft.class_1657
import net.minecraft.class_1661
import net.minecraft.class_1747
import net.minecraft.class_1767
import net.minecraft.class_1792
import net.minecraft.class_1799
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2561
import net.minecraft.class_2596
import net.minecraft.class_2653
import net.minecraft.class_2680
import net.minecraft.class_3218
import net.minecraft.class_3222
import net.minecraft.class_5250
import net.minecraft.class_7923
import org.valkyrienskies.eureka.EurekaBlocks
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.block.FloaterBlock
import org.valkyrienskies.mod.common.BlockStateInfo

@SourceDebugExtension(["SMAP\nEurekaAssembler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EurekaAssembler.kt\norg/valkyrienskies/eureka/util/EurekaAssembler\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,382:1\n290#1,6:397\n290#1,6:403\n306#1,12:409\n306#1,12:421\n1310#2,2:383\n1872#3,3:385\n1557#3:388\n1628#3,3:389\n1557#3:393\n1628#3,3:394\n1#4:392\n*S KotlinDebug\n*F\n+ 1 EurekaAssembler.kt\norg/valkyrienskies/eureka/util/EurekaAssembler\n*L\n236#1:397,6\n239#1:403,6\n255#1:409,12\n257#1:421,12\n83#1:383,2\n117#1:385,3\n128#1:388\n128#1:389,3\n228#1:393\n228#1:394,3\n*E\n"])
public object EurekaAssembler {
   private const val AFLOAT_ADDED_FACTOR: Double = 1.0
   private const val DRY_ADDED_FACTOR: Double = 7.5
   private const val SAFETY_MARGIN: Int = 1
   private const val GRAVITY_MAGNITUDE: Double = 10.0
   private const val MAX_SOLVE_ITERATIONS: Int = 32

   private fun massOf(state: class_2680): Double {
      val var10000: Pair = BlockStateInfo.INSTANCE.get(state)
      return if (var10000 != null) (var10000.getFirst() as java.lang.Number).doubleValue() else 0.0
   }

   private fun idOf(block: class_2248): String {
      val var10000: java.lang.String = class_7923.field_41175.method_10221(block).toString()
      return var10000
   }

   private fun dyeColorOf(block: class_2248): class_1767? {
      val path: java.lang.String = class_7923.field_41175.method_10221(block).method_12832()
      val `$this$firstOrNull$iv`: Array<Any> = class_1767.values()
      var var5: Int = 0
      val var6: Int = `$this$firstOrNull$iv`.length

      var var10000: Any
      while (true) {
         if (var5 >= var6) {
            var10000 = null
            break
         }

         val `element$iv`: Any = `$this$firstOrNull$iv`[var5]
         val it: class_1767 = (class_1767)`$this$firstOrNull$iv`[var5]
         if (StringsKt.startsWith$default(path, "${it.method_15434()}_", false, 2, null)) {
            var10000 = `element$iv`
            break
         }

         var5++
      }

      return (class_1767)var10000
   }

   private fun balloonBlockFor(color: class_1767?): class_2248 {
      var var10000: class_2248
      when (if (color == null) -1 else EurekaAssembler.WhenMappings.$EnumSwitchMapping$0[color.ordinal()]) {
         -1 -> var10000 = EurekaBlocks.INSTANCE.BALLOON.get()
         0 -> throw NoWhenBranchMatchedException()
         1 -> var10000 = EurekaBlocks.INSTANCE.WHITE_BALLOON.get()
         2 -> var10000 = EurekaBlocks.INSTANCE.ORANGE_BALLOON.get()
         3 -> var10000 = EurekaBlocks.INSTANCE.MAGENTA_BALLOON.get()
         4 -> var10000 = EurekaBlocks.INSTANCE.LIGHT_BLUE_BALLOON.get()
         5 -> var10000 = EurekaBlocks.INSTANCE.YELLOW_BALLOON.get()
         6 -> var10000 = EurekaBlocks.INSTANCE.LIME_BALLOON.get()
         7 -> var10000 = EurekaBlocks.INSTANCE.PINK_BALLOON.get()
         8 -> var10000 = EurekaBlocks.INSTANCE.GRAY_BALLOON.get()
         9 -> var10000 = EurekaBlocks.INSTANCE.LIGHT_GRAY_BALLOON.get()
         10 -> var10000 = EurekaBlocks.INSTANCE.CYAN_BALLOON.get()
         11 -> var10000 = EurekaBlocks.INSTANCE.PURPLE_BALLOON.get()
         12 -> var10000 = EurekaBlocks.INSTANCE.BLUE_BALLOON.get()
         13 -> var10000 = EurekaBlocks.INSTANCE.BROWN_BALLOON.get()
         14 -> var10000 = EurekaBlocks.INSTANCE.GREEN_BALLOON.get()
         15 -> var10000 = EurekaBlocks.INSTANCE.RED_BALLOON.get()
         16 -> var10000 = EurekaBlocks.INSTANCE.BLACK_BALLOON.get()
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   private fun candidates(level: class_3218, positions: Set<class_2338>, whitelist: List<String>): List<
         org.valkyrienskies.eureka.util.EurekaAssembler.Candidate
      > {
      if (whitelist.isEmpty()) {
         return CollectionsKt.emptyList()
      } else {
         val priority: HashMap = HashMap(whitelist.size())
         var ranked: java.lang.Iterable = whitelist
         var `$i$f$map`: Int = 0

         for (`destination$iv$iv` in ranked) {
            val `$i$f$mapTo`: Int = `$i$f$map`++
            if (`$i$f$mapTo` < 0) {
               CollectionsKt.throwIndexOverflow()
            }

            priority.putIfAbsent(`destination$iv$iv` as java.lang.String, `$i$f$mapTo`)
         }

         ranked = ArrayList()

         for (var20 in positions) {
            val var22: class_2680 = level.method_8320(var20)
            val var10002: class_2248 = var22.method_26204()
            val var30: Int = priority.get(this.idOf(var10002)) as Int
            if (var30 != null) {
               val var10001: Int = var30
               val var10005: Double = this.massOf(var22)
               val var10006: class_2248 = var22.method_26204()
               ranked.add(TuplesKt.to(var10001, EurekaAssembler.Candidate(var20, var10005, var10006)))
            }
         }

         CollectionsKt.sortWith(ranked as java.util.List, ComparisonsKt.compareBy(arrayOf({ it: Pair ->
            it.getFirst() as java.lang.Comparable
         }, { it: Pair ->
            (it.getSecond() as EurekaAssembler.Candidate).pos.method_10264() as java.lang.Comparable
         }, { it: Pair ->
            (it.getSecond() as EurekaAssembler.Candidate).pos.method_10263() as java.lang.Comparable
         }, { it: Pair ->
            (it.getSecond() as EurekaAssembler.Candidate).pos.method_10260() as java.lang.Comparable
         })))
         val var23: java.lang.Iterable = ranked
         val var25: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(ranked, 10))

         for (var28 in var23) {
            var25.add((var28 as Pair).getSecond() as EurekaAssembler.Candidate)
         }

         return var25 as MutableList<EurekaAssembler.Candidate>
      }
   }

   public fun apply(
      level: class_3218,
      player: class_1657,
      positions: Set<class_2338>,
      floaterMode: Boolean,
      balloonMode: Boolean,
      existingFloaters: Int,
      existingBalloons: Int,
      floaterBonusPercent: Int = 0,
      balloonBonusPercent: Int = 0
   ): org.valkyrienskies.eureka.util.EurekaAssembler.Outcome {
      val cfg: EurekaConfig.Server = EurekaConfig.SERVER
      val floaterState: class_2680 = EurekaBlocks.INSTANCE.FLOATER.get().method_9564()
      val balloonState: class_2680 = EurekaBlocks.INSTANCE.BALLOON.get().method_9564()
      val floaterMass: Double = this.massOf(floaterState)
      val balloonMass: Double = this.massOf(balloonState)
      val floaterCandidates: java.util.List = if (floaterMode)
         this.candidates(level, positions, cfg.assemblerFloaterReplaceWhitelist)
         else
         CollectionsKt.emptyList()
         val balloonCandidates: java.util.List = if (balloonMode)
         this.candidates(level, positions, cfg.assemblerBalloonReplaceWhitelist)
         else
         CollectionsKt.emptyList()
         val liftPerBalloon: java.lang.Iterable = positions
      var var22: Double = 0.0

      for (floaterBonusMul in liftPerBalloon) {
         val it: class_2338 = floaterBonusMul as class_2338
         val var10000: EurekaAssembler = INSTANCE
         val var10001: class_2680 = level.method_8320(it)
         var22 += var10000.massOf(var10001)
      }

      val baseMass: Double = var22
      val var59: Double = cfg.massPerBalloon * cfg.balloonLiftMultiplier
      val balloonAscendFactor: Double = 1.0 + Math.max(0.0, cfg.assemblerBalloonAscendRate) / 10.0
      val var60: Double = 1.0 + Math.max(0, floaterBonusPercent) / 100.0
      val var61: Double = 1.0 + Math.max(0, balloonBonusPercent) / 100.0
      var placeF: Int = 0
      var placeB: Int = 0
      var targetF: Int = 0
      var targetB: Int = 0
      var needF: Int = 32

      repeat(needF) { needB ->
         var inv: java.lang.Iterable = CollectionsKt.take(floaterCandidates, placeF)
         var changedSlots: Double = 0.0

         for (target in inv) {
            changedSlots += floaterMass - (target as EurekaAssembler.Candidate).mass
         }

         val var138: Double = baseMass + changedSlots
         inv = CollectionsKt.take(balloonCandidates, placeB)
         changedSlots = 0.0

         for (var96 in inv) {
            changedSlots += balloonMass - (var96 as EurekaAssembler.Candidate).mass
         }

         val `i$iv`: Double = var138 + changedSlots
         if (!(var138 + changedSlots <= 0.0)) {
            targetB = if (balloonMode && var59 > 0.0) (int)Math.ceil(`i$iv` * balloonAscendFactor / var59 * var61) else 0
            val `stack$iv`: Double = Math.min(cfg.floaterBuoyantFactorPerKg / `i$iv`, 15.0 * cfg.maxFloaterBuoyantFactor)
            targetF = if (floaterMode && `stack$iv` > 0.0)
               (int)Math.ceil((double)Math.max((int)Math.ceil(7.5 / `stack$iv`), (int)Math.ceil(1.0 / `stack$iv`) + 1) * var60)
               else
               0
               val var54: Int = Math.min(Math.max(0, targetF - existingFloaters), floaterCandidates.size())
            val var88: Int = Math.min(Math.max(0, targetB - existingBalloons), balloonCandidates.size())
            if (var54 != placeF || var88 != placeB) {
               placeF = var54
               placeB = var88
            }
         }
      }

      needF = Math.max(0, targetF - existingFloaters)
      val var63: Int = Math.max(0, targetB - existingBalloons)
      if (needF <= floaterCandidates.size() && var63 <= balloonCandidates.size()) {
         val var64: java.lang.Iterable = CollectionsKt.take(balloonCandidates, var63)
         val balloonShort: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var64, 10))

         for (var89 in var64) {
            balloonShort.add(
               TuplesKt.to((var89 as EurekaAssembler.Candidate).pos, INSTANCE.balloonBlockFor(INSTANCE.dyeColorOf((var89 as EurekaAssembler.Candidate).block)))
            )
         }

         val balloonPlacements: java.util.List = balloonShort as java.util.List
         val var65: LinkedHashMap = LinkedHashMap()
         val var68: java.util.Iterator = balloonPlacements.iterator()

         while (var68.hasNext()) {
            var65.merge((var68.next() as Pair).component2() as class_2248, 1, { p0: Any, p1: Any ->
               `$tmp0`(p0, p1) as Int
            })
         }

         val var69: class_1661 = player.method_31548()
         val var139: Int
         if (floaterMode) {
            val var77: class_1661 = var69
            var var90: Int = 0
            var var98: Int = 0

            for (var105 in var69.method_5439()..var98) {
               val var112: class_1799 = var77.method_5438(var98)
               if (!var112.method_7960()) {
                  if (INSTANCE.isFloaterItem(var112)) {
                     var90 += var112.method_7947()
                  }
               }
            }

            var139 = var90
         } else {
            var139 = 0
         }

         val var74: LinkedHashMap = LinkedHashMap()

         for (var83 in var65.entrySet()) {
            val var91: class_2248 = var83.getKey() as class_2248
            val var99: Int = (var83.getValue() as java.lang.Number).intValue()
            val var114: class_1661 = var69
            var var123: Int = 0
            var var127: Int = 0

            for (var51 in var69.method_5439()..var127) {
               val var131: class_1799 = var114.method_5438(var127)
               if (!var131.method_7960()) {
                  if (INSTANCE.isSpecificBalloonItem(var131, var91)) {
                     var123 += var131.method_7947()
                  }
               }
            }

            if (var99 > var123) {
               var74.put(var91, TuplesKt.to(var99, var123))
            }
         }

         if ((!floaterMode || needF <= var139) && var74.isEmpty()) {
            repeat(needF) { var79 ->
               level.method_8652((floaterCandidates.get(var79) as EurekaAssembler.Candidate).pos, floaterState, 2)
            }

            for (var84 in balloonPlacements) {
               level.method_8652(var84.component1() as class_2338, (var84.component2() as class_2248).method_9564(), 2)
            }

            val var81: ArrayList = ArrayList(needF + var63)
            if (needF > 0) {
               val var93: class_1661 = var69
               var var107: Int = needF
               var var113: Int = 0

               // $VF: Unable to resugar Kotlin loop from Java for loop
               val var115: Int = var69.method_5439()
               while (true) {
                  if (var113 < var115 && var107 > 0) break
                  val var120: class_1799 = var93.method_5438(var113)
                  if (!var120.method_7960()) {
                     if (INSTANCE.isFloaterItem(var120)) {
                        val var125: Int = Math.min(var107, var120.method_7947())
                        var120.method_7934(var125)
                        var107 -= var125
                        var81.add(var113)
                     }
                  }

                  var113++
               }
            }

            for (var94 in var65.entrySet()) {
               val var102: class_2248 = var94.getKey() as class_2248
               val var108: Int = (var94.getValue() as java.lang.Number).intValue()
               val var116: class_1661 = var69
               var var126: Int = var108
               var var129: Int = 0

               // $VF: Unable to resugar Kotlin loop from Java for loop
               val var130: Int = var69.method_5439()
               while (true) {
                  if (var129 < var130 && var126 > 0) break
                  val var132: class_1799 = var116.method_5438(var129)
                  if (!var132.method_7960()) {
                     if (INSTANCE.isSpecificBalloonItem(var132, var102)) {
                        val var134: Int = Math.min(var126, var132.method_7947())
                        var132.method_7934(var134)
                        var126 -= var134
                        var81.add(var129)
                     }
                  }

                  var129++
               }
            }

            if (!var81.isEmpty() && player is class_3222) {
               player.field_7498.method_7623()
               val var140: java.util.Iterator = var81.iterator()
               val var86: java.util.Iterator = var140

               while (var86.hasNext()) {
                  val var141: Any = var86.next()
                  val var95: Int = (var141 as java.lang.Number).intValue()
                  (player as class_3222).field_13987.method_14364(class_2653(-2, 0, var95, var69.method_5438(var95)) as class_2596)
               }
            }

            return EurekaAssembler.Applied(needF, var63)
         } else {
            return EurekaAssembler.Cancelled(this.inventoryShortfall(floaterMode, needF, var139, var74))
         }
      } else {
         return EurekaAssembler.Cancelled(this.blockShortfall(floaterMode, balloonMode, needF, floaterCandidates.size(), var63, balloonCandidates.size()))
      }
   }

   private fun isFloaterItem(stack: class_1799): Boolean {
      val item: class_1792 = stack.method_7909()
      return item is class_1747 && (item as class_1747).method_7711() is FloaterBlock
   }

   private fun isSpecificBalloonItem(stack: class_1799, block: class_2248): Boolean {
      val item: class_1792 = stack.method_7909()
      return item is class_1747 && (item as class_1747).method_7711() === block
   }

   private inline fun countMatching(inv: class_1661, predicate: (class_1799) -> Boolean): Int {
      var count: Int = 0
      var i: Int = 0

      for (var6 in inv.method_5439()..i) {
         val stack: class_1799 = inv.method_5438(i)
         if (!stack.method_7960()) {
            if (predicate(stack) as java.lang.Boolean) {
               count += stack.method_7947()
            }
         }
      }

      return count
   }

   private inline fun consumeMatching(inv: class_1661, amount: Int, changedSlots: MutableList<Int>, predicate: (class_1799) -> Boolean) {
      var remaining: Int = amount
      var i: Int = 0

      // $VF: Unable to resugar Kotlin loop from Java for loop
      val var8: Int = inv.method_5439()
      while (true) {
         if (i < var8 && remaining > 0) break
         val stack: class_1799 = inv.method_5438(i)
         if (!stack.method_7960()) {
            if (predicate(stack) as java.lang.Boolean) {
               val take: Int = Math.min(remaining, stack.method_7947())
               stack.method_7934(take)
               remaining -= take
               changedSlots.add(i)
            }
         }

         i++
      }
   }

   private fun newline(): class_5250 {
      return class_2561.method_43470("\n")
   }

   private fun blockShortfall(floaterMode: Boolean, balloonMode: Boolean, needF: Int, availF: Int, needB: Int, availB: Int): class_2561 {
      val msg: class_5250 = class_2561.method_43470("Eureka Assembler -- can't assemble this ship")
         .method_27695(arrayOf(class_124.field_1061, class_124.field_1067))
         if (floaterMode && needF > availF) {
         msg.method_10852(this.newline() as class_2561)
         msg.method_10852(
            class_2561.method_43470("Needs $needF floaters, but only $availF convertible plank/log blocks in the hull").method_27692(class_124.field_1054) as class_2561
         )
      }

      if (balloonMode && needB > availB) {
         msg.method_10852(this.newline() as class_2561)
         msg.method_10852(
            class_2561.method_43470("Needs $needB balloons, but only $availB convertible wool/concrete blocks in the hull").method_27692(class_124.field_1054) as class_2561
         )
      }

      msg.method_10852(this.newline() as class_2561)
      msg.method_10852(
         class_2561.method_43470("Build the hull with more of those blocks, or turn it off with /vs eureka-assembler.")
            .method_27695(arrayOf(class_124.field_1080, class_124.field_1056)) as class_2561
      )
      return msg as class_2561
   }

   private fun inventoryShortfall(floaterMode: Boolean, needF: Int, haveF: Int, balloonShort: Map<class_2248, Pair<Int, Int>>): class_2561 {
      val msg: class_5250 = class_2561.method_43470("Eureka Assembler -- missing materials").method_27695(arrayOf(class_124.field_1061, class_124.field_1067))
      if (floaterMode && needF > haveF) {
         msg.method_10852(this.newline() as class_2561)
         msg.method_10852(class_2561.method_43470("Need $needF floaters (have $haveF)").method_27692(class_124.field_1054) as class_2561)
      }

      for (var7 in balloonShort.entrySet()) {
         val block: class_2248 = var7.getKey() as class_2248
         val nh: Pair = var7.getValue() as Pair
         msg.method_10852(this.newline() as class_2561)
         msg.method_10852(
            class_2561.method_43470("Need ${nh.getFirst()} ${class_7923.field_41175.method_10221(block).method_12832()} (have ${nh.getSecond()})")
               .method_27692(class_124.field_1054) as class_2561
         )
      }

      msg.method_10852(this.newline() as class_2561)
      msg.method_10852(
         class_2561.method_43470("Put these in your inventory, or turn it off with /vs eureka-assembler <floater|balloon> false.")
            .method_27695(arrayOf(class_124.field_1080, class_124.field_1056)) as class_2561
      )
      return msg as class_2561
   }

   public class Applied(floatersPlaced: Int, balloonsPlaced: Int) : EurekaAssembler.Outcome {
      public final val floatersPlaced: Int
      public final val balloonsPlaced: Int

      init {
         this.floatersPlaced = floatersPlaced
         this.balloonsPlaced = balloonsPlaced
      }
   }

   public class Cancelled(message: class_2561) : EurekaAssembler.Outcome {
      public final val message: class_2561

      init {
         this.message = message
      }
   }

   private class Candidate(pos: class_2338, mass: Double, block: class_2248) {
      public final val pos: class_2338
      public final val mass: Double
      public final val block: class_2248

      init {
         this.pos = pos
         this.mass = mass
         this.block = block
      }
   }

   public sealed interface Outcome

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class WhenMappings {
      @JvmStatic
      @JvmSynthetic
      public int[] $EnumSwitchMapping$0;

      @JvmStatic
      fun {
         val var0: IntArray = IntArray(class_1767.values().length)

         try {
            var0[class_1767.field_7952.ordinal()] = 1
         } catch (var17: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7946.ordinal()] = 2
         } catch (var16: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7958.ordinal()] = 3
         } catch (var15: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7951.ordinal()] = 4
         } catch (var14: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7947.ordinal()] = 5
         } catch (var13: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7961.ordinal()] = 6
         } catch (var12: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7954.ordinal()] = 7
         } catch (var11: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7944.ordinal()] = 8
         } catch (var10: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7967.ordinal()] = 9
         } catch (var9: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7955.ordinal()] = 10
         } catch (var8: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7945.ordinal()] = 11
         } catch (var7: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7966.ordinal()] = 12
         } catch (var6: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7957.ordinal()] = 13
         } catch (var5: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7942.ordinal()] = 14
         } catch (var4: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7964.ordinal()] = 15
         } catch (var3: NoSuchFieldError) {
         }

         try {
            var0[class_1767.field_7963.ordinal()] = 16
         } catch (var2: NoSuchFieldError) {
         }

         $EnumSwitchMapping$0 = var0
      }
   }
}
