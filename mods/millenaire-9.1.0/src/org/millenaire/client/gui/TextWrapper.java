package org.millenaire.client.gui;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;

public final class TextWrapper {
   public static final String LINE_BREAK = "<ret>";

   private TextWrapper() {
   }

   public static List<String> wrap(String text, int maxWidth, ToIntFunction<String> width) {
      List<String> rows = new ArrayList<>();
      if (!text.isEmpty() && width.applyAsInt(text) > maxWidth) {
         String indent = continuationIndent(text, maxWidth, width);
         StringBuilder row = new StringBuilder();
         boolean rowHasText = false;

         for (String word : text.split(" ")) {
            String candidate = rowHasText ? row + " " + word : row + word;
            if (width.applyAsInt(candidate) <= maxWidth) {
               row = new StringBuilder(candidate);
               rowHasText = true;
            } else {
               if (rowHasText) {
                  String fresh = indent + FormattingCodes.active(row.toString()) + word;
                  if (width.applyAsInt(fresh) <= maxWidth) {
                     rows.add(row.toString());
                     row = new StringBuilder(fresh);
                     continue;
                  }

                  row.append(' ');
               }

               BreakIterator graphemes = BreakIterator.getCharacterInstance();
               graphemes.setText(word);
               int i = 0;

               while (i < word.length()) {
                  int end = pieceEnd(word, i, graphemes);
                  String piece = word.substring(i, end);
                  if (rowHasText && width.applyAsInt(row + piece) > maxWidth) {
                     String done = stripTrailingSpace(row.toString());
                     rows.add(done);
                     row = new StringBuilder(indent + FormattingCodes.active(done));
                     rowHasText = false;
                  }

                  row.append(piece);
                  if (!isCode(piece)) {
                     rowHasText = true;
                  }

                  i = end;
               }
            }
         }

         if (rowHasText || rows.isEmpty()) {
            rows.add(row.toString());
         }

         return rows;
      } else {
         rows.add(text);
         return rows;
      }
   }

   public static List<String> wrapParagraphs(String text, int maxWidth, ToIntFunction<String> width) {
      if (!text.contains("<ret>")) {
         return wrap(text, maxWidth, width);
      }

      List<String> rows = new ArrayList<>();
      String carried = "";
      String[] paragraphs = text.split("<ret>", -1);

      for (int p = 0; p < paragraphs.length; p++) {
         String paragraph = carried + (p == 0 ? paragraphs[p].stripTrailing() : paragraphs[p].strip());
         rows.addAll(wrap(paragraph, maxWidth, width));
         carried = FormattingCodes.active(paragraph);
      }

      return rows;
   }

   public static List<String> wrapTitle(String title, @Nullable String villageName, int maxWidth, ToIntFunction<String> width) {
      int at = villageName != null && !villageName.isEmpty() ? title.indexOf(villageName) : -1;
      if (at > 0 && width.applyAsInt(title) > maxWidth) {
         List<String> rows = wrap(title.substring(0, at).stripTrailing(), maxWidth, width);
         rows.addAll(wrap(title.substring(at), maxWidth, width));
         return rows;
      } else {
         return wrap(title, maxWidth, width);
      }
   }

   private static String continuationIndent(String text, int maxWidth, ToIntFunction<String> width) {
      int i = 0;
      int spaces = 0;

      while (i < text.length()) {
         if (text.charAt(i) == 167 && i + 1 < text.length()) {
            i += 2;
         } else {
            if (text.charAt(i) != ' ') {
               break;
            }

            spaces++;
            i++;
         }
      }

      String indent = " ".repeat(spaces);
      if (i + 1 < text.length() && "-*•".indexOf(text.charAt(i)) >= 0 && text.charAt(i + 1) == ' ') {
         int spaceWidth = Math.max(1, width.applyAsInt(" "));
         int bulletWidth = width.applyAsInt(text.substring(i, i + 2));
         indent = indent + " ".repeat(Math.round((float)bulletWidth / spaceWidth));
      }

      return width.applyAsInt(indent) * 2 > maxWidth ? "" : indent;
   }

   private static int pieceEnd(String word, int i, BreakIterator graphemes) {
      if (word.charAt(i) == 167 && i + 1 < word.length()) {
         return i + 2;
      }

      int next = graphemes.following(i);
      return next == -1 ? word.length() : next;
   }

   private static boolean isCode(String piece) {
      return piece.length() == 2 && piece.charAt(0) == 167;
   }

   private static String stripTrailingSpace(String s) {
      return s.endsWith(" ") ? s.substring(0, s.length() - 1) : s;
   }
}
