package org.valkyrienskies.core.impl.shadow

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.booleans.BooleanConsumer
import java.net.InetSocketAddress
import java.net.SocketAddress
import javax.crypto.SecretKey
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.apache.logging.log4j.Logger
import org.joml.Vector3d
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.event.RegisteredListener
import org.valkyrienskies.core.impl.bodies.properties.BodyKinematicsFactory
import org.valkyrienskies.core.impl.game.phys_entities.PhysicsEntityClient
import org.valkyrienskies.core.impl.game.ships.ShipObjectClientWorld
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.core.impl.networking.impl.PacketPhysEntityCreate
import org.valkyrienskies.core.impl.networking.impl.PacketPhysEntityRemove
import org.valkyrienskies.core.impl.networking.impl.PacketRequestUdp
import org.valkyrienskies.core.impl.networking.impl.PacketShipDataCreate
import org.valkyrienskies.core.impl.networking.impl.PacketShipRemove
import org.valkyrienskies.core.impl.networking.impl.PacketUdpState
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.core.util.VSCoreUtilKt

public class EO @ec  public constructor(p0: ShipObjectClientWorld, p1: Fl, p2: SimplePacketNetworking, p3: Fe) {
   private final val e: ShipObjectClientWorld
   private final val c: Fl
   private final val h: SimplePacketNetworking
   private final val d: Fe
   private final lateinit var a: List<RegisteredListener>
   private final var f: SecretKey?
   public final var b: Boolean
   private final var g: Boolean
   private final var i: Int

   init {
      this.h = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.j = 100;
   }

   private final val j: CoroutineScope
      private final get() {
         return this.h.coroutineScope;
      }


   public fun b() {
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
      // 00: aload 0
      // 01: bipush 7
      // 03: anewarray 125
      // 06: dup
      // 07: astore 1
      // 08: bipush 0
      // 09: aload 0
      // 0a: getfield org/valkyrienskies/core/impl/shadow/EO.d Lorg/valkyrienskies/core/impl/shadow/Fe;
      // 0d: dup
      // 0e: astore 2
      // 0f: getfield org/valkyrienskies/core/impl/shadow/Fe.d Lorg/valkyrienskies/core/impl/shadow/Fd;
      // 12: new org/valkyrienskies/core/impl/shadow/EO$h
      // 15: dup
      // 16: aload 0
      // 17: invokespecial org/valkyrienskies/core/impl/shadow/EO$h.<init> (Lorg/valkyrienskies/core/impl/shadow/EO;)V
      // 1a: checkcast org/valkyrienskies/core/impl/shadow/Fa
      // 1d: invokevirtual org/valkyrienskies/core/impl/shadow/Fd.a (Lorg/valkyrienskies/core/impl/shadow/Fa;)Lorg/valkyrienskies/core/api/event/RegisteredListener;
      // 20: aastore
      // 21: aload 1
      // 22: bipush 1
      // 23: aload 0
      // 24: getfield org/valkyrienskies/core/impl/shadow/EO.d Lorg/valkyrienskies/core/impl/shadow/Fe;
      // 27: dup
      // 28: astore 2
      // 29: getfield org/valkyrienskies/core/impl/shadow/Fe.e Lorg/valkyrienskies/core/impl/shadow/Fd;
      // 2c: new org/valkyrienskies/core/impl/shadow/EO$i
      // 2f: dup
      // 30: aload 0
      // 31: invokespecial org/valkyrienskies/core/impl/shadow/EO$i.<init> (Lorg/valkyrienskies/core/impl/shadow/EO;)V
      // 34: checkcast org/valkyrienskies/core/impl/shadow/Fa
      // 37: invokevirtual org/valkyrienskies/core/impl/shadow/Fd.a (Lorg/valkyrienskies/core/impl/shadow/Fa;)Lorg/valkyrienskies/core/api/event/RegisteredListener;
      // 3a: aastore
      // 3b: aload 1
      // 3c: bipush 2
      // 3d: aload 0
      // 3e: getfield org/valkyrienskies/core/impl/shadow/EO.d Lorg/valkyrienskies/core/impl/shadow/Fe;
      // 41: dup
      // 42: astore 2
      // 43: getfield org/valkyrienskies/core/impl/shadow/Fe.c Lorg/valkyrienskies/core/impl/shadow/Fd;
      // 46: new org/valkyrienskies/core/impl/shadow/EO$j
      // 49: dup
      // 4a: aload 0
      // 4b: invokespecial org/valkyrienskies/core/impl/shadow/EO$j.<init> (Lorg/valkyrienskies/core/impl/shadow/EO;)V
      // 4e: checkcast org/valkyrienskies/core/impl/shadow/Fa
      // 51: invokevirtual org/valkyrienskies/core/impl/shadow/Fd.a (Lorg/valkyrienskies/core/impl/shadow/Fa;)Lorg/valkyrienskies/core/api/event/RegisteredListener;
      // 54: aastore
      // 55: aload 1
      // 56: bipush 3
      // 57: aload 0
      // 58: getfield org/valkyrienskies/core/impl/shadow/EO.c Lorg/valkyrienskies/core/impl/networking/simple/SimplePacketNetworking;
      // 5b: ldc org/valkyrienskies/core/impl/networking/impl/PacketShipDataCreate
      // 5d: invokestatic kotlin/jvm/internal/Reflection.getOrCreateKotlinClass (Ljava/lang/Class;)Lkotlin/reflect/KClass;
      // 60: new org/valkyrienskies/core/impl/shadow/EO$k
      // 63: dup
      // 64: aload 0
      // 65: invokespecial org/valkyrienskies/core/impl/shadow/EO$k.<init> (Lorg/valkyrienskies/core/impl/shadow/EO;)V
      // 68: checkcast kotlin/jvm/functions/Function1
      // 6b: invokeinterface org/valkyrienskies/core/impl/networking/simple/SimplePacketNetworking.registerClientHandler (Lkotlin/reflect/KClass;Lkotlin/jvm/functions/Function1;)Lorg/valkyrienskies/core/api/event/RegisteredListener; 3
      // 70: aastore
      // 71: aload 1
      // 72: bipush 4
      // 73: aload 0
      // 74: getfield org/valkyrienskies/core/impl/shadow/EO.c Lorg/valkyrienskies/core/impl/networking/simple/SimplePacketNetworking;
      // 77: ldc org/valkyrienskies/core/impl/networking/impl/PacketShipRemove
      // 79: invokestatic kotlin/jvm/internal/Reflection.getOrCreateKotlinClass (Ljava/lang/Class;)Lkotlin/reflect/KClass;
      // 7c: new org/valkyrienskies/core/impl/shadow/EO$l
      // 7f: dup
      // 80: aload 0
      // 81: invokespecial org/valkyrienskies/core/impl/shadow/EO$l.<init> (Lorg/valkyrienskies/core/impl/shadow/EO;)V
      // 84: checkcast kotlin/jvm/functions/Function1
      // 87: invokeinterface org/valkyrienskies/core/impl/networking/simple/SimplePacketNetworking.registerClientHandler (Lkotlin/reflect/KClass;Lkotlin/jvm/functions/Function1;)Lorg/valkyrienskies/core/api/event/RegisteredListener; 3
      // 8c: aastore
      // 8d: aload 1
      // 8e: bipush 5
      // 8f: aload 0
      // 90: getfield org/valkyrienskies/core/impl/shadow/EO.c Lorg/valkyrienskies/core/impl/networking/simple/SimplePacketNetworking;
      // 93: ldc org/valkyrienskies/core/impl/networking/impl/PacketPhysEntityCreate
      // 95: invokestatic kotlin/jvm/internal/Reflection.getOrCreateKotlinClass (Ljava/lang/Class;)Lkotlin/reflect/KClass;
      // 98: new org/valkyrienskies/core/impl/shadow/EO$m
      // 9b: dup
      // 9c: aload 0
      // 9d: invokespecial org/valkyrienskies/core/impl/shadow/EO$m.<init> (Lorg/valkyrienskies/core/impl/shadow/EO;)V
      // a0: checkcast kotlin/jvm/functions/Function1
      // a3: invokeinterface org/valkyrienskies/core/impl/networking/simple/SimplePacketNetworking.registerClientHandler (Lkotlin/reflect/KClass;Lkotlin/jvm/functions/Function1;)Lorg/valkyrienskies/core/api/event/RegisteredListener; 3
      // a8: aastore
      // a9: aload 1
      // aa: bipush 6
      // ac: aload 0
      // ad: getfield org/valkyrienskies/core/impl/shadow/EO.c Lorg/valkyrienskies/core/impl/networking/simple/SimplePacketNetworking;
      // b0: ldc org/valkyrienskies/core/impl/networking/impl/PacketPhysEntityRemove
      // b2: invokestatic kotlin/jvm/internal/Reflection.getOrCreateKotlinClass (Ljava/lang/Class;)Lkotlin/reflect/KClass;
      // b5: new org/valkyrienskies/core/impl/shadow/EO$n
      // b8: dup
      // b9: aload 0
      // ba: invokespecial org/valkyrienskies/core/impl/shadow/EO$n.<init> (Lorg/valkyrienskies/core/impl/shadow/EO;)V
      // bd: checkcast kotlin/jvm/functions/Function1
      // c0: invokeinterface org/valkyrienskies/core/impl/networking/simple/SimplePacketNetworking.registerClientHandler (Lkotlin/reflect/KClass;Lkotlin/jvm/functions/Function1;)Lorg/valkyrienskies/core/api/event/RegisteredListener; 3
      // c5: aastore
      // c6: aload 1
      // c7: invokestatic kotlin/collections/CollectionsKt.listOf ([Ljava/lang/Object;)Ljava/util/List;
      // ca: putfield org/valkyrienskies/core/impl/shadow/EO.e Ljava/util/List;
      // cd: aload 0
      // ce: getfield org/valkyrienskies/core/impl/shadow/EO.b Lorg/valkyrienskies/core/impl/shadow/Fl;
      // d1: dup
      // d2: astore 2
      // d3: getfield org/valkyrienskies/core/impl/shadow/Fl.b Lorg/valkyrienskies/core/impl/shadow/Fb;
      // d6: invokevirtual org/valkyrienskies/core/impl/shadow/Fb.b ()V
      // d9: aload 0
      // da: getfield org/valkyrienskies/core/impl/shadow/EO.b Lorg/valkyrienskies/core/impl/shadow/Fl;
      // dd: dup
      // de: astore 2
      // df: getfield org/valkyrienskies/core/impl/shadow/Fl.a Lorg/valkyrienskies/core/impl/shadow/Fb;
      // e2: invokevirtual org/valkyrienskies/core/impl/shadow/Fb.b ()V
      // e5: return
   }

   public fun c() {
      var var10000: java.util.List = this.e;
      if (this.e == null) {
         Intrinsics.throwUninitializedPropertyAccessException("");
         var10000 = null;
      }

      Fg.a(var10000);
      this.f = null;
      var var1: Fl = this.b;
      this.b.b.c();
      var1 = this.b;
      this.b.a.c();
   }

   private fun a(p0: PacketShipRemove): Job {
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
      // 00: aload 0
      // 01: dup
      // 02: astore 2
      // 03: getfield org/valkyrienskies/core/impl/shadow/EO.h Lorg/valkyrienskies/core/impl/game/ships/ShipObjectClientWorld;
      // 06: invokevirtual org/valkyrienskies/core/impl/shadow/Ev.getCoroutineScope ()Lkotlinx/coroutines/CoroutineScope;
      // 09: aconst_null
      // 0a: aconst_null
      // 0b: new org/valkyrienskies/core/impl/shadow/EO$g
      // 0e: dup
      // 0f: aload 1
      // 10: aload 0
      // 11: aconst_null
      // 12: invokespecial org/valkyrienskies/core/impl/shadow/EO$g.<init> (Lorg/valkyrienskies/core/impl/networking/impl/PacketShipRemove;Lorg/valkyrienskies/core/impl/shadow/EO;Lkotlin/coroutines/Continuation;)V
      // 15: checkcast kotlin/jvm/functions/Function2
      // 18: bipush 3
      // 19: aconst_null
      // 1a: invokestatic kotlinx/coroutines/BuildersKt.launch$default (Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lkotlin/jvm/functions/Function2;ILjava/lang/Object;)Lkotlinx/coroutines/Job;
      // 1d: areturn
   }

   private fun a(p0: PacketShipDataCreate): Job {
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
      // 00: aload 0
      // 01: dup
      // 02: astore 2
      // 03: getfield org/valkyrienskies/core/impl/shadow/EO.h Lorg/valkyrienskies/core/impl/game/ships/ShipObjectClientWorld;
      // 06: invokevirtual org/valkyrienskies/core/impl/shadow/Ev.getCoroutineScope ()Lkotlinx/coroutines/CoroutineScope;
      // 09: aconst_null
      // 0a: aconst_null
      // 0b: new org/valkyrienskies/core/impl/shadow/EO$e
      // 0e: dup
      // 0f: aload 1
      // 10: aload 0
      // 11: aconst_null
      // 12: invokespecial org/valkyrienskies/core/impl/shadow/EO$e.<init> (Lorg/valkyrienskies/core/impl/networking/impl/PacketShipDataCreate;Lorg/valkyrienskies/core/impl/shadow/EO;Lkotlin/coroutines/Continuation;)V
      // 15: checkcast kotlin/jvm/functions/Function2
      // 18: bipush 3
      // 19: aconst_null
      // 1a: invokestatic kotlinx/coroutines/BuildersKt.launch$default (Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lkotlin/jvm/functions/Function2;ILjava/lang/Object;)Lkotlinx/coroutines/Job;
      // 1d: areturn
   }

   private fun a(p0: PacketPhysEntityCreate): Job {
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
      // 00: aload 0
      // 01: dup
      // 02: astore 2
      // 03: getfield org/valkyrienskies/core/impl/shadow/EO.h Lorg/valkyrienskies/core/impl/game/ships/ShipObjectClientWorld;
      // 06: invokevirtual org/valkyrienskies/core/impl/shadow/Ev.getCoroutineScope ()Lkotlinx/coroutines/CoroutineScope;
      // 09: aconst_null
      // 0a: aconst_null
      // 0b: new org/valkyrienskies/core/impl/shadow/EO$c
      // 0e: dup
      // 0f: aload 1
      // 10: aload 0
      // 11: aconst_null
      // 12: invokespecial org/valkyrienskies/core/impl/shadow/EO$c.<init> (Lorg/valkyrienskies/core/impl/networking/impl/PacketPhysEntityCreate;Lorg/valkyrienskies/core/impl/shadow/EO;Lkotlin/coroutines/Continuation;)V
      // 15: checkcast kotlin/jvm/functions/Function2
      // 18: bipush 3
      // 19: aconst_null
      // 1a: invokestatic kotlinx/coroutines/BuildersKt.launch$default (Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lkotlin/jvm/functions/Function2;ILjava/lang/Object;)Lkotlinx/coroutines/Job;
      // 1d: areturn
   }

   private fun a(p0: PacketPhysEntityRemove): Job {
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
      // 00: aload 0
      // 01: dup
      // 02: astore 2
      // 03: getfield org/valkyrienskies/core/impl/shadow/EO.h Lorg/valkyrienskies/core/impl/game/ships/ShipObjectClientWorld;
      // 06: invokevirtual org/valkyrienskies/core/impl/shadow/Ev.getCoroutineScope ()Lkotlinx/coroutines/CoroutineScope;
      // 09: aconst_null
      // 0a: aconst_null
      // 0b: new org/valkyrienskies/core/impl/shadow/EO$d
      // 0e: dup
      // 0f: aload 1
      // 10: aload 0
      // 11: aconst_null
      // 12: invokespecial org/valkyrienskies/core/impl/shadow/EO$d.<init> (Lorg/valkyrienskies/core/impl/networking/impl/PacketPhysEntityRemove;Lorg/valkyrienskies/core/impl/shadow/EO;Lkotlin/coroutines/Continuation;)V
      // 15: checkcast kotlin/jvm/functions/Function2
      // 18: bipush 3
      // 19: aconst_null
      // 1a: invokestatic kotlinx/coroutines/BuildersKt.launch$default (Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lkotlin/jvm/functions/Function2;ILjava/lang/Object;)Lkotlinx/coroutines/Job;
      // 1d: areturn
   }

   private fun a(p0: Fc) {
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
      // 01: dup
      // 02: astore 2
      // 03: getfield org/valkyrienskies/core/impl/shadow/Fc.b Lio/netty/buffer/ByteBuf;
      // 06: invokevirtual io/netty/buffer/ByteBuf.retain ()Lio/netty/buffer/ByteBuf;
      // 09: astore 1
      // 0a: aload 0
      // 0b: dup
      // 0c: astore 2
      // 0d: getfield org/valkyrienskies/core/impl/shadow/EO.h Lorg/valkyrienskies/core/impl/game/ships/ShipObjectClientWorld;
      // 10: invokevirtual org/valkyrienskies/core/impl/shadow/Ev.getCoroutineScope ()Lkotlinx/coroutines/CoroutineScope;
      // 13: aconst_null
      // 14: aconst_null
      // 15: new org/valkyrienskies/core/impl/shadow/EO$f
      // 18: dup
      // 19: aload 1
      // 1a: aload 0
      // 1b: aconst_null
      // 1c: invokespecial org/valkyrienskies/core/impl/shadow/EO$f.<init> (Lio/netty/buffer/ByteBuf;Lorg/valkyrienskies/core/impl/shadow/EO;Lkotlin/coroutines/Continuation;)V
      // 1f: checkcast kotlin/jvm/functions/Function2
      // 22: bipush 3
      // 23: aconst_null
      // 24: invokestatic kotlinx/coroutines/BuildersKt.launch$default (Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lkotlin/jvm/functions/Function2;ILjava/lang/Object;)Lkotlinx/coroutines/Job;
      // 27: pop
      // 28: return
   }

   private fun b(p0: Fc) {
      a.a(var1.b, this.h.shipObjects);
   }

   private fun c(p0: Fc) {
      a.b(var1.b, this.h.physicsEntities);
   }

   public fun a(p0: SocketAddress) {
      val var2: Fl = this.b;
      if (!this.b.d && !this.i) {
         this.j += -1;
         if (this.j <= 0) {
            this.f = Fk.a();
            val var10000: Fl = this.b;
            val var10002: SecretKey = this.f;
            val var4: BooleanConsumer = org/valkyrienskies/core/impl/shadow/EO##Lambda_0_290(this);
            if (var10000.f != null) {
               var10000.f.unregister();
            }

            val var5: SimplePacketNetworking = var10000.c;
            var10000.f = var10000.c.registerClientHandler1(PacketUdpState::class, { p0: PacketUdpState ->
               var3.accept(var1x.state);
               if (var1x.state) {
                  var var2: SocketAddress = var1;
                  if (var1 is InetSocketAddress && (var1 as InetSocketAddress).getPort() != var1x.port) {
                     var2 = InetSocketAddress((var2 as InetSocketAddress).getAddress(), var1x.port);
                  }

                  if (!Fl.this.a(var2, var1x.id)) {
                     Fl.this.g();
                  }
               }
            } as Function1);
            val var10004: ByteArray = var10002.getEncoded();
            var5.sendToServer1(PacketRequestUdp(0, var10004));
            this.j = 100;
         }
      }
   }

   @JvmStatic
   fun a(var0: EO, var1: Boolean) {
      if (!var1) {
         var0.i = true;
      }
   }

   @JvmStatic
   @JvmSynthetic
   fun a(var0: EO, var1: Fc) {
      a.a(var1.b, var0.h.shipObjects);
   }

   @JvmStatic
   @JvmSynthetic
   fun b(var0: EO, var1: Fc) {
      a.b(var1.b, var0.h.physicsEntities);
   }

   public companion object a {
      @JvmStatic
      @JvmSynthetic
      private KProperty<Object>[] a;

      private final val a: Logger
         private final get() {
            val var10000: Logger = EO.k;
            return var10000;
         }


      private final val b: BodyKinematicsFactory

      public fun a(p0: ByteBuf, p1: Map<Long, Er>) {
         val var3: Int = var1.readInt();

         try {
            while (var1.isReadable()) {
               val var4: Long = var1.readLong();
               val var6: Er;
               if ((var6 = var2.get(var4) as Er) == null) {
                  this.a.debug("Received ship transform for ship with unknown id: $var4");
                  var1.skipBytes(120);
               } else if (var6.f >= var3) {
                  var1.skipBytes(120);
               } else {
                  val var11: AABBi = VSCoreUtilKt.readAABBi(var1);
                  val var15: BodyKinematics = EO.l
                     .create(
                        VSCoreUtilKt.readVec3fAsDouble(var1),
                        VSCoreUtilKt.readVec3fAsDouble(var1),
                        VSCoreUtilKt.readVec3d(var1),
                        VSCoreUtilKt.read3FAsNormQuatd(var1),
                        VSCoreUtilKt.readVec3fAsDouble(var1),
                        VSCoreUtilKt.readVec3d(var1)
                     );
                  var6.e = var15;
                  var6.f = var3;
                  if (var11.isValid()) {
                     var6.l.shipAABB = var11 as AABBic;
                  }
               }
            }
         } catch (var12: Exception) {
            this.a.error("Something went wrong when reading ship transform packets", var12);
         }
      }

      public fun b(p0: ByteBuf, p1: Map<Long, PhysicsEntityClient>) {
         val var3: Int = var1.readInt();

         try {
            while (var1.isReadable()) {
               val var4: Long = var1.readLong();
               val var6: PhysicsEntityClient;
               if ((var6 = var2.get(var4) as PhysicsEntityClient) == null) {
                  this.a.debug("Received ship transform for physics entity with unknown id: $var4");
                  var1.skipBytes(40);
               } else if (var6.latestNetworkTTick >= var3) {
                  var1.skipBytes(40);
               } else {
                  var6.latestNetworkTransform = ShipTransformImpl.Companion
                     .create(VSCoreUtilKt.readVec3d(var1), Vector3d(), VSCoreUtilKt.read3FAsNormQuatd(var1), Vector3d((double)var1.readFloat()));
                  var6.latestNetworkTTick = var3;
               }
            }
         } catch (var11: Exception) {
            this.a.error("Something went wrong when reading ship transform packets", var11);
         }
      }

      @JvmStatic
      fun {
         val var0: Array<KProperty>;
         (var0 = arrayOfNulls(1))[0] = Reflection.property1(
            PropertyReference1Impl(EO.a.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
         );
         a = var0;
      }
   }

   @eb
   public interface b {
      public abstract fun a(p0: ShipObjectClientWorld): EO {
      }
   }
}
