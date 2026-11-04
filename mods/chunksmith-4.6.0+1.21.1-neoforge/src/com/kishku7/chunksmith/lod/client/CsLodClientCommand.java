package com.kishku7.chunksmith.lod.client;

import com.kishku7.chunksmith.lod.client.net.CsLodClientNet;
import com.kishku7.chunksmith.lod.client.render.LodInjector;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.List;
import java.util.Optional;

public final class CsLodClientCommand {
   private CsLodClientCommand() {
   }

   public static <S> LiteralArgumentBuilder<S> build(String name, CsLodClientCommand.Reply<S> reply) {
      LiteralArgumentBuilder<S> root = LiteralArgumentBuilder.literal(name);
      root.then(LiteralArgumentBuilder.literal("status").executes(context -> {
         S source = (S)context.getSource();

         for (String line : CsLodClientNet.status()) {
            reply.line(source, "[chunksmith] " + line);
         }

         return 1;
      }));
      root.then(LiteralArgumentBuilder.literal("reset").executes(context -> {
         S source = (S)context.getSource();
         long removed = CsLodClientNet.resetAll();
         if (removed < 0L) {
            reply.line(source, "[chunksmith] reset: the store could not be read; nothing was removed");
            return 0;
         } else {
            reply.line(source, "[chunksmith] reset: removed " + removed + " file(s); re-syncing from the server");
            reply.line(source, "[chunksmith] note: your renderer's own database was not touched. If the");
            reply.line(source, "[chunksmith]       server's world was regenerated, clear Distant Horizons");
            reply.line(source, "[chunksmith]       or Voxy separately.");
            return 1;
         }
      }));
      root.then(settings(reply));
      root.then(inject(reply, "inject_voxy", "Voxy"));
      root.then(inject(reply, "inject_dh", "Distant Horizons"));
      return root;
   }

   private static <S> LiteralArgumentBuilder<S> settings(CsLodClientCommand.Reply<S> reply) {
      LiteralArgumentBuilder<S> set = LiteralArgumentBuilder.literal("set");
      set.executes(context -> {
         S source = (S)context.getSource();
         reply.line(source, "[chunksmith] LOD client settings (config/chunksmith-lod.properties):");

         for (CsLodClientSettings.Setting setting : CsLodClientSettings.all()) {
            reply.line(source, "[chunksmith]   " + setting.name() + " = " + setting.read() + "  -- " + setting.help());
         }

         return 1;
      });
      RequiredArgumentBuilder<S, String> named = RequiredArgumentBuilder.argument("name", StringArgumentType.word());
      named.suggests((context, builder) -> {
         for (String option : CsLodClientSettings.names()) {
            builder.suggest(option);
         }

         return builder.buildFuture();
      });
      named.executes(context -> show(context.getSource(), reply, StringArgumentType.getString(context, "name")));
      RequiredArgumentBuilder<S, String> valued = RequiredArgumentBuilder.argument("value", StringArgumentType.word());
      valued.suggests((context, builder) -> {
         Optional<CsLodClientSettings.Setting> found = CsLodClientSettings.find(StringArgumentType.getString(context, "name"));
         if (found.isPresent()) {
            for (String option : found.get().kind().completions()) {
               builder.suggest(option);
            }
         }

         return builder.buildFuture();
      });
      valued.executes(
         context -> write(context.getSource(), reply, StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "value"))
      );
      named.then(valued);
      set.then(named);
      return set;
   }

   private static <S> int show(S source, CsLodClientCommand.Reply<S> reply, String name) {
      Optional<CsLodClientSettings.Setting> found = CsLodClientSettings.find(name);
      if (found.isEmpty()) {
         return unknown(source, reply, name);
      }

      CsLodClientSettings.Setting setting = found.get();
      reply.line(source, "[chunksmith] " + setting.name() + " = " + setting.read() + "  -- " + setting.help());
      return 1;
   }

   private static <S> int write(S source, CsLodClientCommand.Reply<S> reply, String name, String value) {
      Optional<CsLodClientSettings.Setting> found = CsLodClientSettings.find(name);
      if (found.isEmpty()) {
         return unknown(source, reply, name);
      } else {
         CsLodClientSettings.Setting setting = found.get();
         if (!setting.write(value)) {
            List<String> expected = setting.kind().completions();
            reply.line(
               source,
               "[chunksmith] '"
                  + value
                  + "' is not a valid value for "
                  + setting.name()
                  + (expected.isEmpty() ? " (expected a whole number)" : " (expected one of: " + String.join(", ", expected) + ")")
            );
            return 0;
         } else {
            reply.line(source, "[chunksmith] " + setting.name() + " = " + setting.read() + ", applied now and saved to config/chunksmith-lod.properties");
            return 1;
         }
      }
   }

   private static <S> int unknown(S source, CsLodClientCommand.Reply<S> reply, String name) {
      reply.line(source, "[chunksmith] no LOD client setting called '" + name + "'. Known: " + String.join(", ", CsLodClientSettings.names()));
      return 0;
   }

   private static <S> LiteralArgumentBuilder<S> inject(CsLodClientCommand.Reply<S> reply, String name, String renderer) {
      return (LiteralArgumentBuilder<S>)LiteralArgumentBuilder.literal(name).executes(context -> {
         S source = (S)context.getSource();
         if (!CsLodClientNet.reinjectNow()) {
            reply.line(source, "[chunksmith] nothing to replay: no dimension is active. Join a world first, or on a single-player world let the pregen run.");
            return 0;
         } else {
            reply.line(source, "[chunksmith] replaying the LOD store into " + renderer + " -- watch /csclient status for progress");
            reply.line(source, "[chunksmith] injected so far: " + LodInjector.describe());
            return 1;
         }
      });
   }

   @FunctionalInterface
   public interface Reply<S> {
      void line(S var1, String var2);
   }
}
