package org.valkyrienskies.core.impl.util.serialization

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.fasterxml.jackson.module.kotlin.ExtensionsKt
import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import org.valkyrienskies.core.impl.shadow.GB
import org.valkyrienskies.core.impl.shadow.GC
import org.valkyrienskies.core.impl.shadow.GD
import org.valkyrienskies.core.impl.shadow.GE
import org.valkyrienskies.core.impl.shadow.GF
import org.valkyrienskies.core.impl.shadow.GJ
import org.valkyrienskies.core.impl.shadow.GQ
import org.valkyrienskies.core.impl.shadow.GR

public object VSJacksonUtil {
   public final val defaultMapper: CBORMapper = CBORMapper()
   public final val packetMapper: CBORMapper = CBORMapper()
   public final val deltaMapper: CBORMapper = CBORMapper()
   public final val configMapper: ObjectMapper = ObjectMapper()
   public final val dtoMapper: ObjectMapper = ObjectMapper()

   public fun configureAll(configure: (ObjectMapper) -> Unit) {
      configure(defaultMapper)
      configure(packetMapper)
      configure(deltaMapper)
      configure(configMapper)
   }

   private fun configureConfigMapper(mapper: ObjectMapper) {
      this.configureMapper(mapper)
      mapper.enable(SerializationFeature.INDENT_OUTPUT)
   }

   private fun configurePacketMapper(mapper: ObjectMapper) {
      this.configureMapper(mapper)
      val var2: Array<Class>
      (var2 = arrayOfNulls(1))[0] = GJ::class.java
      GQ.a(mapper, GE(var2))
      mapper.addMixIn(ShipData::class.java, VSJacksonUtil.a::class.java)
   }

   private fun configureDeltaMapper(mapper: ObjectMapper) {
      this.configureMapper(mapper)
      val var2: Array<Class>
      (var2 = arrayOfNulls(2))[0] = GJ::class.java
      var2[1] = GB::class.java
      GQ.a(mapper, GE(var2))
      mapper.addMixIn(ShipData::class.java, VSJacksonUtil.a::class.java)
   }

   private fun configureDtoMapper(mapper: ObjectMapper) {
      this.registerStandardModules(mapper)
      mapper.setVisibility(
         mapper.getVisibilityChecker()
            .withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
            .withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
            .withCreatorVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
      )
   }

   private fun configureMapper(mapper: ObjectMapper) {
      this.registerStandardModules(mapper)
      mapper.setVisibility(
         mapper.getVisibilityChecker()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
      )
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
   }

   private fun registerStandardModules(mapper: ObjectMapper) {
      val var10000: ObjectMapper = mapper.registerModule(GF()).registerModule(GR()).registerModule(GD()).registerModule(GC())
      ExtensionsKt.registerKotlinModule(var10000)
   }

   @JvmStatic
   fun {
      INSTANCE.configureMapper(defaultMapper)
      INSTANCE.configurePacketMapper(packetMapper)
      INSTANCE.configureDeltaMapper(deltaMapper)
      INSTANCE.configureConfigMapper(configMapper)
      INSTANCE.configureDtoMapper(dtoMapper)
   }

   @JsonSerialize(as = ShipDataCommon::class)
   private object a
}
