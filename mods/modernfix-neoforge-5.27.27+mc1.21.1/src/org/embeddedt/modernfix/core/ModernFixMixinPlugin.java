package org.embeddedt.modernfix.core;

import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.embeddedt.modernfix.annotation.FeatureLevel;
import org.embeddedt.modernfix.core.config.ModernFixEarlyConfig;
import org.embeddedt.modernfix.core.config.Option;
import org.embeddedt.modernfix.core.config.OptionType;
import org.embeddedt.modernfix.core.launchplugin.CoreLaunchPluginService;
import org.embeddedt.modernfix.platform.ModernFixPlatformHooks;
import org.embeddedt.modernfix.world.ThreadDumper;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

public class ModernFixMixinPlugin implements IMixinConfigPlugin {
   private static final String MIXIN_PACKAGE_ROOT = "org.embeddedt.modernfix.mixin.";
   public final Logger logger = LogManager.getLogger("ModernFix");
   public ModernFixEarlyConfig config = null;
   public static ModernFixMixinPlugin instance;

   public ModernFixMixinPlugin() {
      ModernFixPlatformHooks.INSTANCE.getCustomModOptions();
      boolean firstConfig = instance == null;
      if (firstConfig) {
         instance = this;

         try {
            this.config = ModernFixEarlyConfig.load(new File("./config/modernfix-mixins.properties"));
         } catch (Exception e) {
            throw new RuntimeException("Could not load configuration file for ModernFix", e);
         }

         this.logger
            .info(
               "Loaded configuration file for ModernFix {}: {} options available, {} override(s) found",
               ModernFixPlatformHooks.INSTANCE.getVersionString(),
               this.config.getOptionCount(),
               this.config.getOptionOverrideCount()
            );
         if (activeFeatureLevel() != FeatureLevel.GA) {
            this.logger.warn("ModernFix stability level is set to {}. Features at this level may be unstable or cause crashes.", activeFeatureLevel());
         }

         this.config.getOptionMap().values().forEach(option -> {
            if (option.isOverridden()) {
               String source = "[unknown]";
               if (option.isUserDefined()) {
                  source = "user configuration";
               } else if (!ModernFixPlatformHooks.INSTANCE.isEarlyLoadingNormally()) {
                  source = "load error";
               } else if (option.isModDefined()) {
                  source = "mods [" + String.join(", ", option.getDefiningMods()) + "]";
               }

               this.logger.warn("Option '{}' overriden (by {}) to '{}'", option.getName(), source, option.getValue());
            }
         });
         if (ModernFixEarlyConfig.OPTIFINE_PRESENT) {
            this.logger
               .fatal("OptiFine detected. Use of ModernFix with OptiFine is not supported due to its impact on launch time and breakage of Forge features.");
         }

         try {
            Class.forName("sun.misc.Unsafe").getDeclaredMethod("defineAnonymousClass", Class.class, byte[].class, Object[].class);
         } catch (ReflectiveOperationException | NullPointerException e) {
            this.logger.info("Applying Nashorn fix");
            Properties properties = System.getProperties();
            properties.setProperty("nashorn.args", properties.getProperty("nashorn.args", "") + " --anonymous-classes=false");
         }

         ModernFixPlatformHooks.INSTANCE.injectPlatformSpecificHacks();
         if (instance.isOptionEnabled("feature.spam_thread_dump.ThreadDumper")) {
            ThreadDumper.obtainThreadDump();
            Thread t = new Thread() {
               @Override
               public void run() {
                  while (true) {
                     try {
                        Thread.sleep(60000L);
                        ModernFixMixinPlugin.this.logger.info("------ DEBUG THREAD DUMP (occurs every 60 seconds) ------");
                        ModernFixMixinPlugin.this.logger.info(ThreadDumper.obtainThreadDump());
                     } catch (InterruptedException | RuntimeException var2) {
                     }
                  }
               }
            };
            t.setDaemon(true);
            t.start();
         }

         if (ModernFixPlatformHooks.INSTANCE.isClient() && instance.isOptionEnabled("perf.thread_priorities.AdjustThreadCount")) {
            this.computeBetterThreadCount();
         }
      }
   }

   private void computeBetterThreadCount() {
      if (System.getProperty("max.bg.threads") == null) {
         int reservedCores = 3;
         int availableBackgroundCores = Math.max(1, Runtime.getRuntime().availableProcessors() - reservedCores);
         this.logger.info("Configuring Minecraft's max.bg.threads option with {} threads", availableBackgroundCores);
         System.setProperty("max.bg.threads", String.valueOf(availableBackgroundCores));
      }
   }

   public void onLoad(String mixinPackage) {
      CoreLaunchPluginService.install();
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      mixinClassName = ModernFixEarlyConfig.sanitize(mixinClassName);
      if (!mixinClassName.startsWith("org.embeddedt.modernfix.mixin.")) {
         this.logger
            .error("Expected mixin '{}' to start with package root '{}', treating as foreign and disabling!", mixinClassName, "org.embeddedt.modernfix.mixin.");
         return false;
      } else {
         String mixin = mixinClassName.substring("org.embeddedt.modernfix.mixin.".length());
         if (!instance.isOptionEnabled(mixin)) {
            this.logger.debug("Skipping mixin {}: disabled by configuration", mixin);
            return false;
         } else {
            String disabledBecauseMod = instance.config.getPermanentlyDisabledMixins().get(mixin);
            if (disabledBecauseMod != null) {
               this.logger.debug("Skipping mixin {}: disabled for mod compat ({})", mixin, disabledBecauseMod);
               return false;
            } else {
               this.logger.debug("Applying mixin {}", mixin);
               return true;
            }
         }
      }
   }

   public boolean isOptionEnabled(String mixin) {
      Option<?> option = instance.config.getEffectiveOptionForMixin(mixin);
      if (option == null) {
         String msg = "No rules matched mixin '{}', treating as foreign and disabling!";
         if (ModernFixPlatformHooks.INSTANCE.isDevEnv()) {
            this.logger.error(msg, mixin);
         } else {
            this.logger.debug(msg, mixin);
         }

         return false;
      } else {
         return option.getType() == OptionType.BOOLEAN && option.asBoolean().getValue();
      }
   }

   public <T> T getOptionValue(String optionName, Class<T> type) {
      return this.config.getOptionValue(optionName, type);
   }

   public static FeatureLevel activeFeatureLevel() {
      return instance.getOptionValue("stability_level", FeatureLevel.class);
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
      if (mixinClassName.equals("org.embeddedt.modernfix.common.mixin.perf.reduce_blockstate_cache_rebuilds.BlockStateBaseMixin")) {
         try {
            this.applyBlockStateCacheScan(targetClass);
         } catch (RuntimeException e) {
            instance.logger.error("Applying blockstate cache ASM patch failed", e);
         }
      }

      ModernFixPlatformHooks.INSTANCE.applyASMTransformers(mixinClassName, targetClass);
   }

   private void applyBlockStateCacheScan(ClassNode targetClass) {
      Set<String> initCacheMethodNames = ImmutableSet.of("m_60611_", "func_215692_c", "method_26200", "initCache");
      Set<String> whitelistedInjections = ImmutableSet.of("getFluidState", "method_26227", "m_60819_", "func_204520_s");
      Map<String, MethodNode> injectorMethodNames = new HashMap<>();
      Map<String, MethodNode> allMethods = new HashMap<>();
      Map<String, String> injectorMixinSource = new HashMap<>();
      String descriptor = Type.getDescriptor(MixinMerged.class);

      for (MethodNode m : targetClass.methods) {
         if ((m.access & 8) == 0) {
            allMethods.put(m.name, m);
            Set<AnnotationNode> seenNodes = new HashSet<>();
            if (m.invisibleAnnotations != null) {
               for (AnnotationNode ann : m.invisibleAnnotations) {
                  if (ann.desc.equals(descriptor)) {
                     seenNodes.add(ann);
                  }
               }
            }

            if (m.visibleAnnotations != null) {
               for (AnnotationNode ann : m.visibleAnnotations) {
                  if (ann.desc.equals(descriptor)) {
                     seenNodes.add(ann);
                  }
               }
            }

            if (seenNodes.size() > 0) {
               injectorMethodNames.put(m.name, m);

               for (AnnotationNode node : seenNodes) {
                  for (int i = 0; i < node.values.size(); i += 2) {
                     if (Objects.equals(node.values.get(i), "mixin")) {
                        injectorMixinSource.put(m.name, (String)node.values.get(i + 1));
                        break;
                     }
                  }
               }
            }
         }
      }

      Set<String> cacheCalledInjectors = new HashSet<>();

      for (MethodNode m : targetClass.methods) {
         if ((m.access & 8) == 0 && initCacheMethodNames.contains(m.name)) {
            for (AbstractInsnNode n : m.instructions) {
               if (n instanceof MethodInsnNode invoke
                  && ((MethodInsnNode)n).owner.equals(targetClass.name)
                  && injectorMethodNames.containsKey(((MethodInsnNode)n).name)) {
                  cacheCalledInjectors.add(invoke.name);
               }
            }
            break;
         }
      }

      Set<String> accessedFieldNames = new HashSet<>();
      Map<String, MethodNode> writingMethods = new HashMap<>(injectorMethodNames);
      writingMethods.keySet().retainAll(cacheCalledInjectors);
      int previousSize = 0;
      Set<String> checkedCalls = new HashSet<>();

      while (writingMethods.size() > previousSize) {
         previousSize = writingMethods.size();

         for (String name : new ArrayList<>(writingMethods.keySet())) {
            if (checkedCalls.add(name)) {
               for (AbstractInsnNode n : writingMethods.get(name).instructions) {
                  if (n instanceof MethodInsnNode invokeNode && invokeNode.owner.equals(targetClass.name)) {
                     MethodNode theMethod = allMethods.get(invokeNode.name);
                     if (theMethod != null) {
                        writingMethods.put(invokeNode.name, theMethod);
                     }
                  }
               }
            }
         }
      }

      writingMethods.forEach((namex, method) -> {
         for (AbstractInsnNode nx : method.instructions) {
            if (nx instanceof FieldInsnNode fieldAcc && fieldAcc.getOpcode() == 181 && fieldAcc.owner.equals(targetClass.name)) {
               accessedFieldNames.add(fieldAcc.name);
            }
         }
      });
      injectorMethodNames.forEach(
         (namex, method) -> {
            if (!whitelistedInjections.contains(namex) && !cacheCalledInjectors.contains(namex)) {
               boolean needInjection = false;

               for (AbstractInsnNode nx : method.instructions) {
                  if (nx instanceof FieldInsnNode fieldAcc && fieldAcc.getOpcode() == 180 && accessedFieldNames.contains(fieldAcc.name)) {
                     needInjection = true;
                     break;
                  }
               }

               if (needInjection) {
                  instance.logger
                     .info("Injecting BlockStateBase cache population hook into {} from {}", namex, injectorMixinSource.getOrDefault(namex, "[unknown mixin]"));
                  InsnList injection = new InsnList();
                  injection.add(new VarInsnNode(25, 0));
                  injection.add(new MethodInsnNode(182, targetClass.name, "mfix$generateCache", "()V"));
                  method.instructions.insert(injection);
               }
            }
         }
      );
   }
}
