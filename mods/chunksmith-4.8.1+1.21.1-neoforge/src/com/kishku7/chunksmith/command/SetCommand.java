package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.platform.Config;
import com.kishku7.chunksmith.platform.Sender;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SetCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public SetCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      Config config = this.chunky.getConfig();
      Optional<String> key = arguments.next();
      if (key.isEmpty()) {
         this.listAll(sender, config);
      } else {
         Optional<ConfigSetting> found = ConfigSettings.find(key.get());
         if (found.isEmpty()) {
            sender.sendMessagePrefixed("error_set_unknown", key.get());
            sender.sendMessage("format_set_keys", String.join(", ", ConfigSettings.names()));
         } else {
            ConfigSetting setting = found.get();
            Optional<String> value = arguments.next();
            if (value.isEmpty()) {
               this.sendOne(sender, config, setting);
            } else if (!setting.isSupported(config)) {
               sender.sendMessagePrefixed("error_set_unsupported", setting.name());
            } else if (!setting.write(config, value.get())) {
               String why = setting.explainRefusal(config, value.get());
               if (why != null) {
                  sender.sendMessagePrefixed("error_set_refused", setting.name(), why);
               } else {
                  sender.sendMessagePrefixed("error_set_value", value.get(), setting.name(), this.expected(setting));
               }
            } else {
               sender.sendMessagePrefixed("format_set", setting.name(), setting.read(config));
            }
         }
      }
   }

   private void listAll(Sender sender, Config config) {
      StringBuilder list = new StringBuilder();

      for (ConfigSetting setting : ConfigSettings.all()) {
         list.append('\n').append(setting.name()).append(": ").append(setting.read(config));
         if (!setting.isSupported(config)) {
            list.append(" (not used on this platform)");
         }
      }

      sender.sendMessage("format_set_list", list.toString());
   }

   private void sendOne(Sender sender, Config config, ConfigSetting setting) {
      if (!setting.isSupported(config)) {
         sender.sendMessagePrefixed("error_set_unsupported", setting.name());
      } else {
         sender.sendMessagePrefixed("format_set_show", setting.name(), setting.read(config));
      }
   }

   private String expected(ConfigSetting setting) {
      List<String> completions = setting.kind().completions();
      return !completions.isEmpty() ? String.join("/", completions) : setting.kind().name().toLowerCase(Locale.ROOT);
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      if (arguments.size() < 2) {
         return new ArrayList<>(ConfigSettings.names());
      } else {
         return arguments.size() == 2
            ? ConfigSettings.find(arguments.remaining().get(0)).map(setting -> setting.kind().completions()).orElse(List.of())
            : List.of();
      }
   }
}
