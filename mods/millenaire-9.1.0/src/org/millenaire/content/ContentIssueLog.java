package org.millenaire.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public final class ContentIssueLog {
   private static final String APPENDER_NAME = "MillenaireContentIssueAppender";
   private static final List<String> CAPTURED_PACKAGES = List.of(
      "org.millenaire.content",
      "org.millenaire.culture",
      "org.millenaire.building",
      "org.millenaire.goal.gathering",
      "org.millenaire.goal.visit",
      "org.millenaire.quest",
      "org.millenaire.tool",
      "org.millenaire.config.VillagerConfig",
      "org.millenaire.language",
      "org.millenaire.commerce",
      "org.millenaire.dialogue",
      "org.millenaire.goal.GoalRegistry",
      "org.millenaire.Millenaire"
   );
   private static final int MAX_ENTRIES = 400;
   private static final Map<String, ContentIssueLog.Entry> ENTRIES = new LinkedHashMap<>();
   private static final Map<String, ContentIssueLog.Entry> INIT_ENTRIES = new LinkedHashMap<>();
   private static int suppressed;
   private static ContentIssueLog.CapturingAppender appender;
   private static boolean captureInstalled;
   private static boolean captureCompleted;

   private ContentIssueLog() {
   }

   public static synchronized void startCapture() {
      stopCapture();
      ENTRIES.clear();
      suppressed = 0;
      captureInstalled = false;
      captureCompleted = false;

      try {
         LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
         Configuration config = ctx.getConfiguration();
         appender = new ContentIssueLog.CapturingAppender();
         appender.start();
         LoggerConfig root = config.getRootLogger();
         root.addAppender(appender, Level.WARN, null);
         ctx.updateLoggers();
         captureInstalled = true;
      } catch (Exception | LinkageError e) {
         appender = null;
      }
   }

   public static synchronized void stopCapture() {
      if (appender != null) {
         detach();
         captureCompleted = true;
      }
   }

   public static synchronized void abortCapture() {
      if (appender != null) {
         detach();
      }
   }

   private static void detach() {
      try {
         LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
         Configuration config = ctx.getConfiguration();
         config.getRootLogger().removeAppender("MillenaireContentIssueAppender");
         ctx.updateLoggers();
         appender.stop();
      } catch (Exception | LinkageError var5) {
      } finally {
         appender = null;
      }
   }

   public static synchronized void archiveInitWindow() {
      INIT_ENTRIES.putAll(ENTRIES);
      ENTRIES.clear();
   }

   public static synchronized boolean captureWasInstalled() {
      return captureInstalled;
   }

   public static synchronized boolean captureWasCompleted() {
      return captureCompleted;
   }

   public static synchronized List<ContentIssueLog.Entry> snapshot() {
      if (INIT_ENTRIES.isEmpty()) {
         return List.copyOf(ENTRIES.values());
      }

      List<ContentIssueLog.Entry> out = new ArrayList<>(INIT_ENTRIES.size() + ENTRIES.size());
      out.addAll(INIT_ENTRIES.values());
      out.addAll(ENTRIES.values());
      return List.copyOf(out);
   }

   public static synchronized int suppressedCount() {
      return suppressed;
   }

   public static synchronized boolean isEmpty() {
      return ENTRIES.isEmpty() && INIT_ENTRIES.isEmpty();
   }

   public static synchronized void clearForTesting() {
      ENTRIES.clear();
      INIT_ENTRIES.clear();
      suppressed = 0;
      captureInstalled = false;
      captureCompleted = false;
   }

   public static synchronized void recordForTesting(Level level, String loggerName, String message) {
      record(level, loggerName, message);
   }

   public static synchronized void forgetForTesting(String message) {
      ENTRIES.values().removeIf(e -> e.message().equals(message));
      INIT_ENTRIES.values().removeIf(e -> e.message().equals(message));
   }

   static synchronized void record(Level level, String loggerName, String message) {
      if (level != null && loggerName != null && message != null) {
         if (isCaptured(loggerName)) {
            String key = level.name() + "\u0000" + loggerName + "\u0000" + message;
            ContentIssueLog.Entry existing = ENTRIES.get(key);
            if (existing != null) {
               ENTRIES.put(key, new ContentIssueLog.Entry(level, loggerName, message, existing.count() + 1));
            } else if (ENTRIES.size() >= 400) {
               suppressed++;
            } else {
               ENTRIES.put(key, new ContentIssueLog.Entry(level, loggerName, message, 1));
            }
         }
      }
   }

   private static boolean isCaptured(String loggerName) {
      for (String prefix : CAPTURED_PACKAGES) {
         if (loggerName.startsWith(prefix)) {
            return true;
         }
      }

      return false;
   }

   static List<String> renderLines() {
      List<ContentIssueLog.Entry> entries = snapshot();
      if (entries.isEmpty()) {
         return List.of();
      }

      List<String> out = new ArrayList<>();
      long errors = entries.stream().filter(ex -> ex.level() == Level.ERROR).count();
      out.add(String.format("  %d error(s), %d warning(s) during content loading.", errors, entries.size() - errors));
      out.add("");

      for (ContentIssueLog.Entry e : entries) {
         String repeat = e.count() > 1 ? " (x" + e.count() + ")" : "";
         out.add(String.format("  [%s] %s%s", e.level(), e.shortLogger(), repeat));
         out.add("    " + e.message());
      }

      int dropped = suppressedCount();
      if (dropped > 0) {
         out.add("");
         out.add(String.format("  ... and %d further distinct message(s), not listed: see logs/latest.log.", dropped));
      }

      return out;
   }

   private static final class CapturingAppender extends AbstractAppender {
      private CapturingAppender() {
         super("MillenaireContentIssueAppender", null, null, true, null);
      }

      public void append(LogEvent event) {
         try {
            Level level = event.getLevel();
            if (level == null || !level.isMoreSpecificThan(Level.WARN)) {
               return;
            }

            ContentIssueLog.record(level, event.getLoggerName(), event.getMessage() == null ? "" : event.getMessage().getFormattedMessage());
         } catch (Exception var3) {
         }
      }
   }

   public record Entry(Level level, String logger, String message, int count) {
      public String shortLogger() {
         int dot = this.logger.lastIndexOf(46);
         return dot < 0 ? this.logger : this.logger.substring(dot + 1);
      }
   }
}
