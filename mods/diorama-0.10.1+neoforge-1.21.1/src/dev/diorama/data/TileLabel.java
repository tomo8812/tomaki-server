package dev.diorama.data;

import dev.diorama.Facing;
import org.jspecify.annotations.Nullable;

public record TileLabel(boolean underRoof, long pitch, boolean fog, Facing facing) {
   private static final long FIRST_PITCH = 60L;
   private static final String SURFACE = "surface";
   private static final String UNDER_ROOF = "under-roof";
   private static final String FOG = "fog";

   public static String onRoof(long pitch, Facing facing, boolean fog) {
      return "on-roof-" + pitch + (facing == Facing.NORTH ? "" : "-" + facing.id()) + (fog ? "-fog" : "");
   }

   public String folder() {
      String turned = this.facing == Facing.NORTH ? "" : "-" + this.facing.id();
      if (this.underRoof) {
         return "under-roof-" + this.pitch + turned + (this.fog ? "-fog" : "");
      } else {
         return this.pitch == 60L && turned.isEmpty() ? "surface" : "surface-" + this.pitch + turned;
      }
   }

   public static @Nullable TileLabel parse(String folder) {
      boolean roofed = folder.startsWith("under-roof-");
      if (!roofed && !folder.startsWith("surface")) {
         return null;
      }

      String rest = folder.substring((roofed ? "under-roof" : "surface").length());
      if (rest.isEmpty()) {
         return roofed ? null : new TileLabel(false, 60L, false, Facing.NORTH);
      }

      if (rest.charAt(0) != '-') {
         return null;
      }

      String[] words = rest.substring(1).split("-");
      boolean fog = roofed && words[words.length - 1].equals("fog");
      int count = fog ? words.length - 1 : words.length;
      if (count >= 1 && count <= 2) {
         Facing facing = count == 2 ? Facing.of(words[1]) : Facing.NORTH;
         if (count == 2 && !facing.id().equals(words[1])) {
            return null;
         }

         try {
            return new TileLabel(roofed, Long.parseLong(words[0]), fog, facing);
         } catch (NumberFormatException e) {
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean canShow(TileLabel other) {
      return this.underRoof == other.underRoof && this.fog == other.fog;
   }
}
