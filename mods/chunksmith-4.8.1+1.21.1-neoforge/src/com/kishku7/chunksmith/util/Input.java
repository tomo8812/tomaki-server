package com.kishku7.chunksmith.util;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.iterator.PatternType;
import com.kishku7.chunksmith.platform.World;
import com.kishku7.chunksmith.shape.ShapeType;
import java.util.Optional;

public final class Input {
   private static final int MAX_HOST_LENGTH = 253;
   private static final int MAX_LABEL_LENGTH = 63;

   private Input() {
   }

   public static Optional<World> tryWorld(Chunksmith chunky, String input) {
      return input != null && !input.isEmpty() ? chunky.getServer().getWorld(input) : Optional.empty();
   }

   public static Optional<String> tryPattern(String input) {
      if (input != null && !input.isEmpty()) {
         String inputLower = input.toLowerCase();
         return PatternType.ALL.contains(inputLower) ? Optional.of(inputLower) : Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   public static Optional<String> tryShape(String input) {
      if (input != null && !input.isEmpty()) {
         String inputLower = input.toLowerCase();
         return ShapeType.all().contains(inputLower) ? Optional.of(inputLower) : Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   public static Optional<Boolean> tryBoolean(String input) {
      if (input != null && !input.isEmpty()) {
         String value = input.trim();
         if (value.equalsIgnoreCase("true")) {
            return Optional.of(Boolean.TRUE);
         } else {
            return value.equalsIgnoreCase("false") ? Optional.of(Boolean.FALSE) : Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public static Optional<Integer> tryInteger(String input) {
      if (input != null && !input.isEmpty()) {
         try {
            return Optional.of(Integer.parseInt(input));
         } catch (NumberFormatException e) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public static Optional<Integer> tryIntegerSuffixed(String input) {
      if (input != null && !input.isEmpty()) {
         int last = input.length() - 1;
         return suffixValue(input.charAt(last)).map(suffixValue -> tryInteger(input.substring(0, last)).map(i -> i * suffixValue)).orElse(tryInteger(input));
      } else {
         return Optional.empty();
      }
   }

   public static Optional<Double> tryDouble(String input) {
      if (input != null && !input.isEmpty()) {
         try {
            return Optional.of(Double.parseDouble(input));
         } catch (NumberFormatException e) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public static Optional<Double> tryDoubleSuffixed(String input) {
      if (input != null && !input.isEmpty()) {
         int last = input.length() - 1;
         return suffixValue(input.charAt(last))
            .map(suffixValue -> tryDouble(input.substring(0, last)).map(d -> d * suffixValue.intValue()))
            .orElse(tryDouble(input));
      } else {
         return Optional.empty();
      }
   }

   public static Optional<Long> tryLong(String input) {
      if (input != null && !input.isEmpty()) {
         try {
            return Optional.of(Long.parseLong(input));
         } catch (NumberFormatException e) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public static Optional<Integer> trySign(String input) {
      if (input != null && !input.isEmpty()) {
         char sign = input.charAt(0);

         return switch (sign) {
            case '+' -> Optional.of(1);
            case '-' -> Optional.of(-1);
            default -> Optional.empty();
         };
      } else {
         return Optional.empty();
      }
   }

   public static boolean isPastWorldLimit(double value) {
      return Math.abs(value) > 3.0E7;
   }

   public static String checkLanguage(String language) {
      return Translator.isValidLanguage(language) ? language : "en";
   }

   public static String checkHost(String host) {
      if (host == null) {
         return "";
      }

      String trimmed = host.trim();
      if (!trimmed.isEmpty() && trimmed.length() <= 253) {
         String bare = trimmed.startsWith("[") && trimmed.endsWith("]") ? trimmed.substring(1, trimmed.length() - 1) : trimmed;
         if (bare.indexOf(58) >= 0) {
            return bare.matches("[0-9A-Fa-f:.%]+") ? bare : "";
         }

         String labelsPart = bare.endsWith(".") ? bare.substring(0, bare.length() - 1) : bare;
         if (labelsPart.isEmpty()) {
            return "";
         }

         String[] labels = labelsPart.split("\\.", -1);
         String[] var5 = labels;
         int var6 = var5.length;
         int var7 = 0;

         while (var7 < var6) {
            String label = var5[var7];
            if (!label.isEmpty() && label.length() <= 63) {
               if (!label.startsWith("-") && !label.endsWith("-")) {
                  if (!label.matches("[0-9A-Za-z_-]+")) {
                     return "";
                  }

                  var7++;
                  continue;
               }

               return "";
            }

            return "";
         }

         if (isAllDigits(labels[labels.length - 1])) {
            return isDottedQuad(labels) ? bare : "";
         } else {
            return bare;
         }
      } else {
         return "";
      }
   }

   public static String checkAdvertisedHost(String host) {
      String checked = checkHost(host);
      if (checked.isEmpty()) {
         return "";
      } else {
         return isWildcardAddress(checked) ? "" : checked;
      }
   }

   public static boolean isWildcardAddress(String host) {
      if (host == null) {
         return false;
      }

      String bare = host.trim();
      if (bare.startsWith("[") && bare.endsWith("]")) {
         bare = bare.substring(1, bare.length() - 1);
      }

      return !"0.0.0.0".equals(bare) && !"::".equals(bare)
         ? bare.indexOf(58) >= 0 && bare.replace(":", "").chars().allMatch(c -> c == 48) && !bare.replace(":", "").isEmpty()
         : true;
   }

   private static boolean isDottedQuad(String[] labels) {
      if (labels.length != 4) {
         return false;
      }

      for (String label : labels) {
         if (!isAllDigits(label) || label.length() > 3) {
            return false;
         }

         if (label.length() > 1 && label.charAt(0) == '0') {
            return false;
         }

         if (Integer.parseInt(label) > 255) {
            return false;
         }
      }

      return true;
   }

   private static boolean isAllDigits(String value) {
      if (value.isEmpty()) {
         return false;
      }

      for (int i = 0; i < value.length(); i++) {
         if (!Character.isDigit(value.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   private static Optional<Integer> suffixValue(char suffix) {
      return switch (Character.toLowerCase(suffix)) {
         case 'c' -> Optional.of(16);
         case 'k' -> Optional.of(1000);
         case 'r' -> Optional.of(512);
         default -> Optional.empty();
      };
   }
}
