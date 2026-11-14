package org.millenaire.client.gui;

public final class FormattingCodes {
   private FormattingCodes() {
   }

   public static String active(String text) {
      String colour = "";
      StringBuilder styles = new StringBuilder();

      for (int i = 0; i + 1 < text.length(); i++) {
         if (text.charAt(i) == 167) {
            char code = Character.toLowerCase(text.charAt(++i));
            if ((code < '0' || code > '9') && (code < 'a' || code > 'f')) {
               if (code >= 'k' && code <= 'o') {
                  if (styles.indexOf("§" + code) < 0) {
                     styles.append('§').append(code);
                  }
               } else if (code == 'r') {
                  colour = "";
                  styles.setLength(0);
               }
            } else {
               colour = "§" + code;
               styles.setLength(0);
            }
         }
      }

      return colour + styles;
   }
}
