package net.fabricmc.loader.impl.bootstrap;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.NamedPath;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService.ITransformerLoader;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService.Phase;
import java.util.EnumSet;
import java.util.List;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.Type;
import org.slf4j.Logger;

public class FabricLoaderBootstrap implements ILaunchPluginService {
   private static final String NAME = "fabric_loader_bootstrap";
   private static final EnumSet<Phase> NOPE = EnumSet.noneOf(Phase.class);
   private static final Logger LOGGER = LogUtils.getLogger();

   public String name() {
      return "fabric_loader_bootstrap";
   }

   public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
      return NOPE;
   }

   public void initializeLaunch(ITransformerLoader transformerLoader, NamedPath[] specialPaths) {
      LOGGER.info("Propagating FML mod list to Fabric Loader");
      List<ModInfo> mods = LoadingModList.get().getMods();
      FabricLoaderImpl.INSTANCE.addFmlMods(mods);
   }
}
