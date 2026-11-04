package org.valkyrienskies.core.impl.shadow

import java.util.concurrent.locks.Lock
import javax.inject.Inject
import kotlin.concurrent.ThreadsKt
import kotlin.jvm.functions.Function0
import org.valkyrienskies.core.api.ships.ServerTickListener
import org.valkyrienskies.core.impl.api_impl.config.ConfigPhysicsBackendType
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.core.internal.world.VsiPipeline
import org.valkyrienskies.core.internal.world.VsiServerShipWorld
import org.valkyrienskies.physics_api_krunch.KrunchDebug

public class FQ @Inject  public constructor(p0: Et, p1: FH, p2: FN?, p3: FJ, p4: EX) : VsiPipeline {
   public open val i: Et
   private final val c: FH
   private final var g: FN?
   private final val e: FJ

   public final var j: Boolean
      private set

   private final val f: FM

   public open var a: Boolean
      public open set(p0) {
         this.g = value
         if (value) {
            var var2: FM = this.d
            val var5: Lock
            (var5 = this.d.h).lock()

            try {
               var2 = this.d
               this.d.i.signal()
            } finally {
               var5.unlock()
            }
         }
      }


   private final val h: Thread
   public open var b: Boolean

   init {
      this.a = var1
      this.e = var2
      this.b = var3
      this.f = var4
      this.c = VSCoreConfig.SERVER.pt.synchronizePhysics
      this.d = FM(this, 0, 2, null)
      this.g = !var5.m
      this.h = ThreadsKt.thread$default(true, false, null, "Physics thread", 8, {
         FQ.this.d.run()
      } as Function0, 6, null)
   }

   public open val d: Boolean
      public open get() {
         val var10000: FN = this.b
         return var10000.m
      }


   public override fun preTickGame() {
      val var1: Boolean = this.c
      this.c = VSCoreConfig.SERVER.pt.synchronizePhysics
      if (var1) {
         val var7: FM = this.d
         val var8: Lock
         (var8 = this.d.e).lock()

         try {
            var var2: FM = this.d
            this.d.d = 0
            var2 = this.d
            this.d.g.signal()
         } finally {
            var8.unlock()
         }
      }

      val var11: FH = this.e
      val var9: java.util.Iterator = this.e.b.loadedShips.iterator()

      while (var9.hasNext()) {
         val var12: java.util.Iterator = (var9.next() as Es).f.values().iterator()

         while (var12.hasNext()) {
            (var12.next() as ServerTickListener).onServerTick()
         }
      }

      var11.b.preTick()
   }

   public override fun postTickGame() {
      if (this.c) {
         val var1: FM = this.d
         val var6: Lock
         (var6 = this.d.e).lock()

         try {
            val var2: Int = VSCoreConfig.SERVER.pt.physicsTicksPerGameTick

            while (true) {
               var var3: FM = this.d
               if (this.d.d >= var2) {
                  break
               }

               var3 = this.d
               this.d.f.await()
            }
         } finally {
            var6.unlock()
         }
      }

      val var7: FG = this.e.b()
      val var10000: FN = this.b
      if (var10000.b.size() >= 800) {
         FN.a.a(FN.a).warn("Too many game frames in the game frame queue. Is the physics stage broken?")
         Thread.sleep(1000L)
      }

      var10000.b.add(var7)
   }

   public fun c() {
      if (this.b) {
         this.a = true
         val var10000: FN = this.b
         var10000.j()
         this.b = null
         val var1: FM = this.d
         this.d.b = true
      }
   }

   public fun a(p0: Double) {
      val var10000: FN = this.b
      val var3: FL = var10000.a(var1, true)
      val var4: FH = this.e
      val var10001: FN = this.b
      var4.a(var10001)
      this.e.a(var3)
      this.f.a(var3)
   }

   public override fun computePhysTps(): Double {
      val var1: FM = this.d
      return this.d.c.size() / 5.0
   }

   public override fun getLoadedVoxelChunks(): Int {
      return KrunchDebug.getVoxelChunkCount()
   }

   public override fun getPhysicsBackendType(): ConfigPhysicsBackendType {
      val var10000: FN = this.b
      when (FQ.a.a[var10000.f.g.ordinal()]) {
         1 -> return ConfigPhysicsBackendType.KRUNCH_CLASSIC
         2 -> return ConfigPhysicsBackendType.KRUNCH_PHYSX
         3 -> return ConfigPhysicsBackendType.KRUNCH_JOLT
         else -> throw NoWhenBranchMatchedException()
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun getShipWorld(): VsiServerShipWorld {
      this.a as VsiServerShipWorld
   }

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class a {
      @JvmStatic
      @JvmSynthetic
      public int[] a;

      @JvmStatic
      fun {
         val var0: IntArray = IntArray(ID.values().length)

         try {
            var0[ID.KRUNCH_CLASSIC.ordinal()] = 1
         } catch (var3: NoSuchFieldError) {
         }

         try {
            var0[ID.KRUNCH_PHYSX.ordinal()] = 2
         } catch (var2: NoSuchFieldError) {
         }

         try {
            var0[ID.KRUNCH_JOLT.ordinal()] = 3
         } catch (var1: NoSuchFieldError) {
         }

         a = var0
      }
   }
}
