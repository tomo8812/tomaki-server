package dev.terminalmc.clientsort.config;

import dev.terminalmc.clientsort.util.Localization;
import java.text.ParseException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

public class ServerClassPolicy {
   public static final String DATA_FORMAT = "%s,%d,%d,%d";
   public static final String DATA_PATTERN_STRING = "^(.+),([01]),([01]),([01])$";
   public static final Pattern DATA_PATTERN = Pattern.compile("^(.+),([01]),([01]),([01])$");
   public final String className;
   public boolean sortEnabled;
   public boolean stackFillEnabled;
   public boolean transferEnabled;
   @Nullable
   public String lastAutoEditTime;
   @Nullable
   public String lastAutoEditReason;

   public ServerClassPolicy(String className, boolean sortEnabled, boolean stackFillEnabled, boolean transferEnabled) {
      this(className, sortEnabled, stackFillEnabled, transferEnabled, null, null);
   }

   public ServerClassPolicy(
      String className,
      boolean sortEnabled,
      boolean stackFillEnabled,
      boolean transferEnabled,
      @Nullable String lastAutoEditTime,
      @Nullable String lastEditReason
   ) {
      this.className = className;
      this.sortEnabled = sortEnabled;
      this.stackFillEnabled = stackFillEnabled;
      this.transferEnabled = transferEnabled;
      this.lastAutoEditTime = lastAutoEditTime;
      this.lastAutoEditReason = lastEditReason;
   }

   public void setFrom(ServerClassPolicy classPolicy) {
      this.sortEnabled = this.sortEnabled && classPolicy.sortEnabled;
      this.stackFillEnabled = this.stackFillEnabled && classPolicy.stackFillEnabled;
      this.transferEnabled = this.transferEnabled && classPolicy.transferEnabled;
   }

   public String toDataString() {
      return String.format("%s,%d,%d,%d", this.className, this.sortEnabled ? 1 : 0, this.stackFillEnabled ? 1 : 0, this.transferEnabled ? 1 : 0);
   }

   public static ServerClassPolicy fromDataString(String dataString, Set<String> originalClassNames) throws ParseException {
      dataString = dataString.strip();
      Matcher matcher = DATA_PATTERN.matcher(dataString);
      if (!matcher.matches()) {
         throw new ParseException(Localization.localized("error", "classPolicy.pattern", "^(.+),([01]),([01]),([01])$").getString(), 0);
      }

      String className = matcher.group(1);
      if (!originalClassNames.contains(className)) {
         try {
            Class.forName(className);
         } catch (ClassNotFoundException e) {
            throw new ParseException(Localization.localized("error", "classPolicy.classNotFound", className).getString(), 0);
         }
      }

      return new ServerClassPolicy(className, matcher.group(2).equals("1"), matcher.group(3).equals("1"), matcher.group(4).equals("1"));
   }
}
