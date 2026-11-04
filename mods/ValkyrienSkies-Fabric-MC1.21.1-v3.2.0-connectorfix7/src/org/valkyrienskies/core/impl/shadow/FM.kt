package org.valkyrienskies.core.impl.shadow

import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.LockSupport
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import kotlinx.coroutines.BuildersKt
import org.apache.logging.log4j.Logger
import org.valkyrienskies.core.impl.config.VSCoreConfig

public class FM(p0: FQ, p1: Int = 60) : Runnable {
   private final val k: FQ
   private final var a: Int
   private final var b: Boolean
   private final var c: Long
   private final val f: Queue<Long>
   public final var e: Int
   public final val j: ReentrantLock
   public final val g: Condition
   public final val h: Condition
   public final val d: ReentrantLock
   public final val i: Condition

   init {
      this.j = var1
      this.k = var2
      this.c = LinkedList<>()
      this.e = ReentrantLock()
      this.f = this.e.newCondition()
      this.g = this.e.newCondition()
      this.h = ReentrantLock()
      this.i = this.h.newCondition()
   }

   public override fun run() {
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
      // 000: aload 0
      // 001: getfield org/valkyrienskies/core/impl/shadow/FM.b Z
      // 004: ifne 2b0
      // 007: aload 0
      // 008: getfield org/valkyrienskies/core/impl/shadow/FM.j Lorg/valkyrienskies/core/impl/shadow/FQ;
      // 00b: dup
      // 00c: astore 3
      // 00d: getfield org/valkyrienskies/core/impl/shadow/FQ.c Z
      // 010: ifeq 093
      // 013: getstatic org/valkyrienskies/core/impl/config/VSCoreConfig.SERVER Lorg/valkyrienskies/core/impl/config/VSCoreConfig$Server;
      // 016: invokevirtual org/valkyrienskies/core/impl/config/VSCoreConfig$Server.getPt ()Lorg/valkyrienskies/core/impl/config/VSCoreConfig$Server$PhysicsThreadSettings;
      // 019: invokevirtual org/valkyrienskies/core/impl/config/VSCoreConfig$Server$PhysicsThreadSettings.getPhysicsTicksPerGameTick ()I
      // 01c: istore 1
      // 01d: dconst_1
      // 01e: iload 1
      // 01f: bipush 20
      // 021: imul
      // 022: i2d
      // 023: ddiv
      // 024: getstatic org/valkyrienskies/core/impl/config/VSCoreConfig.SERVER Lorg/valkyrienskies/core/impl/config/VSCoreConfig$Server;
      // 027: invokevirtual org/valkyrienskies/core/impl/config/VSCoreConfig$Server.getPhysics ()Lorg/valkyrienskies/core/impl/config/PhysicsConfig;
      // 02a: invokevirtual org/valkyrienskies/core/impl/config/PhysicsConfig.getPhysicsSpeed ()D
      // 02d: dmul
      // 02e: dstore 2
      // 02f: aload 0
      // 030: getfield org/valkyrienskies/core/impl/shadow/FM.e Ljava/util/concurrent/locks/ReentrantLock;
      // 033: checkcast java/util/concurrent/locks/Lock
      // 036: dup
      // 037: astore 4
      // 039: invokeinterface java/util/concurrent/locks/Lock.lock ()V 1
      // 03e: aload 0
      // 03f: getfield org/valkyrienskies/core/impl/shadow/FM.d I
      // 042: iload 1
      // 043: if_icmplt 052
      // 046: aload 0
      // 047: getfield org/valkyrienskies/core/impl/shadow/FM.g Ljava/util/concurrent/locks/Condition;
      // 04a: invokeinterface java/util/concurrent/locks/Condition.await ()V 1
      // 04f: goto 03e
      // 052: aload 0
      // 053: getfield org/valkyrienskies/core/impl/shadow/FM.j Lorg/valkyrienskies/core/impl/shadow/FQ;
      // 056: dload 2
      // 057: invokevirtual org/valkyrienskies/core/impl/shadow/FQ.a (D)V
      // 05a: aload 0
      // 05b: getfield org/valkyrienskies/core/impl/shadow/FM.d I
      // 05e: istore 6
      // 060: aload 0
      // 061: iload 6
      // 063: bipush 1
      // 064: iadd
      // 065: putfield org/valkyrienskies/core/impl/shadow/FM.d I
      // 068: aload 0
      // 069: getfield org/valkyrienskies/core/impl/shadow/FM.d I
      // 06c: iload 1
      // 06d: if_icmplt 079
      // 070: aload 0
      // 071: getfield org/valkyrienskies/core/impl/shadow/FM.f Ljava/util/concurrent/locks/Condition;
      // 074: invokeinterface java/util/concurrent/locks/Condition.signal ()V 1
      // 079: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 07c: pop
      // 07d: aload 4
      // 07f: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
      // 084: goto 000
      // 087: astore 6
      // 089: aload 4
      // 08b: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
      // 090: aload 6
      // 092: athrow
      // 093: aload 0
      // 094: getfield org/valkyrienskies/core/impl/shadow/FM.j Lorg/valkyrienskies/core/impl/shadow/FQ;
      // 097: invokevirtual org/valkyrienskies/core/impl/shadow/FQ.getDeleteResources ()Z
      // 09a: ifeq 12d
      // 09d: aload 0
      // 09e: getfield org/valkyrienskies/core/impl/shadow/FM.j Lorg/valkyrienskies/core/impl/shadow/FQ;
      // 0a1: dup
      // 0a2: astore 3
      // 0a3: invokevirtual org/valkyrienskies/core/impl/shadow/FQ.getDeleteResources ()Z
      // 0a6: ifeq 12a
      // 0a9: aload 3
      // 0aa: bipush 1
      // 0ab: invokevirtual org/valkyrienskies/core/impl/shadow/FQ.setArePhysicsRunning (Z)V
      // 0ae: aload 3
      // 0af: getfield org/valkyrienskies/core/impl/shadow/FQ.b Lorg/valkyrienskies/core/impl/shadow/FN;
      // 0b2: dup
      // 0b3: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 0b6: astore 10
      // 0b8: getstatic org/valkyrienskies/core/impl/shadow/FN.a Lorg/valkyrienskies/core/impl/shadow/FN$a;
      // 0bb: invokestatic org/valkyrienskies/core/impl/shadow/FN$a.a (Lorg/valkyrienskies/core/impl/shadow/FN$a;)Lorg/apache/logging/log4j/Logger;
      // 0be: ldc "Deleting VSPhysicsPipelineStage resources!"
      // 0c0: invokeinterface org/apache/logging/log4j/Logger.info (Ljava/lang/String;)V 2
      // 0c5: aload 10
      // 0c7: getfield org/valkyrienskies/core/impl/shadow/FN.h Z
      // 0ca: ifeq 0d7
      // 0cd: new java/lang/IllegalStateException
      // 0d0: dup
      // 0d1: ldc "Physics engine has already been deleted!"
      // 0d3: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 0d6: athrow
      // 0d7: aload 10
      // 0d9: getfield org/valkyrienskies/core/impl/shadow/FN.e Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;
      // 0dc: invokeinterface it/unimi/dsi/fastutil/longs/Long2ObjectMap.clear ()V 1
      // 0e1: aload 10
      // 0e3: getfield org/valkyrienskies/core/impl/shadow/FN.c Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;
      // 0e6: invokeinterface it/unimi/dsi/fastutil/longs/Long2ObjectMap.clear ()V 1
      // 0eb: aload 10
      // 0ed: getfield org/valkyrienskies/core/impl/shadow/FN.g Lorg/valkyrienskies/core/impl/shadow/GY;
      // 0f0: invokeinterface org/valkyrienskies/core/impl/shadow/GY.close ()V 1
      // 0f5: aload 10
      // 0f7: getfield org/valkyrienskies/core/impl/shadow/FN.l Lkotlinx/coroutines/CoroutineScope;
      // 0fa: invokestatic kotlinx/coroutines/Dispatchers.getDefault ()Lkotlinx/coroutines/CoroutineDispatcher;
      // 0fd: checkcast kotlin/coroutines/CoroutineContext
      // 100: aconst_null
      // 101: new org/valkyrienskies/core/impl/shadow/FN$c
      // 104: dup
      // 105: aload 10
      // 107: aconst_null
      // 108: invokespecial org/valkyrienskies/core/impl/shadow/FN$c.<init> (Lorg/valkyrienskies/core/impl/shadow/FN;Lkotlin/coroutines/Continuation;)V
      // 10b: checkcast kotlin/jvm/functions/Function2
      // 10e: bipush 2
      // 10f: aconst_null
      // 110: invokestatic kotlinx/coroutines/BuildersKt.launch$default (Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lkotlin/jvm/functions/Function2;ILjava/lang/Object;)Lkotlinx/coroutines/Job;
      // 113: pop
      // 114: aload 10
      // 116: bipush 1
      // 117: putfield org/valkyrienskies/core/impl/shadow/FN.h Z
      // 11a: aload 3
      // 11b: aconst_null
      // 11c: putfield org/valkyrienskies/core/impl/shadow/FQ.b Lorg/valkyrienskies/core/impl/shadow/FN;
      // 11f: aload 3
      // 120: getfield org/valkyrienskies/core/impl/shadow/FQ.d Lorg/valkyrienskies/core/impl/shadow/FM;
      // 123: dup
      // 124: astore 10
      // 126: bipush 1
      // 127: putfield org/valkyrienskies/core/impl/shadow/FM.b Z
      // 12a: goto 2b0
      // 12d: aload 0
      // 12e: getfield org/valkyrienskies/core/impl/shadow/FM.j Lorg/valkyrienskies/core/impl/shadow/FQ;
      // 131: invokevirtual org/valkyrienskies/core/impl/shadow/FQ.getArePhysicsRunning ()Z
      // 134: ifne 17f
      // 137: aload 0
      // 138: lconst_0
      // 139: putfield org/valkyrienskies/core/impl/shadow/FM.l J
      // 13c: aload 0
      // 13d: getfield org/valkyrienskies/core/impl/shadow/FM.c Ljava/util/Queue;
      // 140: invokeinterface java/util/Queue.clear ()V 1
      // 145: aload 0
      // 146: getfield org/valkyrienskies/core/impl/shadow/FM.h Ljava/util/concurrent/locks/ReentrantLock;
      // 149: checkcast java/util/concurrent/locks/Lock
      // 14c: dup
      // 14d: astore 1
      // 14e: invokeinterface java/util/concurrent/locks/Lock.lock ()V 1
      // 153: aload 0
      // 154: getfield org/valkyrienskies/core/impl/shadow/FM.j Lorg/valkyrienskies/core/impl/shadow/FQ;
      // 157: invokevirtual org/valkyrienskies/core/impl/shadow/FQ.getArePhysicsRunning ()Z
      // 15a: ifne 169
      // 15d: aload 0
      // 15e: getfield org/valkyrienskies/core/impl/shadow/FM.i Ljava/util/concurrent/locks/Condition;
      // 161: invokeinterface java/util/concurrent/locks/Condition.await ()V 1
      // 166: goto 153
      // 169: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 16c: pop
      // 16d: aload 1
      // 16e: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
      // 173: goto 17f
      // 176: astore 3
      // 177: aload 1
      // 178: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
      // 17d: aload 3
      // 17e: athrow
      // 17f: ldc2_w 1.0E9
      // 182: aload 0
      // 183: getfield org/valkyrienskies/core/impl/shadow/FM.k I
      // 186: i2d
      // 187: ddiv
      // 188: dup2
      // 189: dstore 1
      // 18a: ldc2_w 1.0E9
      // 18d: ddiv
      // 18e: getstatic org/valkyrienskies/core/impl/config/VSCoreConfig.SERVER Lorg/valkyrienskies/core/impl/config/VSCoreConfig$Server;
      // 191: invokevirtual org/valkyrienskies/core/impl/config/VSCoreConfig$Server.getPhysics ()Lorg/valkyrienskies/core/impl/config/PhysicsConfig;
      // 194: invokevirtual org/valkyrienskies/core/impl/config/PhysicsConfig.getPhysicsSpeed ()D
      // 197: dmul
      // 198: dstore 3
      // 199: invokestatic java/lang/System.nanoTime ()J
      // 19c: lstore 7
      // 19e: aload 0
      // 19f: getfield org/valkyrienskies/core/impl/shadow/FM.j Lorg/valkyrienskies/core/impl/shadow/FQ;
      // 1a2: dload 3
      // 1a3: invokevirtual org/valkyrienskies/core/impl/shadow/FQ.a (D)V
      // 1a6: invokestatic java/lang/System.nanoTime ()J
      // 1a9: lload 7
      // 1ab: lsub
      // 1ac: lstore 5
      // 1ae: aload 0
      // 1af: astore 3
      // 1b0: invokestatic java/lang/System.currentTimeMillis ()J
      // 1b3: lstore 10
      // 1b5: aload 3
      // 1b6: getfield org/valkyrienskies/core/impl/shadow/FM.c Ljava/util/Queue;
      // 1b9: lload 10
      // 1bb: invokestatic java/lang/Long.valueOf (J)Ljava/lang/Long;
      // 1be: invokeinterface java/util/Queue.add (Ljava/lang/Object;)Z 2
      // 1c3: pop
      // 1c4: aload 3
      // 1c5: getfield org/valkyrienskies/core/impl/shadow/FM.c Ljava/util/Queue;
      // 1c8: checkcast java/util/Collection
      // 1cb: invokeinterface java/util/Collection.isEmpty ()Z 1
      // 1d0: ifne 1d7
      // 1d3: bipush 1
      // 1d4: goto 1d8
      // 1d7: bipush 0
      // 1d8: ifeq 201
      // 1db: aload 3
      // 1dc: getfield org/valkyrienskies/core/impl/shadow/FM.c Ljava/util/Queue;
      // 1df: invokeinterface java/util/Queue.peek ()Ljava/lang/Object; 1
      // 1e4: checkcast java/lang/Number
      // 1e7: invokevirtual java/lang/Number.longValue ()J
      // 1ea: ldc2_w 5000
      // 1ed: ladd
      // 1ee: lload 10
      // 1f0: lcmp
      // 1f1: ifge 201
      // 1f4: aload 3
      // 1f5: getfield org/valkyrienskies/core/impl/shadow/FM.c Ljava/util/Queue;
      // 1f8: invokeinterface java/util/Queue.remove ()Ljava/lang/Object; 1
      // 1fd: pop
      // 1fe: goto 1c4
      // 201: aload 0
      // 202: dload 1
      // 203: lload 5
      // 205: lstore 12
      // 207: dstore 10
      // 209: astore 3
      // 20a: dload 10
      // 20c: lload 12
      // 20e: l2d
      // 20f: dsub
      // 210: dup2
      // 211: dstore 14
      // 213: dconst_0
      // 214: dcmpg
      // 215: ifge 22d
      // 218: aload 3
      // 219: dup
      // 21a: getfield org/valkyrienskies/core/impl/shadow/FM.l J
      // 21d: dload 14
      // 21f: d2l
      // 220: lsub
      // 221: ldc2_w 1000000000
      // 224: invokestatic java/lang/Math.min (JJ)J
      // 227: putfield org/valkyrienskies/core/impl/shadow/FM.l J
      // 22a: goto 000
      // 22d: aload 3
      // 22e: getfield org/valkyrienskies/core/impl/shadow/FM.l J
      // 231: l2d
      // 232: dload 14
      // 234: dcmpl
      // 235: ifle 247
      // 238: aload 3
      // 239: dup
      // 23a: getfield org/valkyrienskies/core/impl/shadow/FM.l J
      // 23d: dload 14
      // 23f: d2l
      // 240: lsub
      // 241: putfield org/valkyrienskies/core/impl/shadow/FM.l J
      // 244: goto 000
      // 247: dload 14
      // 249: aload 3
      // 24a: getfield org/valkyrienskies/core/impl/shadow/FM.l J
      // 24d: l2d
      // 24e: dsub
      // 24f: dstore 16
      // 251: aload 3
      // 252: lconst_0
      // 253: putfield org/valkyrienskies/core/impl/shadow/FM.l J
      // 256: dload 16
      // 258: d2l
      // 259: lstore 18
      // 25b: invokestatic java/lang/System.nanoTime ()J
      // 25e: lstore 20
      // 260: lload 18
      // 262: ldc2_w 1000000
      // 265: lsub
      // 266: dup2
      // 267: lstore 22
      // 269: invokestatic java/util/concurrent/locks/LockSupport.parkNanos (J)V
      // 26c: invokestatic java/lang/System.nanoTime ()J
      // 26f: lload 20
      // 271: lsub
      // 272: lload 18
      // 274: lcmp
      // 275: ifge 27e
      // 278: invokestatic org/valkyrienskies/core/impl/shadow/Gc.a ()V
      // 27b: goto 26c
      // 27e: goto 000
      // 281: astore 1
      // 282: getstatic org/valkyrienskies/core/impl/shadow/FM.a Lorg/valkyrienskies/core/impl/shadow/FM$a;
      // 285: invokestatic org/valkyrienskies/core/impl/shadow/FM$a.a (Lorg/valkyrienskies/core/impl/shadow/FM$a;)Lorg/apache/logging/log4j/Logger;
      // 288: ldc_w "Error in physics pipeline background task"
      // 28b: aload 1
      // 28c: checkcast java/lang/Throwable
      // 28f: invokeinterface org/apache/logging/log4j/Logger.error (Ljava/lang/String;Ljava/lang/Throwable;)V 3
      // 294: bipush 0
      // 295: istore 3
      // 296: iload 3
      // 297: bipush 10
      // 299: if_icmpge 2b0
      // 29c: getstatic org/valkyrienskies/core/impl/shadow/FM.a Lorg/valkyrienskies/core/impl/shadow/FM$a;
      // 29f: invokestatic org/valkyrienskies/core/impl/shadow/FM$a.a (Lorg/valkyrienskies/core/impl/shadow/FM$a;)Lorg/apache/logging/log4j/Logger;
      // 2a2: ldc_w "!!!!!!! VS PHYSICS THREAD CRASHED !!!!!!!"
      // 2a5: invokeinterface org/apache/logging/log4j/Logger.error (Ljava/lang/String;)V 2
      // 2aa: iinc 3 1
      // 2ad: goto 296
      // 2b0: getstatic org/valkyrienskies/core/impl/shadow/FM.a Lorg/valkyrienskies/core/impl/shadow/FM$a;
      // 2b3: invokestatic org/valkyrienskies/core/impl/shadow/FM$a.a (Lorg/valkyrienskies/core/impl/shadow/FM$a;)Lorg/apache/logging/log4j/Logger;
      // 2b6: ldc_w "Physics pipeline ending"
      // 2b9: invokeinterface org/apache/logging/log4j/Logger.warn (Ljava/lang/String;)V 2
      // 2be: return
   }

   public fun g() {
      this.b = true
   }

   private fun j() {
      val var1: Long = System.currentTimeMillis()
      this.c.add(var1)

      while (!this.c.isEmpty() && this.c.peek().longValue() + 5000L < var1) {
         this.c.remove()
      }
   }

   private fun a(p0: Double, p1: Long) {
      val var5: Double
      if ((var5 = var1 - var3) < 0.0) {
         this.l = Math.min(this.l - (long)var5, 1000000000L)
      } else if (this.l > var5) {
         this.l -= (long)var5
      } else {
         val var7: Double = var5 - this.l
         this.l = 0L
         a((long)var7)
      }
   }

   @JvmStatic
   private fun a(p0: Long) {
      val var2: Long = System.nanoTime()
      LockSupport.parkNanos(var0 - 1000000L)

      while (System.nanoTime() - var2 < var0) {
         Gc.a()
      }
   }

   public fun h(): Double {
      return this.c.size() / 5.0
   }

   public companion object a {
      @JvmStatic
      @JvmSynthetic
      KProperty<Object>[] a;
      private const val a: Long = 1000000000L
      private const val b: Int = 5000

      private final val c: Logger
         private final get() {
            val var10000: Logger = FM.o
            return var10000
         }


      @JvmStatic
      @JvmSynthetic
      fun a(var0: FM.a): Logger {
         val var10000: Logger = FM.o
         var10000
      }

      @JvmStatic
      fun {
         val var0: Array<KProperty>
         (var0 = arrayOfNulls(1))[0] = Reflection.property1(
            PropertyReference1Impl(FM.a.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
         )
         a = var0
      }
   }
}
