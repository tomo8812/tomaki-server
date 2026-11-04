package net.fabricmc.loader.impl.bootstrap;

import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.lang.reflect.Field;
import java.util.Map;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingException;
import net.neoforged.fml.loading.BuiltInLanguageLoader;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;

public class FabricLoaderHackyInjector extends BuiltInLanguageLoader {
   public static final String NAME = "__fabric_loader_bootstrap";

   public FabricLoaderHackyInjector() {
      try {
         Field launchPluginsField = Launcher.class.getDeclaredField("launchPlugins");
         launchPluginsField.setAccessible(true);
         LaunchPluginHandler launchPluginHandler = (LaunchPluginHandler)launchPluginsField.get(Launcher.INSTANCE);
         Field pluginsField = LaunchPluginHandler.class.getDeclaredField("plugins");
         pluginsField.setAccessible(true);
         Map<String, ILaunchPluginService> plugins = (Map<String, ILaunchPluginService>)pluginsField.get(launchPluginHandler);
         ILaunchPluginService bootstrap = new FabricLoaderBootstrap();
         plugins.put(bootstrap.name(), bootstrap);
      } catch (Exception e) {
         throw new RuntimeException("Error injecting fabric loader bootstrap", e);
      }
   }

   public String name() {
      return "__fabric_loader_bootstrap";
   }

   public ModContainer loadMod(IModInfo info, ModFileScanData modFileScanResults, ModuleLayer layer) throws ModLoadingException {
      throw new UnsupportedOperationException();
   }
}
