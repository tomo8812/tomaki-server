package org.embeddedt.modernfix.spark;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Stream;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.SparkPlugin;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.command.sender.CommandSender.Data;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.platform.PlatformInfo.Type;
import me.lucko.spark.common.sampler.Sampler;
import me.lucko.spark.common.sampler.SamplerSettings;
import me.lucko.spark.common.sampler.ThreadDumper;
import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.common.sampler.Sampler.ExportProps;
import me.lucko.spark.common.sampler.async.AsyncSampler;
import me.lucko.spark.common.sampler.async.SampleCollector.Execution;
import me.lucko.spark.common.sampler.java.JavaSampler;
import me.lucko.spark.common.sampler.java.MergeStrategy;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.proto.SparkSamplerProtos.SamplerData;
import net.minecraft.SharedConstants;
import org.embeddedt.modernfix.core.ModernFixMixinPlugin;
import org.embeddedt.modernfix.platform.ModernFixPlatformHooks;

public class SparkLaunchProfiler {
   private static PlatformInfo platformInfo = new SparkLaunchProfiler.ModernFixPlatformInfo();
   private static CommandSender commandSender = new SparkLaunchProfiler.ModernFixCommandSender();
   private static Map<String, Sampler> ongoingSamplers = new Object2ReferenceOpenHashMap();
   private static ExecutorService executor = Executors.newSingleThreadScheduledExecutor(
      new ThreadFactoryBuilder().setDaemon(true).setNameFormat("spark-modernfix-async-worker").build()
   );
   private static final SparkPlatform platform = new SparkPlatform(new SparkLaunchProfiler.ModernFixSparkPlugin());
   private static final String ALLOW_SPARK_PROFILING_PROP = "modernfix.allowSparkProfiling";
   private static final boolean USE_JAVA_SAMPLER_FOR_LAUNCH = !Boolean.getBoolean("modernfix.profileWithAsyncSampler");
   private static final boolean ALLOW_SPARK_PROFILING = Boolean.getBoolean("modernfix.allowSparkProfiling");
   private static final int SAMPLING_INTERVAL = Integer.getInteger("modernfix.profileSamplingIntervalMicroseconds", 4000);
   private static final String THREAD_GROUPER = System.getProperty("modernfix.profileSamplingThreadGrouper", "by-pool");
   private static final boolean SAVE_TO_FILE = Boolean.getBoolean("modernfix.profileSaveToFile");

   private static boolean checkSparkProfilingAllowed() {
      if (!ALLOW_SPARK_PROFILING && !SAVE_TO_FILE) {
         ModernFixMixinPlugin.instance
            .logger
            .fatal(
               "To reduce excessive load on the Spark servers, you must set -Dmodernfix.allowSparkProfiling=true in your JVM arguments for profiling to proceed. Please do this and relaunch the game. Alternatively, set -Dmodernfix.profileSaveToFile=true to write the results to disk instead of uploading them."
            );
         return false;
      } else {
         return true;
      }
   }

   public static void start(String key) {
      if (checkSparkProfilingAllowed()) {
         if (!ongoingSamplers.containsKey(key)) {
            SamplerSettings settings = new SamplerSettings(
               SAMPLING_INTERVAL, ThreadDumper.ALL, (ThreadGrouper)ThreadGrouper.parseConfigSetting(THREAD_GROUPER).get(), -1L, false, true
            );

            Sampler sampler;
            try {
               if (USE_JAVA_SAMPLER_FOR_LAUNCH) {
                  throw new UnsupportedOperationException();
               }

               sampler = new AsyncSampler(platform, settings, new Execution(SAMPLING_INTERVAL));
            } catch (UnsupportedOperationException e) {
               sampler = new JavaSampler(platform, settings);
            }

            ongoingSamplers.put(key, sampler);
            ModernFixMixinPlugin.instance.logger.warn("Profiler has started for stage [{}]...", key);
            sampler.start();
            sampler.getFuture().whenComplete((s, throwable) -> {
               if (throwable != null) {
                  ModernFixMixinPlugin.instance.logger.error("Profiler for stage [" + key + "] terminated abnormally", throwable);
               }
            });
         }
      }
   }

   public static void stop(String key) {
      Sampler sampler = ongoingSamplers.remove(key);
      if (sampler != null) {
         sampler.stop(false);
         output(key, sampler);
      }
   }

   private static void output(String key, Sampler sampler) {
      executor.execute(
         () -> {
            ModernFixMixinPlugin.instance.logger.warn("Stage [{}] profiler has stopped! Exporting results...", key);
            SamplerData output = sampler.toProto(
               platform,
               new ExportProps()
                  .creator(new Data(commandSender.getName(), commandSender.getUniqueId()))
                  .comment("Stage: " + key)
                  .mergeStrategy(MergeStrategy.SAME_METHOD)
                  .classSourceLookup(platform::createClassSourceLookup)
            );
            if (!SAVE_TO_FILE) {
               try {
                  String urlKey = platform.getBytebinClient().postContent(output, "application/x-spark-sampler").key();
                  String url = platform.getViewerUrl() + urlKey;
                  ModernFixMixinPlugin.instance.logger.warn("Profiler results for Stage [{}]: {}", key, url);
                  return;
               } catch (Throwable e) {
                  ModernFixMixinPlugin.instance.logger.error("An error occurred whilst uploading the results, attempting to save them to disk instead.", e);
               }
            }

            saveToDisk(key, output);
         }
      );
   }

   private static void saveToDisk(String key, SamplerData output) {
      try {
         Path file = platform.resolveSaveFile("profile-" + key, "sparkprofile");
         Files.write(file, output.toByteArray());
         ModernFixMixinPlugin.instance.logger.warn("Profiler results for Stage [{}] have been written to {}", key, file);
         ModernFixMixinPlugin.instance.logger.warn("You can view the profile file using the web app @ {}", platform.getViewerUrl());
      } catch (Throwable e) {
         ModernFixMixinPlugin.instance.logger.fatal("An error occurred whilst saving the results for Stage [{}] to disk.", key, e);
      }
   }

   public static class ModernFixCommandSender implements CommandSender {
      private final UUID uuid = UUID.randomUUID();
      private final String name = "ModernFix";

      public String getName() {
         return this.name;
      }

      public UUID getUniqueId() {
         return this.uuid;
      }

      public boolean hasPermission(String s) {
         return true;
      }

      public void sendMessage(Component component) {
      }
   }

   static class ModernFixPlatformInfo implements PlatformInfo {
      public Type getType() {
         return ModernFixPlatformHooks.INSTANCE.isClient() ? Type.CLIENT : Type.SERVER;
      }

      public String getName() {
         return ModernFixPlatformHooks.INSTANCE.getPlatformName();
      }

      public String getBrand() {
         return this.getName();
      }

      public String getVersion() {
         return ModernFixPlatformHooks.INSTANCE.getVersionString();
      }

      public String getMinecraftVersion() {
         return SharedConstants.getCurrentVersion().getName();
      }
   }

   static class ModernFixSparkPlugin implements SparkPlugin {
      public String getVersion() {
         return "1.0";
      }

      public Path getPluginDirectory() {
         return ModernFixPlatformHooks.INSTANCE.getGameDirectory().resolve("spark-modernfix");
      }

      public String getCommandName() {
         return "spark-modernfix";
      }

      public Stream<? extends CommandSender> getCommandSenders() {
         return Stream.of();
      }

      public void executeAsync(Runnable runnable) {
         SparkLaunchProfiler.executor.execute(runnable);
      }

      public void log(Level level, String s) {
         ModernFixMixinPlugin.instance.logger.warn(s);
      }

      public void log(Level level, String s, Throwable t) {
         ModernFixMixinPlugin.instance.logger.warn(s, t);
      }

      public PlatformInfo getPlatformInfo() {
         return SparkLaunchProfiler.platformInfo;
      }
   }
}
