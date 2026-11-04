package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.lod.net.CsLodControl;
import com.kishku7.chunksmith.platform.Config;
import com.kishku7.chunksmith.platform.LodMode;
import com.kishku7.chunksmith.util.Input;
import com.kishku7.chunksmith.worldenter.WorldEnterCenter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

public final class ConfigSettings {
   private static final List<ConfigSetting> ALL = List.of(
      of("language", ConfigSetting.Kind.TEXT, Config::getLanguage, (config, raw) -> {
         if (!Input.checkLanguage(raw).equalsIgnoreCase(raw)) {
            return false;
         }

         config.setLanguage(raw);
         return true;
      }),
      bool("continueOnRestart", Config::getContinueOnRestart, Config::setContinueOnRestart),
      bool("forceLoadExistingChunks", Config::isForceLoadExistingChunks, Config::setForceLoadExistingChunks),
      bool("silent", Config::isSilent, Config::setSilent),
      integer("updateInterval", config -> config.getUpdateInterval(), (config, value) -> config.setUpdateInterval((int)value)),
      bool("ioThrottle", Config::isIoThrottleEnabled, Config::setIoThrottleEnabled),
      of("throttleTargetMspt", ConfigSetting.Kind.DECIMAL, config -> String.valueOf(config.getThrottleTargetMspt()), (config, raw) -> {
         Optional<Double> value = Input.tryDouble(raw);
         value.ifPresent(config::setThrottleTargetMspt);
         return value.isPresent();
      }),
      integer("throttleMaxChunkMillis", Config::getThrottleMaxChunkMillis, Config::setThrottleMaxChunkMillis),
      integer("throttleMaxQueuedWrites", Config::getThrottleMaxQueuedWrites, Config::setThrottleMaxQueuedWrites),
      integer("throttleMaxLodQueue", Config::getThrottleMaxLodQueue, Config::setThrottleMaxLodQueue),
      integer("throttleLodDrainTo", Config::getThrottleLodDrainTo, Config::setThrottleLodDrainTo),
      integer("dispatchMaxConcurrent", Config::getDispatchMaxConcurrent, Config::setDispatchMaxConcurrent),
      integer("throttleMaxAddedChunks", Config::getThrottleMaxAddedChunks, Config::setThrottleMaxAddedChunks),
      integer("throttleMaxHeapPercent", Config::getThrottleMaxHeapPercent, Config::setThrottleMaxHeapPercent),
      integer("throttleTickBudgetMillis", Config::getThrottleTickBudgetMillis, Config::setThrottleTickBudgetMillis),
      integer("throttlePlayerReserveMillis", Config::getThrottlePlayerReserveMillis, Config::setThrottlePlayerReserveMillis),
      integer("throttleCeilingMillis", Config::getThrottleCeilingMillis, Config::setThrottleCeilingMillis),
      bool("autoPauseOnOverload", Config::isAutoPauseEnabled, Config::setAutoPauseEnabled),
      integer("autoPauseGraceSeconds", config -> config.getAutoPauseGraceSeconds(), (config, value) -> config.setAutoPauseGraceSeconds((int)value)),
      of("lodEnabled", ConfigSetting.Kind.TRISTATE, config -> config.getLodMode().name().toLowerCase(Locale.ROOT), (config, raw) -> {
         LodMode mode = LodMode.parse(raw);
         if (mode == null) {
            return false;
         }

         config.setLodMode(mode);
         return true;
      }),
      bool("lodDhOverride", Config::isLodDhOverrideEnabled, Config::setLodDhOverrideEnabled),
      bool("lodFromExploredChunks", Config::isLodFromExploredChunksEnabled, Config::setLodFromExploredChunksEnabled),
      port("lodBackchannelPort", ConfigSetting.Kind.INTEGER, config -> String.valueOf(config.getLodBackchannelPort()), (config, raw) -> {
         Optional<Long> value = Input.tryLong(raw);
         if (value.isEmpty()) {
            return false;
         }

         long asked = value.get();
         if (asked >= -2147483648L && asked <= 2147483647L) {
            OptionalInt game = CsLodControl.gamePort();
            if (asked != 0L && game.isPresent() && asked == game.getAsInt()) {
               return false;
            }

            config.setLodBackchannelPort((int)asked);
            CsLodControl.apply();
            return true;
         } else {
            return false;
         }
      }),
      integer("lodIndexBudgetMb", Config::getLodIndexBudgetMb, Config::setLodIndexBudgetMb),
      worldEnter(bool("worldEnterPregen", Config::isWorldEnterPregenEnabled, Config::setWorldEnterPregenEnabled)),
      worldEnter(of("worldEnterLod", ConfigSetting.Kind.TRISTATE, config -> config.getWorldEnterLodMode().name().toLowerCase(Locale.ROOT), (config, raw) -> {
         LodMode mode = LodMode.parse(raw);
         if (mode == null) {
            return false;
         }

         config.setWorldEnterLodMode(mode);
         return true;
      })),
      worldEnter(integer("worldEnterPregenRadius", Config::getWorldEnterPregenRadius, Config::setWorldEnterPregenRadius)),
      worldEnter(integer("worldEnterLodHorizon", Config::getWorldEnterLodHorizon, Config::setWorldEnterLodHorizon)),
      worldEnter(
         text(
            "worldEnterPregenCenter",
            Config::getWorldEnterPregenCenter,
            Config::setWorldEnterPregenCenter,
            raw -> WorldEnterCenter.parse(raw) != null,
            "expected 'auto', 'origin', 'spawn', or coordinates in quotes like \"512,-64\""
         )
      ),
      host("lodBackchannelBindAddress", Config::getLodBackchannelBindAddress, Config::setLodBackchannelBindAddress, Input::checkHost),
      host("lodBackchannelHost", Config::getLodBackchannelHost, Config::setLodBackchannelHost, Input::checkAdvertisedHost),
      settle(bool("pregenSettle", Config::isPregenSettleEnabled, Config::setPregenSettleEnabled)),
      settle(integer("pregenSettleDelayTicks", Config::getPregenSettleDelayTicks, Config::setPregenSettleDelayTicks)),
      settle(integer("pregenSettleRadius", config -> config.getPregenSettleRadius(), (config, value) -> config.setPregenSettleRadius((int)value))),
      settle(integer("pregenSettleMaxHeld", Config::getPregenSettleMaxHeld, Config::setPregenSettleMaxHeld))
   );
   private static final String CLEAR = "none";

   private ConfigSettings() {
   }

   public static List<ConfigSetting> all() {
      return ALL;
   }

   public static Optional<ConfigSetting> find(String name) {
      for (ConfigSetting setting : ALL) {
         if (setting.name().equalsIgnoreCase(name)) {
            return Optional.of(setting);
         }
      }

      return Optional.empty();
   }

   public static List<String> names() {
      return ALL.stream().map(ConfigSetting::name).toList();
   }

   private static ConfigSetting port(String name, ConfigSetting.Kind kind, Function<Config, String> reader, ConfigSetting.Writer writer) {
      return new ConfigSetting(
         name,
         kind,
         reader,
         writer,
         config -> true,
         (config, raw) -> {
            Optional<Long> asked = Input.tryLong(raw);
            if (asked.isEmpty()) {
               return null;
            }

            long value = asked.get();
            OptionalInt game = CsLodControl.gamePort();
            return value != 0L && game.isPresent() && value == game.getAsInt()
               ? "that is the port the game itself is listening on. Pick another port, or use 0 to derive it (" + (game.getAsInt() + 1) + ")."
               : null;
         }
      );
   }

   private static ConfigSetting host(String name, Function<Config, String> getter, ConfigSettings.TextSetter setter, Function<String, String> validator) {
      return new ConfigSetting(name, ConfigSetting.Kind.TEXT, config -> {
         String value = getter.apply(config);
         return value != null && !value.isEmpty() ? value : "none";
      }, (config, raw) -> {
         String asked = raw == null ? "" : raw.trim();
         if (asked.isEmpty() || "none".equalsIgnoreCase(asked)) {
            setter.set(config, "");
            CsLodControl.apply();
            return true;
         }

         if (validator.apply(asked).isEmpty()) {
            return false;
         }

         setter.set(config, asked);
         CsLodControl.apply();
         return true;
      }, config -> true);
   }

   private static ConfigSetting text(
      String name, Function<Config, String> getter, ConfigSettings.TextSetter setter, Function<String, Boolean> valid, String hint
   ) {
      return new ConfigSetting(name, ConfigSetting.Kind.TEXT, getter, (config, raw) -> {
         String asked = raw == null ? "" : raw.trim();
         if (!asked.isEmpty() && valid.apply(asked)) {
            setter.set(config, asked);
            return true;
         } else {
            return false;
         }
      }, config -> true, (config, raw) -> hint);
   }

   private static ConfigSetting of(String name, ConfigSetting.Kind kind, Function<Config, String> reader, ConfigSetting.Writer writer) {
      return new ConfigSetting(name, kind, reader, writer, config -> true);
   }

   private static ConfigSetting bool(String name, ConfigSettings.BoolGetter getter, ConfigSettings.BoolSetter setter) {
      return new ConfigSetting(name, ConfigSetting.Kind.BOOLEAN, config -> String.valueOf(getter.get(config)), (config, raw) -> {
         Optional<Boolean> value = Input.tryBoolean(raw);
         value.ifPresent(v -> setter.set(config, v));
         return value.isPresent();
      }, config -> true);
   }

   private static ConfigSetting integer(String name, ConfigSettings.LongGetter getter, ConfigSettings.LongSetter setter) {
      return new ConfigSetting(name, ConfigSetting.Kind.INTEGER, config -> String.valueOf(getter.get(config)), (config, raw) -> {
         Optional<Long> value = Input.tryLong(raw);
         value.ifPresent(v -> setter.set(config, v));
         return value.isPresent();
      }, config -> true);
   }

   private static ConfigSetting worldEnter(ConfigSetting base) {
      return new ConfigSetting(base.name(), base.kind(), base::read, base::write, Config::isWorldEnterPregenSupported);
   }

   private static ConfigSetting settle(ConfigSetting base) {
      return new ConfigSetting(base.name(), base.kind(), base::read, base::write, Config::isPregenSettleSupported);
   }

   private interface BoolGetter {
      boolean get(Config var1);
   }

   private interface BoolSetter {
      void set(Config var1, boolean var2);
   }

   private interface LongGetter {
      long get(Config var1);
   }

   private interface LongSetter {
      void set(Config var1, long var2);
   }

   private interface TextSetter {
      void set(Config var1, String var2);
   }
}
