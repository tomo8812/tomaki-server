package org.valkyrienskies.mod.common.config

import java.util.HashMap
import java.util.LinkedHashSet
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.common.ModConfigSpec.Builder
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue
import org.valkyrienskies.core.impl.api_impl.config.VsiConfigModelImpl
import org.valkyrienskies.core.internal.config.VsiConfigModel
import org.valkyrienskies.mod.api.config.VSConfigApi
import org.valkyrienskies.mod.common.hooks.VSGameEvents

public object VSConfigUpdater {
   @JvmStatic
   public final val forgeConfigValuesMap: HashMap<String, ConfigValue<*>> = HashMap()

   private final val configValueConsumer: (String, ConfigValue<*>) -> Unit = { name: java.lang.String, value: ConfigValue ->
      forgeConfigValuesMap.put(name, value)
      Unit.INSTANCE
   }

   private final val core_server_config: VsiConfigModel = vsCore.getServerConfig()
   public final val CORE_SERVER_SPEC: ModConfigSpec
   private final val server_config: VsiConfigModelImpl = VSConfigApi.buildVSConfigModel(VSGameConfig.SERVER)
   public final val SERVER_SPEC: ModConfigSpec
   private final val common_config: VsiConfigModelImpl = VSConfigApi.buildVSConfigModel(VSGameConfig.COMMON)
   public final val COMMON_SPEC: ModConfigSpec
   private final val client_config: VsiConfigModelImpl = VSConfigApi.buildVSConfigModel(VSGameConfig.CLIENT)
   public final val CLIENT_SPEC: ModConfigSpec

   public fun applyFromConfigLoad(spec: ModConfigSpec, getByKey: (String) -> Any?) {
      val var10000: Pair
      if (spec == CORE_SERVER_SPEC) {
         var10000 = TuplesKt.to(core_server_config, ConfigType.CORE_SERVER)
      } else if (spec == SERVER_SPEC) {
         var10000 = TuplesKt.to(server_config, ConfigType.SERVER)
      } else if (spec == COMMON_SPEC) {
         var10000 = TuplesKt.to(common_config, ConfigType.COMMON)
      } else {
         if (!(spec == CLIENT_SPEC)) {
            return
         }

         var10000 = TuplesKt.to(client_config, ConfigType.CLIENT)
      }

      val var7: VsiConfigModel = var10000.component1() as VsiConfigModel
      val type: ConfigType = var10000.component2() as ConfigType
      val updatedEntries: java.util.Set = LinkedHashSet()
      VSConfigApi.INSTANCE.applyToModel(var7, getByKey, type, updatedEntries)
      if (!updatedEntries.isEmpty()) {
         VSGameEvents.INSTANCE.configUpdated.emit(updatedEntries)
      }
   }

   @JvmStatic
   fun {
      var var10000: ModConfigSpec = VSConfigApi.buildModConfigSpec(core_server_config.root, Builder(), configValueConsumer).build()
      CORE_SERVER_SPEC = var10000
      var10000 = VSConfigApi.buildModConfigSpec(server_config.root, Builder(), configValueConsumer).build()
      SERVER_SPEC = var10000
      var10000 = VSConfigApi.buildModConfigSpec(common_config.root, Builder(), configValueConsumer).build()
      COMMON_SPEC = var10000
      var10000 = VSConfigApi.buildModConfigSpec(client_config.root, Builder(), configValueConsumer).build()
      CLIENT_SPEC = var10000
   }
}
