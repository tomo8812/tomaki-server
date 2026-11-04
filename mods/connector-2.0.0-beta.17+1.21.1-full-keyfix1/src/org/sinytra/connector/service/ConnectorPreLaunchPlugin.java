package org.sinytra.connector.service;

import cpw.mods.modlauncher.api.NamedPath;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService.ITransformerLoader;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService.Phase;
import java.util.EnumSet;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.connector.ConnectorEarlyLoader;
import org.sinytra.connector.service.hacks.FabricASMFixer;

public class ConnectorPreLaunchPlugin implements ILaunchPluginService {
   public static final String NAME = "connector_pre_launch";
   private static final EnumSet<Phase> YAY = EnumSet.of(Phase.AFTER);
   private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

   public String name() {
      return "connector_pre_launch";
   }

   public void initializeLaunch(ITransformerLoader transformerLoader, NamedPath[] specialPaths) {
      ConnectorEarlyLoader.init();
      FabricMixinBootstrap.init();
      FabricASMFixer.injectMinecraftModuleReader();
   }

   public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
      return classType.getInternalName().startsWith("net/minecraft/") ? YAY : NAY;
   }

   public int processClassWithFlags(Phase phase, ClassNode classNode, Type classType, String reason) {
      boolean rewrite = false;

      for (FieldNode field : classNode.fields) {
         if ((field.access & 7) != 2) {
            field.access = field.access & -8 | 1;
            rewrite = true;
         }
      }

      for (MethodNode method : classNode.methods) {
         if ((method.access & 7) != 2) {
            method.access = method.access & -8 | 1;
            rewrite = true;
         }
      }

      return rewrite ? 256 : 0;
   }
}
