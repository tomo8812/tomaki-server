package org.valkyrienskies.mod.common.entity.handling

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import java.time.Duration
import java.util.HashMap
import net.minecraft.class_1297
import net.minecraft.class_1299
import net.minecraft.class_1676
import net.minecraft.class_2960
import net.minecraft.class_7922
import net.minecraft.class_7923
import org.valkyrienskies.mod.common.networking.PacketSyncVSEntityTypes
import org.valkyrienskies.mod.common.util.MinecraftPlayer
import org.valkyrienskies.mod.compat.CreateCompat

public object VSEntityManager {
   private final val entityHandlersNamed: HashMap<class_2960, VSEntityHandler> = HashMap()
   private final val namedEntityHandlers: HashMap<VSEntityHandler, class_2960> = HashMap()
   private final val entityHandlers: HashMap<class_1299<*>, VSEntityHandler> = HashMap()
   private final val default: WorldEntityHandler = WorldEntityHandler.INSTANCE
   private final var contraptionHandler: VSEntityHandler = DefaultShipyardEntityHandler.INSTANCE as VSEntityHandler
   private final val defaultHandlersCache: Cache<class_1299<*>, VSEntityHandler> = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5L)).build()
   private final val seatRegistryName: Regex = Regex("(?<![a-z])(seat|chair)(?![a-z])", RegexOption.IGNORE_CASE)

   public fun register(name: class_2960, entityHandler: VSEntityHandler) {
      entityHandlersNamed.put(name, entityHandler)
      namedEntityHandlers.put(entityHandler, name)
   }

   public fun registerContraptionHandler(contraptionHandler: VSEntityHandler) {
      contraptionHandler = contraptionHandler
   }

   public fun pair(entityType: class_1299<*>, entityHandler: VSEntityHandler) {
      entityHandlers.put(entityType, entityHandler)
   }

   public fun getHandler(entity: class_1297): VSEntityHandler {
      if (CreateCompat.isContraption(entity)) {
         return contraptionHandler
      } else {
         var var10000: VSEntityHandler = entityHandlers.get(entity.method_5864())
         if (var10000 == null) {
            var10000 = this.getDefaultHandler(entity)
         }

         return var10000
      }
   }

   private fun getDefaultHandler(entity: class_1297): VSEntityHandler {
      val var10000: Any = defaultHandlersCache.get(entity.method_5864(), { 
         INSTANCE.determineDefaultHandler(`$entity`)
      })
      return var10000 as VSEntityHandler
   }

   private fun determineDefaultHandler(entity: class_1297): VSEntityHandler {
      try {
         val ex: java.lang.String = entity.getClass().getSimpleName()
         val var10000: class_2960 = class_7923.field_41177.method_10221(entity.method_5864())
         if (entity !is class_1676) {
            if (!StringsKt.contains(ex, "SeatEntity", true)) {
               val var6: java.lang.String = var10000.method_12832()
               if (!seatRegistryName.containsMatchIn(var6)) {
                  return default
               }
            }
         }

         return DefaultShipyardEntityHandler.INSTANCE
      } catch (var5: Exception) {
         var5.printStackTrace()
         return default
      }
   }

   public fun getHandler(type: class_2960): VSEntityHandler? {
      return entityHandlersNamed.get(type)
   }

   public fun syncHandlers(player: MinecraftPlayer) {
      val var10001: class_7922 = class_7923.field_41177
      vsCore.simplePacketNetworking
         .sendToClient1(
            PacketSyncVSEntityTypes(
               MapsKt.toMap(
                  SequencesKt.mapNotNull(
                     CollectionsKt.asSequence(RangesKt.until(0, CollectionsKt.count(var10001 as java.lang.Iterable)) as java.lang.Iterable), { i: Int ->
                        val var10000: VSEntityHandler = entityHandlers.get(class_7923.field_41177.method_10200(i))
                        if (var10000 == null) null else TuplesKt.to(i, java.lang.String.valueOf(namedEntityHandlers.get(var10000)))
                     }
                  )
               )
            ),
            player
         )
      }

   @JvmStatic
   public fun isShipyardEntity(entity: class_1297): Boolean {
      return INSTANCE.getHandler(entity) == DefaultShipyardEntityHandler.INSTANCE
   }

   @JvmStatic
   fun {
      var var10000: VSEntityManager = INSTANCE
      var var10001: class_2960 = class_2960.method_60655("valkyrienskies", "shipyard")
      var10000.register(var10001, DefaultShipyardEntityHandler.INSTANCE)
      var10000 = INSTANCE
      var10001 = class_2960.method_60655("valkyrienskies", "default")
      var10000.register(var10001, WorldEntityHandler.INSTANCE)
   }
}
