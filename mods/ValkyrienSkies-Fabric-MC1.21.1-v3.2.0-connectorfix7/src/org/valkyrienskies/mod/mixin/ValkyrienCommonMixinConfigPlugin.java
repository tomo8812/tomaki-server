package org.valkyrienskies.mod.mixin;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.valkyrienskies.mod.compat.LoadedMods;
import org.valkyrienskies.mod.compat.VSRenderer;

public class ValkyrienCommonMixinConfigPlugin implements IMixinConfigPlugin {
   private static final boolean PATH_FINDING_DEBUG = "false".equals(System.getProperty("org.valkyrienskies.render_pathfinding"));
   private static VSRenderer vsRenderer = null;

   public static VSRenderer getVSRenderer() {
      if (vsRenderer == null) {
         vsRenderer = getVSRendererHelper();
      }

      return vsRenderer;
   }

   private static VSRenderer getVSRendererHelper() {
      if (classExists("optifine.OptiFineTransformationService")) {
         return VSRenderer.OPTIFINE;
      } else {
         return classExists("net.caffeinemc.mods.sodium.client.SodiumClientMod") ? VSRenderer.SODIUM : VSRenderer.VANILLA;
      }
   }

   private static boolean classExists(String className) {
      try {
         Class.forName(className, false, ValkyrienCommonMixinConfigPlugin.class.getClassLoader());
         return true;
      } catch (ClassNotFoundException ex) {
         return false;
      }
   }

   public void onLoad(String s) {
      MixinExtrasBootstrap.init();
      Mixins.registerErrorHandlerClass("org.valkyrienskies.mod.mixin.ValkyrienMixinErrorHandler");
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String s, String mixinClassName) {
      VSRenderer renderer = getVSRenderer();
      if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.immersive_portals")) {
         return LoadedMods.getImmersivePortals();
      } else if (mixinClassName.equals("org.valkyrienskies.mod.mixin.client.world.MixinClientChunkCache")
         || mixinClassName.equals("org.valkyrienskies.mod.mixin.mod_compat.vanilla_renderer.MixinViewAreaVanilla")) {
         return !LoadedMods.getImmersivePortals();
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.sodium")) {
         return renderer == VSRenderer.SODIUM;
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.optifine_vanilla")) {
         return renderer == VSRenderer.VANILLA || renderer == VSRenderer.OPTIFINE;
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.vanilla_renderer")) {
         return renderer == VSRenderer.VANILLA;
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.optifine")) {
         return renderer == VSRenderer.OPTIFINE;
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.feature.render_pathfinding")) {
         return PATH_FINDING_DEBUG;
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.flywheel")) {
         return LoadedMods.getFlywheel() == LoadedMods.FlywheelVersion.V1;
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.flywheel_renderer")) {
         return LoadedMods.getFlywheel() == LoadedMods.FlywheelVersion.V1;
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.old_flywheel")) {
         return LoadedMods.getFlywheel() == LoadedMods.FlywheelVersion.V06;
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.bluemap")) {
         return LoadedMods.getBluemap().matches("^5\\.3(-.*)?$");
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.common_create.client.trackOutlines")
         && classExists("org.valkyrienskies.create_interactive.mixin.client.MixinTrackBlockOutline")) {
         MixinService.getService()
            .getLogger("mixin")
            .info(
               "[VS2] found Interactive, disabling VS2's trackOutline Compat - " + mixinClassName.substring(mixinClassName.lastIndexOf(".") + 1), new Object[0]
            );
         return false;
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.common_create")) {
         return LoadedMods.getCreate() || LoadedMods.getOldCreate();
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.old_create")) {
         return LoadedMods.getOldCreate();
      } else if (mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.create")) {
         return LoadedMods.getCreate();
      } else if (mixinClassName.equals("org.valkyrienskies.mod.mixin.mod_compat.etf.MixinBlockEntity")
         && !classExists("traben.entity_texture_features.utils.ETFEntity")) {
         return false;
      } else {
         return mixinClassName.contains("org.valkyrienskies.mod.mixin.mod_compat.alex_caves") ? LoadedMods.getAlexCaves() : true;
      }
   }

   public void acceptTargets(Set<String> set, Set<String> set1) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
   }

   public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
   }
}
