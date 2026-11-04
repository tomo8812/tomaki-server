package com.kishku7.chunksmith.platform.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kishku7.chunksmith.platform.Config;
import com.kishku7.chunksmith.platform.LodMode;
import com.kishku7.chunksmith.util.Input;
import com.kishku7.chunksmith.util.Translator;
import com.kishku7.chunksmith.worldenter.WorldEnterCenter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GsonConfig implements Config {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static final double TARGET_MSPT_MIN = 54.0;
   private static final double TARGET_MSPT_MAX = 1000.0;
   private static final double TARGET_MSPT_DEFAULT = 150.0;
   private static final long MAX_CHUNK_MILLIS_MIN = 100L;
   private static final long MAX_CHUNK_MILLIS_MAX = 60000L;
   private static final long MAX_CHUNK_MILLIS_DEFAULT = 750L;
   private static final long MAX_QUEUED_WRITES_MIN = 50L;
   private static final long MAX_QUEUED_WRITES_MAX = 1000000L;
   private static final long MAX_QUEUED_WRITES_DEFAULT = 800L;
   private static final long MAX_ADDED_CHUNKS_MIN = 1000L;
   private static final long MAX_ADDED_CHUNKS_MAX = 5000000L;
   private static final long MAX_ADDED_CHUNKS_DEFAULT = 0L;
   private static final long MAX_HEAP_PERCENT_MIN = 50L;
   private static final long MAX_HEAP_PERCENT_MAX = 99L;
   private static final long MAX_HEAP_PERCENT_DEFAULT = 85L;
   private static final int AUTO_PAUSE_GRACE_MIN = 10;
   private static final int AUTO_PAUSE_GRACE_MAX = 3600;
   private static final int AUTO_PAUSE_GRACE_DEFAULT = 120;
   private static final long TICK_BUDGET_MIN = 5L;
   private static final long TICK_BUDGET_MAX = 500L;
   private static final long TICK_BUDGET_DEFAULT = 25L;
   private static final long PLAYER_RESERVE_MIN = 0L;
   private static final long PLAYER_RESERVE_MAX = 200L;
   private static final long PLAYER_RESERVE_DEFAULT = 20L;
   private static final long CEILING_MIN = 60L;
   private static final long CEILING_MAX = 2000L;
   private static final long CEILING_DEFAULT = 150L;
   private static final long MAX_LOD_QUEUE_MIN = 16L;
   private static final long MAX_LOD_QUEUE_MAX = 100000L;
   private static final long MAX_LOD_QUEUE_DEFAULT = 512L;
   private static final long LOD_DRAIN_TO_DEFAULT = 0L;
   private static final long DISPATCH_MAX_CONCURRENT_MIN = 1L;
   private static final long DISPATCH_MAX_CONCURRENT_MAX = 4096L;
   private static final long DISPATCH_FLOOR = 8L;
   private static final long DISPATCH_KNEE = 200L;
   private static final long WORLD_ENTER_RADIUS_DEFAULT = 4096L;
   private static final long WORLD_ENTER_RADIUS_MIN = 128L;
   private static final long WORLD_ENTER_RADIUS_MAX = 32768L;
   private static final long LOD_INDEX_BUDGET_MB_NONE = 0L;
   private static final long LOD_INDEX_BUDGET_MB_MAX = 1048576L;
   private static final long SETTLE_DELAY_DEFAULT = 40L;
   private static final long SETTLE_DELAY_MAX = 600L;
   private static final int SETTLE_RADIUS_DEFAULT = 7;
   private static final int SETTLE_RADIUS_MAX = 16;
   private static final long SETTLE_MAX_HELD_MIN = 16L;
   private static final long SETTLE_MAX_HELD_MAX = 1000000L;
   private static final long SETTLE_MAX_HELD_DEFAULT = 256L;
   private static final int BACKCHANNEL_PORT_DERIVE = 0;
   private static final int BACKCHANNEL_PORT_MIN = 1024;
   private static final int BACKCHANNEL_PORT_MAX = 65535;
   private final Path savePath;
   private GsonConfig.ConfigModel configModel = new GsonConfig.ConfigModel();

   private static long dispatchMaxConcurrentDefault() {
      Optional<Integer> override = Input.tryInteger(System.getProperty("chunksmith.maxWorkingCount"));
      return override.isPresent() ? override.get().longValue() : Math.max(8L, 200L);
   }

   public GsonConfig(Path savePath) {
      this.savePath = savePath;
      if (Files.exists(this.savePath)) {
         this.reload();
      } else {
         this.saveConfig();
      }

      Translator.setLanguage(this.getLanguage());
   }

   @Override
   public Path getDirectory() {
      return this.savePath.getParent();
   }

   @Override
   public int getVersion() {
      return Optional.ofNullable(this.configModel.version).orElse(0);
   }

   @Override
   public String getLanguage() {
      return Optional.ofNullable(this.configModel.language).map(Input::checkLanguage).orElse("en");
   }

   @Override
   public boolean getContinueOnRestart() {
      return Optional.ofNullable(this.configModel.continueOnRestart).orElse(false);
   }

   @Override
   public boolean isForceLoadExistingChunks() {
      return Optional.ofNullable(this.configModel.forceLoadExistingChunks).orElse(false);
   }

   @Override
   public boolean isSilent() {
      return Optional.ofNullable(this.configModel.silent).orElse(false);
   }

   @Override
   public void setSilent(boolean silent) {
      this.configModel.silent = silent;
      this.saveConfig();
   }

   @Override
   public int getUpdateInterval() {
      return Optional.ofNullable(this.configModel.updateInterval).orElse(1);
   }

   @Override
   public void setUpdateInterval(int updateInterval) {
      this.configModel.updateInterval = Math.max(0, updateInterval);
      this.saveConfig();
   }

   @Override
   public boolean isIoThrottleEnabled() {
      return Optional.ofNullable(this.configModel.ioThrottle).orElse(true);
   }

   @Override
   public double getThrottleTargetMspt() {
      double raw = Optional.ofNullable(this.configModel.throttleTargetMspt).orElse(150.0);
      double clamped = Math.max(54.0, Math.min(1000.0, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: throttleTargetMspt %.1f is out of range [%.1f, %.1f], using %.1f", raw, 54.0, 1000.0, clamped));
      }

      return clamped;
   }

   @Override
   public long getThrottleMaxChunkMillis() {
      long raw = Optional.ofNullable(this.configModel.throttleMaxChunkMillis).orElse(750L);
      long clamped = Math.max(100L, Math.min(60000L, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: throttleMaxChunkMillis %d is out of range [%d, %d], using %d", raw, 100L, 60000L, clamped));
      }

      return clamped;
   }

   @Override
   public long getThrottleMaxQueuedWrites() {
      long raw = Optional.ofNullable(this.configModel.throttleMaxQueuedWrites).orElse(800L);
      if (raw <= 0L) {
         return 0L;
      }

      long clamped = Math.max(50L, Math.min(1000000L, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: throttleMaxQueuedWrites %d is out of range [%d, %d], using %d", raw, 50L, 1000000L, clamped));
      }

      return clamped;
   }

   @Override
   public long getThrottleMaxAddedChunks() {
      long raw = Optional.ofNullable(this.configModel.throttleMaxAddedChunks).orElse(0L);
      if (raw <= 0L) {
         return 0L;
      }

      long clamped = Math.max(1000L, Math.min(5000000L, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: throttleMaxAddedChunks %d is out of range [%d, %d], using %d", raw, 1000L, 5000000L, clamped));
      }

      return clamped;
   }

   @Override
   public long getThrottleMaxHeapPercent() {
      long raw = Optional.ofNullable(this.configModel.throttleMaxHeapPercent).orElse(85L);
      if (raw <= 0L) {
         return 0L;
      }

      long clamped = Math.max(50L, Math.min(99L, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: throttleMaxHeapPercent %d is out of range [%d, %d], using %d", raw, 50L, 99L, clamped));
      }

      return clamped;
   }

   @Override
   public boolean isAutoPauseEnabled() {
      return Optional.ofNullable(this.configModel.autoPauseOnOverload).orElse(true);
   }

   @Override
   public int getAutoPauseGraceSeconds() {
      int raw = Optional.ofNullable(this.configModel.autoPauseGraceSeconds).orElse(120);
      int clamped = Math.max(10, Math.min(3600, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: autoPauseGraceSeconds %d is out of range [%d, %d], using %d", raw, 10, 3600, clamped));
      }

      return clamped;
   }

   @Override
   public long getThrottleTickBudgetMillis() {
      long raw = Optional.ofNullable(this.configModel.throttleTickBudgetMillis).orElse(25L);
      if (raw <= 0L) {
         return 0L;
      }

      long clamped = Math.max(5L, Math.min(500L, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: throttleTickBudgetMillis %d is out of range [%d, %d], using %d", raw, 5L, 500L, clamped));
      }

      return clamped;
   }

   @Override
   public long getThrottlePlayerReserveMillis() {
      long raw = Optional.ofNullable(this.configModel.throttlePlayerReserveMillis).orElse(20L);
      long clamped = Math.max(0L, Math.min(200L, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: throttlePlayerReserveMillis %d is out of range [%d, %d], using %d", raw, 0L, 200L, clamped));
      }

      return clamped;
   }

   @Override
   public long getThrottleCeilingMillis() {
      long raw = Optional.ofNullable(this.configModel.throttleCeilingMillis).orElse(150L);
      if (raw <= 0L) {
         return 0L;
      }

      long clamped = Math.max(60L, Math.min(2000L, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: throttleCeilingMillis %d is out of range [%d, %d], using %d", raw, 60L, 2000L, clamped));
      }

      return clamped;
   }

   @Override
   public LodMode getLodMode() {
      String raw = this.configModel.lodEnabled;
      LodMode mode = LodMode.parse(raw);
      if (mode == null) {
         LOGGER.warn("Chunksmith: lodEnabled '" + raw + "' is not one of auto/true/false, using auto");
         return LodMode.AUTO;
      } else {
         return mode;
      }
   }

   @Override
   public long getThrottleMaxLodQueue() {
      long raw = Optional.ofNullable(this.configModel.throttleMaxLodQueue).orElse(512L);
      if (raw <= 0L) {
         return 0L;
      }

      long clamped = Math.max(16L, Math.min(100000L, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: throttleMaxLodQueue %d is out of range [%d, %d], using %d", raw, 16L, 100000L, clamped));
      }

      return clamped;
   }

   @Override
   public long getThrottleLodDrainTo() {
      long raw = Optional.ofNullable(this.configModel.throttleLodDrainTo).orElse(0L);
      if (raw <= 0L) {
         return 0L;
      } else {
         long ceiling = this.getThrottleMaxLodQueue();
         if (ceiling > 0L && raw >= ceiling) {
            long clamped = Math.max(1L, ceiling / 2L);
            LOGGER.warn(String.format("Chunksmith: throttleLodDrainTo %d is not below throttleMaxLodQueue %d, using %d", raw, ceiling, clamped));
            return clamped;
         } else {
            return raw;
         }
      }
   }

   @Override
   public long getDispatchMaxConcurrent() {
      long raw = Optional.ofNullable(this.configModel.dispatchMaxConcurrent).orElseGet(GsonConfig::dispatchMaxConcurrentDefault);
      long clamped = Math.max(1L, Math.min(4096L, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: dispatchMaxConcurrent %d is out of range [%d, %d], using %d", raw, 1L, 4096L, clamped));
      }

      return clamped;
   }

   @Override
   public boolean isPregenSettleEnabled() {
      return Optional.ofNullable(this.configModel.pregenSettle).orElse(true);
   }

   @Override
   public long getPregenSettleDelayTicks() {
      long raw = Optional.ofNullable(this.configModel.pregenSettleDelayTicks).orElse(40L);
      long clamped = Math.max(0L, Math.min(600L, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: pregenSettleDelayTicks %d is out of range [0, %d], using %d", raw, 600L, clamped));
      }

      return clamped;
   }

   @Override
   public long getPregenSettleMaxHeld() {
      long raw = Optional.ofNullable(this.configModel.pregenSettleMaxHeld).orElse(256L);
      if (raw <= 0L) {
         return 0L;
      }

      long clamped = Math.max(16L, Math.min(1000000L, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: pregenSettleMaxHeld %d is out of range [%d, %d], using %d", raw, 16L, 1000000L, clamped));
      }

      return clamped;
   }

   @Override
   public int getPregenSettleRadius() {
      int raw = Optional.ofNullable(this.configModel.pregenSettleRadius).orElse(7);
      int clamped = Math.max(1, Math.min(16, raw));
      if (raw != clamped) {
         LOGGER.warn(String.format("Chunksmith: pregenSettleRadius %d is out of range [1, %d], using %d", raw, 16, clamped));
      }

      return clamped;
   }

   @Override
   public void setPregenSettleEnabled(boolean enabled) {
      this.configModel.pregenSettle = enabled;
      this.saveConfig();
   }

   @Override
   public void setPregenSettleDelayTicks(long ticks) {
      this.configModel.pregenSettleDelayTicks = Math.max(0L, Math.min(600L, ticks));
      this.saveConfig();
   }

   @Override
   public void setPregenSettleRadius(int radius) {
      this.configModel.pregenSettleRadius = Math.max(1, Math.min(16, radius));
      this.saveConfig();
   }

   @Override
   public boolean isLodDhOverrideEnabled() {
      return Optional.ofNullable(this.configModel.lodDhOverride).orElse(false);
   }

   @Override
   public boolean isLodFromExploredChunksEnabled() {
      return Optional.ofNullable(this.configModel.lodFromExploredChunks).orElse(true);
   }

   @Override
   public int getLodBackchannelPort() {
      int raw = Optional.ofNullable(this.configModel.lodBackchannelPort).orElse(0);
      if (raw == 0) {
         return 0;
      }

      if (raw >= 1024 && raw <= 65535) {
         return raw;
      }

      LOGGER.warn("Chunksmith: lodBackchannelPort " + raw + " is outside 1024-65535; deriving the port from the game port instead");
      return 0;
   }

   @Override
   public long getLodIndexBudgetMb() {
      long raw = Optional.ofNullable(this.configModel.lodIndexBudgetMb).orElse(2048L);
      if (raw <= 0L) {
         return 0L;
      } else if (raw > 1048576L) {
         LOGGER.warn("Chunksmith: lodIndexBudgetMb " + raw + " is above 1048576 MB; serving without a budget instead");
         return 0L;
      } else {
         return raw;
      }
   }

   @Override
   public boolean isWorldEnterPregenEnabled() {
      return Optional.ofNullable(this.configModel.worldEnterPregen).orElse(true);
   }

   @Override
   public boolean isWorldEnterPregenSupported() {
      return true;
   }

   @Override
   public long getWorldEnterPregenRadius() {
      long raw = Optional.ofNullable(this.configModel.worldEnterPregenRadius).orElse(4096L);
      if (raw >= 128L && raw <= 32768L) {
         return raw;
      }

      LOGGER.warn("Chunksmith: worldEnterPregenRadius " + raw + " is outside 128-32768; using 4096 instead");
      return 4096L;
   }

   @Override
   public long getWorldEnterLodHorizon() {
      long raw = Optional.ofNullable(this.configModel.worldEnterLodHorizon).orElse(0L);
      return Math.max(0L, Math.min(32768L, raw));
   }

   @Override
   public String getWorldEnterPregenCenter() {
      String raw = Optional.ofNullable(this.configModel.worldEnterPregenCenter).orElse("auto");
      WorldEnterCenter parsed = WorldEnterCenter.parse(raw);
      if (parsed == null) {
         LOGGER.warn("Chunksmith: worldEnterPregenCenter \"" + raw + "\" is not 'auto', 'origin', 'spawn' or 'x,z'; using auto instead");
         return "auto";
      } else {
         return parsed.spec();
      }
   }

   @Override
   public String getLodBackchannelBindAddress() {
      return Input.checkHost(this.configModel.lodBackchannelBindAddress);
   }

   @Override
   public String getLodBackchannelHost() {
      return Input.checkAdvertisedHost(this.configModel.lodBackchannelHost);
   }

   @Override
   public void setLanguage(String language) {
      this.configModel.language = Input.checkLanguage(language);
      this.saveConfig();
      Translator.setLanguage(this.getLanguage());
   }

   @Override
   public void setContinueOnRestart(boolean continueOnRestart) {
      this.configModel.continueOnRestart = continueOnRestart;
      this.saveConfig();
   }

   @Override
   public void setForceLoadExistingChunks(boolean forceLoadExistingChunks) {
      this.configModel.forceLoadExistingChunks = forceLoadExistingChunks;
      this.saveConfig();
   }

   @Override
   public void setIoThrottleEnabled(boolean enabled) {
      this.configModel.ioThrottle = enabled;
      this.saveConfig();
   }

   @Override
   public void setThrottleTargetMspt(double mspt) {
      this.configModel.throttleTargetMspt = Math.max(54.0, Math.min(1000.0, mspt));
      this.saveConfig();
   }

   @Override
   public void setThrottleMaxChunkMillis(long millis) {
      this.configModel.throttleMaxChunkMillis = Math.max(100L, Math.min(60000L, millis));
      this.saveConfig();
   }

   @Override
   public void setThrottleMaxQueuedWrites(long writes) {
      this.configModel.throttleMaxQueuedWrites = writes <= 0L ? 0L : Math.max(50L, Math.min(1000000L, writes));
      this.saveConfig();
   }

   @Override
   public void setThrottleMaxAddedChunks(long chunks) {
      this.configModel.throttleMaxAddedChunks = chunks <= 0L ? 0L : Math.max(1000L, Math.min(5000000L, chunks));
      this.saveConfig();
   }

   @Override
   public void setThrottleMaxHeapPercent(long percent) {
      this.configModel.throttleMaxHeapPercent = percent <= 0L ? 0L : Math.max(50L, Math.min(99L, percent));
      this.saveConfig();
   }

   @Override
   public void setThrottleTickBudgetMillis(long millis) {
      this.configModel.throttleTickBudgetMillis = millis <= 0L ? 0L : Math.max(5L, Math.min(500L, millis));
      this.saveConfig();
   }

   @Override
   public void setThrottlePlayerReserveMillis(long millis) {
      this.configModel.throttlePlayerReserveMillis = Math.max(0L, Math.min(200L, millis));
      this.saveConfig();
   }

   @Override
   public void setThrottleCeilingMillis(long millis) {
      this.configModel.throttleCeilingMillis = millis <= 0L ? 0L : Math.max(60L, Math.min(2000L, millis));
      this.saveConfig();
   }

   @Override
   public void setAutoPauseEnabled(boolean enabled) {
      this.configModel.autoPauseOnOverload = enabled;
      this.saveConfig();
   }

   @Override
   public void setAutoPauseGraceSeconds(int seconds) {
      this.configModel.autoPauseGraceSeconds = Math.max(10, Math.min(3600, seconds));
      this.saveConfig();
   }

   @Override
   public void setPregenSettleMaxHeld(long maxHeld) {
      this.configModel.pregenSettleMaxHeld = maxHeld <= 0L ? 0L : Math.max(16L, Math.min(1000000L, maxHeld));
      this.saveConfig();
   }

   @Override
   public void setThrottleMaxLodQueue(long items) {
      this.configModel.throttleMaxLodQueue = items <= 0L ? 0L : Math.max(16L, Math.min(100000L, items));
      this.saveConfig();
   }

   @Override
   public void setThrottleLodDrainTo(long items) {
      this.configModel.throttleLodDrainTo = Math.max(0L, items);
      this.saveConfig();
   }

   @Override
   public void setDispatchMaxConcurrent(long chunks) {
      this.configModel.dispatchMaxConcurrent = Math.max(1L, Math.min(4096L, chunks));
      this.saveConfig();
   }

   @Override
   public void setLodMode(LodMode mode) {
      this.configModel.lodEnabled = mode.name().toLowerCase(Locale.ROOT);
      this.saveConfig();
   }

   @Override
   public void setLodDhOverrideEnabled(boolean enabled) {
      this.configModel.lodDhOverride = enabled;
      this.saveConfig();
   }

   @Override
   public void setLodFromExploredChunksEnabled(boolean enabled) {
      this.configModel.lodFromExploredChunks = enabled;
      this.saveConfig();
   }

   @Override
   public void setLodBackchannelPort(int port) {
      this.configModel.lodBackchannelPort = port >= 1024 && port <= 65535 ? port : 0;
      this.saveConfig();
   }

   @Override
   public void setLodIndexBudgetMb(long megabytes) {
      this.configModel.lodIndexBudgetMb = megabytes > 0L && megabytes <= 1048576L ? megabytes : 0L;
      this.saveConfig();
   }

   @Override
   public void setWorldEnterPregenEnabled(boolean enabled) {
      this.configModel.worldEnterPregen = enabled;
      this.saveConfig();
   }

   @Override
   public LodMode getWorldEnterLodMode() {
      String raw = this.configModel.worldEnterLod;
      LodMode mode = LodMode.parse(raw);
      if (mode == null) {
         LOGGER.warn("Chunksmith: worldEnterLod '" + raw + "' is not one of auto/on/off, using auto");
         return LodMode.AUTO;
      } else {
         return mode;
      }
   }

   @Override
   public void setWorldEnterLodMode(LodMode mode) {
      this.configModel.worldEnterLod = mode.name().toLowerCase(Locale.ROOT);
      this.saveConfig();
   }

   @Override
   public void setWorldEnterLodHorizon(long blocks) {
      this.configModel.worldEnterLodHorizon = Math.max(0L, Math.min(32768L, blocks));
      this.saveConfig();
   }

   @Override
   public void setWorldEnterPregenRadius(long blocks) {
      this.configModel.worldEnterPregenRadius = Math.max(128L, Math.min(32768L, blocks));
      this.saveConfig();
   }

   @Override
   public void setWorldEnterPregenCenter(String spec) {
      WorldEnterCenter parsed = WorldEnterCenter.parse(spec);
      this.configModel.worldEnterPregenCenter = parsed == null ? "auto" : parsed.spec();
      this.saveConfig();
   }

   @Override
   public void setLodBackchannelBindAddress(String address) {
      this.configModel.lodBackchannelBindAddress = Input.checkHost(address);
      this.saveConfig();
   }

   @Override
   public void setLodBackchannelHost(String host) {
      this.configModel.lodBackchannelHost = Input.checkAdvertisedHost(host);
      this.saveConfig();
   }

   @Override
   public void reload() {
      try (Reader reader = Files.newBufferedReader(this.savePath)) {
         this.configModel = (GsonConfig.ConfigModel)GSON.fromJson(reader, GsonConfig.ConfigModel.class);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private void saveConfig() {
      try {
         Files.createDirectories(this.savePath.getParent());
      } catch (IOException e) {
         e.printStackTrace();
      }

      try (Writer writer = Files.newBufferedWriter(this.savePath)) {
         GSON.toJson(this.configModel, writer);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static class ConfigModel {
      private Integer version = 2;
      private String language = "en";
      private Boolean continueOnRestart = false;
      private Boolean forceLoadExistingChunks = false;
      private Boolean silent = false;
      private Integer updateInterval = 1;
      private Boolean ioThrottle = true;
      private Double throttleTargetMspt = 150.0;
      private Long throttleMaxChunkMillis = 750L;
      private Long throttleMaxQueuedWrites = 800L;
      private Long throttleMaxAddedChunks = 0L;
      private Long throttleMaxHeapPercent = 85L;
      private Long throttleTickBudgetMillis = 25L;
      private Long throttlePlayerReserveMillis = 20L;
      private Long throttleCeilingMillis = 150L;
      private Boolean autoPauseOnOverload = true;
      private Integer autoPauseGraceSeconds = 120;
      private String lodEnabled = "auto";
      private Long throttleMaxLodQueue = 512L;
      private Long throttleLodDrainTo = 0L;
      private Long dispatchMaxConcurrent = GsonConfig.dispatchMaxConcurrentDefault();
      private Boolean lodDhOverride = false;
      private Boolean lodFromExploredChunks = true;
      private Integer lodBackchannelPort = 0;
      private Long lodIndexBudgetMb = 2048L;
      private Boolean worldEnterPregen = true;
      private String worldEnterLod = "auto";
      private Long worldEnterPregenRadius = 4096L;
      private Long worldEnterLodHorizon = 0L;
      private String worldEnterPregenCenter = "auto";
      private String lodBackchannelBindAddress = "";
      private String lodBackchannelHost = "";
      private Boolean pregenSettle = true;
      private Long pregenSettleDelayTicks = 40L;
      private Integer pregenSettleRadius = 7;
      private Long pregenSettleMaxHeld = 256L;
      private Map<String, GsonConfig.TaskModel> tasks;

      public Integer getVersion() {
         return this.version;
      }

      public void setVersion(Integer version) {
         this.version = version;
      }

      public String getLanguage() {
         return this.language;
      }

      public void setLanguage(String language) {
         this.language = language;
      }

      public Boolean getContinueOnRestart() {
         return this.continueOnRestart;
      }

      public void setContinueOnRestart(Boolean continueOnRestart) {
         this.continueOnRestart = continueOnRestart;
      }

      public Boolean getForceLoadExistingChunks() {
         return this.forceLoadExistingChunks;
      }

      public void setForceLoadExistingChunks(Boolean forceLoadExistingChunks) {
         this.forceLoadExistingChunks = forceLoadExistingChunks;
      }

      public Map<String, GsonConfig.TaskModel> getTasks() {
         return this.tasks;
      }

      public void setTasks(Map<String, GsonConfig.TaskModel> tasks) {
         this.tasks = tasks;
      }

      public boolean isSilent() {
         return this.silent;
      }

      public void setSilent(boolean silent) {
         this.silent = silent;
      }

      public int getUpdateInterval() {
         return this.updateInterval;
      }

      public void setUpdateInterval(int updateInterval) {
         this.updateInterval = updateInterval;
      }

      public Boolean getIoThrottle() {
         return this.ioThrottle;
      }

      public void setIoThrottle(Boolean ioThrottle) {
         this.ioThrottle = ioThrottle;
      }

      public Double getThrottleTargetMspt() {
         return this.throttleTargetMspt;
      }

      public void setThrottleTargetMspt(Double throttleTargetMspt) {
         this.throttleTargetMspt = throttleTargetMspt;
      }

      public Long getThrottleMaxChunkMillis() {
         return this.throttleMaxChunkMillis;
      }

      public void setThrottleMaxChunkMillis(Long throttleMaxChunkMillis) {
         this.throttleMaxChunkMillis = throttleMaxChunkMillis;
      }

      public Long getThrottleMaxQueuedWrites() {
         return this.throttleMaxQueuedWrites;
      }

      public void setThrottleMaxQueuedWrites(Long throttleMaxQueuedWrites) {
         this.throttleMaxQueuedWrites = throttleMaxQueuedWrites;
      }

      public Long getThrottleMaxAddedChunks() {
         return this.throttleMaxAddedChunks;
      }

      public void setThrottleMaxAddedChunks(Long throttleMaxAddedChunks) {
         this.throttleMaxAddedChunks = throttleMaxAddedChunks;
      }

      public Long getThrottleCeilingMillis() {
         return this.throttleCeilingMillis;
      }

      public void setThrottleCeilingMillis(Long throttleCeilingMillis) {
         this.throttleCeilingMillis = throttleCeilingMillis;
      }

      public Long getThrottlePlayerReserveMillis() {
         return this.throttlePlayerReserveMillis;
      }

      public void setThrottlePlayerReserveMillis(Long throttlePlayerReserveMillis) {
         this.throttlePlayerReserveMillis = throttlePlayerReserveMillis;
      }

      public Long getThrottleTickBudgetMillis() {
         return this.throttleTickBudgetMillis;
      }

      public void setThrottleTickBudgetMillis(Long throttleTickBudgetMillis) {
         this.throttleTickBudgetMillis = throttleTickBudgetMillis;
      }

      public Long getThrottleMaxHeapPercent() {
         return this.throttleMaxHeapPercent;
      }

      public void setThrottleMaxHeapPercent(Long throttleMaxHeapPercent) {
         this.throttleMaxHeapPercent = throttleMaxHeapPercent;
      }

      public Boolean getAutoPauseOnOverload() {
         return this.autoPauseOnOverload;
      }

      public void setAutoPauseOnOverload(Boolean autoPauseOnOverload) {
         this.autoPauseOnOverload = autoPauseOnOverload;
      }

      public Integer getAutoPauseGraceSeconds() {
         return this.autoPauseGraceSeconds;
      }

      public void setAutoPauseGraceSeconds(Integer autoPauseGraceSeconds) {
         this.autoPauseGraceSeconds = autoPauseGraceSeconds;
      }

      public Long getPregenSettleMaxHeld() {
         return this.pregenSettleMaxHeld;
      }

      public void setPregenSettleMaxHeld(Long pregenSettleMaxHeld) {
         this.pregenSettleMaxHeld = pregenSettleMaxHeld;
      }
   }

   private static class TaskModel {
      private Boolean cancelled;
      private Double radius;
      private Double radiusZ;
      private Double centerX;
      private Double centerZ;
      private String iterator;
      private String shape;
      private Long count;
      private Long time;

      public Boolean getCancelled() {
         return this.cancelled;
      }

      public void setCancelled(Boolean cancelled) {
         this.cancelled = cancelled;
      }

      public Double getRadius() {
         return this.radius;
      }

      public void setRadius(Double radius) {
         this.radius = radius;
      }

      public Double getRadiusZ() {
         return this.radiusZ;
      }

      public void setRadiusZ(Double radiusZ) {
         this.radiusZ = radiusZ;
      }

      public Double getCenterX() {
         return this.centerX;
      }

      public void setCenterX(Double centerX) {
         this.centerX = centerX;
      }

      public Double getCenterZ() {
         return this.centerZ;
      }

      public void setCenterZ(Double centerZ) {
         this.centerZ = centerZ;
      }

      public String getIterator() {
         return this.iterator;
      }

      public void setIterator(String iterator) {
         this.iterator = iterator;
      }

      public String getShape() {
         return this.shape;
      }

      public void setShape(String shape) {
         this.shape = shape;
      }

      public Long getCount() {
         return this.count;
      }

      public void setCount(Long count) {
         this.count = count;
      }

      public Long getTime() {
         return this.time;
      }

      public void setTime(Long time) {
         this.time = time;
      }
   }
}
