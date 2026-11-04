package org.valkyrienskies.mod.common.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.mojang.logging.LogUtils
import java.nio.file.Files
import java.nio.file.Path
import kotlin.jvm.internal.SourceDebugExtension
import org.slf4j.Logger

@SourceDebugExtension(["SMAP\nVSClientConfigLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VSClientConfigLoader.kt\norg/valkyrienskies/mod/common/config/VSClientConfigLoader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,73:1\n1#2:74\n*E\n"])
public object VSClientConfigLoader {
   private final val LOGGER: Logger = LogUtils.getLogger()
   private final val CONFIG_FILE: Path
   private final val mapper: ObjectMapper

   @JvmStatic
   public fun loadOrCreate() {
      try {
         if (!Files.exists(CONFIG_FILE)) {
            INSTANCE.writeConfig()
            LOGGER.info("Created default VS2 client config at {}", CONFIG_FILE.toAbsolutePath())
            return
         }

         var var8: JsonNode = mapper.readTree(CONFIG_FILE.toFile()).get("client")
         if (var8 != null) {
            var8 = if (!var8.isMissingNode() && !var8.isNull()) var8 else null
            if (var8 != null) {
               mapper.readerForUpdating(VSClientConfig.CLIENT).readValue(var8)
            }
         }

         LOGGER.info("Loaded VS2 client config from {}", CONFIG_FILE.toAbsolutePath())
         INSTANCE.writeConfig()
      } catch (var5: Exception) {
         LOGGER.warn("Failed to load VS2 client config at {} ({}); using built-in defaults.", arrayOf(CONFIG_FILE.toAbsolutePath(), var5.getMessage(), var5))
      }
   }

   @JvmStatic
   public fun save() {
      try {
         INSTANCE.writeConfig()
      } catch (var2: Exception) {
         LOGGER.warn("Failed to save VS2 client config to {}: {}", arrayOf(CONFIG_FILE.toAbsolutePath(), var2.getMessage(), var2))
      }
   }

   private fun writeConfig() {
      val var10000: Path = CONFIG_FILE.getParent()
      if (var10000 != null) {
         Files.createDirectories(var10000)
      }

      mapper.writeValue(CONFIG_FILE.toFile(), MapsKt.linkedMapOf(arrayOf(TuplesKt.to("client", VSClientConfig.CLIENT))))
   }

   @JvmStatic
   fun {
      val var10000: Path = Path.of("config", "valkyrienskies_client.json")
      CONFIG_FILE = var10000
      val var0: ObjectMapper = ObjectMapper()
      var0.enable(SerializationFeature.INDENT_OUTPUT)
      mapper = var0
   }
}
