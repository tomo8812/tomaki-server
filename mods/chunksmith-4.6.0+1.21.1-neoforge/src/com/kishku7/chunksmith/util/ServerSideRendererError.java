package com.kishku7.chunksmith.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public final class ServerSideRendererError {
   private static final String RULE = "*".repeat(78);

   private ServerSideRendererError() {
   }

   public static List<String> rendererIds() {
      return RendererNames.ids();
   }

   public static List<String> lines(boolean dedicated, Predicate<String> modPresent) {
      if (!dedicated) {
         return List.of();
      }

      List<String> found = new ArrayList<>();

      for (String id : RendererNames.ids()) {
         if (modPresent.test(id)) {
            found.add(RendererNames.display(id));
         }
      }

      if (found.isEmpty()) {
         return List.of();
      }

      boolean dh = modPresent.test("distanthorizons");
      String names = String.join(" and ", found);
      String verb = found.size() == 1 ? "is" : "are";
      String pronoun = found.size() == 1 ? "it" : "them";
      List<String> out = new ArrayList<>();
      out.add(RULE);
      out.add(names + " " + verb + " installed on this DEDICATED SERVER. REMOVE " + pronoun.toUpperCase(Locale.ROOT) + ".");
      out.add("An LOD renderer is a CLIENT mod. This server does not render anything.");
      out.add("Chunksmith builds its own LOD data while it pregenerates and serves that to each");
      out.add("player's client, which injects it into the renderer THEY have installed.");
      out.add("Running one here costs threads, memory and disk generating a second copy of the");
      out.add("terrain this server is already generating.");
      if (dh) {
         out.add("It is also what fills the console with Distant Horizons' own \"No DH level");
         out.add("provided\" warnings whenever /cslod dhpush is run at this console.");
         out.add("Removing it is the recommended setup. Keep Distant Horizons here only if you");
         out.add("deliberately serve players who do not have Chunksmith installed.");
      } else {
         out.add(
            names
               + " "
               + (found.size() == 1 ? "has" : "have")
               + " no server half -- "
               + (found.size() == 1 ? "it renders" : "they render")
               + ", and this machine does"
         );
         out.add("not. There is no setup in which " + pronoun + " belongs here.");
      }

      out.add("Chunksmith itself is running normally; this is a server misconfiguration.");
      out.add(RULE);
      return out;
   }
}
