package org.embeddedt.modernfix.core.launchplugin;

import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService.Phase;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.embeddedt.modernfix.core.launchplugin.transformer.CapabilityProviderTransformer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreLaunchPluginService implements ILaunchPluginService {
   private static final Logger LOGGER = LoggerFactory.getLogger("ModernFixLaunchPlugin");
   private static final EnumSet<Phase> GO = EnumSet.of(Phase.AFTER);
   private static final EnumSet<Phase> NOGO = EnumSet.noneOf(Phase.class);
   private static final Map<String, CoreLaunchPluginService.Transformer> TRANSFORMERS = Map.of(
      "net.minecraftforge.common.capabilities.CapabilityProvider", new CapabilityProviderTransformer()
   );

   public static void install() {
      try {
         Field launchPluginsField = Launcher.class.getDeclaredField("launchPlugins");
         launchPluginsField.setAccessible(true);
         LaunchPluginHandler launchPluginHandler = (LaunchPluginHandler)launchPluginsField.get(Launcher.INSTANCE);
         Field pluginsField = LaunchPluginHandler.class.getDeclaredField("plugins");
         pluginsField.setAccessible(true);
         Map<String, ILaunchPluginService> plugins = (Map<String, ILaunchPluginService>)pluginsField.get(launchPluginHandler);
         CoreLaunchPluginService service = new CoreLaunchPluginService();
         LinkedHashMap<String, ILaunchPluginService> newMap = new LinkedHashMap<>(plugins);
         newMap.put(service.name(), service);
         pluginsField.set(launchPluginHandler, newMap);
      } catch (Exception e) {
         LOGGER.error("Error installing launch plugin service", e);
      }
   }

   public String name() {
      return "modernfix";
   }

   public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
      return !isEmpty && TRANSFORMERS.containsKey(classType.getClassName()) ? GO : NOGO;
   }

   public int processClassWithFlags(Phase phase, ClassNode classNode, Type classType, String reason) {
      if (classNode == null) {
         return 0;
      }

      CoreLaunchPluginService.Transformer transformer = TRANSFORMERS.get(classType.getClassName());
      return transformer == null ? 0 : transformer.transform(classNode);
   }

   public interface Transformer {
      int transform(ClassNode var1);
   }
}
