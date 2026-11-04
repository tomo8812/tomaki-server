package dev.terminalmc.clientsort.util;

import dev.terminalmc.clientsort.platform.services.PlatformServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.AbstractMessageFactory;
import org.apache.logging.log4j.message.FormattedMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;

public class Logging {
   private Logging() {
      throw new UnsupportedOperationException("This class cannot be instantiated.");
   }

   public static Logger getLogger(String name) {
      return !PlatformServices.getInstance().isDevEnv() && !PlatformServices.getInstance().hasNamedLogger()
         ? LogManager.getLogger(name, new Logging.PrefixingMessageFactory("[" + name + "/]: "))
         : LogManager.getLogger(name);
   }

   private static final class PrefixingMessageFactory extends AbstractMessageFactory {
      private final String prefix;

      public PrefixingMessageFactory(String prefix) {
         this.prefix = prefix;
      }

      public Message newMessage(String message) {
         return new SimpleMessage(this.prefix + message);
      }

      public Message newMessage(String message, Object... params) {
         return new FormattedMessage(this.prefix + message, params);
      }
   }
}
