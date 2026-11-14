package dev.diorama.waypoint;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class Waypoint {
   public static final int[] PALETTE = new int[]{
      -1750963, -562411, -15043, -12146856, -15555180, -16740097, -12688419, -7450938, -2735969, -1, -5196614, -10460052, -7509173, -6695638, -8592642, -999208
   };
   public static final String BED_NAME = "Bed";
   private static final DateTimeFormatter DEATH_TIME = DateTimeFormatter.ofPattern("HH:mm");
   private static final Pattern DEATH_NAME = Pattern.compile("Death \\d\\d:\\d\\d");
   private static final DateTimeFormatter DEATH_DATE = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
   public static final int MAX_INITIALS = 4;
   public UUID id = UUID.randomUUID();
   public String name = "";
   public String initials = "";
   public String icon = "";
   public int x;
   public int y;
   public int z;
   public String dimension = "minecraft:overworld";
   public int color = PALETTE[0];
   public boolean visible = true;
   public String folder = "";
   public Waypoint.Kind kind = Waypoint.Kind.MANUAL;
   public long created = System.currentTimeMillis();
   public @Nullable String map;
   public boolean deleted;
   public boolean edited;
   public transient @Nullable Waypoint original;

   public Waypoint() {
   }

   public Waypoint(String name, int x, int y, int z, String dimension, int color) {
      this.name = name;
      this.initials = initialsFor(name);
      this.x = x;
      this.y = y;
      this.z = z;
      this.dimension = dimension;
      this.color = color;
   }

   public Waypoint source() {
      return this.original != null ? this.original : this;
   }

   Waypoint scaled(double divider, @Nullable Waypoint reuse) {
      Waypoint copy = reuse != null ? reuse : new Waypoint();
      copy.id = this.id;
      copy.name = this.name;
      copy.initials = this.initials;
      copy.icon = this.icon;
      copy.x = (int)Math.floor(this.x / divider);
      copy.y = this.y;
      copy.z = (int)Math.floor(this.z / divider);
      copy.dimension = this.dimension;
      int alpha = this.color >>> 24 == 0 ? 255 : this.color >>> 24;
      copy.color = Math.max(1, alpha * 3 / 5) << 24 | this.color & 16777215;
      copy.visible = this.visible;
      copy.folder = this.folder;
      copy.kind = this.kind;
      copy.created = this.created;
      copy.map = this.map;
      copy.deleted = this.deleted;
      copy.edited = this.edited;
      copy.original = this;
      return copy;
   }

   public boolean editable() {
      return this.kind != Waypoint.Kind.WAYSTONE;
   }

   public static String deathName(LocalTime time) {
      return "Death " + time.format(DEATH_TIME);
   }

   public static String deathDate(long millis) {
      String date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(DEATH_DATE);
      return date.replace(' ', ' ').replace(' ', ' ');
   }

   public String shownName() {
      if (this.original != null) {
         return Component.translatable("diorama.waypoint.from_dimension", new Object[]{this.original.shownName(), Waypoints.dimensionName(this.dimension)})
            .getString();
      }

      switch (this.kind) {
         case DEATH:
            if (DEATH_NAME.matcher(this.name).matches()) {
               return Component.translatable("diorama.waypoint.death", new Object[]{deathDate(this.created)}).getString();
            }
         case TEMPORARY:
         case WAYSTONE:
         default:
            break;
         case BED:
            if (this.name.equals("Bed")) {
               return WaypointIconScreen.name("diorama:bed").getString();
            }
            break;
         case NETHER_PORTAL:
         case END_PORTAL:
         case END_GATEWAY:
            if (this.name.equals(Portals.savedName(this.kind))) {
               return Portals.block(this.kind).getName().getString();
            }

            if (this.name.equals("Portal")) {
               return WaypointIconScreen.name("diorama:portal").getString();
            }
      }

      return this.name;
   }

   public String label() {
      return this.initials.isEmpty() ? initialsFor(this.name) : this.initials;
   }

   public double distanceTo(double px, double py, double pz) {
      double dx = this.x + 0.5 - px;
      double dy = this.y - py;
      double dz = this.z + 0.5 - pz;
      return Math.sqrt(dx * dx + dy * dy + dz * dz);
   }

   public static String initialsFor(String name) {
      StringBuilder out = new StringBuilder();

      for (String word : name.trim().split("\\s+")) {
         if (!word.isEmpty() && out.length() < 2) {
            out.appendCodePoint(Character.toUpperCase(word.codePointAt(0)));
         }
      }

      return out.isEmpty() ? "?" : out.toString();
   }

   public enum Kind {
      MANUAL,
      DEATH,
      TEMPORARY,
      WAYSTONE,
      BED,
      NETHER_PORTAL,
      END_PORTAL,
      END_GATEWAY;

      public boolean isPortal() {
         return this == NETHER_PORTAL || this == END_PORTAL || this == END_GATEWAY;
      }

      // $VF: synthetic method
      private static Waypoint.Kind[] $values() {
         return new Waypoint.Kind[]{MANUAL, DEATH, TEMPORARY, WAYSTONE, BED, NETHER_PORTAL, END_PORTAL, END_GATEWAY};
      }
   }
}
