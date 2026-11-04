package org.valkyrienskies.mod.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import java.util.HashMap
import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1297
import net.minecraft.class_1299
import net.minecraft.class_1761
import net.minecraft.class_1792
import net.minecraft.class_1799
import net.minecraft.class_1935
import net.minecraft.class_1937
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2561
import net.minecraft.class_2586
import net.minecraft.class_2591
import net.minecraft.class_2960
import net.minecraft.class_5321
import net.minecraft.class_6862
import net.minecraft.class_7924
import net.minecraft.class_9331
import net.minecraft.class_1761.class_7704
import net.minecraft.class_1761.class_7915
import net.minecraft.class_1761.class_8128
import net.minecraft.server.MinecraftServer
import org.valkyrienskies.core.api.attachment.AttachmentRegistration
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.core.internal.VsiCore
import org.valkyrienskies.core.internal.VsiCoreClient
import org.valkyrienskies.mod.api.BlockEntityPhysicsListener
import org.valkyrienskies.mod.api.EntityPhysicsListener
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.api.ValkyrienSkies
import org.valkyrienskies.mod.api_impl.events.VsApiImpl
import org.valkyrienskies.mod.common.blockentity.TestAntigravBlockEntity
import org.valkyrienskies.mod.common.blockentity.TestHingeBlockEntity
import org.valkyrienskies.mod.common.blockentity.TestThrusterBlockEntity
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import org.valkyrienskies.mod.common.jackson.BlockPosDeserializer
import org.valkyrienskies.mod.common.jackson.BlockPosKeyDeserializer
import org.valkyrienskies.mod.common.jackson.BlockPosKeySerializer
import org.valkyrienskies.mod.common.jackson.BlockPosSerializer
import org.valkyrienskies.mod.common.networking.VSGamePackets
import org.valkyrienskies.mod.common.util.BuoyancyHandlerAttachment
import org.valkyrienskies.mod.common.util.GameToPhysicsAdapter
import org.valkyrienskies.mod.common.util.ShipSettings
import org.valkyrienskies.mod.common.util.SplitHandler
import org.valkyrienskies.mod.common.util.SplittingDisablerAttachment
import org.valkyrienskies.mod.common.util.WaveBuoyancyAttachment

@SourceDebugExtension(["SMAP\nValkyrienSkiesMod.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ValkyrienSkiesMod.kt\norg/valkyrienskies/mod/common/ValkyrienSkiesMod\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,293:1\n381#2,7:294\n72#3,2:301\n72#3,2:304\n72#3,2:307\n72#3,2:310\n72#3,2:313\n72#3,2:316\n72#3,2:319\n72#3,2:322\n72#3,2:325\n1#4:303\n1#4:306\n1#4:309\n1#4:312\n1#4:315\n1#4:318\n1#4:321\n1#4:324\n1#4:327\n*S KotlinDebug\n*F\n+ 1 ValkyrienSkiesMod.kt\norg/valkyrienskies/mod/common/ValkyrienSkiesMod\n*L\n249#1:294,7\n262#1:301,2\n266#1:304,2\n270#1:307,2\n277#1:310,2\n281#1:313,2\n285#1:316,2\n289#1:319,2\n191#1:322,2\n201#1:325,2\n262#1:303\n266#1:306\n270#1:309\n277#1:312\n281#1:315\n285#1:318\n289#1:321\n191#1:324\n201#1:327\n*E\n"])
public object ValkyrienSkiesMod {
   public const val MOD_ID: String = "valkyrienskies"
   public final lateinit var TEST_CHAIR: class_2248
   public final lateinit var TEST_HINGE: class_2248
   public final lateinit var TEST_FLAP: class_2248
   public final lateinit var TEST_WING: class_2248
   public final lateinit var TEST_SPHERE: class_2248
   public final lateinit var TEST_THRUSTER: class_2248
   public final lateinit var TEST_ANTIGRAV: class_2248
   public final lateinit var CONNECTION_CHECKER_ITEM: class_1792
   public final lateinit var SHIP_CREATOR_ITEM: class_1792
   public final lateinit var SHIP_REMOVER_ITEM: class_1792
   public final lateinit var SHIP_ASSEMBLER_ITEM: class_1792
   public final lateinit var SHIP_CREATOR_ITEM_SMALLER: class_1792
   public final lateinit var AREA_ASSEMBLER_ITEM: class_1792
   public final lateinit var PHYSICS_ENTITY_CREATOR_ITEM: class_1792
   public final lateinit var SHIP_MOUNTING_ENTITY_TYPE: class_1299<ShipMountingEntity>
   public final lateinit var PHYSICS_ENTITY_TYPE: class_1299<out class_1297>
   public final lateinit var TEST_HINGE_BLOCK_ENTITY_TYPE: class_2591<TestHingeBlockEntity>
   public final lateinit var BLOCK_POS_COMPONENT: class_9331<class_2338>
   public final lateinit var TEST_THRUSTER_BLOCK_ENTITY_TYPE: class_2591<TestThrusterBlockEntity>
   public final lateinit var TEST_ANTIGRAV_BLOCK_ENTITY_TYPE: class_2591<TestAntigravBlockEntity>
   private final val dimensionalGTPAs: HashMap<String, GameToPhysicsAdapter> = HashMap()
   public final val VS_CREATIVE_TAB: class_5321<class_1761> = class_5321.method_29179(class_7924.field_44688, class_2960.method_60654("valkyrienskies"))
   public final val ASSEMBLE_BLACKLIST: class_6862<class_2248>

   @JvmStatic
   public final var currentServer: MinecraftServer?

   @JvmStatic
   public final val vsCoreProvider: VSCoreProvider by LazyKt.lazy({ 
      ServiceLoader.load(VSCoreProvider.class, VSCoreProvider.class.getClassLoader()).findFirst().orElseThrow({ 
         IllegalStateException("No VSCoreProvider implementation found via ServiceLoader!")
      }) as VSCoreProvider
   })
      public final get() {
         val var10000: Any = vsCoreProvider$delegate.getValue()
         return var10000 as VSCoreProvider
      }


   @JvmStatic
   public final val vsCore: VsiCore = vsCoreProvider.newVSCore()

   @JvmStatic
   public final val api: VsApiImpl by LazyKt.lazy({ 
      VsApiImpl(vsCore)
   })
      public final get() {
         return api$delegate.getValue() as VsApiImpl
      }


   public final val blockEntityPhysListeners: ConcurrentHashMap<String, ConcurrentHashMap<class_2338, Pair<Long?, BlockEntityPhysicsListener>>> =
      ConcurrentHashMap()
      public final val entityPhysListeners: ConcurrentHashMap<String, ConcurrentHashMap<Int, EntityPhysicsListener>> = ConcurrentHashMap()

   @JvmStatic
   public final lateinit var splitHandler: SplitHandler

   @JvmStatic
   public final val vsCoreClient: VsiCoreClient
      public final get() {
         val var10000: VsiCore = vsCore
         return var10000 as VsiCoreClient
      }


   public fun init() {
      val core: VsiCore = vsCore
      VSCoreConfig.SERVER.shipLoadDistance = 4096.0
      VSCoreConfig.SERVER.shipUnloadDistance = 4480.0
      VSCoreConfig.SERVER.physics.shipLoadFreezeSeconds = 0.0
      BlockStateInfo.INSTANCE.init()
      VSGamePackets.INSTANCE.register()
      VSGamePackets.INSTANCE.registerHandlers()
      val aabbModule: SimpleModule = SimpleModule()
      aabbModule.addSerializer(class_2338::class.java, BlockPosSerializer())
      aabbModule.addDeserializer(class_2338::class.java, BlockPosDeserializer())
      aabbModule.addKeySerializer(class_2338::class.java, BlockPosKeySerializer())
      aabbModule.addKeyDeserializer(class_2338::class.java, BlockPosKeyDeserializer())
      ObjectMapper().registerModule(aabbModule)
      splitHandler = SplitHandler(vsCore.hooks.getEnableBlockEdgeConnectivity(), vsCore.hooks.getEnableBlockCornerConnectivity())
      core.registerAttachment(ShipSettings::class.java)
      core.registerAttachment(SeatedControllingPlayer::class.java, { $this$registerAttachment: AttachmentRegistration.Builder ->
         `$this$registerAttachment`.useLegacySerializer()
         Unit.INSTANCE
      })
      core.registerAttachment(SplittingDisablerAttachment::class.java, { $this$registerAttachment: AttachmentRegistration.Builder ->
         `$this$registerAttachment`.useLegacySerializer()
         Unit.INSTANCE
      })
      core.registerAttachment(BuoyancyHandlerAttachment::class.java)
      core.registerAttachment(WaveBuoyancyAttachment::class.java)
      core.getShipLoadEvent().on({ p0: Any ->
         `$tmp0`(p0)
      })
      core.getPhysTickEvent().on({ p0: Any ->
         `$tmp0`(p0)
      })
      core.getShipUnloadEventClient().on({ p0: Any ->
         `$tmp0`(p0)
      })
   }

   public fun createCreativeTab(): class_1761 {
      val var10000: class_1761 = class_1761.method_47307(class_7915.field_41049, 0)
         .method_47321(class_2561.method_43471("itemGroup.valkyrienSkies") as class_2561)
         .method_47320(
            { 
               if (SHIP_CREATOR_ITEM != null)
                  class_1799(INSTANCE.SHIP_CREATOR_ITEM as class_1935)
                  else
                  (
                     if (SHIP_ASSEMBLER_ITEM != null)
                        class_1799(INSTANCE.SHIP_ASSEMBLER_ITEM as class_1935)
                        else
                        (if (CONNECTION_CHECKER_ITEM != null) class_1799(INSTANCE.CONNECTION_CHECKER_ITEM as class_1935) else class_1799.field_8037)
                  )
               }
         )
         .method_47317({ var0: class_8128, output: class_7704 ->
            if (TEST_CHAIR != null) {
               output.method_45421(INSTANCE.TEST_CHAIR as class_1935)
            }

            if (TEST_HINGE != null) {
               output.method_45421(INSTANCE.TEST_HINGE as class_1935)
            }

            if (TEST_FLAP != null) {
               output.method_45421(INSTANCE.TEST_FLAP as class_1935)
            }

            if (TEST_WING != null) {
               output.method_45421(INSTANCE.TEST_WING as class_1935)
            }

            if (TEST_THRUSTER != null) {
               output.method_45421(INSTANCE.TEST_THRUSTER as class_1935)
            }

            if (TEST_ANTIGRAV != null) {
               output.method_45421(INSTANCE.TEST_ANTIGRAV as class_1935)
            }

            if (CONNECTION_CHECKER_ITEM != null) {
               output.method_45421(INSTANCE.CONNECTION_CHECKER_ITEM as class_1935)
            }

            if (AREA_ASSEMBLER_ITEM != null) {
               output.method_45421(INSTANCE.AREA_ASSEMBLER_ITEM as class_1935)
            }

            if (PHYSICS_ENTITY_CREATOR_ITEM != null) {
               output.method_45421(INSTANCE.PHYSICS_ENTITY_CREATOR_ITEM as class_1935)
            }
         })
         .method_47324()
         return var10000
   }

   @JvmStatic
   public fun getOrCreateGTPA(dimensionId: String): GameToPhysicsAdapter {
      val `$this$getOrPut$iv`: java.util.Map = dimensionalGTPAs
      val `value$iv`: Any = dimensionalGTPAs.get(dimensionId)
      val var10000: Any
      if (`value$iv` == null) {
         val var5: Any = GameToPhysicsAdapter()
         `$this$getOrPut$iv`.put(dimensionId, var5)
         var10000 = var5
      } else {
         var10000 = `value$iv`
      }

      return var10000 as GameToPhysicsAdapter
   }

   public fun addBlockEntityPhysTicker(dimensionId: String, pos: class_2338, blockEntity: BlockEntityPhysicsListener) {
      val var10000: class_1937 = (blockEntity as class_2586).method_10997()
      if (var10000 != null) {
         if (!var10000.field_9236) {
            var shipId: java.lang.Long = null
            if (!var10000.field_9236) {
               val ship: Ship = ValkyrienSkies.getShipManagingBlock(var10000, pos)
               shipId = if (ship != null) ship.id else null
            }

            val `$this$getOrPut$iv`: ConcurrentMap = blockEntityPhysListeners
            var var14: Any = blockEntityPhysListeners.get(dimensionId)
            if (var14 == null) {
               val `default$iv`: Any = ConcurrentHashMap()
               var14 = `$this$getOrPut$iv`.putIfAbsent(dimensionId, `default$iv`)
               if (var14 == null) {
                  var14 = `default$iv`
               }
            }

            (var14 as java.util.Map).put(pos, Pair(shipId, blockEntity))
         }
      }
   }

   public fun getBlockEntityPhysTicker(dimensionId: String, pos: class_2338): BlockEntityPhysicsListener? {
      val `$this$getOrPut$iv`: ConcurrentMap = blockEntityPhysListeners
      var var10000: Any = blockEntityPhysListeners.get(dimensionId)
      if (var10000 == null) {
         val `default$iv`: Any = ConcurrentHashMap()
         var10000 = `$this$getOrPut$iv`.putIfAbsent(dimensionId, `default$iv`)
         if (var10000 == null) {
            var10000 = `default$iv`
         }
      }

      val var8: Pair = (var10000 as ConcurrentHashMap).get(pos) as Pair
      return if (var8 != null) var8.getSecond() as BlockEntityPhysicsListener else null
   }

   public fun removeBlockEntityPhysTicker(pos: class_2338, dimensionId: String) {
      val `$this$getOrPut$iv`: ConcurrentMap = blockEntityPhysListeners
      var var10000: Any = blockEntityPhysListeners.get(dimensionId)
      if (var10000 == null) {
         val `default$iv`: Any = ConcurrentHashMap()
         var10000 = `$this$getOrPut$iv`.putIfAbsent(dimensionId, `default$iv`)
         if (var10000 == null) {
            var10000 = `default$iv`
         }
      }

      (var10000 as ConcurrentHashMap).remove(pos)
   }

   public fun addEntityPhysTicker(dimensionId: String, entity: class_1297) {
      if (entity.method_37908() != null && !entity.method_37908().field_9236) {
         val `$this$getOrPut$iv`: ConcurrentMap = entityPhysListeners
         var var10000: Any = entityPhysListeners.get(dimensionId)
         if (var10000 == null) {
            val `default$iv`: Any = ConcurrentHashMap()
            var10000 = `$this$getOrPut$iv`.putIfAbsent(dimensionId, `default$iv`)
            if (var10000 == null) {
               var10000 = `default$iv`
            }
         }

         (var10000 as java.util.Map).put(entity.method_5628(), entity as EntityPhysicsListener)
      }
   }

   public fun removeEntityPhysTicker(entity: class_1297, dimensionId: String) {
      val `$this$getOrPut$iv`: ConcurrentMap = entityPhysListeners
      var var10000: Any = entityPhysListeners.get(dimensionId)
      if (var10000 == null) {
         val `default$iv`: Any = ConcurrentHashMap()
         var10000 = `$this$getOrPut$iv`.putIfAbsent(dimensionId, `default$iv`)
         if (var10000 == null) {
            var10000 = `default$iv`
         }
      }

      (var10000 as ConcurrentHashMap).remove(entity.method_5628())
   }

   public fun getEntityPhysTicker(dimensionId: String, entityId: Int): EntityPhysicsListener? {
      val `$this$getOrPut$iv`: ConcurrentMap = entityPhysListeners
      var var10000: Any = entityPhysListeners.get(dimensionId)
      if (var10000 == null) {
         val `default$iv`: Any = ConcurrentHashMap()
         var10000 = `$this$getOrPut$iv`.putIfAbsent(dimensionId, `default$iv`)
         if (var10000 == null) {
            var10000 = `default$iv`
         }
      }

      return (var10000 as ConcurrentHashMap).get(entityId) as EntityPhysicsListener
   }

   public fun getEntityPhysTicker(dimensionId: String, entity: class_1297): EntityPhysicsListener? {
      val `$this$getOrPut$iv`: ConcurrentMap = entityPhysListeners
      var var10000: Any = entityPhysListeners.get(dimensionId)
      if (var10000 == null) {
         val `default$iv`: Any = ConcurrentHashMap()
         var10000 = `$this$getOrPut$iv`.putIfAbsent(dimensionId, `default$iv`)
         if (var10000 == null) {
            var10000 = `default$iv`
         }
      }

      return (var10000 as ConcurrentHashMap).get(entity.method_5628()) as EntityPhysicsListener
   }

   @JvmStatic
   fun {
      val var10000: class_6862 = class_6862.method_40092(class_7924.field_41254, class_2960.method_60655("valkyrienskies", "assemble_blacklist"))
      ASSEMBLE_BLACKLIST = var10000
   }
}
