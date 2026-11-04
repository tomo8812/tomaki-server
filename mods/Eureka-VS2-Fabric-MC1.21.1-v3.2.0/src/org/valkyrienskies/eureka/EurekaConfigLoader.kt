package org.valkyrienskies.eureka

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.mojang.logging.LogUtils
import java.nio.file.Files
import java.nio.file.Path
import kotlin.jvm.internal.SourceDebugExtension
import org.slf4j.Logger

@SourceDebugExtension(["SMAP\nEurekaConfigLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EurekaConfigLoader.kt\norg/valkyrienskies/eureka/EurekaConfigLoader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,91:1\n1#2:92\n*E\n"])
public object EurekaConfigLoader {
   private final val LOGGER: Logger = LogUtils.getLogger()
   private final val CONFIG_FILE: Path
   private final val mapper: ObjectMapper

   @JvmStatic
   public fun loadOrCreate() {
      try {
         if (!Files.exists(CONFIG_FILE)) {
            INSTANCE.writeConfig()
            LOGGER.info("Created default Eureka config at {}", CONFIG_FILE.toAbsolutePath())
            return
         }

         val e: JsonNode = mapper.readTree(CONFIG_FILE.toFile())
         var var18: JsonNode = e.get("server")
         if (var18 != null) {
            var18 = if (!var18.isMissingNode() && !var18.isNull()) var18 else null
            if (var18 != null) {
               mapper.readerForUpdating(EurekaConfig.ADVANCED).readValue(var18)
            }
         }

         var18 = e.get("serverVanilla")
         if (var18 != null) {
            var18 = if (!var18.isMissingNode() && !var18.isNull()) var18 else null
            if (var18 != null) {
               mapper.readerForUpdating(EurekaConfig.VANILLA).readValue(var18)
            }
         }

         var18 = e.get("client")
         if (var18 != null) {
            var18 = if (!var18.isMissingNode() && !var18.isNull()) var18 else null
            if (var18 != null) {
               mapper.readerForUpdating(EurekaConfig.CLIENT).readValue(var18)
            }
         }

         LOGGER.info("Loaded Eureka config from {}", CONFIG_FILE.toAbsolutePath())
         INSTANCE.writeConfig()
      } catch (var5: Exception) {
         LOGGER.warn("Failed to load Eureka config at {} ({}); using built-in defaults.", arrayOf(CONFIG_FILE.toAbsolutePath(), var5.getMessage(), var5))
      }
   }

   @JvmStatic
   public fun save() {
      try {
         INSTANCE.writeConfig()
      } catch (var2: Exception) {
         LOGGER.warn("Failed to save Eureka config at {} ({})", arrayOf(CONFIG_FILE.toAbsolutePath(), var2.getMessage(), var2))
      }
   }

   private fun writeConfig() {
      val var10000: Path = CONFIG_FILE.getParent()
      if (var10000 != null) {
         Files.createDirectories(var10000)
      }

      mapper.writeValue(
         CONFIG_FILE.toFile(),
         MapsKt.linkedMapOf(
            arrayOf(
               TuplesKt.to("client", EurekaConfig.CLIENT),
               TuplesKt.to("server", EurekaConfig.ADVANCED),
               TuplesKt.to("serverVanilla", EurekaConfig.VANILLA)
            )
         )
      )
   }

   @JvmStatic
   fun {
      val var10000: Path = Path.of("config", "vs_eureka.json")
      CONFIG_FILE = var10000
      val var0: ObjectMapper = ObjectMapper()
      var0.enable(SerializationFeature.INDENT_OUTPUT)
      mapper = var0
   }
}
