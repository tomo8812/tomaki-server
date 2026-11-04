package org.embeddedt.modernfix.neoforge;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ModernFixConfig {
   private static final Builder COMMON_BUILDER = new Builder();
   public static ModConfigSpec COMMON_CONFIG = COMMON_BUILDER.build();
   public static ConfigValue<List<? extends String>> BLACKLIST_ASYNC_JEI_PLUGINS;
   private static Set<ResourceLocation> jeiPluginBlacklist;

   public static Set<ResourceLocation> getJeiPluginBlacklist() {
      if (jeiPluginBlacklist == null) {
         jeiPluginBlacklist = ((List)BLACKLIST_ASYNC_JEI_PLUGINS.get()).stream().<ResourceLocation>map(ResourceLocation::parse).collect(Collectors.toSet());
      }

      return jeiPluginBlacklist;
   }

   static {
      Predicate<Object> locationValidator = o -> o instanceof String && ((String)o).contains(":");
      BLACKLIST_ASYNC_JEI_PLUGINS = COMMON_BUILDER.comment("These JEI plugins will be loaded on the main thread")
         .defineList("blacklist_async_jei_plugins", ImmutableList.of("jepb:jei_plugin"), locationValidator);
   }
}
