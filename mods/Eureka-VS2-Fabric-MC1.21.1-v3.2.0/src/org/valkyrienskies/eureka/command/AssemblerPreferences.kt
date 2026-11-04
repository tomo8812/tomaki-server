package org.valkyrienskies.eureka.command

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nAssemblerPreferences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AssemblerPreferences.kt\norg/valkyrienskies/eureka/command/AssemblerPreferences\n+ 2 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,69:1\n72#2,2:70\n72#2,2:73\n72#2,2:77\n72#2,2:81\n72#2,2:84\n1#3:72\n1#3:75\n1#3:76\n1#3:79\n1#3:80\n1#3:83\n1#3:86\n1#3:87\n*S KotlinDebug\n*F\n+ 1 AssemblerPreferences.kt\norg/valkyrienskies/eureka/command/AssemblerPreferences\n*L\n42#1:70,2\n46#1:73,2\n50#1:77,2\n54#1:81,2\n58#1:84,2\n42#1:72\n46#1:75\n50#1:79\n54#1:83\n58#1:86\n*E\n"])
public object AssemblerPreferences {
   public const val MAX_BONUS: Int = 999
   private final val byPlayer: ConcurrentHashMap<UUID, org.valkyrienskies.eureka.command.AssemblerPreferences.Prefs> = ConcurrentHashMap()

   public fun setEnabled(id: UUID, value: Boolean) {
      val `$this$getOrPut$iv`: ConcurrentMap = byPlayer
      var var10000: Any = byPlayer.get(id)
      if (var10000 == null) {
         val `default$iv`: Any = AssemblerPreferences.Prefs(false, false, false, 0, 0, 31, null)
         var10000 = `$this$getOrPut$iv`.putIfAbsent(id, `default$iv`)
         if (var10000 == null) {
            var10000 = `default$iv`
         }
      }

      (var10000 as AssemblerPreferences.Prefs).enabled = value
   }

   public fun setFloater(id: UUID, value: Boolean) {
      val `$this$getOrPut$iv`: ConcurrentMap = byPlayer
      var var10000: Any = byPlayer.get(id)
      if (var10000 == null) {
         val `default$iv`: Any = AssemblerPreferences.Prefs(false, false, false, 0, 0, 31, null)
         var10000 = `$this$getOrPut$iv`.putIfAbsent(id, `default$iv`)
         if (var10000 == null) {
            var10000 = `default$iv`
         }
      }

      val var9: AssemblerPreferences.Prefs = var10000 as AssemblerPreferences.Prefs
      (var10000 as AssemblerPreferences.Prefs).floater = value
      if (value) {
         var9.enabled = true
      }
   }

   public fun setBalloon(id: UUID, value: Boolean) {
      val `$this$getOrPut$iv`: ConcurrentMap = byPlayer
      var var10000: Any = byPlayer.get(id)
      if (var10000 == null) {
         val `default$iv`: Any = AssemblerPreferences.Prefs(false, false, false, 0, 0, 31, null)
         var10000 = `$this$getOrPut$iv`.putIfAbsent(id, `default$iv`)
         if (var10000 == null) {
            var10000 = `default$iv`
         }
      }

      val var9: AssemblerPreferences.Prefs = var10000 as AssemblerPreferences.Prefs
      (var10000 as AssemblerPreferences.Prefs).balloon = value
      if (value) {
         var9.enabled = true
      }
   }

   public fun setFloaterBonus(id: UUID, percent: Int) {
      val `$this$getOrPut$iv`: ConcurrentMap = byPlayer
      var var10000: Any = byPlayer.get(id)
      if (var10000 == null) {
         val `default$iv`: Any = AssemblerPreferences.Prefs(false, false, false, 0, 0, 31, null)
         var10000 = `$this$getOrPut$iv`.putIfAbsent(id, `default$iv`)
         if (var10000 == null) {
            var10000 = `default$iv`
         }
      }

      (var10000 as AssemblerPreferences.Prefs).floaterBonusPercent = RangesKt.coerceIn(percent, 0, 999)
   }

   public fun setBalloonBonus(id: UUID, percent: Int) {
      val `$this$getOrPut$iv`: ConcurrentMap = byPlayer
      var var10000: Any = byPlayer.get(id)
      if (var10000 == null) {
         val `default$iv`: Any = AssemblerPreferences.Prefs(false, false, false, 0, 0, 31, null)
         var10000 = `$this$getOrPut$iv`.putIfAbsent(id, `default$iv`)
         if (var10000 == null) {
            var10000 = `default$iv`
         }
      }

      (var10000 as AssemblerPreferences.Prefs).balloonBonusPercent = RangesKt.coerceIn(percent, 0, 999)
   }

   public fun clearBonuses(id: UUID) {
      val var10000: AssemblerPreferences.Prefs = byPlayer.get(id)
      if (var10000 != null) {
         var10000.floaterBonusPercent = 0
         var10000.balloonBonusPercent = 0
      }
   }

   public fun get(id: UUID): org.valkyrienskies.eureka.command.AssemblerPreferences.Prefs {
      var var10000: AssemblerPreferences.Prefs = byPlayer.get(id)
      if (var10000 != null) {
         var10000 = AssemblerPreferences.Prefs.copy$default(var10000, false, false, false, 0, 0, 31, null)
         if (var10000 != null) {
            return var10000
         }
      }

      return AssemblerPreferences.Prefs(false, false, false, 0, 0, 31, null)
   }

   public data class Prefs(enabled: Boolean = false,
      floater: Boolean = false,
      balloon: Boolean = false,
      floaterBonusPercent: Int = 0,
      balloonBonusPercent: Int = 0
   ) {
      public final var enabled: Boolean
      public final var floater: Boolean
      public final var balloon: Boolean
      public final var floaterBonusPercent: Int
      public final var balloonBonusPercent: Int

      init {
         this.enabled = enabled
         this.floater = floater
         this.balloon = balloon
         this.floaterBonusPercent = floaterBonusPercent
         this.balloonBonusPercent = balloonBonusPercent
      }

      public operator fun component1(): Boolean {
         return this.enabled
      }

      public operator fun component2(): Boolean {
         return this.floater
      }

      public operator fun component3(): Boolean {
         return this.balloon
      }

      public operator fun component4(): Int {
         return this.floaterBonusPercent
      }

      public operator fun component5(): Int {
         return this.balloonBonusPercent
      }

      public fun copy(
         enabled: Boolean = this.enabled,
         floater: Boolean = this.floater,
         balloon: Boolean = this.balloon,
         floaterBonusPercent: Int = this.floaterBonusPercent,
         balloonBonusPercent: Int = this.balloonBonusPercent
      ): org.valkyrienskies.eureka.command.AssemblerPreferences.Prefs {
         return AssemblerPreferences.Prefs(enabled, floater, balloon, floaterBonusPercent, balloonBonusPercent)
      }

      public override fun toString(): String {
         return "Prefs(enabled=${this.enabled}, floater=${this.floater}, balloon=${this.balloon}, floaterBonusPercent=${this.floaterBonusPercent}, balloonBonusPercent=${this.balloonBonusPercent})"
      }

      public override fun hashCode(): Int {
         return (
                  ((java.lang.Boolean.hashCode(this.enabled) * 31 + java.lang.Boolean.hashCode(this.floater)) * 31 + java.lang.Boolean.hashCode(this.balloon))
                        * 31
                     + Integer.hashCode(this.floaterBonusPercent)
               )
               * 31
            + Integer.hashCode(this.balloonBonusPercent)
         }

      public override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is AssemblerPreferences.Prefs
               && this.enabled == (other as AssemblerPreferences.Prefs).enabled
               && this.floater == (other as AssemblerPreferences.Prefs).floater
               && this.balloon == (other as AssemblerPreferences.Prefs).balloon
               && this.floaterBonusPercent == (other as AssemblerPreferences.Prefs).floaterBonusPercent
               && this.balloonBonusPercent == (other as AssemblerPreferences.Prefs).balloonBonusPercent
            }
      }

      fun Prefs() {
         this(false, false, false, 0, 0, 31, null)
      }
   }
}
