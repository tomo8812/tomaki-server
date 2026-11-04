package com.kishku7.chunksmith.lod.client;

import com.kishku7.chunksmith.util.Input;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class CsLodClientSettings {
   private static final List<CsLodClientSettings.Setting> ALL = List.of(
      new CsLodClientSettings.Setting(
         "sync-interval-seconds", CsLodClientSettings.Kind.INTEGER, () -> Integer.toString(CsLodClientConfig.syncIntervalSeconds()), raw -> {
            Optional<Long> value = Input.tryLong(raw);
            if (!value.isEmpty() && value.get() <= 2147483647L && value.get() >= -2147483648L) {
               CsLodClientConfig.setSyncSeconds(value.get().intValue());
               return true;
            } else {
               return false;
            }
         }, "how often to ask the server whether its LOD store changed, in seconds (minimum 30)"
      ),
      new CsLodClientSettings.Setting(
         "reinject-on-join", CsLodClientSettings.Kind.BOOLEAN, () -> Boolean.toString(CsLodClientConfig.reinjectOnJoin()), raw -> {
            Optional<Boolean> value = Input.tryBoolean(raw);
            value.ifPresent(CsLodClientConfig::setReinjectOnJoin);
            return value.isPresent();
         }, "send every LOD region to your renderer again on the next join, then set it back"
      ),
      new CsLodClientSettings.Setting("max-disk-mb", CsLodClientSettings.Kind.INTEGER, () -> Long.toString(CsLodClientConfig.maxDiskMb()), raw -> {
         Optional<Long> value = Input.tryLong(raw);
         if (value.isEmpty()) {
            return false;
         }

         CsLodClientConfig.setMaxDiskMb(value.get());
         return true;
      }, "ceiling in MEGABYTES on stored LOD per dimension per server; 0 means no ceiling (the default, and how every version before 4.0.0 behaved)")
   );

   private CsLodClientSettings() {
   }

   public static List<CsLodClientSettings.Setting> all() {
      return ALL;
   }

   public static Optional<CsLodClientSettings.Setting> find(String name) {
      for (CsLodClientSettings.Setting setting : ALL) {
         if (setting.name().equalsIgnoreCase(name)) {
            return Optional.of(setting);
         }
      }

      return Optional.empty();
   }

   public static List<String> names() {
      return ALL.stream().map(CsLodClientSettings.Setting::name).toList();
   }

   public enum Kind {
      BOOLEAN(List.of("true", "false")),
      INTEGER(List.of());

      private final List<String> completions;

      Kind(List<String> completions) {
         this.completions = completions;
      }

      public List<String> completions() {
         return this.completions;
      }

      // $VF: synthetic method
      private static CsLodClientSettings.Kind[] $values() {
         return new CsLodClientSettings.Kind[]{BOOLEAN, INTEGER};
      }
   }

   public static final class Setting {
      private final String name;
      private final CsLodClientSettings.Kind kind;
      private final Supplier<String> reader;
      private final CsLodClientSettings.Setting.Writer writer;
      private final String help;

      Setting(String name, CsLodClientSettings.Kind kind, Supplier<String> reader, CsLodClientSettings.Setting.Writer writer, String help) {
         this.name = name;
         this.kind = kind;
         this.reader = reader;
         this.writer = writer;
         this.help = help;
      }

      public String name() {
         return this.name;
      }

      public CsLodClientSettings.Kind kind() {
         return this.kind;
      }

      public String help() {
         return this.help;
      }

      public String read() {
         return this.reader.get();
      }

      public boolean write(String raw) {
         return this.writer.write(raw);
      }

      @FunctionalInterface
      public interface Writer {
         boolean write(String var1);
      }
   }
}
