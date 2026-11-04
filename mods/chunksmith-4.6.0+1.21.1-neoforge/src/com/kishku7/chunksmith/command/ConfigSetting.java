package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.platform.Config;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ConfigSetting {
   private final String name;
   private final ConfigSetting.Kind kind;
   private final Function<Config, String> reader;
   private final ConfigSetting.Writer writer;
   private final Predicate<Config> supported;
   private final ConfigSetting.Explainer explainer;

   ConfigSetting(String name, ConfigSetting.Kind kind, Function<Config, String> reader, ConfigSetting.Writer writer, Predicate<Config> supported) {
      this(name, kind, reader, writer, supported, null);
   }

   ConfigSetting(
      String name,
      ConfigSetting.Kind kind,
      Function<Config, String> reader,
      ConfigSetting.Writer writer,
      Predicate<Config> supported,
      ConfigSetting.Explainer explainer
   ) {
      this.name = name;
      this.kind = kind;
      this.reader = reader;
      this.writer = writer;
      this.supported = supported;
      this.explainer = explainer;
   }

   public String explainRefusal(Config config, String raw) {
      return this.explainer == null ? null : this.explainer.explain(config, raw);
   }

   public String name() {
      return this.name;
   }

   public ConfigSetting.Kind kind() {
      return this.kind;
   }

   public String read(Config config) {
      return this.reader.apply(config);
   }

   public boolean write(Config config, String raw) {
      return this.writer.write(config, raw);
   }

   public boolean isSupported(Config config) {
      return this.supported.test(config);
   }

   @FunctionalInterface
   public interface Explainer {
      String explain(Config var1, String var2);
   }

   public enum Kind {
      BOOLEAN(List.of("true", "false")),
      TRISTATE(List.of("auto", "true", "false")),
      INTEGER(List.of()),
      DECIMAL(List.of()),
      TEXT(List.of());

      private final List<String> completions;

      Kind(List<String> completions) {
         this.completions = completions;
      }

      public List<String> completions() {
         return this.completions;
      }

      // $VF: synthetic method
      private static ConfigSetting.Kind[] $values() {
         return new ConfigSetting.Kind[]{BOOLEAN, TRISTATE, INTEGER, DECIMAL, TEXT};
      }
   }

   @FunctionalInterface
   public interface Writer {
      boolean write(Config var1, String var2);
   }
}
