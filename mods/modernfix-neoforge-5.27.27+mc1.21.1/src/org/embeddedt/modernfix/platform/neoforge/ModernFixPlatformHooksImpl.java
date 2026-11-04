package org.embeddedt.modernfix.platform.neoforge;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.ModLoadingIssue.Severity;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.TracingPrintStream;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.embeddedt.modernfix.core.ModernFixMixinPlugin;
import org.embeddedt.modernfix.neoforge.init.ModernFixForge;
import org.embeddedt.modernfix.platform.ModernFixPlatformHooks;
import org.embeddedt.modernfix.spark.SparkLaunchProfiler;
import org.embeddedt.modernfix.util.CommonModUtil;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.LoggerFactory;

public class ModernFixPlatformHooksImpl implements ModernFixPlatformHooks {
   private static final String verString = Optional.ofNullable(ModernFixMixinPlugin.class.getPackage().getImplementationVersion()).orElse("[unknown]");
   private static Multimap<String, String> modOptions;

   @Override
   public boolean isClient() {
      return FMLLoader.getDist() == Dist.CLIENT;
   }

   @Override
   public boolean isDedicatedServer() {
      return FMLLoader.getDist().isDedicatedServer();
   }

   @Override
   public String getVersionString() {
      return verString;
   }

   @Override
   public boolean modPresent(String modId) {
      return FMLLoader.getLoadingModList().getModFileById(modId) != null;
   }

   @Override
   public boolean isDevEnv() {
      return !FMLLoader.isProduction();
   }

   @Override
   public MinecraftServer getCurrentServer() {
      return ServerLifecycleHooks.getCurrentServer();
   }

   @Override
   public boolean isEarlyLoadingNormally() {
      List<ModLoadingIssue> issues = LoadingModList.get().getModLoadingIssues();
      return issues.isEmpty() ? true : issues.stream().noneMatch(issue -> issue.severity() == Severity.ERROR);
   }

   @Override
   public boolean isLoadingNormally() {
      return this.isEarlyLoadingNormally() && !ModLoader.hasErrors();
   }

   @Override
   public Path getGameDirectory() {
      return FMLPaths.GAMEDIR.get();
   }

   @Override
   public void sendPacket(ServerPlayer player, CustomPacketPayload packet) {
      PacketDistributor.sendToPlayer(player, packet, new CustomPacketPayload[0]);
   }

   @Override
   public void injectPlatformSpecificHacks() {
      if (ModernFixMixinPlugin.instance.isOptionEnabled("feature.spark_profile_launch.OnForge")) {
         CommonModUtil.runWithoutCrash(() -> SparkLaunchProfiler.start("launch"), "Failed to start profiler");
      }

      if (ModernFixMixinPlugin.instance.isOptionEnabled("feature.log_stdout_in_log_files.PrintStreamReplacement")) {
         System.setOut(new TracingPrintStream(LoggerFactory.getLogger("STDOUT"), System.out));
         System.setErr(new TracingPrintStream(LoggerFactory.getLogger("STDERR"), System.err));
      }
   }

   @Override
   public void applyASMTransformers(String mixinClassName, ClassNode targetClass) {
   }

   @Override
   public void onServerCommandRegister(Consumer<CommandDispatcher<CommandSourceStack>> handler) {
      NeoForge.EVENT_BUS.addListener(event -> handler.accept(event.getDispatcher()));
   }

   @Override
   public Multimap<String, String> getCustomModOptions() {
      if (modOptions == null) {
         modOptions = ArrayListMultimap.create();

         for (ModInfo meta : LoadingModList.get().getMods()) {
            meta.getConfigElement(new String[]{"modernfix:integration"}).ifPresent(optionsObj -> {
               if (optionsObj instanceof Map<Object, Object> options) {
                  options.forEach((key, value) -> {
                     if (key instanceof String && value instanceof String) {
                        modOptions.put(key, value);
                     }
                  });
               }
            });
         }
      }

      return modOptions;
   }

   @Override
   public void onLaunchComplete() {
      if (ModernFixMixinPlugin.instance.isOptionEnabled("feature.spark_profile_launch.OnForge")) {
         CommonModUtil.runWithoutCrash(() -> SparkLaunchProfiler.stop("launch"), "Failed to stop profiler");
      }

      ModernFixForge.launchDone = true;
   }

   @Override
   public String getPlatformName() {
      return "Forge";
   }
}
